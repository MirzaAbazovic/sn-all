/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2004 11:01:27
 */
package de.augustakom.hurrican.model.cc;

import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;


/**
 * Modell enthaelt Informationen ueber eine Inhouse-Verkabelung.
 *
 *
 */
public class Inhouse extends AbstractCCHistoryModel implements DebugModel {

    private Long endstelleId = null;
    private String raumnummer = null;
    private String verkabelung = null;
    private String ansprechpartner = null;
    private String bemerkung = null;

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
     * @return Returns the bemerkung.
     */
    public String getBemerkung() {
        return bemerkung;
    }

    /**
     * @param bemerkung The bemerkung to set.
     */
    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
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
     * @return Returns the raumnummer.
     */
    public String getRaumnummer() {
        return raumnummer;
    }

    /**
     * @param raumnummer The raumnummer to set.
     */
    public void setRaumnummer(String raumnummer) {
        this.raumnummer = raumnummer;
    }

    /**
     * @return Returns the verkabelung.
     */
    public String getVerkabelung() {
        return verkabelung;
    }

    /**
     * @param verkabelung The verkabelung to set.
     */
    public void setVerkabelung(String verkabelung) {
        this.verkabelung = verkabelung;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + Inhouse.class.getName());
            logger.debug("  ID         : " + getId());
            logger.debug("  Raumnummer : " + getRaumnummer());
            logger.debug("  Verkabelung: " + getVerkabelung());
            logger.debug("  Ansprechp. : " + getAnsprechpartner());
        }
    }
}


