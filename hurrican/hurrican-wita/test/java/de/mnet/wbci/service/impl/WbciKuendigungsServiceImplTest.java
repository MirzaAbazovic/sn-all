/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.common.service.holiday.DateCalculationHelper.*;
import static de.mnet.wbci.service.impl.WbciKuendigungsServiceImpl.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBuilder;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungViewBuilder;
import de.augustakom.hurrican.model.cc.KuendigungCheck;
import de.augustakom.hurrican.model.cc.KuendigungCheckBuilder;
import de.augustakom.hurrican.model.cc.KuendigungFrist;
import de.augustakom.hurrican.model.cc.KuendigungFristBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.KuendigungsCheckVO;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.builder.LeitungBuilder;
import de.mnet.wbci.model.builder.TechnischeRessourceBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciValidationService;

@Test(groups = UNIT)
public class WbciKuendigungsServiceImplTest {

    @InjectMocks
    @Spy
    private WbciKuendigungsServiceImpl testling;

    @Mock
    private WbciDao wbciDao;

    @Mock
    private BillingAuftragService billingAuftragService;

    @Mock
    private RufnummerService rufnummerService;
    @Mock
    private WbciCommonService wbciCommonService;
    @Mock
    private WbciValidationService wbciValidationService;

    @BeforeMethod
    public void setUp() {
        testling = new WbciKuendigungsServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void doKuendigungsCheckNoBillingOrder() throws FindException {
        Mockito.when(billingAuftragService.findAuftrag(Matchers.anyLong())).thenReturn(null);
        testling.doKuendigungsCheck(Long.valueOf(1), LocalDateTime.now());
    }

    @Test
    public void doKuendigungsCheck() throws FindException {
        LocalDateTime vertragsende = LocalDateTime.now().plusYears(2);
        BAuftrag billingOrder = new BAuftragBuilder().withOeNoOrig(3401L).withVertragsendedatum(Date.from(vertragsende.atZone(ZoneId.systemDefault()).toInstant())).build();
        Mockito.when(billingAuftragService.findAuftrag(Matchers.anyLong())).thenReturn(billingOrder);

        doReturn(null).when(testling).getRelevantOrderPositions(Matchers.anyLong());
        Mockito.doNothing().when(testling).evaluateCancelCheckForOrder(any(BAuftrag.class), any(LocalDateTime.class), any(KuendigungsCheckVO.class));

        LocalDateTime now = LocalDateTime.now();
        KuendigungsCheckVO vo = testling.doKuendigungsCheck(Long.valueOf(1), now);
        Assert.assertNotNull(vo);
        assertEquals(vo.getMindestVertragslaufzeitTaifun(), vertragsende);
        Mockito.verify(testling).evaluateCancelCheckForOrder(billingOrder, now, vo);
    }

    @Test
    public void testGetTaifunKuendigungstermin() throws Exception {
        LocalDateTime kundigungsDatum = LocalDateTime.now().plusYears(2);
        BAuftrag billingOrder = new BAuftragBuilder().withOeNoOrig(3401L).withKuendigungsdatum(Date.from(kundigungsDatum.atZone(ZoneId.systemDefault()).toInstant())).build();
        Mockito.when(billingAuftragService.findAuftrag(Matchers.anyLong())).thenReturn(billingOrder);

        Long taifunOrderNo = 3402L;
        LocalDateTime result = testling.getTaifunKuendigungstermin(taifunOrderNo);
        Assert.assertNotNull(result);
        assertEquals(result, kundigungsDatum);
    }

    @Test
    public void testGetTaifunKuendigungsterminNull() throws Exception {
        BAuftrag billingOrder = new BAuftragBuilder().withOeNoOrig(3401L).withKuendigungsdatum(null).build();
        Mockito.when(billingAuftragService.findAuftrag(Matchers.anyLong())).thenReturn(billingOrder);

        Long taifunOrderNo = 3402L;
        LocalDateTime result = testling.getTaifunKuendigungstermin(taifunOrderNo);
        Assert.assertNull(result);
    }

    @Test
    public void testGetTaifunKuendigungsterminNoOrder() throws Exception {
        Mockito.when(billingAuftragService.findAuftrag(Matchers.anyLong())).thenReturn(null);

        LocalDateTime result = testling.getTaifunKuendigungstermin(3402L);
        Assert.assertEquals(result, ZonedDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneId.systemDefault()).toLocalDateTime());
    }

