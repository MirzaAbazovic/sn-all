/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.wbci.model.builder.cdm.location.v1;

import de.augustakom.hurrican.model.builder.cdm.location.v1.SearchBuildingResponseBuilder;

/**
 *
 */
public class SearchBuildingsResponseTestBuilder extends SearchBuildingResponseBuilder {

    public SearchBuildingsResponseTestBuilder() {
        addBuilding(new BuildingTestBuilder().build());
    }
}
