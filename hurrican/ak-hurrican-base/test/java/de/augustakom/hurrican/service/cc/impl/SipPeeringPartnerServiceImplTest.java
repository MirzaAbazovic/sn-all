package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.dao.cc.Auftrag2PeeringPartnerDAO;
import de.augustakom.hurrican.model.cc.Auftrag2PeeringPartner;
import de.augustakom.hurrican.model.cc.Auftrag2PeeringPartnerBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartnerBuilder;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSet;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSetBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;

@Test(groups = BaseTest.UNIT)
public class SipPeeringPartnerServiceImplTest extends BaseTest {

    @Mock
    private Auftrag2PeeringPartnerDAO auftrag2PeeringPartnerDAO;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private CPSService cpsService;
    
    @InjectMocks
    @Spy
    private SipPeeringPartnerServiceImpl testling;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider(name = "testSaveAuftrag2PeeringPartnerButCheckOverlappingDP")
    public Object[][] testSaveAuftrag2PeeringPartnerButCheckOverlappingDP() {
        LocalDateTime left = LocalDate.now().minusDays(5).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime middle = LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
        Auftrag2PeeringPartner rangeClosed = new Auftrag2PeeringPartnerBuilder()
                .withId(1L)
                .withGueltigVon(Date.from(left.atZone(ZoneId.systemDefault()).toInstant()))
                .withGueltigBis(Date.from(middle.minusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        Auftrag2PeeringPartner rangeEmpty = new Auftrag2PeeringPartnerBuilder()
                .withId(2L)
                .withGueltigVon(Date.from(middle.atZone(ZoneId.systemDefault()).toInstant()))
                .withGueltigBis(Date.from(middle.atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        Auftrag2PeeringPartner rangeFromNow = new Auftrag2PeeringPartnerBuilder()
                .withId(3L)
                .withGueltigVon(Date.from(middle.atZone(ZoneId.systemDefault()).toInstant()))
                .withGueltigBis(DateTools.getHurricanEndDate())
                .build();
        List<Auftrag2PeeringPartner> auftrag2PeeringPartners = Arrays.asList(rangeClosed, rangeEmpty, rangeFromNow);
        Auftrag2PeeringPartner toCheckTightenedRange = new Auftrag2PeeringPartnerBuilder()
                .withId(1L) // refers to rangeClosed
                .withGueltigVon(Date.from(left.atZone(ZoneId.systemDefault()).toInstant()))
                .withGueltigBis(Date.from(middle.minusDays(2).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        Auftrag2PeeringPartner toCheckOverlapping = new Auftrag2PeeringPartnerBuilder()
                .withId(2L) // refers to rangeEmpty
                .withGueltigVon(Date.from(middle.atZone(ZoneId.systemDefault()).toInstant()))
                .withGueltigBis(Date.from(middle.plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        return new Object[][] {
                { auftrag2PeeringPartners, toCheckTightenedRange, false },
                { auftrag2PeeringPartners, toCheckOverlapping, true },
        };
    }

    @Test(dataProvider = "testSaveAuftrag2PeeringPartnerButCheckOverlappingDP")
    public void testSaveAuftrag2PeeringPartnerButCheckOverlapping(List<Auftrag2PeeringPartner> auftrag2PeeringPartners,
            Auftrag2PeeringPartner toCheck, boolean exceptionExpected) throws FindException {
        doReturn(auftrag2PeeringPartners).when(testling)
                .findAuftragPeeringPartners(anyLong());
        try {
            testling.saveAuftrag2PeeringPartnerButCheckOverlapping(toCheck);
        }
        catch (StoreException e) {
            if (!exceptionExpected) {
                fail();
            }
            return;
        }
        if (exceptionExpected) {
            fail();
        }
    }
    
    @Test(expectedExceptions = StoreException.class,
            expectedExceptionsMessageRegExp = "Es ist bereits ein Peering Partner ab bzw. nach dem .* zugeordnet. Bitte Peering Partner beenden oder Startdatum weiter in die Zukunft anpassen!")
    public void addPeeringPartnerWithPPInFutureAlreadyAssigned() throws StoreException {
        Auftrag2PeeringPartner a2pp = new Auftrag2PeeringPartnerBuilder()
                .withGueltigVon(Date.from(LocalDateTime.now().plusDays(10).atZone(ZoneId.systemDefault()).toInstant()))
                .withGueltigBis(DateTools.getHurricanEndDate())
                .setPersist(false).build();

        when(auftrag2PeeringPartnerDAO.findByAuftragId(Matchers.anyLong())).thenReturn(Arrays.asList(a2pp));

        testling.addAuftrag2PeeringPartner(1L, 2L, new Date());
    }


    @Test
    public void addPeeringPartner() throws StoreException {
        Auftrag2PeeringPartner a2pp = new Auftrag2PeeringPartnerBuilder()
                .withGueltigVon(Date.from(LocalDateTime.now().minusDays(100).atZone(ZoneId.systemDefault()).toInstant()))
                .withGueltigBis(DateTools.getHurricanEndDate())
                .setPersist(false).build();

        when(auftrag2PeeringPartnerDAO.findByAuftragId(Matchers.anyLong())).thenReturn(null);
        when(auftrag2PeeringPartnerDAO.findValidAuftrag2PeeringPartner(Matchers.anyLong(), Matchers.any(Date.class)))
                .thenReturn(Arrays.asList(a2pp));

        testling.addAuftrag2PeeringPartner(1L, 2L, new Date());

        verify(testling).deactivateAuftrag2PeeringPartner(Matchers.eq(a2pp), Matchers.any(Date.class));
        verify(auftrag2PeeringPartnerDAO, times(2)).store(Matchers.any(Auftrag2PeeringPartner.class));
    }


    @Test
    public void findActiveOrdersAssignedToPeeringPartner() throws FindException {
        Date past = Date.from(LocalDateTime.now().minusYears(2).atZone(ZoneId.systemDefault()).toInstant());

        AuftragDaten activeOrder = new AuftragDatenBuilder().withRandomAuftragId()
                .withInbetriebnahme(past).withStatusId(AuftragStatus.IN_BETRIEB).build();
        AuftragDaten cancelledOrder = new AuftragDatenBuilder().withRandomAuftragId()
                .withInbetriebnahme(past).withKuendigung(Date.from(LocalDateTime.now().minusYears(1).atZone(ZoneId.systemDefault()).toInstant()))
                .withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT).build();
        AuftragDaten inCancellationOrder = new AuftragDatenBuilder().withRandomAuftragId()
                .withInbetriebnahme(past).withStatusId(AuftragStatus.KUENDIGUNG_ERFASSEN)
                .withKuendigung(Date.from(LocalDateTime.now().plusDays(100).atZone(ZoneId.systemDefault()).toInstant())).build();
        AuftragDaten stornoOrder = new AuftragDatenBuilder().withRandomAuftragId()
                .withStatusId(AuftragStatus.STORNO).build();

        Auftrag2PeeringPartner active = new Auftrag2PeeringPartnerBuilder().withAuftragId(activeOrder.getAuftragId()).build();
        Auftrag2PeeringPartner cancelled = new Auftrag2PeeringPartnerBuilder().withAuftragId(cancelledOrder.getAuftragId()).build();
        Auftrag2PeeringPartner inCancellation = new Auftrag2PeeringPartnerBuilder().withAuftragId(inCancellationOrder.getAuftragId()).build();
        Auftrag2PeeringPartner storno = new Auftrag2PeeringPartnerBuilder().withAuftragId(stornoOrder.getAuftragId()).build();

        when(auftrag2PeeringPartnerDAO.findAuftrag2PeeringPartner(anyLong(), any(Date.class))).thenReturn(
                Arrays.asList(active, cancelled, inCancellation, storno));

        when(auftragService.findAuftragDatenByAuftragId(activeOrder.getAuftragId())).thenReturn(activeOrder);
        when(auftragService.findAuftragDatenByAuftragId(cancelledOrder.getAuftragId())).thenReturn(cancelledOrder);
        when(auftragService.findAuftragDatenByAuftragId(inCancellationOrder.getAuftragId())).thenReturn(inCancellationOrder);
        when(auftragService.findAuftragDatenByAuftragId(stornoOrder.getAuftragId())).thenReturn(stornoOrder);

        List<AuftragDaten> result = testling.findActiveOrdersAssignedToPeeringPartner(1L, new Date());
        assertNotEmpty(result);
        assertEquals(result.size(), 2);
        assertTrue(result.contains(activeOrder));
        assertTrue(result.contains(inCancellationOrder));
    }


    @Test
    public void createAndSendCpsTxForAssignedOrders() throws FindException, StoreException {
        AuftragDaten adCpsSuccess = new AuftragDatenBuilder().withRandomAuftragId().build();
        AuftragDaten adCpsWarnings = new AuftragDatenBuilder().withRandomAuftragId().build();
        AuftragDaten adCpsNotAllowed = new AuftragDatenBuilder().withRandomAuftragId().build();

        doReturn(Arrays.asList(adCpsSuccess, adCpsWarnings, adCpsNotAllowed)).when(testling)
                .findActiveOrdersAssignedToPeeringPartner(anyLong(), any(Date.class));

        when(cpsService.isCPSProvisioningAllowed(adCpsSuccess.getAuftragId(), null, false, false, true))
                .thenReturn(new CPSProvisioningAllowed(true, null));
        when(cpsService.isCPSProvisioningAllowed(adCpsWarnings.getAuftragId(), null, false, false, true))
                .thenReturn(new CPSProvisioningAllowed(true, null));
        when(cpsService.isCPSProvisioningAllowed(adCpsNotAllowed.getAuftragId(), null, false, false, true))
                .thenReturn(new CPSProvisioningAllowed(false, "test"));

        when(cpsService.createCPSTransaction(argThat(new CreateCpsTxParameterMatcher(adCpsSuccess.getAuftragId()))))
                .thenReturn(new CPSTransactionResult(Arrays.asList(new CPSTransaction()), new AKWarnings()));

        when(cpsService.createCPSTransaction(argThat(new CreateCpsTxParameterMatcher(adCpsWarnings.getAuftragId()))))
                .thenReturn(new CPSTransactionResult(Arrays.asList(new CPSTransaction()),
                        new AKWarnings().addAKWarning(this, "warning")));

        verify(cpsService, never())
                .createCPSTransaction(argThat(new CreateCpsTxParameterMatcher(adCpsNotAllowed.getAuftragId())));

        AKWarnings result = testling.createAndSendCpsTxForAssignedOrders(1L, new Date(), 99L);
        assertNotNull(result);
        assertTrue(result.getWarningsAsText().contains(String.format("%s", adCpsWarnings.getAuftragId())));
        assertTrue(result.getWarningsAsText().contains(String.format("%s", adCpsNotAllowed.getAuftragId())));
        assertFalse(result.getWarningsAsText().contains(String.format("%s", adCpsSuccess.getAuftragId())));

        verify(cpsService, times(2)).sendCPSTx2CPS(any(CPSTransaction.class), eq(99L));
    }


    class CreateCpsTxParameterMatcher extends ArgumentMatcher<CreateCPSTransactionParameter> {
        private Long auftragId;

        private CreateCpsTxParameterMatcher(Long auftragId) {
            this.auftragId = auftragId;
        }

        @Override
        public boolean matches(Object object) {
            if (object instanceof CreateCPSTransactionParameter) {
                CreateCPSTransactionParameter param = (CreateCPSTransactionParameter) object;
                return auftragId.equals(param.getAuftragId());
            }
            return false;
        }
    }

    @DataProvider(name = "testSipPeeringPartnerCheckFutures")
    public Object[][] testSipPeeringPartnerCheckFuturesDP() {
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date tomorrow = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date future = Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date yesterday = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        SipSbcIpSet todaySet = new SipSbcIpSetBuilder().withGueltigAb(today).build();
        SipSbcIpSet futureSet = new SipSbcIpSetBuilder().withGueltigAb(future).build();
        SipSbcIpSet tomorrowSet = new SipSbcIpSetBuilder().withGueltigAb(tomorrow).build();
        SipSbcIpSet yesterdaySet = new SipSbcIpSetBuilder().withGueltigAb(yesterday).build();

        List<SipSbcIpSet> yersterdayList = Arrays.asList(yesterdaySet);
        List<SipSbcIpSet> todayList = Arrays.asList(yesterdaySet, todaySet);
        List<SipSbcIpSet> tomorrowList = Arrays.asList(yesterdaySet, todaySet, tomorrowSet);
        List<SipSbcIpSet> futureList = Arrays.asList(yesterdaySet, todaySet, tomorrowSet, futureSet); // invalid!

        SipPeeringPartner yesterdayPP = new SipPeeringPartnerBuilder().withSbcIpSets(yersterdayList).build();
        SipPeeringPartner todayPP = new SipPeeringPartnerBuilder().withSbcIpSets(todayList).build();
        SipPeeringPartner tomorrowPP = new SipPeeringPartnerBuilder().withSbcIpSets(tomorrowList).build();
        SipPeeringPartner futurePP = new SipPeeringPartnerBuilder().withSbcIpSets(futureList).build();

        return new Object[][] {
                { yesterdayPP, false },
                { todayPP, false },
                { tomorrowPP, false },
                { futurePP, true },
        };
    }

    @Test(dataProvider = "testSipPeeringPartnerCheckFutures")
    public void testSipPeeringPartnerCheckFutures(SipPeeringPartner sipPeeringPartner, boolean exceptionExpected)
            throws FindException {
        try {
            testling.sipPeeringPartnerCheckFutures(sipPeeringPartner);
        }
        catch (StoreException e) {
            if (!exceptionExpected) {
                fail();
            }
            return;
        }
        if (exceptionExpected) {
            fail();
        }
    }

    @DataProvider(name = "testSipPeeringPartnerCheckDuplicates")
    public Object[][] testSipPeeringPartnerCheckDuplicatesDP() {
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date tomorrow = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date yesterday = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        SipSbcIpSet todaySet = new SipSbcIpSetBuilder().withGueltigAb(today).build();
        SipSbcIpSet tomorrowSet = new SipSbcIpSetBuilder().withGueltigAb(tomorrow).build();
        SipSbcIpSet yesterdaySet = new SipSbcIpSetBuilder().withGueltigAb(yesterday).build();

        List<SipSbcIpSet> yersterdayList = Arrays.asList(yesterdaySet, todaySet, tomorrowSet, yesterdaySet);
        List<SipSbcIpSet> tomorrowList = Arrays.asList(yesterdaySet, todaySet, tomorrowSet, tomorrowSet);
        List<SipSbcIpSet> todayList = Arrays.asList(yesterdaySet, todaySet, tomorrowSet, todaySet);
        List<SipSbcIpSet> validList = Arrays.asList(yesterdaySet, todaySet, tomorrowSet);

        SipPeeringPartner validPP = new SipPeeringPartnerBuilder().withSbcIpSets(validList).build();
        SipPeeringPartner todayPP = new SipPeeringPartnerBuilder().withSbcIpSets(todayList).build();
        SipPeeringPartner tomorrowPP = new SipPeeringPartnerBuilder().withSbcIpSets(tomorrowList).build();
        SipPeeringPartner yesterdayPP = new SipPeeringPartnerBuilder().withSbcIpSets(yersterdayList).build();

        return new Object[][] {
                { validPP, false },
                { todayPP, true },
                { tomorrowPP, true },
                { yesterdayPP, true },
        };
    }

    @Test(dataProvider = "testSipPeeringPartnerCheckDuplicates")
    public void testSipPeeringPartnerCheckDuplicates(SipPeeringPartner sipPeeringPartner, boolean exceptionExpected)
            throws FindException {
        try {
            testling.sipPeeringPartnerCheckDuplicates(sipPeeringPartner);
        }
        catch (StoreException e) {
            if (!exceptionExpected) {
                fail();
            }
            return;
        }
        if (exceptionExpected) {
            fail();
        }
    }
}
