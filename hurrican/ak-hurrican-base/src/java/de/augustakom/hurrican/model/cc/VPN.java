/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2004 14:01:43
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Modell bildet einen VPN-Auftrag ab.
 *
 *
 */
public class VPN extends AbstractCCIDModel implements KundenModel {

    private Long vpnNr = null;
    private String projektleiter = null;
    private Date datum = null;
    private Long kundeNo = null;
    private String bemerkung = null;
    private String einwahl = null;
    private Long vpnType = null;
    private String vpnName = null;
    private String realm = null;
    private Long niederlassungId = null;
    private String salesRep = null;
    private Boolean qos = null;

    /**
     * Der Parameter <code>vpnNr</code> entspricht fuer die Kommunikation im Unternehmen der VPN ID. Es handelt sich
     * hierbei um die Taifun Auftragsnummer des VPN Rahmenvertrags!
     *
     * @return
     */
    public Long getVpnNr() {
        return vpnNr;
    }

    public void setVpnNr(Long vpnNr) {
        this.vpnNr = vpnNr;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getEinwahl() {
        return einwahl;
    }

    public void setEinwahl(String einwahl) {
        this.einwahl = einwahl;
    }

    @Override
    public Long getKundeNo() {
        return kundeNo;
    }

    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    public String getProjektleiter() {
        return projektleiter;
    }

    public void setProjektleiter(String projektleiter) {
        this.projektleiter = projektleiter;
    }

    public Long getVpnType() {
        return vpnType;
    }

    public void setVpnType(Long vpnType) {
        this.vpnType = vpnType;
    }

    public String getVpnName() {
        return vpnName;
    }

    public void setVpnName(String vpnName) {
        this.vpnName = vpnName;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public Long getNiederlassungId() {
        return niederlassungId;
    }

    public void setNiederlassungId(Long niederlassungId) {
        this.niederlassungId = niederlassungId;
    }

    public String getSalesRep() {
        return salesRep;
    }

    public void setSalesRep(String salesRep) {
        this.salesRep = salesRep;
    }

    public Boolean getQos() {
        return qos;
    }

    public void setQos(Boolean qos) {
        this.qos = qos;
    }

}


