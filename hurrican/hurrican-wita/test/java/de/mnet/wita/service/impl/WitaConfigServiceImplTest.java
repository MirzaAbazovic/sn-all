/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.2011 08:51:15
 */
package de.mnet.wita.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static de.mnet.wita.message.auftrag.Kundenwunschtermin.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.service.cc.HVTService;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.dao.WitaConfigDao;
import de.mnet.wita.exceptions.WitaConfigException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.MnetWitaRequestBuilder;
import de.mnet.wita.message.builder.StornoBuilder;
import de.mnet.wita.message.builder.TerminVerschiebungBuilder;
import de.mnet.wita.model.KollokationsTyp;
import de.mnet.wita.model.SendAllowed;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaConfig;
import de.mnet.wita.model.WitaSendCount;
import de.mnet.wita.model.WitaSendLimit;
import de.mnet.wita.model.WitaSendLimitBuilder;
import de.mnet.wita.service.WitaConfigService;


@Test(groups = UNIT)
public class WitaConfigServiceImplTest extends BaseTest {

    @InjectMocks
    @Spy
    private WitaConfigServiceImpl cut;

    @Mock
    private WitaConfigDao witaConfigDao;
    @Mock
    private HVTService hvtService;

    @BeforeMethod
    public void setUp() {
        cut = new WitaConfigServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] checkSendLimit() {
        // @formatter:off
        return new Object[][] {
                { new WitaSendLimitBuilder().withGeschaeftsfallTyp(BEREITSTELLUNG.name()).withSendLimit(WitaSendLimit.INFINITE_LIMIT).setPersist(false).build(), 10L, true },
                { new WitaSendLimitBuilder().withGeschaeftsfallTyp(BEREITSTELLUNG.name()).withSendLimit(10L).setPersist(false).build(), 9L, true },
                { new WitaSendLimitBuilder().withGeschaeftsfallTyp(BEREITSTELLUNG.name()).withSendLimit(10L).setPersist(false).build(), 10L, false },
                { new WitaSendLimitBuilder().withGeschaeftsfallTyp(BEREITSTELLUNG.name()).withSendLimit(10L).setPersist(false).build(), 11L, false },
                { new WitaSendLimitBuilder().withGeschaeftsfallTyp(BEREITSTELLUNG.name()).withSendLimit(10L).setPersist(false).build(), null, true },
                { null, null, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "checkSendLimit")
    public void checkSendLimit(WitaSendLimit limit, Long existingCount, boolean expectedResult) {
        String gfTyp = (limit != null) ? limit.getGeschaeftsfallTyp() : null;
        doReturn(limit).when(cut).findWitaSendLimit(gfTyp, KollokationsTyp.HVT, null);
        doReturn(existingCount).when(witaConfigDao).getWitaSentCount(gfTyp, KollokationsTyp.HVT, null);

        boolean result = cut.checkSendLimit(new AuftragBuilder((gfTyp == null) ? BEREITSTELLUNG : GeschaeftsfallTyp.valueOf(gfTyp)).buildValid());

        assertEquals(result, expectedResult);
    }

    @Test
    public void testFindSendLimit() throws Exception {
        Long hvtStdId = 100L;
        HVTStandort hvtStandort = mock(HVTStandort.class);
        when(hvtService.findHVTStandort(hvtStdId)).thenReturn(hvtStandort);
        when(hvtStandort.getStandortTypRefId()).thenReturn(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ);

        cut.findWitaSendLimit(BEREITSTELLUNG, hvtStdId);
        verify(cut).findWitaSendLimit(BEREITSTELLUNG_NAME, KollokationsTyp.FTTC_KVZ, null);

    }

    @DataProvider
    public Object[][] checkRequestDelay() {
        // @formatter:off
        return new Object[][] {
                { "0",      null, true },
                { "0",    kwt(0), true },
                { "0",    kwt(1), true },
                { "0",   kwt(10), true },
                { "0",  kwt(100), true },
                { null, kwt(WitaConfigService.DEFAULT_COUNT_DAYS_BEFORE_SENT - 1),  true },
                { null, kwt(WitaConfigService.DEFAULT_COUNT_DAYS_BEFORE_SENT + 1), false },

                { "100",     null,  true },
                { "100",   kwt(0),  true },
                { "100",  kwt(50),  true },
                { "100",  kwt(99),  true },
                { "100", kwt(100), false },
                { "100", kwt(101), false },

                { null,     null,  true },
                { null,   kwt(1),  true },
                { null,  kwt(99),  true },
                { null, kwt(101),  true },
                { null, kwt(WitaConfigService.DEFAULT_COUNT_DAYS_BEFORE_SENT - 1),  true },
                { null, kwt(WitaConfigService.DEFAULT_COUNT_DAYS_BEFORE_SENT + 1), false },

                { "a1",     null, true },
                { "a2",   kwt(1), true },
                { "a3",  kwt(50), true },
                { "a2", kwt(100), true },
                { "a4", kwt(WitaConfigService.DEFAULT_COUNT_DAYS_BEFORE_SENT - 1), true },
                { "a5", kwt(WitaConfigService.DEFAULT_COUNT_DAYS_BEFORE_SENT + 1), false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "checkRequestDelay")
    public void checkRequestDelay(String daysBeforeSentDefault, Kundenwunschtermin kwt, boolean expectedResult) {
        when(witaConfigDao.getWitaConfigValue(WitaConfig.DAYS_BEFORE_SENT)).thenReturn(daysBeforeSentDefault);
        assertEquals(cut.checkRequestDelay(BEREITSTELLUNG, kwt), expectedResult);
    }

    @DataProvider
    public Object[][] isSendLogNecessary() {
        // @formatter:off
        return new Object[][] {
                { new WitaSendLimitBuilder().withSendLimit(WitaSendLimit.INFINITE_LIMIT).setPersist(false).build(), false },
                { new WitaSendLimitBuilder().withSendLimit(10L).setPersist(false).build(), true },
                { null, false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "isSendLogNecessary")
    public void isSendLogNecessary(WitaSendLimit limit, boolean expectedResult) {
        boolean result = cut.isSendLogNecessary(limit);
        assertEquals(result, expectedResult);
    }

    @DataProvider
    public Object[][] createSendLog() {
        // @formatter:off
        return new Object[][] {
                { new WitaSendLimitBuilder().withSendLimit(WitaSendLimit.INFINITE_LIMIT).setPersist(false).build(), 0 },
                { new WitaSendLimitBuilder().withSendLimit(10L).setPersist(false).build(), 1 },
                { null, 0 },
        };
        // @formatter:on
    }

    @Test(dataProvider = "createSendLog")
    public void createSendLog(WitaSendLimit limit, int expectedTimes) {
        doReturn(limit).when(cut).findWitaSendLimit(any(String.class), any(KollokationsTyp.class), (String) Matchers.isNull());
        cut.createSendLog(new AuftragBuilder(BEREITSTELLUNG).buildValid(), LocalDateTime.now());
        verify(witaConfigDao, times(expectedTimes)).store(any(WitaSendCount.class));
    }

    private Kundenwunschtermin createKundenWunschtermin(LocalDate kwtDate) {
        Kundenwunschtermin kwt = new Kundenwunschtermin();
        kwt.setDatum(kwtDate);
        return kwt;
    }

    /**
     * passive modus must not be check in {@link WitaConfigService} because otherwise the request would not be delivered
     * to activemq while facade is in passive mode
     */
    public void sendAllowedPassiveModeMustNotBeCheckedHere() {
        SendAllowed sendAllowed = cut.checkSendAllowed(getAuftragWithRequestOnHold(false));

        assertThat(sendAllowed, equalTo(SendAllowed.OK));
    }

    public void sendAllowedSendLimit() {
        Auftrag auftrag = getAuftragWithSendLimit(true);
        SendAllowed sendAllowed = cut.checkSendAllowed(auftrag);
        assertThat(sendAllowed, equalTo(SendAllowed.SENDE_LIMIT));
        assertThat(cut.isSendAllowed(auftrag), equalTo(false));
    }

    /**
     * Requests werden erst nach einem bestimmten Offset in Minuten rausgesendet - hier wird noch gewartet
     */
    public void sendNotAllowedWhileRequestOnHold() {
        Auftrag auftrag = getAuftragWithRequestOnHold(true);

        SendAllowed sendAllowed = cut.checkSendAllowed(auftrag);

        assertThat(sendAllowed, equalTo(SendAllowed.REQUEST_VORGEHALTEN));
        assertThat(cut.isSendAllowed(auftrag), equalTo(false));
    }

    /**
     * Requests werden erst nach dem fruehsten möglichen Versand rausgesendet - hier wird noch gewartet
     */
    public void sendNotAllowedBeforeEarliestSendDate() {
        Auftrag auftrag = getAuftragWithEarliestSendDate(LocalDateTime.now().plusMinutes(1));

        SendAllowed sendAllowed = cut.checkSendAllowed(auftrag);

        assertThat(sendAllowed, equalTo(SendAllowed.EARLIEST_SEND_DATE_IN_FUTURE));
        assertThat(cut.isSendAllowed(auftrag), equalTo(false));
    }

    /**
     * Requests werden erst nach einem bestimmten Offset in Minuten rausgesendet - hier kann gesendet werden
     */
    public void sendAllowedAfterRequestOnHold() {
        Auftrag auftrag = getAuftragWithRequestOnHold(false);
        SendAllowed sendAllowed = cut.checkSendAllowed(auftrag);
        assertThat(sendAllowed, equalTo(SendAllowed.OK));
        assertThat(cut.isSendAllowed(auftrag), equalTo(true));
    }

    /**
     * Requests werden erst nach einem bestimmten Offset in Minuten rausgesendet - hier kann gesendet werden
     */
    public void sendAllowedAfterEarliestSendDate() {
        Auftrag auftrag = getAuftragWithEarliestSendDate(LocalDateTime.now().minusMinutes(1));
        SendAllowed sendAllowed = cut.checkSendAllowed(auftrag);
        assertThat(sendAllowed, equalTo(SendAllowed.OK));
        assertThat(cut.isSendAllowed(auftrag), equalTo(true));
    }

    @DataProvider
    public Object[][] stornoAndTv() {
        // @formatter:off
        return new Object[][] {
                { new TerminVerschiebungBuilder(BEREITSTELLUNG).withExterneAuftragsnummer("1234556")},
                { new StornoBuilder(BEREITSTELLUNG)},
            };
        // @formatter:on
    }

    @Test(dataProvider = "stornoAndTv")
    public <T extends MnetWitaRequest> void checkSendAllowedForStornoAndTv(MnetWitaRequestBuilder<T> builder) {
        T request = builder.buildValid();
        doReturn(true).when(cut).checkSendLimit(request);
        doReturn(true).when(cut).checkRequestDelay(any(GeschaeftsfallTyp.class), any(Kundenwunschtermin.class));
        when(witaConfigDao.getWitaConfigValue(WitaConfig.MINUTES_WHILE_REQUESTS_ON_HOLD)).thenReturn("3");
        SendAllowed sendAllowed = cut.checkSendAllowed(request);
        assertThat(sendAllowed, equalTo(SendAllowed.REQUEST_VORGEHALTEN));
        assertThat(cut.isSendAllowed(request), equalTo(false));
    }

    @Test(dataProvider = "stornoAndTv")
    public <T extends MnetWitaRequest> void checkSendLimitDoesNotCountForStornoAndTv(MnetWitaRequestBuilder<T> builder) {
        T request = builder.buildValid();

        doReturn(false).when(cut).checkSendLimit(request);
        doReturn(true).when(cut).checkRequestDelay(any(GeschaeftsfallTyp.class), any(Kundenwunschtermin.class));
        doReturn("0").when(witaConfigDao).getWitaConfigValue(WitaConfig.MINUTES_WHILE_REQUESTS_ON_HOLD);

        assertThat(cut.checkSendAllowed(request), equalTo(SendAllowed.OK));
        assertThat(cut.isSendAllowed(request), equalTo(true));
    }

    @Test(dataProvider = "stornoAndTv")
    public <T extends MnetWitaRequest> void checkRequestDelayDoesNotCountForStornoAndTv(
            MnetWitaRequestBuilder<T> builder) {
        T request = builder.buildValid();

        doReturn(true).when(cut).checkSendLimit(request);
        doReturn(false).when(cut).checkRequestDelay(any(GeschaeftsfallTyp.class), any(Kundenwunschtermin.class));
        doReturn("0").when(witaConfigDao).getWitaConfigValue(WitaConfig.MINUTES_WHILE_REQUESTS_ON_HOLD);

        assertThat(cut.checkSendAllowed(request), equalTo(SendAllowed.OK));
        assertThat(cut.isSendAllowed(request), equalTo(true));
    }

    public void sendAllowedKwt() {
        Auftrag auftrag = getAuftragWithSendLimit(false);
        doReturn(false).when(cut).checkRequestDelay(any(GeschaeftsfallTyp.class), any(Kundenwunschtermin.class));

        SendAllowed sendAllowed = cut.checkSendAllowed(auftrag);

        assertThat(sendAllowed, equalTo(SendAllowed.KWT_IN_ZUKUNFT));
        assertThat(cut.isSendAllowed(auftrag), equalTo(false));
    }

    public void sendAllowedOk() {
        Auftrag auftrag = getAuftragWithSendLimit(false);
        doReturn(true).when(cut).checkRequestDelay(any(GeschaeftsfallTyp.class), any(Kundenwunschtermin.class));
        doReturn(true).when(cut).checkRequestOnHold(any(Auftrag.class));
        SendAllowed sendAllowed = cut.checkSendAllowed(auftrag);

        assertThat(sendAllowed, equalTo(SendAllowed.OK));
        assertThat(cut.isSendAllowed(auftrag), equalTo(true));
    }

    @DataProvider
    public Object[][] dataProviderCountOfDaysBeforeSentEasy() {
        // @formatter:off
        return new Object[][] {
                { null,   WitaConfigService.DEFAULT_COUNT_DAYS_BEFORE_SENT },
                { "bla",  WitaConfigService.DEFAULT_COUNT_DAYS_BEFORE_SENT },
                { "-1",   WitaConfigService.DEFAULT_COUNT_DAYS_BEFORE_SENT },
                { "0",    WitaConfigService.DEFAULT_COUNT_DAYS_BEFORE_SENT },
                { "10",   10 },
                { "30",   30 },
                { "100", 100 },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderCountOfDaysBeforeSentEasy")
    public void testCountOfDaysBeforeSentEasy(String daysBeforeSent, int expectedResult) {
        when(witaConfigDao.getWitaConfigValue(WitaConfig.DAYS_BEFORE_SENT)).thenReturn(daysBeforeSent);
        assertThat(cut.getCountOfDaysBeforeSent(BEREITSTELLUNG), equalTo(expectedResult));
    }

    @DataProvider
    public Object[][] dataProviderCountOfDaysBeforeSentByGeschaeftsfall() {
        // @formatter:off
        return new Object[][] {
                { BEREITSTELLUNG,                        60 },
                { KUENDIGUNG_KUNDE,                      10 },

                { LEISTUNGS_AENDERUNG,                   60 },
                { LEISTUNGSMERKMAL_AENDERUNG,            60 },
                { PORTWECHSEL,                           60 },

                { PROVIDERWECHSEL,                       60 },
                { VERBUNDLEISTUNG,                       60 },
                { RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, 120 },

                { BESTANDSUEBERSICHT,                    60 },
                { PRODUKTGRUPPENWECHSEL,                 60 },

                { KUENDIGUNG_TELEKOM,                    60 },
                { UNBEKANNT,                             60 },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderCountOfDaysBeforeSentByGeschaeftsfall")
    public void testCountOfDaysBeforeSentByGeschaeftsfall(GeschaeftsfallTyp geschaeftsfallTyp, int expectedResult) {
        when(witaConfigDao.getWitaConfigValue(WitaConfig.daysBeforeSentFor(KUENDIGUNG_KUNDE))).thenReturn("10");
        when(witaConfigDao.getWitaConfigValue(WitaConfig.daysBeforeSentFor(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG)))
                .thenReturn("120");
        when(witaConfigDao.getWitaConfigValue(WitaConfig.DAYS_BEFORE_SENT)).thenReturn("60");

        assertThat(cut.getCountOfDaysBeforeSent(geschaeftsfallTyp), equalTo(expectedResult));
    }

    private Auftrag getAuftragWithSendLimit(boolean limit) {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).withExterneAuftragsnummer("1234556").buildValid();
        doReturn(!limit).when(cut).checkSendLimit(auftrag);
        return auftrag;
    }

    private Auftrag getAuftragWithRequestOnHold(boolean onHold) {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).withExterneAuftragsnummer("1234556").buildValid();
        when(witaConfigDao.getWitaConfigValue(WitaConfig.MINUTES_WHILE_REQUESTS_ON_HOLD)).thenReturn("3");
        if (onHold) {
            auftrag.setMwfCreationDate(new Date());
        }
        else {
            auftrag.setMwfCreationDate(Date.from(ZonedDateTime.now().minusMinutes(4).toInstant()));
        }
        return auftrag;
    }

    private Auftrag getAuftragWithEarliestSendDate(LocalDateTime earliestSendDate) {
        when(witaConfigDao.getWitaConfigValue(WitaConfig.MINUTES_WHILE_REQUESTS_ON_HOLD)).thenReturn("3");

        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG)
                .withExterneAuftragsnummer("1234556")
                .withEarliestSendDate(earliestSendDate)
                .withMwfCreationDate(LocalDateTime.now().minusMinutes(4))
                .buildValid();
        return auftrag;
    }

    private Kundenwunschtermin kwt(int plusDays) {
        return createKundenWunschtermin(LocalDate.now().plusDays(plusDays));
    }

    @Test
    public void getDefaultWitaVersion() {
        WitaCdmVersion defaultVersion = WitaCdmVersion.V1;
        when(witaConfigDao.getWitaConfigValue(eq(WitaConfig.DEFAULT_WITA_VERSION))).thenReturn(
                defaultVersion.getVersion());
        assertThat(cut.getDefaultWitaVersion(), equalTo(defaultVersion));

        when(witaConfigDao.getWitaConfigValue(eq(WitaConfig.DEFAULT_WITA_VERSION))).thenReturn(null);
        try {
            cut.getDefaultWitaVersion();
            fail("WitaConfigException has been expected, but has not been thrown!");
        }
        catch (WitaConfigException wce) {
        }

        when(witaConfigDao.getWitaConfigValue(eq(WitaConfig.DEFAULT_WITA_VERSION))).thenReturn("");
        try {
            cut.getDefaultWitaVersion();
            fail("WitaConfigException has been expected, but has not been thrown!");
        }
        catch (WitaConfigException wce) {
        }
    }

    @Test
    public void getCdmVersion() {
        WbciCdmVersion expectedVersion = WbciCdmVersion.V1;
        when(witaConfigDao.getWitaConfigValue(eq("default.wbci.cdm.version.DTAG"))).thenReturn("1");
        when(witaConfigDao.getWitaConfigValue(eq("default.wbci.cdm.version.VODAFONE"))).thenReturn("1");
        Assert.assertEquals(cut.getWbciCdmVersion(CarrierCode.DTAG), expectedVersion);
        Assert.assertEquals(cut.getWbciCdmVersion(CarrierCode.VODAFONE), expectedVersion);
    }

    @Test(expectedExceptions = WitaConfigException.class)
    public void testgetCdmVersionException() throws Exception {
        cut.getWbciCdmVersion(null);
    }

    @Test(expectedExceptions = { WitaConfigException.class })
    public void getUndefindedCdmVersion() {
        cut.getWbciCdmVersion(CarrierCode.MNET);
    }


    @DataProvider(name = "getPossibleZeitfensterDataProvider")
    public Object[][] getPossibleZeitfensterDataProvider() {
        List<Zeitfenster> zfListNeuSince2014 = Arrays.asList(Zeitfenster.SLOT_7, Zeitfenster.SLOT_9);
        List<Zeitfenster> zfListKueKdSince2014 = Arrays.asList(Zeitfenster.SLOT_2);
        List<Zeitfenster> zfListVblSince2014 = Arrays.asList(Zeitfenster.SLOT_3, Zeitfenster.SLOT_4,
                Zeitfenster.SLOT_6, Zeitfenster.SLOT_7, Zeitfenster.SLOT_9);

        return new Object[][] {
                { WitaCBVorgang.TYP_NEU, null, zfListNeuSince2014 },
                { WitaCBVorgang.TYP_KUENDIGUNG, null, zfListKueKdSince2014 },
                { WitaCBVorgang.TYP_ANBIETERWECHSEL, Carrier.ID_QSC, zfListNeuSince2014 },
                { WitaCBVorgang.TYP_ANBIETERWECHSEL, Carrier.ID_DTAG, zfListVblSince2014 },
        };
    }


    @Test(dataProvider = "getPossibleZeitfensterDataProvider")
    public void testGetPossibleZeitfenster(Long cbVorgangTyp, Long vorabstimmungCarrier,
            List<Kundenwunschtermin.Zeitfenster> expectedResult) {

        List<Zeitfenster> result = cut.getPossibleZeitfenster(cbVorgangTyp, vorabstimmungCarrier);

        assertEquals(result.size(), expectedResult.size());
        assertTrue(result.containsAll(expectedResult));
    }

}
