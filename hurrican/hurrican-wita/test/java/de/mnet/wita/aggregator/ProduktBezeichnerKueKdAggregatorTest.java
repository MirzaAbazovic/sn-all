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
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.builder.AuftragBuilder;

/**
 * TestNG Klasse fuer den {@link ProduktBezeichnerKueKdAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class ProduktBezeichnerKueKdAggregatorTest extends BaseTest {

    private ProduktBezeichnerKueKdAggregator underTest;

    @BeforeMethod
    public void setUp() {
        underTest = new ProduktBezeichnerKueKdAggregator();
    }

    @DataProvider
    public Object[][] data() {
        // @formatter:off
        Auftrag auftragEmpty = new AuftragBuilder(KUENDIGUNG_KUNDE).buildValid();
        Auftrag auftragHvt2N = new AuftragBuilder(KUENDIGUNG_KUNDE).buildKueKdWithLeitungsSchluesselZahl("96U");
        Auftrag auftragHvt2HZwr = new AuftragBuilder(KUENDIGUNG_KUNDE).buildKueKdWithLeitungsSchluesselZahl("96Y");
        Auftrag auftragHvt4HZwr = new AuftragBuilder(KUENDIGUNG_KUNDE).buildKueKdWithLeitungsSchluesselZahl("96Z");
        Auftrag auftragHvt2H = new AuftragBuilder(KUENDIGUNG_KUNDE).buildKueKdWithLeitungsSchluesselZahl("96W");
        Auftrag auftragHvt4H = new AuftragBuilder(KUENDIGUNG_KUNDE).buildKueKdWithLeitungsSchluesselZahl("96X");
        Auftrag auftragLwl = new AuftragBuilder(KUENDIGUNG_KUNDE).buildKueKdWithLeitungsSchluesselZahl("95W");

        return new Object[][] {
                { auftragEmpty, null },
                { auftragHvt2N, ProduktBezeichner.HVT_2N },
                { auftragHvt2HZwr, ProduktBezeichner.HVT_2ZWR },
                { auftragHvt4HZwr, ProduktBezeichner.HVT_4ZWR },
                { auftragHvt2H, ProduktBezeichner.HVT_2H },
                { auftragHvt4H, ProduktBezeichner.HVT_4H },
                { auftragLwl, ProduktBezeichner.LWL },
        };
        // @formatter:on
    }

    @Test(dataProvider = "data")
    public void testLoadDtagProduktBezeichner(Auftrag auftrag, ProduktBezeichner expected) {
        assertEquals(underTest.aggregate(auftrag), expected);
    }

    @DataProvider
    public Object[][] dataProviderLoadDtagProduktBezeichnerForKvz() {
        // @formatter:off
        Auftrag auftragKvz2H = new AuftragBuilder(KUENDIGUNG_KUNDE).buildKueKdWithLeitungsSchluesselZahl("9KW");
        Auftrag auftragKvz4H = new AuftragBuilder(KUENDIGUNG_KUNDE).buildKueKdWithLeitungsSchluesselZahl("9KX");

        return new Object[][] {
                { auftragKvz2H, ProduktBezeichner.KVZ_2H },
                { auftragKvz4H, ProduktBezeichner.KVZ_4H },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderLoadDtagProduktBezeichnerForKvz")
    public void testLoadDtagProduktBezeichnerForKvz(Auftrag auftrag, ProduktBezeichner expected) {
        assertEquals(underTest.aggregate(auftrag), expected);
    }
}
