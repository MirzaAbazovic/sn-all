/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2004 13:33:14
 */
package de.augustakom.hurrican.model.shared.view;

import java.util.*;
import org.apache.log4j.Logger;


/**
 * View-Modell, um Auftrags- und Endstellen-Daten darzustellen. (Die Informationen werden ueber Billing- und CC-Services
 * zusammengetragen.)
 *
 *
 */
public class AuftragEndstelleView extends DefaultSharedAuftragView {

    private Long produktId;
    private Long endstelleId;
    private String endstelle;
    private String endstelleName;
    private Long endstelleGeoId;
    private String endstelleOrt;
    private String endstelleTyp;
    private Long rangierId;
    private Date vorgabeSCV;
    private String dslamProfile;
    private String ltgArt;

    /**
     * @return Returns the produktId.
     */
    public Long getProduktId() {
        return produktId;
    }

    /**
     * @param produktId The produktId to set.
     */
    public void setProduktId(Long produktId) {
        this.produktId = produktId;
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
     * @return Returns the endstelle.
     */
    public String getEndstelle() {
        return endstelle;
    }

    /**
     * @param endstelle The endstelle to set.
     */
    public void setEndstelle(String endstelle) {
        this.endstelle = endstelle;
    }

    /**
     * @return Returns the endstelleName.
     */
    public String getEndstelleName() {
        return endstelleName;
    }

    /**
     * @param endstelleName The endstelleName to set.
     */
    public void setEndstelleName(String endstelleName) {
        this.endstelleName = endstelleName;
    }

    /**
     * @return Returns the endstelleTyp.
     */
    public String getEndstelleTyp() {
        return endstelleTyp;
    }

    /**
     * @param endstelleTyp The endstelleTyp to set.
     */
    public void setEndstelleTyp(String endstelleTyp) {
        this.endstelleTyp = endstelleTyp;
    }

    /**
     * @return Returns the endstelleOrt.
     */
    public String getEndstelleOrt() {
        return endstelleOrt;
    }

    /**
     * @param endstelleOrt The endstelleOrt to set.
     */
    public void setEndstelleOrt(String endstelleOrt) {
        this.endstelleOrt = endstelleOrt;
    }

    /**
     * @return Returns the rangierId.
     */
    public Long getRangierId() {
        return rangierId;
    }

    /**
     * @param rangierId The rangierId to set.
     */
    public void setRangierId(Long rangierId) {
        this.rangierId = rangierId;
    }

    /**
     * @return Returns the vorgabeSCV.
     */
    public Date getVorgabeSCV() {
        return vorgabeSCV;
    }

    /**
     * @param vorgabeSCV The vorgabeSCV to set.
     */
    public void setVorgabeSCV(Date vorgabeSCV) {
        this.vorgabeSCV = vorgabeSCV;
    }

    /**
     * @return Returns the dslamProfile.
     */
    public String getDslamProfile() {
        return dslamProfile;
    }

    /**
     * @param dslamProfile The dslamProfile to set.
     */
    public void setDslamProfile(String dslamProfile) {
        this.dslamProfile = dslamProfile;
    }


    /**
     * @return Returns the ltgArt.
     */
    public String getLtgArt() {
        return ltgArt;
    }


    /**
     * @param ltgArt The ltgArt to set.
     */
    public void setLtgArt(String ltgArt) {
        this.ltgArt = ltgArt;
    }


    public Long getEndstelleGeoId() {
        return endstelleGeoId;
    }

    public void setEndstelleGeoId(Long endstelleGeoId) {
        this.endstelleGeoId = endstelleGeoId;
    }


    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            super.debugModel(logger);
            logger.debug("  Endstelle     : " + getEndstelle());
            logger.debug("  Endstelle-Typ : " + getEndstelleTyp());
            logger.debug("  Inbetriebnahme: " + getInbetriebnahme());
            logger.debug("  Kuendigung    : " + getKuendigung());
        }
    }

}


