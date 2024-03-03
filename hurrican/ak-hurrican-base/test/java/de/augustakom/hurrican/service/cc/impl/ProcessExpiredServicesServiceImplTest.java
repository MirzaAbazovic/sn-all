/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2015
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.Sets;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.exceptions.ExpiredServicesException;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBuilder;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.view.LeistungsDiffView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CreateVerlaufParameter;
import de.mnet.common.tools.DateConverterUtils;

@Test(groups = BaseTest.UNIT)
public class ProcessExpiredServicesServiceImplTest extends BaseTest {

    @Mock
    private CCLeistungsService ccLeistungsService;
    @Mock
    BillingAuftragService billingAuftragService;
    @Mock
    CCAuftragService ccAuftragService;
    @Mock
    BAService baService;

    @InjectMocks
    @Spy
    private ProcessExpiredServicesServiceImpl testling;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    public void testGetExtLeistungNosForExpireServices() throws FindException {
        TechLeistung autoExpireTechLs = new TechLeistungBuilder()
                .withAutoExpire(Boolean.TRUE).withExternLeistungNo(1L).setPersist(false).build();
        TechLeistung autoExpireTechLsWithoutExtLstNo = new TechLeistungBuilder()
                .withAutoExpire(Boolean.TRUE).withExternLeistungNo(null).setPersist(false).build();
        TechLeistung noAutoExpireTechLs = new TechLeistungBuilder()
                .withAutoExpire(Boolean.FALSE).withExternLeistungNo(2L).setPersist(false).build();

        when(ccLeistungsService.findTechLeistungen(true)).thenReturn(
                Arrays.asList(autoExpireTechLs, autoExpireTechLsWithoutExtLstNo, noAutoExpireTechLs));

        Set<Long> result = testling.getExtLeistungNosForExpireServices();
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertTrue(result.contains(autoExpireTechLs.getExternLeistungNo()));
    }


    @Test(expectedExceptions = ExpiredServicesException.class, expectedExceptionsMessageRegExp = "Es wurden keine 'AUTO_EXPIRE' Leistungen gefunden!")
    public void testProcessExpiredServicesFailureNoExpireServices() throws FindException {
        when(ccLeistungsService.findTechLeistungen(true)).thenReturn(Collections.emptyList());
        testling.processExpiredServices(1L);
    }

    public void testProcessExpiredServicesFailureNoHurricanOrder() throws FindException {
        AuftragDaten inActiveOrder = new AuftragDatenBuilder()
                .withAuftragNoOrig(99L)
                .withStatusId(AuftragStatus.TECHNISCHE_REALISIERUNG)
                .setPersist(false).build();

        BAuftrag bAuftrag = new BAuftragBuilder()
                .withAuftragNoOrig(99L)
                .withGueltigBis(DateTools.getBillingEndDate())
                .setPersist(false).build();

        doReturn(Sets.newHashSet(10000L)).when(testling).getExtLeistungNosForExpireServices();
        when(billingAuftragService.findAuftragNoOrigsWithExtLeistungNos(any(), any(), anyInt()))
                .thenReturn(Sets.newHashSet(inActiveOrder.getAuftragNoOrig()));
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(inActiveOrder.getAuftragNoOrig()))
                .thenReturn(Arrays.asList(inActiveOrder));
        when(billingAuftragService.findAuftrag(any())).thenReturn(bAuftrag);

