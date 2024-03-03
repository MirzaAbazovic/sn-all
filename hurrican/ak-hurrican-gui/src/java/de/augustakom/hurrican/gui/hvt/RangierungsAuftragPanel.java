/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2007 17:10:32
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.text.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKFilterTableModelListener;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.reports.jasper.AKJasperViewer;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.RangierungAdminService;


/**
 * Panel zur Darstellung der offenen Rangierungs-Auftraege.
 *
 *
 */
public class RangierungsAuftragPanel extends AbstractAdminPanel implements AKFilterTableModelListener {

    private static final Logger LOGGER = Logger.getLogger(RangierungsAuftragPanel.class);

    // GUI-Komponenten
    private AKJTable tbRAs = null;
    private AKReferenceAwareTableModel<RangierungsAuftrag> tbMdlRAs = null;
    private AKReferenceField rfHVT = null;
    private AKJFormattedTextField tfAnzahl = null;
    private AKJComboBox cbPTParent = null;
    private AKJComboBox cbPTChild = null;
    private AKJTextField tfAuftragVon = null;
    private AKJDateComponent dcAuftragAm = null;
    private AKJDateComponent dcFaelligAm = null;
    private AKJTextField tfDefiniertVon = null;
    private AKJDateComponent dcDefiniertAm = null;
    private AKJTextField tfAusgefuehrtVon = null;
    private AKJDateComponent dcAusgefuehrtAm = null;

    // Modelle
    private RangierungsAuftrag actModel = null;

