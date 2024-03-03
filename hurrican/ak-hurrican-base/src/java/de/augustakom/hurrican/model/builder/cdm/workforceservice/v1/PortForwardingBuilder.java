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
public class PortForwardingBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.PortForwarding> {

    private String protocol;
    private OrderTechnicalParams.PortForwarding.Source source;
    private OrderTechnicalParams.PortForwarding.Target target;

    @Override
    public OrderTechnicalParams.PortForwarding build() {
        OrderTechnicalParams.PortForwarding portForwarding = new OrderTechnicalParams.PortForwarding();
        portForwarding.setProtocol(this.protocol);
        portForwarding.setSource(this.source);
        portForwarding.setTarget(this.target);
        return portForwarding;
    }

    public PortForwardingBuilder withProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public PortForwardingBuilder withSource(OrderTechnicalParams.PortForwarding.Source source) {
        this.source = source;
        return this;
    }

    public PortForwardingBuilder withTarget(OrderTechnicalParams.PortForwarding.Target target) {
        this.target = target;
        return this;
    }
}