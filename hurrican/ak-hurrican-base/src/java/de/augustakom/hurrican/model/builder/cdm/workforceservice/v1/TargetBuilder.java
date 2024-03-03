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
public class TargetBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.PortForwarding.Target> {

    private String ipAddress;
    private String port;

    @Override
    public OrderTechnicalParams.PortForwarding.Target build() {
        OrderTechnicalParams.PortForwarding.Target target = new OrderTechnicalParams.PortForwarding.Target();
        target.setIPAddress(this.ipAddress);
        target.setPort(this.port);
        return target;
    }

    public TargetBuilder withIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public TargetBuilder withPort(String port) {
        this.port = port;
        return this;
    }
}