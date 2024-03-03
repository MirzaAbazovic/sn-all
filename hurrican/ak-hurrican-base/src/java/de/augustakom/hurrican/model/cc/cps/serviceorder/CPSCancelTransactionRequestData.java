/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.06.2009 11:32:34
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * Modell-Klasse, um eine eingestellte CPS-Tx zu stornieren..
 *
 *
 */
@XStreamAlias("CANCEL_TRANSACTION")
public class CPSCancelTransactionRequestData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("TRANSACTION_ID")
    private Long transactionId = null;

    /**
     * @return the transactionId
     */
    public Long getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

}


