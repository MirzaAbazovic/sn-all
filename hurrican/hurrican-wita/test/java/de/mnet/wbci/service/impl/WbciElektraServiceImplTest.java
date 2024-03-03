/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 11.06.2014 
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.hurrican.model.cc.Feature.*;
import static de.augustakom.hurrican.model.cc.Feature.FeatureName.*;
import static de.mnet.wbci.model.AutomationTask.*;
import static de.mnet.wbci.model.GeschaeftsfallTyp.*;
import static de.mnet.wbci.model.WbciCdmVersion.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.OngoingStubbing;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.elektra.ElektraCancelOrderResponseDto;
import de.augustakom.hurrican.service.elektra.ElektraFacadeService;
import de.augustakom.hurrican.service.elektra.ElektraResponseDto;
import de.augustakom.hurrican.service.elektra.builder.ElektraCancelOrderResponseDtoBuilder;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.elektra.services.DialingNumberType;
import de.mnet.elektra.services.NumberPortierungType;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.RufnummernportierungVO;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.RufnummernblockTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnTestBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueOrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallRrnpTestBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

@Test(groups = UNIT)
public class WbciElektraServiceImplTest {

    @InjectMocks
    @Spy
    private WbciElektraServiceImpl testling;

    @Mock
    private ElektraFacadeService elektraFacadeServiceMock;

    @Mock
    private WbciCommonService wbciCommonServiceMock;

    @Mock
    private FeatureService featureService;

    @Mock
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;

    @Captor
    private ArgumentCaptor<NumberPortierungType> numberPortierungCaptor;

    @Captor
    private ArgumentCaptor<List<DialingNumberType>> dialNumbersCaptor;

    private AKUser user;

    @BeforeMethod
    public void setUp() {
        testling = new WbciElektraServiceImpl();
        MockitoAnnotations.initMocks(this);
        user = new AKUserBuilder().setPersist(false).withRandomId().withName("username").build();
    }

