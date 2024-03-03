/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 11:12:33
 */
package de.augustakom.hurrican.model.cc;

import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 * TestNG Klasse fuer {@link HVTGruppe}
 */
@Test(groups = BaseTest.UNIT)
public class HVTGruppeTest extends BaseTest {

    @DataProvider
    public Object[][] houseNumData() {
        return new Object[][] {
                { "1", "1", null },
                { "1-3", "1", "-3" },
                { "1a", "1", "a" },
                { "1A", "1", "A" },
                { "1 A", "1", "A" },
                { "1 1/2", "1", "1/2" },
                { "44/1", "44", "/1" },
        };
    }

    @Test(dataProvider = "houseNumData")
    public void getHausNrSplitted(String houseNumCombi, String expectedHouseNum, String expectedHouseNumAdd) {
        HVTGruppe hvtGruppe = new HVTGruppeBuilder().withHausNr(houseNumCombi).setPersist(false).build();
        String[] splitted = hvtGruppe.getHausNrSplitted();
        assertEquals(splitted[0], expectedHouseNum);
        assertEquals(splitted[1], expectedHouseNumAdd);
    }

}


