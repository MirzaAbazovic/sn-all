/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.03.2005 09:16:51
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.EqVerwendung;
import de.augustakom.hurrican.model.cc.HVTBestellung;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.view.HVTBestellungView;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HVTToolService;


/**
 * Panel, um eine HVT-Bestellung zu bearbeiten bzw. neu anzulegen.
 *
 *
 */
public class HVTBestellungEditPanel extends AbstractDataPanel implements ChangeListener {

    private static final Logger LOGGER = Logger.getLogger(HVTBestellungEditPanel.class);

    // GUI-Elemente
    private AKJDateComponent dcAngebot = null;
    private AKJDateComponent dcBestelltAm = null;
    private AKJTextField tfBestNrAKom = null;
    private AKJTextField tfBestNrDTAG = null;
    private AKJComboBox cbPhysiktyp = null;
    private AKJFormattedTextField tfAnzahl = null;
    private AKJDateComponent dcBereitgestellt = null;
    private AKJDateComponent dcRealDTAG = null;
    private AKJButton btnSave = null;
    private AKJComboBox cbEqVerwendung = null;

    private AKJTabbedPane tabbedPane = null;
    private HVTBestellHistoryPanel panelHistory = null;
    private UEVTStiftVergabePanel panelStifte = null;

    // Modelle
    private HVTBestellungView model = null;
    private UEVT uevt = null;
    private HVTGruppe hvtGruppe = null;
    private HVTBestellung hvtBestellung = null;

    // Sonstiges
    private boolean editMode = true;

