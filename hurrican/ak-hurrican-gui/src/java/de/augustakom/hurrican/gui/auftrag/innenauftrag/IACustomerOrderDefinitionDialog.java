/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2007 13:57:55
 */
package de.augustakom.hurrican.gui.auftrag.innenauftrag;

import java.awt.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Dialog dient zur Definition der Innenauftragsnummer. <br> Die definierte IA-Nummer wird ueber die Methode
 * <code>setValue</code> gespeichert.
 *
 *
 */
public class IACustomerOrderDefinitionDialog extends IADefinitionDialog {

    private static final Logger LOGGER = Logger.getLogger(IACustomerOrderDefinitionDialog.class);

    /**
     * Konstruktor mit Angabe der zugehoerigen Leitungsnummer.
     *
     * @param vbz
     */
    public IACustomerOrderDefinitionDialog(String vbz) {
        super(vbz);
    }

    public IACustomerOrderDefinitionDialog(String vbz, String kostenstelle) {
        super(vbz, kostenstelle);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        super.createGUI();

        child.add(cbKoUProduct, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE, ins));
        child.add(cbKoUType, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE, ins));
        child.add(tfNumber, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        tfNumber.setText(number);
    }

    @Override
    public void parametersDefined() {
        selectKindOfUseFields();
    }

    /*
    * Ermittelt die Nutzungsarten (Produkt / Typ) des Auftrags und
    * waehlt die entsprechenden Werte in den ComboBoxes aus.
    */
    private void selectKindOfUseFields() {
        try {
            VerbindungsBezeichnung vbzToUse = loadOrCalculateVbz();
            if ((vbzToUse != null) && StringUtils.isNotBlank(vbzToUse.getKindOfUseProduct())) {
                cbKoUProduct.selectItem("getStrValue", Reference.class, vbzToUse.getKindOfUseProduct());
                cbKoUType.selectItem("getStrValue", Reference.class, vbzToUse.getKindOfUseType());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /*
    * Ermittelt die Verbindungsbezeichnung des Auftrags und gibt diese zurueck.
    * Falls der Auftrag keine oder eine Verbindungsbezeichnung ohne Nutzungsart
    * besitzt, wird eine neue Verbindungsbezeichnung berechnet.
    */
    private VerbindungsBezeichnung loadOrCalculateVbz() throws ServiceNotFoundException, FindException {
        PhysikService physikService = getCCService(PhysikService.class);
        CCAuftragService auftragService = getCCService(CCAuftragService.class);

        VerbindungsBezeichnung vbzToUse = null;
        if ((ccAuftragModel != null) && (ccAuftragModel.getAuftragId() != null)) {
            vbzToUse = physikService.findVerbindungsBezeichnungByAuftragId(ccAuftragModel.getAuftragId());

            if (vbzToUse == null) {
                AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(ccAuftragModel.getAuftragId());
                if (auftragDaten != null) {
                    vbzToUse = physikService.calculateVerbindungsBezeichnung(auftragDaten.getProdId(), null);
                }
            }
        }

        return vbzToUse;
    }

    /**
     * @see de.augustakom.hurrican.gui.auftrag.innenauftrag.IADefinitionDialog#buildIaNumber()
     */
    @Override
    protected String buildIaNumber() {
        StringBuilder iaNumber = new StringBuilder();
        iaNumber.append((String) cbPrefix.getSelectedItemValue("getStrValue", String.class));
        iaNumber.append((String) cbKoUProduct.getSelectedItemValue("getStrValue", String.class));
        iaNumber.append((String) cbKoUType.getSelectedItemValue("getStrValue", String.class));
        iaNumber.append(tfNumber.getText());
        return iaNumber.toString();
    }

    /**
     * @see de.augustakom.hurrican.gui.auftrag.innenauftrag.IADefinitionDialog#validateDialog()
     */
    @Override
    protected boolean validateDialog() {
        try {
            if ((null == tfNumber) || (null == tfNumber.getText()) || (tfNumber.getText().length() == 0)) {
                throw new HurricanGUIException("Keine Vertragsnummer definiert.");
            }

            if ((null != tfNumber) && (null != tfNumber.getText()) && (tfNumber.getText().length() > 9)) {
                throw new HurricanGUIException("Vertragsnummer zu lang, maximal neun Stellen zulässig.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showInfoDialog(this, e.getMessage(), null, true);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
