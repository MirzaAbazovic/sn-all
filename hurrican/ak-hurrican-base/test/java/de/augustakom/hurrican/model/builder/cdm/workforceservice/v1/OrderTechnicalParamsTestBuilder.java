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
public class OrderTechnicalParamsTestBuilder extends OrderTechnicalParamsBuilder {

    public OrderTechnicalParamsTestBuilder() {
        withCommon(new CommonTestBuilder().build());
        withVPN(new VPNTestBuilder().build());
        withISDNBackup(new ISDNBackupTestBuilder().build());
        addCPE(new CPETestBuilder().build());
        addIPAddress(new IPAddressTestBuilder().build());
        addPortForwarding(new PortForwardingTestBuilder().build());
        addStaticRoute(new StaticRouteTestBuilder().build());
        addVLAN(new VLANTestBuilder().build());
        addCrossConnection(new CrossConnectionTestBuilder().build());
        addDialUpAccess(new DialUpAccessTestBuilder().build());
        addDialNumber(new DialNumberTestBuilder().withMain(Boolean.TRUE).withNumber("6906354").build());
        addDialNumber(new DialNumberTestBuilder().withNumber("69349598").build());
        addDialNumber(new DialNumberTestBuilder().withNumber("69349599").build());
        addSite(new SiteTestBuilder().build());
        withHousing(new HousingTestBuilder().build());
    }

}
