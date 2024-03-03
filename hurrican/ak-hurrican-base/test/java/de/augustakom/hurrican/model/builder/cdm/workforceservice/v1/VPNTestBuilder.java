/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 *
 */
public class VPNTestBuilder extends VPNBuilder {

    public VPNTestBuilder() {
        withLinkAggregation("link aggregation");
        withNumberOfChannels("number of channels");
        withPhysicalLine("physical line");
        withVplsId("123456");
        withVpnName("vpn name");
        withVpnTyp("vpn typ");
        withVpnId("vpn id");
        withEinwahl("einwahl");
    }
}