    @DataProvider(name = "dataProviderGetRelevantOrderPositions")
    public Object[][] dataProviderGetRelevantOrderPositions() {
        BAuftragLeistungView cancelled1 = new BAuftragLeistungViewBuilder().withAuftragPosGueltigBis(Date.from(ZonedDateTime.now().toInstant())).build();
        BAuftragLeistungView cancelled2 = new BAuftragLeistungViewBuilder().withAuftragPosGueltigBis(Date.from(ZonedDateTime.now().toInstant())).build();
        BAuftragLeistungView cancelled3 = new BAuftragLeistungViewBuilder().withAuftragPosGueltigBis(Date.from(ZonedDateTime.now().minusDays(5).toInstant())).build();

        BAuftragLeistungView active1 = new BAuftragLeistungViewBuilder().build();
        BAuftragLeistungView active2 = new BAuftragLeistungViewBuilder().build();

        return new Object[][] {
                { Lists.newArrayList(active1, active2), Lists.newArrayList(active1, active2) },
                { Lists.newArrayList(cancelled1, cancelled3, active1, active2), Lists.newArrayList(active1, active2) },
                { Lists.newArrayList(cancelled1, cancelled2, cancelled3), Lists.newArrayList(cancelled1, cancelled2) },
        };
    }


    @Test(dataProvider = "dataProviderGetRelevantOrderPositions")
    public void getRelevantOrderPositions(List<BAuftragLeistungView> positions, List<BAuftragLeistungView> expectedResult) throws FindException {
        Mockito.when(billingAuftragService.findProduktLeistungen4Auftrag(Matchers.any(Long.class))).thenReturn(positions);

        List<BAuftragLeistungView> result = testling.getRelevantOrderPositions(1L);
        assertEquals(result.size(), expectedResult.size());
        Assert.assertTrue(expectedResult.containsAll(result));
    }


    @Test
    public void evaluateCancelCheckForOrderWithoutConfigurationOrCheckVertrieb() {
        KuendigungCheck checkVertrieb = new KuendigungCheckBuilder().withDurchVertrieb().setPersist(false).build();
        List<KuendigungCheck> values = Lists.newArrayList(null, checkVertrieb);

        for (KuendigungCheck check : values) {
            Mockito.when(wbciDao.findKuendigungCheckForOeNoOrig(Matchers.anyLong())).thenReturn(check);

            BAuftrag billingOrder = new BAuftragBuilder()
                    .withVertragsLaufzeit(12L)
                    .build();
            doReturn(false).when(testling).isPmxOrTk(billingOrder);
            KuendigungsCheckVO result = new KuendigungsCheckVO();

            testling.evaluateCancelCheckForOrder(billingOrder, LocalDateTime.now(), result);
            Assert.assertNull(result.getVertragsverlaengerung());
            Assert.assertNull(result.getCalculatedEarliestCancelDate());
            Assert.assertTrue(result.getKuendigungsstatus().isAskSales());
            assertEquals(result.getKuendigungsstatus(),
                    KuendigungsCheckVO.Kuendigungsstatus.MANUELL_CONTACT_SALES);

            Mockito.verify(testling, Mockito.times(0))
                    .calculateEarliestCancelDate(
                            any(KuendigungsCheckVO.class),
                            any(LocalDateTime.class),
                            any(KuendigungCheck.class),
                            any(KuendigungFrist.class));
        }
    }

    @Test
    public void evaluateCancelCheckForOrderWithCancelledOrder() {
        BAuftrag billingOrder = new BAuftragBuilder()
                .withKuendigungsdatum(new Date())
                .build();

        KuendigungsCheckVO result = new KuendigungsCheckVO();
        doReturn(KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_AKTIV_ABW_OK).when(testling)
                .getKuendigungstatusForCanceledOrders(any(LocalDateTime.class), any(LocalDateTime.class));

        testling.evaluateCancelCheckForOrder(billingOrder, LocalDateTime.now(), result);
        assertEquals(Date.from(result.getCalculatedEarliestCancelDate().atZone(ZoneId.systemDefault()).toInstant()), billingOrder.getKuendigungsdatum());
        Assert.assertNull(result.getKuendigungsfrist());
        assertEquals(result.getKuendigungsstatus(), KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_AKTIV_ABW_OK);
    }

