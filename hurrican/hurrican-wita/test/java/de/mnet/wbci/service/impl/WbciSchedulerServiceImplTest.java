/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.10.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;
import org.hibernate.LockMode;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciBaseException;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.service.WbciSchemaValidationService;
import de.mnet.wbci.service.WbciSendMessageService;
import de.mnet.wita.model.WitaSendLimit;
import de.mnet.wita.model.WitaSendLimitBuilder;
import de.mnet.wita.service.WitaConfigService;

/**
 *
 */
@Test(groups = UNIT)
public class WbciSchedulerServiceImplTest {

    @InjectMocks
    private WbciSchedulerServiceImpl testling;

    @Mock
    private WbciDao wbciDao;

    @Mock
    private WbciSendMessageService wbciSendMessageService;
    @Mock
    private WbciSchemaValidationService wbciSchemaValidationService;
    @Mock
    private WitaConfigService witaConfigService;

    @BeforeMethod
    public void setUp() {
        testling = new WbciSchedulerServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testScheduleRequestSendImmediately() {
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(witaConfigService.getWbciMinutesWhileRequestIsOnHold()).thenReturn(-1);
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = vorabstimmungsAnfrage.getWbciGeschaeftsfall();
        when(witaConfigService.findWitaSendLimit(wbciGeschaeftsfall.getTyp().name(),
                null, wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode())).thenReturn(null);
        testling.scheduleRequest(vorabstimmungsAnfrage);
        verify(witaConfigService).getWbciMinutesWhileRequestIsOnHold();
        verify(witaConfigService).findWitaSendLimit(wbciGeschaeftsfall.getTyp().name(),
                null, wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode());
        verify(wbciSendMessageService).sendAndProcessMessage(vorabstimmungsAnfrage);
        Assert.assertNotNull(vorabstimmungsAnfrage.getSendAfter());
        boolean isBeforeOrEqualNow = (DateConverterUtils.asLocalDateTime(vorabstimmungsAnfrage.getSendAfter())).isBefore(LocalDateTime.now())
                || DateConverterUtils.asLocalDateTime(vorabstimmungsAnfrage.getSendAfter()).isEqual(LocalDateTime.now());
        Assert.assertTrue("sendAfter Date is not in the past (or current timestamp)", isBeforeOrEqualNow);
    }

    @Test
    public void testScheduleRequestSendImmediatelySendlimitReached() {
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(witaConfigService.getWbciMinutesWhileRequestIsOnHold()).thenReturn(-1);
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = vorabstimmungsAnfrage.getWbciGeschaeftsfall();
        final WitaSendLimit witaSendLimit = new WitaSendLimitBuilder()
                .withGeschaeftsfallTyp(wbciGeschaeftsfall.getTyp().name())
                .withKollokationsTyp(null)
                .withItuCarrierCode(wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode())
                .withSendLimit(10L)
                .build();
        when(witaConfigService.findWitaSendLimit(wbciGeschaeftsfall.getTyp().name(),
                null, wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode())).thenReturn(witaSendLimit);
        when(witaConfigService.getWitaSentCount(wbciGeschaeftsfall.getTyp().name(),
                null, wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode())).thenReturn(10L);
        testling.scheduleRequest(vorabstimmungsAnfrage);
        verify(witaConfigService).getWbciMinutesWhileRequestIsOnHold();
        verify(witaConfigService).findWitaSendLimit(wbciGeschaeftsfall.getTyp().name(),
                null, wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode());
        verify(witaConfigService).getWitaSentCount(wbciGeschaeftsfall.getTyp().name(),
                null, wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode());
        verify(witaConfigService, never()).createSendLog(any(WbciRequest.class));
        verify(wbciSendMessageService, never()).sendAndProcessMessage(vorabstimmungsAnfrage);
        Assert.assertNotNull(vorabstimmungsAnfrage.getSendAfter());
        boolean isBeforeOrEqualNow = (DateConverterUtils.asLocalDateTime(vorabstimmungsAnfrage.getSendAfter()).isBefore(LocalDateTime.now())
                || DateConverterUtils.asLocalDateTime(vorabstimmungsAnfrage.getSendAfter()).isEqual(LocalDateTime.now()));
        Assert.assertTrue("sendAfter Date is not in the past (or current timestamp)", isBeforeOrEqualNow);
    }

    @Test
    public void testScheduleRequest() {
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(witaConfigService.getWbciMinutesWhileRequestIsOnHold()).thenReturn(1);
        when(witaConfigService.getWbciCdmVersion(vorabstimmungsAnfrage.getEKPPartner())).thenReturn(WbciCdmVersion.V1);
        testling.scheduleRequest(vorabstimmungsAnfrage);
        verify(witaConfigService).getWbciMinutesWhileRequestIsOnHold();
        verify(wbciDao).store(vorabstimmungsAnfrage);
        verify(wbciSendMessageService, times(0)).sendAndProcessMessage(vorabstimmungsAnfrage);
        verify(witaConfigService).getWbciCdmVersion(CarrierCode.DTAG);
        verify(wbciSchemaValidationService).validateWbciMessage(vorabstimmungsAnfrage, WbciCdmVersion.V1);
        Assert.assertNotNull(vorabstimmungsAnfrage.getSendAfter());
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testScheduleRequestSchemaException() {
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(witaConfigService.getWbciMinutesWhileRequestIsOnHold()).thenReturn(1);
        when(witaConfigService.getWbciCdmVersion(vorabstimmungsAnfrage.getEKPPartner())).thenReturn(WbciCdmVersion.V1);
        doThrow(WbciServiceException.class).when(wbciSchemaValidationService).validateWbciMessage(vorabstimmungsAnfrage, WbciCdmVersion.V1);

        testling.scheduleRequest(vorabstimmungsAnfrage);
        verify(witaConfigService).getWbciMinutesWhileRequestIsOnHold();
        verify(wbciDao).store(vorabstimmungsAnfrage);
        verify(wbciSendMessageService, times(0)).sendAndProcessMessage(vorabstimmungsAnfrage);
        verify(witaConfigService).getWbciCdmVersion(CarrierCode.DTAG);
        Assert.assertNotNull(vorabstimmungsAnfrage.getSendAfter());
    }

    @Test
    public void findScheduledWbciRequestIds() {
        List<Long> requestIds = Arrays.asList(1L, 2L);
        when(wbciDao.findScheduledWbciRequestIds()).thenReturn(requestIds);

        final List<Long> scheduledRequestIds = testling.findScheduledWbciRequestIds();

        verify(wbciDao).findScheduledWbciRequestIds();
        Assert.assertEquals(scheduledRequestIds, requestIds);
    }

    @Test
    public void sendScheduledRequest() {
        final VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                        .withSendAfter(LocalDateTime.now().minusMinutes(2))
                        .withoutProcessedAt()
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.byIdWithLockMode(vorabstimmungsAnfrage.getId(), LockMode.PESSIMISTIC_WRITE, WbciRequest.class)).thenReturn(vorabstimmungsAnfrage);
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = vorabstimmungsAnfrage.getWbciGeschaeftsfall();
        final WitaSendLimit witaSendLimit = new WitaSendLimitBuilder()
                .withGeschaeftsfallTyp(wbciGeschaeftsfall.getTyp().name())
                .withKollokationsTyp(null)
                .withItuCarrierCode(wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode())
                .withSendLimit(-1L)
                .build();
        when(witaConfigService.findWitaSendLimit(wbciGeschaeftsfall.getTyp().name(),
                null, wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode())).thenReturn(witaSendLimit);

        testling.sendScheduledRequest(vorabstimmungsAnfrage.getId());

        verify(witaConfigService).findWitaSendLimit(wbciGeschaeftsfall.getTyp().name(),
                null, wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode());
        verify(witaConfigService, never()).createSendLog(any(WbciRequest.class));
        verify(wbciSendMessageService).sendAndProcessMessage(vorabstimmungsAnfrage);
        Assert.assertNotNull(vorabstimmungsAnfrage.getSendAfter());
    }

    @Test
    public void sendScheduledRequestWithSendLimit() {
        final VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                        .withSendAfter(LocalDateTime.now().minusMinutes(2))
                        .withoutProcessedAt()
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.byIdWithLockMode(vorabstimmungsAnfrage.getId(), LockMode.PESSIMISTIC_WRITE, WbciRequest.class)).thenReturn(vorabstimmungsAnfrage);
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = vorabstimmungsAnfrage.getWbciGeschaeftsfall();
        final WitaSendLimit witaSendLimit = new WitaSendLimitBuilder()
                .withGeschaeftsfallTyp(wbciGeschaeftsfall.getTyp().name())
                .withKollokationsTyp(null)
                .withItuCarrierCode(wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode())
                .withSendLimit(1L)
                .build();
        when(witaConfigService.findWitaSendLimit(wbciGeschaeftsfall.getTyp().name(),
                null, wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode())).thenReturn(witaSendLimit);
        when(witaConfigService.getWitaSentCount(wbciGeschaeftsfall.getTyp().name(),
                null, wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode())).thenReturn(0L);

        testling.sendScheduledRequest(vorabstimmungsAnfrage.getId());

        verify(witaConfigService).findWitaSendLimit(wbciGeschaeftsfall.getTyp().name(),
                null, wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode());
        verify(witaConfigService).getWitaSentCount(wbciGeschaeftsfall.getTyp().name(),
                null, wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode());
        verify(witaConfigService).createSendLog(vorabstimmungsAnfrage);
        verify(wbciSendMessageService).sendAndProcessMessage(vorabstimmungsAnfrage);
        Assert.assertNotNull(vorabstimmungsAnfrage.getSendAfter());
    }

