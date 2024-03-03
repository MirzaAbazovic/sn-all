/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2007 17:09:56
 */
package de.augustakom.hurrican.model.cc.tal;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell bildet die zur Konfiguration benötigten Daten ab
 *
 *
 */
public class CbConfigData extends AbstractCCIDModel {

    private Long cbConfigId = null;
    private String bezeichnung = null;
    private String wertString = null;
    private String beschreibung = null;
    private String matchcode = null;

    /**
     * @return Returns the beschreibung.
     */
    public String getBeschreibung() {
        return beschreibung;
    }

    /**
     * @param beschreibung The beschreibung to set.
     */
    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    /**
     * @return Returns the bezeichnung.
     */
    public String getBezeichnung() {
        return bezeichnung;
    }

    /**
     * @param bezeichnung The bezeichnung to set.
     */
    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    /**
     * @return Returns the cbConfigId.
     */
    public Long getCbConfigId() {
        return cbConfigId;
    }

    /**
     * @param cbConfigId The cbConfigId to set.
     */
    public void setCbConfigId(Long cbConfigId) {
        this.cbConfigId = cbConfigId;
    }

    /**
     * @return Returns the matchcode.
     */
    public String getMatchcode() {
        return matchcode;
    }

    /**
     * @param matchcode The matchcode to set.
     */
    public void setMatchcode(String matchcode) {
        this.matchcode = matchcode;
    }

    /**
     * @return Returns the wertString.
     */
    public String getWertString() {
        return wertString;
    }

    /**
     * @param wertString The wertString to set.
     */
    public void setWertString(String wertString) {
        this.wertString = wertString;
    }


}


