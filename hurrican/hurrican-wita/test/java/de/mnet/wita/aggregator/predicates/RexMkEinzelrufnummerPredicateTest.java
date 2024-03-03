/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2011 17:59:16
 */
package de.mnet.wita.aggregator.predicates;

import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.builder.AuftragBuilder;

@Test(groups = BaseTest.UNIT)
public class RexMkEinzelrufnummerPredicateTest extends BaseTest {

    private RexMkEinzelrufnummerPredicate cut;

    @BeforeMethod
    public void setUp() {
        cut = new RexMkEinzelrufnummerPredicate();
    }

    @DataProvider
    public Object[][] data() {
        Auftrag auftrag1 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildAuftragWithEinzelrufnummer(1);
        Auftrag auftrag2 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildAuftragWithEinzelrufnummer(2);
        Auftrag auftrag3 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildAuftragWithAnlagenrufnummer();
        Auftrag auftrag4 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();

        return new Object[][] {
                { auftrag1, true },
                { auftrag2, false },
                { auftrag3, false },
                { auftrag4, false },
        };
    }

    @Test(dataProvider = "data")
    public void hvtMatch(Auftrag witaAuftrag, boolean expectedResult) {
        assertEquals(cut.apply(witaAuftrag), expectedResult);
    }

}


