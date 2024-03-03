/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2007 13:57:55
 */
package de.augustakom.hurrican.gui.auftrag.innenauftrag;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Dialog dient zur Definition der Innenauftragsnummer. <br> Die definierte IA-Nummer wird ueber die Methode
 * <code>setValue</code> gespeichert.
 *
 *
 */
public class IADefinitionDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(IADefinitionDialog.class);

    protected String number = null;

    protected AKJComboBox cbPrefix = null;
    protected AKJComboBox cbKoUProduct = null;
    protected AKJComboBox cbKoUType = null;
    protected AKJComboBox cbServiceRoom = null;
    protected AKJComboBox cbEquipment = null;
    protected AKJPanel child = null;
    protected AKJTextField tfNumber = null;
    protected AKJTextField tfKostenstelle = null;

    protected final Insets ins = new Insets(5, 5, 5, 5);

    protected BAuftrag billingAuftrag;
    protected CCAuftragModel ccAuftragModel;
    protected String kostenstelle;

    /**
     * Konstruktor mit Angabe der zugehoerigen Leitungsnummer.
     *
     * @param verbindungsBezeichnung
     */
    public IADefinitionDialog(String number) {
        this(number, "");
    }

    public IADefinitionDialog(String number, String kostenstelle) {
        super("de/augustakom/hurrican/gui/auftrag/innenauftrag/resources/IADefinitionDialog.xml");
        this.number = number;
        this.kostenstelle = kostenstelle;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKCustomListCellRenderer<Reference> cellRenderer =
                new AKCustomListCellRenderer<>(Reference.class, Reference::getStrValue);
        cbPrefix = getSwingFactory().createComboBox("ia.prefix", cellRenderer);
        cbServiceRoom = getSwingFactory().createComboBox("ia.serviceRoom", cellRenderer);
        cbEquipment = getSwingFactory().createComboBox("ia.equipment", cellRenderer);
        cbKoUProduct = getSwingFactory().createComboBox("kind.of.use.product", cellRenderer);
        cbKoUType = getSwingFactory().createComboBox("kind.of.use.type", cellRenderer);

        tfNumber = getSwingFactory().createTextField("ia.vbz");

        child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(cbPrefix, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE, ins));

        // kostenstelle
        final AKJPanel kostenStellePanel = new AKJPanel();
        child.setLayout(new GridBagLayout());
        child.add(kostenStellePanel, GBCFactory.createGBC(0, 0, 0, 1, 4, 1, GridBagConstraints.BOTH));

        final AKJLabel lblKostenstelle = getSwingFactory().createLabel("ia.kostenstelle");
        tfKostenstelle = getSwingFactory().createTextField("ia.kostenstelle");
        kostenStellePanel.add(lblKostenstelle, GBCFactory.createGBC(  0, 0,  0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        kostenStellePanel.add(new AKJPanel(),  GBCFactory.createGBC(  0, 0,  1, 0, 1, 1, GridBagConstraints.NONE));
        kostenStellePanel.add(tfKostenstelle,  GBCFactory.createGBC(  0, 0,  2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        kostenStellePanel.add(new AKJPanel(),  GBCFactory.createGBC(  0, 0,  3, 0, 1, 1, GridBagConstraints.NONE));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            ReferenceService referenceService = getCCService(ReferenceService.class);
            List<Reference> nks = referenceService.findReferencesByType(Reference.REF_TYPE_IA_NIEDERLASSUNG, Boolean.TRUE);
            cbPrefix.addItems(nks, true);

            List<Reference> rooms = referenceService.findReferencesByType(Reference.REF_TYPE_IA_BETRIEBSRAUM, Boolean.TRUE);
            cbServiceRoom.addItems(rooms, true);

            List<Reference> products = referenceService.findReferencesByType(Reference.REF_TYPE_VBZ_PRODUCT, Boolean.TRUE);
            cbKoUProduct.addItems(products, true);

            List<Reference> types = referenceService.findReferencesByType(Reference.REF_TYPE_VBZ_TYPE, Boolean.TRUE);
            cbKoUType.addItems(types, true);

            List<Reference> iaArt = referenceService.findReferencesByType(Reference.REF_TYPE_IA_ART, Boolean.TRUE);
            cbEquipment.addItems(iaArt, true);

            tfKostenstelle.setText(this.kostenstelle);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    @Override
    protected void validateSaveButton() {
        // do nothing
    }

    /**
     * Muss vom Client aufgerufen werden, wenn alle Parameter definiert sind.
     */
    public void parametersDefined() {

    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        if (validateDialog()) {
            String iaNumber = buildIaNumber();
            final String kostenstelle = tfKostenstelle.getText(null);
            prepare4Close();
            final String[] result = { iaNumber, kostenstelle };
            setValue(result);
        }
    }

    /**
     * Erzeugt die Nummer für den Innenauftrag
     */
    protected String buildIaNumber() {
        return null;
    }


    /**
     * Methode für die Validierung des Dialogs
     */
    protected boolean validateDialog() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    public void setBillingAuftrag(BAuftrag billingAuftrag) {
        this.billingAuftrag = billingAuftrag;
    }

    public void setCcAuftragModel(CCAuftragModel ccAuftragModel) {
        this.ccAuftragModel = ccAuftragModel;
    }
}


