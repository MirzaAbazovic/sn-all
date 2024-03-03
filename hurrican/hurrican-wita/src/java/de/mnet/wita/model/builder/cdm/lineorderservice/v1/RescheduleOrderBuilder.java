/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RescheduleOrder;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TerminverschiebungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class RescheduleOrderBuilder implements LineOrderTypeBuilder<RescheduleOrder> {

    private TerminverschiebungType order;

    @Override
    public RescheduleOrder build() {
        RescheduleOrder rescheduleOrder = new RescheduleOrder();
        rescheduleOrder.setOrder(order);
        return rescheduleOrder;
    }

    public RescheduleOrderBuilder withOrder(TerminverschiebungType order) {
        this.order = order;
        return this;
    }

}
