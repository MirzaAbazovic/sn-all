/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.service.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.validation.*;
import org.apache.commons.lang.StringUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.AbbruchmeldungTerminverschiebung;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.PersonTestBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDeadlineService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciSchedulerService;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.validation.constraints.CheckTvTerminNotBroughtForward;
import de.mnet.wbci.validation.helper.ConstraintViolationHelper;

@Test(groups = BaseTest.UNIT)
public class WbciTvServiceImplTest {

    @InjectMocks
    private WbciTvServiceImpl testling;
    @Mock
    private WbciDao wbciDao;
    @Mock
    private WbciCommonService wbciCommonService;
    @Mock
    private WbciValidationService wbciValidationService;
    @Mock
    private ConstraintViolationHelper constraintViolationHelper;
    @Mock
    private WbciDeadlineService wbciDeadlineService;
    @Mock
    private WbciMeldungService wbciMeldungService;
    @Mock
    private WbciSchedulerService wbciSchedulerService;
    @Mock
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;

    @BeforeMethod
    public void setUp() {
        testling = new WbciTvServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateWbciTvWithoutAenderungsId() {
        LocalDate tvTermin = LocalDate.now().plusDays(20);
        TerminverschiebungsAnfrage terminverschiebung = new TerminverschiebungsAnfrageBuilder()
                .withTvTermin(tvTermin)
                .build();
        testCreateWbciTv(terminverschiebung);
    }

    @Test
    public void testCreateWbciTvWithAenderungsId() {
        String aenId = "DEU.MNET.T999999999";
        LocalDate tvTermin = LocalDate.now().plusDays(20);
        TerminverschiebungsAnfrage terminverschiebung = new TerminverschiebungsAnfrageBuilder()
                .withTvTermin(tvTermin)
                .withAenderungsId(aenId)
                .build();
        testCreateWbciTv(terminverschiebung);
    }

    private void testCreateWbciTv(TerminverschiebungsAnfrage terminverschiebung) {
        String aenId = terminverschiebung.getAenderungsId();
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.findWbciGeschaeftsfall(eq(wbciGeschaeftsfallKueMrn.getVorabstimmungsId())))
                .thenReturn(wbciGeschaeftsfallKueMrn);

        if (StringUtils.isEmpty(aenId)) {
            when(wbciCommonService.getNextPreAgreementId(RequestTyp.TV)).thenReturn("DEU.MNET.TH99999999");
        }

        // reset any data that should be overridden
        terminverschiebung.setCreationDate(null);
        terminverschiebung.setUpdatedAt(null);
        terminverschiebung.setIoType(null);

        TerminverschiebungsAnfrage result = testling.createWbciTv(terminverschiebung,
                wbciGeschaeftsfallKueMrn.getVorabstimmungsId());

        assertNotNull(result);
        assertNotNull(result.getCreationDate());
        assertNotNull(result.getUpdatedAt());
        assertEquals(result.getIoType(), IOType.OUT);
        assertEquals(result.getTvTermin(), terminverschiebung.getTvTermin());
        assertEquals(result.getWbciGeschaeftsfall().getKundenwunschtermin(), terminverschiebung.getTvTermin());
        assertEquals(result.getEndkunde(), terminverschiebung.getEndkunde());
        assertEquals(result.getRequestStatus(), WbciRequestStatus.TV_VORGEHALTEN);
        assertNotNull(result.getWbciGeschaeftsfall());
        assertEquals(result.getWbciGeschaeftsfall(), wbciGeschaeftsfallKueMrn);
        verify(wbciCommonService, times(1)).findWbciGeschaeftsfall(eq(wbciGeschaeftsfallKueMrn.getVorabstimmungsId()));
        if (StringUtils.isEmpty(aenId)) {
            assertEquals(result.getAenderungsId(), "DEU.MNET.TH99999999");
            verify(wbciCommonService).getNextPreAgreementId(eq(RequestTyp.TV));
        }
        else {
            assertEquals(result.getAenderungsId(), aenId);
            verify(wbciCommonService, times(0)).getNextPreAgreementId(eq(RequestTyp.TV));
        }
        verify(wbciDao).store(result);
        verify(wbciSchedulerService).scheduleRequest(result);
        verify(wbciValidationService).checkWbciMessageForErrors(CarrierCode.DTAG, result);
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testCreateTvWithoutTvTermin() throws Exception {
        TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageBuilder().withTvTermin(null).build();
        testling.createWbciTv(tv, "");
        Mockito.verify(wbciCommonService, never()).findWbciGeschaeftsfall(anyString());
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testCreateTvWithValidationException() throws Exception {
        TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageBuilder().build();
        Set<ConstraintViolation<TerminverschiebungsAnfrage>> constraintsMock = new HashSet<>();
        constraintsMock.add(Mockito.mock(ConstraintViolation.class));
        when(wbciValidationService.checkWbciMessageForErrors(any(CarrierCode.class), any(TerminverschiebungsAnfrage.class))).thenReturn(constraintsMock);
        when(constraintViolationHelper.generateErrorMsg(anySet())).thenReturn("VALIDATION ERROR");

        testCreateWbciTv(tv);
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testCreateTvWithExistingChangeRequest() throws Exception {
        TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageBuilder().build();
        doThrow(WbciServiceException.class).when(wbciValidationService).assertGeschaeftsfallHasNoActiveChangeRequests(any(String.class));
        testCreateWbciTv(tv);
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testCreateTvWithExistingStornoErlm() throws Exception {
        TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageBuilder().build();
        doThrow(WbciServiceException.class).when(wbciValidationService).assertGeschaeftsfallHasNoStornoErlm(any(String.class));
        testCreateWbciTv(tv);
    }

    @Test
    public void testProcessIncomingTvWithValidTv() {
        VorabstimmungsAnfrage va = new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        TerminverschiebungsAnfrage tv = buildTv(CarrierCode.MNET, CarrierCode.DTAG);
        tv.setVorabstimmungsIdRef(va.getVorabstimmungsId());
        tv.setWbciGeschaeftsfall(null);
        tv.setId(null);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDao.findWbciGeschaeftsfall(tv.getVorabstimmungsIdRef())).thenReturn(va.getWbciGeschaeftsfall());
        when(wbciCommonService.findLastForVaId(tv.getVorabstimmungsIdRef(), RueckmeldungVorabstimmung.class))
                .thenReturn(new RueckmeldungVorabstimmungBuilder().withProcessedAt(LocalDateTime.now()).build());

        assertNull(tv.getWbciGeschaeftsfall());
        testling.processIncomingTv(metadata, tv);
        assertNotNull(tv.getWbciGeschaeftsfall());
        assertEquals(tv.getWbciGeschaeftsfall(), va.getWbciGeschaeftsfall());
        assertEquals(tv.getWbciGeschaeftsfall().getKundenwunschtermin(), tv.getTvTermin());
        verify(wbciDao).findWbciGeschaeftsfall(va.getVorabstimmungsId());
        verify(wbciDao).store(tv);
        verify(wbciDeadlineService).updateAnswerDeadline(tv);
        verify(wbciGeschaeftsfallService).checkCbVorgangAndMarkForClarification(tv.getWbciGeschaeftsfall());
    }

    @Test
    public void testProcessIncomingTvMnetIsReceivingCarrier() {
        VorabstimmungsAnfrage va = new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        TerminverschiebungsAnfrage tv = buildTv(CarrierCode.MNET, CarrierCode.DTAG);
        tv.setVorabstimmungsIdRef(va.getVorabstimmungsId());
        tv.setWbciGeschaeftsfall(null);
        tv.setId(null);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDao.findWbciGeschaeftsfall(tv.getVorabstimmungsIdRef())).thenReturn(va.getWbciGeschaeftsfall());
        when(wbciValidationService.isMNetReceivingCarrier(tv)).thenReturn(true);

        testling.processIncomingTv(metadata, tv);

        assertNotNull(tv.getWbciGeschaeftsfall());
        assertEquals(tv.getWbciGeschaeftsfall(), va.getWbciGeschaeftsfall());
        assertNotEquals(tv.getWbciGeschaeftsfall().getKundenwunschtermin(), tv.getTvTermin());

        verify(wbciDao).findWbciGeschaeftsfall(va.getVorabstimmungsId());

        ArgumentCaptor<MessageProcessingMetadata> metadataArgument = ArgumentCaptor.forClass(MessageProcessingMetadata.class);
        ArgumentCaptor<AbbruchmeldungTerminverschiebung> abbmArgument = ArgumentCaptor.forClass(AbbruchmeldungTerminverschiebung.class);
        verify(wbciMeldungService).sendErrorResponse(metadataArgument.capture(), abbmArgument.capture());
        Assert.assertTrue(metadataArgument.getValue().isPostProcessMessage());
        Assert.assertFalse(metadataArgument.getValue().isResponseToDuplicateVaRequest());
        Assert.assertTrue(abbmArgument.getValue().containsMeldungsCodes(MeldungsCode.TV_ABG));
        Assert.assertTrue(metadata.isPostProcessMessage());

        verify(wbciDao).store(tv);
        verify(wbciDao).store(abbmArgument.getValue());
        verify(wbciDeadlineService, never()).updateAnswerDeadline(tv);
        verify(wbciGeschaeftsfallService, never()).checkCbVorgangAndMarkForClarification(any(WbciGeschaeftsfall.class));
    }

    @Test
    public void testProcessIncomingTvWithLowSeverityError() {
        VorabstimmungsAnfrage va = new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        TerminverschiebungsAnfrage tv = buildTv(CarrierCode.MNET, CarrierCode.DTAG);
        tv.setVorabstimmungsIdRef(va.getVorabstimmungsId());
        tv.setWbciGeschaeftsfall(null);
        tv.setId(null);

        Set<ConstraintViolation<TerminverschiebungsAnfrage>> constraintsMock = new HashSet<>();
        constraintsMock.add(Mockito.mock(ConstraintViolation.class));
        final String errorMessage = "VALIDATION ERROR";
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDao.findWbciGeschaeftsfall(va.getVorabstimmungsId()))
                .thenReturn(va.getWbciGeschaeftsfall());

        when(wbciValidationService.checkWbciMessageForErrors(any(CarrierCode.class), any(TerminverschiebungsAnfrage.class))).thenReturn(constraintsMock);
        when(wbciValidationService.hasConstraintViolation(constraintsMock, CheckTvTerminNotBroughtForward.class)).thenReturn(false);
        when(constraintViolationHelper.generateErrorMsgForInboundMsg(anySet())).thenReturn(errorMessage);
        when(wbciDao.findWbciGeschaeftsfall(va.getVorabstimmungsId()))
                .thenReturn(va.getWbciGeschaeftsfall());

        testling.processIncomingTv(metadata, tv);

        assertEquals(tv.getRequestStatus(), WbciRequestStatus.TV_EMPFANGEN);
        assertEquals(tv.getWbciGeschaeftsfall(), va.getWbciGeschaeftsfall());
        assertEquals(tv.getWbciGeschaeftsfall().getKundenwunschtermin(), tv.getTvTermin());
        verify(wbciDao).store(tv);
        verify(wbciDeadlineService).updateAnswerDeadline(tv);
        verify(wbciGeschaeftsfallService).markGfForClarification(va.getWbciGeschaeftsfall().getId(), errorMessage, null);
        verify(wbciGeschaeftsfallService).checkCbVorgangAndMarkForClarification(tv.getWbciGeschaeftsfall());
    }

    @Test
    public void testProcessIncomingTvWithHighSeverityTechnicalError() {
        VorabstimmungsAnfrage va = new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        TerminverschiebungsAnfrage tv = buildTv(CarrierCode.MNET, CarrierCode.DTAG);
        tv.setVorabstimmungsIdRef(va.getVorabstimmungsId());
        tv.setWbciGeschaeftsfall(null);
        tv.setId(null);

        Set<ConstraintViolation<TerminverschiebungsAnfrage>> validationErrorsMock = new HashSet<>();
        ConstraintViolation constraintViolation = Mockito.mock(ConstraintViolation.class);
        validationErrorsMock.add(constraintViolation);
        final String errorMessage = "VALIDATION ERROR";
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDao.findWbciGeschaeftsfall(va.getVorabstimmungsId()))
                .thenReturn(null);

        testling.processIncomingTv(metadata, tv);

        ArgumentCaptor<MessageProcessingMetadata> metadataArgument = ArgumentCaptor.forClass(MessageProcessingMetadata.class);
        ArgumentCaptor<AbbruchmeldungTerminverschiebung> abbmArgument = ArgumentCaptor.forClass(AbbruchmeldungTerminverschiebung.class);
        verify(wbciMeldungService).sendErrorResponse(metadataArgument.capture(), abbmArgument.capture());
        Assert.assertFalse(metadataArgument.getValue().isPostProcessMessage());
        Assert.assertFalse(metadataArgument.getValue().isResponseToDuplicateVaRequest());
        Assert.assertTrue(abbmArgument.getValue().containsMeldungsCodes(MeldungsCode.VAID_ABG));
        Assert.assertFalse(metadata.isPostProcessMessage());

        verify(wbciValidationService, never()).checkWbciMessageForErrors(any(CarrierCode.class), any(TerminverschiebungsAnfrage.class));
        verify(constraintViolationHelper, never()).generateErrorMsgForInboundMsg(anySet());
        verify(wbciDao, never()).store(tv);
        verify(wbciDao, never()).store(abbmArgument.getValue());
        verify(wbciDeadlineService, never()).updateAnswerDeadline(tv);
        verify(wbciGeschaeftsfallService, never()).checkCbVorgangAndMarkForClarification(any(WbciGeschaeftsfall.class));
    }