    @DataProvider
    public Object[][] alreadyCanceledOrders() {
        return new Object[][] {
                // @formatter:off
                //date of line cancelation, requestIncome, expected Kuendigungstatus
                {LocalDateTime.now(), LocalDateTime.now(), KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_AKTIV_ABW_NICHT_OK},
                {getDateInWorkingDaysFromNow(5),getDateInWorkingDaysFromNow(0), KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_AKTIV_ABW_NICHT_OK},
                {getDateInWorkingDaysFromNow(6),  getDateInWorkingDaysFromNow(0), KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_AKTIV_ABW_WARNING},
                {getDateInWorkingDaysFromNow(10), getDateInWorkingDaysFromNow(0), KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_AKTIV_ABW_WARNING},
                {getDateInWorkingDaysFromNow(11), getDateInWorkingDaysFromNow(0), KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_AKTIV_ABW_OK},
                {LocalDateTime.now().minusDays(1), LocalDateTime.now(), KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_INAKTIV_PORTIERUNG_OK},
                {LocalDateTime.now().minusDays(90), LocalDateTime.now(), KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_INAKTIV_PORTIERUNG_OK},
                {LocalDateTime.now().minusDays(91), LocalDateTime.now(), KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_INAKTIV_PORTIERUNG_NICHT_OK},
                // @formatter:on
        };
    }

    @Test(dataProvider = "alreadyCanceledOrders")
    public void testGetKundigungstatusForCanceledOrders(LocalDateTime lineCancelation, LocalDateTime requestIncome,
            KuendigungsCheckVO.Kuendigungsstatus kuendigungsstatus) throws Exception {
        assertEquals(testling.getKuendigungstatusForCanceledOrders(lineCancelation, requestIncome),
                kuendigungsstatus);
    }

    @Test
    public void evaluateCancelCheckForOrderForPmxOrTk() {
        BAuftrag billingOrder = new BAuftragBuilder().build();

        doReturn(true).when(testling).isPmxOrTk(billingOrder);

        KuendigungCheck check = new KuendigungCheckBuilder()
                .addKuendiungFrist(new KuendigungFristBuilder()
                        .withAutoVerlaengerung(12L)
                        .withFristAuf(KuendigungFrist.FristAuf.EINGANGSDATUM)
                        .withFristInWochen(4L)
                        .setPersist(false).build())
                .setPersist(false).build();
        Mockito.when(wbciDao.findKuendigungCheckForOeNoOrig(Matchers.anyLong())).thenReturn(check);

        KuendigungsCheckVO result = new KuendigungsCheckVO();

        testling.evaluateCancelCheckForOrder(billingOrder, LocalDateTime.now(), result);
        Assert.assertNotNull(result.getCalculatedEarliestCancelDate());
        Assert.assertNotNull(result.getKuendigungsfrist());
        Assert.assertTrue(result.getKuendigungsstatus().isAskSales());
        assertEquals(result.getKuendigungsstatus(), KuendigungsCheckVO.Kuendigungsstatus.MANUELL_PMX_OR_TK);
    }


    @Test(expectedExceptions = WbciServiceException.class)
    public void calculateEarliestCancelDateWithoutCancelationPeriodException() {
        testling.calculateEarliestCancelDateWithoutCancellationPeriod(
                LocalDateTime.now(),
                new KuendigungFristBuilder().withFristAuf(KuendigungFrist.FristAuf.ENDE_MVLZ).setPersist(false).build()
        );
    }

    @DataProvider(name = "dataProviderWithoutCancellationPeriod")
    public Object[][] dataProviderWithoutCancellationPeriod() {
        KuendigungFrist abEingang4Wochen = new KuendigungFristBuilder()
                .withFristAuf(KuendigungFrist.FristAuf.EINGANGSDATUM)
                .withFristInWochen(4L)
                .setPersist(false).build();
        KuendigungFrist abEingang6Wochen = new KuendigungFristBuilder()
                .withFristAuf(KuendigungFrist.FristAuf.EINGANGSDATUM)
                .withFristInWochen(6L)
                .setPersist(false).build();
        KuendigungFrist zumMonatsende4Wochen = new KuendigungFristBuilder()
                .withFristAuf(KuendigungFrist.FristAuf.MONATSENDE)
                .withFristInWochen(4L)
                .setPersist(false).build();
        LocalDateTime cancellationIncome = LocalDateTime.of(2014, 1, 20, 0, 0, 0, 0);

        return new Object[][] {
                { cancellationIncome, abEingang4Wochen, LocalDateTime.of(2014, 2, 20, 0, 0, 0, 0) },
                { cancellationIncome, abEingang6Wochen, LocalDateTime.of(2014, 3, 3, 0, 0, 0, 0) },
                { cancellationIncome, zumMonatsende4Wochen, LocalDateTime.of(2014, 2, 28, 0, 0, 0, 0) },
        };
    }

