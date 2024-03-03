/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2007 16:17:25
 */
package de.augustakom.hurrican.gui.auftrag.innenauftrag;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterial;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahme;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahmeArtikel;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.InnenauftragService;


/**
 * Dialog, um einer Materialentnahme einen Artikel zuzuordnen.
 *
 *
 */
public class IAMaterialEntnahmeArtikelDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(IAMaterialEntnahmeArtikelDialog.class);

    private IAMaterialEntnahme matEnt = null;

    private AKJTextField tfArtikel = null;
    private AKJTextField tfMatNr = null;
    private AKJFormattedTextField tfAnzahl = null;
    private AKJFormattedTextField tfEPreis = null;
    private AKJTextField tfAnlagenBez = null;

    /**
     * Konstruktor.
     *
     * @param matEnt zugehoerige Materialentnahme.
     */
    public IAMaterialEntnahmeArtikelDialog(IAMaterialEntnahme matEnt) {
        super("de/augustakom/hurrican/gui/auftrag/innenauftrag/resources/IAMaterialEntnahmeArtikelDialog.xml");
        this.matEnt = matEnt;
        if (this.matEnt == null) {
            throw new IllegalArgumentException("Es muss eine Materialentnahme uebergeben werden!");
        }
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblArtikel = getSwingFactory().createLabel("artikel");
        AKJLabel lblMatNr = getSwingFactory().createLabel("material.nr");
        AKJLabel lblAnzahl = getSwingFactory().createLabel("anzahl");
        AKJLabel lblEPreis = getSwingFactory().createLabel("einzelpreis");
        AKJLabel lblAnlagenBez = getSwingFactory().createLabel("anlagenbezeichnung");

        tfArtikel = getSwingFactory().createTextField("artikel");
        tfMatNr = getSwingFactory().createTextField("material.nr");
        tfAnzahl = getSwingFactory().createFormattedTextField("anzahl");
        tfEPreis = getSwingFactory().createFormattedTextField("einzelpreis");
        tfAnlagenBez = getSwingFactory().createTextField("anlagenbezeichnung");
        AKJButton btnSelectArt = getSwingFactory().createButton("select.artikel", getActionListener());
        btnSelectArt.setPreferredSize(new Dimension(20, 20));
        AKJButton btnSelectABez = getSwingFactory().createButton("select.anlagenbezeichnung", getActionListener());
        btnSelectABez.setPreferredSize(new Dimension(20, 20));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblArtikel, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        child.add(tfArtikel, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(btnSelectArt, GBCFactory.createGBC(0, 0, 4, 1, 1, 1, GridBagConstraints.NONE));
        child.add(lblMatNr, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfMatNr, GBCFactory.createGBC(0, 0, 3, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblAnzahl, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfAnzahl, GBCFactory.createGBC(0, 0, 3, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblEPreis, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfEPreis, GBCFactory.createGBC(0, 0, 3, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblAnlagenBez, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfAnlagenBez, GBCFactory.createGBC(0, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(btnSelectABez, GBCFactory.createGBC(0, 0, 4, 5, 1, 1, GridBagConstraints.NONE));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 6, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    protected void validateSaveButton() {
        // nothing to do
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    protected void doSave() {
        try {
            IAMaterialEntnahmeArtikel mea = new IAMaterialEntnahmeArtikel();
            mea.setMaterialEntnahmeId(this.matEnt.getId());
            mea.setArtikel(tfArtikel.getText(null));
            mea.setAnzahl(tfAnzahl.getValueAsFloat(null));
            mea.setEinzelpreis(tfEPreis.getValueAsFloat(null));
            mea.setMaterialNr(tfMatNr.getText(null));
            mea.setAnlagenBez(tfAnlagenBez.getText(null));

            InnenauftragService ias = getCCService(InnenauftragService.class);
            ias.saveMaterialEntnahmeArtikel(mea);

            prepare4Close();
            setValue(mea);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    protected void execute(String command) {
        if ("select.artikel".equals(command)) {
            selectArticle();
        }
        else if ("select.anlagenbezeichnung".equals(command)) {
            selectAnlagenBez();
        }
    }

    /* Auswahl eines Artikels ueber die SAP-Materialliste */
    private void selectArticle() {
        try {
            SelectArtikelDialog dlg = new SelectArtikelDialog();
            Object selection = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (selection instanceof IAMaterial) {
                IAMaterial material = (IAMaterial) selection;
                tfArtikel.setText(material.getArtikel());
                tfMatNr.setText(material.getMaterialNr());
                tfEPreis.setValue(material.getEinzelpreis());
                tfAnzahl.setValue(Integer.valueOf(1));
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /*
     * Auswahl einer Anlagenbezeichnung ueber die HW-Racks des Standorts.
     * (Der Standort ist ueber die Material-Entnahme definiert.)
     */
    private void selectAnlagenBez() {
        try {
            if (matEnt.getHvtIdStandort() == null) {
                throw new HurricanGUIException(
                        "Auswahl der Anlagenbezeichnung nicht moeglich, da kein Standort definiert ist!");
            }

            HWService hws = getCCService(HWService.class);
            List<HWRack> racks = hws.findRacks(matEnt.getHvtIdStandort());
            if (CollectionTools.isNotEmpty(racks)) {
                CollectionUtils.filter(racks, new Predicate() {
                    public boolean evaluate(Object obj) {
                        // nur Racks mit Anlagenbezeichnung filtern
                        return StringUtils.isNotBlank(((HWRack) obj).getAnlagenBez()) ? true : false;
                    }
                });
            }

            if (CollectionTools.isEmpty(racks)) {
                throw new HurricanGUIException("Es wurden keine Anlagenbezeichnungen zu dem Standort gefunden!");
            }

            // Anlagenbezeichnungen zur Auswahl anbieten
            Object result = MessageHelper.showInputDialog(getMainFrame(), racks,
                    new AKCustomListCellRenderer<>(HWRack.class, HWRack::getAnlagenBez),
                    "Anlagen-Bezeichnung", "Auswahl der Anlagen-Bezeichnung", "Anlagen-Bez.:");
            if (result instanceof HWRack) {
                tfAnlagenBez.setText(((HWRack) result).getAnlagenBez());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

}


