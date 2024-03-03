/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2004 13:08:41
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.lang.reflect.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKSimpleModelOwner;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AKReferenceFieldEvent;
import de.augustakom.common.gui.swing.AKReferenceFieldObserver;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierContact;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.CarrierMapping;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Panel fuer die Darstellung eines Carrier-Mappings. <br> Dieses Panel wird ausserdem in dem Admin-Frame fuer die
 * Carrier-Daten verwendet!
 *
 *
 */
public class CarrierMappingPanel extends AbstractServicePanel implements AKSimpleModelOwner, AKReferenceFieldObserver {

    private static final Logger LOGGER = Logger.getLogger(CarrierMappingPanel.class);

    private AKReferenceField rfCarrier = null;
    private AKReferenceField rfCarrierContact = null;
    private AKReferenceField rfCarrierIdentifier = null;

    // Variable enableGUI entscheidet ob GUI-Elemente zum Editieren freigegeben werden können.
    private boolean enableGUI = false;
    private CarrierMapping carrierMapping = null;

    private static final String CARRIERMAPPING_CARRIER = "carriermapping.carrier";
    private static final String CARRIERMAPPING_CARRIERCONTACT = "carriermapping.carriercontact";
    private static final String CARRIERMAPPING_CARRIERIDENTIFIER = "carriermapping.carrieridentifier";


    /**
     * Konstruktor
     */
    public CarrierMappingPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/CarrierMappingPanel.xml");
        this.createGUI();
    }

    /**
     * Konstruktor
     */
    public CarrierMappingPanel(boolean enableGUI) {
        super("de/augustakom/hurrican/gui/stammdaten/resources/CarrierMappingPanel.xml");
        this.enableGUI = enableGUI;
        this.createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblCarrier = getSwingFactory().createLabel(CARRIERMAPPING_CARRIER);
        AKJLabel lblCarrierContact = getSwingFactory().createLabel(CARRIERMAPPING_CARRIERCONTACT);
        AKJLabel lblCarrierIdentifier = getSwingFactory().createLabel(CARRIERMAPPING_CARRIERIDENTIFIER);

        rfCarrier = getSwingFactory().createReferenceField(CARRIERMAPPING_CARRIER, Carrier.class, "id", "name", null);
        rfCarrierContact = getSwingFactory().createReferenceField(CARRIERMAPPING_CARRIERCONTACT, CarrierContact.class, "id", "branchOffice", null);
        rfCarrierIdentifier = getSwingFactory().createReferenceField(CARRIERMAPPING_CARRIERIDENTIFIER, CarrierKennung.class, "id", "bezeichnung", null);

        rfCarrier.addObserver(this);
        rfCarrierContact.addObserver(this);
        rfCarrierIdentifier.addObserver(this);

        AKJPanel carrierContactPanel = new AKJPanel(new GridBagLayout());

        // Carrier
        carrierContactPanel.add(lblCarrier, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(rfCarrier, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        // Carrier-Kontakt
        carrierContactPanel.add(lblCarrierContact, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(rfCarrierContact, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        // Carrier-Kennung
        carrierContactPanel.add(lblCarrierIdentifier, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(rfCarrierIdentifier, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        manageGUI();

        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 5, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(carrierContactPanel, GBCFactory.createGBC(20, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(80, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 60, 0, 1, 1, 1, GridBagConstraints.VERTICAL));

        load();

        // Sperre GUI fürs Editieren
        enableGUI(false);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.carrierMapping = (model instanceof CarrierMapping) ? (CarrierMapping) model : null;
        if (model != null) {
            rfCarrier.setReferenceId(carrierMapping.getCarrierId());

            rfCarrierContact.setReferenceId(carrierMapping.getCarrierContactId());
            preselectReferenceField(rfCarrier, "setCarrierId", CarrierContact.class, rfCarrierContact);

            rfCarrierIdentifier.setReferenceId(carrierMapping.getCarrierKennungId());
            preselectReferenceField(rfCarrier, "setCarrierId", CarrierKennung.class, rfCarrierIdentifier);
        }
        else {
            this.clear();
        }

        // GUI zum Editieren freigeben
        if (enableGUI) {
            enableGUI(true);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        if (carrierMapping == null) {
            carrierMapping = new CarrierMapping();
        }

        carrierMapping.setCarrierId(rfCarrier.getReferenceIdAs(Long.class));
        carrierMapping.setCarrierContactId(rfCarrierContact.getReferenceIdAs(Long.class));
        carrierMapping.setCarrierKennungId(rfCarrierIdentifier.getReferenceIdAs(Long.class));

        return carrierMapping;
    }

    /* Laedt die benoetigten Daten. */
    private void load() {
        try {
            setWaitCursor();

            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfCarrier.setFindService(sfs);
            rfCarrierContact.setFindService(sfs);
            rfCarrierIdentifier.setFindService(sfs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /* 'Loescht' alle Felder */
    private void clear() {
        GuiTools.cleanFields(this);
    }

    /* Setzt die editable-Attribute aller GUI-Elemente */
    private void enableGUI(boolean enableGUI) {
        GuiTools.enableContainerComponents(this, enableGUI);
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

    /**
     * @see de.augustakom.common.gui.swing.AKReferenceFieldObserver#update(de.augustakom.common.gui.swing.AKReferenceFieldEvent)
     */
    @Override
    public void update(final AKReferenceFieldEvent akReferenceFieldEvent) throws Exception {
        if ((akReferenceFieldEvent != null) && (akReferenceFieldEvent instanceof AKReferenceField)) {
            AKReferenceField akRefField = (AKReferenceField) akReferenceFieldEvent;

            if (akRefField.getName() != null) {
                if (CARRIERMAPPING_CARRIER.equals(akRefField.getName())) {
                    preselectReferenceField(akRefField, "setCarrierId", CarrierContact.class, rfCarrierContact);
                    preselectReferenceField(akRefField, "setCarrierId", CarrierKennung.class, rfCarrierIdentifier);
                }
                else if (CARRIERMAPPING_CARRIERCONTACT.equals(akRefField.getName())) {
                    CarrierContact selectedCarrierContact = (CarrierContact) rfCarrierContact.getReferenceObject();
                    if ((selectedCarrierContact != null) && (rfCarrier != null)) {
                        rfCarrier.setReferenceId(selectedCarrierContact.getCarrierId());
                    }
                }
                else if (CARRIERMAPPING_CARRIERIDENTIFIER.equals(akRefField.getName())) {
                    CarrierKennung selectedCarrierIdentifier = (CarrierKennung) rfCarrierIdentifier.getReferenceObject();
                    if ((selectedCarrierIdentifier != null) && (rfCarrier != null)) {
                        rfCarrier.setReferenceId(selectedCarrierIdentifier.getCarrierId());
                    }

                    preselectReferenceField(akRefField, "setCarrierId", CarrierContact.class, rfCarrierContact);
                }
            }
        }
    }

    /*
     * Methode zur Vorselektion eines AKReferenceFields
     */
    private void preselectReferenceField(AKReferenceField akRefField, String methodName, Class<?> clz, AKReferenceField refField) {
        try {
            if ((clz == null) || (methodName == null) || (refField == null)) {
                throw new HurricanGUIException("Method preselectReferenceField not initialized!");
            }

            Object object = clz.newInstance();
            Method method = clz.getDeclaredMethod(methodName, Long.class);
            method.invoke(object, akRefField.getReferenceId());
            refField.setReferenceFindExample(object);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }
}
