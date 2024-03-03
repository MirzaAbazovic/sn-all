/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.03.14
 */
package de.mnet.hurrican.scheduler.job.wita;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.quartz.JobExecutionContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.CreateVerlaufParameter;
import de.augustakom.hurrican.service.elektra.ElektraFacadeService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaTalOrderService;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class AbstractProcessWitaResponseJobTest {

    @Mock
    private BillingAuftragService billingAuftragService;
    @Mock
    private BAService baService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private CarrierService carrierService;
    @Mock
    private CarrierElTALService carrierElTalService;
    @Mock
    private ElektraFacadeService elektraFacadeService;
    @Mock
    private WitaTalOrderService witaTalOrderService;

    @Spy
    private AbstractProcessWitaResponseJob underTest;

    @BeforeMethod
    public void beforeMethod() throws Exception {
        underTest = new AutomaticallyProcessWitaOrdersJob();
        MockitoAnnotations.initMocks(this);
        initMocks(underTest);
    }

    private void initMocks(AbstractProcessWitaResponseJob underTest) {
        underTest.billingAuftragService = billingAuftragService;
        underTest.baService = baService;
        underTest.auftragService = auftragService;
        underTest.carrierService = carrierService;
        underTest.carrierElTalService = carrierElTalService;
        underTest.elektraFacadeService = elektraFacadeService;
        underTest.witaTalOrderService = witaTalOrderService;
    }

    @DataProvider
    public Object[][] canBothOrdersBeClosedDP() {
        // @formatter:off
        return new Object[][] {
                { true,  false, true,  false, CBVorgang.STATUS_ANSWERED,    true },
                { false, false, true,  false, CBVorgang.STATUS_ANSWERED,    false},
                { true,  true,  true,  false, CBVorgang.STATUS_ANSWERED,    false},
                { true,  false, false, false, CBVorgang.STATUS_ANSWERED,    false},
                { true,  false, true,  true,  CBVorgang.STATUS_ANSWERED,    false},

                { true,  false, false, false, CBVorgang.STATUS_CLOSED,      true },
                { true,  true,  false, false, CBVorgang.STATUS_CLOSED,      false},
                { false, false, false, false, CBVorgang.STATUS_CLOSED,      false},
        };
        // @formatter:on
    }

    @Test(dataProvider = "canBothOrdersBeClosedDP")
    public void testCanBothOrdersBeClosed(boolean automationPossible, boolean hasAutomationErrors,
            boolean automationPossibleRef, boolean hasAutomationErrorsRef,
            Long cbVorgangRefStatus, boolean expected) throws FindException {
        final WitaCBVorgang witaCBVorgang = mock(WitaCBVorgang.class);
        when(witaCBVorgang.hasAutomationErrors()).thenReturn(hasAutomationErrors);
        final WitaCBVorgang witaCBVorgangRef = mock(WitaCBVorgang.class);
        when(witaCBVorgangRef.hasAutomationErrors()).thenReturn(hasAutomationErrorsRef);
        when(witaCBVorgangRef.getStatus()).thenReturn(cbVorgangRefStatus);

        when(witaTalOrderService.isAutomationAllowed(witaCBVorgang)).thenReturn(automationPossible);
        when(witaTalOrderService.isAutomationAllowed(witaCBVorgangRef)).thenReturn(automationPossibleRef);

        assertEquals(underTest.canBothOrdersBeClosed(witaCBVorgang, witaCBVorgangRef), expected);

        verify(witaTalOrderService).isAutomationAllowed(witaCBVorgang);
        final boolean automationAllowedCalled = automationPossible && !hasAutomationErrors
                && !CBVorgang.STATUS_CLOSED.equals(cbVorgangRefStatus);
        verify(witaTalOrderService, times(automationAllowedCalled ? 1 : 0)).isAutomationAllowed(witaCBVorgangRef);
    }

    @Test
    public void testProcessOrdersKueHvtKvzAutomationAllowed() throws StoreException, FindException, ValidationException {
        final JobExecutionContext jobExecutionContext = mock(JobExecutionContext.class);
        final Long[] cbVorgangTypes = { CBVorgang.TYP_KUENDIGUNG };
        doReturn(cbVorgangTypes).when(underTest).getCbVorgangTypes();
        final WitaCBVorgang kue_hvt_kvz = new WitaCBVorgangBuilder()
                .withTyp(CBVorgang.TYP_KUENDIGUNG)
                .withAuftragId(1L)
                .build();
        final List<WitaCBVorgang> witaCBVorgangs = Arrays.asList(kue_hvt_kvz);
        final long cbVorgangRefId = 1L;
        final WitaCBVorgang neu_hvt_kvz = new WitaCBVorgangBuilder()
                .withTyp(CBVorgang.TYP_NEU)
                .withAuftragId(2L)
                .build();

        when(witaTalOrderService.findWitaCBVorgaengeForAutomation(cbVorgangTypes)).thenReturn(witaCBVorgangs);
        doReturn(cbVorgangRefId).when(underTest).getCbVorgangRefId(kue_hvt_kvz);
        when(carrierElTalService.findCBVorgang(cbVorgangRefId)).thenReturn(neu_hvt_kvz);
        doReturn(true).when(underTest).canBothOrdersBeClosed(kue_hvt_kvz, neu_hvt_kvz);
        doNothing().when(underTest).closeWitaAutomatically(kue_hvt_kvz, jobExecutionContext);

        underTest.processOrders(jobExecutionContext);

        verify(witaTalOrderService).findWitaCBVorgaengeForAutomation(cbVorgangTypes);
        verify(underTest).getCbVorgangRefId(kue_hvt_kvz);
        verify(carrierElTalService).findCBVorgang(cbVorgangRefId);
        verify(underTest).canBothOrdersBeClosed(kue_hvt_kvz, neu_hvt_kvz);
        verify(underTest).closeWitaAutomatically(kue_hvt_kvz, jobExecutionContext);
        verify(underTest, never()).sendMailWithWarningsAndErrors(any(JobExecutionContext.class),
                any(StringBuilder.class), any(StringBuilder.class), anyBoolean());
    }

    @Test
    public void testProcessOrdersKueHvtKvzAutomationNotAllowed() throws StoreException, FindException,
            ValidationException {
        final JobExecutionContext jobExecutionContext = mock(JobExecutionContext.class);
        final Long[] cbVorgangTypes = { CBVorgang.TYP_KUENDIGUNG };
        doReturn(cbVorgangTypes).when(underTest).getCbVorgangTypes();
        final WitaCBVorgang kue_hvt_kvz = new WitaCBVorgangBuilder()
                .withTyp(CBVorgang.TYP_KUENDIGUNG)
                .build();
        final List<WitaCBVorgang> witaCBVorgangs = Arrays.asList(kue_hvt_kvz);
        final long cbVorgangRefId = 1L;
        final WitaCBVorgang neu_hvt_kvz = new WitaCBVorgangBuilder()
                .withTyp(CBVorgang.TYP_NEU)
                .build();

        when(witaTalOrderService.findWitaCBVorgaengeForAutomation(cbVorgangTypes)).thenReturn(witaCBVorgangs);
        doReturn(cbVorgangRefId).when(underTest).getCbVorgangRefId(kue_hvt_kvz);
        when(carrierElTalService.findCBVorgang(cbVorgangRefId)).thenReturn(neu_hvt_kvz);
        doReturn(false).when(underTest).canBothOrdersBeClosed(kue_hvt_kvz, neu_hvt_kvz);

        underTest.processOrders(jobExecutionContext);

        verify(witaTalOrderService).findWitaCBVorgaengeForAutomation(cbVorgangTypes);
        verify(underTest).getCbVorgangRefId(kue_hvt_kvz);
        verify(carrierElTalService).findCBVorgang(cbVorgangRefId);
        verify(underTest).canBothOrdersBeClosed(kue_hvt_kvz, neu_hvt_kvz);
        verify(underTest, never()).closeWitaAutomatically(kue_hvt_kvz, jobExecutionContext);
        verify(underTest).sendMailWithWarningsAndErrors(any(JobExecutionContext.class),
                any(StringBuilder.class), any(StringBuilder.class), anyBoolean());
    }

    @Test
    public void testProcessOrdersExceptionThrown() throws StoreException, FindException, ValidationException {
        final JobExecutionContext jobExecutionContext = mock(JobExecutionContext.class);
        final Long[] cbVorgangTypes = { CBVorgang.TYP_KUENDIGUNG };
        doReturn(cbVorgangTypes).when(underTest).getCbVorgangTypes();
        final WitaCBVorgang kue_hvt_kvz = new WitaCBVorgangBuilder()
                .withTyp(CBVorgang.TYP_KUENDIGUNG)
                .build();

        when(witaTalOrderService.findWitaCBVorgaengeForAutomation(cbVorgangTypes)).thenReturn(
                Arrays.asList(kue_hvt_kvz));
        doThrow(Exception.class).when(underTest).getCbVorgangRefId(kue_hvt_kvz);

        underTest.processOrders(jobExecutionContext);

        verify(witaTalOrderService).findWitaCBVorgaengeForAutomation(cbVorgangTypes);
        verify(underTest).getCbVorgangRefId(kue_hvt_kvz);
        verify(underTest, never()).closeWitaAutomatically(kue_hvt_kvz, jobExecutionContext);
        verify(underTest).sendMailWithWarningsAndErrors(any(JobExecutionContext.class),
                any(StringBuilder.class), any(StringBuilder.class), anyBoolean());
    }

    @Test
    public void testProcessOrdersKueAutomationAllowed() throws StoreException, FindException, ValidationException {
        final JobExecutionContext jobExecutionContext = mock(JobExecutionContext.class);
        final Long[] cbVorgangTypes = { CBVorgang.TYP_KUENDIGUNG };
        doReturn(cbVorgangTypes).when(underTest).getCbVorgangTypes();
        final WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder()
                .withTyp(CBVorgang.TYP_KUENDIGUNG)
                .withAuftragId(1L)
                .build();

        when(witaTalOrderService.findWitaCBVorgaengeForAutomation(cbVorgangTypes)).thenReturn(
                Arrays.asList(witaCBVorgang));
        doReturn(null).when(underTest).getCbVorgangRefId(witaCBVorgang);
        when(witaTalOrderService.isAutomationAllowed(witaCBVorgang)).thenReturn(true);
        doNothing().when(underTest).closeWitaAutomatically(witaCBVorgang, jobExecutionContext);

        underTest.processOrders(jobExecutionContext);

        verify(witaTalOrderService).findWitaCBVorgaengeForAutomation(cbVorgangTypes);
        verify(underTest).getCbVorgangRefId(witaCBVorgang);
        verify(carrierElTalService, never()).findCBVorgang(anyLong());
        verify(witaTalOrderService).isAutomationAllowed(witaCBVorgang);
        verify(underTest).closeWitaAutomatically(witaCBVorgang, jobExecutionContext);
        verify(underTest, never()).sendMailWithWarningsAndErrors(any(JobExecutionContext.class),
                any(StringBuilder.class), any(StringBuilder.class), anyBoolean());
    }

    @Test
    public void testProcessOrdersKueAutomationNotAllowed() throws StoreException, FindException, ValidationException {
        final JobExecutionContext jobExecutionContext = mock(JobExecutionContext.class);
        final Long[] cbVorgangTypes = { CBVorgang.TYP_KUENDIGUNG };
        doReturn(cbVorgangTypes).when(underTest).getCbVorgangTypes();
        final WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder()
                .withTyp(CBVorgang.TYP_KUENDIGUNG)
                .build();

        when(witaTalOrderService.findWitaCBVorgaengeForAutomation(cbVorgangTypes)).thenReturn(
                Arrays.asList(witaCBVorgang));
        doReturn(null).when(underTest).getCbVorgangRefId(witaCBVorgang);
        when(witaTalOrderService.isAutomationAllowed(witaCBVorgang)).thenReturn(false);

        underTest.processOrders(jobExecutionContext);

        verify(witaTalOrderService).findWitaCBVorgaengeForAutomation(cbVorgangTypes);
        verify(underTest).getCbVorgangRefId(witaCBVorgang);
        verify(carrierElTalService, never()).findCBVorgang(anyLong());
        verify(witaTalOrderService).isAutomationAllowed(witaCBVorgang);
        verify(underTest, never()).closeWitaAutomatically(witaCBVorgang, jobExecutionContext);
        verify(underTest).sendMailWithWarningsAndErrors(any(JobExecutionContext.class),
                any(StringBuilder.class), any(StringBuilder.class), anyBoolean());
    }

    @Test
    public void testWriteAutomationError() throws StoreException {
        final WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder().build();

        final StringBuilder occuredErrors = new StringBuilder();
        underTest.writeAutomationError(witaCBVorgang, new Exception(), occuredErrors);

        assertTrue(occuredErrors.length() == 0);
        assertFalse(witaCBVorgang.getAutomationErrors().isEmpty());
        verify(carrierElTalService).saveCBVorgang(any(WitaCBVorgang.class));
    }

    @Test
    public void testWriteAutomationErrorThrowsException() throws StoreException {
        final WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder().build();

        final StringBuilder occuredErrors = new StringBuilder();
        when(carrierElTalService.saveCBVorgang(any(WitaCBVorgang.class))).thenThrow(StoreException.class);
        underTest.writeAutomationError(witaCBVorgang, new Exception(), occuredErrors);

        assertTrue(occuredErrors.toString().contains("Error writing AutomationError for WitaCbVorgang with Id"));
        assertFalse(witaCBVorgang.getAutomationErrors().isEmpty());
        verify(carrierElTalService).saveCBVorgang(any(WitaCBVorgang.class));
    }

    @Test
    public void testGetCbVorgangRefIdNeu() throws StoreException {
        final WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder()
                .withTyp(CBVorgang.TYP_NEU)
                .withCbVorgangRefId(1L)
                .build();

        assertEquals(underTest.getCbVorgangRefId(witaCBVorgang), new Long(1));
        verify(witaTalOrderService, never()).findWitaCBVorgangByRefId(anyLong());
    }

    @Test
    public void testGetCbVorgangRefIdKueWithReference() throws StoreException {
        final WitaCBVorgang witaCBVorgangKue = new WitaCBVorgangBuilder()
                .withTyp(CBVorgang.TYP_KUENDIGUNG)
                .withId(1L)
                .build();

        final WitaCBVorgang witaCBVorgangNeu = new WitaCBVorgangBuilder()
                .withTyp(CBVorgang.TYP_NEU)
                .withId(2L)
                .build();
        when(witaTalOrderService.findWitaCBVorgangByRefId(1L)).thenReturn(witaCBVorgangNeu);

        assertEquals(underTest.getCbVorgangRefId(witaCBVorgangKue), new Long(2));
        verify(witaTalOrderService).findWitaCBVorgangByRefId(1L);
    }

    @Test
    public void testGetCbVorgangRefIdKueWithoutReference() throws StoreException {
        final WitaCBVorgang witaCBVorgangKue = new WitaCBVorgangBuilder()
                .withTyp(CBVorgang.TYP_KUENDIGUNG)
                .withId(1L)
                .build();

        when(witaTalOrderService.findWitaCBVorgangByRefId(1L)).thenReturn(null);
        assertNull(underTest.getCbVorgangRefId(witaCBVorgangKue));
        verify(witaTalOrderService).findWitaCBVorgangByRefId(1L);
    }

    @Test
    public void testGetCbVorgangRefIdOther() throws StoreException {
        final WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder()
                .withTyp(CBVorgang.TYP_ANBIETERWECHSEL)
                .withId(1L)
                .build();

        assertNull(underTest.getCbVorgangRefId(witaCBVorgang));
        verify(witaTalOrderService, never()).findWitaCBVorgangByRefId(anyLong());
    }

    @Test
    public void testProcessOrderAndSwitchAuftragsart() throws FindException,StoreException, ValidationException {
        final JobExecutionContext jobExecutionContext = mock(JobExecutionContext.class);
        final WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder()
                .withTyp(CBVorgang.TYP_ANBIETERWECHSEL)
                .withAnbieterwechselTkg46(Boolean.TRUE)
                .withReturnRealDate(DateConverterUtils.asDate(LocalDate.now()))
                .withAutomation(Boolean.TRUE)
                .withId(1L)
                .build();

        final AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withId(1L)
                .withAuftragId(1L)
                .withAuftragNoOrig(1L)
                .build();

        final AuftragTechnik auftragTechnik = new AuftragTechnikBuilder()
                .withAuftragart(BAVerlaufAnlass.NEUSCHALTUNG)
                .withId(1L)
                .build();

        when(auftragService.findAuftragDatenByAuftragId(anyLong())).thenReturn(auftragDaten);
        when(auftragService.findAuftragTechnikByAuftragId(anyLong())).thenReturn(auftragTechnik);
        when(baService.createVerlauf(any(CreateVerlaufParameter.class))).thenReturn(Pair.create(new Verlauf(), new AKWarnings()));
        when(carrierElTalService.findCBVorgang(witaCBVorgang.getId())).thenReturn(witaCBVorgang);
        underTest.closeWitaAutomatically(witaCBVorgang, jobExecutionContext);

        assertEquals(auftragTechnik.getAuftragsart(), BAVerlaufAnlass.ABW_TKG46_NEUSCHALTUNG);
    }

}
