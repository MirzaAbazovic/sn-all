/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.2005 11:22:02
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;


/**
 * Verlaufs-View fuer die Ruecklaeufer AM.
 *
 *
 */
public class VerlaufAmRlView extends AbstractBauauftragView {

    private String bearbeiter = null;
    private String bearbeiterAm = null;
    private Integer buendelNr = null;
    private Date inbetriebnahme = null;
    private String hvtAnschlussart = null;
    private Long vpnNr = null;

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
     * @return Returns the buendelNr.
     */
    public Integer getBuendelNr() {
        return buendelNr;
    }

    /**
     * @param buendelNr The buendelNr to set.
     */
    public void setBuendelNr(Integer buendelNr) {
        this.buendelNr = buendelNr;
    }

    /**
     * @return Returns the inbetriebnahme.
     */
    public Date getInbetriebnahme() {
        return inbetriebnahme;
    }

    /**
     * @param inbetriebnahme The inbetriebnahme to set.
     */
    public void setInbetriebnahme(Date inbetriebnahme) {
        this.inbetriebnahme = inbetriebnahme;
    }

    /**
     * @return Returns the hvtAnschlussart.
     */
    public String getHvtAnschlussart() {
        return hvtAnschlussart;
    }

    /**
     * @param hvtAnschlussart The hvtAnschlussart to set.
     */
    public void setHvtAnschlussart(String hvtAnschlussart) {
        this.hvtAnschlussart = hvtAnschlussart;
    }

    /**
     * @return Returns the vpnNr.
     */
    public Long getVpnNr() {
        return vpnNr;
    }

    /**
     * @param vpnNr The vpnNr to set.
     */
    public void setVpnNr(Long vpnNr) {
        this.vpnNr = vpnNr;
    }

    /**
     * @return bearbeiterAm
     */
    public String getBearbeiterAm() {
        return bearbeiterAm;
    }

    /**
     * @param bearbeiterAm Festzulegender bearbeiterAm
     */
    public void setBearbeiterAm(String bearbeiterAm) {
        this.bearbeiterAm = bearbeiterAm;
    }
}


