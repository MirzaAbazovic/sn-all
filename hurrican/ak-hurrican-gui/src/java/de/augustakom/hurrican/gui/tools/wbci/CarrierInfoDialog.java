/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.14
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierContact;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.wbci.model.WbciRequest;

/**
 * Lädt alle Carrier Kontakt Informationen für die ausgewählte Vorabstimmung und zeigt diese in einer Tabelle an.
 *
 *
 */
public class CarrierInfoDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final long serialVersionUID = -349335594472384074L;
    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/CarrierInfoDialog.xml";

    private static final Logger LOGGER = Logger.getLogger(CarrierInfoDialog.class);

    private static final String DIALOG_TITLE = "dialog.title";

    private final WbciRequest wbciRequest;

    //Services
    private ReferenceService referenceService;
    private QueryCCService queryService;
    private CarrierService carrierService;

    private AKReferenceAwareTableModel<CarrierContact> tbMdlCarrierInfo = null;

    /**
     * Konstruktor mit Angabe des {@link WbciRequest}s
     *
     * @param wbciRequest
     */
    public CarrierInfoDialog(WbciRequest wbciRequest) {
        super(RESOURCE, true);
        this.wbciRequest = wbciRequest;

        try {
            initServices();
            createGUI();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    /**
     * Looks up to the CCServices and init the service.
     *
     * @throws de.augustakom.common.service.exceptions.ServiceNotFoundException if a service could not be looked up
     */
    protected final void initServices() throws ServiceNotFoundException {
        referenceService = getCCService(ReferenceService.class);
        queryService = getCCService(QueryCCService.class);
        carrierService = getCCService(CarrierService.class);
    }

    @Override
    protected final void createGUI() {
        configureButton(CMD_CANCEL, "OK", "Schliesst den Dialog", true, true);
        configureButton(CMD_SAVE, null, null, false, false);

        tbMdlCarrierInfo = new AKReferenceAwareTableModel<>(
                new String[] { "Id", "Carrier", "Niederlassung", "Ressort", "Telefon", "Fax", "EMail", "Typ" },
                new String[] { "id", "carrierId", "branchOffice", "ressort", "faultClearingPhone", "faultClearingFax", "faultClearingEmail", "contactType" },
                new Class[] { Long.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class });

        AKJTable tbCarrierInfo = new AKJTable(tbMdlCarrierInfo, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION, false);
        tbCarrierInfo.attachSorter();
        tbCarrierInfo.fitTable(new int[] { 40, 50, 120, 120, 120, 100, 100, 80 });

        AKJScrollPane tbScrollPane = new AKJScrollPane(tbCarrierInfo, new Dimension(750, 180));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(tbScrollPane, BorderLayout.CENTER);
    }

    @Override
    public final void loadData() {
        setTitle(String.format(getSwingFactory().getText(DIALOG_TITLE), wbciRequest.getWbciGeschaeftsfall().getEKPPartner().getITUCarrierCode()));

        try {
            setWaitCursor();
            showProgressBar("laden...");

            Carrier carrier = carrierService.findCarrierByCarrierCode(wbciRequest.getEKPPartner());
            tbMdlCarrierInfo.addReference(1, Collections.singletonMap(carrier.getId(), carrier), "name");

            CarrierContact carrierContact = new CarrierContact();
            carrierContact.setCarrierId(carrier.getId());
            List<CarrierContact> carrierContactList = queryService.findByExample(carrierContact, CarrierContact.class);
            tbMdlCarrierInfo.setData(carrierContactList);

            List<Reference> contactTypes = referenceService.findReferencesByType(Reference.REF_TYPE_CARRIER_CONTACT_TYPE, Boolean.TRUE);
            Map<Long, Reference> contactTypeMap = new HashMap<>();
            CollectionMapConverter.convert2Map(contactTypes, contactTypeMap, "getId", null);
            tbMdlCarrierInfo.addReference(7, contactTypeMap, "strValue");
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
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void doSave() {
    }
}
