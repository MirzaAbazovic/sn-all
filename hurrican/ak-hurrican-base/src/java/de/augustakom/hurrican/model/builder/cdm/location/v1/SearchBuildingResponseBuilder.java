/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.augustakom.hurrican.model.builder.cdm.location.v1;

import java.util.*;

import de.mnet.esb.cdm.resource.location.v1.Building;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchBuildingsResponse;

/**
 *
 */
public class SearchBuildingResponseBuilder extends SearchResponseBuilder<SearchBuildingsResponse> {

    protected List<Building> buildings = new ArrayList<>();

    @Override
    public SearchBuildingsResponse build() {
        SearchBuildingsResponse searchBuildingsResponse = new SearchBuildingsResponse();
        searchBuildingsResponse.getBuilding().addAll(buildings);
        return super.enrich(searchBuildingsResponse);
    }

    public SearchBuildingResponseBuilder withBuildings(List<Building> buildings) {
        this.buildings = buildings;
        return this;
    }

    public SearchBuildingResponseBuilder addBuilding(Building building) {
        this.buildings.add(building);
        return this;
    }
}
