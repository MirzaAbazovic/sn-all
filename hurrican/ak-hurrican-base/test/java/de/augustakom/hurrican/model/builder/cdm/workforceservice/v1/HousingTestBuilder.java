/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.util.*;

/**
 * Created by glinkjo on 06.02.2015.
 */
public class HousingTestBuilder extends HousingBuilder {
    
    public HousingTestBuilder() {
        withBuilding("building");
        withStreet("street");
        withZipCode("zipcode");
        withFloor("floor");
        withRoom("room");
        withPlot("plot");
        withRack("rack");
        withRackUnits("rack-units");
        withCircuitCount("2");
        withWattage("2500");
        withFuse("ja");
        withElectricMeter(Arrays.asList(new ElectricMeterTestBuilder().build()));
        withTransponder(Arrays.asList(new TransponderTestBuilder().build()));
    }
    
}
