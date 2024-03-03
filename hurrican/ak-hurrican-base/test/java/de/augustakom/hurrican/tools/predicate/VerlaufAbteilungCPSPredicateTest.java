/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.06.2010 10:48:26
 */
package de.augustakom.hurrican.tools.predicate;

import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;

@Test(groups = { "unit" })
public class VerlaufAbteilungCPSPredicateTest {

    @DataProvider
    public Object[][] dataProviderEvaluate() {

        Abteilung abteilungFalse = new Abteilung();
        abteilungFalse.setId(Abteilung.FIELD_SERVICE);
        Abteilung abteilungTrue = new Abteilung();
        abteilungTrue.setId(Abteilung.ST_ONLINE);

        VerlaufAbteilung verlaufAbteilungFalse = new VerlaufAbteilung();
        verlaufAbteilungFalse.setAbteilungId(Abteilung.AM);
        VerlaufAbteilung verlaufAbteilungTrue = new VerlaufAbteilung();
        verlaufAbteilungTrue.setAbteilungId(Abteilung.MQUEUE);

        return new Object[][] {
                { Abteilung.AM, false },
                { Abteilung.DISPO, false },
                { Abteilung.NP, false },
                { Abteilung.ST_CONNECT, false },
                { Abteilung.FIELD_SERVICE, false },
                { Abteilung.EXTERN, false },
                { abteilungFalse, false },
                { verlaufAbteilungFalse, false },

                { Abteilung.ST_ONLINE, true },
                { Abteilung.ST_VOICE, true },
                { Abteilung.MQUEUE, true },
                { abteilungTrue, true },
                { verlaufAbteilungTrue, true },

        };
    }

    @Test(dataProvider = "dataProviderEvaluate")
    public void testEvaluate(Object object2Check, boolean expectedResult) {
        assertEquals(new VerlaufAbteilungCPSPredicate().evaluate(object2Check), expectedResult);
    }
}
