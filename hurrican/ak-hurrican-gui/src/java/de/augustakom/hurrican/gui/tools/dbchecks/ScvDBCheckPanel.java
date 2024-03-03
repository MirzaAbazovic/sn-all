/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2005 08:43:23
 */
package de.augustakom.hurrican.gui.tools.dbchecks;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMapTableModel;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.DBQueryDef;
import de.augustakom.hurrican.service.billing.QueryBillingService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ScvViewService;


/**
 * Panel zur Durchfuehrung von div. DB-Kontrollabfragen fuer die Abteilung AM (vorher SCV).
 *
 *
 */
public class ScvDBCheckPanel extends AbstractServicePanel implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(ScvDBCheckPanel.class);

    private AKJTable tbQueries = null;
    private AKReflectionTableModel<DBQueryDef> tbMdlQueries = null;
    private AKJButton btnExecute = null;
    private AKJTable tbResult = null;
    private AKJLabel lblCount = null;
    private AKJScrollPane spResult = null;

    /**
     * Default-Konstruktor.
     */
    public ScvDBCheckPanel() {
        super("de/augustakom/hurrican/gui/tools/dbchecks/resources/ScvDBCheckPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblQueries = getSwingFactory().createLabel("db.queries");
        AKJLabel lblResult = getSwingFactory().createLabel("result");
        lblCount = new AKJLabel("");
        btnExecute = getSwingFactory().createButton("execute.query", getActionListener());

        tbMdlQueries = new AKReflectionTableModel<DBQueryDef>(
                new String[] { "Name", "Datenbank", "Beschreibung" },
                new String[] { "name", "service", "description" },
                new Class[] { String.class, String.class, String.class });
        tbQueries = new AKJTable(tbMdlQueries, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbQueries.setFilterEnabled(false);
        tbQueries.addPopupAction(new ShowSQLAction());
        tbQueries.attachSorter();
        tbQueries.fitTable(800, new double[] { 20d, 10d, 70d });
        AKJScrollPane spQueries = new AKJScrollPane(tbQueries, new Dimension(820, 100));

        tbResult = new AKJTable();
        tbResult.setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
        spResult = new AKJScrollPane(tbResult, new Dimension(820, 300));

        this.setLayout(new GridBagLayout());
        this.add(lblQueries, GBCFactory.createGBC(0, 0, 0, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(spQueries, GBCFactory.createGBC(100, 0, 0, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(btnExecute, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblResult, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblCount, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(spResult, GBCFactory.createGBC(100, 100, 0, 5, 2, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            ScvViewService service = getCCService(ScvViewService.class);
            List<DBQueryDef> queryDefs = service.findDBQueryDefs();
            tbMdlQueries.setData(queryDefs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("execute.query".equals(command)) {
            executeQuery();
        }
    }

    /**
     * Fuehrt die selektierte Abfrage aus.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "DM_GC", justification = "Verhindert Out of Memory Exceptions")
    private void executeQuery() {
        try {
            // Weil haeufiger Out of Memory Exceptions fliegen, die durch Kontrollabfragen
            // mit ueberdurchschnittlich grossen Datenmengen verursacht sind, wird an
            // dieser Stelle der Garbage Collector aufgerufen.
            Runtime runtime = Runtime.getRuntime();
            LOGGER.info("############# free memory before NULL: " + runtime.freeMemory());
            tbResult = null;
            SwingWorker<List<Map<String, Object>>, Void> worker = null;
            LOGGER.info("############# free memory before   GC: " + runtime.freeMemory());
            runtime.gc();
            LOGGER.info("############# free memory after    GC: " + runtime.freeMemory());

            tbResult = new AKJTable();
            tbResult.setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
            spResult.setViewportView(tbResult);
            tbResult.setModel(new DefaultTableModel());
            tbResult.revalidate();

            AKMutableTableModel tbMdl = (AKMutableTableModel) tbQueries.getModel();
            Object tmp = tbMdl.getDataAtRow(tbQueries.getSelectedRow());
            if (tmp instanceof DBQueryDef) {
                final DBQueryDef query = (DBQueryDef) tmp;
                final Object[] queryParams;
                if (StringUtils.isNotBlank(query.getParams())) {
                    DBQueryParamDialog dlg = new DBQueryParamDialog(query);
                    queryParams = (Object[]) DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                }
                else {
                    queryParams = null;
                }

                worker = new SwingWorker<List<Map<String, Object>>, Void>() {

                    @Override
                    protected List<Map<String, Object>> doInBackground() throws Exception {
                        if (query.isBillingQuery()) {
                            QueryBillingService qs = getBillingService(QueryBillingService.class);
                            return qs.query(query.getSqlQuery(), query.getColumnNames(), queryParams);
                        }
                        else if (query.isCCQuery()) {
                            QueryCCService qs = getCCService(QueryCCService.class);
                            return qs.query(query.getSqlQuery(), query.getColumnNames(), queryParams);
                        }
                        else {
                            throw new HurricanGUIException(
                                    "Es werden z.Z. nur Abfragen für das Billing- und CC-System unterstuetzt.");
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            Runtime runtime = Runtime.getRuntime();
                            LOGGER.info("############# free memory before createResultTableModel: " + runtime.freeMemory());
                            createResultTableModel(get());
                            LOGGER.info("############# free memory after createResultTableModel: " + runtime.freeMemory());

                        }
                        catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                            MessageHelper.showErrorDialog(getMainFrame(), e);
                        }
                        finally {
                            GuiTools.unlockComponents(new Component[] { btnExecute });
                            stopProgressBar();
                            setDefaultCursor();
                        }
                    }
                };
                setWaitCursor();
                showProgressBar("Datensätze suchen...");
                GuiTools.lockComponents(new Component[] { btnExecute });
                worker.execute();
            }
            else {
                throw new HurricanGUIException("Bitte wählen Sie zuerst eine Abfrage aus.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Erstellt aus einer Liste mit Maps ein Table-Model fuer die Result-Table. */
    private void createResultTableModel(List<Map<String, Object>> data) {
        lblCount.setText("" + ((data != null) ? data.size() : 0));

        AKMapTableModel tbMdl = new AKMapTableModel();
        tbMdl.setData(data);

        tbResult.setModel(tbMdl);
        tbResult.attachSorter();
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /* Action, um das SQL-Statement eines Queries anzuzeigen. */
    class ShowSQLAction extends AKAbstractAction {
        /**
         * Default-Const.
         */
        public ShowSQLAction() {
            setName("SQL anzeigen");
            setTooltip("Zeigt das SQL-Statement des Queries an");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            int selRow = tbQueries.getSelectedRow();
            if (selRow >= 0) {
                @SuppressWarnings("unchecked")
                AKMutableTableModel<DBQueryDef> tbMdl = (AKMutableTableModel<DBQueryDef>) tbQueries.getModel();
                Object tmp = tbMdl.getDataAtRow(selRow);
                if (tmp instanceof DBQueryDef) {
                    String sql = ((DBQueryDef) tmp).getSqlQuery();
                    MessageHelper.showInfoDialog(getMainFrame(), sql);
                }
            }
        }
    }
}
