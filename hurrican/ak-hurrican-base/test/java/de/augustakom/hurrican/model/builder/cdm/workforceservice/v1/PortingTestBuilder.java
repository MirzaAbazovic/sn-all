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
public class PortingTestBuilder extends PortingBuilder {

    public PortingTestBuilder() {
        withLastCarrier("DTAG");
        withPortingDate("02.07.2014");
        withWindow("9-12");
    }
}