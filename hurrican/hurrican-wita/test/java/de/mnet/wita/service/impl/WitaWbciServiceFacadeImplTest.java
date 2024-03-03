/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.14
 */
package de.mnet.wita.service.impl;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.mnet.wbci.dao.VorabstimmungIdsByBillingOrderNoCriteria;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.DecisionAttribute;
import de.mnet.wbci.model.DecisionResult;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Leitung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.DecisionVOBuilder;
import de.mnet.wbci.model.builder.LeitungBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDecisionService;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.attribute.AufnehmenderProviderBuilder;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaWbciServiceFacade;

/**
 *
 */
@Test(groups = UNIT)
public class WitaWbciServiceFacadeImplTest {
    
    @Spy
    @InjectMocks
    private WitaWbciServiceFacadeImpl testling;
    @Mock
    private WbciDecisionService wbciDecisionService;
    @Mock
    private WbciCommonService wbciCommonService;
    @Mock
    private WitaTalOrderService witaTalOrderService;
    @Mock
    private WitaConfigService witaConfigService;

    private String vorabstimmungsId = "DEU.MNET.0001";

    @BeforeMethod
    public void setup() {
        testling = new WitaWbciServiceFacadeImpl();
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider(name = "getWitaVersions")
    public static Object[][] getWitaVersions() {
        return new WitaCdmVersion[][] {
                {WitaCdmVersion.V1},
                {WitaCdmVersion.V2}
        };
    }

    @Test
    public void testGetCheckedWbciGeschaeftsfall() throws Exception {
        String vorabstimmungsId = "DEU.MNET.NOTVALID";
        when(wbciCommonService.findWbciGeschaeftsfall(anyString())).thenReturn(new WbciGeschaeftsfallKueMrn());
        Assert.assertNotNull(testling.getCheckedWbciGeschaeftsfall(vorabstimmungsId));
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Zu der Vorabstimmungs-ID 'DEU.MNET.NOTVALID' konnte keine gültige WBCI-Vorabstimmung gefunden werden!")
    public void testGetCheckedWbciGeschaeftsfallNotFound() throws Exception {
        String vorabstimmungsId = "DEU.MNET.NOTVALID";
        when(wbciCommonService.findWbciGeschaeftsfall(anyString())).thenReturn(null);
        testling.getCheckedWbciGeschaeftsfall(vorabstimmungsId);
    }

    @Test
    public void testGetAutomaticAnswerForAkmPvFromWbciNoVorabstimmungFound() throws Exception {
        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder()
                .withVorabstimmungsId("DEU.MNET.V1234567890").build();
        when(wbciCommonService.findWbciRequestByType(akmPv.getVorabstimmungsId(), VorabstimmungsAnfrage.class)).thenReturn(null);

        Map<WitaTaskVariables, Object> variablesObjectMap = testling.getAutomaticAnswerForAkmPv(akmPv);
        verify(wbciCommonService).findWbciRequestByType(akmPv.getVorabstimmungsId(), VorabstimmungsAnfrage.class);
        assertEquals(variablesObjectMap.size(), 2);
        assertEquals(variablesObjectMap.get(WitaTaskVariables.RUEM_PV_ANTWORTCODE), RuemPvAntwortCode.SONSTIGES);
        assertNotNull(variablesObjectMap.get(WitaTaskVariables.RUEM_PV_ANTWORTTEXT));
    }

    @Test
    public void testGetAutomaticAnswerForAkmPvFromWbciNegativeRuemVa() throws Exception {
        final String vorabstimmungsId = "DEU.MNET.V1234567890";
        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder()
                .withVorabstimmungsId(vorabstimmungsId).build();
        final VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                        .withWbciGeschaeftsfall(
                                new WbciGeschaeftsfallKueMrnTestBuilder()
                                        .withVorabstimmungsId(vorabstimmungsId)
                                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)
                        )
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciCommonService.findWbciRequestByType(akmPv.getVorabstimmungsId(), VorabstimmungsAnfrage.class))
                .thenReturn(Arrays.<VorabstimmungsAnfrage>asList(vorabstimmungsAnfrage));
        when(wbciCommonService.findLastForVaId(akmPv.getVorabstimmungsId(), UebernahmeRessourceMeldung.class))
                .thenReturn(null);

        Map<WitaTaskVariables, Object> variablesObjectMap = testling.getAutomaticAnswerForAkmPv(akmPv);
        verify(wbciCommonService).findWbciRequestByType(akmPv.getVorabstimmungsId(), VorabstimmungsAnfrage.class);
        verify(wbciCommonService).findLastForVaId(akmPv.getVorabstimmungsId(), UebernahmeRessourceMeldung.class);

        assertEquals(variablesObjectMap.size(), 2);
        assertEquals(variablesObjectMap.get(WitaTaskVariables.RUEM_PV_ANTWORTCODE), RuemPvAntwortCode.SONSTIGES);
        assertNotNull(variablesObjectMap.get(WitaTaskVariables.RUEM_PV_ANTWORTTEXT));
    }

