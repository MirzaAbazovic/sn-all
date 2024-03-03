/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2010 11:03:15
 */
package de.augustakom.hurrican.model.cc.temp;

import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 * Temp. Modell, um Basis-Optionen zu definieren, die fuer "Revoke" Operationen benoetigt werden.
 *
 *
 */
public abstract class AbstractRevokeModel extends AbstractCCModel {

    private Long auftragId = null;
    private Long sessionId = null;
    private Long cpsTxServiceOrderType = null;
    private Long auftragsArtId = null;

    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getCpsTxServiceOrderType() {
        return cpsTxServiceOrderType;
    }

    public void setCpsTxServiceOrderType(Long cpsTxServiceOrderType) {
        this.cpsTxServiceOrderType = cpsTxServiceOrderType;
    }

    public Long getAuftragsArtId() {
        return auftragsArtId;
    }

    public void setAuftragsArtId(Long auftragsArtId) {
        this.auftragsArtId = auftragsArtId;
    }

}


