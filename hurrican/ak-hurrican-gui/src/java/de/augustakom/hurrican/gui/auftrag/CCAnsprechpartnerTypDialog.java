/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.07.2007 13:48:18
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog zur Auswahl eines Ansprechpartnertyps, eines Adresstyps und ob ein Ansprechpartner preferred ist.
 */
public class CCAnsprechpartnerTypDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {
    private static final Logger LOGGER = Logger.getLogger(CCAnsprechpartnerTypDialog.class);
    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/CCAnsprechpartnerTypDialog.xml";
    private static final long serialVersionUID = 6002153930209197519L;

    private AKReferenceField rfAnspType;
    private AKReferenceField rfAddressType;
    private AKJCheckBox chbPreferred;
    private ISimpleFindService ccSimpleFindService;

    public static class Result {
        public final Ansprechpartner.Typ ansprechpartnerTyp;
        public final Long addressType;
        public final Boolean preferred;

        public Result(Ansprechpartner.Typ ansprechpartnerTyp, Long addressType, Boolean preferred) {
            this.ansprechpartnerTyp = ansprechpartnerTyp;
            this.addressType = addressType;
            this.preferred = preferred;
        }
    }

    public CCAnsprechpartnerTypDialog() {
        super(RESOURCE);
        initServices();
        createGUI();
        initFirst();
    }

    private void initServices() {
        try {
            ccSimpleFindService = getCCService(QueryCCService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        AKJLabel lblAnspType = getSwingFactory().createLabel("ansprechpartner.type");
        AKJLabel lblAddressType = getSwingFactory().createLabel("address.type");
        AKJLabel lblPreferred = getSwingFactory().createLabel("ansprechpartner.preferred");

        rfAnspType = getSwingFactory().createReferenceField("ansprechpartner.type");
        rfAddressType = getSwingFactory().createReferenceField("address.type");
        chbPreferred = getSwingFactory().createCheckBox("ansprechpartner.preferred", true);

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(lblAnspType, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(rfAnspType, GBCFactory.createGBC(500, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblAddressType, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(rfAddressType, GBCFactory.createGBC(500, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblPreferred, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(chbPreferred, GBCFactory.createGBC(500, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 4, 1, 1, GridBagConstraints.BOTH));
    }

    private void initFirst() {
        try {
            Reference adrTypeEx = new Reference();
            adrTypeEx.setType(Reference.REF_TYPE_ADDRESS_TYPE);
            adrTypeEx.setGuiVisible(Boolean.TRUE);
            rfAddressType.setFindService(ccSimpleFindService);
            rfAddressType.setReferenceFindExample(adrTypeEx);

            Reference anspTypeEx = new Reference();
            anspTypeEx.setType(Reference.REF_TYPE_ANSPRECHPARTNER);
            anspTypeEx.setGuiVisible(Boolean.TRUE);
            rfAnspType.setFindService(ccSimpleFindService);
            rfAnspType.setReferenceFindExample(anspTypeEx);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void doSave() {
        try {
            Long ansprechpartnerTypeInt = rfAnspType.getReferenceIdAs(Long.class);
            Long addressType = rfAddressType.getReferenceIdAs(Long.class);
            Boolean preferred = chbPreferred.isSelected() ? Boolean.TRUE : Boolean.FALSE;

            if ((addressType != null) && (ansprechpartnerTypeInt != null)) {
                Ansprechpartner.Typ ansprechpartnerType = Ansprechpartner.Typ.forRefId(ansprechpartnerTypeInt);
                prepare4Close();
                setValue(new Result(ansprechpartnerType, addressType, preferred));
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public final void loadData() {
        // noting to do
    }

    @Override
    public void update(Observable o, Object arg) {
        // nothing to do

    }

    @Override
    protected void execute(String command) {
        // nothing to do
    }

}


