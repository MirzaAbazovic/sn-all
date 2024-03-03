/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.13
 */
package de.mnet.wbci.model;

import static de.mnet.wbci.TestGroups.*;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = UNIT)
public class WbciEntityTest {

    @DataProvider
    public Object[][] stripLeadingZerosDataProvider() {
        return new Object[][] {
                { "0123", "123" },
                { "1023", "1023" },
                { null, null },
        };
    }


    @Test(dataProvider = "stripLeadingZerosDataProvider")
    public void testStripLeadingZero(String in, String expected) {
        final WbciEntity cut = new VorabstimmungsAnfrage();
        Assert.assertEquals(cut.stripLeadingZero(in), expected);
    }

    @DataProvider
    public Object[][] addLeadingZeroDataProvider() {
        // @formatter:off
        return new Object[][] {
                { "89", "089" },
                { "089", "0089" },
                { "", "0" },
                { null, null },
        };
        // @formatter:on
    }

    @Test(dataProvider = "addLeadingZeroDataProvider")
    public void testAddLeadingZero(String onkz, String expected) {
        final WbciEntity cut = new VorabstimmungsAnfrage();
        Assert.assertEquals(cut.addLeadingZero(onkz), expected);
    }

}