    @Test(dataProvider = "dataProviderWithoutCancellationPeriod")
    public void calculateEarliestCancelDateWithoutCancellationPeriod(LocalDateTime cancellationIncome, KuendigungFrist fristToUse, LocalDateTime expected) {
        LocalDateTime result = testling.calculateEarliestCancelDateWithoutCancellationPeriod(cancellationIncome, fristToUse);
        assertEquals(result, expected);
    }


    @DataProvider(name = "dataProviderWithCancellationPeriod")
    public Object[][] dataProviderWithCancellationPeriod() {
        LocalDateTime firstOfMarch2014 = LocalDateTime.of(2014, 3, 1, 0, 0, 0, 0);
        LocalDateTime firstOfJune2014 = LocalDateTime.of(2014, 6, 1, 0, 0, 0, 0);
        LocalDateTime firstOfJuly2014 = LocalDateTime.of(2014, 7, 1, 0, 0, 0, 0);
        LocalDateTime firstOfJanuary2015 = LocalDateTime.of(2015, 1, 1, 0, 0, 0, 0);
        KuendigungCheck kuendigungCheck = new KuendigungCheckBuilder().setPersist(false).build();

        KuendigungFrist fristWith3MonthAuto3 = new KuendigungFristBuilder()
                .withFristAuf(KuendigungFrist.FristAuf.ENDE_MVLZ)
                .withFristInWochen(12L)
                .withAutoVerlaengerung(3L)
                .withDescription("3 Monate zum Ende der MVLZ")
                .setPersist(false).build();

        KuendigungFrist fristWith3MonthAuto6 = new KuendigungFristBuilder()
                .withFristAuf(KuendigungFrist.FristAuf.ENDE_MVLZ)
                .withFristInWochen(12L)
                .withAutoVerlaengerung(6L)
                .withDescription("3 Monate zum Ende der MVLZ")
                .setPersist(false).build();

        KuendigungFrist fristWith3MonthAuto12 = new KuendigungFristBuilder()
                .withFristAuf(KuendigungFrist.FristAuf.ENDE_MVLZ)
                .withFristInWochen(12L)
                .withAutoVerlaengerung(12L)
                .withDescription("3 Monate zum Ende der MVLZ")
                .setPersist(false).build();

        return new Object[][] {
                { firstOfMarch2014, firstOfJuly2014, kuendigungCheck, fristWith3MonthAuto12, firstOfJuly2014 },
                { firstOfJune2014, firstOfJuly2014, kuendigungCheck, fristWith3MonthAuto12, firstOfJuly2014.plusMonths(12) },
                { firstOfJune2014, firstOfJuly2014, kuendigungCheck, fristWith3MonthAuto6, firstOfJuly2014.plusMonths(6) },
                // MVLZ=01.07.2014; Eingang=11.07.2014 --> erste Verlaengerung auf 01.10.2014; dann + autom. Verlaengerung von 3 Monaten --> 01.01.2015
                { firstOfJuly2014.plusDays(10), firstOfJuly2014, kuendigungCheck, fristWith3MonthAuto3, firstOfJanuary2015 }
        };
    }

    @Test(dataProvider = "dataProviderWithCancellationPeriod")
    public void calculateEarliestCancelDateWithCancellationPeriod(
            LocalDateTime cancellationIncome, LocalDateTime vertragsende, KuendigungCheck kuendigungCheck, KuendigungFrist fristToUse, LocalDateTime expected) {
        LocalDateTime result = testling.calculateEarliestCancelDateWithCancellationPeriod(cancellationIncome, vertragsende, fristToUse);
        assertEquals(result, expected);
    }

