/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2011 08:14:09
 */
package de.mnet.wita.aggregator;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import com.google.common.base.Strings;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.CarrierKennungBuilder;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.wita.message.auftrag.Kunde;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * TestNG Klasse fuer {@link KundeAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class KundeAggregatorTest extends BaseTest {

    @InjectMocks
    private KundeAggregator cut;

    @Mock
    private CarrierService carrierServiceMock;

    private WitaCBVorgang cbVorgang;

    @Mock
    WitaDataService witaDataService;

    @BeforeMethod
    public void setUp() {
        cut = new KundeAggregator();
        MockitoAnnotations.initMocks(this);
        cbVorgang = new WitaCBVorgangBuilder().setPersist(false).build();
    }

    @DataProvider
    public Object[][] dataProviderAggregate() {
        CarrierKennung kennungMnetMuc = new CarrierKennungBuilder().withId(CarrierKennung.ID_MNET_MUENCHEN)
                .withKundenNr(CarrierKennung.DTAG_KUNDEN_NR_MNET).setPersist(false).build();
        CarrierKennung kennungMnetAgb = new CarrierKennungBuilder().withId(CarrierKennung.ID_MNET_AUGSBURG)
                .withKundenNr(CarrierKennung.DTAG_KUNDEN_NR_MNET).setPersist(false).build();
        CarrierKennung kennungAugustaKom = new CarrierKennungBuilder().withId(CarrierKennung.ID_AUGUSTAKOM)
                .withKundenNr(CarrierKennung.DTAG_KUNDEN_NR_AUGUSTAKOM).setPersist(false).build();
        // @formatter:off
        return new Object[][] {
                { kennungMnetMuc, null },
                { kennungMnetAgb, null },
                { kennungAugustaKom, kennungMnetMuc },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderAggregate")
    public void testAggregate(CarrierKennung kennungKunde, CarrierKennung kennungBesteller) throws Exception {
        when(witaDataService.loadCarrierKennung(cbVorgang)).thenReturn(kennungKunde);
        if (kennungBesteller != null) {
            when(carrierServiceMock.findCarrierKennung(kennungBesteller.getId())).thenReturn(kennungBesteller);
        }

        Pair<Kunde, Kunde> result = cut.aggregate(cbVorgang);
        assertNotNull(result);

        Kunde kunde = result.getFirst();
        assertNotNull(kunde);
        assertEquals(kunde.getKundennummer(), kennungKunde.getKundenNr());
        assertEquals(kunde.getLeistungsnummer(), Strings.padStart(kennungKunde.getWitaLeistungsNr(), 10, '0'));

        Kunde besteller = result.getSecond();
        if (kennungBesteller == null) {
            assertNull(besteller);
        }
        else {
            assertNotNull(besteller);
            assertEquals(besteller.getKundennummer(), kennungBesteller.getKundenNr());
            assertEquals(besteller.getLeistungsnummer(),
                    Strings.padStart(kennungBesteller.getWitaLeistungsNr(), 10, '0'));
        }
    }


    public void testAggregateForRexMk() {
        cbVorgang.setTyp(CBVorgang.TYP_REX_MK);

        CarrierKennung kennungMnetMuc = new CarrierKennungBuilder().withId(CarrierKennung.ID_MNET_MUENCHEN)
                .withKundenNr(CarrierKennung.DTAG_KUNDEN_NR_MNET).setPersist(false).build();
        when(witaDataService.loadCarrierKennungForRexMk(cbVorgang)).thenReturn(kennungMnetMuc);

        Pair<Kunde, Kunde> result = cut.aggregate(cbVorgang);

        assertNotNull(result);
        assertThat(result.getFirst().getKundennummer(), equalTo(kennungMnetMuc.getKundenNr()));
        assertNull(result.getSecond());

        verify(witaDataService).loadCarrierKennungForRexMk(cbVorgang);
    }

}
