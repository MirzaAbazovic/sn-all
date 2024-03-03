/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.02.2015
 */
package de.augustakom.hurrican.gui.sip.peering;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.time.DateUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.IpAddressInputDialog;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSet;

class NewIpSetDialog extends AbstractServiceOptionDialog {
    private static final long serialVersionUID = 6469136034320967335L;

    private AKJButton btnNewIp;
    private AKJButton btnDelIp;
    private AKJDateComponent dcGueltigAb;

    private IpSetTableModel ipSetTableModel;
    private AKJTable ipSetTable;

    private final Date todayExact;

    public NewIpSetDialog() {
        super("de/augustakom/hurrican/gui/sip/peering/resources/NewIpSetDialog.xml");
        todayExact = new Date();
        createGUI();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        final AKJPanel outerPanel = new AKJPanel(new GridBagLayout());
        outerPanel.setBorder(BorderFactory.createEmptyBorder());

        final AKJPanel ipSetEditPanel = createIpSetEditPanel();
        final AKJScrollPane ipSetTable = createIpSetTableScrollPane();

        // @formatter:off
        outerPanel.add(ipSetEditPanel, GBCFactory.createGBC(100,   0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        outerPanel.add(ipSetTable,     GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
        getChildPanel().add(outerPanel);
        getChildPanel().setBorder(BorderFactory.createEmptyBorder());

        // logic:
        btnNewIp.addActionListener(this::handleBtnNewIp);
        btnDelIp.addActionListener(this::handleBtnDelIp);
    }

    private AKJPanel createIpSetEditPanel() {
        btnNewIp = getSwingFactory().createButton("new.ip");
        btnDelIp = getSwingFactory().createButton("del.ip");

        final AKJLabel lblGueltigAb = getSwingFactory().createLabel("gueltig.ab");
        dcGueltigAb = new AKJDateComponent(getTodayMidnight());

        final AKJPanel editPanel = new AKJPanel(new GridBagLayout());
        // @formatter:off
        editPanel.add(btnNewIp,       GBCFactory.createGBC( 0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        editPanel.add(btnDelIp,       GBCFactory.createGBC( 0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        editPanel.add(lblGueltigAb,   GBCFactory.createGBC( 0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        editPanel.add(dcGueltigAb,    GBCFactory.createGBC(20, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(new AKJPanel(), GBCFactory.createGBC(80, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
        return editPanel;
    }

    private AKJScrollPane createIpSetTableScrollPane() {
        ipSetTableModel = new IpSetTableModel();
        ipSetTable = new AKJTable(ipSetTableModel, AKJTable.AUTO_RESIZE_ALL_COLUMNS,
                ListSelectionModel.SINGLE_SELECTION);
        return new AKJScrollPane(ipSetTable, new Dimension(520, 190));
    }

    private void handleBtnNewIp(ActionEvent event) {
        final IpAddressInputDialog inputDialog = new IpAddressInputDialog(new IPAddress());
        final Object result = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), inputDialog,
                true, true);
        if (!(result instanceof IPAddress)) {
            return;
        }

        final IPAddress ipAddress = (IPAddress) result;
        if (ipAddress.getAddress() == null || ipAddress.getAddress().isEmpty()) {
            return;
        }

        ipSetTableModel.addObject(ipAddress);
    }

    private void handleBtnDelIp(ActionEvent event) {
        final int selectedRow = ipSetTable.getSelectedRow();
        if (selectedRow >= 0) {
            final IPAddress dataAtRow = ipSetTableModel.getDataAtRow(selectedRow);
            ipSetTableModel.removeObject(dataAtRow);
        }
    }

    /**
     * Liefert für heute die Zeit Sekunden genau. Liegt die Zeit in der Zukunft/Vergangenheit wird die Zeit nur
     * tagesgenau berechnet. Wenn ein Set zu heute eingestellt wird, besteht die Gefahr, dass ein anderes aelteres Set
     * zu heute abgeloest wird. Hier reichen tagesgenaue Zeiten nicht aus. Wenn allerdings ein Datum fuer die Zukunft
     * eingestellt wird, gilt der Tag ab der ersten Sekunde (also Mitternacht).
     */
    private Date getAndTransformSelectedDate() {
        Date selectedDate = getSelectedDate();
        Date selectedDateTrunc = DateUtils.truncate(selectedDate, Calendar.DAY_OF_MONTH);

        if (selectedDateTrunc.getTime() != getTodayMidnight().getTime()) {
            // Das Datum ist nicht zu heute -> Datum tagesgenau
            selectedDate = selectedDateTrunc;
        }
        else if (selectedDate.getTime() == selectedDateTrunc.getTime()) {
            // Datum ist heute, aber nur tagesgenau -> Datum exact
            selectedDate = getTodayExact();
        }
        return selectedDate;
    }

    private Date getSelectedDate() {
        return dcGueltigAb.getDate(getTodayExact());
    }

    private Date getTodayMidnight() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date getTodayExact() {
        return todayExact;
    }

    @Override
    public Object getValue() {
        final Collection<IPAddress> data = ipSetTableModel.getData();
        if (data == null || data.isEmpty()) {
            return null;
        }

        final SipSbcIpSet ipSet = new SipSbcIpSet();
        ipSet.setGueltigAb(getAndTransformSelectedDate());
        data.stream().forEach(ipSet::addSbcIp);
        return ipSet;
    }

    @Override
    protected void doSave() {
        if (DateTools.isBefore(getSelectedDate(), getTodayMidnight())) {
            MessageHelper.showWarningDialog(getMainFrame(), getSwingFactory().getText("date.in.past"), true);
        }
        else {
            prepare4Close();
            setValue(getValue());
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
