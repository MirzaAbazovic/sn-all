/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2004 14:14:14
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Modell enthaelt Konfigurationsdaten fuer einen VPN-Anschluss.
 *
 *
 */
public class VPNKonfiguration extends AbstractCCHistoryModel implements CCAuftragModel {

    private Boolean kanalbuendelung = null;
    private Short anzahlKanaele = null;
    private Boolean dialOut = null;
    private Long auftragId = null;
    private Long physAuftragId = null;
    private String vplsId = null;

    /**
     * @return Returns the anzahlKanaele.
     */
    public Short getAnzahlKanaele() {
        return anzahlKanaele;
    }

    /**
     * @param anzahlKanaele The anzahlKanaele to set.
     */
    public void setAnzahlKanaele(Short anzahlKanaele) {
        this.anzahlKanaele = anzahlKanaele;
    }

    /**
     * @return Returns the auftragId.
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the dialOut.
     */
    public Boolean getDialOut() {
        return dialOut;
    }

    /**
     * @param dialOut The dialOut to set.
     */
    public void setDialOut(Boolean dialOut) {
        this.dialOut = dialOut;
    }

    /**
     * @return Returns the kanalbuendelung.
     */
    public Boolean getKanalbuendelung() {
        return kanalbuendelung;
    }

    /**
     * @return Returns the kanalbuendelung as boolean.
     */
    public boolean hasKanalbuendelung() {
        return (kanalbuendelung != null) ? kanalbuendelung.booleanValue() : false;
    }

    /**
     * @param kanalbuendelung The kanalbuendelung to set.
     */
    public void setKanalbuendelung(Boolean kanalbuendelung) {
        this.kanalbuendelung = kanalbuendelung;
    }

    /**
     * @param kanalbuendelung The kanalbuendelung to set.
     */
    public void setKanalbuendelung(boolean kanalbuendelung) {
        this.kanalbuendelung = Boolean.valueOf(kanalbuendelung);
    }

    /**
     * @return Returns the physAuftragId.
     */
    public Long getPhysAuftragId() {
        return physAuftragId;
    }

    /**
     * @param physAuftragId The physAuftragId to set.
     */
    public void setPhysAuftragId(Long physAuftragId) {
        this.physAuftragId = physAuftragId;
    }

    /**
     * @return Returns the vplsId.
     */
    public String getVplsId() {
        return vplsId;
    }

    /**
     * @param vplsId The vplsId to set.
     */
    public void setVplsId(String vplsId) {
        this.vplsId = vplsId;
    }
}


