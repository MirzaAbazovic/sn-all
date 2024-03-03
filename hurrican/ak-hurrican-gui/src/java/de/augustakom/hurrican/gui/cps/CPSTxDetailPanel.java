/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.service.cc.CPSService;

/**
 * Panel fuer die Anzeige/Bearbeitung der CPS-Transaktionen
 *
 *
 */
public class CPSTxDetailPanel extends AbstractServicePanel implements CPSTxObserver, CPSTxObservable {

    private static final Logger LOGGER = Logger.getLogger(CPSTxDetailPanel.class);
    private static final String RESOURCE = "de/augustakom/hurrican/gui/cps/resources/CPSTransactionPanel.xml";

    private static final String STATE = "button.state";
    private static final String CANCEL = "button.cancel";
    private static final String RESEND = "button.resend";
    private static final String CLOSE = "button.close";

    private static final String DETAILS = "details";
    private static final String FUNCTIONS = "functions";

    public static final String FIELDSDETAILS = "fieldsDetails";
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";
    public static final String LOG = "log";
    public static final String ORDERS = "orders";

    // GUI-Komponenten
    private CPSTxButtonPanel buttonPanel = null;
    private JPanel tabPanel = null;
    private CPSTxTabbedPane tabbedPane = null;

    private Vector<CPSTransactionExt> selectedTxList = null;

    private final List<CPSTxObserver> observers = new ArrayList<CPSTxObserver>();

