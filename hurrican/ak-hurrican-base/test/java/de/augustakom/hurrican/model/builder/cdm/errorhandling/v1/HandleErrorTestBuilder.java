/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.13
 */
package de.augustakom.hurrican.model.builder.cdm.errorhandling.v1;

import java.time.*;
import java.util.*;

import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

public class HandleErrorTestBuilder extends HandleErrorBuilder {
    public HandleError buildValid() {
        withError(new ErrorBuilder()
                .withCode("123")
                .withErrorDetails("some details")
                .withTime(LocalDateTime.now())
                .withMessage("some message")
                .build());

        withMessage(new MessageBuilder()
                .withJMSEndpoint("some endpoint")
                .withJMSProperties(Arrays.asList(new String[][] { { "key", "val" } }))
                .withPayload("some payload")
                .addContexts(new ContextBuilder().withKeyValue("contextkey", "valContext").build())
                .build());

        withTrackingId("T123");

        withComponent(new ComponentBuilder()
                .withService("svc")
                .withProcessName("p1")
                .withOperation("op")
                .withName("some name")
                .withHost("localhost")
                .withProcessId("1")
                .build());

        addBusinessKey(new BusinessKeyBuilder().withKeyValue("businesskey", "1000333").build());

        return build();
    }
}
