/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.06.2005 07:44:57
 */
package de.augustakom.hurrican.model.shared.view;

import java.util.*;


/**
 * View-Modell zur Darstellung von Auftrags- und Leitungsdaten, die innerhalb eines best. Zeitraums in Betrieb genommen,
 * geaendert oder gekuendigt wurden bzw. werden sollen.
 *
 *
 */
public class AuftragRealisierungView extends DefaultSharedAuftragView {

    private String bearbeiter = null;
    private Date vorgabeSCV = null;
    private Date vorgabeKunde = null;
    private Date realisierungstermin = null;
    private String esB = null;
    private String esBOrt = null;
    private String esA = null;
    private String esAOrt = null;

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
     * @return Returns the vorgabeKunde.
     */
    public Date getVorgabeKunde() {
        return vorgabeKunde;
    }

    /**
     * @param vorgabeKunde The vorgabeKunde to set.
     */
    public void setVorgabeKunde(Date vorgabeKunde) {
        this.vorgabeKunde = vorgabeKunde;
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
     * @return Returns the realisierungstermin.
     */
    public Date getRealisierungstermin() {
        return realisierungstermin;
    }

    /**
     * @param realisierungstermin The realisierungstermin to set.
     */
    public void setRealisierungstermin(Date realisierungstermin) {
        this.realisierungstermin = realisierungstermin;
    }

    /**
     * @return Returns the esB.
     */
    public String getEsB() {
        return esB;
    }

    /**
     * @param esB The esB to set.
     */
    public void setEsB(String esB) {
        this.esB = esB;
    }

    /**
     * @return Returns the esBOrt.
     */
    public String getEsBOrt() {
        return esBOrt;
    }

    /**
     * @param esBOrt The esBOrt to set.
     */
    public void setEsBOrt(String esBOrt) {
        this.esBOrt = esBOrt;
    }

    /**
     * @return Returns the esA.
     */
    public String getEsA() {
        return esA;
    }

    /**
     * @param esA The esA to set.
     */
    public void setEsA(String esA) {
        this.esA = esA;
    }

    /**
     * @return Returns the esAOrt.
     */
    public String getEsAOrt() {
        return esAOrt;
    }

    /**
     * @param esAOrt The esAOrt to set.
     */
    public void setEsAOrt(String esAOrt) {
        this.esAOrt = esAOrt;
    }

}


