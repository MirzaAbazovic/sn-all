/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.13
 */
package de.mnet.wbci.service.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;

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

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.BaseTest;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.PersonTestBuilder;
import de.mnet.wbci.model.builder.StandortTestBuilder;
import de.mnet.wbci.model.builder.StornoAenderungAbgAnfrageBuilder;
import de.mnet.wbci.model.builder.StornoAenderungAbgAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAufhebungAufAnfrageBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciSchedulerService;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wbci.validation.helper.ConstraintViolationHelper;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class WbciStornoServiceImplTest {

    @InjectMocks
    private WbciStornoServiceImpl testling;

    @Mock
    private WbciDao wbciDao;
    @Mock
    private WbciCommonService wbciCommonService;
    @Mock
    private WbciDeadlineServiceImpl wbciDeadlineService;
    @Mock
    private WbciSchedulerService wbciSchedulerService;
    @Mock
    private WbciMeldungService wbciMeldungService;
    @Mock
    private WbciValidationService wbciValidationService;
    @Mock
    private ConstraintViolationHelper constraintViolationHelper;
    @Mock
    protected WbciGeschaeftsfallService wbciGeschaeftsfallService;
    @Mock
    private WbciWitaServiceFacade wbciWitaServiceFacade;

    @BeforeMethod
    public void setUp() {
        testling = new WbciStornoServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateWbciStornoWithoutVorabstimmungsIdRef() {
        StornoAenderungAbgAnfrage<WbciGeschaeftsfall> stornoAnfrage = new StornoAenderungAbgAnfrageBuilder<>()
                .build();

        StornoAenderungAbgAnfrage<WbciGeschaeftsfall> result = testCreateWbciStorno(stornoAnfrage, createVorabstimmungsId());
        Assert.assertNull(result.getEndkunde());
        Assert.assertNull(result.getStandort());
    }

    @Test
    public void testCreateWbciStornoWithoutAenderungsId() {
        String vorabstimmungsId = createVorabstimmungsId();
        StornoAenderungAbgAnfrage<WbciGeschaeftsfall> stornoAnfrage = new StornoAenderungAbgAnfrageBuilder<>()
                .withVorabstimmungsIdRef(vorabstimmungsId)
                .build();

        StornoAenderungAbgAnfrage<WbciGeschaeftsfall> result = testCreateWbciStorno(stornoAnfrage, vorabstimmungsId);
        Assert.assertNull(result.getEndkunde());
        Assert.assertNull(result.getStandort());
    }

    @Test
    public void testCreateWbciStornoWithAenderungsId() {
        String aenderungsId = "DEU.MNET.SH99999999";
        String vorabstimmungsId = createVorabstimmungsId();
        StornoAufhebungAufAnfrage<WbciGeschaeftsfall> stornoAnfrage = new StornoAufhebungAufAnfrageBuilder<>()
                .withVorabstimmungsIdRef(vorabstimmungsId)
                .withAenderungsId(aenderungsId)
                .build();

        testCreateWbciStorno(stornoAnfrage, vorabstimmungsId);
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testCreateStornoWithValidationException() throws Exception {
        StornoAnfrage storno = new StornoAenderungAbgAnfrageBuilder<>().build();
        Set<ConstraintViolation<StornoAnfrage>> constraintsMock = new HashSet<>();
        constraintsMock.add(Mockito.mock(ConstraintViolation.class));
        when(wbciValidationService.checkWbciMessageForErrors(any(CarrierCode.class), any(StornoAnfrage.class))).thenReturn(constraintsMock);
        when(constraintViolationHelper.generateErrorMsg(anySet())).thenReturn("VALIDATION ERROR");

        testCreateWbciStorno(storno, "DEU.MNET.SH99999999");
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testCreateStornoWithExistingChangeRequest() throws Exception {
        StornoAnfrage storno = new StornoAenderungAbgAnfrageBuilder<>().build();
        doThrow(WbciServiceException.class).when(wbciValidationService).assertGeschaeftsfallHasNoActiveChangeRequests(any(String.class));
        testCreateWbciStorno(storno, "DEU.MNET.SH99999999");
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testCreateStornoWithExistingStornoErlm() throws Exception {
        StornoAnfrage storno = new StornoAenderungAbgAnfrageBuilder<>().build();
        doThrow(WbciServiceException.class).when(wbciValidationService).assertGeschaeftsfallHasNoStornoErlm(any(String.class));
        testCreateWbciStorno(storno, "DEU.MNET.SH99999999");
    }

    private <T extends StornoAnfrage> T testCreateWbciStorno(T stornoAnfrage, String vorabstimmungsId) {
        String aenId = stornoAnfrage.getAenderungsId();
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withVorabstimmungsId(vorabstimmungsId)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.findWbciGeschaeftsfall(eq(wbciGeschaeftsfallKueMrn.getVorabstimmungsId())))
                .thenReturn(wbciGeschaeftsfallKueMrn);

        if (StringUtils.isEmpty(aenId)) {
            when(wbciCommonService.getNextPreAgreementId(stornoAnfrage.getTyp())).thenReturn("DEU.MNET.SH99999999");
        }

        // reset any data that should be overridden
        stornoAnfrage.setCreationDate(null);
        stornoAnfrage.setUpdatedAt(null);
        stornoAnfrage.setIoType(null);

        StornoAnfrage result = testling.createWbciStorno(stornoAnfrage, wbciGeschaeftsfallKueMrn.getVorabstimmungsId());

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getCreationDate());
        Assert.assertNotNull(result.getUpdatedAt());
        Assert.assertEquals(result.getIoType(), IOType.OUT);
        Assert.assertEquals(result.getRequestStatus(), WbciRequestStatus.STORNO_VORGEHALTEN);
        Assert.assertEquals(result.getVorabstimmungsIdRef(), vorabstimmungsId);
        Assert.assertNotNull(result.getWbciGeschaeftsfall());
        Assert.assertEquals(result.getWbciGeschaeftsfall(), wbciGeschaeftsfallKueMrn);
        verify(wbciCommonService, times(1)).findWbciGeschaeftsfall(eq(wbciGeschaeftsfallKueMrn.getVorabstimmungsId()));
        if (StringUtils.isEmpty(aenId)) {
            Assert.assertEquals(result.getAenderungsId(), "DEU.MNET.SH99999999");
            verify(wbciCommonService).getNextPreAgreementId(eq(stornoAnfrage.getTyp()));
        }
        else {
            Assert.assertEquals(result.getAenderungsId(), aenId);
            verify(wbciCommonService, times(0)).getNextPreAgreementId(eq(stornoAnfrage.getTyp()));
        }
        verify(wbciDao).store(result);
        verify(wbciSchedulerService).scheduleRequest(result);
        verify(wbciValidationService).checkWbciMessageForErrors(CarrierCode.DTAG, result);
        return (T) result;
    }

    @Test
    public void testProcessIncomingStornoWithValidStorno() {
        final VorabstimmungsAnfrage<WbciGeschaeftsfall> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        StornoAnfrage stornoAnfrage = ((StornoAenderungAbgAnfrageTestBuilder) new StornoAenderungAbgAnfrageTestBuilder<>()
                .withVorabstimmungsIdRef(vorabstimmungsAnfrage.getWbciGeschaeftsfall().getVorabstimmungsId()))
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        stornoAnfrage.setWbciGeschaeftsfall(vorabstimmungsAnfrage.getWbciGeschaeftsfall());
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        Mockito.when(wbciDao.findWbciGeschaeftsfall(vorabstimmungsAnfrage.getVorabstimmungsId()))
                .thenReturn(vorabstimmungsAnfrage.getWbciGeschaeftsfall());
        testling.processIncomingStorno(metadata, stornoAnfrage);
        Assert.assertEquals(stornoAnfrage.getRequestStatus(), WbciRequestStatus.STORNO_EMPFANGEN);
        Assert.assertEquals(stornoAnfrage.getWbciGeschaeftsfall(), vorabstimmungsAnfrage.getWbciGeschaeftsfall());
        Assert.assertEquals(stornoAnfrage.getWbciGeschaeftsfall().getKlaerfall(), Boolean.FALSE);

        Mockito.verify(wbciDao).store(stornoAnfrage);
        Mockito.verify(wbciDeadlineService).updateAnswerDeadline(stornoAnfrage);
        verify(wbciGeschaeftsfallService, never()).markGfForClarification(anyLong(), anyString(), any(AKUser.class));
        verify(wbciGeschaeftsfallService, never()).checkCbVorgangAndMarkForClarification(stornoAnfrage.getWbciGeschaeftsfall());
    }

    @Test
    public void testProcessIncomingStornoWithIsDuplicate() {
        final VorabstimmungsAnfrage<WbciGeschaeftsfall> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        StornoAnfrage stornoAnfrage = ((StornoAenderungAbgAnfrageTestBuilder) new StornoAenderungAbgAnfrageTestBuilder<>()
                .withVorabstimmungsIdRef(vorabstimmungsAnfrage.getWbciGeschaeftsfall().getVorabstimmungsId()))
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        stornoAnfrage.setWbciGeschaeftsfall(vorabstimmungsAnfrage.getWbciGeschaeftsfall());
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        Mockito.when(wbciDao.findWbciGeschaeftsfall(vorabstimmungsAnfrage.getVorabstimmungsId()))
                .thenReturn(vorabstimmungsAnfrage.getWbciGeschaeftsfall());

        when(wbciValidationService.isDuplicateStornoRequest(stornoAnfrage)).thenReturn(true);

        testling.processIncomingStorno(metadata, stornoAnfrage);

        Mockito.verify(wbciDao).findWbciGeschaeftsfall(stornoAnfrage.getVorabstimmungsId());

        ArgumentCaptor<MessageProcessingMetadata> metadataArgument = ArgumentCaptor.forClass(MessageProcessingMetadata.class);
        ArgumentCaptor<Abbruchmeldung> abbmArgument = ArgumentCaptor.forClass(Abbruchmeldung.class);
        verify(wbciMeldungService).sendErrorResponse(metadataArgument.capture(), abbmArgument.capture());
        Assert.assertFalse(metadataArgument.getValue().isPostProcessMessage());
        Assert.assertFalse(metadataArgument.getValue().isResponseToDuplicateVaRequest());

        Mockito.verify(wbciDao, never()).store(stornoAnfrage);
        Mockito.verify(wbciDao, never()).store(abbmArgument.getValue());
        Mockito.verify(wbciDeadlineService, never()).updateAnswerDeadline(stornoAnfrage);
        verify(wbciGeschaeftsfallService, never()).checkCbVorgangAndMarkForClarification(any(WbciGeschaeftsfall.class));
    }

    @Test
    public void testProcessIncomingStornoWithHighSeverityTechnicalError() {
        final VorabstimmungsAnfrage<WbciGeschaeftsfall> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        StornoAnfrage stornoAnfrage = ((StornoAenderungAbgAnfrageTestBuilder) new StornoAenderungAbgAnfrageTestBuilder<>()
                .withVorabstimmungsIdRef(vorabstimmungsAnfrage.getWbciGeschaeftsfall().getVorabstimmungsId()))
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        stornoAnfrage.setWbciGeschaeftsfall(vorabstimmungsAnfrage.getWbciGeschaeftsfall());
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        Mockito.when(wbciDao.findWbciGeschaeftsfall(vorabstimmungsAnfrage.getVorabstimmungsId()))
                .thenReturn(null);

        testling.processIncomingStorno(metadata, stornoAnfrage);

        Mockito.verify(wbciDao).findWbciGeschaeftsfall(stornoAnfrage.getVorabstimmungsId());

        ArgumentCaptor<MessageProcessingMetadata> metadataArgument = ArgumentCaptor.forClass(MessageProcessingMetadata.class);
        ArgumentCaptor<Abbruchmeldung> abbmArgument = ArgumentCaptor.forClass(Abbruchmeldung.class);
        verify(wbciMeldungService).sendErrorResponse(metadataArgument.capture(), abbmArgument.capture());
        Assert.assertFalse(metadataArgument.getValue().isPostProcessMessage());
        Assert.assertFalse(metadataArgument.getValue().isResponseToDuplicateVaRequest());
        Assert.assertTrue(abbmArgument.getValue().containsMeldungsCodes(MeldungsCode.VAID_ABG));
        Assert.assertFalse(metadata.isPostProcessMessage());

        Mockito.verify(wbciDao, never()).store(stornoAnfrage);
        Mockito.verify(wbciDao, never()).store(abbmArgument.getValue());
        Mockito.verify(wbciDeadlineService, never()).updateAnswerDeadline(stornoAnfrage);
        verify(wbciGeschaeftsfallService, never()).checkCbVorgangAndMarkForClarification(any(WbciGeschaeftsfall.class));
    }

    @Test
    public void testProcessIncomingStornoWithHighSeverityError() {
        final VorabstimmungsAnfrage<WbciGeschaeftsfall> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        StornoAnfrage stornoAnfrage = ((StornoAenderungAbgAnfrageTestBuilder) new StornoAenderungAbgAnfrageTestBuilder<>()
                .withVorabstimmungsIdRef(vorabstimmungsAnfrage.getWbciGeschaeftsfall().getVorabstimmungsId()))
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        stornoAnfrage.setWbciGeschaeftsfall(vorabstimmungsAnfrage.getWbciGeschaeftsfall());
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        Mockito.when(wbciDao.findWbciGeschaeftsfall(vorabstimmungsAnfrage.getVorabstimmungsId()))
                .thenReturn(vorabstimmungsAnfrage.getWbciGeschaeftsfall());

        when(wbciValidationService.isTooEarlyForReceivingStornoRequest(stornoAnfrage)).thenReturn(true);

        testling.processIncomingStorno(metadata, stornoAnfrage);

        Mockito.verify(wbciDao).findWbciGeschaeftsfall(stornoAnfrage.getVorabstimmungsId());

        ArgumentCaptor<MessageProcessingMetadata> metadataArgument = ArgumentCaptor.forClass(MessageProcessingMetadata.class);
        ArgumentCaptor<Abbruchmeldung> abbmArgument = ArgumentCaptor.forClass(Abbruchmeldung.class);
        verify(wbciMeldungService).sendErrorResponse(metadataArgument.capture(), abbmArgument.capture());
        Assert.assertTrue(metadataArgument.getValue().isPostProcessMessage());
        Assert.assertFalse(metadataArgument.getValue().isResponseToDuplicateVaRequest());
        Assert.assertTrue(abbmArgument.getValue().containsMeldungsCodes(MeldungsCode.STORNO_ABG));
        Assert.assertTrue(metadata.isPostProcessMessage());

        Mockito.verify(wbciDao).store(stornoAnfrage);
        Mockito.verify(wbciDao).store(abbmArgument.getValue());
        Mockito.verify(wbciDeadlineService, never()).updateAnswerDeadline(stornoAnfrage);
        verify(wbciGeschaeftsfallService, never()).checkCbVorgangAndMarkForClarification(any(WbciGeschaeftsfall.class));
    }

    @Test
    public void testProcessIncomingStornoWithValidationError() {
        final VorabstimmungsAnfrage<WbciGeschaeftsfall> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        StornoAnfrage stornoAnfrage = ((StornoAenderungAbgAnfrageTestBuilder) new StornoAenderungAbgAnfrageTestBuilder<>()
                .withVorabstimmungsIdRef(vorabstimmungsAnfrage.getWbciGeschaeftsfall().getVorabstimmungsId()))
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        stornoAnfrage.setWbciGeschaeftsfall(vorabstimmungsAnfrage.getWbciGeschaeftsfall());
        Set<ConstraintViolation<StornoAnfrage>> constraintsMock = new HashSet<>();
        constraintsMock.add(Mockito.mock(ConstraintViolation.class));
        final String errorMessage = "VALIDATION ERROR";
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDao.findWbciGeschaeftsfall(vorabstimmungsAnfrage.getVorabstimmungsId()))
                .thenReturn(vorabstimmungsAnfrage.getWbciGeschaeftsfall());

        when(wbciValidationService.checkWbciMessageForErrors(any(CarrierCode.class), any(StornoAnfrage.class))).thenReturn(constraintsMock);
        when(constraintViolationHelper.generateErrorMsgForInboundMsg(anySet())).thenReturn(errorMessage);
        when(wbciDao.findWbciGeschaeftsfall(vorabstimmungsAnfrage.getVorabstimmungsId()))
                .thenReturn(vorabstimmungsAnfrage.getWbciGeschaeftsfall());

        testling.processIncomingStorno(metadata, stornoAnfrage);

        Assert.assertEquals(stornoAnfrage.getRequestStatus(), WbciRequestStatus.STORNO_EMPFANGEN);
        Assert.assertEquals(stornoAnfrage.getWbciGeschaeftsfall(), vorabstimmungsAnfrage.getWbciGeschaeftsfall());
        verify(wbciDao).store(stornoAnfrage);
        verify(wbciDeadlineService).updateAnswerDeadline(stornoAnfrage);
        verify(wbciGeschaeftsfallService).markGfForClarification(vorabstimmungsAnfrage.getWbciGeschaeftsfall().getId(), errorMessage, null);
        verify(wbciGeschaeftsfallService, never()).checkCbVorgangAndMarkForClarification(stornoAnfrage.getWbciGeschaeftsfall());
    }

    @Test
    public void testCleanseIncomingStorno() {
        final VorabstimmungsAnfrage<WbciGeschaeftsfall> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        StornoAenderungAbgAnfrage stornoAnfrage = ((StornoAenderungAbgAnfrageTestBuilder) new StornoAenderungAbgAnfrageTestBuilder<>()
                .withVorabstimmungsIdRef(vorabstimmungsAnfrage.getWbciGeschaeftsfall().getVorabstimmungsId()))
                .withEndkunde(new PersonTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                .withStandort(new StandortTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        stornoAnfrage.setWbciGeschaeftsfall(vorabstimmungsAnfrage.getWbciGeschaeftsfall());

        testling.cleanseIncommingStorno(stornoAnfrage);

        Assert.assertNull(stornoAnfrage.getEndkunde());
        Assert.assertNull(stornoAnfrage.getStandort());
    }

    private String createVorabstimmungsId() {
        return "DEU.MNET.VH" + String.valueOf(System.currentTimeMillis()).substring(String.valueOf(System.currentTimeMillis()).length() - 8);
    }

}
