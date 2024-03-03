/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2005 13:14:07
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;


/**
 * Modell zur Protokollierung der Stift-Vergabe von HVT-Bestellungen.
 *
 *
 */
public class HVTBestellHistory extends AbstractCCIDModel {

    private Long bestellId = null;
    private String leiste = null;
    private Integer anzahl = null;
    private String bearbeiter = null;
    private Date datum = null;

    /**
     * @return Returns the anzahl.
     */
    public Integer getAnzahl() {
        return anzahl;
    }

    /**
     * @param anzahl The anzahl to set.
     */
    public void setAnzahl(Integer anzahl) {
        this.anzahl = anzahl;
    }

    /**
     * @return Returns the bearbeiter.
     */
    public String getBearbeiter() {
        return bearbeiter;
    }

    /**
     * @param bearbeiter The bearbeiter to set.
     */
    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    /**
     * @return Returns the bestellId.
     */
    public Long getBestellId() {
        return bestellId;
    }

    /**
     * @param bestellId The bestellId to set.
     */
    public void setBestellId(Long bestellId) {
        this.bestellId = bestellId;
    }

    /**
     * @return Returns the datum.
     */
    public Date getDatum() {
        return datum;
    }

    /**
     * @param datum The datum to set.
     */
    public void setDatum(Date datum) {
        this.datum = datum;
    }

    /**
     * @return Returns the leiste.
     */
    public String getLeiste() {
        return leiste;
    }

    /**
     * @param leiste The leiste to set.
     */
    public void setLeiste(String leiste) {
        this.leiste = leiste;
    }

}