    public HVTBestellungEditPanel() {
        super("de/augustakom/hurrican/gui/hvt/resources/HVTBestellungEditPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblEqVerwendung = getSwingFactory().createLabel("eq.verwendung");
        AKJLabel lblAngebot = getSwingFactory().createLabel("angebot");
        AKJLabel lblBestelltAm = getSwingFactory().createLabel("bestelldatum");
        AKJLabel lblBestNrAKom = getSwingFactory().createLabel("bestellnr.akom");
        AKJLabel lblBestNrDTAG = getSwingFactory().createLabel("bestellnr.dtag");
        AKJLabel lblPhysiktyp = getSwingFactory().createLabel("physiktyp");
        AKJLabel lblAnzahl = getSwingFactory().createLabel("anzahl.cuda");
        AKJLabel lblBereitAm = getSwingFactory().createLabel("bereitgestellt");
        AKJLabel lblRealDTAG = getSwingFactory().createLabel("real.dtag");

        btnSave = getSwingFactory().createButton("save", getActionListener());
        AKJButton btnNew = getSwingFactory().createButton("new", getActionListener());
        dcAngebot = getSwingFactory().createDateComponent("angebot");
        dcBestelltAm = getSwingFactory().createDateComponent("bestelldatum");
        tfBestNrAKom = getSwingFactory().createTextField("bestellnr.akom");
        tfBestNrDTAG = getSwingFactory().createTextField("bestellnr.dtag");
        cbPhysiktyp = getSwingFactory().createComboBox("physiktyp");
        tfAnzahl = getSwingFactory().createFormattedTextField("anzahl.cuda");
        dcBereitgestellt = getSwingFactory().createDateComponent("bereitgestellt");
        dcRealDTAG = getSwingFactory().createDateComponent("real.dtag");
        cbEqVerwendung = getSwingFactory().createComboBox("eq.verwendung", new AKCustomListCellRenderer<>(EqVerwendung.class, EqVerwendung::toString));
        cbEqVerwendung.addItems(Arrays.asList(EqVerwendung.values()), true);

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(lblAngebot, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        left.add(dcAngebot, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBestelltAm, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcBestelltAm, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBestNrAKom, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBestNrAKom, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBestNrDTAG, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBestNrDTAG, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblPhysiktyp, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbPhysiktyp, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAnzahl, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfAnzahl, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBereitAm, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcBereitgestellt, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblEqVerwendung, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbEqVerwendung, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblRealDTAG, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        right.add(dcRealDTAG, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        right.add(btnSave, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.NONE));

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("bestellung")));
        top.add(left, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        top.add(right, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnNew, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE, new Insets(8, 2, 2, 2)));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 1, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel north = new AKJPanel(new GridBagLayout());
        north.add(top, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        north.add(btnPnl, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.VERTICAL));
        north.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        panelHistory = new HVTBestellHistoryPanel();
        panelStifte = new UEVTStiftVergabePanel();

        tabbedPane = new AKJTabbedPane();
        tabbedPane.addChangeListener(this);
        tabbedPane.addTab("Stiftverteilung", panelHistory);
        tabbedPane.setToolTipTextAt(0, "Zeigt die Stift-Verteilung der aktuellen HVT-Bestellung an");
        tabbedPane.addTab("Belegung", panelStifte);
        tabbedPane.setToolTipTextAt(1, "Zeigt die Details des aktuellen UEVTs an");

        this.setLayout(new BorderLayout());
        this.add(north, BorderLayout.NORTH);
        this.add(tabbedPane, BorderLayout.CENTER);

        manageGUI(btnNew, btnSave);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) {
        this.model = null;
        if (model instanceof HVTBestellungView) {
            this.model = (HVTBestellungView) model;
        }

        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    public void readModel() {
        clear();
        if ((this.model == null) || (this.model.getUevtId() == null)) {
            MessageHelper.showErrorDialog(getMainFrame(),
                    new Exception("Daten konnten nicht geladen werden, da ein ungültiges Modell angegeben wurde!"));
            return;
        }

        try {
            editMode = (model.getHVTBestellId() != null) ? true : false;
            validate4Mode();

            HVTService hs = getCCService(HVTService.class);
            uevt = hs.findUEVT(model.getUevtId());
            if (uevt == null) {
                throw new HurricanGUIException("UEVT konnte nicht ermittelt werden!");
            }
            hvtGruppe = hs.findHVTGruppe4Standort(uevt.getHvtIdStandort());
            if (hvtGruppe == null) {
                throw new HurricanGUIException("HVT konnte nicht ermittelt werden!");
            }

            HVTToolService hts = getCCService(HVTToolService.class);
            hvtBestellung = hts.findHVTBestellung(model.getHVTBestellId());
            if (hvtBestellung == null) {
                hvtBestellung = new HVTBestellung();
                hvtBestellung.setUevtId(uevt.getId());
            }

            panelHistory.setModel(hvtBestellung);
            showValues();
            btnSave.setEnabled(true);
        }
        catch (Exception e) {
            btnSave.setEnabled(false);
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            changeTitle();
            setDefaultCursor();
        }
    }

    /* Zeigt die Daten an. */
    private void showValues() {
        if (hvtBestellung != null) {
            dcAngebot.setDate(hvtBestellung.getAngebotDatum());
            dcBestelltAm.setDate(hvtBestellung.getBestelldatum());
            tfBestNrAKom.setText(hvtBestellung.getBestellNrAKom());
            tfBestNrDTAG.setText(hvtBestellung.getBestellNrDTAG());
            cbPhysiktyp.selectItem("toString", String.class, hvtBestellung.getPhysiktyp());
            tfAnzahl.setValue(hvtBestellung.getAnzahlCuDA());
            dcBereitgestellt.setDate(hvtBestellung.getBereitgestellt());
            dcRealDTAG.setDate(hvtBestellung.getRealDTAGBis());
            cbEqVerwendung.setSelectedItem(hvtBestellung.getEqVerwendung());
        }
    }

    /* Speichert die angezeigten HVTBestellungs-Daten in dem Modell ab. */
    private void setValues() {
        if (hvtBestellung == null) {
            hvtBestellung = new HVTBestellung();
        }

        hvtBestellung.setAngebotDatum(dcAngebot.getDate(null));
        hvtBestellung.setBestelldatum(dcBestelltAm.getDate(null));
        hvtBestellung.setBestellNrAKom(tfBestNrAKom.getText(null));
        hvtBestellung.setBestellNrDTAG(tfBestNrDTAG.getText(null));
        hvtBestellung.setPhysiktyp((cbPhysiktyp.getSelectedItem() != null)
                ? cbPhysiktyp.getSelectedItem().toString() : null);
        hvtBestellung.setAnzahlCuDA(tfAnzahl.getValueAsInt(null));
        hvtBestellung.setBereitgestellt(dcBereitgestellt.getDate(null));
        hvtBestellung.setRealDTAGBis(dcRealDTAG.getDate(null));
        hvtBestellung.setEqVerwendung((EqVerwendung) cbEqVerwendung.getSelectedItem());
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    public void saveModel() {
        try {
            setWaitCursor();
            setValues();
            HVTToolService hts = getCCService(HVTToolService.class);
            hts.saveHVTBestellung(hvtBestellung);

            editMode = true;
            validate4Mode();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("save".equals(command)) {
            saveModel();
        }
        else if ("new".equals(command)) {
            hvtBestellung = new HVTBestellung();
            hvtBestellung.setUevtId(uevt.getId());
            UEVT uevtBackup = uevt;
            clear();
            uevt = uevtBackup;
            editMode = false;
            validate4Mode();
        }
    }

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == tabbedPane) {
            Component comp = tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
            if (comp instanceof AKModelOwner) {
                try {
                    ((AKModelOwner) comp).setModel(hvtBestellung);
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    MessageHelper.showErrorDialog(this, ex);
                }
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /* Aendert den Text des Frame-Titels. */
    private void changeTitle() {
        if ((hvtGruppe != null) && (uevt != null)) {
            StringBuilder title = new StringBuilder(getFrameTitle());
            title.append("  (");
            title.append(hvtGruppe.getOrtsteil());
            title.append("  -  UEVT: ");
            title.append(uevt.getUevt());
            title.append(")");
            setFrameTitle(title.toString());
        }
        else {
            setFrameTitle("error");
        }
    }

    /*
     * Validiert die Felder abhaengig davon, ob es sich um eine
     * Neuanlage oder eine Aenderung der HVT-Bestellung handelt.
     */
    private void validate4Mode() {
        if (editMode) {
            GuiTools.disableComponents(new Component[] { dcAngebot, cbPhysiktyp, tfAnzahl });
            GuiTools.enableComponents(new Component[] { dcBestelltAm, tfBestNrAKom, tfBestNrDTAG,
                    dcBereitgestellt, dcRealDTAG });
            tabbedPane.setEnabledAt(0, true);
            tabbedPane.setEnabledAt(1, true);
        }
        else {
            GuiTools.disableComponents(new Component[] { dcRealDTAG });
            GuiTools.enableComponents(new Component[] { dcAngebot, cbPhysiktyp, tfAnzahl,
                    dcBestelltAm, tfBestNrAKom, tfBestNrDTAG, dcBereitgestellt });
            tabbedPane.setEnabledAt(0, true);
            tabbedPane.setEnabledAt(1, false);
        }
    }

    /* 'Loescht' alle Felder. */
    private void clear() {
        uevt = null;
        hvtGruppe = null;
        GuiTools.cleanFields(this);

        tabbedPane.removeChangeListener(this);
        tabbedPane.setSelectedIndex(0);
        tabbedPane.addChangeListener(this);

        panelHistory.setModel(null);
        panelStifte.setModel(null);
    }
}


