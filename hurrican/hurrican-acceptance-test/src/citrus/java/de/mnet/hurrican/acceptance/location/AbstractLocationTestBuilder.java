/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.03.2015
 */
package de.mnet.hurrican.acceptance.location;

import de.mnet.hurrican.acceptance.AbstractHurricanTestBuilder;
import de.mnet.hurrican.acceptance.builder.GeoIdBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 * Base class for all location notification related tests
 */
public class AbstractLocationTestBuilder extends AbstractHurricanTestBuilder {

    protected void simulatorUseCase(SimulatorUseCase useCase) {
        simulatorUseCase(useCase, LocationTestVersion.V1);
    }

    protected void addGeoIdVariables(GeoIdBuilder.GeoIdBuilderResult geoIdResult) {
        addGeoIdVariables(geoIdResult, "");
    }
    protected void addGeoIdVariables(GeoIdBuilder.GeoIdBuilderResult geoIdResult, String prefix) {
        variables().add(prefix + VariableNames.GEO_ID, geoIdResult.geoId.getId().toString());
        variables().add(prefix + VariableNames.GEO_ID_HOUSE_NR, geoIdResult.geoId.getHouseNum());
        variables().add(prefix + VariableNames.STREET_ID, geoIdResult.streetSection.getId().toString());
        variables().add(prefix + VariableNames.STREET_NAME, geoIdResult.streetSection.getName());
        variables().add(prefix + VariableNames.ZIPCODE_ID, geoIdResult.zipCode.getId().toString());
        variables().add(prefix + VariableNames.ZIPCODE_CODE, geoIdResult.zipCode.getZipCode());
        variables().add(prefix + VariableNames.CITY_ID, geoIdResult.city.getId().toString());
        variables().add(prefix + VariableNames.CITY_NAME, geoIdResult.city.getName());
        variables().add(prefix + VariableNames.COUNTRY_ID, geoIdResult.country.getId().toString());
        variables().add(prefix + VariableNames.COUNTRY_NAME, geoIdResult.country.getName());
    }

}
