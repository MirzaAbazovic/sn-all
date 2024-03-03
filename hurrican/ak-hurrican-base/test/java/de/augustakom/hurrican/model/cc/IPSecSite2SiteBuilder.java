/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2009 11:02:10
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;


/**
 * Builder fuer {@link IPSecSite2Site} Objekte.
 *
 *
 */
@SuppressWarnings("unused")
public class IPSecSite2SiteBuilder extends EntityBuilder<IPSecSite2SiteBuilder, IPSecSite2Site> {

    private AuftragBuilder auftragBuilder;
    private String wanGateway = randomString(20);
    private String loopbackIp = randomString(20);
    private String loopbackIpPassive = randomString(20);
    private String virtualLanIp = randomString(20);
    private String virtualLan2Scramble = randomString(20);
    private String virtualLanSubmask = randomString(20);
    private String virtualWanIp = randomString(20);
    private String virtualWanSubmask = randomString(20);
    private String hostname = randomString(40);
    private String hostnamePassive = randomString(40);
    private String isdnDialInNumber = randomString(20);
    private Boolean splitTunnel = Boolean.FALSE;
    private String description = randomString(20);
    private String accessCarrier = randomString(20);
    private String accessBandwidth = randomString(20);
    private String accessType = randomString(20);
    private Integer accessAuftragId = randomInt(1, Integer.MAX_VALUE);
    private Boolean hasPresharedKey = Boolean.FALSE;
    private Boolean hasCertificate = Boolean.FALSE;

    public IPSecSite2SiteBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public IPSecSite2SiteBuilder withWanGateway(String wanGateway) {
        this.wanGateway = wanGateway;
        return this;
    }

    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }


    public String getWanGateway() {
        return wanGateway;
    }


    public String getVirtualLanIp() {
        return virtualLanIp;
    }


    public String getVirtualLanSubmask() {
        return virtualLanSubmask;
    }


    public String getVirtualWanIp() {
        return virtualWanIp;
    }


    public String getVirtualWanSubmask() {
        return virtualWanSubmask;
    }


    public String getHostname() {
        return hostname;
    }


    public String getIsdnDialInNumber() {
        return isdnDialInNumber;
    }


    public Boolean getSplitTunnel() {
        return splitTunnel;
    }


    public Boolean getHasPresharedKey() {
        return hasPresharedKey;
    }


    public Boolean getHasCertificate() {
        return hasCertificate;
    }


}


