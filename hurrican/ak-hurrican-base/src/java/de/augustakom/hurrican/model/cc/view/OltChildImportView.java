/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.2014
 */
package de.augustakom.hurrican.model.cc.view;

public abstract class OltChildImportView {
    public static final String INSTALLTIONSSTATUS_DELETED = "GELOESCHT";

    private String bezeichnung = null; // Geraetebezeichnung: z.B. ONT-412345; Quelle: <resource><name>
    private String hersteller = null;
    private String seriennummer = null;
    private String modellnummer = null; // Modellbezeichner: z.B. 'HG8242' Huawei ONT
    private String olt = null; // OLT Geraetebezeichnung: z.B. OLT-400027
    private Long oltRack = null;
    private Long oltSubrack = null;
    private Long oltSlot = null;
    private Long oltPort = null;
    private Long gponId = null;
    private String standort = null;
    private String raumbezeichung = null;
    private String installationsstatus;

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getHersteller() {
        return hersteller;
    }

    public void setHersteller(String hersteller) {
        this.hersteller = hersteller;
    }

    public String getSeriennummer() {
        return seriennummer;
    }

    public void setSeriennummer(String seriennummer) {
        this.seriennummer = seriennummer;
    }

    public String getModellnummer() {
        return modellnummer;
    }

    public void setModellnummer(String modellnummer) {
        this.modellnummer = modellnummer;
    }

    public String getOlt() {
        return olt;
    }

    public void setOlt(String olt) {
        this.olt = olt;
    }

    public Long getOltRack() {
        return oltRack;
    }

    public void setOltRack(Long oltRack) {
        this.oltRack = oltRack;
    }

    public Long getOltSubrack() {
        return oltSubrack;
    }

    public void setOltSubrack(Long oltSubrack) {
        this.oltSubrack = oltSubrack;
    }

    public Long getOltSlot() {
        return oltSlot;
    }

    public void setOltSlot(Long oltSlot) {
        this.oltSlot = oltSlot;
    }

    public Long getOltPort() {
        return oltPort;
    }

    public void setOltPort(Long oltPort) {
        this.oltPort = oltPort;
    }

    public Long getGponId() {
        return gponId;
    }

    public void setGponId(Long gponId) {
        this.gponId = gponId;
    }

    public String getStandort() {
        return standort;
    }

    public void setStandort(String standort) {
        this.standort = standort;
    }

    public String getRaumbezeichung() {
        return raumbezeichung;
    }

    public void setRaumbezeichung(String raumbezeichung) {
        this.raumbezeichung = raumbezeichung;
    }

    public String getInstallationsstatus() {
        return installationsstatus;
    }

    public void setInstallationsstatus(String installationsstatus) {
        this.installationsstatus = installationsstatus;
    }

    public boolean isInstallationsstatusDeleted() {
        return INSTALLTIONSSTATUS_DELETED.equals(installationsstatus);
    }

}
