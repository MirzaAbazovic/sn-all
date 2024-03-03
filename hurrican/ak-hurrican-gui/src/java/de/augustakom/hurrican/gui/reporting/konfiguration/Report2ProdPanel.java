/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2007 10:20:18
 */
package de.augustakom.hurrican.gui.reporting.konfiguration;

import java.awt.*;
import java.util.*;
import java.util.List;
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
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.view.Report2ProdView;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.reporting.ReportConfigService;


/**
 * Panel fuer die Zuordnung von Produkten zu einem ausgewählten Report.
 *
 *
 */
public class Report2ProdPanel extends AbstractAdminPanel implements AKModelOwner,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(Report2ProdPanel.class);

    private AKJButton btnSave = null;
    private AKJButton btnDelete = null;
    private AKJButton btnAddCommand = null;
    private AKJButton btnRemCommand = null;

    private AKJTable tbStatus = null;
    private AKReflectionTableModel<AuftragStatus> tbMdlStatus = null;
    private AKJTable tbProdukte = null;
    private AKReflectionTableModel<Produkt> tbMdlProdukte = null;
    private AKJTable tbView = null;
    private AKReflectionTableModel<Report2ProdView> tbMdlView = null;

    private Report detail = null;

    /**
     * Standardkonstruktor
     */
    public Report2ProdPanel() {
        super("de/augustakom/hurrican/gui/reporting/konfiguration/resources/Report2ProdPanel.xml");
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

        tbMdlStatus = new AKReflectionTableModel<AuftragStatus>(
                new String[] { "ID", "Status" },
                new String[] { "id", "statusText" },
                new Class[] { Long.class, String.class });
        tbStatus = new AKJTable(tbMdlStatus, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbStatus.attachSorter();
        tbStatus.fitTable(new int[] { 55, 150 });
        AKJScrollPane spStatus = new AKJScrollPane(tbStatus, new Dimension(230, 100));

        tbMdlProdukte = new AKReflectionTableModel<Produkt>(
                new String[] { "ID", "Produkt" }, new String[] { "id", "anschlussart" },
                new Class[] { Long.class, String.class });
        tbProdukte = new AKJTable(tbMdlProdukte, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbProdukte.attachSorter();
        tbProdukte.fitTable(new int[] { 55, 150 });
        AKJScrollPane spProdukte = new AKJScrollPane(tbProdukte, new Dimension(230, 100));

        tbMdlView = new AKReflectionTableModel<Report2ProdView>(
                new String[] { "Produkt", "Status" },
                new String[] { "produktName", "statusName" },
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
        mid.add(btnAddCommand, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.NONE, new Insets(2, 2, 5, 2)));
        mid.add(btnRemCommand, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.NONE));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 8, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("available.products")));
        right.add(spProdukte, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
        right.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH));
        right.add(spStatus, GBCFactory.createGBC(100, 100, 2, 1, 1, 1, GridBagConstraints.BOTH));

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
        if (detail != null) {
            final SwingWorker<List<Report2ProdView>, Void> worker = new SwingWorker<List<Report2ProdView>, Void>() {
                final Long localDetailId = detail.getId();

                @Override
                protected List<Report2ProdView> doInBackground() throws Exception {
                    ReportConfigService rs = getReportService(ReportConfigService.class);
                    return rs.findReport2ProdView4Report(localDetailId);
                }

                @Override
                protected void done() {
                    try {
                        List<Report2ProdView> views = get();
                        if (views != null) {
                            tbMdlView.setData(views);
                        }
                        // Falls Report ein Auftragsreport ist und bereits gespeichert wurde, d.h. id != null,
                        // dann aktiviere GUI-Elemente
                        setGuiEnabled((detail.getId() != null) && (Report.REPORT_TYPE_AUFTRAG.equals(detail.getType())));

                        // Lösche Selektion in Tabellen
                        tbProdukte.clearSelection();
                        tbStatus.clearSelection();
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        GuiTools.unlockComponents(new Component[] { btnDelete, btnSave, btnAddCommand, btnRemCommand });
                        setDefaultCursor();
                    }
                }
            };
            setWaitCursor();
            GuiTools.lockComponents(new Component[] { btnDelete, btnSave, btnAddCommand, btnRemCommand });
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
        try {
            // alle Produkte ermitteln
            ProduktService ps = getCCService(ProduktService.class);
            List<Produkt> produkte = ps.findProdukte(false);
            tbMdlProdukte.setData(produkte);

            // alle Auftragsstati ermitteln
            CCAuftragService as = getCCService(CCAuftragService.class);
            List<AuftragStatus> stati = as.findAuftragStati();
            tbMdlStatus.setData(stati);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
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

        final List<Report2ProdView> tbMdlData;
        if ((tbMdlView == null) || (tbMdlView.getData() == null) || (tbMdlView.getData().size() == 0)) {
            tbMdlData = null;
        }
        else {
            @SuppressWarnings("unchecked")
            List<Report2ProdView> tmp = (List<Report2ProdView>) tbMdlView.getData();
            tbMdlData = tmp;
        }

        final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            final List<Report2ProdView> dataToSave = tbMdlData;
            final Long id = detail.getId();

            @Override
            protected Void doInBackground() throws Exception {
                ReportConfigService rs = getReportService(ReportConfigService.class);
                rs.saveReport2ProdView(dataToSave, id);
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
                    GuiTools.unlockComponents(new Component[] { btnDelete, btnSave, btnAddCommand, btnRemCommand });
                    setDefaultCursor();
                    stopProgressBar();
                }
            }
        };
        setWaitCursor();
        showProgressBar("speichern...");
        GuiTools.lockComponents(new Component[] { btnDelete, btnSave, btnAddCommand, btnRemCommand });
        worker.execute();
    }

    /* Fuegt die selektierten Commands der Liste hinzu. */
    private void addCommand() {
        if (detail == null) {
            MessageHelper.showInfoDialog(getMainFrame(), "Bitte zuerst einen Report auswählen", null, true);
        }
        int[] selProd = tbProdukte.getSelectedRows();
        int[] selStatus = tbStatus.getSelectedRows();
        AKMutableTableModel tbMdlStatus = (AKMutableTableModel) tbStatus.getModel();
        AKMutableTableModel tbMdlProd = (AKMutableTableModel) tbProdukte.getModel();
        for (int i = 0; i < selProd.length; i++) {
            Produkt prod = (Produkt) tbMdlProd.getDataAtRow(selProd[i]);
            for (int j = 0; j < selStatus.length; j++) {
                AuftragStatus status = (AuftragStatus) tbMdlStatus.getDataAtRow(selStatus[j]);
                // Erzeuge Report2ProdView-Objekt
                Report2ProdView view = new Report2ProdView();
                view.setProduktId(prod.getId());
                view.setProduktName(prod.getAnschlussart());
                view.setReportId(detail.getId());
                view.setReportName(detail.getName());
                view.setStatusId(status.getId());
                view.setStatusName(status.getStatusText());

                // Objekt dem TableModel hinzufügen
                if (!contains(tbMdlView, view)) {
                    tbMdlView.addObject(view);
                }
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
                    rs.saveReport2ProdView(null, detail.getId());
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
     * Funktion prüft, ob eine Report2ProdView bereits im Table Modell enthalten ist.
     */
    private Boolean contains(AKTableModel model, Report2ProdView view) {
        Collection data = model.getData();
        if (data == null) {
            return Boolean.FALSE;
        }
        else {
            // Durchlaufe data und überprüfe, ob Objekt bereits vorhanden ist
            Iterator it = data.iterator();
            while (it.hasNext()) {
                Report2ProdView current = (Report2ProdView) it.next();
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
        tbStatus.setEnabled(enable);
        tbProdukte.setEnabled(enable);
        tbView.setEnabled(enable);
    }

}
