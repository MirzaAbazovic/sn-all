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
public class KvzTalPredicateTest extends BaseTest {

    private KvzTalPredicate cut;

    @BeforeMethod
    public void setUp() {
        cut = new KvzTalPredicate();
    }

    @DataProvider
    public Object[][] data() {
        Auftrag auftrag1 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildAuftragWithSchaltungKvz();
        Auftrag auftrag2 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildAuftragWithSchaltungKupfer();
        Auftrag auftrag3 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();
        auftrag3.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().setSchaltangaben(null);

        return new Object[][] {
                { auftrag1, true },
                { auftrag2, false },
                { auftrag3, false },
        };
    }

    @Test(dataProvider = "data")
    public void hvtMatch(Auftrag witaAuftrag, boolean expectedResult) {
        assertEquals(cut.apply(witaAuftrag), expectedResult);
    }

}


