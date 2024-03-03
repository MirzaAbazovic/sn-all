/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.05.2007 13:20:18
 */
package de.augustakom.hurrican.gui.reporting.konfiguration;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.view.Report2TechLsView;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.reporting.ReportConfigService;


/**
 * Panel fuer die Zuordnung von Reports zu Technischen Leistungen.
 *
 *
 */
public class Report2TechLsPanel extends AbstractAdminPanel implements AKModelOwner,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(Report2TechLsPanel.class);

    private AKJButton btnSave = null;
    private AKJButton btnDelete = null;
    private AKJButton btnAddCommand = null;
    private AKJButton btnRemCommand = null;
    private AKJButton btnRefresh = null;

    private AKJTable tbTechLs = null;
    private AKReflectionTableModel<TechLeistung> tbMdlTechLs = null;
    private AKJTable tbProdukte = null;
    private AKReflectionTableModel<Produkt> tbMdlProdukte = null;
    private AKJTable tbView = null;
    private AKReflectionTableModel<Report2TechLsView> tbMdlView = null;

    private Report detail = null;

    /**
     * Standardkonstruktor
     */
    public Report2TechLsPanel() {
        super("de/augustakom/hurrican/gui/reporting/konfiguration/resources/Report2TechLsPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        btnDelete = getSwingFactory().createButton("delete.config", getActionListener(), null);
        btnSave = getSwingFactory().createButton("save.config", getActionListener(), null);
        btnAddCommand = getSwingFactory().createButton("add.command", getActionListener(), null);
        btnRemCommand = getSwingFactory().createButton("remove.command", getActionListener(), null);
        btnRefresh = getSwingFactory().createButton("refresh.command", getActionListener(), null);

        tbMdlTechLs = new AKReflectionTableModel<>(
                new String[] { "ID", "Technische Leistung" },
                new String[] { "id", "name" },
                new Class[] { Long.class, String.class });
        tbTechLs = new AKJTable(tbMdlTechLs, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbTechLs.attachSorter();
        tbTechLs.fitTable(new int[] { 55, 150 });
        AKJScrollPane spLeistungen = new AKJScrollPane(tbTechLs, new Dimension(230, 100));

        tbMdlProdukte = new AKReflectionTableModel<>(
                new String[] { "ID", "Produkt" }, new String[] { "id", "anschlussart" },
                new Class[] { Long.class, String.class });
        tbProdukte = new AKJTable(tbMdlProdukte, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbProdukte.attachSorter();
        tbProdukte.addTableListener(this);
        tbProdukte.fitTable(new int[] { 55, 150 });
        AKJScrollPane spProdukte = new AKJScrollPane(tbProdukte, new Dimension(230, 100));

        tbMdlView = new AKReflectionTableModel<>(
                new String[] { "Produkt", "Leistung" },
                new String[] { "produktName", "techLsName" },
                new Class[] { String.class, String.class });
        tbView = new AKJTable(tbMdlView, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbView.attachSorter();
        tbView.fitTable(new int[] { 100, 100 });
        AKJScrollPane spView = new AKJScrollPane(tbView, new Dimension(230, 100));

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("assigned.products")));
        left.add(spView, GBCFactory.createGBC(100, 100, 0, 1, 1, 3, GridBagConstraints.BOTH));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 3, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel mid = new AKJPanel(new GridBagLayout());
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(btnSave, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.NONE));
        mid.add(btnDelete, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.NONE));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.NONE));
        mid.add(btnRefresh, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.NONE));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.NONE));
        mid.add(btnAddCommand, GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.NONE, new Insets(2, 2, 5, 2)));
        mid.add(btnRemCommand, GBCFactory.createGBC(0, 0, 0, 9, 1, 1, GridBagConstraints.NONE));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 10, 1, 1, GridBagConstraints.VERTICAL));

        spProdukte.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("available.products")));
        spLeistungen.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("available.leistungen")));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.setBorder(BorderFactory.createEtchedBorder());
        right.add(spProdukte, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
        right.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH));
        right.add(spLeistungen, GBCFactory.createGBC(100, 100, 2, 1, 1, 1, GridBagConstraints.BOTH));

        AKJPanel center = new AKJPanel(new GridBagLayout());
        center.add(left, GBCFactory.createGBC(20, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        center.add(mid, GBCFactory.createGBC(0, 100, 1, 0, 1, 1, GridBagConstraints.VERTICAL));
        center.add(right, GBCFactory.createGBC(100, 100, 2, 0, 1, 1, GridBagConstraints.BOTH));

        setGuiEnabled(false);

        this.setLayout(new BorderLayout());
        this.add(center, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        tbMdlView.removeAll();
        tbMdlProdukte.removeAll();
        tbMdlTechLs.removeAll();

        if (detail != null) {
            final SwingWorker<Pair<List<Report2TechLsView>, List<Produkt>>, Void> worker =
                    new SwingWorker<Pair<List<Report2TechLsView>, List<Produkt>>, Void>() {
                        final Long localDetailId = detail.getId();

                        @Override
                        protected Pair<List<Report2TechLsView>, List<Produkt>> doInBackground() throws Exception {
                            ReportConfigService rs = getReportService(ReportConfigService.class);
                            // Leistungs-Zuordnung laden
                            List<Report2TechLsView> views = rs.findReport2TechLsView4Report(localDetailId);
                            // alle Produkte für diesen Report ermitteln
                            List<Produkt> produkte = rs.findProdukte4Report(localDetailId);
                            return Pair.create(views, produkte);
                        }

                        @Override
                        protected void done() {
                            try {
                                Pair<List<Report2TechLsView>, List<Produkt>> pair = get();
                                if (CollectionTools.isNotEmpty(pair.getFirst())) {
                                    tbMdlView.setData(pair.getFirst());
                                }
                                if (CollectionTools.isNotEmpty(pair.getSecond())) {
                                    tbMdlProdukte.setData(pair.getSecond());
                                }

                                // Falls Report ein Auftragsreport ist und bereits gespeichert wurde, d.h. id != null,
                                // dann aktiviere GUI-Elemente
                                setGuiEnabled((detail.getId() != null)
                                        && (Report.REPORT_TYPE_AUFTRAG.equals(detail.getType())));

                                // Lösche Selektion in Tabellen
                                tbProdukte.clearSelection();
                                tbTechLs.clearSelection();
                            }
                            catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                                MessageHelper.showErrorDialog(getMainFrame(), e);
                            }
                            finally {
                                GuiTools.unlockComponents(new Component[] { btnDelete, btnSave, btnAddCommand, btnRemCommand, btnRefresh });
                                setDefaultCursor();
                                stopProgressBar();
                            }
                        }
                    };
            setWaitCursor();
            showProgressBar("lade Techn. Leistungen...");
            GuiTools.lockComponents(new Component[] { btnDelete, btnSave, btnAddCommand, btnRemCommand, btnRefresh });
            worker.execute();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.detail = (model instanceof Report) ? (Report) model : null;
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("save.config".equals(command)) {
            saveConfig();
        }
        else if ("add.command".equals(command)) {
            addCommand();
        }
        else if ("remove.command".equals(command)) {
            removeCommand();
        }
        else if ("delete.config".equals(command)) {
            deleteConfig();
        }
        else if ("refresh.command".equals(command)) {
            readModel();
        }

    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable arg0, Object arg1) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if ((details != null) && (details instanceof Produkt)) {
            Produkt prod = (Produkt) details;
            tbMdlTechLs.removeAll();
            tbTechLs.clearSelection();
            try {
                CCLeistungsService ls = getCCService(CCLeistungsService.class);

                // Alle techn. Leistungen laden
                List<TechLeistung> leistungen = ls.findProd2TechLs(prod.getId(), null, null).
                        stream().map(p2t->p2t.getTechLeistung()).collect(Collectors.toList());
                if (CollectionTools.isNotEmpty(leistungen)) {
                    tbMdlTechLs.setData(leistungen);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /* Speichert die aktuelle Konfiguration. */
    private void saveConfig() {
        if (detail == null) {
            MessageHelper.showInfoDialog(getMainFrame(), "Bitte zuerst einen Report auswaehlen!", null, true);
            return;
        }
        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                getSwingFactory().getText("ask.save.msg"), getSwingFactory().getText("ask.save.title"));
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        final List<Report2TechLsView> tbMdlData;
        if ((tbMdlView == null) || (tbMdlView.getData() == null) || (tbMdlView.getData().size() == 0)) {
            tbMdlData = null;
        }
        else {
            @SuppressWarnings("unchecked")
            List<Report2TechLsView> tmp = (List<Report2TechLsView>) tbMdlView.getData();
            tbMdlData = tmp;
        }

        final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            final Long id = detail.getId();
            final List<Report2TechLsView> dataToSave = tbMdlData;

            @Override
            protected Void doInBackground() throws Exception {
                ReportConfigService rs = getReportService(ReportConfigService.class);
                rs.saveReport2TechLsView(dataToSave, id);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    GuiTools.unlockComponents(new Component[] { btnDelete, btnSave, btnAddCommand, btnRemCommand, btnRefresh });
                    setDefaultCursor();
                    stopProgressBar();
                }
            }
        };
        setWaitCursor();
        showProgressBar("speichern...");
        GuiTools.lockComponents(new Component[] { btnDelete, btnSave, btnAddCommand, btnRemCommand, btnRefresh });
        worker.execute();
    }

    /* Fuegt die selektierten Commands der Liste hinzu. */
    private void addCommand() {
        if (detail == null) {
            MessageHelper.showInfoDialog(getMainFrame(), "Bitte zuerst einen Report auswählen", null, true);
        }
        int selProd = tbProdukte.getSelectedRow();
        int[] selTechLs = tbTechLs.getSelectedRows();
        AKMutableTableModel tbMdlTechLs = (AKMutableTableModel) tbTechLs.getModel();
        AKMutableTableModel tbMdlProd = (AKMutableTableModel) tbProdukte.getModel();
        Produkt prod = (Produkt) tbMdlProd.getDataAtRow(selProd);
        for (int j = 0; j < selTechLs.length; j++) {
            TechLeistung techLs = (TechLeistung) tbMdlTechLs.getDataAtRow(selTechLs[j]);
            // Erzeuge Report2TechLsView-Objekt
            Report2TechLsView view = new Report2TechLsView();
            view.setProduktId(prod.getId());
            view.setProduktName(prod.getAnschlussart());
            view.setReportId(detail.getId());
            view.setReportName(detail.getName());
            view.setTechLsId(techLs.getId());
            view.setTechLsName(techLs.getName());

            // Objekt dem TableModel hinzufügen
            if (!contains(tbMdlView, view)) {
                tbMdlView.addObject(view);
            }
        }
    }

    /* Entfernt die selektierten Commands aus der Liste. */
    private void removeCommand() {
        int[] selection = tbView.getSelectedRows();
        if (selection != null) {
            AKMutableTableModel tmMdl = (AKMutableTableModel) tbView.getModel();

            List data = new ArrayList();
            for (int i = 0; i < selection.length; i++) {
                Object test = tmMdl.getDataAtRow(selection[i]);
                data.add(test);
            }
            // Entferne Objekte aus TableModel
            Collection coll = tbMdlView.getData();
            coll.removeAll(data);
            tbMdlView.setData(coll);
        }
    }

    /* Loescht die aktuell ausgewaehlte Konfiguration. */
    private void deleteConfig() {
        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                getSwingFactory().getText("ask.delete.msg"), getSwingFactory().getText("ask.delete.title"));
        if (option != JOptionPane.YES_OPTION) {
            return;
        }
        else {
            try {
                setWaitCursor();

                // Lösche alle Daten aus View
                tbMdlView.removeAll();

                // Speichere in DB
                if (detail != null) {
                    ReportConfigService rs = getReportService(ReportConfigService.class);
                    rs.saveReport2TechLsView(null, detail.getId());
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
    }

    /*
     * Funktion prüft, ob eine Report2TechLsView bereits im Table Modell enthalten ist.
     */
    private Boolean contains(AKTableModel model, Report2TechLsView view) {
        Collection data = model.getData();
        if (CollectionTools.isEmpty(data)) {
            return Boolean.FALSE;
        }
        else {
            // Durchlaufe data und überprüfe, ob Objekt bereits vorhanden ist
            Iterator it = data.iterator();
            while (it.hasNext()) {
                Report2TechLsView current = (Report2TechLsView) it.next();
                if (current.hasSameValues(view)) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
    }

    /*
     * Setzt die Gui-Elemente editierbar
     */
    private void setGuiEnabled(boolean enable) {
        btnSave.setEnabled(enable);
        btnDelete.setEnabled(enable);
        btnAddCommand.setEnabled(enable);
        btnRemCommand.setEnabled(enable);
        btnRefresh.setEnabled(enable);
        tbTechLs.setEnabled(enable);
        tbProdukte.setEnabled(enable);
        tbView.setEnabled(enable);
    }

}
