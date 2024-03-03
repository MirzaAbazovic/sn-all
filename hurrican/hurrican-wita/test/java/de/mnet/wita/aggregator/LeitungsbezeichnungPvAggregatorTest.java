/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2014
 */
package de.mnet.wita.aggregator;

import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.VorabstimmungBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaWbciServiceFacade;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * TestNG Klasse fuer {@link de.mnet.wita.aggregator.LeitungsbezeichnungPvAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class LeitungsbezeichnungPvAggregatorTest extends BaseTest {

    private WitaCBVorgang cbVorgang;

    @Spy
    @InjectMocks
    private LeitungsbezeichnungPvAggregator cut = spy(new LeitungsbezeichnungPvAggregator());

    @Mock
    private WitaDataService witaDataService;

    @Mock
    private WitaWbciServiceFacade witaWbciServiceFacade;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        cbVorgang = new WitaCBVorgangBuilder().build();
    }

    @DataProvider(name = "aggregateDP")
    public Object[][] aggregateDP() {
        VorabstimmungBuilder vaBuilder = new VorabstimmungBuilder().setPersist(false);
        return new Object[][] {
                { vaBuilder.withProviderLbz("96W/821/821/123").build(), "0821", "96W/821/821/0000000123" },
                { vaBuilder.withProviderLbz("96W/89/89/123").build(), "089", "96W/89/89/0000000123" },
                { vaBuilder.withProviderLbz(null).build(), null, null }
        };
    }


    @Test(dataProvider = "aggregateDP")
    public void aggregate(Vorabstimmung vorabstimmung, String onkz, String expected) {
        doReturn(vorabstimmung).when(witaDataService).loadVorabstimmung(cbVorgang);
        doReturn(onkz).when(witaDataService).loadHVTStandortOnkz4Auftrag(cbVorgang.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);

        LeitungsBezeichnung result = cut.aggregate(cbVorgang);
        if (expected != null) {
            Assert.assertEquals(result.getLeitungsbezeichnungString(), expected);
        }
        else {
            Assert.assertNull(result);
        }
    }


    public void aggregateWithWbci() {
        cbVorgang.setVorabstimmungsId("DEU.MNET.123");
        Assert.assertNull(cut.aggregate(cbVorgang));
    }

}
