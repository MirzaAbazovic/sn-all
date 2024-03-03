/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

/**
 * Modell duer Kundendaten-Schnittstelle
 *
 *
 */
public class FTTXKundendatenView {

    // Referenz
    private String geraetebezeichnung = null;
    private String port = null;
    private Date datum = null;

    // Endstelle
    private String esAnrede = null;
    private String esName = null;
    private String esVorname = null;
    private String esTelefon = null;
    private String esMobil = null;
    private String esMail = null;

    // Kunde
    private String kdAnrede = null;
    private String kdName = null;
    private String kdVorname = null;
    private String kdTelefon = null;
    private String kdMobil = null;
    private String kdMail = null;

    // Anschlussdose
    private String anschlussDoseLage = null;

    // Endgerät
    private String egTyp = null;
    private String egSeriennummer = null;
    private String egCwmpId = null;

    // Auftrag/Produkt
    private Long auftragsnummerHurrican = null;
    private Long auftragsnummerTaifun = null;
    private String vbz = null;
    private String produkt = null;
    private String auftragsstatus = null;

    // Port
    private String portstatus = null;

    public String getGeraetebezeichnung() {
        return geraetebezeichnung;
    }

    public void setGeraetebezeichnung(String geraeteBezeichnung) {
        this.geraetebezeichnung = geraeteBezeichnung;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getEsAnrede() {
        return esAnrede;
    }

    public void setEsAnrede(String esAnrede) {
        this.esAnrede = esAnrede;
    }

    public String getEsName() {
        return esName;
    }

    public void setEsName(String esName) {
        this.esName = esName;
    }

    public String getEsVorname() {
        return esVorname;
    }

    public void setEsVorname(String esVorname) {
        this.esVorname = esVorname;
    }

    public String getEsTelefon() {
        return esTelefon;
    }

    public void setEsTelefon(String esTelefon) {
        this.esTelefon = esTelefon;
    }

    public String getEsMobil() {
        return esMobil;
    }

    public void setEsMobil(String esMobil) {
        this.esMobil = esMobil;
    }

    public String getEsMail() {
        return esMail;
    }

    public void setEsMail(String esMail) {
        this.esMail = esMail;
    }

    public String getKdAnrede() {
        return kdAnrede;
    }

    public void setKdAnrede(String kdAnrede) {
        this.kdAnrede = kdAnrede;
    }

    public String getKdName() {
        return kdName;
    }

    public void setKdName(String kdName) {
        this.kdName = kdName;
    }

    public String getKdVorname() {
        return kdVorname;
    }

    public void setKdVorname(String kdVorname) {
        this.kdVorname = kdVorname;
    }

    public String getKdTelefon() {
        return kdTelefon;
    }

    public void setKdTelefon(String kdTelefon) {
        this.kdTelefon = kdTelefon;
    }

    public String getKdMobil() {
        return kdMobil;
    }

    public void setKdMobil(String kdMobil) {
        this.kdMobil = kdMobil;
    }

    public String getKdMail() {
        return kdMail;
    }

    public void setKdMail(String kdMail) {
        this.kdMail = kdMail;
    }

    public String getAnschlussDoseLage() {
        return anschlussDoseLage;
    }

    public void setAnschlussDoseLage(String anschlussDoseLage) {
        this.anschlussDoseLage = anschlussDoseLage;
    }

    public String getEgTyp() {
        return egTyp;
    }

    public void setEgTyp(String egTyp) {
        this.egTyp = egTyp;
    }

    public String getEgSeriennummer() {
        return egSeriennummer;
    }

    public void setEgSeriennummer(String egSeriennummer) {
        this.egSeriennummer = egSeriennummer;
    }

    public String getEgCwmpId() {
        return egCwmpId;
    }

    public void setEgCwmpId(String egCwmpId) {
        this.egCwmpId = egCwmpId;
    }

    public Long getAuftragsnummerHurrican() {
        return auftragsnummerHurrican;
    }

    public void setAuftragsnummerHurrican(Long auftragsnummerHurrican) {
        this.auftragsnummerHurrican = auftragsnummerHurrican;
    }

    public Long getAuftragsnummerTaifun() {
        return auftragsnummerTaifun;
    }

    public void setAuftragsnummerTaifun(Long auftragsnummerTaifun) {
        this.auftragsnummerTaifun = auftragsnummerTaifun;
    }

    public String getVbz() {
        return vbz;
    }

    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    public String getProdukt() {
        return produkt;
    }

    public void setProdukt(String produkt) {
        this.produkt = produkt;
    }

    public String getAuftragsstatus() {
        return auftragsstatus;
    }

    public void setAuftragsstatus(String auftragsstatus) {
        this.auftragsstatus = auftragsstatus;
    }

    public String getPortstatus() {
        return portstatus;
    }

    public void setPortstatus(String portstatus) {
        this.portstatus = portstatus;
    }
}
