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

/**
 *
 */
public class ScheduledBuilder implements WorkforceNotificationTypeBuilder<NotifyUpdateOrder.Scheduled> {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean fixed;

    @Override
    public NotifyUpdateOrder.Scheduled build() {
        NotifyUpdateOrder.Scheduled scheduled = new NotifyUpdateOrder.Scheduled();
        scheduled.setFixed(this.fixed);
        scheduled.setStartTime(this.startTime);
        scheduled.setEndTime(this.endTime);
        return scheduled;
    }

    public ScheduledBuilder withStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public ScheduledBuilder withEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public ScheduledBuilder withFixed(boolean fixed) {
        this.fixed = fixed;
        return this;
    }
}