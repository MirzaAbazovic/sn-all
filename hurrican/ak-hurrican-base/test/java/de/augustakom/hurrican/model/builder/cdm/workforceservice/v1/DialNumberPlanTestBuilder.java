/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.2015 14:26
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 *
 */
public class DialNumberPlanTestBuilder extends DialNumberPlanBuilder{

    public DialNumberPlanTestBuilder() {
        withDnBase("12345");
        withStart("00");
        withEnd("99");
        withOnkz("821");
        withZentrale(false);
    }
}
