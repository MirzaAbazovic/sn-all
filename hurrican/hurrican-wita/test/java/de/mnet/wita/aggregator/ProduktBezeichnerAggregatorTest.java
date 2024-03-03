/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 13:00:45
 */
package de.mnet.wita.aggregator;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.auftrag.SchaltungKupferBuilder;

/**
 * TestNG Klasse fuer den {@link AuftragspositionAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class ProduktBezeichnerAggregatorTest extends BaseTest {

    private ProduktBezeichnerAggregator underTest;

    @BeforeMethod
    public void setUp() {
        underTest = new ProduktBezeichnerAggregator();
    }

    @DataProvider
    public Object[][] data() {
        // @formatter:off
        Auftrag auftragEmpty = new AuftragBuilder(BEREITSTELLUNG).buildWithoutGeschaeftsfall();
        SchaltungKupfer schaltungKupfer = new SchaltungKupferBuilder().withUebertragungsverfahren(null)
                .withDoppelader("1").withEVS("1").build();
        Auftrag auftragHvt2N = new AuftragBuilder(BEREITSTELLUNG).buildAuftragWithSchaltungKupfer(schaltungKupfer);
        Auftrag auftragHvt2H = new AuftragBuilder(BEREITSTELLUNG).buildAuftragWithSchaltungKupfer();
        Auftrag auftragHvt4H = new AuftragBuilder(BEREITSTELLUNG).buildAuftragWithSchaltungKupferVierDraht();
        Auftrag auftragKvz2H = new AuftragBuilder(BEREITSTELLUNG).buildAuftragWithSchaltungKvz();

        return new Object[][] {
                { auftragEmpty, null, true },
                { auftragHvt2N, ProduktBezeichner.HVT_2N, false },
                { auftragHvt2H, ProduktBezeichner.HVT_2H, false },
                { auftragHvt4H, ProduktBezeichner.HVT_4H, false },
                { auftragKvz2H, ProduktBezeichner.KVZ_2H, false },
            };
        // @formatter:on
    }

    @Test(dataProvider = "data")
    public void testLoadDtagProduktBezeichner(Auftrag auftrag, ProduktBezeichner expected, boolean expectException) {
        try {
            assertEquals(underTest.aggregate(auftrag), expected);

            assertFalse(expectException, "No exception was thrown but one expected");
        }
        catch (WitaDataAggregationException e) {
            assertTrue(expectException, "Exception was thrown but none expected");
        }
    }
}
