/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.11.2011 13:42:43
 */
package de.mnet.wita.model;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = UNIT)
public class AbstractWitaModelTest extends BaseTest {

    @DataProvider
    public Object[][] ids() {
        return new Object[][] {
                { 1L, 2L, false },
                { 1L, 1L, true },
                { null, null, false },
                { null, 1L, false },
                { 1L, null, false },
        };
    }

    @Test(dataProvider = "ids")
    public void equalsAndIds(Long id1, Long id2, boolean equals) {
        IoArchive archive1 = new IoArchive();
        IoArchive archive2 = new IoArchive();
        archive1.setId(id1);
        archive2.setId(id2);
        assertEquals(archive1.equals(archive2), equals);
    }

    @DataProvider
    public Object[][] sameInstance() {
        return new Object[][] {
                { 1L },
                { null },
        };
    }

    @Test(dataProvider = "sameInstance")
    public void equalsAndSameInstance(Long id) {
        IoArchive archive = new IoArchive();
        archive.setId(id);
        assertTrue(archive.equals(archive));
    }

}


