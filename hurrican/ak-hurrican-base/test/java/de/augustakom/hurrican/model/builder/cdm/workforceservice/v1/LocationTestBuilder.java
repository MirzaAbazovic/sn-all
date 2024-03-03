/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 *
 */
public class LocationTestBuilder extends LocationBuilder {

    public LocationTestBuilder() {
        withCity("München");
        withHouseNumber("10");
        withStreet("Mangfallplatz");
        withZipCode("81547");
        withTAE1("TAE 123");
    }
}