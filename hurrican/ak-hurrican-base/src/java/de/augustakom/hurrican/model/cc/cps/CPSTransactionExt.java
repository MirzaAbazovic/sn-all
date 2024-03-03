/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2009 14:13:50
 */
package de.augustakom.hurrican.model.cc.cps;

import java.util.*;

import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Reference;

/**
 * Erweiterung der CPSTransaction
 *
 *
 */
public class CPSTransactionExt extends CPSTransaction {

    public static final String CUST_NO = "custNo";
    public static final String ESTIMATED_EXEC_TIME = "estimatedExecTime";
    public static final String ID = "id";
    public static final String NL = "nl";
    public static final String ORDER_NO_ORIG = "orderNoOrig";
    public static final String REGION = "region";
    public static final String REQUEST_AT = "requestAt";
    public static final String RESPONSE_AT = "responseAt";
    public static final String REQUEST_DATA = "requestData";
    public static final String RESPONSE_DATA = "responseData";
    public static final String SERVICE_ORDER_DATA = "serviceOrderData";
    public static final String SERVICE_ORDER_PRIO = "serviceOrderPrio";
    public static final String SERVICE_ORDER_STACK_ID = "serviceOrderStackId";
    public static final String SERVICE_ORDER_STACK_SEQ = "serviceOrderStackSeq";
    public static final String SERVICE_ORDER_TYPE = "serviceOrderType";
    public static final String SOTYPE = "soType";
    public static final String STATUS = "status";
    public static final String TX_SOURCE = "txSource";
    public static final String TX_STATE = "txState";
    public static final String TX_USER = "txUser";
    public static final String USER_W = "userW";
    public static final String VERLAUF_ID = "verlaufId";
    public static final String HW_RACK_ID = "hwRackId";

    private Auftrag auftrag = null;
    private Long custNo = null;
    private Date timestamp = null;

    private Reference soTypeReference = null;
    private String soType = null;

    private Reference statusReference = null;
    private String status = null;

    private Reference txSourceReference = null;
    private String source = null;

    private Niederlassung niederlassung = null;
    private String nl = null;

    private Reference soPrioReference = null;
    private String prio = null;

    private Set<CPSTransactionLog> cpsTxLog = new HashSet<CPSTransactionLog>();
    private Set<CPSTransactionSubOrder> cpsTxSubOrder = new HashSet<CPSTransactionSubOrder>();

    /**
     * @return the cpsTxSubOrder
     */
    public Set<CPSTransactionSubOrder> getCpsTxSubOrder() {
        return cpsTxSubOrder;
    }

    /**
     * @param cpsTxSubOrder the cpsTxSubOrder to set
     */
    public void setCpsTxSubOrder(Set<CPSTransactionSubOrder> cpsTxSubOrder) {
        this.cpsTxSubOrder = cpsTxSubOrder;
    }

    /**
     * @return the cpsTxLog
     */
    public Set<CPSTransactionLog> getCpsTxLog() {
        return cpsTxLog;
    }

    /**
     * @param cpsTxLog the cpsTxLog to set
     */
    public void setCpsTxLog(Set<CPSTransactionLog> cpsTxLog) {
        this.cpsTxLog = cpsTxLog;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the niederlassung
     */
    public Niederlassung getNiederlassung() {
        return niederlassung;
    }

    /**
     * @return the nl
     */
    public String getNl() {
        return nl;
    }

    /**
     * @return the custNo
     */
    public Long getCustNo() {
        return custNo;
    }

    /**
     * @return the auftrag
     */
    public Auftrag getAuftrag() {
        return auftrag;
    }

    /**
     * @return the soTypeReference
     */
    public Reference getSoTypeReference() {
        return soTypeReference;
    }

    /**
     * @return the soType
     */
    public String getSoType() {
        return soType;
    }

    /**
     * @return the statusReference
     */
    public Reference getStatusReference() {
        return statusReference;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the txSourceReference
     */
    public Reference getTxSourceReference() {
        return txSourceReference;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param auftrag the auftrag to set
     */
    public void setAuftrag(Auftrag auftrag) {
        this.auftrag = auftrag;

        if (null != this.auftrag) {
            this.custNo = this.auftrag.getKundeNo();
        }
    }

    /**
     * @param niederlassung the niederlassung to set
     */
    public void setNiederlassung(Niederlassung niederlassung) {
        this.niederlassung = niederlassung;

        if (null != niederlassung) {
            this.nl = this.niederlassung.getName();
        }
    }

    /**
     * @param statusReference the statusReference to set
     */
    public void setStatusReference(Reference statusReference) {
        this.statusReference = statusReference;

        if (null != this.statusReference) {
            this.status = this.statusReference.getStrValue();
        }
    }

    /**
     * @param soTypeReference the soTypeReference to set
     */
    public void setSoTypeReference(Reference soTypeReference) {
        this.soTypeReference = soTypeReference;

        if (null != this.soTypeReference) {
            this.soType = this.soTypeReference.getStrValue();
        }
    }

    /**
     * @param txSourceReference the txSourceReference to set
     */
    public void setTxSourceReference(Reference txSourceReference) {
        this.txSourceReference = txSourceReference;

        if (null != txSourceReference) {
            this.source = txSourceReference.getStrValue();
        }
    }


    /**
     * @return the soPrioReference
     */
    public Reference getSoPrioReference() {
        return soPrioReference;
    }

    /**
     * @param soPrioReference the soPrioReference to set
     */
    public void setSoPrioReference(Reference soPrioReference) {
        this.soPrioReference = soPrioReference;

        if (null != soPrioReference) {
            this.prio = soPrioReference.getStrValue();
        }
    }

    /**
     * @return the prio
     */
    public String getPrio() {
        return prio;
    }

    /**
     * @param prio the prio to set
     */
    public void setPrio(String prio) {
        this.prio = prio;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getId());
        return builder.toString();
    }
}
