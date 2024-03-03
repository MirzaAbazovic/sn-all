/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2009 14:14:16
 */
package de.augustakom.hurrican.model.cc.hardware;

import java.util.*;

/**
 * Modell-Klasse, um eine OLT abzubilden.
 *
 *
 */
public class HWOlt extends HWRack {

    public static enum GPON_PORT_ID_REG_EXP {
        GPON_HUAWEI,
        GPON_ALCATEL;
    }

    private String serialNo = null;
    private String ipNetzVon = null;
    private String qinqVon = null;
    private String qinqBis = null;
    /**
     * lfd. Nr. der OLT fuer VLAN Berechnung
     */
    private Integer oltNr;
    private Date vlanAktivAb;
    private Integer svlanOffset;
    private String oltType;

    /**
     * @return the serialNo
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * @param serialNo the serialNo to set
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * @return the qinqVon
     */
    public String getQinqVon() {
        return qinqVon;
    }

    /**
     * @param qinqVon the qinqVon to set
     */
    public void setQinqVon(String qinqVon) {
        this.qinqVon = qinqVon;
    }

    /**
     * @return the qinqBis
     */
    public String getQinqBis() {
        return qinqBis;
    }

    /**
     * @param qinqBis the qinqBis to set
     */
    public void setQinqBis(String qinqBis) {
        this.qinqBis = qinqBis;
    }

    /**
     * @return the ipNetzVon
     */
    public String getIpNetzVon() {
        return ipNetzVon;
    }

    /**
     * @param ipNetzVon the ipNetzVon to set
     */
    public void setIpNetzVon(String ipNetzVon) {
        this.ipNetzVon = ipNetzVon;
    }

    public Integer getOltNr() {
        return oltNr;
    }

    public void setOltNr(Integer oltNr) {
        this.oltNr = oltNr;
    }

    public Date getVlanAktivAb() {
        return vlanAktivAb;
    }

    /**
     * <b>ACHTUNG:</b> Datum darf nur eingeschränkt geändert werden. <ul> <li>wenn Datum bereits in der Vergangenheit
     * ... gar nicht mehr</li> <li>wenn Datum in der Zukunft müssen evtl. auch die Vlans für alle Aufträge der OLT neu
     * berechnet werden</li> </ul>
     *
     * @param vlanAktivAb
     */
    public void setVlanAktivAb(Date vlanAktivAb) {
        this.vlanAktivAb = vlanAktivAb;
    }

    public Integer getSvlanOffset() {
        return svlanOffset;
    }

    public void setSvlanOffset(Integer ipoeOffset) {
        this.svlanOffset = ipoeOffset;
    }

    public String getOltType() {
        return oltType;
    }

    public void setOltType(String oltType) {
        this.oltType = oltType;
    }
}
