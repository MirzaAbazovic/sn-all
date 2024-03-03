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
public class Tal4DrahtPredicateTest extends BaseTest {

    private Tal4DrahtPredicate cut;

    @BeforeMethod
    public void setUp() {
        cut = new Tal4DrahtPredicate();
    }

    @DataProvider
    public Object[][] data() {
        Auftrag auftrag1 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildAuftragWithSchaltungKupfer();
        Auftrag auftrag2 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildAuftragWithSchaltungKupferVierDraht();
        Auftrag auftrag3 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildAuftragWithSchaltungKvz();
        Auftrag auftrag4 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();
        auftrag4.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().setSchaltangaben(null);

        return new Object[][] {
                { auftrag1, false },
                { auftrag2, true },
                { auftrag3, false },
                { auftrag4, false },
        };
    }

    @Test(dataProvider = "data")
    public void tal2NMatch(Auftrag witaAuftrag, boolean expectedResult) {
        assertEquals(cut.apply(witaAuftrag), expectedResult);
    }

}