    public void processRuemVaWhenFeatureOffline() {
        String vorabstimmungsId = "DEU.MNET.VH0000001";
        final WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withVorabstimmungsId(vorabstimmungsId)
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET)
                .buildValid(V1, VA_KUE_MRN);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(gf);
        when(featureService.isFeatureOnline(WBCI_RUEMVA_AUTO_PROCESSING)).thenReturn(false);
        testling.processRuemVa(vorabstimmungsId, user);
        verify(testling, never()).changeOrderDialNumber(any(WbciGeschaeftsfall.class), eq(user), eq(TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN));
    }

    public void testProcessRuemVaWithNumberBlock() throws StoreException {
        final RufnummernportierungAnlage rufnummernportierung = new RufnummernportierungAnlageTestBuilder()
                .addRufnummernblock(new RufnummernblockTestBuilder()
                        .withRnrBlockVon("000")
                        .withRnrBlockBis("950")
                        .buildValid(V1, VA_KUE_MRN))
                .buildValid(V1, VA_KUE_MRN);

        final String portKennungTnbAuf = "D052";
        final String portKennungTnbAbg = rufnummernportierung.getRufnummernbloecke().get(0).getPortierungskennungPKIabg();
        final Long billingOrderNoOrig = 123L;
        final LocalDate wechseltermin = LocalDate.now().plusDays(7);
        final LocalDateTime vaProcessedAt = LocalDateTime.now().minusDays(1);
        final LocalDateTime ruemVaProcessedAt = LocalDateTime.now().minusHours(1);

        final WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withBillingOrderNoOrig(billingOrderNoOrig)
                .withWechseltermin(wechseltermin)
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET)
                .buildValid(V1, VA_KUE_MRN);

        final VorabstimmungsAnfrage vorabstimmungsAnfrage = buildVaRequest(gf, VA_KUE_MRN, vaProcessedAt);
        final RueckmeldungVorabstimmung ruemVa = buildRuemVaMeldung(gf, VA_KUE_MRN, ruemVaProcessedAt, rufnummernportierung);

        when(featureService.isFeatureOnline(WBCI_RUEMVA_AUTO_PROCESSING)).thenReturn(true);
        prepareChangeOrderDialNumberMocks(portKennungTnbAuf, gf, vorabstimmungsAnfrage, ruemVa, false);

        testling.processRuemVa(vorabstimmungsAnfrage.getVorabstimmungsId(), user);

        verify(elektraFacadeServiceMock).changeOrderDialNumber(
                eq(wechseltermin),
                eq(ruemVaProcessedAt.toLocalDate()),
                eq(vaProcessedAt.toLocalDate()),
                eq(Collections.singletonList(billingOrderNoOrig)),
                numberPortierungCaptor.capture());

        NumberPortierungType numberPortierung = numberPortierungCaptor.getValue();
        assertEquals(numberPortierung.getPortKennungTnbAbg(), portKennungTnbAbg);
        assertEquals(numberPortierung.getPortKennungTnbAuf(), portKennungTnbAuf);

        List<DialingNumberType> dialNumbers = numberPortierung.getDialNumbers();
        assertEquals(dialNumbers.size(), 1);
        assertEquals(dialNumbers.get(0).getAreaDialingCode(), rufnummernportierung.getOnkzWithLeadingZero());
        assertEquals(dialNumbers.get(0).getDialingNumber(), rufnummernportierung.getDurchwahlnummer());
        assertEquals(dialNumbers.get(0).getCentral(), rufnummernportierung.getAbfragestelle());
        assertEquals(dialNumbers.get(0).getRangeFrom().toString(), "0");
        assertEquals(dialNumbers.get(0).getRangeTo().toString(), rufnummernportierung.getRufnummernbloecke().get(0).getRnrBlockBis());
    }

    public void testProcessRuemVaWithIndividualNumbers() throws StoreException {
        final String portKennungTnbAuf = "D052";
        final RufnummernportierungEinzeln rufnummernportierung = new RufnummernportierungEinzelnTestBuilder()
                .withPortierungskennungPKIauf(portKennungTnbAuf)
                .buildValid(V1, VA_RRNP);

        final String portKennungTnbAbg = rufnummernportierung.getRufnummernOnkz().get(0).getPortierungskennungPKIabg();
        final Long billingOrderNoOrig = 123L;
        final LocalDate wechseltermin = LocalDate.now().plusDays(7);
        final LocalDateTime vaProcessedAt = LocalDateTime.now().minusDays(1);
        final LocalDateTime ruemVaProcessedAt = LocalDateTime.now().minusHours(1);

        final WbciGeschaeftsfall gf = new WbciGeschaeftsfallRrnpTestBuilder()
                .withBillingOrderNoOrig(billingOrderNoOrig)
                .withWechseltermin(wechseltermin)
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET)
                .withRufnummernportierung(rufnummernportierung)
                .buildValid(V1, VA_RRNP);

        final VorabstimmungsAnfrage vorabstimmungsAnfrage = buildVaRequest(gf, VA_RRNP, vaProcessedAt);
        final RueckmeldungVorabstimmung ruemVa = buildRuemVaMeldung(gf, VA_RRNP, ruemVaProcessedAt, rufnummernportierung);

        when(featureService.isFeatureOnline(WBCI_RUEMVA_AUTO_PROCESSING)).thenReturn(true);
        prepareChangeOrderDialNumberMocks(portKennungTnbAuf, gf, vorabstimmungsAnfrage, ruemVa, false);

        testling.processRuemVa(vorabstimmungsAnfrage.getVorabstimmungsId(), user);

        verify(elektraFacadeServiceMock).changeOrderDialNumber(
                eq(wechseltermin),
                eq(ruemVaProcessedAt.toLocalDate()),
                eq(vaProcessedAt.toLocalDate()),
                eq(Collections.singletonList(billingOrderNoOrig)),
                numberPortierungCaptor.capture());

        NumberPortierungType numberPortierung = numberPortierungCaptor.getValue();
        assertEquals(numberPortierung.getPortKennungTnbAbg(), portKennungTnbAbg);
        assertEquals(numberPortierung.getPortKennungTnbAuf(), portKennungTnbAuf);

        List<DialingNumberType> dialNumbers = numberPortierung.getDialNumbers();
        assertEquals(dialNumbers.size(), 2);
        assertEquals(dialNumbers.get(0).getAreaDialingCode(), rufnummernportierung.getRufnummernOnkz().get(0).getOnkzWithLeadingZero());
        assertEquals(dialNumbers.get(0).getDialingNumber(), rufnummernportierung.getRufnummernOnkz().get(0).getRufnummer());
    }

    public void testProcessRuemVaForKueOrn() throws StoreException {
        final String portKennungTnbAuf = "D052";
        final Long billingOrderNoOrig = 123L;
        final LocalDate wechseltermin = LocalDate.now().plusDays(7);
        final LocalDateTime vaProcessedAt = LocalDateTime.now().minusDays(1);
        final LocalDateTime ruemVaProcessedAt = LocalDateTime.now().minusHours(1);

        final WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueOrnTestBuilder()
                .withBillingOrderNoOrig(billingOrderNoOrig)
                .withWechseltermin(wechseltermin)
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET)
                .buildValid(V1, VA_KUE_ORN);

        final VorabstimmungsAnfrage vorabstimmungsAnfrage = buildVaRequest(gf, VA_KUE_ORN, vaProcessedAt);
        final RueckmeldungVorabstimmung ruemVa =  buildRuemVaMeldung(gf, VA_KUE_ORN, ruemVaProcessedAt);

        when(featureService.isFeatureOnline(WBCI_RUEMVA_AUTO_PROCESSING)).thenReturn(true);
        prepareChangeOrderDialNumberMocks(portKennungTnbAuf, gf, vorabstimmungsAnfrage, ruemVa, false);

        testling.processRuemVa(vorabstimmungsAnfrage.getVorabstimmungsId(), user);

        verify(elektraFacadeServiceMock).changeOrderDialNumber(
                eq(wechseltermin),
                eq(ruemVaProcessedAt.toLocalDate()),
                eq(vaProcessedAt.toLocalDate()),
                eq(Collections.singletonList(billingOrderNoOrig)),
                numberPortierungCaptor.capture());

        Assert.assertNull(numberPortierungCaptor.getValue());
    }

    public void testProcessRuemVaForRrnp() throws StoreException {
        final String portKennungTnbAuf = "D052";
        final RufnummernportierungEinzeln rufnummernportierung = new RufnummernportierungEinzelnTestBuilder()
                .withPortierungskennungPKIauf(portKennungTnbAuf)
                .buildValid(V1, VA_RRNP);

        final String portKennungTnbAbg = rufnummernportierung.getRufnummernOnkz().get(0).getPortierungskennungPKIabg();
        final Long billingOrderNoOrig = 123L;
        final LocalDate wechseltermin = LocalDate.now().plusDays(7);
        final LocalDateTime vaProcessedAt = LocalDateTime.now().minusDays(1);
        final LocalDateTime ruemVaProcessedAt = LocalDateTime.now().minusHours(1);

        final WbciGeschaeftsfall gf = new WbciGeschaeftsfallRrnpTestBuilder()
                .withBillingOrderNoOrig(billingOrderNoOrig)
                .withWechseltermin(wechseltermin)
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET)
                .withRufnummernportierung(rufnummernportierung)
                .buildValid(V1, VA_RRNP);

        final VorabstimmungsAnfrage vorabstimmungsAnfrage = buildVaRequest(gf, VA_RRNP, vaProcessedAt);
        final RueckmeldungVorabstimmung ruemVa = buildRuemVaMeldung(gf, VA_RRNP, ruemVaProcessedAt, rufnummernportierung);

        when(featureService.isFeatureOnline(WBCI_RUEMVA_AUTO_PROCESSING)).thenReturn(true);
        prepareChangeOrderDialNumberMocks(portKennungTnbAuf, gf, vorabstimmungsAnfrage, ruemVa, false);

        testling.processRuemVa(vorabstimmungsAnfrage.getVorabstimmungsId(), user);

        verify(elektraFacadeServiceMock).changeOrderDialNumber(
                eq(wechseltermin),
                eq(ruemVaProcessedAt.toLocalDate()),
                eq(vaProcessedAt.toLocalDate()),
                eq(Collections.singletonList(billingOrderNoOrig)),
                numberPortierungCaptor.capture());

        NumberPortierungType numberPortierung = numberPortierungCaptor.getValue();
        assertEquals(numberPortierung.getPortKennungTnbAbg(), portKennungTnbAbg);
        assertEquals(numberPortierung.getPortKennungTnbAuf(), portKennungTnbAuf);

        List<DialingNumberType> dialNumbers = numberPortierung.getDialNumbers();
        assertEquals(dialNumbers.size(), 2);
        assertEquals(dialNumbers.get(0).getAreaDialingCode(), rufnummernportierung.getRufnummernOnkz().get(0).getOnkzWithLeadingZero());
        assertEquals(dialNumbers.get(0).getDialingNumber(), rufnummernportierung.getRufnummernOnkz().get(0).getRufnummer());
    }

    public void processRrnpWhenFeatureOffline() {
        String vorabstimmungsId = "DEU.MNET.VH0000001";
        final WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withVorabstimmungsId(vorabstimmungsId)
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET)
                .buildValid(V1, VA_KUE_MRN);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(gf);
        when(featureService.isFeatureOnline(WBCI_RRNP_AUTO_PROCESSING)).thenReturn(false);
        testling.processRuemVa(vorabstimmungsId, user);
        verify(testling, never()).changeOrderDialNumber(any(WbciGeschaeftsfall.class), eq(user), eq(TaskName.TAIFUN_NACH_RRNP_AKTUALISIEREN));
    }

    public void testProcessRrnp() throws StoreException {
        final String portKennungTnbAuf = "D052";
        final RufnummernportierungEinzeln rufnummernportierung = new RufnummernportierungEinzelnTestBuilder()
                .withPortierungskennungPKIauf(portKennungTnbAuf)
                .withPortierungszeitfenster(Portierungszeitfenster.ZF2)
                .buildValid(V1, VA_RRNP);

        final LocalDate wechseltermin = LocalDate.now().plusDays(7);
        final LocalDateTime vaProcessedAt = LocalDateTime.now().minusDays(1);

        final WbciGeschaeftsfall gf = new WbciGeschaeftsfallRrnpTestBuilder()
                .withWechseltermin(wechseltermin)
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET)
                .withRufnummernportierung(rufnummernportierung)
                .buildValid(V1, VA_RRNP);

        final VorabstimmungsAnfrage vorabstimmungsAnfrage = buildVaRequest(gf, VA_RRNP, vaProcessedAt);

        when(featureService.isFeatureOnline(WBCI_RRNP_AUTO_PROCESSING)).thenReturn(true);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vorabstimmungsAnfrage.getVorabstimmungsId())).thenReturn(gf);
        when(wbciCommonServiceMock.findWbciRequestByType(vorabstimmungsAnfrage.getVorabstimmungsId(), VorabstimmungsAnfrage.class)).thenReturn(Collections.singletonList(vorabstimmungsAnfrage));
        when(elektraFacadeServiceMock.portCancelledDialNumber(
                eq(Portierungszeitfenster.ZF2.timeFrom(wechseltermin)),
                eq(Portierungszeitfenster.ZF2.timeTo(wechseltermin)),
                any(List.class),
                eq(vaProcessedAt.toLocalDate()),
                eq(portKennungTnbAuf))).thenReturn(new ElektraResponseDto());

        testling.processRrnp(vorabstimmungsAnfrage.getVorabstimmungsId(), user);

        verify(elektraFacadeServiceMock).portCancelledDialNumber(
                eq(Portierungszeitfenster.ZF2.timeFrom(wechseltermin)),
                eq(Portierungszeitfenster.ZF2.timeTo(wechseltermin)),
                dialNumbersCaptor.capture(),
                eq(vaProcessedAt.toLocalDate()),
                eq(portKennungTnbAuf));

        List<DialingNumberType> dialNumbers = dialNumbersCaptor.getValue();
        assertEquals(dialNumbers.size(), 2);
        assertEquals(dialNumbers.get(0).getAreaDialingCode(), rufnummernportierung.getRufnummernOnkz().get(0).getOnkzWithLeadingZero());
        assertEquals(dialNumbers.get(0).getDialingNumber(), rufnummernportierung.getRufnummernOnkz().get(0).getRufnummer());
    }

    public void testProcessRrnpWrongGeschaeftsfallTyp() throws StoreException {
        String vorabstimmungsId = "DEU.MNET.VH0000001";
        final WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET)
                .buildValid(V1, VA_KUE_MRN);

        when(featureService.isFeatureOnline(WBCI_RRNP_AUTO_PROCESSING)).thenReturn(true);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(gf);

        try {
            testling.processRrnp(vorabstimmungsId, user);
            Assert.fail("Missing exception due to wrong Geschaeftsfalltyp for Rrnp processing");
        } catch (WbciServiceException e) {
            Assert.assertTrue(e.getMessage().contains("'portCancelledDialNumber' kann nicht für die VA 'DEU.MNET.VH0000001' aufgerufen werden"));
        }

        verify(elektraFacadeServiceMock, never()).portCancelledDialNumber(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(List.class),
                any(LocalDate.class),
                anyString());
    }

    public void testUpdatePortKennungTnb() throws StoreException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.DTAG)
                .buildValid(V1, VA_KUE_MRN);

        when(wbciCommonServiceMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(buildRuemVaMeldung());
        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), UebernahmeRessourceMeldung.class)).thenReturn(buildAkmTrMeldung());

        when(elektraFacadeServiceMock.updatePortKennungTnb(Collections.singletonList(wbciGeschaeftsfall.getBillingOrderNoOrig()),
                "D001", "D052")).thenReturn(new ElektraResponseDto());

        testling.updatePortKennungTnbTx(wbciGeschaeftsfall.getVorabstimmungsId(), user);

        verify(elektraFacadeServiceMock).updatePortKennungTnb(Collections.singletonList(wbciGeschaeftsfall.getBillingOrderNoOrig()), "D001", "D052");
    }

    public void processAkmTrWhenFeatureOffline() {
        String vorabstimmungsId = "DEU.MNET.VH0000001";
        final WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withVorabstimmungsId(vorabstimmungsId)
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET)
                .buildValid(V1, VA_KUE_MRN);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(gf);
        when(featureService.isFeatureOnline(WBCI_AKMTR_AUTO_PROCESSING)).thenReturn(false);
        testling.processRuemVa(vorabstimmungsId, user);
        verify(testling, never()).changeOrderDialNumber(any(WbciGeschaeftsfall.class), eq(user), eq(TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN));
    }

    public void testProcessAkmTrVaKueMrn() throws StoreException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.DTAG)
                .buildValid(V1, VA_KUE_MRN);

        when(featureService.isFeatureOnline(WBCI_AKMTR_AUTO_PROCESSING)).thenReturn(true);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(buildRuemVaMeldung());
        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), UebernahmeRessourceMeldung.class)).thenReturn(buildAkmTrMeldung());

        when(elektraFacadeServiceMock.updatePortKennungTnb(Collections.singletonList(wbciGeschaeftsfall.getBillingOrderNoOrig()),
                "D001", "D052")).thenReturn(new ElektraResponseDto());

        testling.processAkmTr(wbciGeschaeftsfall.getVorabstimmungsId(), user);

        verify(elektraFacadeServiceMock).updatePortKennungTnb(Collections.singletonList(wbciGeschaeftsfall.getBillingOrderNoOrig()), "D001", "D052");
    }

    public void testProcessAkmTrVaKueOrn() throws StoreException {
        WbciGeschaeftsfallKueOrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueOrnTestBuilder()
                .withStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.DTAG)
                .buildValid(V1, VA_KUE_ORN);

        when(featureService.isFeatureOnline(WBCI_AKMTR_AUTO_PROCESSING)).thenReturn(true);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), UebernahmeRessourceMeldung.class)).thenReturn(buildAkmTrMeldung());
        when(wbciCommonServiceMock.getTnbKennung(wbciGeschaeftsfall.getBillingOrderNoOrig())).thenReturn("D052");

        when(elektraFacadeServiceMock.updatePortKennungTnb(Collections.singletonList(wbciGeschaeftsfall.getBillingOrderNoOrig()),
                "D001", "D052")).thenReturn(new ElektraResponseDto());

        testling.processAkmTr(wbciGeschaeftsfall.getVorabstimmungsId(), user);

        verify(elektraFacadeServiceMock).updatePortKennungTnb(Collections.singletonList(wbciGeschaeftsfall.getBillingOrderNoOrig()), "D001", "D052");
    }

    public void testProcessAkmTrMissingRueckmeldungVorabstimmung() throws StoreException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.DTAG)
                .buildValid(V1, VA_KUE_MRN);

        when(featureService.isFeatureOnline(WBCI_AKMTR_AUTO_PROCESSING)).thenReturn(true);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(null);

        try {
            testling.processAkmTr(wbciGeschaeftsfall.getVorabstimmungsId(), user);
            Assert.fail("Missing WBCI exception due to missing RUEMVA Meldung");
        } catch (WbciServiceException e) {
            Assert.assertTrue(e.getMessage().contains("Es liegt keine Rueckmeldung"));
            Assert.assertTrue(e.getMessage().contains(wbciGeschaeftsfall.getVorabstimmungsId()));
        }

        verify(elektraFacadeServiceMock, never()).updatePortKennungTnb(any(), anyString(), anyString());
    }

    public void testProcessAkmTrMissingUebernahmeRessourceMeldung() throws StoreException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.DTAG)
                .buildValid(V1, VA_KUE_MRN);

        when(featureService.isFeatureOnline(WBCI_AKMTR_AUTO_PROCESSING)).thenReturn(true);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(buildRuemVaMeldung());
        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), UebernahmeRessourceMeldung.class)).thenReturn(null);

        try {
            testling.processAkmTr(wbciGeschaeftsfall.getVorabstimmungsId(), user);
            Assert.fail("Missing WBCI exception due to missing AKM-TR Meldung");
        } catch (WbciServiceException e) {
            Assert.assertTrue(e.getMessage().contains("Es liegt keine UebernahmeRessourceMeldung"));
            Assert.assertTrue(e.getMessage().contains(wbciGeschaeftsfall.getVorabstimmungsId()));
        }

        verify(elektraFacadeServiceMock, never()).updatePortKennungTnb(any(), anyString(), anyString());
    }

    public void testProcessAkmTrMissingTnbKennung() throws StoreException {
        WbciGeschaeftsfallKueOrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueOrnTestBuilder()
                .withStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.DTAG)
                .buildValid(V1, VA_KUE_ORN);

        when(featureService.isFeatureOnline(WBCI_AKMTR_AUTO_PROCESSING)).thenReturn(true);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), UebernahmeRessourceMeldung.class)).thenReturn(buildAkmTrMeldung());
        when(wbciCommonServiceMock.getTnbKennung(wbciGeschaeftsfall.getBillingOrderNoOrig())).thenReturn(null);

        try {
            testling.processAkmTr(wbciGeschaeftsfall.getVorabstimmungsId(), user);
            Assert.fail("Missing WBCI exception due to missing TnbKennung for Hurrican Auftrag");
        } catch (WbciServiceException e) {
            assertEquals(e.getMessage(), String.format("Es konnte keine TnbKennung zum Auftrag %s ermittelt werden", wbciGeschaeftsfall.getAuftragId()));
        }

        verify(elektraFacadeServiceMock, never()).updatePortKennungTnb(any(), anyString(), anyString());
    }

    public void testProcessAkmTrMissingPkiAufKennungInAkmTr() throws StoreException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.DTAG)
                .buildValid(V1, VA_KUE_MRN);

        UebernahmeRessourceMeldung akmTrMeldung = buildAkmTrMeldung();
        akmTrMeldung.setPortierungskennungPKIauf(null);

        when(featureService.isFeatureOnline(WBCI_AKMTR_AUTO_PROCESSING)).thenReturn(true);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(buildRuemVaMeldung());
        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), UebernahmeRessourceMeldung.class)).thenReturn(akmTrMeldung);

        try {
            testling.processAkmTr(wbciGeschaeftsfall.getVorabstimmungsId(), user);
            Assert.fail("Missing WBCI exception due to missing PKIauf kennung in AKM-TR");
        } catch (WbciServiceException e) {
            Assert.assertTrue(e.getMessage().startsWith("Es konnte keine Portierungskennung PKIauf"));
        }

        verify(elektraFacadeServiceMock, never()).updatePortKennungTnb(any(), anyString(), anyString());
    }

    public void testProcessAkmTrWithNonBillingRelevantOrders() throws StoreException {
        Set<Long> nonBillingRelevantOrderNoOrigs = new HashSet<>();
        nonBillingRelevantOrderNoOrigs.add(200L);
        nonBillingRelevantOrderNoOrigs.add(300L);
        nonBillingRelevantOrderNoOrigs.add(400L);

        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.DTAG)
                .withNonBillingRelevantOrderNos(nonBillingRelevantOrderNoOrigs)
                .buildValid(V1, VA_KUE_MRN);

        List<Long> auftragOrderNoOrigs = new ArrayList<>();
        auftragOrderNoOrigs.add(wbciGeschaeftsfall.getBillingOrderNoOrig());
        auftragOrderNoOrigs.addAll(nonBillingRelevantOrderNoOrigs);

        when(featureService.isFeatureOnline(WBCI_AKMTR_AUTO_PROCESSING)).thenReturn(true);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(buildRuemVaMeldung());
        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), UebernahmeRessourceMeldung.class)).thenReturn(buildAkmTrMeldung());

        when(elektraFacadeServiceMock.updatePortKennungTnb(
                Matchers.<List<Long>>argThat(new ListWithoutOrderMatcher(auftragOrderNoOrigs)), eq("D001"), eq("D052")))
                .thenReturn(new ElektraResponseDto());

        ElektraResponseDto elektraResponseDto = testling.processAkmTr(wbciGeschaeftsfall.getVorabstimmungsId(), user);
        assertNotNull(elektraResponseDto);

        verify(elektraFacadeServiceMock).updatePortKennungTnb(
                Matchers.<List<Long>>argThat(new ListWithoutOrderMatcher(auftragOrderNoOrigs)), eq("D001"), eq("D052"));
    }

    private class ListWithoutOrderMatcher<T> extends ArgumentMatcher<List<T>> {
        private final List<T> expectedList;

        private ListWithoutOrderMatcher(List<T> expectedList) {
            this.expectedList = expectedList;
        }

        @Override
        public boolean matches(Object o) {
            final List list = (List) o;
            return list.size() == expectedList.size() && list.containsAll(expectedList);
        }
    }

    private VorabstimmungsAnfrage buildVaRequest(WbciGeschaeftsfall gf, GeschaeftsfallTyp typ, LocalDateTime processedAt) {
        return new VorabstimmungsAnfrageTestBuilder()
                .withWbciGeschaeftsfall(gf)
                .withProcessedAt(processedAt)
                .buildValid(V1, typ);
    }

    private RueckmeldungVorabstimmung buildRuemVaMeldung(WbciGeschaeftsfall gf, GeschaeftsfallTyp typ, LocalDateTime processedAt, Rufnummernportierung rufnummernportierung) {
        if (rufnummernportierung != null) {
            return new RueckmeldungVorabstimmungTestBuilder()
                    .withWbciGeschaeftsfall(gf)
                    .withProcessedAt(processedAt)
                    .withRufnummernportierung(rufnummernportierung)
                    .buildValid(V1, typ);
        } else {
            return new RueckmeldungVorabstimmungTestBuilder()
                    .withWbciGeschaeftsfall(gf)
                    .withProcessedAt(processedAt)
                    .buildValid(V1, typ);
        }
    }

    private RueckmeldungVorabstimmung buildRuemVaMeldung(WbciGeschaeftsfall gf, GeschaeftsfallTyp typ, LocalDateTime processedAt) {
        return buildRuemVaMeldung(gf, typ, processedAt, null);
    }

    private RueckmeldungVorabstimmung buildRuemVaMeldung() {
        return new RueckmeldungVorabstimmungTestBuilder()
                .withRufnummernportierung(new RufnummernportierungAnlageTestBuilder()
                        .withPortierungskennungPKIauf("D001")
                        .addRufnummernblock(new RufnummernblockTestBuilder()
                                .withPkiAbg("D052")
                                .buildValid(V1, VA_KUE_MRN))
                        .buildValid(V1, VA_KUE_MRN))
                .buildValid(V1, VA_KUE_MRN);
    }

    private UebernahmeRessourceMeldung buildAkmTrMeldung() {
        return new UebernahmeRessourceMeldungTestBuilder()
                .withPortierungskennungPKIauf("D001")
                .buildValid(V1, VA_KUE_MRN);
    }

    @Test
    public void processTvErlmWhenFeatureOffline() {
        String vorabstimmungsId = "DEU.MNET.VH0000001";
        final WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withVorabstimmungsId(vorabstimmungsId)
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET)
                .buildValid(V1, VA_KUE_MRN);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(gf);
        when(featureService.isFeatureOnline(WBCI_TVS_VA_AUTO_PROCESSING)).thenReturn(false);
        testling.processTvErlm(vorabstimmungsId, user);
        verify(wbciCommonServiceMock).findLastForVaId(vorabstimmungsId, ErledigtmeldungTerminverschiebung.class);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Es konnte keine Erledigtmeldung zu einer Terminverschiebung.*")
    public void processTvErlmWithoutTvErlm() {
        String vorabstimmungsId = "DEU.MNET.VH0000001";
        when(wbciCommonServiceMock.findLastForVaId(vorabstimmungsId, ErledigtmeldungTerminverschiebung.class)).thenReturn(null);
        when(featureService.isFeatureOnline(WBCI_TVS_VA_AUTO_PROCESSING)).thenReturn(true);
        testling.processTvErlm(vorabstimmungsId, user);
    }

    @Test
    public void processOutgoingTvErlmWithExceptionThrownDuringElektraCall() throws StoreException, FindException {
        String vorabstimmungsId = "DEU.MNET.VH0000001";
        final ErledigtmeldungTerminverschiebung erlm = mock(ErledigtmeldungTerminverschiebung.class);
        LocalDate wechseltermin = LocalDate.now();
        final WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueOrnTestBuilder()
                .withBillingOrderNoOrig(1L)
                .withNonBillingRelevantOrderNos(new HashSet<>(Arrays.asList(2L, 3L)))
                .withWechseltermin(wechseltermin)
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.DTAG)
                .buildValid(V1, VA_KUE_ORN);

        when(wbciCommonServiceMock.findLastForVaId(vorabstimmungsId, ErledigtmeldungTerminverschiebung.class))
                .thenReturn(erlm);
        when(erlm.getWbciGeschaeftsfall()).thenReturn(wbciGeschaeftsfall);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);
        Exception exceptionThrown = new RuntimeException();
        when(elektraFacadeServiceMock.changeOrderCancellationDate(new HashSet<>(Arrays.asList(1L, 2L, 3L)),
                wechseltermin)).thenThrow(exceptionThrown);
        doReturn(true).when(testling).isFeatureOnline(WBCI_TVS_VA_AUTO_PROCESSING);

        testling.processTvErlm(vorabstimmungsId, user);
        verify(wbciCommonServiceMock).findLastForVaId(vorabstimmungsId, ErledigtmeldungTerminverschiebung.class);
        verify(wbciCommonServiceMock).findWbciGeschaeftsfall(vorabstimmungsId);
        verify(testling).handleCaughtException(eq("changeOrderCancellationDate"), eq(wbciGeschaeftsfall), 
                any(Meldung.class), eq(exceptionThrown), eq(TaskName.TAIFUN_NACH_TVS_ERLM_AKTUALISIEREN), eq(user));
    }

    @Test
    public void processOutgoingTvErlm() throws StoreException, FindException {
        String vorabstimmungsId = "DEU.MNET.VH0000001";
        final ErledigtmeldungTerminverschiebung erlm = mock(ErledigtmeldungTerminverschiebung.class);
        final WbciGeschaeftsfall wbciGeschaeftsfall = mock(WbciGeschaeftsfall.class);
        prepareGfMockForDonatingRole(wbciGeschaeftsfall);

        when(wbciCommonServiceMock.findLastForVaId(vorabstimmungsId, ErledigtmeldungTerminverschiebung.class))
                .thenReturn(erlm);
        when(erlm.getWbciGeschaeftsfall()).thenReturn(wbciGeschaeftsfall);
        Set<Long> orderNoOrigs = new HashSet<>(Arrays.asList(1L, 2L, 3L));
        LocalDateTime wechseltermin = LocalDateTime.now();
        when(wbciGeschaeftsfall.getOrderNoOrigs()).thenReturn(orderNoOrigs);
        when(wbciGeschaeftsfall.getWechseltermin()).thenReturn(wechseltermin.toLocalDate());
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);
        when(featureService.isFeatureOnline(WBCI_TVS_VA_AUTO_PROCESSING)).thenReturn(true);

        testling.processTvErlm(vorabstimmungsId, user);
        verify(wbciCommonServiceMock).findLastForVaId(vorabstimmungsId, ErledigtmeldungTerminverschiebung.class);
        verify(wbciCommonServiceMock).findWbciGeschaeftsfall(vorabstimmungsId);
        verify(elektraFacadeServiceMock).changeOrderCancellationDate(orderNoOrigs, wechseltermin.toLocalDate());
    }

    @Test
    public void processIncomingTvErlmWithExceptionThrownDuringElektraCall() throws StoreException {
        final String portKennungTnbAuf = "D052";
        final Long billingOrderNoOrig = 123L;
        final LocalDate wechseltermin = LocalDate.now().plusDays(7);
        final LocalDateTime vaProcessedAt = LocalDateTime.now().minusDays(1);
        final LocalDateTime ruemVaProcessedAt = LocalDateTime.now().minusHours(1);

        final ErledigtmeldungTerminverschiebung erlmMock = mock(ErledigtmeldungTerminverschiebung.class);
        final WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueOrnTestBuilder()
                .withBillingOrderNoOrig(billingOrderNoOrig)
                .withWechseltermin(wechseltermin)
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET)
                .buildValid(V1, VA_KUE_ORN);

        final VorabstimmungsAnfrage vorabstimmungsAnfrage = buildVaRequest(gf, VA_KUE_ORN, vaProcessedAt);
        final RueckmeldungVorabstimmung ruemVa = buildRuemVaMeldung(gf, VA_KUE_ORN, ruemVaProcessedAt);

        when(erlmMock.getWbciGeschaeftsfall()).thenReturn(gf);
        when(wbciCommonServiceMock.findLastForVaId(vorabstimmungsAnfrage.getVorabstimmungsId(), ErledigtmeldungTerminverschiebung.class)).thenReturn(erlmMock);
        doReturn(true).when(testling).isFeatureOnline(WBCI_TVS_VA_AUTO_PROCESSING);
        prepareChangeOrderDialNumberMocks(portKennungTnbAuf, gf, vorabstimmungsAnfrage, ruemVa, true);

        testling.processTvErlm(vorabstimmungsAnfrage.getVorabstimmungsId(), user);
        verify(testling).handleCaughtException(eq("changeOrderDialNumber"), eq(gf), eq(ruemVa), any(Exception.class),
                eq(TaskName.TAIFUN_NACH_TVS_ERLM_AKTUALISIEREN), eq(user));
    }

    @Test
    public void processIncomingTvErlm() throws StoreException, FindException {
        final String portKennungTnbAuf = "D052";
        final Long billingOrderNoOrig = 123L;
        final LocalDate wechseltermin = LocalDate.now().plusDays(7);
        final LocalDateTime vaProcessedAt = LocalDateTime.now().minusDays(1);
        final LocalDateTime ruemVaProcessedAt = LocalDateTime.now().minusHours(1);

        final ErledigtmeldungTerminverschiebung erlmMock = mock(ErledigtmeldungTerminverschiebung.class);
        final WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueOrnTestBuilder()
                .withBillingOrderNoOrig(billingOrderNoOrig)
                .withWechseltermin(wechseltermin)
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET)
                .buildValid(V1, VA_KUE_ORN);

        final VorabstimmungsAnfrage vorabstimmungsAnfrage = buildVaRequest(gf, VA_KUE_ORN, vaProcessedAt);
        final RueckmeldungVorabstimmung ruemVa = buildRuemVaMeldung(gf, VA_KUE_ORN, ruemVaProcessedAt);

        when(erlmMock.getWbciGeschaeftsfall()).thenReturn(gf);
        when(wbciCommonServiceMock.findLastForVaId(vorabstimmungsAnfrage.getVorabstimmungsId(), ErledigtmeldungTerminverschiebung.class)).thenReturn(erlmMock);
        when(featureService.isFeatureOnline(WBCI_TVS_VA_AUTO_PROCESSING)).thenReturn(true);
        prepareChangeOrderDialNumberMocks(portKennungTnbAuf, gf, vorabstimmungsAnfrage, ruemVa, false);

        testling.processTvErlm(vorabstimmungsAnfrage.getVorabstimmungsId(), user);

        verify(elektraFacadeServiceMock).changeOrderDialNumber(
                eq(wechseltermin),
                eq(ruemVaProcessedAt.toLocalDate()),
                eq(vaProcessedAt.toLocalDate()),
                eq(Collections.singletonList(billingOrderNoOrig)),
                numberPortierungCaptor.capture());

        Assert.assertNull(numberPortierungCaptor.getValue());
    }

    @Test
    public void testAddDialNumber() throws StoreException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.DTAG)
                .buildValid(V1, VA_KUE_MRN);

        when(featureService.isFeatureOnline(WBCI_DIAL_NUMBER_UPDATE)).thenReturn(true);
        when(wbciCommonServiceMock.getTnbKennung(anyLong())).thenReturn("D052");
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), RueckmeldungVorabstimmung.class))
                .thenReturn(new RueckmeldungVorabstimmungTestBuilder().withWechseltermin(LocalDate.now().plusDays(1)).build());

        RufnummernportierungVO vo = new RufnummernportierungVO();
        vo.setOnkz("89");
        vo.setDnBase("123");
        vo.setPkiAbg("D001");
        
        when(elektraFacadeServiceMock.addDialNumber(
                eq(wbciGeschaeftsfall.getBillingOrderNoOrig()),
                any(DialingNumberType.class),
                eq("D052"),
                eq("D001"),
                any(LocalDate.class)))
                .thenReturn(new ElektraResponseDto());

        testling.addDialNumber(wbciGeschaeftsfall.getVorabstimmungsId(), vo, user);

        verify(elektraFacadeServiceMock)
                .addDialNumber(
                        eq(wbciGeschaeftsfall.getBillingOrderNoOrig()), 
                        any(DialingNumberType.class), 
                        eq("D052"),
                        eq("D001"),
                        any(LocalDate.class));
    }


    @Test(expectedExceptions = WbciServiceException.class, 
            expectedExceptionsMessageRegExp = "Wechseltermin konnte nicht ermittelt werden. Wahrscheinlich ist keine RUEM-VA zur Vorabstimmung DEU.MNET.1234 vorhanden.")
    public void testAddDialNumberNoRealDate() throws StoreException {
        when(featureService.isFeatureOnline(WBCI_DIAL_NUMBER_UPDATE)).thenReturn(true);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(anyString())).thenReturn(null);
        when(wbciCommonServiceMock.findLastForVaId(anyString(), eq(RueckmeldungVorabstimmung.class)))
                .thenReturn(null);

        testling.addDialNumber("DEU.MNET.1234", null, user);
    }

    
    @Test
    public void testDeleteDialNumber() throws StoreException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.DTAG)
                .buildValid(V1, VA_KUE_MRN);

        when(featureService.isFeatureOnline(WBCI_DIAL_NUMBER_UPDATE)).thenReturn(true);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        when(elektraFacadeServiceMock.deleteDialNumber(
                eq(wbciGeschaeftsfall.getBillingOrderNoOrig()),
                any(DialingNumberType.class)))
                .thenReturn(new ElektraResponseDto());

        RufnummernportierungVO vo = new RufnummernportierungVO();
        vo.setOnkz("89");
        vo.setDnBase("123");

        testling.deleteDialNumber(wbciGeschaeftsfall.getVorabstimmungsId(), vo, user);

        verify(elektraFacadeServiceMock)
                .deleteDialNumber(
                        eq(wbciGeschaeftsfall.getBillingOrderNoOrig()),
                        any(DialingNumberType.class));
    }
    

    private void prepareGfMockForDonatingRole(WbciGeschaeftsfall gf) {
        when(gf.getAufnehmenderEKP()).thenReturn(CarrierCode.DTAG);
        when(gf.getAbgebenderEKP()).thenReturn(CarrierCode.MNET);
    }

    private void prepareChangeOrderDialNumberMocks(String portKennungTnb,
            WbciGeschaeftsfall wbciGeschaeftsfall,
            VorabstimmungsAnfrage vorabstimmungsAnfrage,
            RueckmeldungVorabstimmung ruemVa,
            boolean simulateElektraException) throws StoreException {

        // prepare
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vorabstimmungsAnfrage.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        when(wbciCommonServiceMock.findWbciRequestByType(vorabstimmungsAnfrage.getVorabstimmungsId(), VorabstimmungsAnfrage.class)).thenReturn(Collections.singletonList(vorabstimmungsAnfrage));
        when(wbciCommonServiceMock.findLastForVaId(vorabstimmungsAnfrage.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(ruemVa);
        when(wbciCommonServiceMock.getTnbKennung(1L)).thenReturn(portKennungTnb);

        OngoingStubbing<ElektraResponseDto> elektraOngoingStub = when(elektraFacadeServiceMock.changeOrderDialNumber(
                        eq(wbciGeschaeftsfall.getWechseltermin()),
                        eq(DateConverterUtils.asLocalDate(ruemVa.getProcessedAt())),
                        eq(DateConverterUtils.asLocalDate(vorabstimmungsAnfrage.getProcessedAt())),
                        eq(Collections.singletonList(wbciGeschaeftsfall.getBillingOrderNoOrig())),
                        any(NumberPortierungType.class))
        );
        if(simulateElektraException) {
            elektraOngoingStub.thenThrow(Exception.class);
        }
        else {
            elektraOngoingStub.thenReturn(new ElektraResponseDto());
        }
    }

    @Test
    public void isFeatureOnline() {
        final FeatureName featureName = WBCI_RUEMVA_AUTO_PROCESSING;
        when(featureService.isFeatureOnline(featureName)).thenReturn(true);
        assertTrue(testling.isFeatureOnline(featureName));
    }

    @Test
    public void isFeatureOffline() {
        final FeatureName featureName = WBCI_RUEMVA_AUTO_PROCESSING;
        when(featureService.isFeatureOnline(featureName)).thenReturn(false);
        assertFalse(testling.isFeatureOnline(featureName));
    }

    @Test
    public void handleCaughtException() {
        final WbciGeschaeftsfallKueMrn gf = new WbciGeschaeftsfallKueMrn();
        final RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmung();
        final TaskName taifunNachRuemvaAktualisieren = TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN;
        final String elektraService = "processRuemVa";
        final String message = "exception message";
        final ElektraResponseDto elektraResponseDto = testling.handleCaughtException(elektraService, gf, ruemVa,
                new RuntimeException(message), taifunNachRuemvaAktualisieren, user);
        verify(wbciGeschaeftsfallService).createOrUpdateAutomationTask(eq(gf), eq(ruemVa), eq(taifunNachRuemvaAktualisieren),
                eq(AutomationStatus.ERROR), argThat(new ArgumentMatcher<String>() {
                    @Override
                    public boolean matches(Object o) {
                        final String msg = (String) o;
                        return msg.contains(elektraService) && msg.contains(message) && msg.contains("RuntimeException");
                    }
                }), eq(user)
        );
        assertTrue(elektraResponseDto.getModifications().contains(message));
        assertEquals(elektraResponseDto.getStatus(), ElektraResponseDto.ResponseStatus.ERROR);
    }

    @Test
    public void handleFeatureNotOnline() {
        final WbciGeschaeftsfallKueMrn gf = new WbciGeschaeftsfallKueMrn();
        final RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmung();
        final TaskName taifunNachRuemvaAktualisieren = TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN;
        final FeatureName featureName = WBCI_RUEMVA_AUTO_PROCESSING;
        final ElektraResponseDto elektraResponseDto = testling.handleFeatureNotOnline(gf, ruemVa, featureName,
                taifunNachRuemvaAktualisieren, user);
        verify(wbciGeschaeftsfallService).createOrUpdateAutomationTask(eq(gf), eq(ruemVa), eq(taifunNachRuemvaAktualisieren),
                eq(AutomationStatus.FEATURE_IS_NOT_ENABLED), argThat(new ArgumentMatcher<String>() {
                    @Override
                    public boolean matches(Object o) {
                        final String msg = (String) o;
                        return msg.contains(featureName.name());
                    }
                }), eq(user)
        );
        assertTrue(elektraResponseDto.getModifications().contains(featureName.name()));
        assertEquals(elektraResponseDto.getStatus(), ElektraResponseDto.ResponseStatus.ERROR);
    }

    @DataProvider
    private Object[][] cancelBillingOrderDataProvider(){
        return new Object[][] {
                {true, ElektraFacadeService.REPORT_TYPE_KB_MIT_RUECKFORDERUNG},
                {false, ElektraFacadeService.REPORT_TYPE_KB_OHNE_RUECKFORDERUNG},
        };
    }

    @Test(dataProvider = "cancelBillingOrderDataProvider")
    public void testCancelBillingOrder(boolean reclaimPositions, String expectedDocumentName) throws StoreException, FindException {
        final String portKennungTnbAuf = "D001";
        final RufnummernportierungEinzeln rufnummernportierung = new RufnummernportierungEinzelnTestBuilder()
                .withPortierungskennungPKIauf(portKennungTnbAuf)
                .buildValid(V1, VA_KUE_MRN);

        final Long billingOrderNoOrig = 123L;
        final LocalDate wechseltermin = LocalDate.now().plusDays(7);

        final WbciGeschaeftsfall gf = new WbciGeschaeftsfallRrnpTestBuilder()
                .withBillingOrderNoOrig(billingOrderNoOrig)
                .withWechseltermin(wechseltermin)
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET)
                .withRufnummernportierung(rufnummernportierung)
                .buildValid(V1, VA_RRNP);

        final RueckmeldungVorabstimmung ruemVa = buildRuemVaMeldung(gf, VA_RRNP, LocalDateTime.now(), rufnummernportierung);

        when(elektraFacadeServiceMock.cancelOrder(anyLong(), any(LocalDate.class), any(LocalDate.class), any(CarrierCode.class),
                anyListOf(DialingNumberType.class)))
                .thenReturn(new ElektraCancelOrderResponseDtoBuilder()
                                .withReclaimPositions(reclaimPositions)
                                .build()
                );
        when(wbciCommonServiceMock.findOriginalWbciGeschaeftsfall(anyString())).thenReturn(gf);
        final VorabstimmungsAnfrage vorabstimmungsAnfrage = buildVaRequest(gf, VA_RRNP, LocalDateTime.now().minusDays(1));
        when(wbciCommonServiceMock.findWbciRequestByType(vorabstimmungsAnfrage.getVorabstimmungsId(), VorabstimmungsAnfrage.class)).thenReturn(Collections.singletonList(vorabstimmungsAnfrage));

        testling.cancelBillingOrder(gf.getBillingOrderNoOrig(), ruemVa);

        verify(elektraFacadeServiceMock).cancelOrder(
                eq(gf.getBillingOrderNoOrig()),
                eq(ruemVa.getWechseltermin()),
                any(LocalDate.class),
                eq(ruemVa.getEKPPartner()),
                anyListOf(DialingNumberType.class));
        verify(elektraFacadeServiceMock).generateAndPrintReportByType(gf.getBillingOrderNoOrig(), expectedDocumentName);
    }


    @Test
    public void testUndoCancellation() throws StoreException, FindException {
        when(elektraFacadeServiceMock.undoCancellation(anyLong()))
                .thenReturn(new ElektraCancelOrderResponseDto());

        testling.undoCancellation(1L);

        verify(elektraFacadeServiceMock).undoCancellation(anyLong());
        verify(elektraFacadeServiceMock).generateAndPrintReportByType(1L, ElektraFacadeService.REPORT_TYPE_KB_STORNO);
    }
    
}