    @Test
    public void testGetAutomaticAnswerForAkmPvFromWbciWechselterminAbweichend() throws Exception {
        final String vorabstimmungsId = "DEU.MNET.V1234567890";
        final LocalDate uebernahmeDatumGeplant = LocalDate.of(2014, 1, 20);
        final LocalDate wechselTermin = LocalDate.of(2014, 1, 22);
        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder()
                .withVorabstimmungsId(vorabstimmungsId)
                .withAufnehmenderProvider(
                        new AufnehmenderProviderBuilder()
                                .withUebernahmeDatumGeplant(uebernahmeDatumGeplant)
                                .build()
                )
                .build();
        final VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                        .withRequestStatus(WbciRequestStatus.AKM_TR_EMPFANGEN)
                        .withWbciGeschaeftsfall(
                                new WbciGeschaeftsfallKueMrnTestBuilder()
                                        .withVorabstimmungsId(vorabstimmungsId)
                                        .withWechseltermin(wechselTermin)
                                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)
                        )
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciCommonService.findWbciRequestByType(akmPv.getVorabstimmungsId(), VorabstimmungsAnfrage.class))
                .thenReturn(Arrays.<VorabstimmungsAnfrage>asList(vorabstimmungsAnfrage));
        when(wbciCommonService.findLastForVaId(akmPv.getVorabstimmungsId(), UebernahmeRessourceMeldung.class))
                .thenReturn(new UebernahmeRessourceMeldungTestBuilder()
                        .withUebernahme(Boolean.TRUE)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN));

        Map<WitaTaskVariables, Object> variablesObjectMap = testling.getAutomaticAnswerForAkmPv(akmPv);
        verify(wbciCommonService).findWbciRequestByType(akmPv.getVorabstimmungsId(), VorabstimmungsAnfrage.class);
        verify(wbciCommonService).findLastForVaId(akmPv.getVorabstimmungsId(), UebernahmeRessourceMeldung.class);

        assertEquals(variablesObjectMap.size(), 2);
        assertEquals(variablesObjectMap.get(WitaTaskVariables.RUEM_PV_ANTWORTCODE), RuemPvAntwortCode.TERMIN_UNGUELTIG);
        assertEquals(variablesObjectMap.get(WitaTaskVariables.RUEM_PV_ANTWORTTEXT), WitaWbciServiceFacade.AKM_PV_TERMIN_ABWEICHEND);
    }

    @Test
    public void testGetAutomaticAnswerForAkmPvFromWbci() throws Exception {
        final String vorabstimmungsId = "DEU.MNET.V1234567890";
        final LocalDate uebernahmeDatumGeplant = LocalDate.of(2014, 1, 20);
        final LocalDate wechselTermin = LocalDate.of(2014, 1, 20);
        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder()
                .withVorabstimmungsId(vorabstimmungsId)
                .withAufnehmenderProvider(
                        new AufnehmenderProviderBuilder()
                                .withUebernahmeDatumGeplant(uebernahmeDatumGeplant)
                                .build()
                )
                .build();
        final VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                        .withRequestStatus(WbciRequestStatus.AKM_TR_EMPFANGEN)
                        .withWbciGeschaeftsfall(
                                new WbciGeschaeftsfallKueMrnTestBuilder()
                                        .withVorabstimmungsId(vorabstimmungsId)
                                        .withWechseltermin(wechselTermin)
                                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)
                        )
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciCommonService.findWbciRequestByType(akmPv.getVorabstimmungsId(), VorabstimmungsAnfrage.class))
                .thenReturn(Arrays.<VorabstimmungsAnfrage>asList(vorabstimmungsAnfrage));
        when(wbciCommonService.findLastForVaId(akmPv.getVorabstimmungsId(), UebernahmeRessourceMeldung.class))
                .thenReturn(new UebernahmeRessourceMeldungTestBuilder()
                        .withUebernahme(Boolean.TRUE)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN));

        Map<WitaTaskVariables, Object> variablesObjectMap = testling.getAutomaticAnswerForAkmPv(akmPv);
        verify(wbciCommonService).findWbciRequestByType(akmPv.getVorabstimmungsId(), VorabstimmungsAnfrage.class);
        verify(wbciCommonService).findLastForVaId(akmPv.getVorabstimmungsId(), UebernahmeRessourceMeldung.class);

        assertEquals(variablesObjectMap.size(), 1);
        assertEquals(variablesObjectMap.get(WitaTaskVariables.RUEM_PV_ANTWORTCODE), RuemPvAntwortCode.OK);
    }

    @Test
    public void testFindNonCompletedVorabstimmungen() throws Exception {
        prepareMocksForFindNonCompletedVAs();
        Set<String> result = testling.findNonCompletedVorabstimmungen(WbciRequestStatus.VA_VERSENDET, 1234L, false);
        Assert.assertEquals(result.size(), 4);
        Assert.assertTrue(result.containsAll(Arrays.asList("DEU.MNET.001", "DEU.MNET.002", "DEU.MNET.003",
                "DEU.MNET.004")));
    }

    @Test
    public void testFindNonCompletedVorabstimmungenWithCondsiderationOfTvAndStorno() throws Exception {
        prepareMocksForFindNonCompletedVAs();
        Set<String> result = testling.findNonCompletedVorabstimmungen(WbciRequestStatus.VA_VERSENDET, 1234L, true);
        Assert.assertEquals(result.size(), 2);
        Assert.assertTrue(result.containsAll(Arrays.asList("DEU.MNET.002", "DEU.MNET.003")));
    }

    private void prepareMocksForFindNonCompletedVAs() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                VorabstimmungIdsByBillingOrderNoCriteria criteria = (VorabstimmungIdsByBillingOrderNoCriteria) invocationOnMock
                        .getArguments()[0];
                Class<? extends WbciRequest> wbciRequestClassType = criteria.getWbciRequestClassType();
                if (wbciRequestClassType.equals(VorabstimmungsAnfrage.class)) {
                    return new HashSet<>(Arrays.asList("DEU.MNET.001", "DEU.MNET.002", "DEU.MNET.003", "DEU.MNET.004"));
                }
                else if (wbciRequestClassType.equals(TerminverschiebungsAnfrage.class)) {
                    return new HashSet<>(Arrays.asList("DEU.MNET.001"));
                }
                else if (wbciRequestClassType.equals(StornoAnfrage.class)) {
                    return new HashSet<>(Arrays.asList("DEU.MNET.004"));
                }
                return null;
            }

        }).when(wbciCommonService).findVorabstimmungIdsByBillingOrderNoOrig(
                any(VorabstimmungIdsByBillingOrderNoCriteria.class));
    }

    @Test
    public void testGetWechselterminForVaId() throws Exception {
        LocalDate wechseltermin = DateCalculationHelper.getDateInWorkingDaysFromNow(1).toLocalDate();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(
                new WbciGeschaeftsfallKueMrnTestBuilder().withWechseltermin(wechseltermin).buildValid(WbciCdmVersion.V1,
                        GeschaeftsfallTyp.VA_KUE_MRN)
        );

        Assert.assertEquals(testling.getWechselterminForVaId(vorabstimmungsId).toLocalDate(), wechseltermin);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Zu der Vorabstimmungs-ID 'DEU.MNET.0001' konnte keine gültige WBCI-Vorabstimmung gefunden werden!")
    public void testGetWechselterminForVaIdException() throws Exception {
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(null);

        testling.getWechselterminForVaId(vorabstimmungsId);
    }

    @Test
    public void testUpdateRufnummernSelectionForVaId() throws Exception {
        Rufnummer rnr = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .build();
        Rufnummer rnr2 = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("20030")
                .build();
        Rufnummernportierung rufnummernportierungMock = Mockito.mock(Rufnummernportierung.class);

        doReturn(rufnummernportierungMock).when(testling).getRuemVaPortierung(vorabstimmungsId);
        when(wbciDecisionService.evaluateRufnummernDecisionData(any(Rufnummernportierung.class), anyListOf(Rufnummer.class), anyBoolean())).
                thenReturn(Arrays.asList(
                        new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                                .withControlObject(rnr)
                                .withFinalResult(DecisionResult.NICHT_OK)
                                .withFinalMeldungsCode(MeldungsCode.ZWA)
                                .build(),
                        new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                                .withControlObject(rnr2)
                                .withFinalResult(DecisionResult.OK)
                                .withFinalMeldungsCode(MeldungsCode.ZWA)
                                .build()
                ))
        ;
        List<RufnummerPortierungSelection> rufnummerPortierungSelections = Arrays.asList(
                new RufnummerPortierungSelection(rnr), new RufnummerPortierungSelection(rnr2));

        Collection<RufnummerPortierungSelection> result = testling.updateRufnummernSelectionForVaId(
                rufnummerPortierungSelections, vorabstimmungsId);
        Assert.assertEquals(result.size(), rufnummerPortierungSelections.size());
        Iterator<RufnummerPortierungSelection> iterator = result.iterator();
        Assert.assertFalse(iterator.next().getSelected());
        Assert.assertTrue(iterator.next().getSelected());
        verify(wbciDecisionService).evaluateRufnummernDecisionData(any(Rufnummernportierung.class),
                anyListOf(Rufnummer.class), anyBoolean());
    }

    @Test
    public void testGetRuemVaPortierung() throws Exception {
        RueckmeldungVorabstimmung ruemVaMock = mock(RueckmeldungVorabstimmung.class);
        when(wbciCommonService.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class)).thenReturn(ruemVaMock);
        when(ruemVaMock.getRufnummernportierung()).thenReturn(new RufnummernportierungEinzeln());

        Assert.assertNotNull(testling.getRuemVaPortierung(vorabstimmungsId));
        verify(wbciCommonService).findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Zu der Vorabstimmungs-ID 'DEU.MNET.0001' konnte keine .*")
    public void testGetRuemVaPortierungException() throws Exception {
        when(wbciCommonService.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class)).thenReturn(null);
        testling.getRuemVaPortierung(vorabstimmungsId);
        verify(wbciCommonService).findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Zu der Vorabstimmungs-ID 'DEU.MNET.0001' konnte keine .*")
    public void testGetRuemVaPortierungExceptionNullPortierung() throws Exception {
        RueckmeldungVorabstimmung ruemVaMock = mock(RueckmeldungVorabstimmung.class);
        when(wbciCommonService.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class)).thenReturn(ruemVaMock);
        when(ruemVaMock.getRufnummernportierung()).thenReturn(null);
        testling.getRuemVaPortierung(vorabstimmungsId);
        verify(wbciCommonService).findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class);
    }

    @Test
    public void testCheckDateForMatchingWithVorabstimmung() throws Exception {
        LocalDate wechseltermin = DateCalculationHelper.getDateInWorkingDaysFromNow(1).toLocalDate();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(
                new WbciGeschaeftsfallKueMrnTestBuilder().withWechseltermin(wechseltermin).buildValid(WbciCdmVersion.V1,
                        GeschaeftsfallTyp.VA_KUE_MRN)
        );

        testling.checkDateForMatchingWithVorabstimmung(Date.from(wechseltermin.atStartOfDay(ZoneId.systemDefault()).toInstant()), vorabstimmungsId);
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "Der angegebene Termin .* 'DEU.MNET.0001'.*")
    public void testCheckDateForMatchingWithVorabstimmungException() throws Exception {
        LocalDate wechseltermin = DateCalculationHelper.getDateInWorkingDaysFromNow(1).toLocalDate();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(
                new WbciGeschaeftsfallKueMrnTestBuilder().withWechseltermin(wechseltermin).buildValid(WbciCdmVersion.V1,
                        GeschaeftsfallTyp.VA_KUE_MRN)
        );

        testling.checkDateForMatchingWithVorabstimmung(new Date(), vorabstimmungsId);
    }

    @Test
    public void testCheckDateIsEqualOrAfterWbciVa() throws Exception {
        LocalDate wechseltermin = DateCalculationHelper.getDateInWorkingDaysFromNow(1).toLocalDate();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(
                new WbciGeschaeftsfallKueMrnTestBuilder().withWechseltermin(wechseltermin).buildValid(WbciCdmVersion.V1,
                        GeschaeftsfallTyp.VA_KUE_MRN)
        );

        testling.checkDateIsEqualOrAfterWbciVa(Date.from(wechseltermin.atStartOfDay(ZoneId.systemDefault()).toInstant()), vorabstimmungsId);
        testling.checkDateIsEqualOrAfterWbciVa(Date.from(wechseltermin.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant()), vorabstimmungsId);
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "Der angegebene Termin .* 'DEU.MNET.0001'.*")
    public void testCheckDateIsEqualOrAfterWbciVaException() throws Exception {
        LocalDate wechseltermin = DateCalculationHelper.getDateInWorkingDaysFromNow(1).toLocalDate();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(
                new WbciGeschaeftsfallKueMrnTestBuilder().withWechseltermin(wechseltermin).buildValid(WbciCdmVersion.V1,
                        GeschaeftsfallTyp.VA_KUE_MRN)
        );

        testling.checkDateIsEqualOrAfterWbciVa(new Date(), vorabstimmungsId);
    }


    @Test
    public void testCheckVorabstimmungValidForWitaVorgang() throws Exception {
        Long taifunNoAuf = 123L;
        String vaIdAuf = "DEU.MNET.AUF001";
        String vaIdAbg = "DEU.MNET.ABG001";
        WbciGeschaeftsfall gfMock = mock(WbciGeschaeftsfall.class);
        when(gfMock.getBillingOrderNoOrig()).thenReturn(taifunNoAuf);
        when(wbciCommonService.findWbciGeschaeftsfall(anyString())).thenReturn(gfMock);

        doReturn(new HashSet<>(Arrays.asList(vaIdAbg))).when(testling).findNonCompletedVorabstimmungen(
                WbciRequestStatus.AKM_TR_EMPFANGEN, taifunNoAuf, true);
        doReturn(new HashSet<>(Arrays.asList(vaIdAuf))).when(testling).findNonCompletedVorabstimmungen(
                WbciRequestStatus.AKM_TR_VERSENDET, taifunNoAuf, true);

        testling.checkVorabstimmungValidForWitaVorgang(de.mnet.wita.message.GeschaeftsfallTyp.KUENDIGUNG_KUNDE, vaIdAbg);
        testling.checkVorabstimmungValidForWitaVorgang(de.mnet.wita.message.GeschaeftsfallTyp.VERBUNDLEISTUNG, vaIdAuf);
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "Die Vorabstimmung 'DEU.MNET.AUF001' ist nicht gültig.*")
    public void testCheckVorabstimmungValidForWitaVorgangActiveStrono() throws Exception {
        Long taifunNoAuf = 123L;
        String vorabstimmungsId = "DEU.MNET.AUF001";
        WbciGeschaeftsfall gfMock = mock(WbciGeschaeftsfall.class);
        when(gfMock.getBillingOrderNoOrig()).thenReturn(taifunNoAuf);
        when(wbciCommonService.findWbciGeschaeftsfall(anyString())).thenReturn(gfMock);

        doReturn(new HashSet<>(Arrays.asList())).when(testling).findNonCompletedVorabstimmungen(
                WbciRequestStatus.AKM_TR_VERSENDET, taifunNoAuf, true);

        testling.checkVorabstimmungValidForWitaVorgang(de.mnet.wita.message.GeschaeftsfallTyp.VERBUNDLEISTUNG, vorabstimmungsId);
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "Für die Vorabstimmungs-ID 'DEU.MNET.NOTVALID' ist kein Taifun-Auftrag zugeordnet!")
    public void testCheckVorabstimmungValidForWitaVorgangNoBillingOrderNo() throws Exception {
        String vorabstimmungsId = "DEU.MNET.NOTVALID";
        WbciGeschaeftsfall wbciGfMock = mock(WbciGeschaeftsfall.class);
        when(wbciCommonService.findWbciGeschaeftsfall(anyString())).thenReturn(wbciGfMock);
        when(wbciGfMock.getBillingOrderNoOrig()).thenReturn(null);
        testling.checkVorabstimmungValidForWitaVorgang(de.mnet.wita.message.GeschaeftsfallTyp.VERBUNDLEISTUNG, vorabstimmungsId);
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "Die Vorabstimmung 'DEU.MNET.NOTVALID' ist nicht gültig.*")
    public void testCheckVorabstimmungValidForWitaVorgangZeroElements() throws Exception {
        Long taifunNoAuf = 123L;
        String vorabstimmungsId = "DEU.MNET.AUF001";
        WbciGeschaeftsfall gfMock = mock(WbciGeschaeftsfall.class);
        when(gfMock.getBillingOrderNoOrig()).thenReturn(taifunNoAuf);
        when(wbciCommonService.findWbciGeschaeftsfall(anyString())).thenReturn(gfMock);

        doReturn(new HashSet<>(Arrays.asList(vorabstimmungsId))).when(testling).findNonCompletedVorabstimmungen(
                WbciRequestStatus.AKM_TR_EMPFANGEN, taifunNoAuf, true);

        testling.checkVorabstimmungValidForWitaVorgang(de.mnet.wita.message.GeschaeftsfallTyp.KUENDIGUNG_KUNDE, "DEU.MNET.NOTVALID");
    }

    @Test
    public void testGetCheckedAkmTr() throws Exception {
        when(wbciCommonService.findLastForVaId(vorabstimmungsId, UebernahmeRessourceMeldung.class)).thenReturn(new UebernahmeRessourceMeldung());
        Assert.assertNotNull(testling.getCheckedAkmTr(vorabstimmungsId));
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testGetCheckedAkmTrNotFound() throws Exception {
        when(wbciCommonService.findLastForVaId(vorabstimmungsId, UebernahmeRessourceMeldung.class)).thenReturn(null);
        Assert.assertNull(testling.getCheckedAkmTr(vorabstimmungsId));
    }

    @Test(dataProvider = "getWitaVersions")
    public void testCheckCountOfWitaVertragsnummern(WitaCdmVersion version) throws Exception {
        when(witaConfigService.getDefaultWitaVersion()).thenReturn(version);

        de.mnet.wita.message.GeschaeftsfallTyp gfTyp = de.mnet.wita.message.GeschaeftsfallTyp.VERBUNDLEISTUNG;
        WbciGeschaeftsfall wbciGfMock = mock(WbciGeschaeftsfall.class);
        doReturn(wbciGfMock).when(testling).getCheckedWbciGeschaeftsfall(vorabstimmungsId);
        doReturn(new TreeSet<>(Arrays.asList("V003", "V001", "V002"))).when(testling).getSortedWitaVertragsNr(gfTyp, vorabstimmungsId);
        doReturn(new TreeSet<>(Arrays.asList(20L, 30L, 10L))).when(testling).getSortedWitaCbIDs(anyLong(), anyLong());

        //Check for != DTAG
        when(wbciGfMock.getAbgebenderEKP()).thenReturn(CarrierCode.VODAFONE);
        Assert.assertEquals(testling.checkAndReturnNextWitaVertragsnummern(gfTyp, vorabstimmungsId, 10L, 99L), "V001");
        Assert.assertEquals(testling.checkAndReturnNextWitaVertragsnummern(gfTyp, vorabstimmungsId, 20L, 99L), "V002");
        Assert.assertEquals(testling.checkAndReturnNextWitaVertragsnummern(gfTyp, vorabstimmungsId, 30L, 99L), "V003");
        //Check for == DTAG
        when(wbciGfMock.getAbgebenderEKP()).thenReturn(CarrierCode.DTAG);
        if (WitaCdmVersion.V1.equals(version)) {
            Assert.assertEquals(testling.checkAndReturnNextWitaVertragsnummern(gfTyp, vorabstimmungsId, 10L, 99L), null);
        }
        else {
            Assert.assertEquals(testling.checkAndReturnNextWitaVertragsnummern(gfTyp, vorabstimmungsId, 10L, 99L), "V001");
            Assert.assertEquals(testling.checkAndReturnNextWitaVertragsnummern(gfTyp, vorabstimmungsId, 20L, 99L), "V002");
            Assert.assertEquals(testling.checkAndReturnNextWitaVertragsnummern(gfTyp, vorabstimmungsId, 30L, 99L), "V003");
        }
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "In der AKM-TR zu der Vorabstimmungs-ID '.*' stimmt die Anzahl der übernommenen Leitungen \\(2\\) nicht mit der Anzahl der ausgelösten WITA-Vorgänge \\(1\\) überein!")
    public void testCheckAndReturnNextWitaVertragsnummernNotMatching() throws Exception {
        de.mnet.wita.message.GeschaeftsfallTyp gfTyp = de.mnet.wita.message.GeschaeftsfallTyp.VERBUNDLEISTUNG;
        doReturn(new TreeSet<>(Arrays.asList("V0001", "V0002"))).when(testling).getSortedWitaVertragsNr(gfTyp, vorabstimmungsId);
        doReturn(new TreeSet<>(Arrays.asList(13L))).when(testling).getSortedWitaCbIDs(anyLong(), anyLong());

        testling.checkAndReturnNextWitaVertragsnummern(gfTyp, vorabstimmungsId, 33L, 33L);
    }

    @Test
    public void testCheckAndReturnNextWitaVertragsnummernDoNothing() throws Exception {
        Assert.assertNull(testling.checkAndReturnNextWitaVertragsnummern(de.mnet.wita.message.GeschaeftsfallTyp.KUENDIGUNG_KUNDE, vorabstimmungsId, 13L, 23L));
        verify(testling, never()).getSortedWitaCbIDs(anyLong(), anyLong());
        verify(testling, never()).getSortedWitaVertragsNr(any(de.mnet.wita.message.GeschaeftsfallTyp.class), anyString());
    }

    @Test
    public void testGetSortedWitaVertragsNr() throws Exception {
        UebernahmeRessourceMeldung akmtr = mock(UebernahmeRessourceMeldung.class);
        doReturn(akmtr).when(testling).getLastAkmTr(vorabstimmungsId);
        when(akmtr.isUebernahme()).thenReturn(true);
        when(akmtr.getLeitungen()).thenReturn(new HashSet<>(Arrays.asList(
                new LeitungBuilder().withVertragsnummer("V003").build(),
                new LeitungBuilder().withVertragsnummer("V001").build(),
                new LeitungBuilder().withVertragsnummer("V002").build()
        )));
        SortedSet<String> result = testling.getSortedWitaVertragsNr(de.mnet.wita.message.GeschaeftsfallTyp.VERBUNDLEISTUNG, vorabstimmungsId);
        Assert.assertEquals(result.size(), 3);
        Assert.assertEquals(result.first(), "V001");
        Assert.assertEquals(result.last(), "V003");
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "Zu der Vorabstimmungs-ID '.*' wurde keine Ressourcenübernahme in der AKM-TR ausgewählt, obwohl dies für den WITA-Geschäftsfall '.*' erwartet wurde!")
    public void testGetSortedWitaVertragsNoRessourcenUebernahme() throws Exception {
        UebernahmeRessourceMeldung akmtr = mock(UebernahmeRessourceMeldung.class);
        doReturn(akmtr).when(testling).getLastAkmTr(vorabstimmungsId);

        when(akmtr.isUebernahme()).thenReturn(false);
        testling.getSortedWitaVertragsNr(de.mnet.wita.message.GeschaeftsfallTyp.VERBUNDLEISTUNG, vorabstimmungsId);
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "Zu der Vorabstimmungs-ID '.*' wurde keine Ressourcenübernahme in der AKM-TR ausgewählt, obwohl dies für den WITA-Geschäftsfall '.*' erwartet wurde!")
    public void testGetSortedWitaVertragsNoRessourcenUebernahmeNull() throws Exception {
        UebernahmeRessourceMeldung akmtr = mock(UebernahmeRessourceMeldung.class);
        doReturn(akmtr).when(testling).getLastAkmTr(vorabstimmungsId);

        when(akmtr.isUebernahme()).thenReturn(null);
        testling.getSortedWitaVertragsNr(de.mnet.wita.message.GeschaeftsfallTyp.VERBUNDLEISTUNG, vorabstimmungsId);
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "Zu der Vorabstimmungs-ID '.*' sind keine übernommenen Leitungen in der AKM-TR vorhanden, obwohl dies erwartet wurde!")
    public void testGetSortedWitaVertragsNoLines() throws Exception {
        UebernahmeRessourceMeldung akmtr = mock(UebernahmeRessourceMeldung.class);
        doReturn(akmtr).when(testling).getLastAkmTr(vorabstimmungsId);
        when(akmtr.isUebernahme()).thenReturn(true);
        when(akmtr.getLeitungen()).thenReturn(new HashSet<Leitung>());
        testling.getSortedWitaVertragsNr(de.mnet.wita.message.GeschaeftsfallTyp.VERBUNDLEISTUNG, vorabstimmungsId);
    }

    @Test
    public void testGetSortedWitaCbIds() throws Exception {
        Long klammerId = 99L;
        when(witaTalOrderService.findWitaCBVorgaengIDs4Klammer(klammerId)).thenReturn(new TreeSet<>(Arrays.asList(3L, 1L, 2L)));
        SortedSet<Long> result = testling.getSortedWitaCbIDs(10L, klammerId);
        Assert.assertEquals(result.size(), 3);
        Assert.assertEquals(result.first().longValue(), 1L);
        Assert.assertEquals(result.last().longValue(), 3L);

        result = testling.getSortedWitaCbIDs(10L, null);
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.first().longValue(), 10L);
    }

    @Test
    public void testCheckRufnummernForMatchingWithVorabstimmung() throws Exception {
        Rufnummer rnr = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .build();
        Rufnummer rnr2 = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("20030")
                .build();
        Rufnummernportierung rufnummernportierungMock = Mockito.mock(Rufnummernportierung.class);

        doReturn(rufnummernportierungMock).when(testling).getRuemVaPortierung(vorabstimmungsId);
        when(
                wbciDecisionService.evaluateRufnummernDecisionData(any(Rufnummernportierung.class),
                        anyListOf(Rufnummer.class), anyBoolean())
        ).
                thenReturn(Arrays.asList(
                        new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                                .withControlObject(rnr)
                                .withFinalResult(DecisionResult.OK)
                                .withFinalMeldungsCode(MeldungsCode.ZWA)
                                .build(),
                        new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                                .withControlObject(rnr2)
                                .withFinalResult(DecisionResult.OK)
                                .withFinalMeldungsCode(MeldungsCode.ZWA)
                                .build()
                ));
        List<Rufnummer> rufnummerList = Arrays.asList(rnr, rnr2);
        testling.checkRufnummernForMatchingWithVorabstimmung(rufnummerList, vorabstimmungsId);
        verify(wbciDecisionService).evaluateRufnummernDecisionData(rufnummernportierungMock, rufnummerList, false);
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "Die angegebene Taifun-Rufnummer '89.20030' stimmt nicht mit der in der WBCI-Vorabstimmung angegebenen Rufnummer überein!")
    public void testCheckRufnummernForMatchingWithVorabstimmungNotOK() throws Exception {
        Rufnummer rnr = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .build();
        Rufnummer rnr2 = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("20030")
                .build();
        Rufnummernportierung rufnummernportierungMock = Mockito.mock(Rufnummernportierung.class);

        doReturn(rufnummernportierungMock).when(testling).getRuemVaPortierung(vorabstimmungsId);
        when(
                wbciDecisionService.evaluateRufnummernDecisionData(any(Rufnummernportierung.class),
                        anyListOf(Rufnummer.class), anyBoolean())
        ).
                thenReturn(Arrays.asList(
                        new DecisionVOBuilder(DecisionAttribute.RUFNUMMER).withControlObject(rnr)
                                .withControlValue(rnr.getRufnummer())
                                .withFinalResult(DecisionResult.OK)
                                .withFinalMeldungsCode(MeldungsCode.ZWA)
                                .build(),
                        new DecisionVOBuilder(DecisionAttribute.RUFNUMMER).withControlObject(rnr2)
                                .withControlValue(rnr2.getRufnummer())
                                .withFinalResult(DecisionResult.NICHT_OK)
                                .withFinalMeldungsCode(MeldungsCode.ZWA)
                                .build()
                ));
        List<Rufnummer> rufnummerList = Arrays.asList(rnr, rnr2);
        testling.checkRufnummernForMatchingWithVorabstimmung(rufnummerList, vorabstimmungsId);
        verify(wbciDecisionService).evaluateRufnummernDecisionData(rufnummernportierungMock, rufnummerList, false);
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "Die angegebene Taifun-Rufnummer '89.20030' stimmt nicht mit der in der WBCI-Vorabstimmung angegebenen Rufnummer überein!")
    public void testCheckRufnummernForMatchingWithVorabstimmungNoZWA() throws Exception {
        Rufnummer rnr = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .build();
        Rufnummer rnr2 = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("20030")
                .build();
        Rufnummernportierung rufnummernportierungMock = Mockito.mock(Rufnummernportierung.class);

        doReturn(rufnummernportierungMock).when(testling).getRuemVaPortierung(vorabstimmungsId);
        when(
                wbciDecisionService.evaluateRufnummernDecisionData(any(Rufnummernportierung.class),
                        anyListOf(Rufnummer.class), anyBoolean())
        ).
                thenReturn(Arrays.asList(
                        new DecisionVOBuilder(DecisionAttribute.RUFNUMMER).withControlObject(rnr)
                                .withControlValue(rnr.getRufnummer())
                                .withFinalResult(DecisionResult.OK)
                                .withFinalMeldungsCode(MeldungsCode.ZWA)
                                .build(),
                        new DecisionVOBuilder(DecisionAttribute.RUFNUMMER).withControlObject(rnr2)
                                .withControlValue(rnr2.getRufnummer())
                                .withFinalResult(DecisionResult.OK)
                                .withFinalMeldungsCode(MeldungsCode.RNG)
                                .build()
                ));
        List<Rufnummer> rufnummerList = Arrays.asList(rnr, rnr2);
        testling.checkRufnummernForMatchingWithVorabstimmung(rufnummerList, vorabstimmungsId);
        verify(wbciDecisionService).evaluateRufnummernDecisionData(rufnummernportierungMock, rufnummerList, false);
    }

    @Test
    public void testGetRuemVa() throws Exception {
        when(wbciCommonService.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class)).thenReturn(new RueckmeldungVorabstimmung());
        Assert.assertNotNull(testling.getRuemVa(vorabstimmungsId));
        verify(wbciCommonService).findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class);
    }

    @Test
    public void testLastAkmTr() throws Exception {
        when(wbciCommonService.findLastForVaId(vorabstimmungsId, UebernahmeRessourceMeldung.class)).thenReturn(new UebernahmeRessourceMeldung());
        Assert.assertNotNull(testling.getLastAkmTr(vorabstimmungsId));
        verify(wbciCommonService).findLastForVaId(vorabstimmungsId, UebernahmeRessourceMeldung.class);
    }
}
