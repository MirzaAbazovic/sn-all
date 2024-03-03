/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.2011 13:36:49
 */
package de.augustakom.hurrican.model.cc;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class CarrierbestellungTest extends BaseTest {

    @DataProvider
    public Object[][] toSplitData() {
        // @formatter:off
        return new Object[][] {
                {null, null},
                {"1/2/3m/4", 10},
                {"123/345", 468},
                {"123 345", 468},
                {"123'345", 468},
                {"123*345", 468},
                {"123^345", 468},
                {"123&345", 468},
                {"123(/345", 468},
                {"123_345/5", 473},
                {"123\t345(arst", 468},
                {"123m/345(arst", 468},
                {"01/0433", 434},
                {"1/1/3/2/5/2", 14},
        };
        // @formatter:on
    }

    @Test(dataProvider = "toSplitData")
    public void testCalcLlSum(String ll, Integer result) {
        Carrierbestellung carrierbestellung = new Carrierbestellung();
        carrierbestellung.setLl(ll);
        assertEquals(carrierbestellung.calcLlSum(), result);
    }

    public void newCarrierbestellungIsLeereCarrierbestellung() {
        assertTrue(new Carrierbestellung().isLeereCarrierbestellung());
    }

    public void carrierbestellungWithRueckmeldedatenIsNotLeer() {
        Carrierbestellung cb = new Carrierbestellung();
        cb.setZurueckAm(new Date());
        assertFalse(cb.isLeereCarrierbestellung());
    }

}
