/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.v1;

import java.time.*;

import de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.WorkforceNotificationTypeBuilder;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.TimeSlot;

/**
 *
 */
public class TimeSlotBuilder implements WorkforceNotificationTypeBuilder<TimeSlot> {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Override
    public TimeSlot build() {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setStartTime(this.startTime);
        timeSlot.setEndTime(this.endTime);
        return timeSlot;
    }

    public TimeSlotBuilder withStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public TimeSlotBuilder withEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }
}