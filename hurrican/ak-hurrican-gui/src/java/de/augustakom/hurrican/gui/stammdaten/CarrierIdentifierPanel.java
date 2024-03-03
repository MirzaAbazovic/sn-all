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
import de.augustakom.common.gui.iface.AKSimpleModelOwner;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Panel fuer die Darstellung eines Carrier-Kontakts. <br> Dieses Panel wird ausserdem in dem Admin-Frame fuer die
 * Carrier-Daten verwendet!
 *
 *
 */
public class CarrierIdentifierPanel extends AbstractServicePanel implements AKSimpleModelOwner {

    private static final Logger LOGGER = Logger.getLogger(CarrierIdentifierPanel.class);

    private static final String XML_SOURCE = "de/augustakom/hurrican/gui/stammdaten/resources/CarrierIdentifierPanel.xml";

    private static final String CARRIERIDENTIFIER_CARRIER = "carrieridentifier.carrier";
    private static final String CARRIERIDENTIFIER_BEZEICHNUNG = "carrieridentifier.bezeichnung";
    private static final String CARRIERIDENTIFIER_KUNDENNUMMER = "carrieridentifier.kundennummer";
    private static final String CARRIERIDENTIFIER_PORTIERUNGSKENNUNG = "carrieridentifier.portierungskennung";
    private static final String CARRIERIDENTIFIER_NAME = "carrieridentifier.name";
    private static final String CARRIERIDENTIFIER_STRASSE = "carrieridentifier.strasse";
    private static final String CARRIERIDENTIFIER_POSTLEITZAHL = "carrieridentifier.postleitzahl";
    private static final String CARRIERIDENTIFIER_ORT = "carrieridentifier.ort";
    private static final String CARRIERIDENTIFIER_NETZBETREIBER = "carrieridentifier.netzbetreiber";

    private AKReferenceField rfCarrier = null;
    private AKJTextField tfBezeichnung = null;
    private AKJTextField tfKundennummer = null;
    private AKJTextField tfPortierungskennung = null;
    private AKJTextField tfName = null;
    private AKJTextField tfStrasse = null;
    private AKJTextField tfPostleitzahl = null;
    private AKJTextField tfOrt = null;
    private AKJTextField tfNetzbetreiber = null;

    // Variable enableGUI entscheidet ob GUI-Elemente zum Editieren freigegeben werden können.
    private boolean enableGUI = false;
    private CarrierKennung carrierIdentifier = null;

    /**
     * Konstruktor
     */
    public CarrierIdentifierPanel() {
        super(XML_SOURCE);
        this.createGUI();
    }

