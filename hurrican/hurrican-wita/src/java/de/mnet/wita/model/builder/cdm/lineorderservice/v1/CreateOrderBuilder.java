/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.CreateOrder;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class CreateOrderBuilder implements LineOrderTypeBuilder<CreateOrder> {

    private AuftragType order;

    @Override
    public CreateOrder build() {
        CreateOrder createOrder = new CreateOrder();
        createOrder.setOrder(order);
        return createOrder;
    }

    public CreateOrderBuilder withOrder(AuftragType order) {
        this.order = order;
        return this;
    }

}
