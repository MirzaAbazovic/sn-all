/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.13
 */
package de.mnet.wbci.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.AdresseBuilder;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.DecisionAttribute;
import de.mnet.wbci.model.DecisionResult;
import de.mnet.wbci.model.DecisionVO;
import de.mnet.wbci.model.DecisionVOwithKuendigungsCheckVO;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KuendigungsCheckVO;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.builder.DecisionVOBuilder;
import de.mnet.wbci.model.builder.FirmaTestBuilder;
import de.mnet.wbci.model.builder.PersonTestBuilder;
import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;
import de.mnet.wbci.model.builder.RufnummerOnkzTestBuilder;
import de.mnet.wbci.model.builder.RufnummernblockBuilder;
import de.mnet.wbci.model.builder.RufnummernblockTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciKuendigungsService;

@Test(groups = BaseTest.UNIT)
public class WbciDecisionServiceImplTest {

    @Spy
    @InjectMocks
    private WbciDecisionServiceImpl testling;
    @Mock
    private WbciCommonService wbciCommonService;
    @Mock
    private WbciKuendigungsService wbciKuendigungsService;

    @BeforeMethod
    public void setUp() {
        testling = new WbciDecisionServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testEvaluateDecisionDataAllOkAnschluss() throws FindException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withWeitereAnschlussinhaber(new ArrayList<PersonOderFirma>())
                .withRufnummernportierung(
                        new RufnummernportierungAnlageTestBuilder()
                                .withRufnummernbloecke(
                                        Collections.singletonList(
                                                new RufnummernblockTestBuilder().buildValid(WbciCdmVersion.V1,
                                                        GeschaeftsfallTyp.VA_KUE_MRN)
                                        )
                                )
                                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)
                )
                .withBillingOrderNoOrig(9000L)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        VorabstimmungsAnfrage vaKueMrn = new VorabstimmungsAnfrageTestBuilder<>().withWbciGeschaeftsfall(
                wbciGeschaeftsfall)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Adresse kundenAdresse = createKundenAdresse(wbciGeschaeftsfall.getEndkunde(), wbciGeschaeftsfall.getStandort());

        RufnummernportierungAnlage anlage = (RufnummernportierungAnlage) wbciGeschaeftsfall.getRufnummernportierung();
        Rufnummer billingDn = new RufnummerBuilder()
                .withOnKz(anlage.getOnkzWithLeadingZero())
                .withDnBase(anlage.getDurchwahlnummer())
                .withDirectDial(anlage.getAbfragestelle())
                .withRangeFrom(anlage.getRufnummernbloecke().get(0).getRnrBlockVon())
                .withRangeTo(anlage.getRufnummernbloecke().get(0).getRnrBlockBis())
                .build();
        List<Rufnummer> billingDns = Arrays.asList(billingDn);

        KuendigungsCheckVO kuendigungsCheckVO = new KuendigungsCheckVO();
        kuendigungsCheckVO.setCalculatedEarliestCancelDate(wbciGeschaeftsfall.getKundenwunschtermin().atStartOfDay());

        when(
                wbciCommonService.findWbciRequestByType(wbciGeschaeftsfall.getVorabstimmungsId(),
                        VorabstimmungsAnfrage.class)
        ).thenReturn(Arrays.asList(vaKueMrn));
        when(wbciKuendigungsService.doKuendigungsCheck(anyLong(), any(LocalDateTime.class))).thenReturn(kuendigungsCheckVO);
        when(wbciCommonService.getAnschlussAdresse(wbciGeschaeftsfall.getBillingOrderNoOrig())).thenReturn(
                kundenAdresse);
        when(wbciCommonService.getRNs(wbciGeschaeftsfall.getBillingOrderNoOrig())).thenReturn(billingDns);
        when(wbciCommonService.findWbciGeschaeftsfall(wbciGeschaeftsfall.getStrAenVorabstimmungsId())).thenReturn(null);

        List<DecisionVO> decisionData = testling.evaluateDecisionData(wbciGeschaeftsfall.getVorabstimmungsId());

