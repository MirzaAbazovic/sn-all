/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.2016
 */
package de.augustakom.common.tools.lang;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class PairTest extends BaseTest {

    @Test
    public void equals_PairsAreEqual_ReturnTrue(){
        Pair<Long, Integer> pair1 = Pair.create(1L, 2);
        Pair<Long, Integer> pair2 = Pair.create(1L, 2);
        boolean result = pair1.equals(pair2);
        assertTrue(result);
    }

    @Test
    public void equals_PairsAreNotEqual_ReturnFalse(){
        Pair<Long, Integer> pair1 = Pair.create(1L, 2);
        Pair<Long, Integer> pair2 = Pair.create(2L, 2);
        boolean result = pair1.equals(pair2);
        assertFalse(result);
    }

}