    @Test(expectedExceptions = WbciBaseException.class, expectedExceptionsMessageRegExp = "Wbci request can not be sent out, because it is already processed.*")
    public void sendRequestShouldFailAsRequestAlreadyProcessed() {
        final VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                        .withSendAfter(LocalDateTime.now().minusMinutes(2))
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.byIdWithLockMode(vorabstimmungsAnfrage.getId(), LockMode.PESSIMISTIC_WRITE, WbciRequest.class)).thenReturn(vorabstimmungsAnfrage);

        testling.sendScheduledRequest(vorabstimmungsAnfrage.getId());

        verify(wbciSendMessageService, never()).sendAndProcessMessage(vorabstimmungsAnfrage);
        verify(wbciDao, never()).store(vorabstimmungsAnfrage);
    }

    @Test(expectedExceptions = WbciBaseException.class, expectedExceptionsMessageRegExp = "Wbci request can not be sent out, because the Geschaeftsfall is already completed.*")
    public void sendRequestShouldFailAsGeschaefsfallIsAlreadyCompleted() {
        final VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                        .withSendAfter(LocalDateTime.now().minusMinutes(2))
                        .withoutProcessedAt()
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        vorabstimmungsAnfrage.getWbciGeschaeftsfall().setStatus(WbciGeschaeftsfallStatus.COMPLETE);

        when(wbciDao.byIdWithLockMode(vorabstimmungsAnfrage.getId(), LockMode.PESSIMISTIC_WRITE, WbciRequest.class)).thenReturn(vorabstimmungsAnfrage);

        testling.sendScheduledRequest(vorabstimmungsAnfrage.getId());

        verify(wbciSendMessageService, never()).sendAndProcessMessage(vorabstimmungsAnfrage);
        verify(wbciDao, never()).store(vorabstimmungsAnfrage);
    }

}
