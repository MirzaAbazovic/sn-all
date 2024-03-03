/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2009 07:30:12
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.SwingWorker.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDialog;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSGetServiceOrderStatusResponseData;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CPSService;

/**
 * Dialog, für die Anzeige des Status einer CPS-Transaktion
 *
 *
 */
public final class CPSTxStateDialog extends AKJDialog implements AKDataLoaderComponent {

    private final static Logger LOGGER = Logger.getLogger(CPSTxStateDialog.class);

    private final static String TITLE = "title";
    private final static String TXID = "txId";
    private final static String STATUS = "status";
    private final static String RESULT = "result";
    private final static String RESPONSE = "response";
    private final static String CANCEL = "cancel";
    private final static String RELOAD = "reload";
    private final static String PROGRESS = "progress";
    private final static String ERROR = "error";
    private final static String TXCOUNT = "txCount";
    private final static String SUCCESS = "success";
    private final static String PROGRESSCPS = "progressCps";
    private final static String CPSTRANSACTION = "cpsTransaction";
    private final static String PROGRESSCANCEL = "progressCancel";

    private final static String RESOURCE = "de/augustakom/hurrican/gui/cps/resources/CPSTxStateDialog.xml";
    private final static String TIT = SwingFactory.getInstance(RESOURCE).getText(TITLE);

    private List<CPSTransactionExt> txList = null;
    private CPSService cpsService = null;
    private AKJTable cpsTxStateTable = null;
    private CPSTxStateWorker cpsTxStateWorker = null;
    private CPSTxStatePanel cpsTxStatePanel = null;
    private AKJButton cancelButton = null;
    private AKJButton reloadButton = null;

    /**
     * @param cpsService
     * @param txList
     * @param resizable
     */
    public CPSTxStateDialog(CPSService cpsService, List<CPSTransactionExt> txList, boolean resizable) {
        super(HurricanSystemRegistry.instance().getMainFrame(), Boolean.TRUE);
        this.setResizable(resizable);
        this.cpsService = cpsService;
        this.txList = txList;

        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    protected final void createGUI() {
        try {
            setTitle(TIT);

            addWindowListener(new WindowAdapter() {
                /** @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent) */
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    cpsTxStatePanel.stopProgress();
                    cpsTxStateWorker.cancel(Boolean.TRUE);
                }
            });

            this.cpsTxStatePanel = new CPSTxStatePanel(RESOURCE);
            this.getContentPane().add(this.cpsTxStatePanel);
            this.setPreferredSize(new Dimension(400, 250));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        cpsTxStateWorker = new CPSTxStateWorker(txList);
        cancelButton.requestFocusInWindow();
        modifyGUI(Boolean.TRUE, SwingFactory.getInstance(RESOURCE).getText(PROGRESS));
        ((AKMutableTableModel) cpsTxStateTable.getModel()).removeAll();
        cpsTxStateWorker.execute();
    }

    /**
     * @see javax.swing.JDialog#createRootPane()
     */
    @Override
    protected JRootPane createRootPane() {
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
                getToolkit().getSystemEventQueue().postEvent(new WindowEvent(get(), WindowEvent.WINDOW_CLOSING));
            }
        };

        JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }

    public CPSTxStateDialog get() {
        return this;
    }

    /*
    * Details
    */
    public static class CPSTxStateDetailDialog extends AKJDialog {
        private CPSTxState model = null;

        private AKJFormattedTextField idField = null;
        private AKJFormattedTextField stateField = null;
        private AKJFormattedTextField resultField = null;
        private CPSTxTextArea textArea = null;

        /**
         * @param data
         */
        public CPSTxStateDetailDialog(CPSTxState data) {
            super(HurricanSystemRegistry.instance().getMainFrame(), Boolean.TRUE);
            this.model = data;

            setTitle(new StringBuilder(SwingFactory.getInstance(RESOURCE).getText(CPSTRANSACTION)).append(" ").append(model.getTxId()).toString());

            idField = SwingFactory.getInstance(RESOURCE).createFormattedTextField(TXID, Boolean.FALSE);
            stateField = SwingFactory.getInstance(RESOURCE).createFormattedTextField(STATUS, Boolean.FALSE);
            resultField = SwingFactory.getInstance(RESOURCE).createFormattedTextField(RESULT, Boolean.FALSE);

            textArea = new CPSTxTextArea();
            textArea.setEditable(Boolean.FALSE);
            textArea.setPreferredSize(new Dimension(200, 20));
            textArea.setLineWrap(Boolean.TRUE);
            textArea.setWrapStyleWord(Boolean.TRUE);
            AKJScrollPane textScrollPane = new AKJScrollPane(textArea);

            AKJLabel txIdLabel = SwingFactory.getInstance(RESOURCE).createLabel(TXID);
            AKJLabel statusLabel = SwingFactory.getInstance(RESOURCE).createLabel(STATUS);
            AKJLabel resultLabel = SwingFactory.getInstance(RESOURCE).createLabel(RESULT);
            AKJLabel responseLabel = SwingFactory.getInstance(RESOURCE).createLabel(RESPONSE);

            AKJPanel detailPanel = new AKJPanel(new GridBagLayout());
            detailPanel.setBorder(BorderFactory.createEtchedBorder());
            detailPanel.add(txIdLabel, GBCFactory.createGBC(1, 1, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            detailPanel.add(idField, GBCFactory.createGBC(99, 1, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            detailPanel.add(statusLabel, GBCFactory.createGBC(1, 1, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            detailPanel.add(stateField, GBCFactory.createGBC(99, 1, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            detailPanel.add(resultLabel, GBCFactory.createGBC(1, 1, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            detailPanel.add(resultField, GBCFactory.createGBC(99, 1, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            detailPanel.add(responseLabel, GBCFactory.createGBC(1, 97, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
            detailPanel.add(textScrollPane, GBCFactory.createGBC(99, 97, 1, 3, 1, 1, GridBagConstraints.BOTH));

            getContentPane().add(detailPanel);
            setPreferredSize(new Dimension(400, 250));

            idField.setText("" + model.getTxId());
            stateField.setText("" + model.getStatus());
            resultField.setText("" + model.getResult());
            textArea.setText(model.getResponse());
        }

    }

    /*
     * State Panel
     */
    class CPSTxStatePanel extends AbstractServicePanel {

        /*
        * Konstruktor mit Angabe der Resource-Datei.
        */
        public CPSTxStatePanel(String resource) {
            super(resource);
            createGUI();
        }

        /**
         * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
         */
        @Override
        protected final void createGUI() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEtchedBorder());

            AKReflectionTableModel<CPSTxState> tbModelTxState =
                    new AKReflectionTableModel<CPSTxState>(
                            new String[] { SwingFactory.getInstance(RESOURCE).getText(TXID),
                                    SwingFactory.getInstance(RESOURCE).getText(STATUS),
                                    SwingFactory.getInstance(RESOURCE).getText(RESULT),
                                    SwingFactory.getInstance(RESOURCE).getText(RESPONSE) },
                            new String[] { "txId", "status", "result", "response" },
                            new Class[] { Long.class, String.class, Long.class, String.class }
                    );
            cpsTxStateTable = new AKJTable(tbModelTxState, AKJTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
            cpsTxStateTable.addMouseListener(new AKTableDoubleClickMouseListener(new AKObjectSelectionListener() {

                @Override
                public void objectSelected(Object selection) {
                    if (selection instanceof CPSTxState) {
                        CPSTxStateDetailDialog cpsTxStateDetailDialog = new CPSTxStateDetailDialog((CPSTxState) selection);
                        DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), cpsTxStateDetailDialog, Boolean.TRUE, Boolean.TRUE);
                    }
                }
            }));

            cancelButton = getSwingFactory().createButton(CANCEL);
            cancelButton.addActionListener(new ActionListener() {
                /** @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent) */
                @Override
                public void actionPerformed(ActionEvent e) {
                    cpsTxStatePanel.showProgress(SwingFactory.getInstance(RESOURCE).getText(PROGRESSCANCEL));
                    cpsTxStatePanel.stopProgress();
                    cpsTxStateWorker.cancel(Boolean.TRUE);
                }
            });

            cancelButton.addMouseListener(new MouseAdapter() {
                /** @see java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent) */
                @Override
                public void mouseEntered(MouseEvent e) {
                    if ((e.getSource() instanceof AKJButton) && ((AKJButton) e.getSource()).isEnabled()) {
                        setDefaultCursor();
                    }
                }

                /** @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent) */
                @Override
                public void mouseExited(MouseEvent e) {
                    if ((null != cpsTxStateWorker) && (cpsTxStateWorker.getState() != StateValue.DONE)) {
                        setWaitCursor();
                    }
                }
            });

            reloadButton = getSwingFactory().createButton(RELOAD);
            reloadButton.addActionListener(new ActionListener() {
                /** @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent) */
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadData();
                }
            });

            AKJPanel buttonPanel = new AKJPanel();
            buttonPanel.setLayout(new GridBagLayout());
            buttonPanel.add(cancelButton, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            buttonPanel.add(reloadButton, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

            AKJScrollPane tableScrollPane = new AKJScrollPane(cpsTxStateTable);
            tableScrollPane.setPreferredSize(new Dimension(250, 220));
            add(tableScrollPane, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        /**
         * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
         */
        @Override
        protected void execute(String command) {
        }

        /**
         * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
         */
        @Override
        public void update(Observable o, Object arg) {
        }

        /**
         * @see de.augustakom.hurrican.gui.base.AbstractServicePanel#showProgressBar(java.lang.String)
         */
        public void showProgress(String s) {
            super.showProgressBar(s);
        }

        /**
         * @see de.augustakom.hurrican.gui.base.AbstractServicePanel#stopProgressBar()
         */
        public void stopProgress() {
            super.stopProgressBar();
        }
    }

    /*
     * change cursor, statusbar and buttons
     */
    private void modifyGUI(boolean flag, String s) {
        manageButtons(flag);

        if (null != cpsTxStatePanel) {
            if (flag) {
                cpsTxStatePanel.setWaitCursor();
                cpsTxStatePanel.showProgress(s);
            }
            else {
                cpsTxStatePanel.stopProgress();
                cpsTxStatePanel.setDefaultCursor();
            }
        }
    }

    /*
    * enable/disable buttons
    */
    private void manageButtons(boolean flag) {
        if ((null != reloadButton) && (null != cancelButton)) {
            if (flag) {
                reloadButton.setEnabled(Boolean.FALSE);
                cancelButton.setEnabled(Boolean.TRUE);
            }
            else {
                reloadButton.setEnabled(Boolean.TRUE);
                cancelButton.setEnabled(Boolean.FALSE);
            }
        }
    }

    /*
     * Worker - Thread
     */
    class CPSTxStateWorker extends SwingWorker<Void, Pair<String, CPSTxState>> {

        final List<CPSTransactionExt> txListLocal;

        CPSTxStateWorker(List<CPSTransactionExt> cpsTxList) {
            txListLocal = cpsTxList;
        }

        @Override
        protected void process(List<Pair<String, CPSTxState>> chunks) {
            for (Pair<String, CPSTxState> pair : chunks) {
                setTitle(pair.getFirst());
                CPSTxState rowData = pair.getSecond();
                if (rowData != null) {
                    ((AKReflectionTableModel) cpsTxStateTable.getModel()).addObject(rowData);

                    cpsTxStateTable.scrollToRow(cpsTxStateTable.getRowCount() - 1);
                    cpsTxStateTable.setRowSelectionInterval(cpsTxStateTable.getRowCount() - 1, cpsTxStateTable.getRowCount() - 1);
                }
            }
        }

        /**
         * @see javax.swing.SwingWorker#doInBackground()
         */
        @Override
        // Suppress Warnings wegen publish(Pair<_,_>, ...) mit Generics!
        @SuppressWarnings("unchecked")
        protected Void doInBackground() throws Exception {
            if (null != cpsService) {
                for (CPSTransactionExt tx : txListLocal) {

                    if (isCancelled()) {
                        return null;
                    }

                    CPSGetServiceOrderStatusResponseData soData = null;
                    CPSTxState rowData = null;

                    try {
                        Pair<String, CPSTxState> pair = Pair.create(TIT + " - " + SwingFactory.getInstance(RESOURCE).getText(PROGRESSCPS) + " " + tx.getId(), null);
                        publish(pair);
                        soData = cpsService.getStateForCPSTx(tx, HurricanSystemRegistry.instance().getSessionId());
                        if (soData == null) {
                            throw new FindException("CPS-Tx mit Tx-Id " + tx.getId() + " nicht gefunden!");
                        }

                        rowData = new CPSTxState(tx.getId(), soData.getSoStatus(), soData.getSoResult(), soData.getSoResponse());
                        pair = Pair.create(TIT + " - " + SwingFactory.getInstance(RESOURCE).getText(SUCCESS), rowData);

                        publish(pair);
                    }
                    catch (Exception e) {
                        rowData = new CPSTxState(tx.getId(), null, null, e.toString());
                        Pair<String, CPSTxState> pair =
                                Pair.create(TIT + " - " + SwingFactory.getInstance(RESOURCE).getText(ERROR) + tx.getId(), rowData);

                        publish(pair);
                    }
                }
            }
            return null;
        }

        /**
         * @see javax.swing.SwingWorker#done()
         */
        @Override
        protected void done() {
            try {
                get();
            }
            catch (CancellationException e) {
                LOGGER.info(e.getMessage(), e);
            }
            catch (Exception e) {
                setTitle(TIT + " - " + e.getMessage());
                modifyGUI(Boolean.TRUE, SwingFactory.getInstance(RESOURCE).getText(ERROR));
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            }
            finally {
                modifyGUI(Boolean.FALSE, null);
                setTitle(TIT + " - " + SwingFactory.getInstance(RESOURCE).getText(TXCOUNT) + ": " +
                        cpsTxStateTable.getModel().getRowCount());
                reloadButton.requestFocusInWindow();
            }
        }

    }

    /*
    * CPSTxStateMap
    */
    static class CPSTxStateMap extends TreeMap<Number, String> {

        private static CPSTxStateMap cpsTxStateMap = null;

        /*
         * siehe E-Mail von Martin Leichter Mittwoch, 10. Juni 2009 10:55
         *
         * Mögliche Werte für <SO_STATUS/>:
         *
         * - 1 = Unprocessed , 2 = In Process , 3 = Delayed , 5 = Incomplete , 6 = Processed
         *
         * Oder -1 wenn die SO nicht reagiert (Fehler) Im Fall von 6, würde ich dann zusützlich noch die Felder
         * <SO_RESULT/> und <SO_RESPONSE/> befüllen. Darin stehen dann die Antworten , wie sie im Normalfall auch
         * geliefert würden.
         */
        final static Long SOSTATE_ERROR_IDX = Long.valueOf(-1);
        final static Long SO_STATE_NULL_IDX = Long.valueOf(0);
        final static Long SOSTATE_UNPROCESSED_IDX = Long.valueOf(1);
        final static Long SOSTATE_INPROCESS_IDX = Long.valueOf(2);
        final static Long SOSTATE_DELAYED_IDX = Long.valueOf(3);
        final static Long SOSTATE_INCOMPLETE_IDX = Long.valueOf(5);
        final static Long SOSTATE_PROCESSED_IDX = Long.valueOf(6);

        final static String SOSTATE_ERROR = "Error";
        final static String SO_STATE_NULL = "";
        final static String SOSTATE_UNPROCESSED = "Unprocessed";
        final static String SOSTATE_INPROCESS = "In Process";
        final static String SOSTATE_DELAYED = "Delayed";
        final static String SOSTATE_INCOMPLETE = "Incomplete";
        final static String SOSTATE_PROCESSED = "Processed";

        /*
        * Default-Konstruktor
        */
        private CPSTxStateMap() {
            put(SO_STATE_NULL_IDX, SO_STATE_NULL);
            put(SOSTATE_ERROR_IDX, SOSTATE_ERROR);
            put(SOSTATE_UNPROCESSED_IDX, SOSTATE_UNPROCESSED);
            put(SOSTATE_INPROCESS_IDX, SOSTATE_INPROCESS);
            put(SOSTATE_DELAYED_IDX, SOSTATE_DELAYED);
            put(SOSTATE_INCOMPLETE_IDX, SOSTATE_INCOMPLETE);
            put(SOSTATE_PROCESSED_IDX, SOSTATE_PROCESSED);
        }

        /*
        * get an instance of CPSTxStateMap
        */
        public static CPSTxStateMap getInstance() {
            if (null == cpsTxStateMap) {
                cpsTxStateMap = new CPSTxStateMap();
            }
            return cpsTxStateMap;
        }
    }

    public static class CPSTxState {
        Long txId;
        String status;
        Long result;
        String response;

        CPSTxState(Long txId, Long status, Long result, String response) {
            this.txId = txId;
            this.status = CPSTxStateMap.getInstance().get(status == null ? Long.valueOf(0L) : status);
            this.result = result == null ? Long.valueOf(0L) : result;
            this.response = response == null ? "" : response;
        }


        public Long getTxId() {
            return txId;
        }

        public void setTxId(Long txId) {
            this.txId = txId;
        }


        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }


        public Long getResult() {
            return result;
        }

        public void setResult(Long result) {
            this.result = result;
        }


        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }

}
