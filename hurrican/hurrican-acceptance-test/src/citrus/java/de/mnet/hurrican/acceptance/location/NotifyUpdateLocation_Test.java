/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.03.2015
 */
package de.mnet.hurrican.acceptance.location;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.acceptance.builder.GeoIdBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 * Tests the notifyUpdateLocation endpoint (atlas - hurrican interface)
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class NotifyUpdateLocation_Test extends AbstractLocationTestBuilder{

    /**
     * Tests the notifyUpdateLocation asynchronous endpoint in Hurrican by simulating a valid notifyUpdateLocation
     * notification from AtlasESB.
     */
    @CitrusTest
    @Test
    public void NotifyUpdateLocation_01_Test() {
        simulatorUseCase(SimulatorUseCase.NotifyUpdateLocation_01);

        String updatedStreetName = "some new street";
        GeoIdBuilder.GeoIdBuilderResult geoIdResult = hurrican().createGeoId();
        addGeoIdVariables(geoIdResult);
        variables().add(VariableNames.STREET_NAME + ".new", updatedStreetName);
        atlas().sendNotifyUpdateLocationNotification("notifyUpdateLocation");

        hurrican().verifyLocationStreetName(geoIdResult.geoId.getId(), updatedStreetName);
    }

    /**
     * Tests the notifyUpdateLocation asynchronous endpoint in Hurrican by simulating an notifyUpdateLocation
     * notification from AtlasESB. The notification's modified timestamp is older than the modified timestamp
     * stored in hurrican -> the update should therefore be rejected by hurrican, but the notification should be
     * consumed
     */
    @CitrusTest
    @Test
    public void NotifyUpdateLocation_02_Test() {
        simulatorUseCase(SimulatorUseCase.NotifyUpdateLocation_02);

        GeoIdBuilder.GeoIdBuilderResult geoIdResult = hurrican().createGeoId();
        addGeoIdVariables(geoIdResult);

        String originalStreetName = geoIdResult.geoId.getStreet();
        String newStreetName = "some new street";
        variables().add(VariableNames.STREET_NAME + ".new", newStreetName);

        atlas().sendNotifyUpdateLocationNotification("notifyUpdateLocation");

        // allow enough time for the message to be processed by hurrican
        sleep(10000L);

        // check that the street name has not been updated
        // would be nice to verify that the message has really been consumed by hurrican (by checking that the
        // jms queue is empty) but this is not supported out of the box by citrus
        hurrican().verifyLocationStreetName(geoIdResult.geoId.getId(), originalStreetName);
    }


}
