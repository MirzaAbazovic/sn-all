/*
 * Copyright (c) 2017 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.03.2017
 */
package de.mnet.wbci.config;


import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class WbciConstantsTest {

    @DataProvider(name = "provider")
    public static Object[][] dataProvider() {
        return new Object[][] {
                { "DEU.MNET.VF00087423", true },
                { "DEU.MNET.VH00087423", false },
                { "DEU.MNET.V000087423", false },
                { "AC1.123.TF000123daw", true },
                { "DTAG.NEU.SF00075434", true },
                { "a.s.lala", false }
        };
    }

    @Test(dataProvider = "provider")
    public void isFaxRouting_Parameterized(String vorabstimmungsId, boolean expectedReturn) {
        Assert.assertEquals(expectedReturn, WbciConstants.isFaxRouting(vorabstimmungsId));
    }
}