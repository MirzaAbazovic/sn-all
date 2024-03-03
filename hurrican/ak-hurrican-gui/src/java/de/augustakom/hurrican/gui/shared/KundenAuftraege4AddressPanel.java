/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.2015
 */

package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;
import de.augustakom.hurrican.service.cc.CCKundenService;

public class KundenAuftraege4AddressPanel extends AbstractServicePanel {

    private static final Logger LOGGER = Logger.getLogger(KundenAuftraege4AddressPanel.class);

    private final Long addressId;

    private AKReflectionTableModel<CCKundeAuftragView> tbMdlKundenAuftraege;

    public KundenAuftraege4AddressPanel(Long addressId) {
        super(null);
        this.addressId = addressId;

        createGUI();
        loadData();
    }

    @Override
    protected void createGUI() {
        tbMdlKundenAuftraege = new AKReflectionTableModel<>(
                new String[] { "Auftrag-Nr.", "Kunde__No", "Order__No", "Auftrag-Status", "Produkt", "Verb.-Bez.", "Buendel-Nr" },
                new String[] { "auftragId", "kundeNo", "auftragNoOrig", "statusText", "produktName", "vbz", "buendelNr" },
                new Class[] { Long.class, Long.class, Long.class, String.class, String.class, String.class,
                        Integer.class }
        );

        AKJTable tbKundenAuftraege = new AKJTable(tbMdlKundenAuftraege, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbKundenAuftraege.attachSorter();
        tbKundenAuftraege.fitTable(new int[] { 70, 70, 70, 125, 125, 125, 100 });

        AKJScrollPane spTable = new AKJScrollPane(tbKundenAuftraege);
        spTable.setPreferredSize(new Dimension(700, 150));
        this.add(spTable);
    }

    /**
     * Laedt die Auftrags-Daten zur angegebenen Rangierung.
     */
    private void loadData() {
        tbMdlKundenAuftraege.setData(null);
        if (addressId != null) {
            setWaitCursor();

            try {
                tbMdlKundenAuftraege.setData(getCCService(CCKundenService.class).findKundeAuftragViews4Address(addressId));
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    @Override
    protected void execute(String command) {

    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
