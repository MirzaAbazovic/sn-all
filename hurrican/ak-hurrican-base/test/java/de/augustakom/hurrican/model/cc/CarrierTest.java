/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.10.13
 */
package de.augustakom.hurrican.model.cc;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class CarrierTest extends BaseTest {

    @DataProvider
    public Object[][] carrierData() {
        return new Object[][] {
                { null, null, null, "" },
                { "D061", "Telefonica", CarrierVaModus.WBCI, "D061 - Telefonica (WBCI)" },
                { "D061", "Telefonica", CarrierVaModus.PORTAL, "D061 - Telefonica (PORTAL)" },
                { null, "Telefonica", CarrierVaModus.PORTAL, " - Telefonica (PORTAL)" },
                { "D061", null, CarrierVaModus.PORTAL, "D061 -  (PORTAL)" },
                { "D061", "Telefonica", null, "D061 - Telefonica ()" },
        };
    }

    @Test(dataProvider = "carrierData")
    public void getPortierungskennungAndNameAndVaModus(String portierungskennung, String name,
            CarrierVaModus vorabstimmungsModus, String expectedOutput) {
        Carrier carrier = new CarrierBuilder()
                .withPortierungskennung(portierungskennung)
                .withName(name)
                .withVorabstimmungsModus(vorabstimmungsModus)
                .build();
        Assert.assertEquals(carrier.getPortierungskennungAndNameAndVaModus(), expectedOutput);
    }

}
