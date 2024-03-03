/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.v1;

import java.time.*;

import de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.WorkforceNotificationTypeBuilder;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyUpdateOrder;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.TimeSlot;

/**
 *
 */
public class NotifyUpdateOrderBuilder implements WorkforceNotificationTypeBuilder<NotifyUpdateOrder> {

    private String orderId;
    private LocalDateTime modified;
    private NotifyUpdateOrder.Requested requested;
    private TimeSlot agreed;
    private NotifyUpdateOrder.Scheduled scheduled;
    private TimeSlot actual;
    private NotifyUpdateOrder.Resource resource;
    private NotifyUpdateOrder.StateInfo stateInfo;

    @Override
    public NotifyUpdateOrder build() {
        NotifyUpdateOrder order = new NotifyUpdateOrder();
        order.setOrderId(this.orderId);
        order.setModified(this.modified);
        order.setRequested(this.requested);
        order.setAgreed(this.agreed);
        order.setScheduled(this.scheduled);
        order.setActual(this.actual);
        order.setResource(this.resource);
        order.setStateInfo(this.stateInfo);
        return order;
    }

    public NotifyUpdateOrderBuilder withOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public NotifyUpdateOrderBuilder withModified(LocalDateTime modified) {
        this.modified = modified;
        return this;
    }

    public NotifyUpdateOrderBuilder withRequested(NotifyUpdateOrder.Requested requested) {
        this.requested = requested;
        return this;
    }

    public NotifyUpdateOrderBuilder withAgreed(TimeSlot agreed) {
        this.agreed = agreed;
        return this;
    }

    public NotifyUpdateOrderBuilder withScheduled(NotifyUpdateOrder.Scheduled scheduled) {
        this.scheduled = scheduled;
        return this;
    }

    public NotifyUpdateOrderBuilder withActual(TimeSlot actual) {
        this.actual = actual;
        return this;
    }

    public NotifyUpdateOrderBuilder withResource(NotifyUpdateOrder.Resource resource) {
        this.resource = resource;
        return this;
    }

    public NotifyUpdateOrderBuilder withStateInfo(NotifyUpdateOrder.StateInfo stateInfo) {
        this.stateInfo = stateInfo;
        return this;
    }
}