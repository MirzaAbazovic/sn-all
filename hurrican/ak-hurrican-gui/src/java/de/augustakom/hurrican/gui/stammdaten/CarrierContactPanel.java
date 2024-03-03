/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.01.2009 13:08:41
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKSimpleModelOwner;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierContact;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Panel fuer die Darstellung eines Carrier-Kontakts. <br> Dieses Panel wird ausserdem in dem Admin-Frame fuer die
 * Carrier-Daten verwendet!
 *
 *
 */
public class CarrierContactPanel extends AbstractServicePanel implements AKSimpleModelOwner,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(CarrierContactPanel.class);

    private final static String XML_SOURCE = "de/augustakom/hurrican/gui/stammdaten/resources/CarrierContactPanel.xml";

    private final static String CARRIERCONTACT_CARRIER = "carriercontact.carrier";
    private final static String CARRIERCONTACT_NIEDERLASSUNG = "carriercontact.niederlassung";
    private final static String CARRIERCONTACT_RESSORT = "carriercontact.ressort";
    private final static String CARRIERCONTACT_NAME = "carriercontact.name";
    private final static String CARRIERCONTACT_STRASSE = "carriercontact.strasse";
    private final static String CARRIERCONTACT_HAUSNUMMER = "carriercontact.hausnummer";
    private final static String CARRIERCONTACT_POSTLEITZAHL = "carriercontact.postleitzahl";
    private final static String CARRIERCONTACT_ORT = "carriercontact.ort";
    private final static String CARRIERCONTACT_TELEFON = "carriercontact.telefon";
    private final static String CARRIERCONTACT_FAX = "carriercontact.fax";
    private final static String CARRIERCONTACT_EMAIL = "carriercontact.email";
    private final static String CARRIERCONTACT_TYPE = "carriercontact.type";

    private AKReferenceField rfCarrier = null;
    private AKJTextField tfNiederlassung = null;
    private AKJTextField tfRessort = null;
    private AKJTextField tfName = null;
    private AKJTextField tfStrasse = null;
    private AKJTextField tfHausnummer = null;
    private AKJTextField tfPostleitzahl = null;
    private AKJTextField tfOrt = null;
    private AKJTextField tfTelefon = null;
    private AKJTextField tfFax = null;
    private AKJTextField tfEmail = null;
    private AKReferenceField rfType = null;

    // Variable enableGUI entscheidet ob GUI-Elemente zum Editieren freigegeben werden können.
    private boolean enableGUI = false;
    private CarrierContact carrierContact = null;

    /**
     * Konstruktor
     */
    public CarrierContactPanel() {
        super(XML_SOURCE);
        this.createGUI();
    }

    /**
     * Konstruktor
     */
    public CarrierContactPanel(boolean enableGUI) {
        super(XML_SOURCE);
        this.enableGUI = enableGUI;
        this.createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblCarrier = getSwingFactory().createLabel(CARRIERCONTACT_CARRIER);
        AKJLabel lblNiederlassung = getSwingFactory().createLabel(CARRIERCONTACT_NIEDERLASSUNG);
        AKJLabel lblRessort = getSwingFactory().createLabel(CARRIERCONTACT_RESSORT);
        AKJLabel lblName = getSwingFactory().createLabel(CARRIERCONTACT_NAME);
        AKJLabel lblStrasse = getSwingFactory().createLabel(CARRIERCONTACT_STRASSE);
        AKJLabel lblHausnummer = getSwingFactory().createLabel(CARRIERCONTACT_HAUSNUMMER);
        AKJLabel lblPostleitzahl = getSwingFactory().createLabel(CARRIERCONTACT_POSTLEITZAHL);
        AKJLabel lblOrt = getSwingFactory().createLabel(CARRIERCONTACT_ORT);
        AKJLabel lblTelefon = getSwingFactory().createLabel(CARRIERCONTACT_TELEFON);
        AKJLabel lblFax = getSwingFactory().createLabel(CARRIERCONTACT_FAX);
        AKJLabel lblEmail = getSwingFactory().createLabel(CARRIERCONTACT_EMAIL);
        AKJLabel lblType = getSwingFactory().createLabel(CARRIERCONTACT_TYPE);

        rfCarrier = getSwingFactory().createReferenceField(CARRIERCONTACT_CARRIER, Carrier.class, "id", "name", null);
        tfNiederlassung = getSwingFactory().createTextField(CARRIERCONTACT_NIEDERLASSUNG);
        tfRessort = getSwingFactory().createTextField(CARRIERCONTACT_RESSORT);
        tfName = getSwingFactory().createTextField(CARRIERCONTACT_NAME);
        tfStrasse = getSwingFactory().createTextField(CARRIERCONTACT_STRASSE);
        tfHausnummer = getSwingFactory().createTextField(CARRIERCONTACT_HAUSNUMMER);
        tfPostleitzahl = getSwingFactory().createTextField(CARRIERCONTACT_POSTLEITZAHL);
        tfOrt = getSwingFactory().createTextField(CARRIERCONTACT_ORT);
        tfTelefon = getSwingFactory().createTextField(CARRIERCONTACT_TELEFON);
        tfFax = getSwingFactory().createTextField(CARRIERCONTACT_FAX);
        tfEmail = getSwingFactory().createTextField(CARRIERCONTACT_EMAIL);
        rfType = getSwingFactory().createReferenceField(CARRIERCONTACT_TYPE, Reference.class, "id", "strValue", new Reference(Reference.REF_TYPE_CARRIER_CONTACT_TYPE));

        AKJPanel carrierContactPanel = new AKJPanel(new GridBagLayout());

        // Carrier
        carrierContactPanel.add(lblCarrier, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(rfCarrier, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        // Niederlassung
        carrierContactPanel.add(lblNiederlassung, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(tfNiederlassung, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        // Ressort
        carrierContactPanel.add(lblRessort, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(tfRessort, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        // Name
        carrierContactPanel.add(lblName, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(tfName, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        // Straße
        carrierContactPanel.add(lblStrasse, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(tfStrasse, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        // Hausnummer
        carrierContactPanel.add(lblHausnummer, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(tfHausnummer, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));

        // Postleitzahl
        carrierContactPanel.add(lblPostleitzahl, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(tfPostleitzahl, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        // Ort
        carrierContactPanel.add(lblOrt, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 7, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(tfOrt, GBCFactory.createGBC(100, 0, 2, 7, 1, 1, GridBagConstraints.HORIZONTAL));

        // Telefon
        carrierContactPanel.add(lblTelefon, GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 8, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(tfTelefon, GBCFactory.createGBC(100, 0, 2, 8, 1, 1, GridBagConstraints.HORIZONTAL));

        // Fax
        carrierContactPanel.add(lblFax, GBCFactory.createGBC(0, 0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 9, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(tfFax, GBCFactory.createGBC(100, 0, 2, 9, 1, 1, GridBagConstraints.HORIZONTAL));

        // Email
        carrierContactPanel.add(lblEmail, GBCFactory.createGBC(0, 0, 0, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 10, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(tfEmail, GBCFactory.createGBC(100, 0, 2, 10, 1, 1, GridBagConstraints.HORIZONTAL));

        // Type
        carrierContactPanel.add(lblType, GBCFactory.createGBC(0, 0, 0, 11, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierContactPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 11, 1, 1, GridBagConstraints.NONE));
        carrierContactPanel.add(rfType, GBCFactory.createGBC(100, 0, 2, 11, 1, 1, GridBagConstraints.HORIZONTAL));

        manageGUI();

        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 5, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(carrierContactPanel, GBCFactory.createGBC(20, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(80, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 60, 0, 1, 1, 1, GridBagConstraints.VERTICAL));

        // Sperre GUI fürs Editieren
        enableGUI(false);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.carrierContact = (model instanceof CarrierContact) ? (CarrierContact) model : null;
        if (model != null) {
            rfCarrier.setReferenceId(carrierContact.getCarrierId());
            tfNiederlassung.setText(carrierContact.getBranchOffice());
            tfRessort.setText(carrierContact.getRessort());
            tfName.setText(carrierContact.getContactName());
            tfStrasse.setText(carrierContact.getStreet());
            tfHausnummer.setText(carrierContact.getHouseNum());
            tfPostleitzahl.setText(carrierContact.getPostalCode());
            tfOrt.setText(carrierContact.getCity());
            tfTelefon.setText(carrierContact.getFaultClearingPhone());
            tfFax.setText(carrierContact.getFaultClearingFax());
            tfEmail.setText(carrierContact.getFaultClearingEmail());
            rfType.setReferenceId(carrierContact.getContactType());
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
        if (carrierContact == null) {
            carrierContact = new CarrierContact();
        }

        carrierContact.setCarrierId(rfCarrier.getReferenceIdAs(Long.class));
        carrierContact.setBranchOffice(tfNiederlassung.getText().trim());
        carrierContact.setRessort(tfRessort.getText().trim());
        carrierContact.setContactName(tfName.getText().trim());
        carrierContact.setStreet(tfStrasse.getText().trim());
        carrierContact.setHouseNum(tfHausnummer.getText().trim());
        carrierContact.setPostalCode(tfPostleitzahl.getText());
        carrierContact.setCity(tfOrt.getText());
        carrierContact.setFaultClearingPhone(tfTelefon.getText());
        carrierContact.setFaultClearingFax(tfFax.getText());
        carrierContact.setFaultClearingEmail(tfEmail.getText());
        carrierContact.setContactType(rfType.getReferenceIdAs(Long.class));

        return carrierContact;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();

            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfCarrier.setFindService(sfs);
            rfType.setFindService(sfs);
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

}

