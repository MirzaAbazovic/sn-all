/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.07.2009 09:43:08
 */
package de.augustakom.hurrican.model.cc.view;


/**
 * View-Modell, fuer den Import der FttB-Standorte
 *
 *
 */
public class FTTXStandortImportView {

    /**
     * Konstanten
     */
    public static final int STANDORT_FIRST_ROW = 0;
    public static final int STANDORT_POS_BEZEICHNUNG = 0;
    public static final int STANDORT_POS_STRASSE = 1;
    public static final int STANDORT_POS_HAUSNUMMER = 2;
    public static final int STANDORT_POS_HAUSNUMMERZUSATZ = 3;
    public static final int STANDORT_POS_PLZ = 4;
    public static final int STANDORT_POS_ORT = 5;
    public static final int STANDORT_POS_NIEDERLASSUNG = 6;
    public static final int STANDORT_POS_BETRIEBSRAUM = 7;
    public static final int STANDORT_POS_FCRAUM = 8;
    public static final int STANDORT_POS_STANDORTTYP = 9;
    public static final int STANDORT_POS_ONKZ = 10;
    public static final int STANDORT_POS_ASB = 11;
    public static final int STANDORT_POS_VERSORGER = 12;

    public static final String STANDORT_TYP_FTTB_H = "FTTB_H";
    public static final String STANDORT_TYP_FTTB = "FTTB";
    public static final String STANDORT_TYP_FTTH = "FTTH";
    public static final String STANDORT_TYP_FC = "FC";
    public static final String STANDORT_TYP_BR = "BR";
    public static final String STANDORT_TYP_MVG = "MVG";

    private String bezeichnung = null;
    private Long geoId = null;
    private String strasse = null;
    private String hausnummer = null;
    private String hausnummerZusatz = null;
    private String plz = null;
    private String ort = null;
    private String niederlassung = null;
    private String fcRaum = null;
    private String betriebsraum = null;
    private String standortTyp = null;
    private String onkz = null;
    private Integer asb = null;
    private String versorger = null;
    private String clusterId = null;

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Long getGeoId() {
        return geoId;
    }

    public void setGeoId(Long geoId) {
        this.geoId = geoId;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getHausnummer() {
        return hausnummer;
    }

    public void setHausnummer(String hausnummer) {
        this.hausnummer = hausnummer;
    }

    public String getHausnummerZusatz() {
        return hausnummerZusatz;
    }

    public void setHausnummerZusatz(String hausnummerZusatz) {
        this.hausnummerZusatz = hausnummerZusatz;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getNiederlassung() {
        return niederlassung;
    }

    public void setNiederlassung(String niederlassung) {
        this.niederlassung = niederlassung;
    }

    public String getFcRaum() {
        return fcRaum;
    }

    public void setFcRaum(String fcRaum) {
        this.fcRaum = fcRaum;
    }

    public String getStandortTyp() {
        return standortTyp;
    }

    public void setStandortTyp(String standortTyp) {
        this.standortTyp = standortTyp;
    }

    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    public Integer getAsb() {
        return asb;
    }

    public void setAsb(Integer asb) {
        this.asb = asb;
    }

    public String getBetriebsraum() {
        return betriebsraum;
    }

    public void setBetriebsraum(String betriebsraum) {
        this.betriebsraum = betriebsraum;
    }

    public String getVersorger() {
        return versorger;
    }

    public void setVersorger(String versorger) {
        this.versorger = versorger;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

}


