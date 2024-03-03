/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;


import java.time.*;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.RequestedTimeslot;

/**
 *
 */
public class RequestedTimeslotBuilder implements WorkforceTypeBuilder<RequestedTimeslot> {

    private LocalDateTime earliestStart;
    private LocalDateTime latestStart;
    private LocalDateTime earliestEnd;
    private LocalDateTime latestEnd;

    @Override
    public RequestedTimeslot build() {
        RequestedTimeslot rts = new RequestedTimeslot();
        rts.setEarliestStart(this.earliestStart);
        rts.setLatestStart(this.latestStart);
        rts.setEarliestEnd(this.earliestEnd);
        rts.setLatestEnd(this.latestEnd);
        return rts;
    }

    public RequestedTimeslotBuilder withEarliestStart(LocalDateTime earliestStart) {
        this.earliestStart = earliestStart;
        return this;
    }

    public RequestedTimeslotBuilder withLatestStart(LocalDateTime latestStart) {
        this.latestStart = latestStart;
        return this;
    }

    public RequestedTimeslotBuilder withEarliestEnd(LocalDateTime earliestEnd) {
        this.earliestEnd = earliestEnd;
        return this;
    }

    public RequestedTimeslotBuilder withLatestEnd(LocalDateTime latestEnd) {
        this.latestEnd = latestEnd;
        return this;
    }
}