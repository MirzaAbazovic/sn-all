/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.2011 13:36:49
 */
package de.mnet.wita.common;

import static de.mnet.wita.TestGroups.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.message.common.LeitungsBezeichnung;

@Test(groups = UNIT)
public class LeitungsBezeichnungTest {

    @DataProvider
    public Object[][] lbzTest() {
        // formatter:off
        return new Object[][] {
                { "96W/00821/0821/123456", "96W", "821", "821", "0000123456" },
                { "96W / 0821/ 821 /  123456", "96W", "821", "821", "0000123456" },
                { "96W / 0821/ 821 /", "96W", "821", "821", null },
                { "96W / 8210/82100/", "96W", "821", "821", null },
                { "96W / 8210/82100/", "96W", "821", "821", null },
                { "96W / 8210/82100/", "96W", "8210", "8210", null },
                { null, null, null, null, null }, };
        // formatter:on
    }

    @Test(dataProvider = "lbzTest")
    public void testLbzColumns(String lbz, String leitungsschluesselZahl, String onkzKunde, String onkzKollokation,
            String ordnungsnummer) {
        LeitungsBezeichnung leitungsBezeichnung = new LeitungsBezeichnung(lbz, onkzKunde);
        assertEquals(leitungsBezeichnung.getLeitungsSchluesselZahl(), leitungsschluesselZahl);
        assertEquals(leitungsBezeichnung.getOnkzKunde(), onkzKunde);
        assertEquals(leitungsBezeichnung.getOnkzKollokation(), onkzKollokation);
        assertEquals(leitungsBezeichnung.getOrdnungsNummer(), ordnungsnummer);
    }
}