    @Test
    public void testProcessIncomingTvWithHighSeverityError() {
        VorabstimmungsAnfrage va = new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        TerminverschiebungsAnfrage tv = buildTv(CarrierCode.MNET, CarrierCode.DTAG);
        tv.setVorabstimmungsIdRef(va.getVorabstimmungsId());
        tv.setWbciGeschaeftsfall(null);
        tv.setId(null);

        Set<ConstraintViolation<TerminverschiebungsAnfrage>> validationErrorsMock = new HashSet<>();
        ConstraintViolation constraintViolation = Mockito.mock(ConstraintViolation.class);
        validationErrorsMock.add(constraintViolation);
        final String errorMessage = "VALIDATION ERROR";
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDao.findWbciGeschaeftsfall(va.getVorabstimmungsId()))
                .thenReturn(va.getWbciGeschaeftsfall());

        when(wbciValidationService.checkWbciMessageForErrors(any(CarrierCode.class), any(TerminverschiebungsAnfrage.class))).thenReturn(validationErrorsMock);
        when(wbciValidationService.hasConstraintViolation(validationErrorsMock, CheckTvTerminNotBroughtForward.class)).thenReturn(true);
        when(wbciValidationService.getConstraintViolation(validationErrorsMock, CheckTvTerminNotBroughtForward.class)).thenReturn(constraintViolation);
        when(constraintViolation.getMessage()).thenReturn(errorMessage);
        when(wbciDao.findWbciGeschaeftsfall(va.getVorabstimmungsId()))
                .thenReturn(va.getWbciGeschaeftsfall());

