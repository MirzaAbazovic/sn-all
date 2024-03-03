/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.11.2009 15:03:45
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Modell-Klasse (Root-Element) fuer die Daten eines MDU-Initialize in Richtung CPS.
 *
 *
 */
@XStreamAlias("MDU_INITIALIZE")
public class CPSMduInitializeData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("MANUFACTURER")
    private String manufacturer = null;
    @XStreamAlias("MDU_TYP")
    private String mduTyp = null;
    @XStreamAlias("MDU_SERIAL_NO")
    private String serialNo = null;
    @XStreamAlias("MDU_GERAETE_BEZEICHNUNG")
    private String mduGeraeteBezeichnung = null;
    @XStreamAlias("MDU_STANDORT")
    private String mduStandort = null;
    @XStreamAlias("MDU_IP_ADDRESS")
    private String mduIpAdress = null;
    @XStreamAlias("OLT_GERAETE_BEZEICHNUNG")
    private String oltBezeichnung = null;
    @XStreamAlias("GPON_PORT")
    private String gponPort = null;
    @XStreamAlias("QINQ_START")
    private String qinqStart = null;
    @XStreamAlias("INITIALIZED")
    private Boolean initialized = null;

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getMduTyp() {
        return mduTyp;
    }

    public void setMduTyp(String mduTyp) {
        this.mduTyp = mduTyp;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getMduGeraeteBezeichnung() {
        return mduGeraeteBezeichnung;
    }

    public void setMduGeraeteBezeichnung(String mduGeraeteBezeichnung) {
        this.mduGeraeteBezeichnung = mduGeraeteBezeichnung;
    }

    public String getMduStandort() {
        return mduStandort;
    }

    public void setMduStandort(String mduStandort) {
        this.mduStandort = mduStandort;
    }

    public String getMduIpAdress() {
        return mduIpAdress;
    }

    public void setMduIpAdress(String mduIpAdress) {
        this.mduIpAdress = mduIpAdress;
    }

    public String getOltBezeichnung() {
        return oltBezeichnung;
    }

    public void setOltBezeichnung(String oltBezeichnung) {
        this.oltBezeichnung = oltBezeichnung;
    }

    public String getGponPort() {
        return gponPort;
    }

    public void setGponPort(String gponPort) {
        this.gponPort = gponPort;
    }

    public String getQinqStart() {
        return qinqStart;
    }

    public void setQinqStart(String qinqStart) {
        this.qinqStart = qinqStart;
    }

    public Boolean getInitialized() {
        return initialized;
    }

    public void setInitialized(Boolean initialized) {
        this.initialized = initialized;
    }
}
