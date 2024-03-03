/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.v1;

import java.math.*;

/**
 *
 */
public class MaterialTestBuilder extends MaterialBuilder {

    public MaterialTestBuilder() {
        withId("material id");
        withDescription("description");
        withQuantity(BigDecimal.TEN);
        withSerialNumber("serial number");
        withSummary("summary");
    }
}