        testling.processIncomingTv(metadata, tv);

        ArgumentCaptor<MessageProcessingMetadata> metadataArgument = ArgumentCaptor.forClass(MessageProcessingMetadata.class);
        ArgumentCaptor<AbbruchmeldungTerminverschiebung> abbmArgument = ArgumentCaptor.forClass(AbbruchmeldungTerminverschiebung.class);
        verify(wbciMeldungService).sendErrorResponse(metadataArgument.capture(), abbmArgument.capture());
        Assert.assertTrue(metadataArgument.getValue().isPostProcessMessage());
        Assert.assertFalse(metadataArgument.getValue().isResponseToDuplicateVaRequest());
        Assert.assertTrue(abbmArgument.getValue().containsMeldungsCodes(MeldungsCode.TV_ABG));
        Assert.assertTrue(metadata.isPostProcessMessage());

        verify(constraintViolationHelper, never()).generateErrorMsgForInboundMsg(anySet());
        verify(wbciDao).store(tv);
        verify(wbciDao).store(abbmArgument.getValue());
        verify(wbciDeadlineService, never()).updateAnswerDeadline(tv);
        verify(wbciGeschaeftsfallService, never()).checkCbVorgangAndMarkForClarification(any(WbciGeschaeftsfall.class));
    }

    @Test
    public void testCleanseIncomingTv() {
        TerminverschiebungsAnfrage tv = buildTv(CarrierCode.MNET, CarrierCode.DTAG);
        tv.setEndkunde(new PersonTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN));

        testling.cleanseIncomingTv(tv);

        assertNull(tv.getEndkunde());
    }

    private TerminverschiebungsAnfrage buildTv(CarrierCode receivingCarrier, CarrierCode donatingCarrier) {
        return buildTv("DEU.MNET.T000000001", receivingCarrier, donatingCarrier);
    }

    private TerminverschiebungsAnfrage buildTv(String aenderungsId, CarrierCode receivingCarrier, CarrierCode donatingCarrier) {
        TerminverschiebungsAnfrage<WbciGeschaeftsfallKueMrn> tv =
                new TerminverschiebungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                        .withAenderungsId(aenderungsId)
                        .withWbciGeschaeftsfall(
                                (new WbciGeschaeftsfallKueMrnTestBuilder()
                                        .withAufnehmenderEKP(receivingCarrier)
                                        .withAbgebenderEKP(donatingCarrier))
                                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)
                        )
                        .withRequestStatus(WbciRequestStatus.TV_EMPFANGEN)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        tv.setVorabstimmungsIdRef(tv.getWbciGeschaeftsfall().getVorabstimmungsId());
        tv.setId(1L);
        return tv;
    }

}
