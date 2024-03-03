/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2004 12:54:27
 */
package de.augustakom.hurrican.model.cc;

import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;


/**
 * Modell enthaelt Informationen ueber einen Ansprechpartner fuer eine Endstelle.
 *
 *
 */
public class EndstelleAnsprechpartner extends AbstractCCHistoryModel implements DebugModel {

    private Long endstelleId = null;
    private String ansprechpartner = null;

    /**
     * @return Returns the ansprechpartner.
     */
    public String getAnsprechpartner() {
        return ansprechpartner;
    }

    /**
     * @param ansprechpartner The ansprechpartner to set.
     */
    public void setAnsprechpartner(String ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
    }

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
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + EndstelleAnsprechpartner.class.getName());
            logger.debug("  ID             : " + getId());
            logger.debug("  Endstellen-ID  : " + getEndstelleId());
            logger.debug("  Ansprechpartner: " + getAnsprechpartner());
        }
    }

}


