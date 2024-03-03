/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionLog;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionSubOrder;

/**
 *
 */
public class CPSTxController {

    /**
     * Write data from model to view
     *
     * @param tx
     * @param cpsTxFieldPanel
     * @param cpTxRequestPanel
     * @param cpsTxResponsePanel
     * @param cpsTxLogPanel
     * @param cpsTxOrderPanel
     */
    public static void writeDataFromModelToView(CPSTransactionExt tx,
            CPSTxFieldPanel cpsTxFieldPanel, CPSTxRequestPanel cpsTxRequestPanel,
            CPSTxResponsePanel cpsTxResponsePanel, CPSTxLogPanel cpsTxLogPanel,
            CPSTxOrderPanel cpsTxOrderPanel) {

        cpsTxFieldPanel.getTfId().setText((tx.getId() != null) ? tx.getId().toString() : "");
        cpsTxFieldPanel.getTfTaifunOrderNo().setText((tx.getOrderNoOrig() != null) ? tx.getOrderNoOrig().toString() : "");
        cpsTxFieldPanel.getTfAuftragId().setText((tx.getAuftragId() != null) ? tx.getAuftragId().toString() : "");
        cpsTxFieldPanel.getTfVerlaufId().setText((tx.getVerlaufId() != null) ? tx.getVerlaufId().toString() : "");
        cpsTxFieldPanel.getTfTxState().setText((tx.getStatus() != null) ? tx.getStatus().toString() : "");
        cpsTxFieldPanel.getTfTxSource().setText((tx.getSource() != null) ? tx.getSource().toString() : "");
        cpsTxFieldPanel.getTfServiceOrderType().setText((tx.getSoType() != null) ? tx.getSoType().toString() : "");
        cpsTxFieldPanel.getTfServiceOrderPrio().setText((tx.getPrio() != null) ? tx.getPrio().toString() : "");
        cpsTxFieldPanel.getTfSoStackId().setText((tx.getServiceOrderStackId() != null) ? tx.getServiceOrderStackId().toString() : "");
        cpsTxFieldPanel.getTfSoStackSeq().setText((tx.getServiceOrderStackSeq() != null) ? tx.getServiceOrderStackSeq().toString() : "");
        cpsTxFieldPanel.getTfRegion().setText((null != tx.getNl()) ? tx.getNl().toString() : "");
        cpsTxFieldPanel.getDcRequestAt().setDate((tx.getRequestAt() != null) ? tx.getRequestAt() : null);
        cpsTxFieldPanel.getDcResponseAt().setDate((tx.getResponseAt() != null) ? tx.getResponseAt() : null);
        cpsTxFieldPanel.getDcEstimatedExecTime().setDate((tx.getEstimatedExecTime() != null) ? tx.getEstimatedExecTime() : null);
        cpsTxFieldPanel.getDcTimest().setDate((tx.getTimestamp() != null) ? new Date(tx.getTimestamp().getTime()) : null);
        cpsTxFieldPanel.getTfTxUser().setText((tx.getTxUser() != null) ? tx.getTxUser() : "");
        cpsTxFieldPanel.getTfUserW().setText((tx.getUserW() != null) ? tx.getUserW() : "");
        cpsTxRequestPanel.getSoDataTextPane().setText((tx.getServiceOrderData() != null) ? new String(tx.getServiceOrderData(), StringTools.CC_DEFAULT_CHARSET) : "");
        cpsTxRequestPanel.getRequestDataTextPane().setText((tx.getRequestData() != null) ? new String(tx.getRequestData(), StringTools.CC_DEFAULT_CHARSET) : "");
        cpsTxResponsePanel.getResponseTextPane().setText((tx.getResponseData() != null) ? new String(tx.getResponseData(), StringTools.CC_DEFAULT_CHARSET) : "");

        @SuppressWarnings("unchecked")
        AKTableSorter<CPSTransactionLog> sorter = (AKTableSorter<CPSTransactionLog>) cpsTxLogPanel.getLogTable().getModel();
        @SuppressWarnings("unchecked")
        AKTableModelXML<CPSTransactionLog> tableModel = (AKTableModelXML<CPSTransactionLog>) sorter.getModel();
        tableModel.setData(tx.getCpsTxLog());

        if (cpsTxLogPanel.getLogTable().getRowCount() > 0) {
            cpsTxLogPanel.getLogTable().changeSelection(0, 0, Boolean.FALSE, Boolean.FALSE);
            cpsTxLogPanel.getTableListener().selectionChanged(cpsTxLogPanel.getLogTable());
        }

        @SuppressWarnings("unchecked")
        AKTableSorter<CPSTransactionSubOrder> orderSorter = (AKTableSorter<CPSTransactionSubOrder>) cpsTxOrderPanel.getOrderTable().getModel();
        @SuppressWarnings("unchecked")
        AKTableModelXML<CPSTransactionSubOrder> orderTableModel = (AKTableModelXML<CPSTransactionSubOrder>) orderSorter.getModel();
        orderTableModel.setData(tx.getCpsTxSubOrder());
    }
}