    /**
     * Default-Konstruktor
     */
    public CPSTxDetailPanel() {
        super(RESOURCE);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        createTabPanel();
        buttonPanel = new CPSTxButtonPanel();

        setLayout(new GridBagLayout());
        add(buttonPanel, GBCFactory.createGBC(10, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        add(tabPanel, GBCFactory.createGBC(90, 100, 1, 0, 1, 1, GridBagConstraints.BOTH));
    }

    /*
     * Erzeugt das Tab-Panel
     */
    private void createTabPanel() {
        tabbedPane = new CPSTxTabbedPane();
        tabPanel = new JPanel();
        tabPanel.setLayout(new GridBagLayout());
        tabPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText(DETAILS)));
        tabPanel.add(tabbedPane, GBCFactory.createGBC(50, 100, 0, 0, 0, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        try {
            CPSService cpsService = getCCService(CPSService.class);
            Vector<CPSTransactionExt> selectedTxList = buttonPanel.getSelectedTxList();

            if (STATE.equals(command)) {
                CPSTxStateDialog cpsTxStateDialog = new CPSTxStateDialog(cpsService, selectedTxList, Boolean.FALSE);
                DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), cpsTxStateDialog, Boolean.TRUE, Boolean.TRUE);
            }
            else {
                for (CPSTransactionExt tx : selectedTxList) {
                    if (CANCEL.equals(command)) {
                        cpsService.cancelCPSTransaction(tx.getId(), HurricanSystemRegistry.instance().getSessionId());
                    }
                    else if (RESEND.equals(command)) {
                        cpsService.resendCPSTx(tx, HurricanSystemRegistry.instance().getSessionId());
                    }
                    else if (CLOSE.equals(command)) {
                        cpsService.closeCPSTx(tx.getId(), HurricanSystemRegistry.instance().getSessionId());
                    }
                }
                notifyObservers();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @return the fieldPanel
     */
    public CPSTxFieldPanel getFieldPanel() {
        return tabbedPane.getFieldPanel();
    }

    /**
     * @return the requestPanel
     */
    public CPSTxRequestPanel getRequestPanel() {
        return tabbedPane.getRequestPanel();
    }

    /**
     * @return the responsePanel
     */
    public CPSTxResponsePanel getResponsePanel() {
        return tabbedPane.getResponsePanel();
    }

    /**
     * @return the logPanel
     */
    public CPSTxLogPanel getLogPanel() {
        return tabbedPane.getLogPanel();
    }

    /**
     * @return the orderPanel
     */
    public CPSTxOrderPanel getOderPanel() {
        return tabbedPane.getOrderPanel();
    }

    /**
     * @return the buttonPanel
     */
    public CPSTxButtonPanel getButtonPanel() {
        return buttonPanel;
    }

    /**
     * @return the tabbedPane
     */
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    /**
     * @see de.augustakom.hurrican.gui.cps.CPSTxObserver#update(de.augustakom.hurrican.gui.cps.CPSTxObservable)
     */
    @Override
    public void update(CPSTxObservable observable) {
        if ((null != observable) && (observable instanceof CPSTxTable)) {
            CPSTxTable cpsTxTable = (CPSTxTable) observable;

            if (cpsTxTable.getSelectedRows().length > 0) {
                selectedTxList = new Vector<CPSTransactionExt>();

                for (int i = 0; i < cpsTxTable.getSelectedRows().length; i++) {
                    @SuppressWarnings("unchecked")
                    AKTableSorter<CPSTransactionExt> sorter = (AKTableSorter<CPSTransactionExt>) cpsTxTable
                            .getModel();
                    Object val = sorter.getDataAtRow(cpsTxTable.getSelectedRows()[i]);

                    if (!selectedTxList.contains(val) && (null != val)
                            && (val instanceof CPSTransactionExt)) {
                        selectedTxList.add((CPSTransactionExt) val);
                    }
                }
            }
            else {
                selectedTxList = null;
            }
            LOGGER.debug(selectedTxList);
        }
    }

    /*
     * Button-Panel
     */
    class CPSTxButtonPanel extends JPanel implements CPSTxObserver {
        private AKJButton btnState = null;
        private AKJButton btnCancelTx = null;
        private AKJButton btnReSend = null;
        private AKJButton btnCloseTx = null;

        /*
         * Default-Konstruktor
         */
        public CPSTxButtonPanel() {
            this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), getSwingFactory().getText(FUNCTIONS)));
            this.setLayout(new GridBagLayout());

            btnState = getSwingFactory().createButton(STATE, getActionListener());
            btnCancelTx = getSwingFactory().createButton(CANCEL, getActionListener());
            btnReSend = getSwingFactory().createButton(RESEND, getActionListener());
            btnCloseTx = getSwingFactory().createButton(CLOSE, getActionListener());

            this.add(btnState, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            this.add(btnCancelTx, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            this.add(btnReSend, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            this.add(btnCloseTx, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
            this.add(new JPanel(), GBCFactory.createGBC(0, 100, 0, 4, 1, 1, GridBagConstraints.VERTICAL));

            manageGUI(btnState, btnCancelTx, btnReSend, btnCloseTx);
        }

        /**
         * @return the btnState
         */
        public JButton getBtnState() {
            return btnState;
        }

        /**
         * @return the btnCancelTx
         */
        public JButton getBtnCancelTx() {
            return btnCancelTx;
        }

        /**
         * @return the btnReSend
         */
        public JButton getBtnReSend() {
            return btnReSend;
        }

        /**
         * @return the btnCloseTx
         */
        public JButton getBtnCloseTx() {
            return btnCloseTx;
        }

        /* Setzt die Buttons auf enabled/disabled */
        public void enableButtons(boolean enable, boolean allButtons) {
            getBtnState().setEnabled(enable);
            getBtnCancelTx().setEnabled(enable);
            getBtnReSend().setEnabled(enable);
            getBtnCloseTx().setEnabled(enable);
        }

        public void manageButtons(Long txState) {
            btnReSend.setEnabled(isReSendCandidate(txState));
            btnCloseTx.setEnabled(isCloseCandidate(txState));
        }

        private boolean isReSendCandidate(Long txState) {
            return (CPSTransactionExt.TX_STATE_TRANSMISSION_FAILURE.equals(txState) || CPSTransactionExt.TX_STATE_IN_PREPARING
                    .equals(txState));
        }

        private boolean isCloseCandidate(Long txState) {
            return CPSTransactionExt.TX_STATE_FAILURE.equals(txState) ||
                    CPSTransactionExt.TX_STATE_FAILURE_CLOSED.equals(txState) ||
                    CPSTransactionExt.TX_STATE_TRANSMISSION_FAILURE.equals(txState) ||
                    CPSTransactionExt.TX_STATE_CANCELLED.equals(txState) ||
                    CPSTransactionExt.TX_STATE_IN_PREPARING_FAILURE.equals(txState);
        }

        /**
         * @see de.augustakom.hurrican.gui.cps.CPSTxObserver#update(de.augustakom.hurrican.gui.cps.CPSTxObservable)
         */
        @Override
        public void update(CPSTxObservable observable) {
            if (observable instanceof CPSTxTable) {
                CPSTxTable cpsTxTable = (CPSTxTable) observable;
                enableButtons(!(cpsTxTable.getSelectedRow() == -1), Boolean.TRUE);
            }
        }

        /**
         * @return the selectedTxIdList
         */
        public Vector<CPSTransactionExt> getSelectedTxList() {
            return selectedTxList;
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.cps.CPSTxObservable#addObserver(de.augustakom.hurrican.gui.cps.CPSTxObserver)
     */
    @Override
    public void addObserver(CPSTxObserver observer) {
        observers.add(observer);
    }

    /**
     * @see de.augustakom.hurrican.gui.cps.CPSTxObservable#removeObserver(de.augustakom.hurrican.gui.cps.CPSTxObserver)
     */
    @Override
    public void removeObserver(CPSTxObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        Iterator<CPSTxObserver> observerIterator = observers.iterator();

        while (observerIterator.hasNext()) {
            CPSTxObserver observer = observerIterator.next();
            observer.update(this);
        }
    }
}