    @Test
         public void calculateEarliestCancelDateCheckWorkingDay() {
        KuendigungCheck checkWith3Month = new KuendigungCheckBuilder().setPersist(false).build();
        KuendigungFrist fristWith3Month = new KuendigungFristBuilder()
                .withFristAuf(KuendigungFrist.FristAuf.EINGANGSDATUM)
                .withFristInWochen(12L)
                .withAutoVerlaengerung(3L)
                .withDescription("3 Monate zum Ende der MVLZ")
                .setPersist(false).build();

        LocalDateTime holidayOnFriday = LocalDateTime.of(2014, 8, 15, 0, 0, 0, 0);
        doReturn(holidayOnFriday).when(testling).calculateEarliestCancelDateWithoutCancellationPeriod(
                any(LocalDateTime.class), any(KuendigungFrist.class));

        KuendigungsCheckVO vo = new KuendigungsCheckVO();
        testling.calculateEarliestCancelDate(vo, LocalDateTime.now(), checkWith3Month, fristWith3Month);
        Assert.assertTrue(Date.from(vo.getCalculatedEarliestCancelDate().atZone(ZoneId.systemDefault()).toInstant()).equals(Date.from(holidayOnFriday.plusDays(3).atZone(ZoneId.systemDefault()).toInstant())));
    }

    @Test
    public void calculateEarliestCancelDateWithMvlzButWithoutAutoVerlaengerung() {
        KuendigungCheck checkWith4Weeks = new KuendigungCheckBuilder().setPersist(false).build();
        KuendigungFrist fristWith4Weeks = new KuendigungFristBuilder()
                .withFristAuf(KuendigungFrist.FristAuf.EINGANGSDATUM)
                .withFristInWochen(4L)
                .withAutoVerlaengerung(0L)
                .withDescription("4 Wochen ab Eingangsdatum")
                .withMitMvlz()
                .setPersist(false).build();

        LocalDateTime income = LocalDateTime.of(2014, 6, 24, 0, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2014, 7, 24, 0, 0, 0, 0);

        KuendigungsCheckVO vo = new KuendigungsCheckVO();
        testling.calculateEarliestCancelDate(vo, income, checkWith4Weeks, fristWith4Weeks);
        Assert.assertTrue(Date.from(vo.getCalculatedEarliestCancelDate().atZone(ZoneId.systemDefault()).toInstant()).equals(Date.from(expected.atZone(ZoneId.systemDefault()).toInstant())));
    }

    @DataProvider(name = "dataProviderIsPmxOrTk")
    public Object[][] dataProviderIsPmxOrTk() {
        Rufnummer singleDn = new RufnummerBuilder().withOnKz("089").withDnBase("123456").build();
        Rufnummer blockSize10 = new RufnummerBuilder().withOnKz("089").withDnBase("123456").withDirectDial("0").withRangeFrom("0").withRangeTo("9").withDnSize(10L).build();
        Rufnummer blockSize100 = new RufnummerBuilder().withOnKz("089").withDnBase("123456").withDirectDial("0").withRangeFrom("00").withRangeTo("99").withDnSize(100L).build();
        Rufnummer blockSize1000 = new RufnummerBuilder().withOnKz("089").withDnBase("123456").withDirectDial("0").withRangeFrom("000").withRangeTo("999").withDnSize(1000L).build();

        return new Object[][] {
                { Arrays.asList(singleDn), false },
                { Arrays.asList(blockSize10), false },
                { Arrays.asList(singleDn, blockSize10), false },
                { Arrays.asList(blockSize100), false },
                { Arrays.asList(blockSize10, blockSize100), false },
                { Arrays.asList(blockSize1000), true },
                { Arrays.asList(blockSize10, blockSize1000), true },
        };
    }


    @Test(dataProvider = "dataProviderIsPmxOrTk")
    public void isPmxOrTk(List<Rufnummer> rufnummern, boolean expectedResult) throws FindException {
        BAuftrag billingOrder = new BAuftragBuilder().withAuftragNoOrig(99L).build();

        Mockito.when(rufnummerService.findRNs4Auftrag(anyLong())).thenReturn(rufnummern);
        assertEquals(testling.isPmxOrTk(billingOrder), expectedResult);
    }


