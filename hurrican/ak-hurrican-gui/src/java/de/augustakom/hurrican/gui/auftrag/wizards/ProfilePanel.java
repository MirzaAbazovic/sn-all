/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.10.2016
 */
package de.augustakom.hurrican.gui.auftrag.wizards;

import java.awt.*;
import java.net.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.wizards.tablemodels.ProfileAuftragTableModel;
import de.augustakom.hurrican.gui.auftrag.wizards.tablemodels.ProfileAuftragValueTableModel;
import de.augustakom.hurrican.model.cc.DSLAMProfileChangeReason;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.ProfileAuftrag;
import de.augustakom.hurrican.model.cc.ProfileAuftragValue;
import de.augustakom.hurrican.model.cc.ProfileParameter;
import de.augustakom.hurrican.model.cc.ProfileParameterMapper;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.service.cc.ProfileService;

public class ProfilePanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(ProfilePanel.class);
    private long auftragId;
    private Equipment equipment;
    private HWBaugruppe baugruppe;
    private ProfileService profileService;
    private final URL imageIconURL = ClassLoader.getSystemResource("de/augustakom/hurrican/gui/images/new.gif");

    public ProfilePanel(ProfileService profileService, long auftragId, Equipment equipment, HWBaugruppe baugruppe) {
        super(new GridBagLayout());
        setPreferredSize(new Dimension(1060, 300));
        this.auftragId = auftragId;
        this.equipment = equipment;
        this.baugruppe = baugruppe;
        this.profileService = profileService;
        init();
    }

    private void init() {
        try {
            removeAll();
            List<ProfileAuftrag> profiles = profileService.findProfileAuftrags(auftragId);
            JPanel buttonPanel = generateButtonPanel();
            JTable detailTable = generateDetailTable(profiles);
            JTable overviewTable = generateOverviewTable(profiles, detailTable);
            add(buttonPanel, GBCFactory.createGBC(1, 1, 0, 0, 1, 3, 1));
            add(new JScrollPane(overviewTable), GBCFactory.createGBC(1, 1, 1, 0, 1, 1, 1));
            add(new JLabel("Profilwert"), GBCFactory.createGBC(1, 1, 1, 1, 1, 1, 1));
            add(new JScrollPane(detailTable), GBCFactory.createGBC(1, 1, 1, 2, 1, 1, 1));
            validate();
            repaint();
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
            LOGGER.error(e.getMessage(), e);
        }
    }

    private JTable generateDetailTable(List<ProfileAuftrag> profiles) {
        List<ProfileParameterMapper> mappers = Collections.emptyList();
        if (!CollectionUtils.isEmpty(profiles))
            mappers = findParameterMappers(profiles.get(0));
        ProfileAuftragValueTableModel detailTableModel = new ProfileAuftragValueTableModel(Collections.emptySet(),
                mappers, false);
        JTable detailTable = new JTable(detailTableModel);
        detailTable.setPreferredScrollableViewportSize(new Dimension(1060, 25));
        detailTable.setFillsViewportHeight(true);
        return detailTable;
    }

    final List<ProfileParameterMapper> findParameterMappers(ProfileAuftrag auftrag) {
        return profileService.findParameterMappers(auftrag.getProfileAuftragValues());
    }

    private JTable generateOverviewTable(List<ProfileAuftrag> profiles, JTable detailTable) {
        ProfileAuftragTableModel tableModel = new ProfileAuftragTableModel(profiles);
        JTable overviewTable = new JTable(tableModel);
        overviewTable.setPreferredScrollableViewportSize(new Dimension(1060, 250));
        overviewTable.setFillsViewportHeight(true);
        overviewTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        overviewTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                ProfileAuftrag auftrag = tableModel.getProfileAuftrag(overviewTable.getSelectedRow());
                ((ProfileAuftragValueTableModel) detailTable.getModel())
                        .setValues(auftrag.getProfileAuftragValues());
            }
        });
        return overviewTable;
    }

    private JPanel generateButtonPanel() {
        JButton newProfile = new JButton(new ImageIcon(imageIconURL));
        newProfile.setPreferredSize(new Dimension(20, 20));
        newProfile.setToolTipText("Auswahl und Zuordnung eines Profils");
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(20, 300));
        buttonPanel.add(newProfile);
        newProfile.addActionListener(event -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            new CreateProfileDialog(parent, this).setVisible(true);
        });

        return buttonPanel;
    }

    final ProfileAuftrag getProfileAuftrag() {
        ProfileAuftrag profileAuftrag = profileService.findNewestProfileAuftrag(auftragId);
        if (profileAuftrag != null)
            return mergeWithDefaults(profileAuftrag);
        return profileService.createNewProfile(auftragId, HurricanSystemRegistry.instance().getSessionId());
    }

    private ProfileAuftrag mergeWithDefaults(ProfileAuftrag profileAuftrag) {
        final Map<String, List<ProfileParameter>> profileParameters = getProfileParameters();
        final Set<ProfileAuftragValue> profileAuftragValues = profileAuftrag.getProfileAuftragValues();
        final Set<String> keySet = profileParameters.keySet();
        for (String key : keySet) {
            if (profileAuftragValues.stream().noneMatch(value -> value.getParameterName().equals(key))) {
                Optional<String> profileParamValue = profileParameters.get(key).stream()
                        .filter(ProfileParameter::getDefault)
                        .map(ProfileParameter::getParameterValue).findFirst();
                ProfileAuftragValue newValue = new ProfileAuftragValue(key, profileParamValue.orElse(""));
                profileAuftrag.getProfileAuftragValues().add(newValue);
            }
        }
        return profileAuftrag;
    }

    final Map<String, List<ProfileParameter>> getProfileParameters() {
        return profileService.
                findProfileParametersGroupByName(equipment.getHvtIdStandort(), baugruppe);
    }

    final void createNewProfile(List<ProfileAuftragValue> auftragValues, DSLAMProfileChangeReason changeReason,
            String note, Date validFrom) {
        ProfileAuftrag auftrag = profileService.
                createNewProfile(auftragId, HurricanSystemRegistry.instance().getSessionId());
        auftrag.getProfileAuftragValues().clear();
        auftrag.getProfileAuftragValues().addAll(auftragValues);

        // Falls nicht alle Werte erfasst wurden > Fehlerhinweis erzeugen
        if(auftragValues.stream().anyMatch(profileAuftragValue -> Strings.isNullOrEmpty(profileAuftragValue.getParameterValue()))) {
            MessageHelper.showErrorDialog(this, new IllegalArgumentException("Profilwerte nicht vollständig"));
            return;
        }
        auftrag.setGueltigVon(validFrom);
        auftrag.setChangeReason(changeReason);
        auftrag.setBemerkung(note);
        final Either<String, ProfileAuftrag> result = profileService.persistProfileAuftrag(auftrag);
        if (result.isRight()) {
            this.init();
        }
        else {
            MessageHelper.showErrorDialog(this, new Throwable(result.getLeft()));
        }
    }

    final Map<String, DSLAMProfileChangeReason> findAllChangeReasonsNameId() {
        return profileService.findAllChangeReasons();
    }
}