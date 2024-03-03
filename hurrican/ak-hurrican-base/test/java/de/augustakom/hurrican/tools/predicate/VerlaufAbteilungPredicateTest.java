/**
 * Copyright (c) 2010 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.06.2010
 */
package de.augustakom.hurrican.tools.predicate;

import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;

@Test(groups = { "unit" })
public class VerlaufAbteilungPredicateTest {

    @DataProvider
    public Object[][] dataProviderEvaluate() {

        VerlaufAbteilung verlaufAbteilung = new VerlaufAbteilung();
        verlaufAbteilung.setAbteilungId(Abteilung.FIELD_SERVICE);

        Abteilung abteilung = new Abteilung();
        abteilung.setId(Abteilung.AM);

        return new Object[][] {
                { new Long[] { }, "String", false },
                { new Long[] { Abteilung.MQUEUE }, "String", false },

                { new Long[] { Abteilung.ST_CONNECT }, Abteilung.ST_VOICE, false },
                { new Long[] { }, Abteilung.NP, false },
                { new Long[] { Abteilung.DISPO }, Abteilung.DISPO, true }, // !

                { new Long[] { Abteilung.ST_CONNECT, Abteilung.ST_ONLINE, Abteilung.ST_VOICE }, Abteilung.MQUEUE, false },
                { new Long[] { Abteilung.ST_CONNECT, Abteilung.ST_ONLINE, Abteilung.ST_VOICE }, Abteilung.ST_ONLINE,
                        true }, // !

                { new Long[] { Abteilung.ST_ONLINE, Abteilung.FIELD_SERVICE }, abteilung, false },
                { new Long[] { Abteilung.AM, Abteilung.DISPO, Abteilung.NP }, abteilung, true }, // !

                { new Long[] { Abteilung.AM, Abteilung.DISPO }, verlaufAbteilung, false },
                { new Long[] { Abteilung.ST_CONNECT, Abteilung.FIELD_SERVICE }, verlaufAbteilung, true },
        };
    }

    @Test(dataProvider = "dataProviderEvaluate")
    public void testEvaluate(Long[] abtIds2Filter, Object object2Evaluate, boolean expectedResult) {
        VerlaufAbteilungPredicate predicate = new VerlaufAbteilungPredicate(abtIds2Filter);
        assertEquals(predicate.evaluate(object2Evaluate), expectedResult);
    }
}