    /**
     * Konstruktor
     */
    public CarrierIdentifierPanel(boolean enableGUI) {
        super(XML_SOURCE);
        this.enableGUI = enableGUI;
        this.createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblCarrier = getSwingFactory().createLabel(CARRIERIDENTIFIER_CARRIER);
        AKJLabel lblBezeichnung = getSwingFactory().createLabel(CARRIERIDENTIFIER_BEZEICHNUNG);
        AKJLabel lblKundennummer = getSwingFactory().createLabel(CARRIERIDENTIFIER_KUNDENNUMMER);
        AKJLabel lblPortierungskennung = getSwingFactory().createLabel(CARRIERIDENTIFIER_PORTIERUNGSKENNUNG);
        AKJLabel lblName = getSwingFactory().createLabel(CARRIERIDENTIFIER_NAME);
        AKJLabel lblStrasse = getSwingFactory().createLabel(CARRIERIDENTIFIER_STRASSE);
        AKJLabel lblPostleitzahl = getSwingFactory().createLabel(CARRIERIDENTIFIER_POSTLEITZAHL);
        AKJLabel lblOrt = getSwingFactory().createLabel(CARRIERIDENTIFIER_ORT);
        AKJLabel lblNetzbetreiber = getSwingFactory().createLabel(CARRIERIDENTIFIER_NETZBETREIBER);

        rfCarrier = getSwingFactory().createReferenceField(CARRIERIDENTIFIER_CARRIER, Carrier.class, "id", "name", null);
        tfBezeichnung = getSwingFactory().createTextField(CARRIERIDENTIFIER_BEZEICHNUNG);
        tfKundennummer = getSwingFactory().createTextField(CARRIERIDENTIFIER_KUNDENNUMMER);
        tfPortierungskennung = getSwingFactory().createTextField(CARRIERIDENTIFIER_PORTIERUNGSKENNUNG);
        tfName = getSwingFactory().createTextField(CARRIERIDENTIFIER_NAME);
        tfStrasse = getSwingFactory().createTextField(CARRIERIDENTIFIER_STRASSE);
        tfPostleitzahl = getSwingFactory().createTextField(CARRIERIDENTIFIER_POSTLEITZAHL);
        tfOrt = getSwingFactory().createTextField(CARRIERIDENTIFIER_ORT);
        tfNetzbetreiber = getSwingFactory().createTextField(CARRIERIDENTIFIER_NETZBETREIBER);

        AKJPanel carrierIdentifierPanel = new AKJPanel(new GridBagLayout());

        // Carrier
        carrierIdentifierPanel.add(lblCarrier, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierIdentifierPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        carrierIdentifierPanel.add(rfCarrier, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        // Bezeichnung
        carrierIdentifierPanel.add(lblBezeichnung, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierIdentifierPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        carrierIdentifierPanel.add(tfBezeichnung, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        // Kundennummer
        carrierIdentifierPanel.add(lblKundennummer, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierIdentifierPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.NONE));
        carrierIdentifierPanel.add(tfKundennummer, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        // Portierungskennung
        carrierIdentifierPanel.add(lblPortierungskennung, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierIdentifierPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.NONE));
        carrierIdentifierPanel.add(tfPortierungskennung, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        // Name
        carrierIdentifierPanel.add(lblName, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierIdentifierPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.NONE));
        carrierIdentifierPanel.add(tfName, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        // Strasse
        carrierIdentifierPanel.add(lblStrasse, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierIdentifierPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.NONE));
        carrierIdentifierPanel.add(tfStrasse, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));

        // Postleitzahl
        carrierIdentifierPanel.add(lblPostleitzahl, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierIdentifierPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.NONE));
        carrierIdentifierPanel.add(tfPostleitzahl, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        // Ort
        carrierIdentifierPanel.add(lblOrt, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierIdentifierPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 7, 1, 1, GridBagConstraints.NONE));
        carrierIdentifierPanel.add(tfOrt, GBCFactory.createGBC(100, 0, 2, 7, 1, 1, GridBagConstraints.HORIZONTAL));

        // Netzbetreiber
        carrierIdentifierPanel.add(lblNetzbetreiber, GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierIdentifierPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 8, 1, 1, GridBagConstraints.NONE));
        carrierIdentifierPanel.add(tfNetzbetreiber, GBCFactory.createGBC(100, 0, 2, 8, 1, 1, GridBagConstraints.HORIZONTAL));

        manageGUI();

        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 5, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(carrierIdentifierPanel, GBCFactory.createGBC(20, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
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
        this.carrierIdentifier = (model instanceof CarrierKennung) ? (CarrierKennung) model : null;
        if (model != null) {
            rfCarrier.setReferenceId(carrierIdentifier.getCarrierId());
            tfBezeichnung.setText(carrierIdentifier.getBezeichnung());
            tfKundennummer.setText(carrierIdentifier.getKundenNr());
            tfPortierungskennung.setText(carrierIdentifier.getPortierungsKennung());
            tfName.setText(carrierIdentifier.getName());
            tfStrasse.setText(carrierIdentifier.getStrasse());
            tfPostleitzahl.setText(carrierIdentifier.getPlz());
            tfOrt.setText(carrierIdentifier.getOrt());
            tfNetzbetreiber.setText(carrierIdentifier.getElTalAbsenderId());
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
        if (carrierIdentifier == null) {
            carrierIdentifier = new CarrierKennung();
        }

        carrierIdentifier.setCarrierId(rfCarrier.getReferenceIdAs(Long.class));
        carrierIdentifier.setBezeichnung(tfBezeichnung.getText().trim());
        carrierIdentifier.setKundenNr(tfKundennummer.getText().trim());
        carrierIdentifier.setPortierungsKennung(tfPortierungskennung.getText().trim());
        carrierIdentifier.setName(tfName.getText().trim());
        carrierIdentifier.setStrasse(tfStrasse.getText().trim());
        carrierIdentifier.setPlz(tfPostleitzahl.getText());
        carrierIdentifier.setOrt(tfOrt.getText());
        carrierIdentifier.setElTalAbsenderId(tfNetzbetreiber.getText());

        return carrierIdentifier;
    }

    /* Laedt die benoetigten Daten. */
    private void load() {
        try {
            setWaitCursor();

            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfCarrier.setFindService(sfs);
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
