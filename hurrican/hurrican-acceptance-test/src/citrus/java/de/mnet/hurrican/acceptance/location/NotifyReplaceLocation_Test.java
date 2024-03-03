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

/**
 * Tests the notifyReplaceLocation endpoint (atlas - hurrican interface)
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class NotifyReplaceLocation_Test extends AbstractLocationTestBuilder{

    /**
     * Tests the notifyReplaceLocation asynchronous endpoint in Hurrican by simulating a valid notifyReplaceLocation
     * payload from AtlasESB.
     */
    @CitrusTest
    @Test
    public void NotifyReplaceLocation_01_Test() {
        simulatorUseCase(SimulatorUseCase.NotifyReplaceLocation_01);

        GeoIdBuilder.GeoIdBuilderResult oldGeoIdResult = hurrican().createGeoId();
        addGeoIdVariables(oldGeoIdResult, "old.");
        GeoIdBuilder.GeoIdBuilderResult newGeoIdResult = hurrican().createGeoId();
        addGeoIdVariables(newGeoIdResult, "new.");

        atlas().sendNotifyReplaceLocationNotification("notifyReplaceLocation");

        hurrican().verifyLocationReplaced(oldGeoIdResult.geoId.getId(), newGeoIdResult.geoId.getId());
    }

    /**
     * Tests the notifyReplaceLocation asynchronous endpoint in Hurrican by simulating a schema-invalid
     * notifyReplaceLocation payload from AtlasESB.
     */
    @CitrusTest
    @Test
    public void NotifyReplaceLocation_02_Test() {
        simulatorUseCase(SimulatorUseCase.NotifyReplaceLocation_02);
        atlas().sendNotifyReplaceLocationNotification("notifyReplaceLocation");

        atlas().receiveErrorHandlingServiceMessage("errorNotification");
    }
}
