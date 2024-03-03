/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.13
 */
package de.mnet.wbci.model.builder.cdm.location.v1;

import de.augustakom.hurrican.model.builder.cdm.location.v1.SearchBuildingsBuilder;

/**
 *
 */
public class SearchBuildingsTestBuilder extends SearchBuildingsBuilder {

    public SearchBuildingsTestBuilder() {
        withCountryCode("DE");
        withZipCode("80992");
        withCity("München");
        withStreetSection("Emmy-Noether-Straße");
        withBuilding("2", "a");
    }

}
