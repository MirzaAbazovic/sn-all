/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.12.2011 09:55:17
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
public class TalHochbitPredicateTest extends BaseTest {

    private TalHochbitPredicate cut;

    @BeforeMethod
    public void setUp() {
        cut = new TalHochbitPredicate();
    }

    @DataProvider
    public Object[][] data() {
        Auftrag auftrag1 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildAuftragWithSchaltungKupfer();
        Auftrag auftrag2 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildAuftragWithSchaltungKupfer();
        auftrag2.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().getSchaltangaben().getSchaltungKupfer().get(0).setUebertragungsverfahren(null);
        Auftrag auftrag3 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildAuftragWithSchaltungKvz();

        return new Object[][] {
                { auftrag1, true },
                { auftrag2, false },
                { auftrag3, false },
        };
    }

    @Test(dataProvider = "data")
    public void tal2NMatch(Auftrag witaAuftrag, boolean expectedResult) {
        assertEquals(cut.apply(witaAuftrag), expectedResult);
    }

}


