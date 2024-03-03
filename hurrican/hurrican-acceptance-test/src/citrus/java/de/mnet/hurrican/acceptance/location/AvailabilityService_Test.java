/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.08.2015
 */
package de.mnet.hurrican.acceptance.location;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

@Test(groups = BaseTest.ACCEPTANCE)
public class AvailabilityService_Test extends AbstractLocationTestBuilder {

    @Autowired
    private AvailabilityService availabilityService;

    /**
     * Tests the creation of a new GeoId cache entry into Hurrican processed by
     * {@link de.augustakom.hurrican.service.cc.impl.AvailabilityServiceHelperImpl#findOrCreateGeoId(Long, Long)}
     */
    @CitrusTest
    @Test
    public void AvailabilityService_01_Test() {
        simulatorUseCase(SimulatorUseCase.AvailabilityService_01, LocationTestVersion.V1);

        Long geoId = 12999999999L;

        hurrican().callFindOrCreateGeoId(geoId);

        atlas().receiveLocationSearch("SEARCH_BUILDINGS_REQUEST");
        atlas().sendLocationSearchResponse("SEARCH_BUILDINGS_RESPONSE");

        hurrican().verifyLocationCreated(geoId);
    }


    /**
     * Tests that the {@link de.augustakom.hurrican.service.cc.AvailabilityServiceHelper#findExact(String, String, String, String, String, String)}
     * method calls the Search API from Atlas and creates the corresponding cache entries in Hurrican if the GeoId does
     * not exist.
     */
    @CitrusTest
    @Test
    public void AvailabilityService_02_Test() {
        simulatorUseCase(SimulatorUseCase.AvailabilityService_02, LocationTestVersion.V1);

        Long geoId1 = 13999999999L;
        Long geoId2 = 14999999999L;

        hurrican().mapLocationDataToGeoIdsTestAction("street", "abc", null, "zip123", "city1", null);

        atlas().receiveLocationSearch("SEARCH_BUILDINGS_REQUEST");
        atlas().sendLocationSearchResponse("SEARCH_BUILDINGS_RESPONSE");

        atlas().receiveLocationSearch("SEARCH_BUILDINGS_REQUEST_GEO1");
        atlas().sendLocationSearchResponse("SEARCH_BUILDINGS_RESPONSE_GEO1");

        atlas().receiveLocationSearch("SEARCH_BUILDINGS_REQUEST_GEO2");
        atlas().sendLocationSearchResponse("SEARCH_BUILDINGS_RESPONSE_GEO2");

        hurrican().verifyLocationCreated(geoId1);
        hurrican().verifyLocationCreated(geoId2);
    }

}
