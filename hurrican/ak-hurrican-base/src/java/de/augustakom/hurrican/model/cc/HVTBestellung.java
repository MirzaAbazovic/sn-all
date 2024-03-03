/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2005 13:11:15
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;


/**
 * Modell zur Abbildung einer HVT-Bestellung.
 *
 *
 */
public class HVTBestellung extends AbstractCCIDModel {

    /**
     * Konstante fuer den Physiktyp 'N' (niederbitratig) einer HVT-Bestellung.
     */
    public static final String PHYSIKTYP_N = "N";
    /**
     * Konstante fuer den Physiktyp 'H' (hochbitratig) einer HVT-Bestellung.
     */
    public static final String PHYSIKTYP_H = "H";

    private Long uevtId;
    private String physiktyp;
    private Date angebotDatum;
    private Integer anzahlCuDA;
    private Date bestelldatum;
    private Date realDTAGBis;
    private String bestellNrAKom;
    private String bestellNrDTAG;
    private Date bereitgestellt;
    private EqVerwendung eqVerwendung = EqVerwendung.STANDARD;

    /**
     * @return Returns the angebotDatum.
     */
    public Date getAngebotDatum() {
        return angebotDatum;
    }

    /**
     * @param angebotDatum The angebotDatum to set.
     */
    public void setAngebotDatum(Date angebotDatum) {
        this.angebotDatum = angebotDatum;
    }

    /**
     * @return Returns the anzahlCuDA.
     */
    public Integer getAnzahlCuDA() {
        return anzahlCuDA;
    }

    /**
     * @param anzahlCuDA The anzahlCuDA to set.
     */
    public void setAnzahlCuDA(Integer anzahlCuDA) {
        this.anzahlCuDA = anzahlCuDA;
    }

    /**
     * Gibt das Datum an, zu dem alle Stifte der HVT-Bestellung auf den UEVT eingespielt wurden.
     *
     * @return Returns the bereitgestellt.
     */
    public Date getBereitgestellt() {
        return bereitgestellt;
    }

    /**
     * @param bereitgestellt The bereitgestellt to set.
     */
    public void setBereitgestellt(Date bereitgestellt) {
        this.bereitgestellt = bereitgestellt;
    }

    /**
     * @return Returns the bestelldatum.
     */
    public Date getBestelldatum() {
        return bestelldatum;
    }

    /**
     * @param bestelldatum The bestelldatum to set.
     */
    public void setBestelldatum(Date bestelldatum) {
        this.bestelldatum = bestelldatum;
    }

    /**
     * @return Returns the bestellNrAKom.
     */
    public String getBestellNrAKom() {
        return bestellNrAKom;
    }

    /**
     * @param bestellNrAKom The bestellNrAKom to set.
     */
    public void setBestellNrAKom(String bestellNrAKom) {
        this.bestellNrAKom = bestellNrAKom;
    }

    /**
     * @return Returns the bestellNrDTAG.
     */
    public String getBestellNrDTAG() {
        return bestellNrDTAG;
    }

    /**
     * @param bestellNrDTAG The bestellNrDTAG to set.
     */
    public void setBestellNrDTAG(String bestellNrDTAG) {
        this.bestellNrDTAG = bestellNrDTAG;
    }

    /**
     * @return Returns the uevtId.
     */
    public Long getUevtId() {
        return uevtId;
    }

    /**
     * @param uevtId The uevtId to set.
     */
    public void setUevtId(Long uevtId) {
        this.uevtId = uevtId;
    }

    /**
     * @return Returns the physiktyp.
     */
    public String getPhysiktyp() {
        return physiktyp;
    }

    /**
     * @param physiktyp The physiktyp to set.
     */
    public void setPhysiktyp(String physiktyp) {
        this.physiktyp = physiktyp;
    }

    /**
     * @return Returns the realDTAGBis.
     */
    public Date getRealDTAGBis() {
        return this.realDTAGBis;
    }

    /**
     * @param realDTAGBis The realDTAGBis to set.
     */
    public void setRealDTAGBis(Date realDTAGBis) {
        this.realDTAGBis = realDTAGBis;
    }

    public EqVerwendung getEqVerwendung() {
        return eqVerwendung;
    }

    public void setEqVerwendung(EqVerwendung eqVerwendung) {
        this.eqVerwendung = eqVerwendung;
    }

}
