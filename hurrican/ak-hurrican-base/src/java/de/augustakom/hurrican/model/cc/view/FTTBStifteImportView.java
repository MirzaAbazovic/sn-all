/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.07.2009 09:43:08
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;


/**
 * View-Modell, fuer den Import der FttB-Standorte
 *
 *
 */
public class FTTBStifteImportView {

    /**
     * Konstanten
     */
    public static final int STIFTE_FIRST_ROW = 1;
    public static final int STIFTE_POS_STANDORT = 0;
    public static final int STIFTE_POS_TYP = 1;
    public static final int STIFTE_POS_LEISTE = 2;
    public static final int STIFTE_POS_ANZAHL = 3;
    public static final int STIFTE_POS_GUELTIGAB = 4;
    public static final int STIFTE_POS_VERSORGER = 5;


    private String standort = null;
    private String typ = null;
    private String leiste = null;
    private Integer anzahl = null;
    private List<String> stifte = null;
    private String versorger = null;

    /**
     * @return the standort
     */
    public String getStandort() {
        return standort;
    }

    /**
     * @param standort the standort to set
     */
    public void setStandort(String standort) {
        this.standort = standort;
    }

    /**
     * @return the typ
     */
    public String getTyp() {
        return typ;
    }

    /**
     * @param typ the typ to set
     */
    public void setTyp(String typ) {
        this.typ = typ;
    }

    /**
     * @return the leiste
     */
    public String getLeiste() {
        return leiste;
    }

    /**
     * @param leiste the leiste to set
     */
    public void setLeiste(String leiste) {
        this.leiste = leiste;
    }

    /**
     * @return the anzahl
     */
    public Integer getAnzahl() {
        return anzahl;
    }

    /**
     * @param anzahl the anzahl to set
     */
    public void setAnzahl(Integer anzahl) {
        this.anzahl = anzahl;
    }

    /**
     * @return the stifte
     */
    public List<String> getStifte() {
        return stifte;
    }

    /**
     * @param stifte the stifte to set
     */
    public void setStifte(List<String> stifte) {
        this.stifte = stifte;
    }

    /**
     * @return the versorger
     */
    public String getVersorger() {
        return versorger;
    }

    /**
     * @param versorger the versorger to set
     */
    public void setVersorger(String versorger) {
        this.versorger = versorger;
    }

}


