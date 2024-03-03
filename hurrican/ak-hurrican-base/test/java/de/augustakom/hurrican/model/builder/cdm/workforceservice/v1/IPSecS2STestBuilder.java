/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 * Created by glinkjo on 04.02.2015.
 */
public class IPSecS2STestBuilder extends IPSecS2SBuilder {

    public IPSecS2STestBuilder() {
        withHostName("ipsec-hostname");
        withHostNamePassive("hostname-passive");
        withWanIP("188.94.200.142");
        withWanGateway("188.94.200.141");
        withWanSubnet("255.255.255.248");
        withLoopbackIp("172.17.219.10");
        withLoopbackIpPassive("172.17.219.11");
        withVirtualLanIp("172.17.208.89");
        withNetToEncrypt("172.17.180.40/29");
        withVirtualLanSubnet("255.255.255.248");
        withDialInNo("089123456");
        withSplitTunnel("ja");
        withPreSharedKey("nein");
        withCertificate("nein");
        withDescription("beschreibung");
        withCarrier("Orange");
        withBandwidth("1024 kbps");
        withType("SDSL");
        withAuftragsNr("123456");
    }

}
