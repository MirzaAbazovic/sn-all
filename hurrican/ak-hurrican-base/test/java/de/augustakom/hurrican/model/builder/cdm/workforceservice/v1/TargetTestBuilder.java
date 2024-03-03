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
public class TargetTestBuilder extends TargetBuilder {

    public TargetTestBuilder() {
        withIpAddress("127.0.0.1");
        withPort("9090");
    }
}