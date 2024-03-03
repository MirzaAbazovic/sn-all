/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.v1;

import java.time.*;
import java.util.*;

/**
 *
 */
public class NotifyUpdateOrderTestBuilder extends NotifyUpdateOrderBuilder {

    public NotifyUpdateOrderTestBuilder() {
        withOrderId(UUID.randomUUID().toString());
        withModified(LocalDateTime.of(2014, 7, 3, 10, 0, 0, 0));
        withActual(new TimeSlotTestBuilder().build());
        withAgreed(new TimeSlotTestBuilder().build());
        withRequested(new RequestedTestBuilder().build());
        withScheduled(new ScheduledTestBuilder().build());
        withResource(new ResourceTestBuilder().build());
        withStateInfo(new StateInfoTestBuilder().build());
    }
}