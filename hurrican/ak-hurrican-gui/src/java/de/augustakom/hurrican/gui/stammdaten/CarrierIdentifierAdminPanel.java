/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.01.2009 15:33:32
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Panel fuer die Administration der Carrier-Kennung.
 *
 *
 */
public class CarrierIdentifierAdminPanel extends CarrierAdminBasePanel {

    private static final Logger LOGGER = Logger.getLogger(CarrierIdentifierAdminPanel.class);

    private AKReferenceAwareTableModel<CarrierKennung> tbModelCarrier = null;
    private boolean loaded = false;

    private CarrierIdentifierPanel carrierIdentifierPanel = null;

    /**
     * Standardkonstruktor
     */
    public CarrierIdentifierAdminPanel() {
        super();
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbModelCarrier = new AKReferenceAwareTableModel<CarrierKennung>(
                new String[] { "ID", "Carrier", "Bezeichnung", "Kundennummer", "Portierungskennung",
                        "Name", "Strasse", "PLZ", "Ort", "Netzbetreiber" },
                new String[] { "id", "carrierId", "bezeichnung", "kundenNr", "portierungsKennung",
                        "name", "strasse", "plz", "ort", "elTalAbsenderId" },
                new Class[] { Long.class, String.class, String.class, String.class, String.class,
                        String.class, String.class, String.class, String.class, String.class }
        );
        AKJTable tbCarrier = new AKJTable(tbModelCarrier, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbCarrier.attachSorter();
        tbCarrier.addMouseListener(getTableListener());
        tbCarrier.addKeyListener(getTableListener());
        tbCarrier.fitTable(new int[] { 40, 120, 120, 110, 110, 150, 100, 80, 100, 70 });
        AKJScrollPane tableSP = new AKJScrollPane(tbCarrier);
        tableSP.setPreferredSize(new Dimension(750, 270));

        carrierIdentifierPanel = new CarrierIdentifierPanel(true);

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(tableSP);
        split.setBottomComponent(carrierIdentifierPanel);

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
        if (!loaded) {
            loaded = true;
            try {
                setWaitCursor();
                showProgressBar("laden...");

                tbModelCarrier.addReference(1, getCarrierMap(), "name");

                QueryCCService queryService = getCCService(QueryCCService.class);
                List<CarrierKennung> carrierKennung = queryService.findAll(CarrierKennung.class);
                tbModelCarrier.setData(carrierKennung);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
                stopProgressBar();
            }
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof CarrierKennung) {
            carrierIdentifierPanel.setModel((CarrierKennung) details);
        }
        else {
            carrierIdentifierPanel.setModel(null);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        carrierIdentifierPanel.setModel(null);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        try {
            CarrierKennung toSave = (CarrierKennung) carrierIdentifierPanel.getModel();
            boolean isNew = (toSave.getId() == null) ? true : false;

            CarrierService service = getCCService(CarrierService.class);
            service.saveCarrierIdentifier(toSave, HurricanSystemRegistry.instance().getSessionId());

            if (isNew) {
                tbModelCarrier.addObject(toSave);
            }
            tbModelCarrier.fireTableDataChanged();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


