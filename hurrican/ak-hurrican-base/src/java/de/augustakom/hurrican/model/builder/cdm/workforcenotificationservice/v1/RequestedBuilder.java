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
public class RequestedBuilder implements WorkforceNotificationTypeBuilder<NotifyUpdateOrder.Requested> {

    private LocalDateTime earliestStart;
    private LocalDateTime latestStart;
    private LocalDateTime earliestEnd;
    private LocalDateTime latestEnd;

    @Override
    public NotifyUpdateOrder.Requested build() {
        NotifyUpdateOrder.Requested requested = new NotifyUpdateOrder.Requested();
        requested.setEarliestStart(this.earliestStart);
        requested.setLatestStart(this.latestStart);
        requested.setEarliestEnd(this.earliestEnd);
        requested.setLatestEnd(this.latestEnd);
        return requested;
    }

    public RequestedBuilder withEarliestStart(LocalDateTime earliestStart) {
        this.earliestStart = earliestStart;
        return this;
    }

    public RequestedBuilder withLatestStart(LocalDateTime latestStart) {
        this.latestStart = latestStart;
        return this;
    }

    public RequestedBuilder withEarliestEnd(LocalDateTime earliestEnd) {
        this.earliestEnd = earliestEnd;
        return this;
    }

    public RequestedBuilder withLatestEnd(LocalDateTime latestEnd) {
        this.latestEnd = latestEnd;
        return this;
    }
}