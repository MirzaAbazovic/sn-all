/*
 * Copyright (c) 2017 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2017
 */
package de.augustakom.hurrican.gui.tools.tal.wizard;

import java.awt.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import com.google.common.collect.Lists;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.hurrican.model.cc.WBCIVorabstimmungFax;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.service.WbciCommonService;

class VorabstimmungFaxDialog extends JDialog {
    private final URL newIconURL = ClassLoader.getSystemResource("de/augustakom/hurrican/gui/images/new.gif");
    private final URL deleteIconURL = ClassLoader.getSystemResource("de/augustakom/hurrican/gui/images/delete.gif");
    private long auftragsId;
    private RequestTyp requestTyp;
    private WbciCommonService wbciCommonService;
    private AKJTable table;
    private AKReflectionTableModel<WBCIVorabstimmungFax> tableModel;

    VorabstimmungFaxDialog(long auftragsId, WbciCommonService wbciCommonService, RequestTyp requestTyp, Component parent) {
        super(new JFrame(), "Vorabstimmung " + requestTyp.getShortName(), true);
        setLayout(new GridBagLayout());
        this.auftragsId = auftragsId;
        this.requestTyp = requestTyp;
        this.wbciCommonService = wbciCommonService;
        this.tableModel = new AKReflectionTableModel<>(
                new String[] { "Vorabstimmungs-ID", "Datum" },
                new String[] { "vorabstimmungsId", "created" },
                new Class[] { String.class, Date.class });
        this.table = new AKJTable(tableModel);
        tableModel.setData(wbciCommonService.findAll(auftragsId, requestTyp));

        add(generateButtonPanel(), GBCFactory.createGBC(1, 1, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        add(new JScrollPane(table), GBCFactory.createGBC(1, 1, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private JPanel generateButtonPanel() {
        JButton newID = new JButton(new ImageIcon(newIconURL));
        newID.setPreferredSize(new Dimension(20, 20));
        newID.setToolTipText("Generierung Vorabstimmungs-ID");

        JButton deleteID = new JButton(new ImageIcon(deleteIconURL));
        deleteID.setPreferredSize(new Dimension(20, 20));
        deleteID.setToolTipText("Vorabstimmungs-Id löschen");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(20, 300));
        buttonPanel.add(newID);
        buttonPanel.add(deleteID);

        newID.addActionListener(event -> generateVorabstimmungsID(requestTyp));
        deleteID.addActionListener(event -> deleteEntry());
        return buttonPanel;
    }

    private void generateVorabstimmungsID(RequestTyp requestTyp) {
        final String nextPreAgreementId = wbciCommonService.getNextPreAgreementIdFax(requestTyp);
        final WBCIVorabstimmungFax wbciVorabstimmungFax = new WBCIVorabstimmungFax(auftragsId, nextPreAgreementId);
        wbciCommonService.save(wbciVorabstimmungFax);
        tableModel.setData(wbciCommonService.findAll(auftragsId, requestTyp));
        MessageHelper.showInfoDialog(this, String.format("ID: %s wurde generiert", nextPreAgreementId));
    }

    private void deleteEntry() {
        final int[] selectedRows = table.getSelectedRows();
        Collection<WBCIVorabstimmungFax> toDelete = Lists.newArrayList();
        for (int row : selectedRows) {
            final WBCIVorabstimmungFax dataAtRow = tableModel.getDataAtRow(row);
            toDelete.add(dataAtRow);
        }
        try {
            wbciCommonService.delete(toDelete);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
        }
        tableModel.setData(wbciCommonService.findAll(auftragsId, requestTyp));
    }
}