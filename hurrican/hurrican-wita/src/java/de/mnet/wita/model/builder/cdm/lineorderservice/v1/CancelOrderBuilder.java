/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.CancelOrder;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StornierungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class CancelOrderBuilder implements LineOrderTypeBuilder<CancelOrder> {

    private StornierungType order;

    @Override
    public CancelOrder build() {
        CancelOrder cancelOrder = new CancelOrder();
        cancelOrder.setOrder(order);
        return cancelOrder;
    }

    public CancelOrderBuilder withOrder(StornierungType order) {
        this.order = order;
        return this;
    }

}
