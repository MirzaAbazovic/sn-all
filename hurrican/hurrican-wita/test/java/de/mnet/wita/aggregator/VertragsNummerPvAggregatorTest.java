/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.02.14
 */
package de.mnet.wita.aggregator;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaWbciServiceFacade;
import de.mnet.wita.service.impl.WitaDataService;

@Test(groups = BaseTest.UNIT)
public class VertragsNummerPvAggregatorTest extends BaseTest {

    @Spy
    @InjectMocks
    private VertragsNummerPvAggregator cut = spy(new VertragsNummerPvAggregator());

    @Mock
    private WitaDataService witaDataService;

    @Mock
    private WitaWbciServiceFacade witaWbciServiceFacade;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void aggregateWithWbciVaId() {
        String witaVertragsNr = "V0001";
        when(witaWbciServiceFacade.checkAndReturnNextWitaVertragsnummern(any(de.mnet.wita.message.GeschaeftsfallTyp.class), anyString(), anyLong(), anyLong())).thenReturn(witaVertragsNr);

        String result = cut.aggregate(buildCbv("DEU.MNET.123"));
        Assert.assertNotNull(result);
        Assert.assertEquals(result, witaVertragsNr);
        verify(witaDataService, never()).loadVorabstimmung(any(WitaCBVorgang.class));
    }

    @Test
    public void aggregateWithOutWbciVaId() {
        String providerVtrNr = "059876";
        Vorabstimmung vorabstimmungMock = mock(Vorabstimmung.class);
        WitaCBVorgang cbVorgang = buildCbv(null);
        when(witaDataService.loadVorabstimmung(cbVorgang)).thenReturn(vorabstimmungMock);
        when(vorabstimmungMock.getProviderVtrNr()).thenReturn(providerVtrNr);

        String result = cut.aggregate(cbVorgang);
        Assert.assertNotNull(result);
        Assert.assertEquals(result, "0000" + providerVtrNr);
        verify(witaWbciServiceFacade, never()).checkAndReturnNextWitaVertragsnummern(any(de.mnet.wita.message.GeschaeftsfallTyp.class), anyString(), anyLong(), anyLong());
    }

    private WitaCBVorgang buildCbv(String vaId) {
        return new WitaCBVorgangBuilder().withVorabstimmungsId(vaId).setPersist(false).build();
    }

}
