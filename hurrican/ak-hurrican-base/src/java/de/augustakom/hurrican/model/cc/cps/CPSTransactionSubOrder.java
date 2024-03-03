/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2009 11:31:24
 */
package de.augustakom.hurrican.model.cc.cps;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell-Klasse, ueber die eine Sub-Order zu einer CPS-Tx protokolliert ist. <br> Dies ist dann notwendig, wenn ein
 * Billing-Auftrag ueber mehrere Hurrican-Auftraege realisiert ist und eine gemeinsame Provisionierung der technischen
 * Auftraege benoetigt wird (z.B. bei TK-Anlagen).
 *
 *
 */
public class CPSTransactionSubOrder extends AbstractCCIDModel {

    private Long cpsTxId = null;
    private Long auftragId = null;
    private Long verlaufId = null;

    /**
     * @return the cpsTxId
     */
    public Long getCpsTxId() {
        return cpsTxId;
    }

    /**
     * @param cpsTxId the cpsTxId to set
     */
    public void setCpsTxId(Long cpsTxId) {
        this.cpsTxId = cpsTxId;
    }

    /**
     * @return the auftragId
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId the auftragId to set
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return the verlaufId
     */
    public Long getVerlaufId() {
        return verlaufId;
    }

    /**
     * @param verlaufId the verlaufId to set
     */
    public void setVerlaufId(Long verlaufId) {
        this.verlaufId = verlaufId;
    }

}


