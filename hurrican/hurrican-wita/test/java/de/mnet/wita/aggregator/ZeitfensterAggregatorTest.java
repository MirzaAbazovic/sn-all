/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.09.2011 15:24:47
 */
package de.mnet.wita.aggregator;

import static org.testng.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaConfigService;

/**
 * TestNG Klasse fuer {@link ZeitfensterAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class ZeitfensterAggregatorTest extends BaseTest {

    @InjectMocks
    private ZeitfensterAggregator zeitfensterAggregator;

    @Mock
    private WitaConfigService witaConfigService;

    private WitaCBVorgang cbVorgang;


    @BeforeMethod
    public void setup() {
        zeitfensterAggregator = new ZeitfensterAggregator();
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] dataProviderAggregateZeitfensterNotSetSinceFeb2014() {
        // @formatter:off
        return new Object[][] {
                {GeschaeftsfallTyp.KUENDIGUNG_KUNDE, Zeitfenster.SLOT_2},
                {GeschaeftsfallTyp.BEREITSTELLUNG, Zeitfenster.SLOT_9},
                {GeschaeftsfallTyp.PROVIDERWECHSEL, Zeitfenster.SLOT_9},
                {GeschaeftsfallTyp.PORTWECHSEL, Zeitfenster.SLOT_9},
                {GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG, Zeitfenster.SLOT_9},
        };
    }

    @Test(dataProvider = "dataProviderAggregateZeitfensterNotSetSinceFeb2014")
    public void aggregateZeitfensterNotSetSinceFeb2014(GeschaeftsfallTyp gfTyp, Zeitfenster expected) throws Exception {
        cbVorgang = new WitaCBVorgangBuilder().withWitaGeschaeftsfallTyp(gfTyp).setPersist(false).build();
        assertNull(cbVorgang.getRealisierungsZeitfenster());
        assertNotNull(zeitfensterAggregator.aggregate(cbVorgang));
        assertEquals(zeitfensterAggregator.aggregate(cbVorgang), expected);
    }

    @DataProvider
    public Object[][] dataProviderZeitfenster() {
        // @formatter:off
        return new Object[][] {
                { new WitaCBVorgangBuilder().withRealisierungsZeitfenster(Zeitfenster.SLOT_2).setPersist(false).build(), Zeitfenster.SLOT_2 },
                { new WitaCBVorgangBuilder().withRealisierungsZeitfenster(Zeitfenster.SLOT_3).setPersist(false).build(), Zeitfenster.SLOT_3 },
                { new WitaCBVorgangBuilder().withRealisierungsZeitfenster(Zeitfenster.SLOT_4).setPersist(false).build(), Zeitfenster.SLOT_4 },
                { new WitaCBVorgangBuilder().withRealisierungsZeitfenster(Zeitfenster.SLOT_6).setPersist(false).build(), Zeitfenster.SLOT_6 },
                { new WitaCBVorgangBuilder().withRealisierungsZeitfenster(Zeitfenster.SLOT_7).setPersist(false).build(), Zeitfenster.SLOT_7 },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderZeitfenster")
    public void aggregateZeitfenster(WitaCBVorgang cbVorgang, Zeitfenster zeitfenster) throws Exception {
        assertNotNull(cbVorgang.getRealisierungsZeitfenster());
        assertNotNull(zeitfensterAggregator.aggregate(cbVorgang));
        assertEquals(zeitfensterAggregator.aggregate(cbVorgang), zeitfenster);
    }
}
