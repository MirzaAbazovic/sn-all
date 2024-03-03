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
public class CPETestBuilder extends CPEBuilder {

    public CPETestBuilder() {
        withType("AVM Fritz!Box FON WLAN 7390");
        withManufacturer("AVM");
        withModel("FRITZBOX_7390");
        withSerialNumber("E19243300049901");

        withAccessData("access data");
        withDHCP(new DHCPTestBuilder().build());
        withLayer2Protocol("layer 2 protocol");
        withMacAddress("mac address");
        withNAT(Boolean.TRUE);
        withQoS(Boolean.FALSE);
        withWANVPVC("wanvpvc");

        withDns(Boolean.TRUE);
        withDnsServerIp("192.168.1.1");
        withSnmpCustomer(Boolean.FALSE);
        withSnmpMnet(Boolean.TRUE);

        withMtu(5);
        withCpeBemerkung("CPE Bemerkung");
        addACL(new ACLTestBuilder().build());
        addACL(new ACLBuilder()
                        .withName("MVIcisco")
                        .withRouterType("Cisco Router")
                        .build()
        );
    }

}
