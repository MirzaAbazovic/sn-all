/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.06.2007 08:04:13
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Modell beinhaltet VoIP-spezifische Daten fuer einen Auftrag.
 *
 *
 */
public class AuftragVoIP extends AbstractCCHistoryUserModel implements CCAuftragModel {

    /**
     * Konstante definiert die notwendige Laenge vom SIP-Passwort.
     */
    public static final int PASSWORD_LENGTH = 6;

    private Long auftragId = null;
    private Long egMode = null;
    private Boolean isActive = null;

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCAuftragModel#getAuftragId()
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCAuftragModel#setAuftragId(java.lang.Integer)
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the egMode.
     */
    public Long getEgMode() {
        return egMode;
    }

    /**
     * @param egMode The egMode to set.
     */
    public void setEgMode(Long egMode) {
        this.egMode = egMode;
    }

    /**
     * @return Returns the isActive.
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @param isActive The isActive to set.
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}