    @DataProvider(name = "dataProviderCalculateMindestvertragslaufzeit")
    public Object[][] dataProviderCalculateMindestvertragslaufzeit() {
        BAuftrag withMvlz = new BAuftragBuilder().withVertragsLaufzeit(12L).build();
        BAuftrag withoutMvlz = new BAuftragBuilder().withVertragsLaufzeit(null).build();

        LocalDateTime firstOfJuly2013 = LocalDateTime.of(2013, 7, 1, 0, 0, 0, 0);
        LocalDateTime endOfJune2013 = LocalDateTime.of(2014, 6, 30, 0, 0, 0, 0);
        LocalDateTime firstOfJanuary2014 = LocalDateTime.of(2014, 1, 1, 0, 0, 0, 0);
        LocalDateTime endOfDecember2014 = LocalDateTime.of(2014, 12, 31, 0, 0, 0, 0);

        BAuftragLeistungView active1 = new BAuftragLeistungViewBuilder().withAuftragPosGueltigVon(Date.from(firstOfJuly2013.atZone(ZoneId.systemDefault()).toInstant())).build();
        BAuftragLeistungView active2 = new BAuftragLeistungViewBuilder().withAuftragPosGueltigVon(Date.from(firstOfJuly2013.atZone(ZoneId.systemDefault()).toInstant())).build();
        BAuftragLeistungView active3 = new BAuftragLeistungViewBuilder().withAuftragPosGueltigVon(Date.from(firstOfJanuary2014.atZone(ZoneId.systemDefault()).toInstant())).build();

        return new Object[][] {
                { withoutMvlz, null, null },
                { withMvlz, Lists.newArrayList(active1, active2), endOfJune2013 },
                { withMvlz, Lists.newArrayList(active1, active2, active3), endOfDecember2014 }
        };
    }

    @Test(dataProvider = "dataProviderCalculateMindestvertragslaufzeit")
    public void calculateMindestvertragslaufzeit(BAuftrag billingOrder, List<BAuftragLeistungView> positions, LocalDateTime expected) {
        LocalDateTime result = testling.calculateMindestvertragslaufzeit(billingOrder, positions);
        assertEquals(result, expected);
    }


    @DataProvider(name = "isSurfAndFonHackPresentDP")
    public Object[][] isSurfAndFonHackPresentDP() {
        LocalDateTime firstOfJanuary2014 = LocalDateTime.of(2014, 1, 1, 0, 0, 0, 0);
        LocalDateTime firstOfJuly2014 = LocalDateTime.of(2014, 7, 1, 0, 0, 0, 0);

        BAuftragLeistungView other = new BAuftragLeistungViewBuilder().withLeistungNoOrig(99L).withAuftragPosGueltigVon(Date.from(firstOfJanuary2014.atZone(ZoneId.systemDefault()).toInstant())).build();
        // Surf & Phone 18
        BAuftragLeistungView sf18Old = new BAuftragLeistungViewBuilder().withLeistungNoOrig(SURF_AND_FON_18.getFirst()).withAuftragPosGueltigVon(Date.from(firstOfJanuary2014.atZone(ZoneId.systemDefault()).toInstant())).build();
        BAuftragLeistungView sf18New = new BAuftragLeistungViewBuilder().withLeistungNoOrig(SURF_AND_FON_18.getSecond()).withAuftragPosGueltigVon(Date.from(firstOfJuly2014.atZone(ZoneId.systemDefault()).toInstant())).build();
        BAuftragLeistungView sf18NewValidFromNotOk = new BAuftragLeistungViewBuilder().withLeistungNoOrig(SURF_AND_FON_18.getSecond()).withAuftragPosGueltigVon(Date.from(firstOfJuly2014.plusDays(1).atZone(ZoneId.systemDefault()).toInstant())).build();
        // Surf & Phone 50
        BAuftragLeistungView sf50Old = new BAuftragLeistungViewBuilder().withLeistungNoOrig(SURF_AND_FON_50.getFirst()).withAuftragPosGueltigVon(Date.from(firstOfJanuary2014.atZone(ZoneId.systemDefault()).toInstant())).build();
        BAuftragLeistungView sf50New = new BAuftragLeistungViewBuilder().withLeistungNoOrig(SURF_AND_FON_50.getSecond()).withAuftragPosGueltigVon(Date.from(firstOfJuly2014.atZone(ZoneId.systemDefault()).toInstant())).build();
        BAuftragLeistungView sf50NewValidFromNotOk = new BAuftragLeistungViewBuilder().withLeistungNoOrig(SURF_AND_FON_50.getSecond()).withAuftragPosGueltigVon(Date.from(firstOfJuly2014.plusDays(1).atZone(ZoneId.systemDefault()).toInstant())).build();
        // Surf & Phone 18
        BAuftragLeistungView sf100Old = new BAuftragLeistungViewBuilder().withLeistungNoOrig(SURF_AND_FON_100.getFirst()).withAuftragPosGueltigVon(Date.from(firstOfJanuary2014.atZone(ZoneId.systemDefault()).toInstant())).build();
        BAuftragLeistungView sf100New = new BAuftragLeistungViewBuilder().withLeistungNoOrig(SURF_AND_FON_100.getSecond()).withAuftragPosGueltigVon(Date.from(firstOfJuly2014.atZone(ZoneId.systemDefault()).toInstant())).build();
        BAuftragLeistungView sf100NewValidFromNotOk = new BAuftragLeistungViewBuilder().withLeistungNoOrig(SURF_AND_FON_100.getSecond()).withAuftragPosGueltigVon(Date.from(firstOfJuly2014.plusDays(1).atZone(ZoneId.systemDefault()).toInstant())).build();

        return new Object[][] {
                { Lists.newArrayList(other), Lists.newArrayList(sf18New), SURF_AND_FON_18, false },
                { Lists.newArrayList(sf18Old), Lists.newArrayList(sf18New), SURF_AND_FON_18, true },
                { Lists.newArrayList(sf18Old), Lists.newArrayList(sf18NewValidFromNotOk), SURF_AND_FON_18, false },
                { Lists.newArrayList(sf18Old), Lists.newArrayList(other), SURF_AND_FON_18, false },
                { Lists.newArrayList(other), Lists.newArrayList(sf50New), SURF_AND_FON_50, false },
                { Lists.newArrayList(sf50Old), Lists.newArrayList(sf50New), SURF_AND_FON_50, true },
                { Lists.newArrayList(sf50Old), Lists.newArrayList(sf50NewValidFromNotOk), SURF_AND_FON_50, false },
                { Lists.newArrayList(sf50Old), Lists.newArrayList(other), SURF_AND_FON_50, false },
                { Lists.newArrayList(other), Lists.newArrayList(sf100New), SURF_AND_FON_100, false },
                { Lists.newArrayList(sf100Old), Lists.newArrayList(sf100New), SURF_AND_FON_100, true },
                { Lists.newArrayList(sf100Old), Lists.newArrayList(sf100NewValidFromNotOk), SURF_AND_FON_100, false },
                { Lists.newArrayList(sf100Old), Lists.newArrayList(other), SURF_AND_FON_100, false },
        };
    }

