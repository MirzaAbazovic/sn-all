/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2009 11:17:10
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * Modell-Klasse zur Abbildung der Radius Account-Daten zur CPS-Provisionierung.
 *
 *
 */
@XStreamAlias("ACCOUNT")
public class CPSRadiusAccountData extends AbstractCPSServiceOrderDataModel {

    /**
     * Default-Domain fuer Radius-Accounts.
     */
    public static final String RADIUS_DEFAULT_DOMAIN = "@mdsl.mnet-online.de";

    /**
     * Konstante fuer 'tarif' definiert einen Flatrate-Tarif.
     */
    public static final String TARIF_FLAT = "FLAT";
    /**
     * EFLAT fuer Flat-Rates mit EVN
     */
    public static final String TARIF_E_FLAT = "EFLAT";
    /**
     * Konstante fuer 'tarif' definiert einen Volumen-Tarif.
     */
    public static final String TARIF_VOLUME = "VOLUME";
    /**
     * Konstante fuer 'tarif' definiert einen Zeit-Tarif.
     */
    public static final String TARIF_TIME = "TIME";

    @XStreamAlias("USERNAME")
    private String userName = null;
    @XStreamAlias("PASSWORD")
    private String password = null;
    @XStreamAlias("ACC_TYPE")
    private String accountType = null;
    @XStreamAlias("ALWAYSON")
    private String alwaysOn = null;
    @XStreamAlias("PRODUCTDATARATEDOWN")
    private String productDataRateDown = null;
    @XStreamAlias("PROFILEDATARATEDOWN")
    private String profileDataRateDown = null;
    @XStreamAlias("PORTID")
    private String portId = null;
    @XStreamAlias("VPNID")
    private String vpnId = null;
    @XStreamAlias("ACCOUNTING")
    private String tarif = null;
    @XStreamAlias("IPv4")
    private CPSRadiusIPv4Data ipv4 = null;
    @XStreamAlias("IPv6")
    private CPSRadiusIPv6Data ipv6 = null;
    @XStreamAlias("IP_MODE")
    private String ipMode = null;
    @XStreamAlias("QOSPROFILE")
    private String qosProfile;
    @XStreamAlias("REALM")
    private String realm;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAlwaysOn() {
        return alwaysOn;
    }

    public void setAlwaysOn(String alwaysOn) {
        this.alwaysOn = alwaysOn;
    }

    public String getProductDataRateDown() {
        return productDataRateDown;
    }

    public void setProductDataRateDown(String productDataRateDown) {
        this.productDataRateDown = productDataRateDown;
    }

    public String getProfileDataRateDown() {
        return profileDataRateDown;
    }

    public void setProfileDataRateDown(String profileDataRateDown) {
        this.profileDataRateDown = profileDataRateDown;
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    /**
     * Gibt den Tarif-Typ an. <br> Moegliche Werte sind: <br> <ul> <li>TIME <li>VOLUME <li>FLAT </ul>
     *
     * @return the tarif
     */
    public String getTarif() {
        return tarif;
    }

    public void setTarif(String tarif) {
        this.tarif = tarif;
    }

    public CPSRadiusIPv4Data getIpv4() {
        return ipv4;
    }

    public void setIpv4(CPSRadiusIPv4Data ipv4) {
        this.ipv4 = ipv4;
    }

    public CPSRadiusIPv6Data getIpv6() {
        return ipv6;
    }

    public void setIpv6(CPSRadiusIPv6Data ipv6) {
        this.ipv6 = ipv6;
    }

    public String getVpnId() {
        return vpnId;
    }

    public void setVpnId(String vpnId) {
        this.vpnId = vpnId;
    }

    public String getIpMode() {
        return ipMode;
    }

    public void setIpMode(String ipMode) {
        this.ipMode = ipMode;
    }

    public String getQosProfile() {
        return qosProfile;
    }

    public void setQosProfile(final String qosProfile) {
        this.qosProfile = qosProfile;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }
}


