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
public class IPAddressBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.IPAddress> {

    private String version;
    private String type;
    private String address;
    private String netMask;

    @Override
    public OrderTechnicalParams.IPAddress build() {
        OrderTechnicalParams.IPAddress ipAddress = new OrderTechnicalParams.IPAddress();
        ipAddress.setAddress(address);
        ipAddress.setType(type);
        ipAddress.setNetMask(netMask);
        ipAddress.setVersion(version);
        return ipAddress;
    }

    public IPAddressBuilder withVersion(String version) {
        this.version = version;
        return this;
    }

    public IPAddressBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public IPAddressBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public IPAddressBuilder withNetMask(String netMask) {
        this.netMask = netMask;
        return this;
    }
}