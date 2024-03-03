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
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierVaModus;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.QueryCCService;

/**
 * Panel fuer die Administration der Carrier Daten.
 */
public class CarrierDataAdminPanel extends CarrierAdminBasePanel {

    private static final long serialVersionUID = 4822142898978967459L;
    private static final Logger LOGGER = Logger.getLogger(CarrierDataAdminPanel.class);

    private AKTableModel<Carrier> tbModelCarrier;
    private CarrierPanel carrierPanel;

    @Override
    protected final void createGUI() {
        // @formatter:off
        tbModelCarrier = new AKReflectionTableModel(
                new String[] { "ID", "Bezeichnung", "Bestellung erforderlich", "Portierungskennung", "ITU Carrier Code", "Firmenname", "Sortierung", "Netzbetreiber (ESAA)", "Auf Wita", "Cuda Kündigung", "Vorabstimmung Modus"},
                new String[] { "id", "name", "cbNotwendig", "portierungskennung", "ituCarrierCode", "companyName", "orderNo", "elTalEmpfId", "hasWitaInterface", "cudaKuendigungNotwendig", "vorabstimmungsModus"},
                new Class[] { Long.class, String.class, Boolean.class, String.class, String.class, String.class, Integer.class, String.class, Boolean.class, Boolean.class, CarrierVaModus.class});
        // @formatter:on
        AKJTable tbCarrier = new AKJTable(tbModelCarrier, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbCarrier.attachSorter();
        tbCarrier.addMouseListener(getTableListener());
        tbCarrier.addKeyListener(getTableListener());
        AKJScrollPane tableSP = new AKJScrollPane(tbCarrier);
        tableSP.setPreferredSize(new Dimension(750, 270));
        tbCarrier.fitTable(new int[] { 40, 120, 120, 200, 200, 60 });

        carrierPanel = new CarrierPanel(true);

        AKJSplitPane split = new AKJSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(tableSP);
        split.setBottomComponent(carrierPanel);

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);
    }

    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            showProgressBar("laden...");

            QueryCCService queryService = getCCService(QueryCCService.class);
            List<Carrier> carrierList = queryService.findAll(Carrier.class);
            tbModelCarrier.setData(carrierList);
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

    @Override
    public void showDetails(Object details) {
        if (details instanceof Carrier) {
            carrierPanel.setModel((Carrier) details);
        }
        else {
            carrierPanel.setModel(null);
        }
    }

    @Override
    public void createNew() {
        carrierPanel.setModel(null);
    }

    @Override
    public void saveData() {
        try {
            Carrier toSave = (Carrier) carrierPanel.getModel();
            boolean isNew = (toSave.getId() == null) ? true : false;

            CarrierService service = getCCService(CarrierService.class);
            service.saveCarrier(toSave, HurricanSystemRegistry.instance().getSessionId());

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

    @Override
    protected void execute(String command) {
        // not used
    }

    @Override
    public void update(Observable o, Object arg) {
        // not useds
    }

}
