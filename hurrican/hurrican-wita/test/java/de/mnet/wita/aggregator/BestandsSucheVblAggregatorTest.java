/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2011 11:38:17
 */
package de.mnet.wita.aggregator;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.builder.PersonTestBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungBuilder;
import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.model.VorabstimmungBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaWbciServiceFacade;
import de.mnet.wita.service.impl.WitaDataService;

@Test(groups = BaseTest.UNIT)
public class BestandsSucheVblAggregatorTest extends BaseTest {

    @InjectMocks
    private BestandsSucheVblAggregator cut = new BestandsSucheVblAggregator();
    @Mock
    private WitaDataService witaDataService;
    @Mock
    private WitaWbciServiceFacade witaWbciServiceFacade;

    private WitaCBVorgang cbVorgang;
    private static WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cbVorgang = new WitaCBVorgangBuilder()
                        .withWitaGeschaeftsfallTyp(VERBUNDLEISTUNG)
                        .setPersist(false)
                        .build();
    }

    public void aggregateEinzelanschlussWitaVorabstimmung() throws Exception {
        BestandsSuche achieved = aggregate(new VorabstimmungBuilder().withBestandssucheEinzelanschluss());
        assertNotNull(achieved.getOnkz());
        assertNotNull(achieved.getRufnummer());
    }


    public void bestandsSucheNotSet() throws Exception {
        assertNull(aggregate(new VorabstimmungBuilder()));
    }

    private BestandsSuche aggregate(VorabstimmungBuilder pvBuilder) throws Exception {
        when(witaDataService.loadVorabstimmung(cbVorgang)).thenReturn(pvBuilder.build());
        return cut.aggregate(cbVorgang);
    }

    private BestandsSuche aggregateWithWbci(RueckmeldungVorabstimmung ruemVa) throws Exception {
        when(witaWbciServiceFacade.getRuemVa(cbVorgang.getVorabstimmungsId())).thenReturn(ruemVa);
        return cut.aggregate(cbVorgang);
    }

    @Test(expectedExceptions = WitaDataAggregationException.class)
    public void aggregateWbciNoRuemVa() throws Exception {
        cbVorgang.setVorabstimmungsId("DEU.MNET.V00000001");
        BestandsSuche achieved = aggregateWithWbci(null);
        assertNotNull(achieved.getOnkz());
        assertNotNull(achieved.getRufnummer());
    }

    public void aggregateWbciRuemVaWithoutRufnummer() throws Exception {
        cbVorgang.setVorabstimmungsId("DEU.MNET.V00000001");

        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAbgebenderEKP(CarrierCode.DTAG)
                .buildValid(wbciCdmVersion, GeschaeftsfallTyp.VA_KUE_MRN);

        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungBuilder()
                .withRufnummernportierung(null)
                .build();
        ruemVa.setWbciGeschaeftsfall(wbciGeschaeftsfall);

        when(witaDataService.loadVorabstimmung(cbVorgang)).thenReturn(
                new VorabstimmungBuilder().withBestandssucheEinzelanschluss().build());
        BestandsSuche achieved = aggregateWithWbci(ruemVa);
        assertNotNull(achieved);
        verify(witaDataService).loadVorabstimmung(any(WitaCBVorgang.class));
    }

    public void aggregateWbciRuemVaWithEinzeln() throws Exception {
        cbVorgang.setVorabstimmungsId("DEU.MNET.V00000001");
        String onkz = "089";
        String rufnummer = "123456789";

        RueckmeldungVorabstimmung ruemVa = createRuemVa(onkz, rufnummer, CarrierCode.DTAG);

        BestandsSuche achieved = aggregateWithWbci(ruemVa);
        assertEquals(achieved.getOnkz(), "89");
        assertEquals(achieved.getRufnummer(), rufnummer);
    }

    public void aggregateWbciRuemVaWithAnlage() throws Exception {
        cbVorgang.setVorabstimmungsId("DEU.MNET.V00000001");
        String onkz = "089";
        String rufnummer = "123456789";

        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAbgebenderEKP(CarrierCode.DTAG)
                .buildValid(wbciCdmVersion, GeschaeftsfallTyp.VA_KUE_MRN);

        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungBuilder()
                .withRufnummernportierung(
                        new RufnummernportierungAnlageBuilder()
                                .withOnkz(onkz)
                                .withDurchwahlnummer(rufnummer)
                                .build()
                )
                .build();
        ruemVa.setWbciGeschaeftsfall(wbciGeschaeftsfall);

        VorabstimmungBuilder pvBuilder = new VorabstimmungBuilder().setPersist(false).withBestandssucheAnlagenanschluss();
        when(witaDataService.loadVorabstimmung(cbVorgang)).thenReturn(pvBuilder.build());

        BestandsSuche achieved = aggregateWithWbci(ruemVa);
        assertEquals(achieved.getOnkz(), pvBuilder.get().getBestandssucheOnkzWithoutLeadingZeros());
        assertEquals(achieved.getRufnummer(), pvBuilder.get().getBestandssucheDn());

        verify(witaDataService).loadVorabstimmung(cbVorgang);
    }


    public void aggregateWbciRuemVaPartnerCarrierNotDtagNullResultExpected() throws Exception {
        cbVorgang.setVorabstimmungsId("DEU.MNET.V00000001");
        String onkz = "089";
        String rufnummer = "123456789";

        RueckmeldungVorabstimmung ruemVa = createRuemVa(onkz, rufnummer, CarrierCode.VODAFONE);
        BestandsSuche achieved = aggregateWithWbci(ruemVa);
        assertNull(achieved, "BestandsSuche should be null because WBCI partner carrier is NOT DTAG!");
    }


    private RueckmeldungVorabstimmung createRuemVa(String onkz, String rufnummer, CarrierCode partnerCarrier) {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAbgebenderEKP(partnerCarrier)
                .buildValid(wbciCdmVersion, GeschaeftsfallTyp.VA_KUE_MRN);

        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungBuilder()
                .withRufnummernportierung(
                        new RufnummernportierungEinzelnBuilder()
                                .addRufnummer(
                                        new RufnummerOnkzBuilder()
                                                .withOnkz(onkz)
                                                .withRufnummer(rufnummer)
                                                .build()
                                )
                                .build()
                )
                .build();
        ruemVa.setWbciGeschaeftsfall(wbciGeschaeftsfall);
        return ruemVa;
    }

    public void aggregate_KUE_ORN_withoutPortierung() throws Exception {
        final RueckmeldungVorabstimmung ruemVa = createRuemVa(null, null, CarrierCode.DTAG);
        ruemVa.setWbciGeschaeftsfall(new WbciGeschaeftsfallKueOrn());
        ruemVa.getWbciGeschaeftsfall().setEndkunde(new PersonTestBuilder().buildValid(wbciCdmVersion, GeschaeftsfallTyp.VA_KUE_ORN));
        ruemVa.getWbciGeschaeftsfall().setAbgebenderEKP(CarrierCode.DTAG);
        ruemVa.getWbciGeschaeftsfall().setAufnehmenderEKP(CarrierCode.DTAG);

        ruemVa.setRufnummernportierung(null);
        cbVorgang.setVorabstimmungsId("1231");
        when(witaWbciServiceFacade.getRuemVa(anyString())).thenReturn(ruemVa);
        final BestandsSuche result = cut.aggregate(cbVorgang);
        assertNotNull(result);
        assertEquals(result.getOnkz(), BestandsSucheVblAggregator.DUMMY_ONKZ);
        assertEquals(result.getRufnummer(), BestandsSucheVblAggregator.DUMMY_DN);
    }
}
