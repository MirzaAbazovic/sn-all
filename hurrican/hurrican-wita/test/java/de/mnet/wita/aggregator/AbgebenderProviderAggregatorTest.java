/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 18:30:57
 */
package de.mnet.wita.aggregator;

import static de.mnet.wita.message.common.Carrier.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.model.VorabstimmungBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaWbciServiceFacade;
import de.mnet.wita.service.impl.WitaDataService;

@Test(groups = BaseTest.UNIT)
public class AbgebenderProviderAggregatorTest extends BaseTest {

    private WitaCBVorgang cbVorgang;

    @Spy
    @InjectMocks
    private AbgebenderProviderAggregator cut = spy(new AbgebenderProviderAggregator());

    private VorabstimmungBuilder vorabstimmungBuilder;

    @Mock
    private WitaDataService witaDataService;

    @Mock
    private WitaWbciServiceFacade witaWbciServiceFacade;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        cbVorgang = new WitaCBVorgangBuilder().build();
        vorabstimmungBuilder = new VorabstimmungBuilder();
    }

    public void aggregateNoProvider() {
        assertProviderEquals(null, vorabstimmungBuilder.withCarrier(null));
    }

    public void aggregateDtagProvider() {
        assertProviderEquals(DTAG, vorabstimmungBuilder.withCarrierDtag());
    }

    public void aggregateO2Provider() {
        assertProviderEquals(OTHER, vorabstimmungBuilder.withCarrierO2());
    }

    private void assertProviderEquals(Carrier expected, VorabstimmungBuilder builder) {
        doReturn(builder.build()).when(witaDataService).loadVorabstimmung(cbVorgang);
        assertEquals(cut.aggregate(cbVorgang), expected);
    }

    @DataProvider(name = "aggregateWithWbciDP")
    public Object[][] aggregateWithWbciDP() {
        return new Object[][] {
                { CarrierCode.DTAG, Carrier.DTAG },
                { CarrierCode.VODAFONE, Carrier.OTHER },
        };
    }


    @Test(dataProvider = "aggregateWithWbciDP")
    public void aggregateWithWbci(CarrierCode wbciAbgebenderCarrier, Carrier expected) {
        doReturn(null).when(witaDataService).loadVorabstimmung(cbVorgang);
        cbVorgang.setVorabstimmungsId("DEU.MNET.123");

        WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAbgebenderEKP(wbciAbgebenderCarrier)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(witaWbciServiceFacade.getWbciGeschaeftsfall(anyString())).thenReturn(gf);

        assertEquals(cut.aggregate(cbVorgang), expected);
    }
}
