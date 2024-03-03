/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.02.2012 09:49:17
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.query;

import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class CPSQueryEntryDataTest extends BaseTest {


    @DataProvider
    public Object[][] dataProviderHasReasonableValues() {
        // @formatter:off
       return new Object[][] {
               { null,  null,   false },
               { "384", null,   false },
               { null , "128",  false },
               { "384", "0",    false },
               { "0",   "128",  false },
               { "0",   "0",    false },
               { "",    "",     false },
               { "384", "128",  true },
       };
       // @formatter:on
    }

    @Test(dataProvider = "dataProviderHasReasonableValues")
    public void testHasReasonableValues(String down, String up, boolean expected) {
        CPSQueryEntryData data = new CPSQueryEntryData();
        data.setMaxAttBrDn(down);
        data.setMaxAttBrUp(up);
        assertEquals(data.hasReasonableValues(), expected);
    }

}