        Assert.assertEquals(decisionData.size(), 10);
        assertDecisionData(decisionData, DecisionAttribute.VORNAME, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.NACHNAME, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.PLZ, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.ORT, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.STRASSENNAME, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.HAUSNUMMER, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.HAUSNUMMERZUSATZ, DecisionResult.INFO);
        assertTrue(decisionData.contains(
                new DecisionVOBuilder(DecisionAttribute.HAUSNUMMERZUSATZ)
                        .withPropertyValue("a")
                        .withSuggestedResult(DecisionResult.INFO)
                        .withFinalResult(DecisionResult.INFO)
                        .build()
        ));
        assertDecisionData(decisionData, DecisionAttribute.KUNDENWUNSCHTERMIN, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.AKT_VERTRAGSENDE, DecisionResult.INFO);
        assertDecisionData(decisionData, DecisionAttribute.RUFNUMMERN_BLOCK, DecisionResult.OK);
        verify(wbciCommonService, never()).findWbciGeschaeftsfall(wbciGeschaeftsfall.getStrAenVorabstimmungsId());
    }

    @Test
    public void testEvaluateDecisionDataAllOkEinzel() throws FindException {
        String strAenVorabstimmungsId = "DEU.MNET.VH00000001";
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withWeitereAnschlussinhaber(new ArrayList<PersonOderFirma>())
                .withRufnummernportierung(
                        new RufnummernportierungEinzelnTestBuilder()
                                .addRufnummer(
                                        new RufnummerOnkzTestBuilder().buildValid(WbciCdmVersion.V1,
                                                GeschaeftsfallTyp.VA_KUE_MRN)
                                )
                                .addRufnummer(new RufnummerOnkzTestBuilder()
                                        .withRufnummer("987654321")
                                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                                .withAlleRufnummernPortieren(true)
                                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)
                )
                .withBillingOrderNoOrig(9000L)
                .withStrAenVorabstimmungsId(strAenVorabstimmungsId)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        VorabstimmungsAnfrage vaKueMrn = new VorabstimmungsAnfrageTestBuilder<>()
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Adresse kundenAdresse = createKundenAdresse(wbciGeschaeftsfall.getEndkunde(), wbciGeschaeftsfall.getStandort());

        RufnummernportierungEinzeln einzel = (RufnummernportierungEinzeln) wbciGeschaeftsfall.getRufnummernportierung();
        List<Rufnummer> billingDns = new ArrayList<>();
        for (RufnummerOnkz wbciDn : einzel.getRufnummernOnkz()) {
            billingDns.add(new RufnummerBuilder()
                    .withOnKz(wbciDn.getOnkzWithLeadingZero())
                    .withDnBase(wbciDn.getRufnummer())
                    .build());
        }

        when(
                wbciCommonService.findWbciRequestByType(wbciGeschaeftsfall.getVorabstimmungsId(),
                        VorabstimmungsAnfrage.class)
        ).thenReturn(Arrays.asList(vaKueMrn));
        when(wbciCommonService.getAnschlussAdresse(wbciGeschaeftsfall.getBillingOrderNoOrig())).thenReturn(
                kundenAdresse);
        when(wbciCommonService.getRNs(wbciGeschaeftsfall.getBillingOrderNoOrig())).thenReturn(billingDns);
        WbciGeschaeftsfall strAenGf = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withVorabstimmungsId(strAenVorabstimmungsId)
                .withWechseltermin(DateCalculationHelper.getDateInWorkingDaysFromNow(11).toLocalDate())
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.findWbciGeschaeftsfall(wbciGeschaeftsfall.getStrAenVorabstimmungsId())).thenReturn(
                strAenGf);

        when(wbciKuendigungsService.doKuendigungsCheck(anyLong(), any(LocalDateTime.class))).thenReturn(
                new KuendigungsCheckVO());

        List<DecisionVO> decisionData = testling.evaluateDecisionData(wbciGeschaeftsfall.getVorabstimmungsId());

        Assert.assertEquals(decisionData.size(), 13);
        assertDecisionData(decisionData, DecisionAttribute.VORNAME, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.NACHNAME, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.PLZ, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.ORT, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.STRASSENNAME, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.HAUSNUMMER, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.HAUSNUMMERZUSATZ, DecisionResult.INFO);
        assertDecisionData(decisionData, DecisionAttribute.KUNDENWUNSCHTERMIN, DecisionResult.ABWEICHEND);
        assertDecisionData(decisionData, DecisionAttribute.AKT_VERTRAGSENDE, DecisionResult.INFO);
        assertDecisionData(decisionData, DecisionAttribute.STR_AEN_WECHSELTERMIN, DecisionResult.INFO);
        assertDecisionData(decisionData, DecisionAttribute.RUFNUMMER, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.ALLE_RUFNUMMERN_PORTIEREN, DecisionResult.INFO);
        verify(wbciCommonService).findWbciGeschaeftsfall(wbciGeschaeftsfall.getStrAenVorabstimmungsId());
    }

    @Test
    public void testEvaluateDecisionDataWeitereAnschlussInhaber() throws FindException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .addAnschlussinhaber(new PersonTestBuilder()
                        .withVorname("Marion")
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                .withRufnummernportierung(
                        new RufnummernportierungEinzelnTestBuilder()
                                .addRufnummer(
                                        new RufnummerOnkzTestBuilder().buildValid(WbciCdmVersion.V1,
                                                GeschaeftsfallTyp.VA_KUE_MRN)
                                )
                                .withAlleRufnummernPortieren(false)
                                .withPortierungszeitfenster(Portierungszeitfenster.ZF3)
                                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)
                )
                .withBillingOrderNoOrig(9000L)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        VorabstimmungsAnfrage vaKueMrn = new VorabstimmungsAnfrageTestBuilder<>()
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Adresse kundenAdresse = createKundenAdresse(wbciGeschaeftsfall.getEndkunde(), wbciGeschaeftsfall.getStandort());

        when(
                wbciCommonService.findWbciRequestByType(wbciGeschaeftsfall.getVorabstimmungsId(),
                        VorabstimmungsAnfrage.class)
        ).thenReturn(Arrays.asList(vaKueMrn));
        when(wbciCommonService.getAnschlussAdresse(wbciGeschaeftsfall.getBillingOrderNoOrig())).thenReturn(
                kundenAdresse);

        when(wbciKuendigungsService.doKuendigungsCheck(anyLong(), any(LocalDateTime.class))).thenReturn(
                new KuendigungsCheckVO());
        when(wbciCommonService.getRNs(anyLong())).thenReturn(Collections.<Rufnummer>emptyList());

        List<DecisionVO> decisionData = testling.evaluateDecisionData(wbciGeschaeftsfall.getVorabstimmungsId());

        Assert.assertEquals(decisionData.size(), 13);
        assertDecisionData(decisionData, DecisionAttribute.VORNAME, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.NACHNAME, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.PLZ, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.ORT, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.STRASSENNAME, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.HAUSNUMMER, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.HAUSNUMMERZUSATZ, DecisionResult.INFO);
        assertDecisionData(decisionData, DecisionAttribute.KUNDENWUNSCHTERMIN, DecisionResult.ABWEICHEND);
        assertDecisionData(decisionData, DecisionAttribute.AKT_VERTRAGSENDE, DecisionResult.INFO);
        assertDecisionData(decisionData, DecisionAttribute.PORTIERUNGSZEITFENSTER, DecisionResult.NICHT_OK);
        assertDecisionData(decisionData, DecisionAttribute.RUFNUMMER, DecisionResult.NICHT_OK);
        assertDecisionData(decisionData, DecisionAttribute.VORNAME_WAI, DecisionResult.MANUELL);
        assertDecisionData(decisionData, DecisionAttribute.NACHNAME_WAI, DecisionResult.MANUELL);
        assertTrue(decisionData.contains(
                new DecisionVOBuilder(DecisionAttribute.PORTIERUNGSZEITFENSTER)
                        .withPropertyValue(Portierungszeitfenster.ZF3.name())
                        .withSuggestedMeldungsCode(MeldungsCode.SONST)
                        .withFinalMeldungsCode(MeldungsCode.SONST)
                        .withSuggestedResult(DecisionResult.NICHT_OK)
                        .withFinalResult(DecisionResult.NICHT_OK)
                        .build()
        ));
    }

    @Test
    public void testEvaluateDecisionDataFirmaAsEndkunde() throws FindException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withEndkunde(new FirmaTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                .withWeitereAnschlussinhaber(new ArrayList<PersonOderFirma>())
                .withRufnummernportierung(
                        new RufnummernportierungEinzelnTestBuilder()
                                .addRufnummer(
                                        new RufnummerOnkzTestBuilder().buildValid(WbciCdmVersion.V1,
                                                GeschaeftsfallTyp.VA_KUE_MRN)
                                )
                                .withAlleRufnummernPortieren(false)
                                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)
                )
                .withBillingOrderNoOrig(9000L)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        VorabstimmungsAnfrage vaKueMrn = new VorabstimmungsAnfrageTestBuilder<>()
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Adresse kundenAdresse = createKundenAdresse(wbciGeschaeftsfall.getEndkunde(), wbciGeschaeftsfall.getStandort());

        when(
                wbciCommonService.findWbciRequestByType(wbciGeschaeftsfall.getVorabstimmungsId(),
                        VorabstimmungsAnfrage.class)
        ).thenReturn(Arrays.asList(vaKueMrn));
        when(wbciCommonService.getAnschlussAdresse(wbciGeschaeftsfall.getBillingOrderNoOrig())).thenReturn(
                kundenAdresse);

        when(wbciKuendigungsService.doKuendigungsCheck(anyLong(), any(LocalDateTime.class))).thenReturn(
                new KuendigungsCheckVO());

        when(wbciCommonService.getRNs(anyLong())).thenReturn(Collections.<Rufnummer>emptyList());

        List<DecisionVO> decisionData = testling.evaluateDecisionData(wbciGeschaeftsfall.getVorabstimmungsId());

        Assert.assertEquals(decisionData.size(), 10);
        assertDecisionData(decisionData, DecisionAttribute.FIRMEN_NAME, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.FIRMENZUSATZ, DecisionResult.MANUELL);
        assertDecisionData(decisionData, DecisionAttribute.PLZ, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.ORT, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.STRASSENNAME, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.HAUSNUMMER, DecisionResult.OK);
        assertDecisionData(decisionData, DecisionAttribute.HAUSNUMMERZUSATZ, DecisionResult.INFO);
        assertDecisionData(decisionData, DecisionAttribute.KUNDENWUNSCHTERMIN, DecisionResult.ABWEICHEND);
        assertDecisionData(decisionData, DecisionAttribute.AKT_VERTRAGSENDE, DecisionResult.INFO);
        assertDecisionData(decisionData, DecisionAttribute.RUFNUMMER, DecisionResult.NICHT_OK);
    }

    @Test
    public void createDecisionDataForDnAnlage() {
        final Rufnummer rufnummer_1 = new RufnummerBuilder()
                .withOnKz("089")
                .withDnBase("123")
                .withDirectDial("0")
                .withRangeFrom("00")
                .withRangeTo("99")
                .build();
        final Rufnummer rufnummer_2 = new RufnummerBuilder()
                .withOnKz("089")
                .withDnBase("124")
                .withDirectDial("0")
                .withRangeFrom("00")
                .withRangeTo("99")
                .build();
        final List<Rufnummer> billingDns = Arrays.asList(rufnummer_1, rufnummer_2);
        final RufnummernportierungAnlage rufnummernportierungAnlage = new RufnummernportierungAnlageBuilder()
                .withOnkz("089")
                .withAbfragestelle("0")
                .withDurchwahlnummer("123")
                .addRufnummernblock(new RufnummernblockBuilder()
                        .withRnrBlockVon("00")
                        .withRnrBlockBis("99")
                        .build())
                .build();

        final List<DecisionVO> decisionVOs =
                testling.createDecisionDataForDn(rufnummernportierungAnlage, billingDns, true);
        Assert.assertNotNull(decisionVOs);
        Assert.assertEquals(decisionVOs.size(), 2);
        assertTrue(decisionVOs.contains(
                new DecisionVOBuilder(DecisionAttribute.RUFNUMMERN_BLOCK)
                        .withControlObject(rufnummer_1)
                        .withControlValue("089/123 (0) - 00 bis 99")
                        .withPropertyValue("089/123 (0) - 00 bis 99")
                        .build()
        ));

        assertTrue(decisionVOs.contains(new DecisionVOBuilder(DecisionAttribute.RUFNUMMERN_BLOCK)
                .withControlObject(rufnummer_2)
                .withControlValue("089/124 (0) - 00 bis 99")
                .withSuggestedResult(DecisionResult.INFO)
                .withFinalResult(DecisionResult.INFO)
                .build()));
    }

    @Test
    public void createDecisionDataForDnEinzeln() {
        for (Boolean negativeAlleRufnr : Arrays.asList(false, null)) {
            final Rufnummer rufnummer_1 = new RufnummerBuilder()
                    .withOnKz("089")
                    .withDnBase("123")
                    .build();
            final Rufnummer rufnummer_2 = new RufnummerBuilder()
                    .withOnKz("089")
                    .withDnBase("124")
                    .build();
            final List<Rufnummer> billingDns = Arrays.asList(rufnummer_1, rufnummer_2);
            final RufnummernportierungEinzeln rufnummernportierungEinzeln = new RufnummernportierungEinzelnBuilder()
                    .withAlleRufnummernPortieren(negativeAlleRufnr)
                    .addRufnummer(new RufnummerOnkzBuilder()
                            .withOnkz("089")
                            .withRufnummer("123")
                            .build())
                    .addRufnummer(new RufnummerOnkzBuilder()
                            .withOnkz("089")
                            .withRufnummer("3333")
                            .build())
                    .build();

            final List<DecisionVO> decisionVOs =
                    testling.createDecisionDataForDn(rufnummernportierungEinzeln, billingDns, true);
            Assert.assertNotNull(decisionVOs);
            Assert.assertEquals(decisionVOs.size(), 3);
            assertTrue(decisionVOs.contains(
                    new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                            .withControlObject(rufnummer_1)
                            .withControlValue("089/123")
                            .withPropertyValue("089/123")
                            .build()
            ));
            assertTrue(decisionVOs.contains(
                    new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                            .withControlObject(rufnummer_2)
                            .withControlValue("089/124")
                            .withSuggestedResult(DecisionResult.INFO)
                            .withFinalResult(DecisionResult.INFO)
                            .build()
            ));
            assertTrue(decisionVOs.contains(
                    new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                            .withPropertyValue("089/3333")
                            .withSuggestedMeldungsCode(MeldungsCode.RNG)
                            .withFinalMeldungsCode(MeldungsCode.RNG)
                            .withSuggestedResult(DecisionResult.NICHT_OK)
                            .withFinalResult(DecisionResult.NICHT_OK)
                            .build()
            ));
            Assert.assertFalse(decisionVOs.contains(
                    new DecisionVOBuilder(DecisionAttribute.ALLE_RUFNUMMERN_PORTIEREN)
                            .withPropertyValue("ja")
                            .withSuggestedResult(DecisionResult.INFO)
                            .withFinalResult(DecisionResult.INFO)
                            .build()
            ));
        }
    }

    @Test
    public void createDecisionDataForDnEinzelnAlleRufnummer() {
        final Rufnummer rufnummer_1 = new RufnummerBuilder()
                .withOnKz("089")
                .withDnBase("123")
                .build();
        final Rufnummer rufnummer_2 = new RufnummerBuilder()
                .withOnKz("089")
                .withDnBase("124")
                .build();
        final List<Rufnummer> billingDns = Arrays.asList(rufnummer_1, rufnummer_2);
        final RufnummernportierungEinzeln rufnummernportierungEinzeln = new RufnummernportierungEinzelnBuilder()
                .withAlleRufnummernPortieren(true)
                .addRufnummer(new RufnummerOnkzBuilder()
                        .withOnkz("089")
                        .withRufnummer("123")
                        .build())
                .addRufnummer(new RufnummerOnkzBuilder()
                        .withOnkz("089")
                        .withRufnummer("3333")
                        .build())
                .build();

        final List<DecisionVO> decisionVOs =
                testling.createDecisionDataForDn(rufnummernportierungEinzeln, billingDns, true);
        Assert.assertNotNull(decisionVOs);
        Assert.assertEquals(decisionVOs.size(), 4);
        assertTrue(decisionVOs.contains(
                new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                        .withControlObject(rufnummer_1)
                        .withControlValue("089/123")
                        .withPropertyValue("089/123")
                        .build()
        ));
        assertTrue(decisionVOs.contains(
                new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                        .withControlObject(rufnummer_2)
                        .withControlValue("089/124")
                        .build()
        ));
        assertTrue(decisionVOs.contains(
                new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                        .withPropertyValue("089/3333")
                        .build()
        ));
        assertTrue(decisionVOs.contains(
                new DecisionVOBuilder(DecisionAttribute.ALLE_RUFNUMMERN_PORTIEREN)
                        .withPropertyValue("ja")
                        .withSuggestedResult(DecisionResult.INFO)
                        .withFinalResult(DecisionResult.INFO)
                        .build()
        ));
    }

    @Test
    public void createDecisionDataForDnEinzelnMissingTaifunRufnummerNotOk() {
        for (Boolean negativeAlleRufnr : Arrays.asList(false, null)) {
            final Rufnummer rufnummer_1 = new RufnummerBuilder()
                    .withOnKz("089")
                    .withDnBase("123")
                    .build();
            final Rufnummer rufnummer_2 = new RufnummerBuilder()
                    .withOnKz("089")
                    .withDnBase("124")
                    .build();
            final List<Rufnummer> billingDns = Arrays.asList(rufnummer_1, rufnummer_2);
            final RufnummernportierungEinzeln rufnummernportierungEinzeln = new RufnummernportierungEinzelnBuilder()
                    .withAlleRufnummernPortieren(negativeAlleRufnr)
                    .addRufnummer(new RufnummerOnkzBuilder()
                            .withOnkz("089")
                            .withRufnummer("123")
                            .build())
                    .addRufnummer(new RufnummerOnkzBuilder()
                            .withOnkz("089")
                            .withRufnummer("3333")
                            .build())
                    .build();

            final List<DecisionVO> decisionVOs =
                    testling.createDecisionDataForDn(rufnummernportierungEinzeln, billingDns, false);
            Assert.assertNotNull(decisionVOs);
            Assert.assertEquals(decisionVOs.size(), 3);
            assertTrue(decisionVOs.contains(
                    new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                            .withControlObject(rufnummer_1)
                            .withControlValue("089/123")
                            .withPropertyValue("089/123")
                            .build()
            ));
            assertTrue(decisionVOs.contains(
                    new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                            .withControlObject(rufnummer_2)
                            .withControlValue("089/124")
                            .withSuggestedResult(DecisionResult.NICHT_OK)
                            .withFinalResult(DecisionResult.NICHT_OK)
                            .build()
            ));
            assertTrue(decisionVOs.contains(
                    new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                            .withPropertyValue("089/3333")
                            .withSuggestedMeldungsCode(MeldungsCode.RNG)
                            .withFinalMeldungsCode(MeldungsCode.RNG)
                            .withSuggestedResult(DecisionResult.NICHT_OK)
                            .withFinalResult(DecisionResult.NICHT_OK)
                            .build()
            ));
            Assert.assertFalse(decisionVOs.contains(
                    new DecisionVOBuilder(DecisionAttribute.ALLE_RUFNUMMERN_PORTIEREN)
                            .withPropertyValue("ja")
                            .withSuggestedResult(DecisionResult.INFO)
                            .withFinalResult(DecisionResult.INFO)
                            .build()
            ));
        }
    }

    @Test
    public void testEvaluateRufnummernDecisionData() throws Exception {
        Rufnummernportierung rnp = new RufnummernportierungEinzeln();
        List<Rufnummer> taifunRufnummern = Arrays.asList(new Rufnummer());
        doReturn(Collections.EMPTY_LIST).when(testling).createDecisionDataForDn(rnp, taifunRufnummern, false);

        testling.evaluateRufnummernDecisionData(rnp, taifunRufnummern, false);
        verify(testling).createDecisionDataForDn(rnp, taifunRufnummern, false);
    }

    @Test
    public void testEvaluateRufnummernDecisionDataNull() throws Exception {
        List<Rufnummer> taifunRufnummern = Arrays.asList(new Rufnummer());
        doReturn(Collections.EMPTY_LIST).when(testling).createDecisionDataForDn(any(Rufnummernportierung.class),
                anyListOf(Rufnummer.class), eq(false));

        testling.evaluateRufnummernDecisionData(null, taifunRufnummern, false);
        verify(testling, never()).createDecisionDataForDn(any(Rufnummernportierung.class), anyListOf(Rufnummer.class),
                eq(false));
    }

    @Test
    public void testGetRNsFilteredByPortierungTypeAsInfo() throws FindException {
        final Rufnummer rufnummer_1 = new RufnummerBuilder()
                .withOnKz("089")
                .withDnBase("123")
                .build();
        final Rufnummer rufnummer_2 = new RufnummerBuilder()
                .withOnKz("089")
                .withDnBase("124")
                .build();
        final Rufnummer rufnummer_3 = new RufnummerBuilder()
                .withOnKz("089")
                .withDnBase("123")
                .withDirectDial("0")
                .withRangeFrom("00")
                .withRangeTo("99")
                .build();
        final Rufnummer rufnummer_4 = new RufnummerBuilder()
                .withOnKz("089")
                .withDnBase("124")
                .withDirectDial("0")
                .withRangeFrom("00")
                .withRangeTo("99")
                .build();

        final List<Rufnummer> billingDns = Arrays.asList(rufnummer_1, rufnummer_2, rufnummer_3, rufnummer_4);
        final RufnummernportierungEinzeln rufnummernportierungEinzeln = new RufnummernportierungEinzelnBuilder()
                .withAlleRufnummernPortieren(true)
                .addRufnummer(new RufnummerOnkzBuilder()
                        .withOnkz("089")
                        .withRufnummer("123")
                        .build())
                .build();
        final RufnummernportierungAnlage rufnummernportierungAnlage = new RufnummernportierungAnlageBuilder()
                .withOnkz("089")
                .withDurchwahlnummer("123")
                .withAbfragestelle("0")
                .addRufnummernblock(new RufnummernblockBuilder()
                        .withRnrBlockVon("00")
                        .withRnrBlockBis("99")
                        .build())
                .build();

        // Test für Einzelrufnummern => Anlagenrufnummern sind INFO
        final List<DecisionVO> decisionVOs =
                testling.createDecisionDataForDn(rufnummernportierungEinzeln, billingDns, true);
        Assert.assertNotNull(decisionVOs);
        Assert.assertEquals(decisionVOs.size(), 5);
        assertTrue(decisionVOs.contains(
                new DecisionVOBuilder(DecisionAttribute.ALLE_RUFNUMMERN_PORTIEREN)
                        .withPropertyValue("ja")
                        .withSuggestedResult(DecisionResult.INFO)
                        .withFinalResult(DecisionResult.INFO)
                        .build()
        ));
        assertTrue(decisionVOs.contains(
                new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                        .withControlObject(rufnummer_1)
                        .withControlValue("089/123")
                        .withPropertyValue("089/123")
                        .build()
        ));
        assertTrue(decisionVOs.contains(
                new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                        .withControlObject(rufnummer_2)
                        .withControlValue("089/124")
                        .build()
        ));
        assertTrue(decisionVOs.contains(
                new DecisionVOBuilder(DecisionAttribute.RUFNUMMERN_BLOCK)
                        .withControlObject(rufnummer_3)
                        .withControlValue("089/123 (0) - 00 bis 99")
                        .withPropertyValue(null)
                        .withSuggestedResult(DecisionResult.INFO)
                        .withFinalResult(DecisionResult.INFO)
                        .build()
        ));
        assertTrue(decisionVOs.contains(
                new DecisionVOBuilder(DecisionAttribute.RUFNUMMERN_BLOCK)
                        .withControlObject(rufnummer_4)
                        .withControlValue("089/124 (0) - 00 bis 99")
                        .withPropertyValue(null)
                        .withSuggestedResult(DecisionResult.INFO)
                        .withFinalResult(DecisionResult.INFO)
                        .build()
        ));

        // Test für Anlagenrufnummern => Einzelrufnummern sind INFO
        final List<DecisionVO> decisionVOsAnlage =
                testling.createDecisionDataForDn(rufnummernportierungAnlage, billingDns, true);
        Assert.assertNotNull(decisionVOsAnlage);
        Assert.assertEquals(decisionVOsAnlage.size(), 4);
        assertTrue(decisionVOsAnlage.contains(
                new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                        .withControlObject(rufnummer_1)
                        .withControlValue("089/123")
                        .withPropertyValue(null)
                        .withSuggestedResult(DecisionResult.INFO)
                        .withFinalResult(DecisionResult.INFO)
                        .build()
        ));
        assertTrue(decisionVOsAnlage.contains(
                new DecisionVOBuilder(DecisionAttribute.RUFNUMMER)
                        .withControlObject(rufnummer_2)
                        .withControlValue("089/124")
                        .withPropertyValue(null)
                        .withSuggestedResult(DecisionResult.INFO)
                        .withFinalResult(DecisionResult.INFO)
                        .build()
        ));
        assertTrue(decisionVOsAnlage.contains(
                new DecisionVOBuilder(DecisionAttribute.RUFNUMMERN_BLOCK)
                        .withControlObject(rufnummer_3)
                        .withControlValue("089/123 (0) - 00 bis 99")
                        .withPropertyValue("089/123 (0) - 00 bis 99")
                        .withSuggestedResult(DecisionResult.OK)
                        .withFinalResult(DecisionResult.OK)
                        .build()
        ));
        assertTrue(decisionVOsAnlage.contains(
                new DecisionVOBuilder(DecisionAttribute.RUFNUMMERN_BLOCK)
                        .withControlObject(rufnummer_4)
                        .withControlValue("089/124 (0) - 00 bis 99")
                        .withPropertyValue(null)
                        .withSuggestedResult(DecisionResult.INFO)
                        .withFinalResult(DecisionResult.INFO)
                        .build()
        ));
    }

    @Test
    public void testCreateChangeCarrierDateDecisionDataForKueMrn() throws Exception {
        VorabstimmungsAnfrage va = new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        KuendigungsCheckVO kuendigungsCheckVOmock = mock(KuendigungsCheckVO.class);
        when(wbciKuendigungsService.doKuendigungsCheck(any(Long.class), any(LocalDateTime.class))).thenReturn(
                kuendigungsCheckVOmock);
        when(kuendigungsCheckVOmock.getKuendigungsstatus()).thenReturn(
                KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_AKTIV_ABW_OK);
        when(kuendigungsCheckVOmock.getCalculatedEarliestCancelDate()).thenReturn(LocalDateTime.now().minusMonths(1));

        // Gutfall
        DecisionVOwithKuendigungsCheckVO result = testling.createChangeCarrierDateDecisionData(va);
        Assert.assertEquals(result.getKuendigungsCheckVO(), kuendigungsCheckVOmock);
        Assert.assertEquals(result.getAttribute(), DecisionAttribute.KUNDENWUNSCHTERMIN);
        Assert.assertEquals(result.getSuggestedResult(), DecisionResult.OK);
        Assert.assertEquals(result.getSuggestedMeldungsCode(), MeldungsCode.ZWA);
        Assert.assertNotNull(result.getControlObject());
        Assert.assertNotNull(result.getControlValue());
        Assert.assertNotNull(result.getPropertyValue());

        // Schlechtfall
        when(kuendigungsCheckVOmock.getCalculatedEarliestCancelDate()).thenReturn(LocalDateTime.now().plusMonths(2));
        result = testling.createChangeCarrierDateDecisionData(va);
        Assert.assertEquals(result.getKuendigungsCheckVO(), kuendigungsCheckVOmock);
        Assert.assertEquals(result.getAttribute(), DecisionAttribute.KUNDENWUNSCHTERMIN);
        Assert.assertEquals(result.getSuggestedResult(), DecisionResult.ABWEICHEND);
        Assert.assertEquals(result.getSuggestedMeldungsCode(), MeldungsCode.NAT);
        Assert.assertNotNull(result.getControlObject());
        Assert.assertNotNull(result.getControlValue());
        Assert.assertNotNull(result.getPropertyValue());
    }

    @Test
    public void testCreateChangeCarrierDateDecisionDataForRrnp() throws Exception {
        VorabstimmungsAnfrage va = new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_RRNP);

        // Gutfall (KWT > today)
        va.getWbciGeschaeftsfall().setKundenwunschtermin(
                DateCalculationHelper.getDateInWorkingDaysFromNow(1).toLocalDate());
        DecisionVOwithKuendigungsCheckVO result = testling.createChangeCarrierDateDecisionData(va);

        Assert.assertNull(result.getKuendigungsCheckVO());
        Assert.assertEquals(result.getAttribute(), DecisionAttribute.KUNDENWUNSCHTERMIN);
        Assert.assertEquals(result.getSuggestedResult(), DecisionResult.OK);
        Assert.assertEquals(result.getSuggestedMeldungsCode(), MeldungsCode.ZWA);
        Assert.assertNotNull(result.getControlObject());
        Assert.assertNotNull(result.getControlValue());
        Assert.assertNotNull(result.getPropertyValue());

        // Schlechtfall (KWT <= today)
        va.getWbciGeschaeftsfall().setKundenwunschtermin(LocalDate.now());
        result = testling.createChangeCarrierDateDecisionData(va);

        Assert.assertNull(result.getKuendigungsCheckVO());
        Assert.assertEquals(result.getAttribute(), DecisionAttribute.KUNDENWUNSCHTERMIN);
        Assert.assertEquals(result.getSuggestedResult(), DecisionResult.ABWEICHEND);
        Assert.assertEquals(result.getSuggestedMeldungsCode(), MeldungsCode.NAT);
        Assert.assertNotNull(result.getControlObject());
        Assert.assertNotNull(result.getControlValue());
        Assert.assertNotNull(result.getPropertyValue());
    }

    private void assertDecisionData(List<DecisionVO> decisionData, DecisionAttribute attribute, DecisionResult result) {
        boolean attributeFound = false;
        for (DecisionVO decisionVO : decisionData) {
            if (decisionVO.getAttribute().equals(attribute)) {
                Assert.assertEquals(decisionVO.getSuggestedResult(), result,
                        "Assertion failed for " + decisionVO.getName());
                attributeFound = true;
            }
        }

        assertTrue(attributeFound, "Missing attribute in decision data: " + attribute);
    }

    private Adresse createKundenAdresse(PersonOderFirma endkunde, Standort standort) {
        return createKundenAdresse(endkunde, standort, null);
    }

    private Adresse createKundenAdresse(PersonOderFirma endkunde, Standort standort, Person weitereAnschlussInhaber) {
        String vorname;
        String nachname;

        if (endkunde instanceof Person) {
            vorname = ((Person) endkunde).getVorname();
            nachname = ((Person) endkunde).getNachname();
        }
        else {
            vorname = "";
            nachname = ((Firma) endkunde).getFirmenname();
        }

        AdresseBuilder kundenAdresse = new AdresseBuilder()
                .withRandomAdresseNo()
                .withAnrede(Anrede.HERR.name())
                .withVorname(vorname)
                .withName(nachname)
                .withPlz(standort.getPostleitzahl())
                .withOrt(standort.getOrt())
                .withStrasse(standort.getStrasse().getStrassenname())
                .withNummer(standort.getStrasse().getHausnummer());

        if (weitereAnschlussInhaber != null) {
            kundenAdresse.withVorname2(weitereAnschlussInhaber.getVorname());
            kundenAdresse.withName2(weitereAnschlussInhaber.getNachname());
        }

        return kundenAdresse.build();
    }

}
