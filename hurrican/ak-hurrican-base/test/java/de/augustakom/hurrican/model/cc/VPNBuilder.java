/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.10.2009 17:15:54
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;


/**
 * Entity-Builder fuer VPN Objekte
 *
 *
 */
@SuppressWarnings("unused")
public class VPNBuilder extends EntityBuilder<VPNBuilder, VPN> {

    private Long id = randomLong(10000);
    private Long vpnNr = getLongId();
    private String vpnName;
    private String projektleiter;
    private Date datum = new Date();
    private Long kundeNo = getLongId();
    private String bemerkung;
    private String einwahl;
    private Long vpnType;
    private String realm;

    public VPNBuilder withVpnNr(Long vpnNr) {
        this.vpnNr = vpnNr;
        return this;
    }

    public VPNBuilder withDatum(Date datum) {
        this.datum = datum;
        return this;
    }

    public VPNBuilder withKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
        return this;
    }

    public VPNBuilder withVpnName(String vpnName) {
        this.vpnName = vpnName;
        return this;
    }

    public VPNBuilder withRealm(String realm) {
        this.realm = realm;
        return this;
    }

    public VPNBuilder withEinwahl(String einwahl) {
        this.einwahl = einwahl;
        return this;
    }

    public VPNBuilder withVpnType(Long vpnType) {
        this.vpnType = vpnType;
        return this;
    }

}


