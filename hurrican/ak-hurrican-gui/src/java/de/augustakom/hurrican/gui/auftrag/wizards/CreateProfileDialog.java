/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.2016
 */
package de.augustakom.hurrican.gui.auftrag.wizards;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.auftrag.wizards.tablemodels.ProfileAuftragValueTableModel;
import de.augustakom.hurrican.model.cc.DSLAMProfileChangeReason;
import de.augustakom.hurrican.model.cc.ProfileAuftrag;
import de.augustakom.hurrican.model.cc.ProfileParameter;
import de.augustakom.hurrican.model.cc.ProfileParameterMapper;

class CreateProfileDialog extends JDialog {

    private static final int PREFERRED_WIDTH = 830;

    private ProfilePanel profilePanel;
    private JComboBox<String> changeReasons;
    private JTextField noteText;
    private AKJDateComponent vonDate;
    private Map<String, DSLAMProfileChangeReason> profileChangeReason;
    private ProfileAuftragValueTableModel auftragValueTableModel;

    CreateProfileDialog(JFrame parent, ProfilePanel profilePanel) {
        super(parent, "Profil anpassen", true);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        this.profilePanel = profilePanel;
        setDialogDimensions();
        add(createProfileChangePanel(), BorderLayout.NORTH);
        add(createButtonPanel(), BorderLayout.CENTER);
        this.pack();
    }

    private void setDialogDimensions() {
        setMinimumSize(new Dimension(PREFERRED_WIDTH, 190));
        setMaximumSize(new Dimension(PREFERRED_WIDTH, 190));
    }

    private JPanel createProfileChangePanel() {
        fillReasons();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        panel.add(createProfileTable(), GBCFactory.createGBC(1, 1, 0, 0, 2, 2, 1));
        panel.add(createChangeReasonPanel(), GBCFactory.createGBC(1, 1, 0, 2, 2, 1, 1));
        panel.setPreferredSize(new Dimension(PREFERRED_WIDTH, 120));
        return panel;
    }

    private void fillReasons() {
        this.profileChangeReason = profilePanel.findAllChangeReasonsNameId();
        changeReasons = new JComboBox<>();
        profileChangeReason.keySet().forEach(changeReasons::addItem);
    }

    private JPanel createChangeReasonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JLabel reason = new JLabel("Änderungsgrund:");
        JLabel note = new JLabel("Bemerkung:");
        JLabel von = new JLabel("gültig von");
        noteText = new JTextField();
        noteText.setColumns(30);
        vonDate = new AKJDateComponent(new Date());
        panel.add(reason);
        panel.add(changeReasons);
        panel.add(note);
        panel.add(noteText);
        panel.add(von);
        panel.add(vonDate);
        panel.setPreferredSize(new Dimension(PREFERRED_WIDTH, 30));
        return panel;
    }

    private JScrollPane createProfileTable() {
        List<ProfileParameterMapper> mappers = fillTableModel();
        JTable table = new JTable(auftragValueTableModel);
        table.setPreferredSize(new Dimension(PREFERRED_WIDTH, 45));
        table.setFillsViewportHeight(true);
        Map<String, List<ProfileParameter>> profileDefaults = profilePanel.getProfileParameters();
        for (int i = 0; i < table.getColumnCount(); i++)
            setUpColumnRenderer(table.getColumnModel().getColumn(i), profileDefaults, mappers);
        JScrollPane pane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setPreferredSize(new Dimension(PREFERRED_WIDTH, 60));
        return pane;
    }

    private List<ProfileParameterMapper> fillTableModel() {
        try {
            ProfileAuftrag profileAuftrag = profilePanel.getProfileAuftrag();
            //no change reason means profile was created from defaults so only reason for change should be INIT
            if (profileAuftrag.getChangeReason() == null)
                lockComboBoxToInit();
            List<ProfileParameterMapper> mappers = profilePanel.findParameterMappers(profileAuftrag);
            auftragValueTableModel = new ProfileAuftragValueTableModel(
                    profileAuftrag.getProfileAuftragValues(), mappers, true);
            return mappers;
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
        }
        return Collections.emptyList();
    }

    private void lockComboBoxToInit() {
        getInitReason().ifPresent(profile -> changeReasons.setSelectedItem(profile.getName()));
        changeReasons.setEnabled(false);
    }

    private Optional<DSLAMProfileChangeReason> getInitReason() {
        return profileChangeReason.entrySet().stream().
                map(Map.Entry::getValue).
                filter(profile -> profile.getId().equals(DSLAMProfileChangeReason.CHANGE_REASON_ID_INIT)).
                findFirst();
    }

    private void setUpColumnRenderer(TableColumn column, Map<String, List<ProfileParameter>> profileDefaults,
            List<ProfileParameterMapper> mappers) {
        String key = transformKey(column.getHeaderValue().toString(), mappers);
        List<ProfileParameter> defaults = profileDefaults.getOrDefault(key, Collections.emptyList());
        column.setCellEditor(createCellEditor(defaults));
    }

    private String transformKey(String key, List<ProfileParameterMapper> mappers) {
        return mappers.stream().filter(mapper -> key.equals(mapper.getParameterGuiName()))
                .map(ProfileParameterMapper::getParameterName).findFirst().orElse(key);
    }

    private DefaultCellEditor createCellEditor(List<ProfileParameter> values) {
        JComboBox<String> component = createComboBox(values);
        return new DefaultCellEditor(component);
    }

    private JComboBox<String> createComboBox(List<ProfileParameter> defaults) {
        final JComboBox<String> combo = new JComboBox<>();
        defaults.forEach(def -> combo.addItem(def.getParameterValue()));
        return combo;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("Speichern");
        save.addActionListener(event -> save());
        JButton cancel = new JButton("Abbrechen");
        cancel.addActionListener(event -> this.dispose());
        panel.add(save);
        panel.add(cancel);
        panel.setPreferredSize(new Dimension(PREFERRED_WIDTH, 20));
        return panel;
    }

    private void save() {
        String itemKey = (String) changeReasons.getSelectedItem();
        String note = noteText.getText();
        Date date = vonDate.getDate(new Date());
        if (itemKey != null) {
            profilePanel.createNewProfile(auftragValueTableModel.getValues(), profileChangeReason.get(itemKey),
                    note, date);
            this.dispose();
        }
    }
}