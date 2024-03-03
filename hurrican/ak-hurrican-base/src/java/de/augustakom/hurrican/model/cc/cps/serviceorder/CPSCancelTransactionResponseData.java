/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.06.2009 11:43:39
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * Modell-Klasse fuer die SO-Data, die bei einer Status-Abfrage der CPS-Transaktion geliefert wird.
 *
 *
 */
@XStreamAlias("CANCEL_TRANSACTION_RESPONSE")
public class CPSCancelTransactionResponseData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("CANCEL_ACCEPTED")
    private Long cancelAccepted = null;
    @XStreamAlias("MESSAGE")
    private String message = null;

    /**
     * @return Returns the cancelAccepted.
     */
    public Long getCancelAccepted() {
        return cancelAccepted;
    }

    /**
     * @param cancelAccepted The cancelAccepted to set.
     */
    public void setCancelAccepted(Long cancelAccepted) {
        this.cancelAccepted = cancelAccepted;
    }

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
