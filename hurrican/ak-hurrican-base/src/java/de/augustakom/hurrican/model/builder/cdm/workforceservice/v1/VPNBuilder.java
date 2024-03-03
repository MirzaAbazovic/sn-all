/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class VPNBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.VPN> {

    private String linkAggregation;
    private String physicalLine;
    private String numberOfChannels;
    private String vplsId;
    private String vpnName;
    private String vpnTyp;
    private String vpnId;
    private String einwahl;

    @Override
    public OrderTechnicalParams.VPN build() {
        OrderTechnicalParams.VPN vpn = new OrderTechnicalParams.VPN();
        vpn.setLinkAggregation(this.linkAggregation);
        vpn.setPhysicalLine(this.physicalLine);
        vpn.setNumberOfChannels(this.numberOfChannels);
        vpn.setVPLSId(this.vplsId);
        vpn.setName(this.vpnName);
        vpn.setVpnType(this.vpnTyp);
        vpn.setId(this.vpnId);
        vpn.setDialUp(this.einwahl);
        return vpn;
    }

    public VPNBuilder withLinkAggregation(String linkAggregation) {
        this.linkAggregation = linkAggregation;
        return this;
    }

    public VPNBuilder withPhysicalLine(String physicalLine) {
        this.physicalLine = physicalLine;
        return this;
    }

    public VPNBuilder withNumberOfChannels(String numberOfChannels) {
        this.numberOfChannels = numberOfChannels;
        return this;
    }

    public VPNBuilder withVplsId(String vplsId) {
        this.vplsId = vplsId;
        return this;
    }

    public VPNBuilder withVpnName(String vpnName) {
        this.vpnName = vpnName;
        return this;
    }

    public VPNBuilder withVpnTyp(String vpnTyp) {
        this.vpnTyp = vpnTyp;
        return this;
    }

    public VPNBuilder withVpnId(String vpnId) {
        this.vpnId = vpnId;
        return this;
    }

    public VPNBuilder withEinwahl(String einwahl) {
        this.einwahl = einwahl;
        return this;
    }

}