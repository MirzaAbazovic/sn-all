/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class DeviceBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.Site.Device> {

    private String manufacturer;
    private String description;
    private String serialNumber;
    private String connection;

    @Override
    public OrderTechnicalParams.Site.Device build() {
        OrderTechnicalParams.Site.Device device = new OrderTechnicalParams.Site.Device();
        device.setManufacturer(this.manufacturer);
        device.setDescription(this.description);
        device.setSerialNumber(this.serialNumber);
        device.setConnection(this.connection);
        return device;
    }

    public DeviceBuilder withManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public DeviceBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public DeviceBuilder withSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public DeviceBuilder withConnection(String connection) {
        this.connection = connection;
        return this;
    }
}