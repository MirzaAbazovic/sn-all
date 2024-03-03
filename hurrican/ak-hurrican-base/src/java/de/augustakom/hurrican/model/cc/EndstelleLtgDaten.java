/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2004 12:00:04
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell enthaelt Leitungsdaten zu einer Endstelle.
 *
 *
 */
public class EndstelleLtgDaten extends AbstractCCHistoryModel {

    private Long endstelleId = null;
    private Long leitungsartId = null;
    private Long schnittstelleId = null;


    /**
     * @return Returns the endstelleId.
     */
    public Long getEndstelleId() {
        return endstelleId;
    }

    /**
     * @param endstelleId The endstelleId to set.
     */
    public void setEndstelleId(Long endstelleId) {
        this.endstelleId = endstelleId;
    }

    /**
     * @return Returns the schnittstelleId.
     */
    public Long getSchnittstelleId() {
        return schnittstelleId;
    }

    /**
     * @param schnittstelleId The schnittstelleId to set.
     */
    public void setSchnittstelleId(Long schnittstelleId) {
        this.schnittstelleId = schnittstelleId;
    }

    /**
     * @return Returns the leitungsartId.
     */
    public Long getLeitungsartId() {
        return leitungsartId;
    }

    /**
     * @param leitungsartId The leitungsartId to set.
     */
    public void setLeitungsartId(Long leitungsart) {
        this.leitungsartId = leitungsart;
    }
}