    /**
     * Default-Const.
     */
    public RangierungsAuftragPanel() {
        super("de/augustakom/hurrican/gui/hvt/resources/RangierungsAuftragPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJButton btnDoDefinition = getSwingFactory().createButton("do.definition", getActionListener());
        AKJButton btnPrintDef = getSwingFactory().createButton("print.definitions", getActionListener());
        AKJButton btnShowBudget = getSwingFactory().createButton("show.budget", getActionListener());
        AKJButton btnReleaseDef = getSwingFactory().createButton("release.rangierungen", getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton("cancel", getActionListener());
        AKJButton btnWorkOn = getSwingFactory().createButton("work.on", getActionListener());

        AKJLabel lblHVT = getSwingFactory().createLabel("hvt.standort");
        AKJLabel lblAnzahl = getSwingFactory().createLabel("anzahl.ports");
        AKJLabel lblPTParent = getSwingFactory().createLabel("physiktyp.parent");
        AKJLabel lblPTChild = getSwingFactory().createLabel("physiktyp.child");
        AKJLabel lblAuftragVon = getSwingFactory().createLabel("auftrag.von");
        AKJLabel lblAuftragAm = getSwingFactory().createLabel("auftrag.am");
        AKJLabel lblFaelligAm = getSwingFactory().createLabel("faellig.am");
        AKJLabel lblDefiniertVon = getSwingFactory().createLabel("definiert.von");
        AKJLabel lblDefiniertAm = getSwingFactory().createLabel("definiert.am");
        AKJLabel lblAusgefuehrtVon = getSwingFactory().createLabel("ausgefuehrt.von");
        AKJLabel lblAusgefuehrtAm = getSwingFactory().createLabel("ausgefuehrt.am");

        AKCustomListCellRenderer crPT = new AKCustomListCellRenderer<>(PhysikTyp.class, PhysikTyp::getName);
        rfHVT = getSwingFactory().createReferenceField("hvt.standort");
        tfAnzahl = getSwingFactory().createFormattedTextField("anzahl.ports");
        cbPTParent = getSwingFactory().createComboBox("physiktyp.parent", crPT);
        cbPTChild = getSwingFactory().createComboBox("physiktyp.child", crPT);
        tfAuftragVon = getSwingFactory().createTextField("auftrag.von", false);
        dcAuftragAm = getSwingFactory().createDateComponent("auftrag.am", false);
        dcFaelligAm = getSwingFactory().createDateComponent("faellig.am");
        tfDefiniertVon = getSwingFactory().createTextField("definiert.von", false);
        dcDefiniertAm = getSwingFactory().createDateComponent("definiert.am", false);
        tfAusgefuehrtVon = getSwingFactory().createTextField("ausgefuehrt.von", false);
        dcAusgefuehrtAm = getSwingFactory().createDateComponent("ausgefuehrt.am", false);

        tbMdlRAs = new AKReferenceAwareTableModel<RangierungsAuftrag>(
                new String[] { "ID", "HVT", "Anzahl", "PT Parent", "PT Child", "von",
                        "am", "faellig", "definiert von", "definiert am", "Bearbeiter",
                        "ausgefuehrt von", "ausgefuehrt am" },
                new String[] { "id", "hvtStandortId", "anzahlPorts", "physiktypParent", "physiktypChild", "auftragVon",
                        "auftragAm", "faelligAm", "definiertVon", "definiertAm", "technikBearbeiter",
                        "ausgefuehrtVon", "ausgefuehrtAm" },
                new Class[] { Long.class, String.class, Integer.class, String.class, String.class, String.class,
                        Date.class, Date.class, String.class, Date.class, String.class,
                        String.class, Date.class }
        );
        tbRAs = new AKJTable(tbMdlRAs, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbRAs.attachSorter();
        tbRAs.addTableListener(this);
        tbRAs.fitTable(new int[] { 30, 170, 50, 85, 85, 85, 75, 75, 85, 75, 80, 85, 75 });
        AKJScrollPane spTable = new AKJScrollPane(tbRAs, new Dimension(500, 250));

        AKJPanel tablePanel = new AKJPanel(new BorderLayout());
        tablePanel.add(spTable, BorderLayout.CENTER);

        AKJPanel fctPnl = new AKJPanel(new GridBagLayout(), "Funktionen");
        fctPnl.add(btnDoDefinition, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(btnPrintDef, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(btnShowBudget, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(btnWorkOn, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(btnReleaseDef, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 5, 1, 1, GridBagConstraints.VERTICAL));
        fctPnl.add(btnCancel, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel leftPnl = new AKJPanel(new GridBagLayout());
        leftPnl.add(lblHVT, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        leftPnl.add(rfHVT, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblAnzahl, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(tfAnzahl, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblPTParent, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(cbPTParent, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblPTChild, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(cbPTChild, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblAuftragVon, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(tfAuftragVon, GBCFactory.createGBC(0, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblAuftragAm, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(dcAuftragAm, GBCFactory.createGBC(0, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblFaelligAm, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(dcFaelligAm, GBCFactory.createGBC(0, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 2, 7, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel rightPnl = new AKJPanel(new GridBagLayout());
        rightPnl.add(lblDefiniertVon, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        rightPnl.add(tfDefiniertVon, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(lblDefiniertAm, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(dcDefiniertAm, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(lblAusgefuehrtVon, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(tfAusgefuehrtVon, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(lblAusgefuehrtAm, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(dcAusgefuehrtAm, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 2, 4, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel detailPnl = new AKJPanel(new GridBagLayout(), "Details");
        detailPnl.add(leftPnl, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        detailPnl.add(rightPnl, GBCFactory.createGBC(0, 100, 1, 0, 1, 1, GridBagConstraints.BOTH, 30));
        detailPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel subPnl = new AKJPanel(new GridBagLayout());
        subPnl.add(fctPnl, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        subPnl.add(detailPnl, GBCFactory.createGBC(100, 100, 1, 0, 1, 1, GridBagConstraints.BOTH));

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(tablePanel);
        split.setBottomComponent(subPnl);
        split.setDividerSize(2);
        split.setResizeWeight(1d);  // Top-Panel erhaelt komplette Ausdehnung!

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);

        manageGUI(btnCancel, btnDoDefinition,
                btnPrintDef, btnReleaseDef, btnShowBudget, btnWorkOn);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            GuiTools.cleanFields(this);

            // Default-Daten laden
            HVTService hvts = getCCService(HVTService.class);
            List<HVTGruppeStdView> hvtViews = hvts.findHVTViews();
            tbMdlRAs.addReference(1,
                    CollectionMapConverter.convert2Map(hvtViews, "getHvtIdStandort", null), "name");

            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfHVT.setFindService(sfs);
            rfHVT.setReferenceList(hvtViews);

            PhysikService ps = getCCService(PhysikService.class);
            List<PhysikTyp> pts = ps.findPhysikTypen();
            Map<Long, PhysikTyp> ptMap = CollectionMapConverter.convert2Map(pts, "getId", null);
            tbMdlRAs.addReference(3, ptMap, "name");
            tbMdlRAs.addReference(4, ptMap, "name");
            cbPTParent.removeAllItems();
            cbPTParent.addItems(pts, true, PhysikTyp.class);
            cbPTChild.removeAllItems();
            cbPTChild.addItems(pts, true, PhysikTyp.class);

            // offene Rangierungsauftraege laden
            RangierungAdminService ras = getCCService(RangierungAdminService.class);
            List<RangierungsAuftrag> unfinished = ras.findUnfinishedRAs();
            tbMdlRAs.setData(unfinished);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        this.actModel = new RangierungsAuftrag();
        GuiTools.cleanFields(this);
        showDetails(actModel);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        try {
            if (actModel == null) {
                actModel = new RangierungsAuftrag();
            }

            boolean isNew = false;
            if (actModel.getId() == null) {
                isNew = true;
                AKUser user = HurricanSystemRegistry.instance().getCurrentUser();
                tfAuftragVon.setText((user != null) ? user.getNameAndFirstName() : HurricanConstants.UNKNOWN);
                dcAuftragAm.setDate(new Date());
            }

            actModel.setHvtStandortId(rfHVT.getReferenceIdAs(Long.class));
            actModel.setAnzahlPorts(tfAnzahl.getValueAsInt(null));
            actModel.setPhysiktypParent((Long) cbPTParent.getSelectedItemValue("getId", Long.class));
            actModel.setPhysiktypChild((Long) cbPTChild.getSelectedItemValue("getId", Long.class));
            actModel.setAuftragVon(tfAuftragVon.getText(null));
            actModel.setAuftragAm(dcAuftragAm.getDate(null));
            actModel.setFaelligAm(dcFaelligAm.getDate(null));

            // Speichern
            RangierungAdminService ras = getCCService(RangierungAdminService.class);
            ras.saveRangierungsAuftrag(actModel);

            // Erneuere Tabelle
            if (isNew) {
                tbMdlRAs.addObject(actModel);
            }
            tbMdlRAs.fireTableDataChanged();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
            this.actModel = null;
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof RangierungsAuftrag) {
            this.actModel = (RangierungsAuftrag) details;

            rfHVT.setReferenceId(actModel.getHvtStandortId());
            tfAnzahl.setValue(actModel.getAnzahlPorts());
            cbPTParent.selectItem("getId", PhysikTyp.class, actModel.getPhysiktypParent());
            cbPTChild.selectItem("getId", PhysikTyp.class, actModel.getPhysiktypChild());
            tfAuftragVon.setText(actModel.getAuftragVon());
            dcAuftragAm.setDate(actModel.getAuftragAm());
            dcFaelligAm.setDate(actModel.getFaelligAm());
            tfDefiniertVon.setText(actModel.getDefiniertVon());
            dcDefiniertAm.setDate(actModel.getDefiniertAm());
            tfAusgefuehrtVon.setText(actModel.getAusgefuehrtVon());
            dcAusgefuehrtAm.setDate(actModel.getAusgefuehrtAm());

            Component[] comps = new Component[] { rfHVT, tfAnzahl, cbPTParent, cbPTChild, dcFaelligAm };
            if (actModel.getId() != null) {
                // bei bestehenden Rangierungsauftraegen Daten nicht mehr veraenderbar!
                GuiTools.disableComponents(comps);
            }
            else {
                GuiTools.enableComponents(comps);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        try {
            if ((actModel == null) || (actModel.getId() == null)) {
                throw new HurricanGUIException("Es muss zuerst ein Rangierungs-Auftrag ausgewaehlt sein!");
            }

            if ("do.definition".equals(command)) {
                doDefinition();
            }
            else if ("print.definitions".equals(command)) {
                printRA();
            }
            else if ("show.budget".equals(command)) {
                showBudget();
            }
            else if ("release.rangierungen".equals(command)) {
                releaseRangierungen();
            }
            else if ("work.on".equals(command)) {
                workOn();
            }
            else if ("cancel".equals(command)) {
                cancelRA();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Oeffnet das Frame fuer die Rangierungs-Definition. */
    private void doDefinition() throws HurricanGUIException {
        try {
            setWaitCursor();
            showProgressBar("Equipments laden...");

            RangierungDefinitionFrame rdf = new RangierungDefinitionFrame(actModel);
            getMainFrame().registerFrame(rdf, false);
        }
        catch (Exception e) {
            throw new HurricanGUIException(
                    "Frame fuer die Rangierungs-Definition konnte nicht erzeugt werden. Grund:\n" + e.getMessage(), e);
        }
        finally {
            stopProgressBar();
            setDefaultCursor();
        }
    }

    /* Erstellt den Report mit der Rangierungsliste. */
    private void printRA() throws HurricanGUIException {
        try {
            setWaitCursor();
            RangierungAdminService ras = getCCService(RangierungAdminService.class);
            JasperPrint jp = ras.printRangierungsliste(actModel.getId());
            AKJasperViewer.viewReport(jp);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException("Fehler bei der Freigabe des Auftrags: " + e.getMessage(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /* Zeigt die Budgets zu dem Rangierungs-Auftrag an. */
    private void showBudget() {
        RangierungBudgetFrame frame = new RangierungBudgetFrame(actModel);
        getMainFrame().registerFrame(frame, false);
    }

    /* Setzt die zu dem Auftrag definierten Rangierungen auf 'freigegeben' */
    private void releaseRangierungen() throws HurricanGUIException {
        try {
            String input = MessageHelper.showInputDialog(getMainFrame(),
                    "Benötigte Arbeitszeit in Std. (z.B. 2,5):", "Arbeitszeit", JOptionPane.PLAIN_MESSAGE);
            Float stunden = null;
            if (StringUtils.isNotBlank(input)) {
                NumberFormat nf = NumberFormat.getNumberInstance();
                stunden = new Float(nf.parse(input).floatValue());
            }

            int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                    "Sollen die Rangierungen wirklich freigeben werden?", "Rangierungen freigeben?");
            if (option == JOptionPane.YES_OPTION) {
                setWaitCursor();
                RangierungAdminService ras = getCCService(RangierungAdminService.class);
                RangierungsAuftrag ra = ras.releaseRangierungen4RA(
                        actModel.getId(), stunden, HurricanSystemRegistry.instance().getSessionId());

                PropertyUtils.copyProperties(actModel, ra);
                actModel.notifyObservers(true);
                tbRAs.repaint();

                showDetails(actModel);

                MessageHelper.showInfoDialog(getMainFrame(),
                        "Die Rangierungen wurden freigegeben.", null, true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException("Fehler bei der Freigabe des Auftrags: " + e.getMessage(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /* Setzt den Auftrag auf 'in Bearbeitung' (mit Angabe des Bearbeiters). */
    private void workOn() throws HurricanGUIException {
        try {
            String bearbeiter = MessageHelper.showInputDialog(getMainFrame(), "Bearbeiter:");
            if (StringUtils.isNotBlank(bearbeiter)) {
                RangierungAdminService ras = getCCService(RangierungAdminService.class);

                actModel.setTechnikBearbeiter(bearbeiter);
                ras.saveRangierungsAuftrag(actModel);
                tbRAs.repaint();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException("Fehler beim Modifizieren des Auftrags: " + e.getMessage(), e);
        }
    }

    /* Storniert den aktuellen Rangierungs-Auftrag. */
    private void cancelRA() throws HurricanGUIException {
        try {
            RangierungAdminService ras = getCCService(RangierungAdminService.class);
            ras.cancelRA(actModel.getId(), HurricanSystemRegistry.instance().getSessionId());

            MessageHelper.showInfoDialog(getMainFrame(),
                    "Der Rangierungsauftrag wurde storniert. Ansicht bitte aktualisieren.", null, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException("Fehler beim Stornieren des Auftrags: " + e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKFilterTableModelListener#tableFiltered()
     */
    @Override
    public void tableFiltered() {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


