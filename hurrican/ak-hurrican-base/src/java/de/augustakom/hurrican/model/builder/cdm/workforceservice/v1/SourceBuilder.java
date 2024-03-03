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
public class SourceBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.PortForwarding.Source> {

    private String ipAddress;
    private String port;

    @Override
    public OrderTechnicalParams.PortForwarding.Source build() {
        OrderTechnicalParams.PortForwarding.Source source = new OrderTechnicalParams.PortForwarding.Source();
        source.setIPAddress(this.ipAddress);
        source.setPort(this.port);
        return source;
    }

    public SourceBuilder withIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public SourceBuilder withPort(String port) {
        this.port = port;
        return this;
    }
}