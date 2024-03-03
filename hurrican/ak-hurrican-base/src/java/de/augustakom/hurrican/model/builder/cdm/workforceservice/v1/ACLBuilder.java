/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class ACLBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.CPE.ACL> {

    private String name;
    private String routerType;

    @Override
    public OrderTechnicalParams.CPE.ACL build() {
        OrderTechnicalParams.CPE.ACL acl = new OrderTechnicalParams.CPE.ACL();
        acl.setName(name);
        acl.setRouterType(routerType);
        return acl;
    }

    public ACLBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ACLBuilder withRouterType(String routerType) {
        this.routerType = routerType;
        return this;
    }

}
