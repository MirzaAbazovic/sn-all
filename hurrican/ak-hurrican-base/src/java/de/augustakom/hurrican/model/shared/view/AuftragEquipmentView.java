/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.05.2005 16:21:12
 */
package de.augustakom.hurrican.model.shared.view;


/**
 * View-Modell fuer die Anzeige von Equipment- und Auftrags-Daten.
 *
 *
 */
public class AuftragEquipmentView extends DefaultSharedAuftragView {

    private Long equipmentId = null;
    private Long hvtIdStandort = null;
    private String hvtOrtsteil = null;
    private String eqBucht = null;
    private String eqLeiste1 = null;
    private String eqStift1 = null;
    private String eqSwitch = null;
    private String eqHwEqn = null;
    private Long rangierId = null;
    private Long endstelleId = null;
    private String endstelle = null;
    private String endstelleName = null;
    private String endstelleTyp = null;
    private String dslamProfile = null;
    private String geraeteBezeichung = null;
    private String mgmtBezeichnung = null;
    private Long vpnNr = null;

    public String getEqBucht() {
        return eqBucht;
    }

    public void setEqBucht(String eqBucht) {
        this.eqBucht = eqBucht;
    }

    public String getEqHwEqn() {
        return eqHwEqn;
    }

    public void setEqHwEqn(String eqHwEqn) {
        this.eqHwEqn = eqHwEqn;
    }

    public String getEqLeiste1() {
        return eqLeiste1;
    }

    public void setEqLeiste1(String eqLeiste1) {
        this.eqLeiste1 = eqLeiste1;
    }

    public String getEqStift1() {
        return eqStift1;
    }

    public void setEqStift1(String eqStift1) {
        this.eqStift1 = eqStift1;
    }

    public String getEqSwitch() {
        return eqSwitch;
    }

    public void setEqSwitch(String eqSwitch) {
        this.eqSwitch = eqSwitch;
    }

    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    public Long getRangierId() {
        return rangierId;
    }

    public void setRangierId(Long rangierId) {
        this.rangierId = rangierId;
    }

    public String getHvtOrtsteil() {
        return hvtOrtsteil;
    }

    public void setHvtOrtsteil(String hvtOrtsteil) {
        this.hvtOrtsteil = hvtOrtsteil;
    }

    public String getEndstelle() {
        return endstelle;
    }

    public void setEndstelle(String endstelle) {
        this.endstelle = endstelle;
    }

    public Long getEndstelleId() {
        return endstelleId;
    }

    public void setEndstelleId(Long endstelleId) {
        this.endstelleId = endstelleId;
    }

    public String getEndstelleName() {
        return endstelleName;
    }

    public void setEndstelleName(String endstelleName) {
        this.endstelleName = endstelleName;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEndstelleTyp() {
        return endstelleTyp;
    }

    public void setEndstelleTyp(String endstelleTyp) {
        this.endstelleTyp = endstelleTyp;
    }

    public String getDslamProfile() {
        return dslamProfile;
    }

    public void setDslamProfile(String dslamProfile) {
        this.dslamProfile = dslamProfile;
    }

    public String getGeraeteBezeichung() {
        return geraeteBezeichung;
    }

    public void setGeraeteBezeichung(String geraeteBezeichung) {
        this.geraeteBezeichung = geraeteBezeichung;
    }

    public String getMgmtBezeichnung() {
        return mgmtBezeichnung;
    }

    public void setMgmtBezeichnung(String mgmtBezeichnung) {
        this.mgmtBezeichnung = mgmtBezeichnung;
    }

    public Long getVpnNr() {
        return vpnNr;
    }

    public void setVpnNr(Long vpnNr) {
        this.vpnNr = vpnNr;
    }

}


