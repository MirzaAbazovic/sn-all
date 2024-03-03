/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 *
 */
public class AddressTestBuilder extends AddressBuilder {

    public AddressTestBuilder() {
        withStreet("Mangfallplatz");
        withHouseNumber("10");
        withFloor("1.OG mitte");
        withZipCode("81547");
        withCity("München");
        withCountry("DE");
    }
}