    @Test(dataProvider = "isSurfAndFonHackPresentDP")
    public void isSurfAndFonHackPresent(List<BAuftragLeistungView> cancelled, List<BAuftragLeistungView> active,
            Pair<Long, Long> surfAndFonServices, boolean expected) {
        boolean result = testling.isSurfAndFonHackPresent(active, cancelled, surfAndFonServices);
        assertEquals(result, expected);
    }

    @DataProvider
    public Object[][] changeDateDP() {
        return new Object[][] {
                { dt(2014, 2, 1), true, 2, dt(2014, 2, 15) },
                { dt(2014, 2, 1), true, 4, dt(2014, 3, 1) },
                { dt(2014, 2, 1), true, 7, dt(2014, 3, 22) },
                { dt(2014, 2, 1), true, 8, dt(2014, 4, 1) },
                { dt(2014, 2, 1), true, 15, dt(2014, 5, 17) },
                { dt(2014, 2, 1), true, 16, dt(2014, 6, 1) },

                { dt(2014, 2, 1), false, 2, dt(2014, 1, 18) },
                { dt(2014, 2, 1), false, 4, dt(2014, 1, 1) },
                { dt(2014, 2, 1), false, 7, dt(2013, 12, 14) },
                { dt(2014, 2, 1), false, 8, dt(2013, 12, 1) },
                { dt(2014, 2, 1), false, 15, dt(2013, 10, 19) },
                { dt(2014, 2, 1), false, 16, dt(2013, 10, 1) },
        };
    }

    @Test(dataProvider = "changeDateDP")
    public void changeDate(LocalDateTime base, boolean add, int weeks, LocalDateTime expected) {
        assertEquals(testling.changeDate(base, add, weeks), expected);
    }

