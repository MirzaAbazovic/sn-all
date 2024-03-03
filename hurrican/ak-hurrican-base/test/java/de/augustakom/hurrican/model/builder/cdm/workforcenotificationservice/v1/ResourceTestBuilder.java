/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.v1;

import java.util.*;

/**
 *
 */
public class ResourceTestBuilder extends ResourceBuilder {

    public ResourceTestBuilder() {
        withFixed(true);
        withId(UUID.randomUUID().toString());
    }
}