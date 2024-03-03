/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.time.*;

/**
 *
 */
public class RequestedTimeslotTestBuilder extends RequestedTimeslotBuilder {

    public RequestedTimeslotTestBuilder() {
        LocalDateTime todayAt10 = LocalDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0);
        withEarliestStart(todayAt10);
        // withEarliestEnd(todayAt10.plusMinutes(45));
        withLatestStart(todayAt10.plusHours(3));
        withLatestEnd(todayAt10.plusHours(3).plusMinutes(45));
    }
}