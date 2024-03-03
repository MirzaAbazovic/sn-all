/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKFilterTableModelListener;
import de.augustakom.common.gui.swing.table.AKTableListener;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.cps.CPSTxDetailPanel.CPSTxButtonPanel;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.service.cc.CPSService;

/**
 * Panel fuer die Anzeige/Bearbeitung der CPS-Transaktionen
 *
 *
 */
public class CPSTxPanel extends AbstractServicePanel implements AKDataLoaderComponent,
        AKTableOwner, CPSTxObserver, AKFilterTableModelListener {

    public static final String RESOURCE = "de/augustakom/hurrican/gui/cps/resources/CPSTransactionPanel.xml";

    private static final String TABLE_RESOURCE = "de/augustakom/hurrican/gui/cps/resources/CPSTxTable.xml";

    private static final Logger LOGGER = Logger.getLogger(CPSTxPanel.class);

    private static final String PROGRESS_TEXT = "progressText";

    private static final String LOAD_TEXT = "loadText";
    private static final long serialVersionUID = -3023950205582488951L;

    private AKTableListener akTableListener = null;

    private CPSTxTable cpsTxTable = null;

    private CPSTxDetailPanel cpsTransactionDetailPanel = null;

    private CPSTxLoader txLoader = null;

    /**
     * Default-Konstruktor
     */
    public CPSTxPanel() {
        super(RESOURCE);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        try {
            cpsTransactionDetailPanel = new CPSTxDetailPanel();
            cpsTransactionDetailPanel.addObserver(this);
            cpsTransactionDetailPanel.getButtonPanel().enableButtons(Boolean.FALSE, Boolean.TRUE);
            akTableListener = new AKTableListener(this, false);
            cpsTxTable = new CPSTxTable(TABLE_RESOURCE);

            @SuppressWarnings("unchecked")
            AKTableSorter<CPSTransactionExt> sorter = (AKTableSorter<CPSTransactionExt>) cpsTxTable
                    .getModel();
            @SuppressWarnings("unchecked")
            AKTableModelXML<CPSTransactionExt> model = (AKTableModelXML<CPSTransactionExt>) sorter
                    .getModel();

            model.addFilterTableModelListener(this);
            cpsTxTable.addMouseListener(akTableListener);
            cpsTxTable.addKeyListener(akTableListener);
            cpsTxTable.fitTable(model.getFitList());

            cpsTxTable.getSelectionModel().addListSelectionListener(
                    new CPSTxTableSelectionListener(cpsTxTable));
            cpsTxTable.addObserver(cpsTransactionDetailPanel);
            cpsTxTable.addObserver(cpsTransactionDetailPanel.getButtonPanel());
            cpsTxTable.addObserver(cpsTransactionDetailPanel.getFieldPanel());
            cpsTxTable.addObserver(cpsTransactionDetailPanel.getRequestPanel());
            cpsTxTable.addObserver(cpsTransactionDetailPanel.getResponsePanel());
            cpsTxTable.addObserver(cpsTransactionDetailPanel.getLogPanel());

            AKJScrollPane tableSP = new AKJScrollPane(cpsTxTable);
            tableSP.setPreferredSize(new Dimension(925, 270));

            JSplitPane jSplitPane = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT, true);
            jSplitPane.setLeftComponent(tableSP);
            jSplitPane.setRightComponent(cpsTransactionDetailPanel);
            jSplitPane.setDividerLocation(370);

            setLayout(new BorderLayout());
            add(jSplitPane, BorderLayout.CENTER);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    public void cancel() {
        txLoader.cancel(true);
        stopProgressBar();
        // query should be canceled
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        txLoader = new CPSTxLoader();
        txLoader.execute();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(final Object details) {
        if (null == details) { throw new RuntimeException("CPSTransaction NOT SET!!!"); }

        if ((details instanceof CPSTransactionExt) && (cpsTxTable.getSelectedRowCount() == 1)) {
            try {
                setWaitCursor();
                showProgressBar(getSwingFactory().getText(LOAD_TEXT));

                if ((null != ((CPSTransactionExt) details).getTxState())
                        && (null != cpsTransactionDetailPanel)) {
                    CPSTxButtonPanel cpsTxButtonPanel = cpsTransactionDetailPanel.getButtonPanel();

                    if (null != cpsTxButtonPanel) {
                        cpsTxButtonPanel.manageButtons(((CPSTransactionExt) details).getTxState());
                    }
                }
                CPSTxController.writeDataFromModelToView((CPSTransactionExt) details,
                        cpsTransactionDetailPanel.getFieldPanel(),
                        cpsTransactionDetailPanel.getRequestPanel(),
                        cpsTransactionDetailPanel.getResponsePanel(),
                        cpsTransactionDetailPanel.getLogPanel(),
                        cpsTransactionDetailPanel.getOderPanel());
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            finally {
                setDefaultCursor();
                stopProgressBar();
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKFilterTableModelListener#tableFiltered()
     */
    @Override
    public void tableFiltered() {
        StringBuilder newTitle = new StringBuilder(StringUtils.trimToEmpty(getFrameTitle()));
        newTitle.append(" - Anzahl: ");
        newTitle.append(cpsTxTable.getRowCount());
        setFrameTitle(newTitle.toString());
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.hurrican.gui.cps.CPSTxObserver#update(de.augustakom.hurrican.gui.cps.CPSTxObservable)
     */
    @Override
    public void update(CPSTxObservable observable) {
        @SuppressWarnings("unchecked")
        AKTableSorter<CPSTransactionExt> sorter = (AKTableSorter<CPSTransactionExt>) cpsTxTable
                .getModel();
        sorter.setData(null);
        loadData();
    }

    class CPSTxLoader extends SwingWorker<List<CPSTransactionExt>, Void> {

        CPSTxLoader() {
            setWaitCursor();
            showProgressBar(getSwingFactory().getText(PROGRESS_TEXT));
        }

        /**
         * @see javax.swing.SwingWorker#doInBackground()
         */
        @Override
        protected List<CPSTransactionExt> doInBackground() throws Exception {
            CPSService cpsService = getCCService(CPSService.class);
            return cpsService.findOpenTransactions(null);
        }

        @Override
        protected void done() {
            try {
                List<CPSTransactionExt> cpsTransactionList = get();

                @SuppressWarnings("unchecked")
                AKTableSorter<CPSTransactionExt> sorter = (AKTableSorter<CPSTransactionExt>) cpsTxTable
                        .getModel();
                @SuppressWarnings("unchecked")
                AKTableModelXML<CPSTransactionExt> tableModel = (AKTableModelXML<CPSTransactionExt>) sorter
                        .getModel();
                tableModel.setData(cpsTransactionList);

                if ((null != cpsTransactionList) && (!cpsTransactionList.isEmpty())) {
                    cpsTxTable.changeSelection(0, 0, Boolean.FALSE, Boolean.FALSE);
                    akTableListener.selectionChanged(cpsTxTable);
                }

                setFrameTitle((cpsTransactionList != null) ? new StringBuilder(getFrameTitle())
                        .append(" - Anzahl: ").append(cpsTransactionList.size()).toString()
                        : getFrameTitle());
            }
            catch (CancellationException e) {
                LOGGER.debug("CPSTxLoader canceled", e);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                stopProgressBar();
                setDefaultCursor();
            }
        }

    }
}
