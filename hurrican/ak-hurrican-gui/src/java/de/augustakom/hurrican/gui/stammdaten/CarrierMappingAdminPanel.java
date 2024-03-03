/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.01.2009 15:33:32
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.CarrierContact;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.CarrierMapping;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.QueryCCService;

/**
 * Panel fuer die Administration der Carrier-Mappings.
 *
 *
 */
public class CarrierMappingAdminPanel extends CarrierAdminBasePanel {

    private static final Logger LOGGER = Logger.getLogger(CarrierMappingAdminPanel.class);

    private AKReferenceAwareTableModel<CarrierMapping> tbModelCarrier = null;

    private CarrierMappingPanel carrierMappingPanel = null;

    /**
     * Standardkonstruktor
     */
    public CarrierMappingAdminPanel() {
        super();
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbModelCarrier = new AKReferenceAwareTableModel<CarrierMapping>(
                new String[] { "ID", "Carrier", "Carrier-Kontakt", "Carrier-Kennung" },
                new String[] { "id", "carrierId", "carrierContactId", "carrierKennungId" },
                new Class[] { Long.class, String.class, String.class, String.class });
        AKJTable tbCarrier = new AKJTable(tbModelCarrier, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbCarrier.attachSorter();
        tbCarrier.addMouseListener(getTableListener());
        tbCarrier.addKeyListener(getTableListener());
        tbCarrier.fitTable(new int[] { 40, 120, 120, 120 });
        AKJScrollPane tableSP = new AKJScrollPane(tbCarrier);
        tableSP.setPreferredSize(new Dimension(750, 270));

        carrierMappingPanel = new CarrierMappingPanel(true);

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(tableSP);
        split.setBottomComponent(carrierMappingPanel);

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            showProgressBar("laden...");

            QueryCCService queryService = getCCService(QueryCCService.class);

            tbModelCarrier.addReference(1, getCarrierMap(), "name");

            List<CarrierContact> carrierContacts = queryService.findAll(CarrierContact.class);
            Map<Long, CarrierContact> ccMap = new HashMap<Long, CarrierContact>();
            CollectionMapConverter.convert2Map(carrierContacts, ccMap, "getId", null);
            tbModelCarrier.addReference(2, ccMap, "branchOffice");

            List<CarrierKennung> carrierKennungen = queryService.findAll(CarrierKennung.class);
            Map<Long, CarrierKennung> ckMap = new HashMap<Long, CarrierKennung>();
            CollectionMapConverter.convert2Map(carrierKennungen, ckMap, "getId", null);
            tbModelCarrier.addReference(3, ckMap, "bezeichnung");

            List<CarrierMapping> carrierMappings = queryService.findAll(CarrierMapping.class);
            tbModelCarrier.setData(carrierMappings);
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

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof CarrierMapping) {
            carrierMappingPanel.setModel((CarrierMapping) details);
        }
        else {
            carrierMappingPanel.setModel(null);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        carrierMappingPanel.setModel(null);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        try {
            CarrierMapping toSave = (CarrierMapping) carrierMappingPanel.getModel();
            boolean isNew = (toSave.getId() == null) ? true : false;

            CarrierService service = getCCService(CarrierService.class);
            service.saveCarrierMapping(toSave, HurricanSystemRegistry.instance().getSessionId());

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


