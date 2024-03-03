/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.2009 15:33:32
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
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Panel fuer die Administration der Carrier-Kontakte.
 *
 *
 */
public class CarrierContactAdminPanel extends CarrierAdminBasePanel {

    private static final Logger LOGGER = Logger.getLogger(CarrierContactAdminPanel.class);

    private AKReferenceAwareTableModel<CarrierContact> tbMdlCarrierContact = null;

    private CarrierContactPanel carrierContactPanel = null;

    // Services
    private ReferenceService referenceService;

    /**
     * Standardkonstruktor
     */
    public CarrierContactAdminPanel() {
        super();
        createGUI();
        init();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbMdlCarrierContact = new AKReferenceAwareTableModel<CarrierContact>(
                new String[] { "ID", "Carrier", "Niederlassung", "Ressort", "Name", "Strasse",
                        "Haus-Nr", "PLZ", "Ort", "Telefon", "Fax", "EMail", "Typ" },
                new String[] { "id", "carrierId", "branchOffice", "ressort", "contactName", "street",
                        "houseNum", "postalCode", "city", "faultClearingPhone", "faultClearingFax", "faultClearingEmail", "contactType" },
                new Class[] { Long.class, String.class, String.class, String.class, String.class, String.class,
                        String.class, String.class, String.class, String.class, String.class, String.class, String.class }
        );
        AKJTable tbCarrierContact = new AKJTable(tbMdlCarrierContact, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbCarrierContact.attachSorter();
        tbCarrierContact.addMouseListener(getTableListener());
        tbCarrierContact.addKeyListener(getTableListener());
        tbCarrierContact.fitTable(new int[] { 40, 120, 120, 120, 120, 100, 80, 60, 120, 100, 100, 100 });
        AKJScrollPane tableSP = new AKJScrollPane(tbCarrierContact);
        tableSP.setPreferredSize(new Dimension(750, 270));

        carrierContactPanel = new CarrierContactPanel(true);

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(tableSP);
        split.setBottomComponent(carrierContactPanel);

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

            tbMdlCarrierContact.addReference(1, getCarrierMap(), "name");

            QueryCCService queryService = getCCService(QueryCCService.class);
            List<CarrierContact> carrierContactList = queryService.findAll(CarrierContact.class);
            tbMdlCarrierContact.setData(carrierContactList);

            List<Reference> contactTypes = referenceService.findReferencesByType(Reference.REF_TYPE_CARRIER_CONTACT_TYPE, Boolean.TRUE);
            Map<Long, Reference> contactTypeMap = new HashMap<Long, Reference>();
            CollectionMapConverter.convert2Map(contactTypes, contactTypeMap, "getId", null);
            tbMdlCarrierContact.addReference(12, contactTypeMap, "strValue");
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
     * Init fuer die Services
     */
    private void init() {
        try {
            referenceService = getCCService(ReferenceService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof CarrierContact) {
            carrierContactPanel.setModel((CarrierContact) details);
        }
        else {
            carrierContactPanel.setModel(null);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        carrierContactPanel.setModel(null);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        try {
            CarrierContact toSave = (CarrierContact) carrierContactPanel.getModel();
            boolean isNew = (toSave.getId() == null) ? true : false;

            CarrierService service = getCCService(CarrierService.class);
            service.saveCarrierContact(toSave, HurricanSystemRegistry.instance().getSessionId());

            if (isNew) {
                tbMdlCarrierContact.addObject(toSave);
            }
            tbMdlCarrierContact.fireTableDataChanged();
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
