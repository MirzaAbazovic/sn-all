/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.11.2011 19:01:50
 */
package de.mnet.wita.message;

import static de.mnet.wita.TestGroups.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = UNIT)
public class GeschaeftsfallTypTest {

    public void buildFromRequestUnknown() {
        assertEquals(GeschaeftsfallTyp.buildFromRequest("Kenn ich nicht"), UNBEKANNT);
    }

    public void buildFromMeldungUnknown() {
        assertEquals(GeschaeftsfallTyp.buildFromMeldung("Kenn ich nicht"), UNBEKANNT);
    }

    @DataProvider
    public Object[][] geschaeftsfallTypen() {
        GeschaeftsfallTyp[] typen = GeschaeftsfallTyp.values();
        Object[][] result = new Object[typen.length][];
        int i = 0;
        for (GeschaeftsfallTyp typ : typen) {
            result[i] = new Object[] { typ };
            i++;
        }
        return result;
    }

    @Test(dataProvider = "geschaeftsfallTypen")
    public void buildFromMeldungShouldWork(GeschaeftsfallTyp typ) {
        GeschaeftsfallTyp builtFromMeldung = GeschaeftsfallTyp.buildFromMeldung(typ.getDtagMeldungGeschaeftsfall());
        assertEquals(builtFromMeldung, typ);
    }

    @Test(dataProvider = "geschaeftsfallTypen")
    public void buildFromRequestShouldWork(GeschaeftsfallTyp typ) {
        GeschaeftsfallTyp builtFromMeldung = GeschaeftsfallTyp.buildFromRequest(typ.getDtagRequestGeschaeftsfall());
        if (typ.getDtagRequestGeschaeftsfall() == null) {
            assertEquals(builtFromMeldung, GeschaeftsfallTyp.UNBEKANNT);
        }
        else {
            assertEquals(builtFromMeldung, typ);
        }
    }
}


