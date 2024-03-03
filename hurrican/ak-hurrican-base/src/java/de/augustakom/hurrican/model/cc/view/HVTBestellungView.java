/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2005 16:35:05
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.EqVerwendung;


/**
 * View-Modell, um die wichtigsten Daten einer HVT-Bestellung zu gruppieren.
 *
 *
 */
public class HVTBestellungView extends AbstractCCIDModel {

    private String hvtOrtsteil = null;
    private Integer hvtASB = null;
    private Long uevtId = null;
    private String uevt = null;
    private String kvzNummer = null;
    private Integer uevtSchwellwert = null;
    private Date angebotDatum = null;
    private String physiktyp = null;
    private String anzahlCuDA = null;
    private Date realDTAGBis = null;
    private String bestellNrAKom = null;
    private String bestellNrDTAG = null;
    private EqVerwendung eqVerwendung = null;

    public Date getAngebotDatum() {
        return angebotDatum;
    }

    public void setAngebotDatum(Date angebotDatum) {
        this.angebotDatum = angebotDatum;
    }

    public String getAnzahlCuDA() {
        return anzahlCuDA;
    }

    public void setAnzahlCuDA(String anzahlCuDA) {
        this.anzahlCuDA = anzahlCuDA;
    }

    public String getBestellNrAKom() {
        return bestellNrAKom;
    }

    public void setBestellNrAKom(String bestellNrAKom) {
        this.bestellNrAKom = bestellNrAKom;
    }

    public String getBestellNrDTAG() {
        return bestellNrDTAG;
    }

    public void setBestellNrDTAG(String bestellNrDTAG) {
        this.bestellNrDTAG = bestellNrDTAG;
    }

    public Integer getHvtASB() {
        return hvtASB;
    }

    public void setHvtASB(Integer hvtASB) {
        this.hvtASB = hvtASB;
    }

    public String getHvtOrtsteil() {
        return hvtOrtsteil;
    }

    public void setHvtOrtsteil(String hvtOrtsteil) {
        this.hvtOrtsteil = hvtOrtsteil;
    }

    public String getPhysiktyp() {
        return physiktyp;
    }

    public void setPhysiktyp(String physiktyp) {
        this.physiktyp = physiktyp;
    }

    public String getUevt() {
        return uevt;
    }

    public void setUevt(String uevt) {
        this.uevt = uevt;
    }

    public Long getUevtId() {
        return uevtId;
    }

    public void setUevtId(Long uevtId) {
        this.uevtId = uevtId;
    }

    public String getKvzNummer() {
        return kvzNummer;
    }

    public void setKvzNummer(String kvzNummer) {
        this.kvzNummer = kvzNummer;
    }

    public Integer getUevtSchwellwert() {
        return uevtSchwellwert;
    }

    public void setUevtSchwellwert(Integer uevtSchwellwert) {
        this.uevtSchwellwert = uevtSchwellwert;
    }

    public void setHVTBestellId(Long id) {
        setId(id);
    }

    public Long getHVTBestellId() {
        return getId();
    }

    public Date getRealDTAGBis() {
        return this.realDTAGBis;
    }

    public void setRealDTAGBis(Date realDTAGBis) {
        this.realDTAGBis = realDTAGBis;
    }

    public void setEqVerwendung(EqVerwendung eqVerwendung) {
        this.eqVerwendung = eqVerwendung;
    }

    public EqVerwendung getEqVerwendung() {
        return eqVerwendung;
    }

}


