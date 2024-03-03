/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.01.2007 14:13:21
 */
package de.augustakom.hurrican.gui.auftrag.innenauftrag;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKDateSelectionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.SimpleMailDialog;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.CurrencyTableCellRenderer;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.mail.IHurricanMailSender;
import de.augustakom.common.tools.reports.jasper.AKJasperExportTypes;
import de.augustakom.common.tools.reports.jasper.AKJasperExporter;
import de.augustakom.common.tools.reports.jasper.AKJasperViewer;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AbstractAuftragPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.Lager;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.innenauftrag.IA;
import de.augustakom.hurrican.model.cc.innenauftrag.IABudget;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahme;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahmeArtikel;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CounterService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungAdminService;
import de.augustakom.hurrican.service.cc.RegistryService;

/**
 * Panel fuer die Anzeige und Verwaltung von Innenauftraegen (inkl. Budgets, Materialentnahmen).
 *
 *
 */
public class InnenauftragPanel extends AbstractAuftragPanel implements AKTableOwner {

    private static final long serialVersionUID = 9003642852201971873L;
    private static final Logger LOGGER = Logger.getLogger(InnenauftragPanel.class);
    public static final String IA_PROJEKTBEZ = "ia.projektbez";

    // GUI-Komponenten
    private AKJTextField tfIANummer = null;
    private AKJTextField tfProjectLead = null;
    private AKJTextField tfKostenstelle = null;
    private AKJButton btnAddMatEnt = null;
    private AKJButton btnAddArtikel = null;
    private AKJButton btnRemArtikel = null;
    private AKJButton btnMailBudget = null;
    private AKJTable tbBudgets = null;
    private AKReflectionTableModel<IABudget> tbMdlBudgets = null;
    private AKJTable tbMatEntnahmen = null;
    private AKReferenceAwareTableModel<IAMaterialEntnahme> tbMdlMatEntnahmen = null;
    private AKJTable tbArtikel = null;
    private AKReflectionTableModel<IAMaterialEntnahmeArtikel> tbMdlArtikel = null;
    private AKJTextField tfProjektBez;

    // Modelle
    private CCAuftragModel auftragModel = null;
    private RangierungsAuftrag rangierungsAuftrag = null;
    private IA actIA = null;
    private IABudget actBudget = null;
    private IAMaterialEntnahme actMatEnt = null;
    private List<Lager> lager = null;

    // sonstiges
    private boolean defaultLoaded = false;

