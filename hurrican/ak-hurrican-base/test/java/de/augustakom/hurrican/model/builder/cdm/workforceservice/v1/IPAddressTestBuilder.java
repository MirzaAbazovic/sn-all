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
public class IPAddressTestBuilder extends IPAddressBuilder {

    public IPAddressTestBuilder() {
        withType("type");
        withAddress("127.0.0.1");
        withNetMask("255.255.255.0");
        withVersion("V4");
    }
}