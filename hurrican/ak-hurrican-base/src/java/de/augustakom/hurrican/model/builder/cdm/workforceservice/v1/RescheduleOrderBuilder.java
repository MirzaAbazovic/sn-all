/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.time.*;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.RequestedTimeslot;
import de.mnet.esb.cdm.resource.workforceservice.v1.RescheduleOrder;

/**
 *
 */
public class RescheduleOrderBuilder implements WorkforceTypeBuilder<RescheduleOrder> {

    private String orderId;
    private LocalDateTime fixedStartTime;
    private RequestedTimeslot requestedTimeSlot;

    @Override
    public RescheduleOrder build() {
        RescheduleOrder rescheduleOrder = new RescheduleOrder();
        rescheduleOrder.setId(this.orderId);
        rescheduleOrder.setFixedStartTime(this.fixedStartTime);
        rescheduleOrder.setRequestedTimeSlot(this.requestedTimeSlot);
        return rescheduleOrder;
    }

    public RescheduleOrderBuilder withOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public RescheduleOrderBuilder withFixedStartTime(LocalDateTime fixedStartTime) {
        this.fixedStartTime = fixedStartTime;
        return this;
    }

    public RescheduleOrderBuilder withRequestedTimeSlot(RequestedTimeslot requestedTimeSlot) {
        this.requestedTimeSlot = requestedTimeSlot;
        return this;
    }
}