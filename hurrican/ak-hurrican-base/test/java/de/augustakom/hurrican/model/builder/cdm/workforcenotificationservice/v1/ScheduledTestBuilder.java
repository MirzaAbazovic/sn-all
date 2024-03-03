/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.v1;

import java.time.*;

/**
 *
 */
public class ScheduledTestBuilder extends ScheduledBuilder {

    public ScheduledTestBuilder() {
        LocalDateTime todayAt10 = LocalDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0);
        withStartTime(todayAt10);
        withEndTime(todayAt10.plusHours(2));
        withFixed(false);
    }
}