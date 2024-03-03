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
public class StaticRouteBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.StaticRoute> {

    private String targetAddress;
    private String nextHop;

    @Override
    public OrderTechnicalParams.StaticRoute build() {
        OrderTechnicalParams.StaticRoute staticRoute = new OrderTechnicalParams.StaticRoute();
        staticRoute.setTargetAddress(this.targetAddress);
        staticRoute.setNextHop(this.nextHop);
        return staticRoute;
    }

    public StaticRouteBuilder withNextHop(String nextHop) {
        this.nextHop = nextHop;
        return this;
    }

    public StaticRouteBuilder withTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
        return this;
    }
}