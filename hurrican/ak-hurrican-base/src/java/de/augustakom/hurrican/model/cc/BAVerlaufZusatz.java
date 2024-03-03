/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.2006 09:53:50
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell-Klasse fuer Zusatzeintraege zu einem Bauauftragsverlauf. <br> Ueber dieses Modell kann definiert werden,
 * welche Abteilungen zusaetzlich informiert werden sollen, wenn die Arbeiten an einer best. HVT-Gruppe durchgefuehrt
 * werden.
 *
 *
 */
public class BAVerlaufZusatz extends AbstractCCHistoryUserModel {

    private Long baVerlaufConfigId = null;
    private Long abtId = null;
    private Long hvtGruppeId = null;
    private Boolean auchSelbstmontage = null;
    private Long standortTypRefId;

    /**
     * @return Returns the abtId.
     */
    public Long getAbtId() {
        return this.abtId;
    }

    /**
     * @param abtId The abtId to set.
     */
    public void setAbtId(Long abtId) {
        this.abtId = abtId;
    }

    /**
     * @return Returns the auchSelbstmontage.
     */
    public Boolean getAuchSelbstmontage() {
        return this.auchSelbstmontage;
    }

    /**
     * @param auchSelbstmontage The auchSelbstmontage to set.
     */
    public void setAuchSelbstmontage(Boolean auchSelbstmontage) {
        this.auchSelbstmontage = auchSelbstmontage;
    }

    /**
     * @return Returns the baVerlaufConfigId.
     */
    public Long getBaVerlaufConfigId() {
        return this.baVerlaufConfigId;
    }

    /**
     * @param baVerlaufConfigId The baVerlaufConfigId to set.
     */
    public void setBaVerlaufConfigId(Long baVerlaufConfigId) {
        this.baVerlaufConfigId = baVerlaufConfigId;
    }

    /**
     * @return Returns the hvtGruppeId.
     */
    public Long getHvtGruppeId() {
        return this.hvtGruppeId;
    }

    /**
     * @param hvtGruppeId The hvtGruppeId to set.
     */
    public void setHvtGruppeId(Long hvtGruppeId) {
        this.hvtGruppeId = hvtGruppeId;
    }

    public Long getStandortTypRefId() {
        return standortTypRefId;
    }

    public void setStandortTypRefId(Long standortTypRefId) {
        this.standortTypRefId = standortTypRefId;
    }
}