        AKWarnings result = testling.processExpiredServices(1L);
        assertNotNull(result);
        assertEquals(result.getAKMessages().size(), 1);
        assertTrue(result.getWarningsAsText()
                .contains("konnte kein Hurrican Auftrag im Status 'in Betrieb'/'Aenderung' gefunden werden"));
    }

    public void testProcessExpiredServicesNoFailureOrderCanceled() throws FindException {
        AuftragDaten inActiveOrder = new AuftragDatenBuilder()
                .withAuftragNoOrig(99L)
                .withGueltigBis(DateConverterUtils.asDate(LocalDate.now().minusDays(1)))
                .withStatusId(AuftragStatus.KUENDIGUNG_TECHN_REAL)
                .setPersist(false).build();

        BAuftrag bAuftrag = new BAuftragBuilder()
                .withAuftragNoOrig(99L)
                .withGueltigBis(DateConverterUtils.asDate(LocalDate.now().minusDays(1)))
                .withAtyp(BillingConstants.ATYP_KUEND)
                .setPersist(false).build();

        doReturn(Sets.newHashSet(10000L)).when(testling).getExtLeistungNosForExpireServices();
        when(billingAuftragService.findAuftragNoOrigsWithExtLeistungNos(any(), any(), anyInt()))
                .thenReturn(Sets.newHashSet(inActiveOrder.getAuftragNoOrig()));
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(inActiveOrder.getAuftragNoOrig()))
                .thenReturn(Arrays.asList(inActiveOrder));
        when(billingAuftragService.findAuftrag(any())).thenReturn(bAuftrag);

        AKWarnings result = testling.processExpiredServices(1L);
        assertNotNull(result);
        assertEquals(result.getAKMessages().size(), 0);
    }

    public void testProcessExpiredServicesFailureNoUniqueHurricanOrder() throws FindException {
        AuftragDaten activeOrder1 = new AuftragDatenBuilder()
                .withAuftragNoOrig(99L)
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .setPersist(false).build();
        AuftragDaten activeOrder2 = new AuftragDatenBuilder()
                .withAuftragNoOrig(99L)
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .setPersist(false).build();

        doReturn(Sets.newHashSet(10000L)).when(testling).getExtLeistungNosForExpireServices();
        when(billingAuftragService.findAuftragNoOrigsWithExtLeistungNos(any(), any(), anyInt()))
                .thenReturn(Sets.newHashSet(activeOrder1.getAuftragNoOrig()));
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(activeOrder1.getAuftragNoOrig()))
                .thenReturn(Arrays.asList(activeOrder1, activeOrder2));

        AKWarnings result = testling.processExpiredServices(1L);
        assertNotNull(result);
        assertEquals(result.getAKMessages().size(), 1);
        assertTrue(result.getWarningsAsText()
                .contains("konnte kein eindeutiger Hurrican Auftrag im Status 'in Betrieb'/'Aenderung' gefunden werden"));
    }

    public void testProcessExpiredServicesSuccess() throws FindException, StoreException {
        AuftragDaten activeOrder = new AuftragDatenBuilder()
                .withAuftragNoOrig(99L)
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .setPersist(false).build();
        AuftragDaten anotherActiveOrder = new AuftragDatenBuilder()
                .withAuftragNoOrig(100L)
            .withStatusId(AuftragStatus.AENDERUNG)
                .setPersist(false).build();

        doReturn(true).when(testling).hasLeistungsDiff(any(AuftragDaten.class));
        doReturn(Sets.newHashSet(10000L)).when(testling).getExtLeistungNosForExpireServices();
        when(billingAuftragService.findAuftragNoOrigsWithExtLeistungNos(any(), any(), anyInt()))
                .thenReturn(Sets.newHashSet(activeOrder.getAuftragNoOrig(), anotherActiveOrder.getAuftragNoOrig()));
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(activeOrder.getAuftragNoOrig()))
                .thenReturn(Arrays.asList(activeOrder));
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(anotherActiveOrder.getAuftragNoOrig()))
                .thenReturn(Arrays.asList(anotherActiveOrder));

        AKWarnings result = testling.processExpiredServices(1L);
        assertNotNull(result);
        assertEquals(result.getAKMessages().size(), 0);
        verify(testling).hasLeistungsDiff(activeOrder);
        verify(testling).hasLeistungsDiff(anotherActiveOrder);

        final ArgumentCaptor<AuftragDaten> auftragDatenCaptor = ArgumentCaptor.forClass(AuftragDaten.class);
        verify(ccAuftragService, times(2)).saveAuftragDatenNoTx(auftragDatenCaptor.capture());
        assertThat(auftragDatenCaptor.getValue().getStatusId(), equalTo(AuftragStatus.AENDERUNG));

        final ArgumentCaptor<CreateVerlaufParameter> verlaufParamsCaptor =
                ArgumentCaptor.forClass(CreateVerlaufParameter.class);
        verify(baService, times(2)).createVerlaufNewTx(verlaufParamsCaptor.capture());
        assertThat(verlaufParamsCaptor.getValue().getAnlass(), equalTo(BAVerlaufAnlass.AUTO_DOWNGRADE));

        Date nextWorkingDay = Date.from(DateCalculationHelper.asWorkingDay(LocalDate.now().plusDays(1)).atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertThat(verlaufParamsCaptor.getValue().getRealisierungsTermin(), equalTo(nextWorkingDay));
    }



    @DataProvider(name = "hasLeistungsDiffDP")
    public Object[][] hasLeistungsDiffDP() {
        return new Object[][] {
                { null, false },
                { Collections.emptyList(), false },
                { Arrays.asList(new LeistungsDiffView(1L, 1L)), true }
        };
    }

    @Test(dataProvider = "hasLeistungsDiffDP")
    public void testHasLeistungsDiff(List<LeistungsDiffView> diffs, boolean expectDiff) throws FindException {
        when(ccLeistungsService.findLeistungsDiffs(anyLong(), anyLong(), anyLong())).thenReturn(diffs);
        assertEquals(testling.hasLeistungsDiff(new AuftragDaten()), expectDiff);
    }

}