    /**
     * Default-Const.
     */
    public InnenauftragPanel() {
        super("de/augustakom/hurrican/gui/auftrag/innenauftrag/resources/InnenauftragPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblIANummer = getSwingFactory().createLabel("ia.nummer");
        AKJLabel lblProjectLead = getSwingFactory().createLabel("ia.projectlead");
        AKJLabel lblKostenstelle = getSwingFactory().createLabel("ia.kostenstelle");
        AKJLabel lblAssigned = getSwingFactory().createLabel("assigned.articles");
        tfIANummer = getSwingFactory().createTextField("ia.nummer", false);
        tfProjectLead = getSwingFactory().createTextField("ia.projectlead", false);
        tfKostenstelle = getSwingFactory().createTextField("ia.kostenstelle");

        AKJButton btnAddBudget = getSwingFactory().createButton("add.budget", getActionListener(), null);
        AKJButton btnPrintBudget = getSwingFactory().createButton("print.budget", getActionListener(), null);
        btnMailBudget = getSwingFactory().createButton("mail.budget", getActionListener(), null);
        AKJButton btnCloseBudget = getSwingFactory().createButton("close.budget", getActionListener());
        AKJButton btnStornoBudget = getSwingFactory().createButton("cancel.budget", getActionListener());
        btnAddMatEnt = getSwingFactory().createButton("add.material.withdrawl", getActionListener(), null);
        AKJButton btnPrintMatEnt = getSwingFactory().createButton("print.material.withdrawl", getActionListener(), null);
        btnAddArtikel = getSwingFactory().createButton("add.article", getActionListener(), null);
        btnRemArtikel = getSwingFactory().createButton("remove.article", getActionListener(), null);
        AKJButton btnImportArtikel = getSwingFactory().createButton("import.article", getActionListener(), null);

        tbMdlBudgets = new AKReflectionTableModel<>(
                new String[] { "erstellt am", "erstellt von", "Budget", "Ebene 1", "Ebene 3", "Ebene 5",
                        "geschlossen am", "gepl. Projektabschluss", "storno" },
                new String[] { IABudget.CREATED_AT, IABudget.PROJEKTLEITER, IABudget.BUDGET,
                        IABudget.IA_LEVEL_1, IABudget.IA_LEVEL_3, IABudget.IA_LEVEL_5,
                        IABudget.CLOSED_AT, IABudget.PLANNED_FINISH_DATE, IABudget.CANCELLED },
                new Class[] { Date.class, String.class, Float.class, String.class, String.class, String.class,
                        Date.class, Date.class, Boolean.class }
        );
        tbBudgets = new AKJTable(tbMdlBudgets, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION, false);
        tbBudgets.addTableListener(this);
        tbBudgets.fitTable(new int[] { 100, 150, 90, 150, 125, 100, 100, 120, 70 });
        tbBudgets.addPopupAction(new DefinePlannedFinishDateAction());
        TableColumn col = tbBudgets.getColumnModel().getColumn(2);
        col.setCellRenderer(new CurrencyTableCellRenderer());
        AKJScrollPane spBudgets = new AKJScrollPane(tbBudgets, new Dimension(400, 150));

        tbMdlMatEntnahmen = new AKReferenceAwareTableModel<>(
                new String[] { "Typ", "Lager", "Standort", "erstellt am", "von" },
                new String[] { IAMaterialEntnahme.ENTNAHME_TYP_TEXT, IAMaterialEntnahme.LAGER_ID, IAMaterialEntnahme.HVT_ID_STANDORT,
                        IAMaterialEntnahme.CREATED_AT, IAMaterialEntnahme.CREATED_FROM },
                new Class[] { String.class, String.class, String.class,
                        Date.class, String.class }
        );
        tbMatEntnahmen = new AKJTable(tbMdlMatEntnahmen,
                AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION, false);
        tbMatEntnahmen.addTableListener(this);
        tbMatEntnahmen.fitTable(new int[] { 80, 100, 80, 80, 80 });
        AKJScrollPane spMatEntnahmen = new AKJScrollPane(tbMatEntnahmen, new Dimension(250, 200));

        tbMdlArtikel = new AKReflectionTableModel<IAMaterialEntnahmeArtikel>(
                new String[] { "Artikel", "Mat-Nr", "Anzahl",
                        "Einzelpreis", "Anlagen-Bez." },
                new String[] { IAMaterialEntnahmeArtikel.ARTIKEL, IAMaterialEntnahmeArtikel.MATERIAL_NR, IAMaterialEntnahmeArtikel.ANZAHL,
                        IAMaterialEntnahmeArtikel.EINZELPREIS, IAMaterialEntnahmeArtikel.ANLAGEN_BEZEICHNUNG },
                new Class[] { String.class, String.class, Float.class,
                        Float.class, String.class }
        );
        tbArtikel = new AKJTable(tbMdlArtikel, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION, false);
        tbArtikel.fitTable(new int[] { 120, 100, 60, 90, 80 });
        col = tbArtikel.getColumnModel().getColumn(3);
        col.setCellRenderer(new CurrencyTableCellRenderer());
        AKJScrollPane spArtikel = new AKJScrollPane(tbArtikel, new Dimension(300, 200));

        final AKJLabel lblProjektBez = getSwingFactory().createLabel(IA_PROJEKTBEZ);
        tfProjektBez = getSwingFactory().createTextField(IA_PROJEKTBEZ);

        // @formatter:off
        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(lblIANummer,     GBCFactory.createGBC(  0, 0,  0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(),  GBCFactory.createGBC(  0, 0,  1, 0, 1, 1, GridBagConstraints.NONE));
        top.add(tfIANummer,      GBCFactory.createGBC(  0, 0,  2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(),  GBCFactory.createGBC(  0, 0,  3, 0, 1, 1, GridBagConstraints.NONE));
        top.add(lblProjectLead,  GBCFactory.createGBC(  0, 0,  4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(),  GBCFactory.createGBC(  0, 0,  5, 0, 1, 1, GridBagConstraints.NONE));
        top.add(tfProjectLead,   GBCFactory.createGBC(  0, 0,  6, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblKostenstelle, GBCFactory.createGBC(  0, 0,  7, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(),  GBCFactory.createGBC(  0, 0,  8, 0, 1, 1, GridBagConstraints.NONE));
        top.add(tfKostenstelle,  GBCFactory.createGBC(  0, 0,  9, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        top.add(lblProjektBez,   GBCFactory.createGBC(  0, 0, 10, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(),  GBCFactory.createGBC(  0, 0, 11, 0, 1, 1, GridBagConstraints.NONE));
        top.add(tfProjektBez,    GBCFactory.createGBC(  0, 0, 12, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        top.add(new AKJPanel(),  GBCFactory.createGBC(  0, 0, 13, 0, 1, 1, GridBagConstraints.NONE));
        top.add(new AKJPanel(),  GBCFactory.createGBC(100, 0, 14, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel budPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("budgets"));
        budPnl.add(btnAddBudget,    GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.NONE));
        budPnl.add(btnPrintBudget,  GBCFactory.createGBC(  0,   0, 0, 1, 1, 1, GridBagConstraints.NONE));
        budPnl.add(btnMailBudget,   GBCFactory.createGBC(  0,   0, 0, 2, 1, 1, GridBagConstraints.NONE));
        budPnl.add(new AKJPanel(),  GBCFactory.createGBC(  0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));
        budPnl.add(spBudgets,       GBCFactory.createGBC(100,   0, 1, 0, 3, 4, GridBagConstraints.HORIZONTAL));
        budPnl.add(btnCloseBudget,  GBCFactory.createGBC(  0,   0, 1, 4, 1, 1, GridBagConstraints.NONE));
        budPnl.add(btnStornoBudget, GBCFactory.createGBC(  0,   0, 2, 4, 1, 1, GridBagConstraints.NONE, 20));
        budPnl.add(new AKJPanel(),  GBCFactory.createGBC(100,   0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel artPnl = new AKJPanel(new GridBagLayout());
        artPnl.add(lblAssigned,      GBCFactory.createGBC(  0,   0, 0, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        artPnl.add(btnAddArtikel,    GBCFactory.createGBC(  0,   0, 0, 1, 1, 1, GridBagConstraints.NONE));
        artPnl.add(btnRemArtikel,    GBCFactory.createGBC(  0,   0, 0, 2, 1, 1, GridBagConstraints.NONE));
        artPnl.add(new AKJPanel(),   GBCFactory.createGBC(  0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));
        artPnl.add(btnImportArtikel, GBCFactory.createGBC(  0,   0, 0, 4, 1, 1, GridBagConstraints.NONE));
        artPnl.add(spArtikel,        GBCFactory.createGBC(100, 100, 1, 1, 1, 4, GridBagConstraints.BOTH));

        AKJPanel matPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("material.withdrawls"));
        matPnl.add(btnAddMatEnt,   GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.NONE));
        matPnl.add(btnPrintMatEnt, GBCFactory.createGBC(  0,   0, 0, 1, 1, 1, GridBagConstraints.NONE));
        matPnl.add(new AKJPanel(), GBCFactory.createGBC(  0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        matPnl.add(spMatEntnahmen, GBCFactory.createGBC(100, 100, 1, 0, 1, 3, GridBagConstraints.BOTH));
        matPnl.add(artPnl,         GBCFactory.createGBC(100, 100, 2, 0, 1, 3, GridBagConstraints.BOTH, 30));

        this.setLayout(new GridBagLayout());
        this.add(top,            GBCFactory.createGBC(100,   0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(budPnl,         GBCFactory.createGBC(100,   0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(matPnl,         GBCFactory.createGBC(100,   0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(  0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        manageGUI(btnAddBudget, btnPrintBudget, btnCloseBudget, btnStornoBudget,
                btnAddMatEnt, btnPrintMatEnt, btnAddArtikel, btnRemArtikel, btnImportArtikel);
        enableAddRemoveButtons(false);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) throws AKGUIException {
        firstLoad();
        this.auftragModel = null;
        this.rangierungsAuftrag = null;
        if (model instanceof CCAuftragModel) {
            this.auftragModel = (CCAuftragModel) model;
        }
        else if (model instanceof RangierungsAuftrag) {
            rangierungsAuftrag = (RangierungsAuftrag) model;
            tfKostenstelle.setEnabled(false);
            btnMailBudget.setEnabled(false);
        }
        readModel();
    }

    /* Default-Daten laden - nur einmal! */
    private void firstLoad() {
        if (!defaultLoaded) {
            try {
                // Lager-Objekte laden und dem TableModel als Reference uebergeben
                InnenauftragService ias = getCCService(InnenauftragService.class);
                lager = ias.findLager();
                Map<Long, Lager> lagerMap = new HashMap<Long, Lager>();
                CollectionMapConverter.convert2Map(lager, lagerMap, "getId", null);
                tbMdlMatEntnahmen.addReference(1, lagerMap, "name");

                // HVTs laden und dem TableModel als Reference uebergeben
                HVTService hvts = getCCService(HVTService.class);
                List<HVTGruppeStdView> hvtViews = hvts.findHVTViews();
                tbMdlMatEntnahmen.addReference(2,
                        CollectionMapConverter.convert2Map(hvtViews, "getHvtIdStandort", null), "name");

                defaultLoaded = true;
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() throws AKGUIException {
        cleanModels();
        GuiTools.cleanFields(this);
        try {
            setWaitCursor();
            InnenauftragService ias = getCCService(InnenauftragService.class);

            if (auftragModel != null) {
                actIA = ias.findIA4Auftrag(auftragModel.getAuftragId());
            }
            else if (rangierungsAuftrag != null) {
                actIA = ias.findIA4RangierungsAuftrag(rangierungsAuftrag.getId());

                if (actIA != null) {
                    RangierungAdminService ras = getCCService(RangierungAdminService.class);
                    RangierungsAuftrag ra = ras.findRA(rangierungsAuftrag.getId());

                    if (ra != null) {
                        HVTService hvts = getCCService(HVTService.class);
                        HVTGruppe hvtGruppe = hvts.findHVTGruppe4Standort(ra.getHvtStandortId());

                        if (hvtGruppe != null) {
                            actIA.setKostenstelle(hvtGruppe.getKostenstelle());
                        }
                    }
                }
            }

            if (actIA != null) {
                List<IABudget> budgets = ias.findBudgets4IA(actIA.getId());

                for (IABudget budget : budgets) {
                    budget.setProjektleiter(getLeaderName(budget.getBudgetUserId()).toString());
                }

                tfIANummer.setText(actIA.getIaNummer());
                tfProjectLead.setText(getLeaderName(actIA.getProjectLeadId()).toString());
                tbMdlBudgets.setData(budgets);
                tfKostenstelle.setText(actIA.getKostenstelle());
                tfProjektBez.setText(getProjektbezeichnung());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    private String getProjektbezeichnung() throws FindException, ServiceNotFoundException {
        String projektBez;
        if (actIA.getProjektbez() == null) {
            final VerbindungsBezeichnung vbz = getCCService(PhysikService.class).findVerbindungsBezeichnungByAuftragId(actIA.getAuftragId());
            projektBez = (vbz == null) ? null : vbz.getVbz();
        }
        else {
            projektBez = actIA.getProjektbez();
        }
        return projektBez;
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof IABudget) {
            actBudget = (IABudget) details;
            enableAddRemoveButtons(actBudget.isOpen());
            loadMatEntnahmen();
        }
        else if (details instanceof IAMaterialEntnahme) {
            actMatEnt = (IAMaterialEntnahme) details;
            loadArtikel();
        }
    }

    /* Laedt alle Materialentnahmen zum aktuellen Budget. */
    private void loadMatEntnahmen() {
        tbMdlMatEntnahmen.removeAll();
        tbMdlArtikel.removeAll();
        if (actBudget != null) {
            setWaitCursor();
            final SwingWorker<List<IAMaterialEntnahme>, Void> worker = new SwingWorker<List<IAMaterialEntnahme>, Void>() {

                final Long actBudgetId = actBudget.getId();

                @Override
                protected List<IAMaterialEntnahme> doInBackground() throws Exception {
                    InnenauftragService ias = getCCService(InnenauftragService.class);
                    List<IAMaterialEntnahme> entnahmen = ias.findMaterialEntnahmen4Budget(actBudgetId);
                    return entnahmen;
                }

                @Override
                protected void done() {
                    try {
                        List<IAMaterialEntnahme> entnahmen = get();
                        tbMdlMatEntnahmen.setData(entnahmen);
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        setDefaultCursor();
                    }
                }

            };
            worker.execute();
        }
    }

    /* Laedt alle Artikel zur aktuellen Materialentnahme. */
    private void loadArtikel() {
        if (actMatEnt != null) {
            setWaitCursor();
            final SwingWorker<List<IAMaterialEntnahmeArtikel>, Void> worker = new SwingWorker<List<IAMaterialEntnahmeArtikel>, Void>() {

                final Long actMatEntId = actMatEnt.getId();

                @Override
                protected List<IAMaterialEntnahmeArtikel> doInBackground() throws Exception {
                    InnenauftragService ias = getCCService(InnenauftragService.class);
                    List<IAMaterialEntnahmeArtikel> artikel = ias.findArtikel4MatEntnahme(actMatEntId);
                    return artikel;
                }

                @Override
                protected void done() {
                    try {
                        List<IAMaterialEntnahmeArtikel> artikel = get();
                        tbMdlArtikel.setData(artikel);
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        setDefaultCursor();
                    }
                }

            };
            worker.execute();
        }
    }

    /* Setzt die Modelle auf 'null' */
    private void cleanModels() {
        tbMdlBudgets.removeAll();
        tbMatEntnahmen.removeAll();
        tbMdlArtikel.removeAll();
        actIA = null;
        actBudget = null;
        actMatEnt = null;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        switch (command) {
            case "close.budget":
                closeBudget(false);
                break;
            case "cancel.budget":
                closeBudget(true);
                break;
            case "add.budget":
                addBudget();
                break;
            case "print.budget":
                printBudget();
                break;
            case "mail.budget":
                mailBudget();
                break;
            case "add.material.withdrawl":
                addMatEntnahme();
                break;
            case "print.material.withdrawl":
                printMaterial();
                break;
            case "add.article":
                addArticle();
                break;
            case "remove.article":
                removeArticle();
                break;
            case "import.article":
                importArticle();
                break;
            default:
                break;
        }
    }

    /* Schliesst das aktuelle Budget. */
    private void closeBudget(boolean storno) {
        try {
            if (actBudget != null) {
                if ((actBudget.getClosedAt() != null) || BooleanTools.nullToFalse(actBudget.getCancelled())) {
                    throw new HurricanGUIException("Das Budget ist bereits geschlossen od. storniert!");
                }

                // Falls es sich um ein Budget zu einem Rangierungsauftrag handelt, darf
                // dieses nur geschlossen werden, wenn die Rangierung bereits durchgefuehrt
                // wurde.
                if (!storno && (rangierungsAuftrag != null) && (rangierungsAuftrag.getAusgefuehrtAm() == null)) {
                    throw new HurricanGUIException(
                            "Budget kann nicht geschlossen werden, da die Rangierung noch nicht erfolgt ist.");
                }

                String msg = (storno) ? "Soll das aktuelle Budget wirklich storniert werden?"
                        : "Soll das aktuelle Budget wirklich geschlossen werden?";
                int option = MessageHelper.showYesNoQuestion(getMainFrame(), msg, "Budget beenden?");
                if (option == JOptionPane.YES_OPTION) {
                    actBudget.setClosedAt(new Date());
                    actBudget.setCancelled((storno) ? Boolean.TRUE : Boolean.FALSE);
                    InnenauftragService ias = getCCService(InnenauftragService.class);
                    ias.saveBudget(actBudget, HurricanSystemRegistry.instance().getSessionId());

                    int row = tbBudgets.getSelectedRow();
                    tbMdlBudgets.fireTableRowsUpdated(row, row);

                    // eMail an Fibu schicken
                    AKLoginContext loginCtx = (AKLoginContext)
                            HurricanSystemRegistry.instance().getValue(HurricanSystemRegistry.REGKEY_LOGIN_CONTEXT);
                    AKUser user = loginCtx.getUser();

                    RegistryService rs = getCCService(RegistryService.class);
                    String to = rs.getStringValue(RegistryService.REGID_EMAIL_FIBU);
                    String subject = getSwingFactory().getText(
                            (storno) ? "cancel.budget.mail.subject" : "close.budget.mail.subject",
                            actIA.getIaNummer());

                    String body = createMailBody(storno, user);
                    String from = user.getEmail();
                    String[] cc = new String[] { from };

                    IHurricanMailSender sender = getMailService();

                    SimpleMailDialog dlg = new SimpleMailDialog(sender, subject, new String[] { to }, cc, from, body, null);
                    Object mail = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                    if (!(mail instanceof IHurricanMailSender.MailData)) {
                        throw new HurricanGUIException("Die eMail wurde nicht versendet!");
                    }
                }
            }
            else {
                throw new HurricanGUIException("Kein aktuelles Budget gewaehlt!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    /*
     * Erstellt abhaengig vom Innenauftragstyp (Auftrag oder Rangierungsauftrag) und der Aktion (schliessen/Storno)
     * den entsprechenden EMail-Body.
     */
    private String createMailBody(boolean storno, AKUser user) {
        String body = null;
        if (auftragModel != null) {
            String stundenMnetMitarbeiter = null;
            boolean extern = false;

            if (!storno) {
                stundenMnetMitarbeiter = MessageHelper.showInputDialog(getMainFrame(),
                        getSwingFactory().getText("mnet.stunden.message"), getSwingFactory().getText("mnet.stunden.title"),
                        JOptionPane.QUESTION_MESSAGE);
                if (StringUtils.isBlank(stundenMnetMitarbeiter)) {
                    stundenMnetMitarbeiter = "0";
                }

                // Abfrage, ob Arbeiten nach extern vergeben wurden
                int toExtern = MessageHelper.showYesNoQuestion(getMainFrame(),
                        getSwingFactory().getText("work.extern.message"), getSwingFactory().getText("work.extern.title"));
                extern = (toExtern == JOptionPane.YES_OPTION);
            }

            String workToExternMsg = (extern) ? getSwingFactory().getText("work.extern.yes") : getSwingFactory().getText("work.extern.no");
            String finishDate = (actBudget.getPlannedFinishDate() != null)
                    ? DateTools.formatDate(actBudget.getPlannedFinishDate(), DateTools.PATTERN_DAY_MONTH_YEAR) : "";
            body = getSwingFactory().getText(
                    (storno) ? "cancel.budget.mail.body"
                            : "close.budget.mail.body",
                    (storno) ? new Object[] { actIA.getIaNummer(), user.getFirstName(), user.getName() }
                            : new Object[] { actIA.getIaNummer(), stundenMnetMitarbeiter, workToExternMsg,
                            user.getFirstName(), user.getName(), finishDate }
            );
        }
        else if (rangierungsAuftrag != null) {
            String stundenMnetMitarbeiter = (rangierungsAuftrag.getTechnikStunden() != null)
                    ? DecimalFormat.getNumberInstance().format(rangierungsAuftrag.getTechnikStunden()) : "0";
            body = getSwingFactory().getText(
                    (storno) ? "cancel.budget.mail.body"
                            : "close.enhancement.budget.mail.body",
                    (storno) ? new Object[] { actIA.getIaNummer(), user.getFirstName(), user.getName() }
                            : new Object[] { actIA.getIaNummer(), String.format("%s", stundenMnetMitarbeiter),
                            user.getFirstName(), user.getName() }
            );
        }

        return body;
    }

    /* Ueberprueft, ob es sich um eine interne Arbeit handelt. */
    private boolean isInterneArbeit(Long auftragId) {
        try {
            final ProduktService produktService = getCCService(ProduktService.class);
            final Produkt produkt = produktService.findProdukt4Auftrag(auftragId);
            return (NumberTools.equal(produkt.getProduktGruppeId(), ProduktGruppe.AK_INTERN_WORK));
        }
        catch (ServiceNotFoundException | FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /* Erzeugt ein neues Budget. */
    private void addBudget() {
        try {
            InnenauftragService ias = getCCService(InnenauftragService.class);

            if (actIA == null) {
                BAuftrag bAuftrag = getBillingOrder();
                String number = getIaNumberPart(isCustomerOrder(bAuftrag), bAuftrag);
                final String initialKostenstelle = fetchInnenAuftragKostenstelle(auftragModel.getAuftragId());
                Object resultArray = DialogHelper.showDialog(getMainFrame(),
                        createIaDefinitionDialog(bAuftrag, number, initialKostenstelle), true, true);

                if (resultArray instanceof String[]) {
                    Number refId = null;
                    Boolean customerOrder = null;

                    if (auftragModel != null) {
                        refId = auftragModel.getAuftragId();
                        customerOrder = Boolean.TRUE;
                    }
                    else if (rangierungsAuftrag != null) {
                        refId = rangierungsAuftrag.getId();
                        customerOrder = Boolean.FALSE;
                    }
                    else {
                        throw new HurricanGUIException("Referenz fuer Innenauftrag ist nicht vorhanden!");
                    }
                    final String result = ((String[])resultArray)[0];
                    final String kostenstelle = ((String[])resultArray)[1];
                    new IACreator(ias, refId, result, customerOrder, kostenstelle);
                    this.readModel();
                    this.repaint();
                    this.revalidate();
                }
                else {
                    throw new HurricanGUIException("IA-Nummer wurde nicht definiert!");
                }
            }

            tfIANummer.setText(actIA.getIaNummer());
            tfProjectLead.setText(getLeaderName(actIA.getProjectLeadId()).toString());

            final CreateIABudgetDialog budgetDialog = new CreateIABudgetDialog(actIA);
            Object result = DialogHelper.showDialog(getMainFrame(), budgetDialog, true, true);
            if ((result instanceof Integer) && (((Integer) result) == CreateIABudgetDialog.OK_OPTION)) {
                tbMdlBudgets.addObject(budgetDialog.getIaBudget());
                tbBudgets.selectAndScrollToLastRow();
                ias.saveIA(actIA);
            }
        }
        catch (NumberFormatException e) {
            MessageHelper.showErrorDialog(getMainFrame(),
                    new HurricanGUIException("Bitte geben Sie das Budget als Dezimalzahl ein."));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private String fetchInnenAuftragKostenstelle(Long auftragId) {
        if (isCurrentUserPdBereich()) {
            return fetchRegistryStringValue(RegistryService.REGID_IA_BUDGET_PD_BEREICH_KOSTENSTELLE);
        } else if (isInterneArbeit(auftragId)) {
            try {
                final InnenauftragService innenauftragService = getCCService(InnenauftragService.class);
                return innenauftragService.fetchInnenAuftragKostenstelle(auftragId);
            }
            catch (ServiceNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            return ""; // not a P&D and not interne arbeit
        }
    }

    private String fetchRegistryStringValue(Long registryId) {
        try {
            final RegistryService registryService = getCCService(RegistryService.class);
            return registryService.getStringValue(registryId);
        }
        catch (ServiceNotFoundException | FindException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        return "";
    }

    private boolean isCurrentUserPdBereich() {
        final String pdBereichName = fetchRegistryStringValue(RegistryService.REGID_IA_BUDGET_PD_BEREICH_NAME);
        final AKUser currentUser = HurricanSystemRegistry.instance().getCurrentUser();
        return currentUser.getBereich() != null
                && pdBereichName.equalsIgnoreCase(currentUser.getBereich().getName());
    }

    /* Ermittelt den Namen des Projektleiters nzw. Budgetverantwortlichen der Form <Vorname> <Nachname> */
    private StringBuilder getLeaderName(Long userId) throws ServiceNotFoundException, AKAuthenticationException {
        StringBuilder projectLeadBuilder = new StringBuilder();

        if (userId != null) {
            AKUserService userService = getAuthenticationService(AKUserService.class);
            AKUser user = userService.findById(userId);
            projectLeadBuilder.append(user.getFirstNameAndName());
        }
        return projectLeadBuilder;
    }

    /* Erzeugt den Innenauftrag-Dialog */
    private IADefinitionDialog createIaDefinitionDialog(BAuftrag bAuftrag, String number, String kostenstelle) throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        IADefinitionDialog dialog = (IADefinitionDialog) getIaDialogClass(isCustomerOrder(bAuftrag))
                .getDeclaredConstructor(String.class, String.class)
                .newInstance(number, kostenstelle);
        dialog.setBillingAuftrag(bAuftrag);
        dialog.setCcAuftragModel(auftragModel);
        dialog.parametersDefined();
        return dialog;
    }

    /* Prüft ob es zu einem Hurrican-Auftrag einen Billing-Auftrag gibt */
    private Boolean isCustomerOrder(BAuftrag bAuftrag) {
        return (bAuftrag != null) && (bAuftrag.getAuftragNoOrig() != null) ? Boolean.TRUE : Boolean.FALSE;
    }

    /* Ermittelt den Billing-Auftrag zum Hurrican-Auftrag */
    private BAuftrag getBillingOrder() throws ServiceNotFoundException, FindException {
        BAuftrag bAuftrag = null;

        // Lade Billing-Auftrag ueber AuftragDaten-Objekt
        if (auftragModel != null) {
            BillingAuftragService as = getBillingService(BillingAuftragService.class.getName(), BillingAuftragService.class);
            CCAuftragService ccService = getCCService(CCAuftragService.class);
            AuftragDaten ad = ccService.findAuftragDatenByAuftragId(auftragModel.getAuftragId());
            if ((ad != null) && (ad.getAuftragNoOrig() != null)) {
                bAuftrag = as.findAuftrag(ad.getAuftragNoOrig());
            }
        }
        return bAuftrag;
    }

    /* Ermittelt den Nummernteil für den Innenauftrag und gibt diesen zurück */
    private String getIaNumberPart(boolean isCustomerOrder, BAuftrag bAuftrag) throws ServiceNotFoundException, StoreException {
        String iaNumberPart = null;

        if (isCustomerOrder) {
            if ((bAuftrag != null) && (bAuftrag.getSapId() != null)) {
                iaNumberPart = String.format("%08d", bAuftrag.getAuftragNoOrig());
            }
        }
        else {
            CounterService counterService = getCCService(CounterService.class);
            Integer count = counterService.getNewIntValue("innenauftrag");

            String iaCounter = String.format("%d", count);
            StringBuilder builder = new StringBuilder(IA.PREFIX_SERVICE_ROOM_CODE);
            builder.append(StringTools.fillToSize(iaCounter, IA.NUMBER_LENGTH_BETRIEBSRAUM - 1, '0', true));
            iaNumberPart = builder.toString();
        }
        return iaNumberPart;
    }

    /* Gibt die Klasse des Dialogs für den Innenauftrag zurück */
    private Class<?> getIaDialogClass(boolean isCustomerOrder) {
        return isCustomerOrder ? IACustomerOrderDefinitionDialog.class : IAServiceRoomDefinitionDialog.class;
    }

    /* Erzeugt eine neue Materialentnahme zu dem aktuellen Budget. */
    private void addMatEntnahme() {
        try {
            if (actBudget == null) {
                throw new HurricanGUIException("Bitte waehlen Sie zuerst ein Budget!");
            }
            else if (!actBudget.isOpen()) {
                throw new HurricanGUIException("Das aktuelle Budget ist bereits geschlossen. Eine " +
                        "Materialentnahme ist nicht mehr moeglich.");
            }

            IAMaterialEntnahmeDialog dlg = new IAMaterialEntnahmeDialog(actBudget, lager);
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (result instanceof IAMaterialEntnahme) {
                tbMdlMatEntnahmen.addObject((IAMaterialEntnahme) result);
                tbMatEntnahmen.selectAndScrollToLastRow();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Fuegt der aktuellen Materialentnahme einen Artikel hinzu. */
    private void addArticle() {
        try {
            if (actMatEnt == null) {
                throw new HurricanGUIException("Es ist keine Materialentnahme selektiert, " +
                        "der ein Artikel zugeordnet werden koennte.");
            }

            if (!actBudget.isOpen()) {
                throw new HurricanGUIException("Das zugehoerige Budget ist bereits geschlossen!");
            }

            IAMaterialEntnahmeArtikelDialog dlg = new IAMaterialEntnahmeArtikelDialog(actMatEnt);
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (result instanceof IAMaterialEntnahmeArtikel) {
                tbMdlArtikel.addObject((IAMaterialEntnahmeArtikel) result);
                tbArtikel.selectAndScrollToLastRow();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Entfernt einen Artikel aus der Zuordnung zur Materialliste. */
    private void removeArticle() {
        try {
            int selRow = tbArtikel.getSelectedRow();
            Object selArt = ((AKMutableTableModel) tbArtikel.getModel()).getDataAtRow(selRow);
            if (selArt instanceof IAMaterialEntnahmeArtikel) {
                int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                        "Soll der Artikel wirklich entfernt werden?", "Entfernen?");
                if (option == JOptionPane.YES_OPTION) {
                    IAMaterialEntnahmeArtikel mea = (IAMaterialEntnahmeArtikel) selArt;
                    mea.setRemovedAt(new Date());
                    mea.setRemovedFrom(HurricanSystemRegistry.instance().getCurrentUserName());

                    InnenauftragService ias = getCCService(InnenauftragService.class);
                    ias.saveMaterialEntnahmeArtikel(mea);

                    tbMdlArtikel.removeObject(mea);
                }
            }
            else {
                throw new HurricanGUIException("Bitte wählen Sie zuerst den zu entfernenden Artikel aus.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Import der Artikelliste ueber ein CSV-File. */
    private void importArticle() {
        try {
            int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                    "Soll die Artikelliste wirklich ersetzt werden?", "Artikel importieren?");
            if (option == JOptionPane.YES_OPTION) {
                InnenauftragService ias = getCCService(InnenauftragService.class);

                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(getMainFrame());
                File file = chooser.getSelectedFile();

                int count = ias.importMaterialliste(file);

                MessageHelper.showInfoDialog(getMainFrame(), "Anzahl importierter Artikel: " + count);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Erstellt den Report fuer das aktuelle Budget. */
    private void printBudget() {
        try {
            if (actBudget == null) {
                throw new HurricanGUIException("Bitte zuerst ein Budget auswählen!");
            }
            saveModel();
            InnenauftragService ias = getCCService(InnenauftragService.class);
            JasperPrint jp = ias.printBudget(actBudget.getId());
            AKJasperViewer.viewReport(jp);

            if (actBudget.getCreatedAt() == null) {
                actBudget.setCreatedAt(new Date());
                ias.saveBudget(actBudget, HurricanSystemRegistry.instance().getSessionId());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Erstellt und versendet den Report fuer das aktuelle Budget per Mail.
     */
    private void mailBudget() {
        try {
            if (actBudget == null) {
                throw new HurricanGUIException("Bitte zuerst ein Budget auswählen!");
            }

            saveModel();

            InnenauftragService ias = getCCService(InnenauftragService.class);
            JasperPrint jp = ias.printBudget(actBudget.getId());

            File pdf = File.createTempFile("Innenauftrag-Budget-" + actIA.getAuftragId() + "-", ".pdf");
            AKJasperExporter.exportReport(jp, AKJasperExportTypes.EXPORT_TYPE_PDF, pdf.getAbsolutePath(), null);

            // eMail an Fibu schicken
            AKLoginContext loginCtx = (AKLoginContext)
                    HurricanSystemRegistry.instance().getValue(HurricanSystemRegistry.REGKEY_LOGIN_CONTEXT);
            AKUser user = loginCtx.getUser();

            RegistryService rs = getCCService(RegistryService.class);
            String to = rs.getStringValue(RegistryService.REGID_IA_BUDGET_PD_VERSAND_PDF);

            String vbz = "?";
            if (auftragModel != null) {
                PhysikService phys = getCCService(PhysikService.class);
                VerbindungsBezeichnung verbindungsBezeichnung =
                        phys.findVerbindungsBezeichnungByAuftragId(auftragModel.getAuftragId());
                vbz = verbindungsBezeichnung.getVbz();
            }

            String subject = getSwingFactory().getText("mail.budget.subject", vbz);
            String body = getSwingFactory().getText("mail.budget.body", user.getFirstName(), user.getName());

            String from = user.getEmail();
            String[] cc = new String[] { from };

            IHurricanMailSender sender = getMailService();

            SimpleMailDialog dlg = new SimpleMailDialog(sender, subject, new String[]{to}, cc, from, body,
                    new File[]{pdf});
            Object mail = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (!(mail instanceof IHurricanMailSender.MailData)) {
                pdf.delete();
                throw new HurricanGUIException("Die eMail wurde nicht versendet!");
            }
            if (actBudget.getCreatedAt() == null) {
                actBudget.setCreatedAt(new Date());
                ias.saveBudget(actBudget, HurricanSystemRegistry.instance().getSessionId());
            }
            pdf.delete();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Erstellt den Report fuer die aktuelle Materialliste. */
    private void printMaterial() {
        try {
            if (actMatEnt == null) {
                throw new HurricanGUIException("Bitte zuerst eine Materialliste auswählen!");
            }

            InnenauftragService ias = getCCService(InnenauftragService.class);
            JasperPrint jp = ias.printMaterialentnahme(actMatEnt.getId());
            AKJasperViewer.viewReport(jp);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        if ((actIA != null)) {
            try {
                if (tfKostenstelle != null) {
                    actIA.setKostenstelle(tfKostenstelle.getText());
                }

                actIA.setProjektbez(tfProjektBez.getText());

                InnenauftragService ias = getCCService(InnenauftragService.class);
                ias.saveIA(actIA);
            }
            catch (ServiceNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            catch (StoreException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }

    private void enableAddRemoveButtons(boolean enable) {
        btnAddMatEnt.setEnabled(enable);
        btnAddArtikel.setEnabled(enable);
        btnRemArtikel.setEnabled(enable);
    }

    /**
     * Action, um den geplanten Projektabschluss zu definieren.
     */
    class DefinePlannedFinishDateAction extends AKAbstractAction {
        private static final long serialVersionUID = -3317279913276267127L;

        DefinePlannedFinishDateAction() {
            super();
            setName("Projektabschluss definieren...");
            setActionCommand("define.planned.finish.date");
            setParentClassName(InnenauftragPanel.class.getName());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (actBudget != null) {
                if (actBudget.isOpen()) {
                    try {
                        AKDateSelectionDialog dateSelectionDlg = new AKDateSelectionDialog(
                                "geplanter Projektabschluss", "Angabe des geplanten Projektabschlusses", "Datum:");
                        dateSelectionDlg.showDate(actBudget.getPlannedFinishDate());
                        Date finishDate = (Date) DialogHelper.showDialog(getMainFrame(), dateSelectionDlg, true, true);
                        if (finishDate != null) {
                            actBudget.setPlannedFinishDate(finishDate);

                            InnenauftragService ias = getCCService(InnenauftragService.class);
                            ias.saveBudget(actBudget, HurricanSystemRegistry.instance().getSessionId());

                            ((AKTableModel) tbBudgets.getModel()).fireTableDataChanged();
                        }
                    }
                    catch (Exception ex) {
                        MessageHelper.showErrorDialog(getMainFrame(), ex);
                    }
                }
                else {
                    MessageHelper.showInfoDialog(getMainFrame(),
                            "Projektabschluss kann nicht mehr definiert werden, da Budget bereits geschlossen oder storniert ist.");
                }
            }
        }
    }


    /**
     * Klasse zum Erzeugen von Innenaufträgen
     *
     *
     */
    class IACreator {
        /**
         * Konstruktor für IACreator, erzeugt einen Innenauftrag
         *
         * @param ias             InnenauftragService
         * @param id              Auftrag-ID bzw. Rangierungsauftrag-ID
         * @param iaNumber        Nummer des Innenauftrags
         * @param isCustomerOrder Flag für Kundenenauftrag
         * @throws StoreException
         * @throws ValidationException
         * @throws HurricanGUIException
         */
        public IACreator(InnenauftragService ias, Number id, String iaNumber, Boolean isCustomerOrder, String kostenstelle) throws StoreException, ValidationException, HurricanGUIException {
            if (ias == null) {
                // HurricanGUIException
                throw new RuntimeException("InnenauftragService nicht initialisiert.");
            }

            if (id == null) {
                throw new RuntimeException("ID für den Innenauftrag nicht initialisiert.");
            }

            if ((iaNumber == null) || (iaNumber.length() == 0)) {
                throw new RuntimeException("Nummer für Innenauftrag nicht initialisiert.");
            }

            if (isCustomerOrder == null) {
                throw new RuntimeException("Flag für die Unterscheidung Auftrag, Rangierungsauftrag nicht initialisiert.");
            }

            Long projectLeadId = HurricanSystemRegistry.instance().getCurrentUser().getId();
            if (projectLeadId == null) {
                throw new RuntimeException("ID des Projektleiters nicht initialisert.");
            }

            if (BooleanTools.nullToFalse(isCustomerOrder)) {
                actIA = ias.createIA((Long) id, iaNumber, projectLeadId, kostenstelle);
            }
            else if (!BooleanTools.nullToFalse(isCustomerOrder)) {
                actIA = new IA();
                actIA.setRangierungsAuftragId((Long) id);
                actIA.setIaNummer(iaNumber);
                actIA.setProjectLeadId(projectLeadId);
                actIA.setKostenstelle(kostenstelle);
                ias.saveIA(actIA);
            }
            else {
                throw new HurricanGUIException("Budget kann nicht erstellt werden! Kein Auftrag definiert.");
            }
        }
    }

}
