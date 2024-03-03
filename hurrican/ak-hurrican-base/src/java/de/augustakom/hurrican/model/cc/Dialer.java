/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2006 16:49:15
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;


/**
 * Modell zur Abbildung einer Dialer-Kennung. <br> Darunter versteht man eine Rufnummer, die als Dialer-Nummer bekannt
 * und in der EWSD gesperrt ist oder war [oder sein wird :~)].
 *
 *
 */
public class Dialer extends AbstractCCHistoryModel {

    private String kennzahl = null;
    private String beschreibung = null;
    private String typ = null;
    private String userGueltigVon = null;
    private String userGueltigBis = null;
    private Date gesperrtAb = null;
    private Date gesperrtBis = null;
    private String userGesperrtAb = null;
    private String userGesperrtBis = null;

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
     * @return Returns the gesperrtAb.
     */
    public Date getGesperrtAb() {
        return gesperrtAb;
    }

    /**
     * @param gesperrtAb The gesperrtAb to set.
     */
    public void setGesperrtAb(Date gesperrtAb) {
        this.gesperrtAb = gesperrtAb;
    }

    /**
     * @param gesperrtAb The gesperrtAb to set.
     * @param user       User-Name wird gesetzt, wenn 'gesperrtAb' != null
     */
    public void setGesperrtAb(Date gesperrtAb, String user) {
        if (gesperrtAb != null) {
            setUserGesperrtAb(user);
        }
        this.gesperrtAb = gesperrtAb;
    }

    /**
     * @return Returns the gesperrtBis.
     */
    public Date getGesperrtBis() {
        return gesperrtBis;
    }

    /**
     * @param gesperrtBis The gesperrtBis to set.
     */
    public void setGesperrtBis(Date gesperrtBis) {
        this.gesperrtBis = gesperrtBis;
    }

    /**
     * @param gesperrtBis The gesperrtBis to set.
     * @param user        user User-Name wird gesetzt, wenn 'gesperrtBis' != null
     */
    public void setGesperrtBis(Date gesperrtBis, String user) {
        if (gesperrtBis != null) {
            setUserGesperrtBis(user);
        }
        this.gesperrtBis = gesperrtBis;
    }

    /**
     * @return Returns the kennzahl.
     */
    public String getKennzahl() {
        return kennzahl;
    }

    /**
     * @param kennzahl The kennzahl to set.
     */
    public void setKennzahl(String kennzahl) {
        this.kennzahl = kennzahl;
    }

    /**
     * @return Returns the typ.
     */
    public String getTyp() {
        return typ;
    }

    /**
     * @param typ The typ to set.
     */
    public void setTyp(String typ) {
        this.typ = typ;
    }

    /**
     * @return Returns the userGesperrtAb.
     */
    public String getUserGesperrtAb() {
        return userGesperrtAb;
    }

    /**
     * @param userGesperrtAb The userGesperrtAb to set.
     */
    public void setUserGesperrtAb(String userGesperrtAb) {
        this.userGesperrtAb = userGesperrtAb;
    }

    /**
     * @return Returns the userGesperrtBis.
     */
    public String getUserGesperrtBis() {
        return userGesperrtBis;
    }

    /**
     * @param userGesperrtBis The userGesperrtBis to set.
     */
    public void setUserGesperrtBis(String userGesperrtBis) {
        this.userGesperrtBis = userGesperrtBis;
    }

    /**
     * @return Returns the userGueltigBis.
     */
    public String getUserGueltigBis() {
        return userGueltigBis;
    }

    /**
     * @param userGueltigBis The userGueltigBis to set.
     */
    public void setUserGueltigBis(String userGueltigBis) {
        this.userGueltigBis = userGueltigBis;
    }

    /**
     * @return Returns the userGueltigVon.
     */
    public String getUserGueltigVon() {
        return userGueltigVon;
    }

    /**
     * @param userGueltigVon The userGueltigVon to set.
     */
    public void setUserGueltigVon(String userGueltigVon) {
        this.userGueltigVon = userGueltigVon;
    }

    /**
     * @param gueltigVon The gueltigVon to set.
     * @param user       user User-Name wird gesetzt, wenn 'gueltigVon' != null
     */
    public void setGueltigVon(Date gueltigVon, String user) {
        if (gueltigVon != null) {
            setUserGueltigVon(user);
        }
        setGueltigVon(gueltigVon);
    }

    /**
     * @param gueltigBis The gueltigBis to set.
     * @param user       user User-Name wird gesetzt, wenn 'gueltigBis' != null
     */
    public void setGueltigBis(Date gueltigBis, String user) {
        if (gueltigBis != null) {
            setUserGueltigBis(user);
        }
        setGueltigBis(gueltigBis);
    }

}


