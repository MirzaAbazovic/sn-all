/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 *
 */
public class DescriptionTestBuilder extends DescriptionBuilder {

    public DescriptionTestBuilder() {
        withSummary("FTTX DSL + Fon");
        withDetails("#abw (fedaiemu, 14.10.2013)");
        withOrderTechnicalParams(new OrderTechnicalParamsTestBuilder().build());
    }
}