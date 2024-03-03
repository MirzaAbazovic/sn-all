/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 * Created by glinkjo on 04.02.2015.
 */
public class IPSecS2SBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.IPSecS2S> {

    private String hostName;
    private String hostNamePassive;
    private String wanIP;
    private String wanSubnet;
    private String wanGateway;
    private String loopbackIp;
    private String loopbackIpPassive;
    private String virtualLanIp;
    private String netToEncrypt;
    private String virtualLanSubnet;
    private String dialInNo;
    private String splitTunnel;
    private String preSharedKey;
    private String certificate;
    private String description;
    private String carrier;
    private String bandwidth;
    private String type;
    private String auftragsNr;

    @Override
    public OrderTechnicalParams.IPSecS2S build() {
        OrderTechnicalParams.IPSecS2S ipSecS2S = new OrderTechnicalParams.IPSecS2S();
        ipSecS2S.setHostName(this.hostName);
        ipSecS2S.setHostNamePassive(this.hostNamePassive);
        ipSecS2S.setWANIp(this.wanIP);
        ipSecS2S.setWANSubnet(this.wanSubnet);
        ipSecS2S.setWANGateway(this.wanGateway);
        ipSecS2S.setLoopbackIp(this.loopbackIp);
        ipSecS2S.setLoopbackIpPassive(this.loopbackIpPassive);
        ipSecS2S.setVLANIp(this.virtualLanIp);
        ipSecS2S.setEncryptedNetwork(this.netToEncrypt);
        ipSecS2S.setVLANSubnet(this.virtualLanSubnet);
        ipSecS2S.setDialInNumber(this.dialInNo);
        ipSecS2S.setSplitTunnel(this.splitTunnel);
        ipSecS2S.setPresharedKey(this.preSharedKey);
        ipSecS2S.setCertificate(this.certificate);
        ipSecS2S.setIpsDescription(this.description);
        ipSecS2S.setCarrier(this.carrier);
        ipSecS2S.setBandwidth(this.bandwidth);
        ipSecS2S.setIpsType(this.type);
        ipSecS2S.setOrderId(this.auftragsNr);
        return ipSecS2S;
    }

    public IPSecS2SBuilder withHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public IPSecS2SBuilder withHostNamePassive(String hostNamePassive) {
        this.hostNamePassive = hostNamePassive;
        return this;
    }

    public IPSecS2SBuilder withWanIP(String wanIP) {
        this.wanIP = wanIP;
        return this;
    }

    public IPSecS2SBuilder withWanSubnet(String wanSubnet) {
        this.wanSubnet = wanSubnet;
        return this;
    }

    public IPSecS2SBuilder withWanGateway(String wanGateway) {
        this.wanGateway = wanGateway;
        return this;
    }

    public IPSecS2SBuilder withLoopbackIp(String loopbackIp) {
        this.loopbackIp = loopbackIp;
        return this;
    }

    public IPSecS2SBuilder withLoopbackIpPassive(String loopbackIpPassive) {
        this.loopbackIpPassive = loopbackIpPassive;
        return this;
    }

    public IPSecS2SBuilder withVirtualLanIp(String virtualLanIp) {
        this.virtualLanIp = virtualLanIp;
        return this;
    }

    public IPSecS2SBuilder withNetToEncrypt(String netToEncrypt) {
        this.netToEncrypt = netToEncrypt;
        return this;
    }

    public IPSecS2SBuilder withVirtualLanSubnet(String virtualLanSubnet) {
        this.virtualLanSubnet = virtualLanSubnet;
        return this;
    }

    public IPSecS2SBuilder withDialInNo(String dialInNo) {
        this.dialInNo = dialInNo;
        return this;
    }

    public IPSecS2SBuilder withSplitTunnel(String splitTunnel) {
        this.splitTunnel = splitTunnel;
        return this;
    }

    public IPSecS2SBuilder withPreSharedKey(String preSharedKey) {
        this.preSharedKey = preSharedKey;
        return this;
    }

    public IPSecS2SBuilder withCertificate(String certificate) {
        this.certificate = certificate;
        return this;
    }

    public IPSecS2SBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public IPSecS2SBuilder withCarrier(String carrier) {
        this.carrier = carrier;
        return this;
    }

    public IPSecS2SBuilder withBandwidth(String bandwidth) {
        this.bandwidth = bandwidth;
        return this;
    }

    public IPSecS2SBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public IPSecS2SBuilder withAuftragsNr(String auftragsNr) {
        this.auftragsNr = auftragsNr;
        return this;
    }

}
