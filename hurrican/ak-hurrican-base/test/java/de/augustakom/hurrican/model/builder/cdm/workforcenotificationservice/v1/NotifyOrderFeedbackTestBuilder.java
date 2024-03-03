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
public class NotifyOrderFeedbackTestBuilder extends NotifyOrderFeedbackBuilder {

    public NotifyOrderFeedbackTestBuilder() {
        withOrderId(UUID.randomUUID().toString());
        withCaptured(LocalDateTime.of(2014, 7, 3, 10, 0, 0, 0));
        withMaterial(new MaterialTestBuilder().build());
        withText("text");
        withWorkUnit("work unit");
    }
}