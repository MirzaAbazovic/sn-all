/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2009 12:20:43
 */
package de.augustakom.hurrican.model.cc.cps;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell zum Logging von Messages (Warnungen / Fehler), die waehrend der Provisionierung auftreten.
 *
 *
 */
public class CPSTransactionLog extends AbstractCCIDModel {

    /**
     * Maximale Laenge fuer den Parameter 'message'
     */
    private static final int MAX_MESSAGE_LENGHT = 3999;

    private Long cpsTxId = null;
    private String message = null;

    /**
     * Default-Const.
     */
    public CPSTransactionLog() {
    }

    /**
     * @param cpsTxId
     * @param message
     */
    public CPSTransactionLog(Long cpsTxId, String message) {
        setCpsTxId(cpsTxId);
        setMessage(message);
    }

    /**
     * @return Returns the cpsTxId.
     */
    public Long getCpsTxId() {
        return cpsTxId;
    }

    /**
     * @param cpsTxId The cpsTxId to set.
     */
    public void setCpsTxId(Long cpsTxId) {
        this.cpsTxId = cpsTxId;
    }

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Angabe der zu protokollierenden Message. <br> Der String wird auf MAX_MESSAGE_LENGHT Zeichen abgeschnitten!
     *
     * @param message The message to set.
     */
    public void setMessage(String message) {
        this.message = StringUtils.abbreviate(message, MAX_MESSAGE_LENGHT);
    }

}


