/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import static de.augustakom.hurrican.service.cc.RegistryService.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.billing.view.TimeSlotViewBuilder;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragWholesale;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungBuilder;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.enums.FfmTyp;
import de.augustakom.hurrican.model.cc.ffm.FfmProductMapping;
import de.augustakom.hurrican.model.cc.ffm.FfmProductMappingBuilder;
import de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster;
import de.augustakom.hurrican.model.cc.view.TimeSlotHolder;
import de.augustakom.hurrican.model.cc.view.TimeSlotHolderBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.resource.workforceservice.v1.RequestedTimeslot;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmHeaderTimeslotCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private BillingAuftragService billingAuftragService;

    @Mock
    private BAService baService;

    @Mock
    private FFMService ffmService;

    @Mock
    private RegistryService registryService;

    @Mock
    private CCAuftragService auftragService;

    @InjectMocks
    @Spy
    private AggregateFfmHeaderTimeslotCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmHeaderTimeslotCommand();
        prepareFfmCommand(testling);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteFailure() throws Exception {
        doThrow(new FFMServiceException("failure")).when(testling).getAuftragDaten();

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(!((ServiceCommandResult) result).isOk());
    }

    @DataProvider
    private Object[][] executeDP() {
        return new Object[][] {
                { Optional.of(new VerlaufBuilder().setPersist(false)
                        .withRealisierungstermin(Date.from(ZonedDateTime.of(2014, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant())).build()),
                        true, Date.from(ZonedDateTime.of(2014, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant()), true },
                { Optional.of(new VerlaufBuilder().setPersist(false)
                        .withRealisierungstermin(Date.from(ZonedDateTime.of(2014, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant())).build()),
                        true, Date.from(ZonedDateTime.of(2014, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant()), false },
                { Optional.of(new VerlaufBuilder().setPersist(false)
                        .withRealisierungstermin(Date.from(ZonedDateTime.of(2014, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant())).build()),
                        true, null, false },
                { Optional.empty(), false, null, false },
        };
    }

    @Test(dataProvider = "executeDP")
    public void testExecute(Optional<Verlauf> bauauftrag, boolean expectCallingFindVA, Date vaRealDatum,
            boolean expectTimeSlotManuallyOverwritten) throws Exception {
        doReturn(bauauftrag).when(testling).getBauauftrag();
        VerlaufAbteilung verlaufAbteilung = new VerlaufAbteilungBuilder().withRealisierungsdatum(vaRealDatum).setPersist(false).build();
        when(baService.findVerlaufAbteilung(any(), eq(Abteilung.FFM))).thenReturn(verlaufAbteilung);
        doNothing().when(testling).setRequestedTimeSlot(DateConverterUtils.asLocalDateTime(vaRealDatum, new Date()), REGID_FFM_TIMESLOT_MANUAL_FROM, REGID_FFM_TIMESLOT_MANUAL_TO);
        doNothing().when(testling).setWorkforceOrderTimeSlot();

        Object result = testling.execute();
        verify(testling).getBauauftrag();
        verify(baService, expectCallingFindVA ? times(1) : never()).findVerlaufAbteilung(any(), eq(Abteilung.FFM));
        verify(testling, expectTimeSlotManuallyOverwritten ? times(1) : never())
                .setRequestedTimeSlot(DateConverterUtils.asLocalDateTime(vaRealDatum, new Date()), REGID_FFM_TIMESLOT_MANUAL_FROM, REGID_FFM_TIMESLOT_MANUAL_TO);
        verify(testling, !expectTimeSlotManuallyOverwritten ? times(1) : never()).setWorkforceOrderTimeSlot();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());
    }


    @Test
    public void testExecute4Wholesaale() throws Exception {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(1L);
        auftragDaten.setWholesaleAuftragsId("HER_0001");

        doReturn(auftragDaten).when(testling).getAuftragDaten();

        Object result = testling.execute();

        verify(testling).setWorkforceOrderTimeSlot4Wholesale(any(Long.class));
        Assert.assertNotNull(result);
    }


    @DataProvider
    private Object[][] setWorkforceOrderTimeSlotDP() {
        return new Object[][] {
                { FfmTyp.KUENDIGUNG },
                { FfmTyp.NEU },
                { FfmTyp.ENTSTOERUNG },
        };
    }

    @Test(dataProvider = "setWorkforceOrderTimeSlotDP")
    public void testSetWorkforceOrderTimeSlot(FfmTyp ffmTyp) throws Exception {
        FfmProductMapping productMapping = new FfmProductMappingBuilder().withBaFfmTyp(ffmTyp).build();
        doReturn(productMapping).when(testling).getFfmProductMapping();
        doNothing().when(testling)
                .setRequestedTimeSlot(REGID_FFM_TIMESLOT_KUENDIGUNG_FROM, REGID_FFM_TIMESLOT_KUENDIGUNG_TO);
        doNothing().when(testling).setRequestedTimeSlotForNeuAndEntstoerung();

        testling.setWorkforceOrderTimeSlot();
        verify(testling).getFfmProductMapping();
        verify(testling, FfmTyp.KUENDIGUNG == ffmTyp ? times(1) : never())
                .setRequestedTimeSlot(REGID_FFM_TIMESLOT_KUENDIGUNG_FROM, REGID_FFM_TIMESLOT_KUENDIGUNG_TO);
        verify(testling, FfmTyp.KUENDIGUNG != ffmTyp ? times(1) : never()).setRequestedTimeSlotForNeuAndEntstoerung();
    }

    @Test
    public void testSetRequestedTimeSlotWithTimeSlotView() throws FindException {
        TimeSlotHolder timeSlotHolder = new TimeSlotHolderBuilder()
                .withTimeSlotViewBuilder(new TimeSlotViewBuilder()
                        .withDate(2014, 10, 21)
                        .withDaytimeFrom(10, 0)  // 10:00 Uhr
                        .withDaytimeTo(13, 0))    // 13:00 Uhr
                .build();
        doReturn(timeSlotHolder).when(baService).getTimeSlotHolder(anyLong());
        doReturn(LocalDateTime.of(2014, 10, 21, 10, 0, 0, 0)).when(testling).getReferenceDate();
        doNothing().when(testling).setRequestedTimeSlot(any(LocalDateTime.class), any(LocalDateTime.class));

        testling.setRequestedTimeSlotForNeuAndEntstoerung();
        verify(baService).getTimeSlotHolder(anyLong());
        verify(testling).setRequestedTimeSlot(eq(LocalDateTime.of(2014, 10, 21, 10, 0, 0, 0)),
                eq(LocalDateTime.of(2014, 10, 21, 13, 0, 0, 0)));
    }

    @DataProvider
    private Object[][] setRequestedTimeSlotWithoutTimeSlotViewDP() {
        return new Object[][] {
                { false, false, true },
                { true, false, false },
                { false, true, false },
        };
    }

    @Test(dataProvider = "setRequestedTimeSlotWithoutTimeSlotViewDP")
    public void testSetRequestedTimeSlotWithVariousTimeSlots(boolean returnTimeSlotView, boolean returnTalTimeSlot,
            boolean expectSettingDefaultTimeSlot) throws FindException {
        LocalDateTime now = LocalDateTime.now();
        FfmProductMapping ffmProductMapping = new FfmProductMappingBuilder()
                .withFfmActivityType(FfmProductMapping.FFM_ACTIVITY_TYPE_NEU_MK_HVT)
                .build();
        doReturn(ffmProductMapping).when(testling).getFfmProductMapping();
        doReturn(now).when(testling).getReferenceDate();
        doNothing().when(testling).setRequestedTimeSlot(REGID_FFM_TIMESLOT_DEFAULT_FROM, REGID_FFM_TIMESLOT_DEFAULT_TO);

        TimeSlotHolderBuilder timeSlotHolderBuilder = new TimeSlotHolderBuilder();
        if (returnTimeSlotView) {
            timeSlotHolderBuilder.withTimeSlotViewBuilder(new TimeSlotViewBuilder()
                    .withDate(now.toLocalDate())
                    .withDaytimeFrom(8, 0)
                    .withDaytimeTo(12, 0));
        }
        else if (returnTalTimeSlot) {
            timeSlotHolderBuilder.withTalRealisierungsZeitfenster(TalRealisierungsZeitfenster.NACHMITTAG)
                    .withTalRealisierungsDay(now.toLocalDate());
        }
        doReturn(timeSlotHolderBuilder.build()).when(baService).getTimeSlotHolder(anyLong());

        testling.setRequestedTimeSlotForNeuAndEntstoerung();
        verify(testling, expectSettingDefaultTimeSlot ? times(1) : never())
                .setRequestedTimeSlot(REGID_FFM_TIMESLOT_DEFAULT_FROM, REGID_FFM_TIMESLOT_DEFAULT_TO);
    }

    @DataProvider
    private Object[][] setRequestedTimeSlotWithoutTimeSlotViewIkAndIntDP() {
        return new Object[][] {
                { FfmProductMapping.FFM_ACTIVITY_TYPE_INT, true, false },
                { FfmProductMapping.FFM_ACTIVITY_TYPE_NEU_IK, true, false },
                { FfmProductMapping.FFM_ACTIVITY_TYPE_NEU_MK_HVT, false, true },
        };
    }

    @Test(dataProvider = "setRequestedTimeSlotWithoutTimeSlotViewIkAndIntDP")
    public void testSetRequestedTimeSlotWithoutTimeSlotViewIkAndInt(String ffmActivityType, boolean expectIkTimeslot, boolean expectDefaultTimeslot) throws FindException {
        FfmProductMapping ffmProductMapping = new FfmProductMappingBuilder().withFfmActivityType(ffmActivityType).build();
        doReturn(ffmProductMapping).when(testling).getFfmProductMapping();
        doReturn(new TimeSlotHolderBuilder().build()).when(baService).getTimeSlotHolder(anyLong());
        doNothing().when(testling).setRequestedTimeSlot(REGID_FFM_TIMESLOT_INT_AND_IK_FROM, REGID_FFM_TIMESLOT_INT_AND_IK_TO);
        doNothing().when(testling).setRequestedTimeSlot(REGID_FFM_TIMESLOT_DEFAULT_FROM, REGID_FFM_TIMESLOT_DEFAULT_TO);

        testling.setRequestedTimeSlotForNeuAndEntstoerung();
        verify(testling).getFfmProductMapping();
        verify(testling, expectIkTimeslot ? times(1) : never())
                .setRequestedTimeSlot(REGID_FFM_TIMESLOT_INT_AND_IK_FROM, REGID_FFM_TIMESLOT_INT_AND_IK_TO);
        verify(testling, expectDefaultTimeslot ? times(1) : never())
                .setRequestedTimeSlot(REGID_FFM_TIMESLOT_DEFAULT_FROM, REGID_FFM_TIMESLOT_DEFAULT_TO);
    }

    @Test
    public void testSetRequestedTimeSlot() throws Exception {
        LocalDateTime timeFrom = LocalDateTime.of(1970, 1, 1, 8, 0, 0, 0);
        LocalDateTime referenceDateWithTimeFrom = LocalDateTime.of(2014, 10, 21, 8, 0, 0, 0);
        LocalDateTime referenceDate = LocalDateTime.of(2014, 10, 21, 0, 0, 0, 0);
        doReturn(referenceDate).when(testling).getReferenceDate();
        doReturn(timeFrom).when(testling).getTimeFromRegistry(REGID_FFM_TIMESLOT_MANUAL_FROM);
        doReturn(referenceDateWithTimeFrom).when(testling).getReferenceDateWithTime(referenceDate, timeFrom);
        LocalDateTime timeTo = LocalDateTime.of(1970, 1, 1, 18, 0, 0, 0);
        LocalDateTime referenceDateWithTimeTo = LocalDateTime.of(2014, 10, 21, 18, 0, 0, 0);
        doReturn(timeTo).when(testling).getTimeFromRegistry(REGID_FFM_TIMESLOT_MANUAL_TO);
        doReturn(referenceDateWithTimeTo).when(testling).getReferenceDateWithTime(referenceDate, timeTo);
        doNothing().when(testling).setRequestedTimeSlot(referenceDateWithTimeFrom, referenceDateWithTimeTo);

        testling.setRequestedTimeSlot(REGID_FFM_TIMESLOT_MANUAL_FROM, REGID_FFM_TIMESLOT_MANUAL_TO);

        verify(testling).getTimeFromRegistry(REGID_FFM_TIMESLOT_MANUAL_FROM);
        verify(testling).getTimeFromRegistry(REGID_FFM_TIMESLOT_MANUAL_TO);
        verify(testling).getReferenceDate();
        verify(testling).getReferenceDateWithTime(referenceDate, timeTo);
        verify(testling).getReferenceDateWithTime(referenceDate, timeFrom);
        verify(testling).setRequestedTimeSlot(referenceDateWithTimeFrom, referenceDateWithTimeTo);
    }

    @DataProvider
    private Object[][] setRequestedTimeSlotDP() {
        return new Object[][] {
                { LocalDateTime.of(2014, 10, 21, 8, 0, 0, 0),
                        LocalDateTime.of(2014, 10, 21, 17, 0, 0, 0), 90,
                        LocalDateTime.of(2014, 10, 21, 8, 0, 0, 0),
                        LocalDateTime.of(2014, 10, 21, 16, 59, 0, 0),
                        LocalDateTime.of(2014, 10, 21, 18, 29, 0, 0) },
                { LocalDateTime.of(2014, 10, 21, 8, 0, 0, 0),
                        LocalDateTime.of(2014, 10, 21, 15, 0, 0, 0), 45,
                        LocalDateTime.of(2014, 10, 21, 8, 0, 0, 0),
                        LocalDateTime.of(2014, 10, 21, 14, 59, 0, 0),
                        LocalDateTime.of(2014, 10, 21, 15, 44, 0, 0) },
        };
    }

    @Test(dataProvider = "setRequestedTimeSlotDP")
    public void testSetRequestedTimeSlot(LocalDateTime from, LocalDateTime to, int plannedDuration,
            LocalDateTime expectedEarliestStart, LocalDateTime expectedLatestStart, LocalDateTime expectedLatestEnd) {
        doReturn(plannedDuration).when(testling).getPlannedDuration();
        testling.setRequestedTimeSlot(from, to);
        verify(testling).getPlannedDuration();
        assertNotNull(workforceOrder.getRequestedTimeSlot());
        RequestedTimeslot requestedTimeslot = workforceOrder.getRequestedTimeSlot();
        Assert.assertEquals(requestedTimeslot.getEarliestStart(), expectedEarliestStart);
        Assert.assertEquals(requestedTimeslot.getLatestStart(), expectedLatestStart);
        Assert.assertEquals(requestedTimeslot.getLatestEnd(), expectedLatestEnd);
    }

    @DataProvider
    private Object[][] getTimeFromRegistryDP() {
        return new Object[][] {
                { "8:00", LocalDateTime.of(1970, 1, 1, 8, 0, 0, 0), false },
                { "08:10", LocalDateTime.of(1970, 1, 1, 8, 10, 0, 0), false },
                { "23:59", LocalDateTime.of(1970, 1, 1, 23, 59, 0, 0), false },
                { "23", null, true },
                { "23:", null, true },
        };
    }

    @Test(dataProvider = "getTimeFromRegistryDP")
    public void testGetTimeFromRegistry(String configuredTime, LocalDateTime expectedResult, boolean expectedExceptoin) throws FindException {
        when(registryService.getStringValue(anyLong())).thenReturn(configuredTime);
        try {
            LocalDateTime timeFromRegistry = testling.getTimeFromRegistry(1L);
            assertFalse(expectedExceptoin);
            assertEquals(expectedResult, timeFromRegistry);
        }
        catch (IllegalArgumentException exc) {
            assertTrue(expectedExceptoin, "IllegalArgumentException has been expected here due to wrong time pattern.");
        }
        verify(registryService).getStringValue(anyLong());
    }

    @DataProvider
    private Object[][] getReferenceDateWithTimeDP() {
        return new Object[][] {
                { LocalDateTime.of(2000, 3, 2, 21, 10, 5, 6),
                        LocalDateTime.of(0, 1, 1, 8, 0, 0, 0),
                        LocalDateTime.of(2000, 3, 2, 8, 0, 0, 0) },
                { LocalDateTime.of(2001, 3, 2, 8, 0, 5, 6),
                        LocalDateTime.of(0, 1, 1, 8, 0, 0, 0),
                        LocalDateTime.of(2001, 3, 2, 8, 0, 0, 0) },
                { LocalDateTime.of(2001, 3, 2, 8, 0, 5, 6),
                        LocalDateTime.of(0, 1, 1, 8, 0, 10, 30),
                        LocalDateTime.of(2001, 3, 2, 8, 0, 0, 0) },
                { LocalDateTime.of(2001, 3, 2, 0, 0, 0, 0),
                        LocalDateTime.of(0, 1, 1, 8, 0, 0, 0),
                        LocalDateTime.of(2001, 3, 2, 8, 0, 0, 0) },
        };
    }

    @Test(dataProvider = "getReferenceDateWithTimeDP")
    public void testGetReferenceDateWithTime(LocalDateTime referenceDate, LocalDateTime time, LocalDateTime expectedResult) {
        assertTrue(testling.getReferenceDateWithTime(referenceDate, time).isEqual(expectedResult));
    }

    /**
     * Testfall: FFM Zeitslot Generierung f&uuml;r einen Wholesale-Auftrag - kein AuftragWholesale Eintrag vorhanden.
     */
    @Test(expectedExceptions = FindException.class)
    public void testSetWorkforceOrderTimeSlot4WholesaleFail() throws Exception {
        final Long auftragId = 123L;

        when(auftragService.findAuftragWholesaleByAuftragId(auftragId)).thenReturn(null);

        testling.setWorkforceOrderTimeSlot4Wholesale(auftragId);
    }

    /**
     * Testfall: FFM Zeitslot Generierung f&uuml;r einen Wholesale-Auftrag - kein Bauauftrag vorhanden.
     */
    @Test
    public void testSetWorkforceOrderTimeSlot4WholesaleSuccessWithoutBauauftrag() throws Exception {
        final Long auftragId = 123L;
        final LocalDate execDate = LocalDate.now();
        final LocalTime execTime = LocalTime.now();
        final AuftragWholesale auftragWholesale = createAuftragWholesale(execDate, execTime);
        final WorkforceOrder current = new WorkforceOrder();

        when(auftragService.findAuftragWholesaleByAuftragId(auftragId)).thenReturn(auftragWholesale);
        when(testling.getBauauftrag()).thenReturn(Optional.empty());
        when(testling.getWorkforceOrder()).thenReturn(current);

        testling.setWorkforceOrderTimeSlot4Wholesale(auftragId);

        assertNotNull(current.getRequestedTimeSlot());
        assertEquals(current.getRequestedTimeSlot().getEarliestStart(), LocalDateTime.of(execDate, execTime));
    }

    /**
     * Testfall: FFM Zeitslot Generierung f&uuml;r einen Wholesale-Auftrag - Bauauftrag vorhanden.
     */
    @Test
    public void testSetWorkforceOrderTimeSlot4WholesaleSuccessWithBauauftrag() throws Exception {
        final Long auftragId = 123L;
        final LocalDate execDate = LocalDate.now();
        final LocalTime execTime = LocalTime.now();
        final AuftragWholesale auftragWholesale = createAuftragWholesale(execDate, execTime);
        final WorkforceOrder current = new WorkforceOrder();
        final VerlaufAbteilung abteilungFfm = new VerlaufAbteilung();
        abteilungFfm.setRealisierungsdatum(DateConverterUtils.asDate(execDate.plusDays(1)));

        when(auftragService.findAuftragWholesaleByAuftragId(auftragId)).thenReturn(auftragWholesale);
        when(testling.getBauauftrag()).thenReturn(Optional.of(new Verlauf()));
        when(testling.getWorkforceOrder()).thenReturn(current);
        when(baService.findVerlaufAbteilung(anyLong(), eq(Abteilung.FFM))).thenReturn(abteilungFfm);

        testling.setWorkforceOrderTimeSlot4Wholesale(auftragId);

        assertNotNull(current.getRequestedTimeSlot());
        assertEquals(current.getRequestedTimeSlot().getEarliestStart(), LocalDateTime.of(execDate.plusDays(1), execTime));
    }

    private AuftragWholesale createAuftragWholesale(final LocalDate execDate, final LocalTime execTime) {
        final AuftragWholesale auftragWholesale = new AuftragWholesale();
        auftragWholesale.setExecutionDate(execDate);
        auftragWholesale.setExecutionTimeBegin(execTime);
        auftragWholesale.setExecutionTimeEnd(execTime);
        return auftragWholesale;
    }

}
