/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2004 13:08:41
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKSimpleModelOwner;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.NiederlassungService;

/**
 * Dialog fuer die Darstellung einer HVT-Gruppe.
 *
 *
 */
public class HVTGruppeDialog extends AbstractServiceOptionDialog implements AKSimpleModelOwner, AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(HVTGruppeDialog.class);

    private AKJTextField tfOnkz = null;
    private AKJTextField tfHvtName = null;
    private AKJTextField tfStrasse = null;
    private AKJTextField tfHNr = null;
    private AKJTextField tfPLZ = null;
    private AKJTextField tfOrt = null;
    private AKJTextField tfSwitch = null;
    private AKJComboBox cbNiederlassung = null;
    private AKJTextField tfKostenstelle = null;
    private AKJTextField tfInnenauftrag = null;
    private AKJTextField tfTelefon = null;

    private AKJCheckBox cbMontag = null;
    private AKJCheckBox cbDienstag = null;
    private AKJCheckBox cbMittwoch = null;
    private AKJCheckBox cbDonnerstag = null;
    private AKJCheckBox cbFreitag = null;

    private HVTGruppe hvtGruppe = null;

    /**
     * Konstruktor
     */
    public HVTGruppeDialog() {
        super("de/augustakom/hurrican/gui/hvt/resources/HVTGruppeDialog.xml");
        this.createGUI();
        this.loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        configureButton(CMD_SAVE, "", "", false, true);
        configureButton(CMD_CANCEL, "Schliessen", "Schliesst den Dialog", true, true);

        AKJLabel lblOnkz = getSwingFactory().createLabel("hvt.gruppe.onkz");
        AKJLabel lblHvtName = getSwingFactory().createLabel("hvt.gruppe.name");
        AKJLabel lblStrasse = getSwingFactory().createLabel("hvt.gruppe.strasse");
        AKJLabel lblOrt = getSwingFactory().createLabel("hvt.gruppe.plz.ort");
        AKJLabel lblSwitch = getSwingFactory().createLabel("hvt.gruppe.switch");
        AKJLabel lblNL = getSwingFactory().createLabel("hvt.gruppe.niederlassung");
        AKJLabel lblKostenstelle = getSwingFactory().createLabel("hvt.gruppe.kostenstelle");
        AKJLabel lblInnenauftrag = getSwingFactory().createLabel("hvt.gruppe.innenauftrag");
        AKJLabel lblTelefon = getSwingFactory().createLabel("hvt.gruppe.telefon");
        AKJLabel lblTage = getSwingFactory().createLabel("hvt.gruppe.schaltungstage");

        tfOnkz = getSwingFactory().createTextField("hvt.gruppe.onkz");
        tfHvtName = getSwingFactory().createTextField("hvt.gruppe.name");
        tfStrasse = getSwingFactory().createTextField("hvt.gruppe.strasse");
        tfHNr = getSwingFactory().createTextField("hvt.gruppe.nummer");
        tfPLZ = getSwingFactory().createTextField("hvt.gruppe.plz");
        tfOrt = getSwingFactory().createTextField("hvt.gruppe.ort");
        tfSwitch = getSwingFactory().createTextField("hvt.gruppe.switch");
        cbNiederlassung = getSwingFactory().createComboBox("hvt.gruppe.niederlassung");
        cbNiederlassung.setRenderer(new AKCustomListCellRenderer<>(Niederlassung.class, Niederlassung::getName));
        tfKostenstelle = getSwingFactory().createTextField("hvt.gruppe.kostenstelle");
        tfInnenauftrag = getSwingFactory().createTextField("hvt.gruppe.innenauftrag");
        tfTelefon = getSwingFactory().createTextField("hvt.gruppe.innenauftrag");

        cbMontag = getSwingFactory().createCheckBox("hvt.gruppe.montag");
        cbDienstag = getSwingFactory().createCheckBox("hvt.gruppe.dienstag");
        cbMittwoch = getSwingFactory().createCheckBox("hvt.gruppe.mittwoch");
        cbDonnerstag = getSwingFactory().createCheckBox("hvt.gruppe.donnerstag");
        cbFreitag = getSwingFactory().createCheckBox("hvt.gruppe.freitag");

        AKJPanel hvtPanel = new AKJPanel(new GridBagLayout());
        hvtPanel.add(lblOnkz, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        hvtPanel.add(tfOnkz, GBCFactory.createGBC(100, 0, 2, 0, 4, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(lblHvtName, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(tfHvtName, GBCFactory.createGBC(100, 0, 2, 1, 4, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(lblStrasse, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(tfStrasse, GBCFactory.createGBC(75, 0, 2, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(tfHNr, GBCFactory.createGBC(25, 0, 5, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(lblOrt, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(tfPLZ, GBCFactory.createGBC(25, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(tfOrt, GBCFactory.createGBC(75, 0, 3, 3, 3, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(lblSwitch, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(tfSwitch, GBCFactory.createGBC(100, 0, 2, 4, 4, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(lblNL, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(cbNiederlassung, GBCFactory.createGBC(100, 0, 2, 5, 4, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(lblKostenstelle, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(tfKostenstelle, GBCFactory.createGBC(100, 0, 2, 6, 4, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(lblInnenauftrag, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(tfInnenauftrag, GBCFactory.createGBC(100, 0, 2, 7, 4, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(lblTelefon, GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        hvtPanel.add(tfTelefon, GBCFactory.createGBC(100, 0, 2, 8, 4, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel tagePanel = new AKJPanel(new GridBagLayout());
        AKJPanel separator = getSwingFactory().createLinePanel(SwingConstants.HORIZONTAL);
        tagePanel.add(lblTage, GBCFactory.createGBC(0, 0, 0, 0, 3, 1, GridBagConstraints.HORIZONTAL));
        tagePanel.add(separator, GBCFactory.createGBC(100, 0, 0, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        tagePanel.add(cbMontag, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        tagePanel.add(cbDienstag, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        tagePanel.add(cbMittwoch, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        tagePanel.add(cbDonnerstag, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        tagePanel.add(cbFreitag, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        tagePanel.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 7, 3, 1, GridBagConstraints.VERTICAL));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().setPreferredSize(new Dimension(400, 250));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(hvtPanel, GBCFactory.createGBC(20, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tagePanel, GBCFactory.createGBC(20, 0, 3, 0, 1, 1, GridBagConstraints.BOTH));

        // Sperre GUI fürs Editieren
        enableGUI(false);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.hvtGruppe = (model instanceof HVTGruppe) ? (HVTGruppe) model : null;
        if (model != null) {
            tfOnkz.setText(hvtGruppe.getOnkz());
            tfHvtName.setText(hvtGruppe.getOrtsteil());
            tfStrasse.setText(hvtGruppe.getStrasse());
            tfHNr.setText(hvtGruppe.getHausNr());
            tfPLZ.setText(hvtGruppe.getPlz());
            tfOrt.setText(hvtGruppe.getOrt());
            tfSwitch.setText((hvtGruppe.getHwSwitch() != null) ? hvtGruppe.getHwSwitch().getName() : null);
            cbNiederlassung.selectItem("getId", Niederlassung.class, hvtGruppe.getNiederlassungId());
            tfKostenstelle.setText(hvtGruppe.getKostenstelle());
            tfInnenauftrag.setText(hvtGruppe.getInnenauftrag());
            tfTelefon.setText(hvtGruppe.getTelefon());

            cbMontag.setSelected(hvtGruppe.getMontag());
            cbDienstag.setSelected(hvtGruppe.getDienstag());
            cbMittwoch.setSelected(hvtGruppe.getMittwoch());
            cbDonnerstag.setSelected(hvtGruppe.getDonnerstag());
            cbFreitag.setSelected(hvtGruppe.getFreitag());

        }
        else {
            this.clear();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        try {
            if (hvtGruppe == null) { hvtGruppe = new HVTGruppe(); }

            hvtGruppe.setOnkz(tfOnkz.getText());
            hvtGruppe.setOrtsteil(tfHvtName.getText());
            hvtGruppe.setStrasse(tfStrasse.getText());
            hvtGruppe.setHausNr(tfHNr.getText());
            hvtGruppe.setPlz(tfPLZ.getText());
            hvtGruppe.setOrt(tfOrt.getText());
            HWSwitchService ccService = getCCService(HWSwitchService.class);
            hvtGruppe.setHwSwitch(ccService.findSwitchByName(tfSwitch.getText()));
            Object nl = cbNiederlassung.getSelectedItem();
            hvtGruppe.setNiederlassungId((nl instanceof Niederlassung) ? ((Niederlassung) nl).getId() : null);
            hvtGruppe.setKostenstelle(tfKostenstelle.getText(null));
            hvtGruppe.setInnenauftrag(tfInnenauftrag.getText(null));
            hvtGruppe.setTelefon(tfTelefon.getText());

            hvtGruppe.setMontag(cbMontag.isSelected());
            hvtGruppe.setDienstag(cbDienstag.isSelected());
            hvtGruppe.setMittwoch(cbMittwoch.isSelected());
            hvtGruppe.setDonnerstag(cbDonnerstag.isSelected());
            hvtGruppe.setFreitag(cbFreitag.isSelected());

            return hvtGruppe;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();

            NiederlassungService niederlassungService = getCCService(NiederlassungService.class);
            List<Niederlassung> niederlassungen = niederlassungService.findNiederlassungen();
            DefaultComboBoxModel cbModel = new DefaultComboBoxModel();
            cbNiederlassung.copyList2Model(niederlassungen, cbModel);
            cbNiederlassung.setModel(cbModel);
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
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        // not needed for this dialog
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        // not needed for this dialog
    }

    @Override
    protected void doSave() {
        // not needed for this dialog
    }

}
