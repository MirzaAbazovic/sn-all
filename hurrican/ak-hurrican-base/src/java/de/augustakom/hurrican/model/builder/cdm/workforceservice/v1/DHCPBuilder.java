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
public class DHCPBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.CPE.DHCP> {

    private String poolFrom;
    private String poolTo;

    @Override
    public OrderTechnicalParams.CPE.DHCP build() {
        OrderTechnicalParams.CPE.DHCP dhcp = new OrderTechnicalParams.CPE.DHCP();
        dhcp.setPoolFrom(this.poolFrom);
        dhcp.setPoolTo(this.poolTo);
        return dhcp;
    }

    public DHCPBuilder withPoolFrom(String poolFrom) {
        this.poolFrom = poolFrom;
        return this;
    }

    public DHCPBuilder withPoolTo(String poolTo) {
        this.poolTo = poolTo;
        return this;
    }
}