    @Test
    public void testGetCancellableWitaVertragsnummernExpectedNull() throws Exception {
        final String vaId = "DEU.DTAG.VH0001";
        when(wbciCommonService.findLastForVaId(vaId, UebernahmeRessourceMeldung.class)).thenReturn(null);
        assertEquals(testling.getCancellableWitaVertragsnummern(vaId).size(), 0);
        verify(wbciCommonService).findLastForVaId(vaId, UebernahmeRessourceMeldung.class);

        UebernahmeRessourceMeldung akmTr = mock(UebernahmeRessourceMeldung.class);
        when(wbciCommonService.findLastForVaId(vaId, UebernahmeRessourceMeldung.class)).thenReturn(akmTr);
        when(wbciValidationService.isTvOrStornoActive(vaId)).thenReturn(true);
        assertEquals(testling.getCancellableWitaVertragsnummern(vaId).size(), 0);
        verify(wbciCommonService, times(2)).findLastForVaId(vaId, UebernahmeRessourceMeldung.class);
        verify(wbciValidationService).isTvOrStornoActive(vaId);

        RueckmeldungVorabstimmung ruemVa = mock(RueckmeldungVorabstimmung.class);
        when(wbciCommonService.findLastForVaId(vaId, RueckmeldungVorabstimmung.class)).thenReturn(ruemVa);
        when(wbciValidationService.isTvOrStornoActive(vaId)).thenReturn(false);
        when(akmTr.isUebernahme()).thenReturn(false);
        assertEquals(testling.getCancellableWitaVertragsnummern(vaId).size(), 0);
        verify(akmTr).isUebernahme();
        verify(wbciCommonService).findLastForVaId(vaId, RueckmeldungVorabstimmung.class);
        verify(wbciCommonService, times(3)).findLastForVaId(vaId, UebernahmeRessourceMeldung.class);
        verify(wbciValidationService, times(2)).isTvOrStornoActive(vaId);
    }

    @Test
    public void testGetCancellableWitaVertragsnummern() throws Exception {
        final String vaId = "DEU.DTAG.VH0001";
        final String vertNr1 = "VERTR1";
        final String vertNr2 = "VERTR2";

        UebernahmeRessourceMeldung akmTr = mock(UebernahmeRessourceMeldung.class);
        when(wbciCommonService.findLastForVaId(vaId, UebernahmeRessourceMeldung.class)).thenReturn(akmTr);
        RueckmeldungVorabstimmung ruemVa = mock(RueckmeldungVorabstimmung.class);
        when(wbciCommonService.findLastForVaId(vaId, RueckmeldungVorabstimmung.class)).thenReturn(ruemVa);
        when(wbciValidationService.isTvOrStornoActive(vaId)).thenReturn(false);
        when(akmTr.isUebernahme()).thenReturn(false);
        when(ruemVa.getTechnischeRessourcen()).thenReturn(Sets.newHashSet(
                new TechnischeRessourceBuilder().withVertragsnummer(vertNr1).build(),
                new TechnischeRessourceBuilder().withVertragsnummer(vertNr2).build()
        ));
        SortedSet<String> result = testling.getCancellableWitaVertragsnummern(vaId);
        assertEquals(result.size(), 2);
        assertTrue(result.contains(vertNr1));
        assertTrue(result.contains(vertNr2));
        verify(akmTr).isUebernahme();
        verify(akmTr, never()).getLeitungen();
        verify(wbciCommonService).findLastForVaId(vaId, RueckmeldungVorabstimmung.class);
        verify(wbciCommonService).findLastForVaId(vaId, UebernahmeRessourceMeldung.class);
        verify(wbciValidationService).isTvOrStornoActive(vaId);

        when(akmTr.isUebernahme()).thenReturn(true);
        when(akmTr.getLeitungen()).thenReturn(Sets.newHashSet(
                new LeitungBuilder().withVertragsnummer(vertNr1).build()
        ));
        result = testling.getCancellableWitaVertragsnummern(vaId);
        assertEquals(result.size(), 1);
        assertTrue(result.contains(vertNr2));
        assertFalse(result.contains(vertNr1));
        verify(akmTr, times(2)).isUebernahme();
        verify(wbciCommonService, times(2)).findLastForVaId(vaId, RueckmeldungVorabstimmung.class);
        verify(wbciCommonService, times(2)).findLastForVaId(vaId, UebernahmeRessourceMeldung.class);
        verify(wbciValidationService, times(2)).isTvOrStornoActive(vaId);

        when(akmTr.isUebernahme()).thenReturn(true);
        when(akmTr.getLeitungen()).thenReturn(null);
        result = testling.getCancellableWitaVertragsnummern(vaId);
        assertFalse(result.contains(vertNr1));
        assertFalse(result.contains(vertNr2));
    }


    private static LocalDateTime dt(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0, 0, 0);
    }

}
