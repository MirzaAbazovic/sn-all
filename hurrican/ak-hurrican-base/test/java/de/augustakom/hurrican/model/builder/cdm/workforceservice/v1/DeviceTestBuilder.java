/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 *
 */
public class DeviceTestBuilder extends DeviceBuilder {

    public DeviceTestBuilder() {
        withManufacturer("Huawei");
        withSerialNumber("48575443F74A1302");
        withConnection("connection");
        withDescription("description");
    }
}