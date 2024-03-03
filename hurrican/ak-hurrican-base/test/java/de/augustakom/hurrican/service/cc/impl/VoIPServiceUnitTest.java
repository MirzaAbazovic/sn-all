package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.hurrican.dao.cc.AuftragVoIPDNDAO;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.AuftragVoIPDNBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.model.cc.VoipDnPlanBuilder;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.cc.view.AuftragVoipDNViewBuilder;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.SIPDomainService;
import de.augustakom.hurrican.service.cc.utils.CalculatedSipDomain4VoipAuftrag;

/**
 * VoIPServiceUnitTest
 */
@Test(groups = BaseTest.UNIT)
public class VoIPServiceUnitTest extends BaseTest {

    @Mock
    private CCAuftragService auftragService;
    @Mock
    private SIPDomainService sipDomainService;
    @Mock
    private AuftragVoIPDNDAO auftragVoIPDNDAO;
    @Mock
    private RufnummerService rufnummerService;

    @Spy
    @InjectMocks
    private VoIPServiceImpl cut;


    @BeforeMethod
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerateSipPassword() {
        String sipPassword = cut.generateSipPassword();
        Assert.assertNotNull(sipPassword);
        Assert.assertTrue(sipPassword.length() == AuftragVoIPDN.PASSWORD_LENGTH);
    }

    @DataProvider
    private Object[][] testActiveAndFutureRufnummernplaene() {
        Date now = new Date();
        Date plusTwo = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusDays(2).toInstant());
        Date plusOne = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant());
        Date minusOne = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(1).toInstant());
        Date minusTwo = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(2).toInstant());
        VoipDnPlan futurePlusTwo = new VoipDnPlanBuilder().withId(5L).withGueltigAb(plusTwo).build();
        VoipDnPlan futurePlusOne = new VoipDnPlanBuilder().withId(4L).withGueltigAb(plusOne).build();
        VoipDnPlan today = new VoipDnPlanBuilder().withId(3L).withGueltigAb(now).build();
        VoipDnPlan pastMinusOne = new VoipDnPlanBuilder().withId(2L).withGueltigAb(minusOne).build();
        VoipDnPlan pastMinusTwo = new VoipDnPlanBuilder().withId(1L).withGueltigAb(minusTwo).build();

        List<VoipDnPlan> future = new ArrayList<>();
        future.addAll(Arrays.asList(futurePlusTwo, futurePlusOne));

        List<VoipDnPlan> currentAndFuture = new ArrayList<>();
        currentAndFuture.addAll(Arrays.asList(futurePlusTwo, futurePlusOne, today));

        List<VoipDnPlan> pastAndFuture = new ArrayList<>();
        pastAndFuture.addAll(Arrays.asList(futurePlusTwo, futurePlusOne, pastMinusOne, pastMinusTwo));
        List<VoipDnPlan> pastAndFutureExpected= new ArrayList<>();
        pastAndFutureExpected.addAll(Arrays.asList(futurePlusTwo, futurePlusOne, pastMinusOne));

        List<VoipDnPlan> past = new ArrayList<>();
        past.addAll(Arrays.asList(pastMinusOne, pastMinusTwo));
        List<VoipDnPlan> pastExpected = new ArrayList<>();
        pastExpected.add(pastMinusOne);

        return new Object[][] {
                {future,           now, future},
                {currentAndFuture, now, currentAndFuture},
                {pastAndFuture,    now, pastAndFutureExpected},
                {pastAndFuture,    now, pastAndFuture},
                {past,             now, pastExpected},
        };
    }

    @Test(dataProvider = "testActiveAndFutureRufnummernplaene")
    public void testActiveAndFutureRufnummernplaene(List<VoipDnPlan> in, Date now, List<VoipDnPlan> expected) {
        AuftragVoIPDN auftragVoIPDN = new AuftragVoIPDN();
        in.stream().forEach(auftragVoIPDN::addRufnummernplan);

        List<VoipDnPlan> toCheck = auftragVoIPDN.getActiveAndFutureRufnummernplaene(now);

        Assert.assertNotNull(toCheck);
        Assert.assertTrue(toCheck.stream().filter(p -> !expected.contains(p)).collect(Collectors.toList()).size() == 0);
    }

    @DataProvider
    protected Object[][] testUpdateSipLoginDP() {
        return new Object[][] {
                { "+499118919820@biz.m-call.de", "biz.m-call.muc07.de", "+499118919820@biz.m-call.muc07.de" }
        };
    }

    @Test(dataProvider = "testUpdateSipLoginDP")
    public void testUpdateSipLogin(final String sipLogin, final String newSipDomain, final String expectedSipLogin) {
        assertEquals(cut.updateSipLogin(sipLogin, newSipDomain), expectedSipLogin);
    }

    @Test
    public void testMigrateSipDomainForVoipDNPlanNoPlans() {
        AuftragVoIPDN voipDN =
                new AuftragVoIPDNBuilder().setPersist(false)
                        .withRufnummernplaene(Collections.emptyList())
                        .build();

        cut.migrateSipDomainForVoipDNPlan(voipDN);
        verify(cut, never()).updateSipLogin(anyString(), anyString());
        verify(auftragVoIPDNDAO, never()).store(any());
    }

    @Test
    public void testMigrateSipDomainForVoipDNPlan() {
        String sipLogin2 = "+499118919822@biz.m-call.de";
        String sipLogin3 = "+499118919823@biz.m-call.de";
        AuftragVoIPDN voipDN =
                new AuftragVoIPDNBuilder().setPersist(false)
                        .withSipDomain(
                                new ReferenceBuilder().setPersist(false)
                                        .withStrValue("biz.m-call.muc07.de")
                                        .build()
                        )
                        .withRufnummernplaene(
                                Arrays.asList(
                                        new VoipDnPlanBuilder().setPersist(false)
                                                .withSipLogin("+499118919820@biz.m-call.de")
                                                .withGueltigAb(Date.from(LocalDateTime.now().minusDays(20).atZone(ZoneId.systemDefault()).toInstant()))
                                                .build(),
                                        new VoipDnPlanBuilder().setPersist(false)
                                                .withSipLogin("+499118919821@biz.m-call.de")
                                                .withGueltigAb(Date.from(LocalDateTime.now().minusDays(10).atZone(ZoneId.systemDefault()).toInstant()))
                                                .build(),
                                        new VoipDnPlanBuilder().setPersist(false)
                                                .withSipLogin(sipLogin2)
                                                .withGueltigAb(Date.from(DateTools.stripTimeFromDate(LocalDateTime.now()).atZone(ZoneId.systemDefault()).toInstant()))
                                                .build(),
                                        new VoipDnPlanBuilder().setPersist(false)
                                                .withSipLogin(sipLogin3)
                                                .withGueltigAb(Date.from(LocalDateTime.now().plusDays(10).atZone(ZoneId.systemDefault()).toInstant()))
                                                .build()
                                )
                                )
                        .build();

        cut.migrateSipDomainForVoipDNPlan(voipDN);
        verify(cut, never()).updateSipLogin(voipDN.getRufnummernplaene().get(0).getSipLogin(), voipDN.getSipDomain().getStrValue());
        verify(cut, never()).updateSipLogin(voipDN.getRufnummernplaene().get(1).getSipLogin(), voipDN.getSipDomain().getStrValue());
        verify(cut).updateSipLogin(sipLogin2, voipDN.getSipDomain().getStrValue());
        verify(cut).updateSipLogin(sipLogin3, voipDN.getSipDomain().getStrValue());
        verify(auftragVoIPDNDAO, never()).store(voipDN.getRufnummernplaene().get(0));
        verify(auftragVoIPDNDAO, never()).store(voipDN.getRufnummernplaene().get(1));
        verify(auftragVoIPDNDAO).store(voipDN.getRufnummernplaene().get(2));
        verify(auftragVoIPDNDAO).store(voipDN.getRufnummernplaene().get(3));
    }

    @Test
    public void migrateSipDomainForVoipDNWithSuccess() throws ServiceNotFoundException, FindException {
        Reference sipDomainToOverride =
                new ReferenceBuilder().setPersist(false)
                        .withStrValue("biz.m-call.muc07.de")
                        .build();
        Reference sipDomainCurrent =
                new ReferenceBuilder().setPersist(false)
                        .withStrValue("mga.m-call.muc07.de")
                        .build();
        AuftragVoIPDN voipDN =
                new AuftragVoIPDNBuilder().setPersist(false)
                        .withSipDomain(sipDomainCurrent)
                        .build();
        long auftragId = 1L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .build();
        Rufnummer rufnummer = new RufnummerBuilder().withDnNoOrig(1234L).withOnKz("0821").withDnBase("123212")
                .withAuftragNoOrig(auftragId)
                .withOeNoOrig(Rufnummer.OE__NO_DEFAULT)
                .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                .withDirectDial(null).build();

        CalculatedSipDomain4VoipAuftrag initialSipDomainStatus = new CalculatedSipDomain4VoipAuftrag();
        initialSipDomainStatus.calculatedSipDomain = sipDomainCurrent;
        initialSipDomainStatus.isOverride = false;
        CalculatedSipDomain4VoipAuftrag calculatedSipDomain = new CalculatedSipDomain4VoipAuftrag();
        calculatedSipDomain.calculatedSipDomain = sipDomainToOverride;
        calculatedSipDomain.isOverride = true;

        when(sipDomainService.calculateSipDomain4VoipAuftrag(auftragId)).thenReturn(calculatedSipDomain);
        when(auftragVoIPDNDAO.findAuftragVoIPDN(auftragId)).thenReturn(Arrays.asList(voipDN));
        doNothing().when(cut).migrateSipDomainForVoipDNPlan(voipDN);
        when(auftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten);

        doReturn(rufnummerService).when(cut).getBillingService(RufnummerService.class);
        when(rufnummerService.findByParam(Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG,
                new Object[] { auftragDaten.getAuftragNoOrig(), Boolean.FALSE }))
                .thenReturn(Arrays.asList(rufnummer));

        Either<String, String> result = cut.migrateSipDomainOfVoipDNs(auftragId, false, initialSipDomainStatus);

        assertNotNull(result);
        assertTrue(result.isLeft());
        assertNotNull(result.getLeft());
        assertEquals(voipDN.getSipDomain(), sipDomainToOverride);
        verify(cut).migrateSipDomainForVoipDNPlan(voipDN);
    }

    @Test
    public void migrateSipDomainOfVoipDNsWithSipDomainMissing() throws ServiceNotFoundException, FindException {
        Reference sipDomainCurrent =
                new ReferenceBuilder().setPersist(false)
                        .withStrValue("mga.m-call.muc07.de")
                        .build();
        AuftragVoIPDN voipDN =
                new AuftragVoIPDNBuilder().setPersist(false)
                        .withSipDomain(sipDomainCurrent)
                        .build();
        long auftragId = 1L;

        CalculatedSipDomain4VoipAuftrag initialSipDomainStatus = new CalculatedSipDomain4VoipAuftrag();
        initialSipDomainStatus.calculatedSipDomain = sipDomainCurrent;
        initialSipDomainStatus.isOverride = true;
        CalculatedSipDomain4VoipAuftrag calculatedSipDomain = new CalculatedSipDomain4VoipAuftrag();
        calculatedSipDomain.calculatedSipDomain = null;
        calculatedSipDomain.isOverride = false;

        when(sipDomainService.calculateSipDomain4VoipAuftrag(auftragId)).thenReturn(calculatedSipDomain);
        when(auftragVoIPDNDAO.findAuftragVoIPDN(auftragId)).thenReturn(Arrays.asList(voipDN));
        doNothing().when(cut).migrateSipDomainForVoipDNPlan(voipDN);

        Either<String, String> result = cut.migrateSipDomainOfVoipDNs(auftragId, false, initialSipDomainStatus);

        assertNotNull(result);
        assertTrue(result.isRight());
        assertNotNull(result.getRight());
        assertEquals(voipDN.getSipDomain(), sipDomainCurrent);
        verify(cut, times(0)).migrateSipDomainForVoipDNPlan(voipDN);
    }

    @DataProvider
    private Object[][] testHasSipDomainChangedDp() {
        Reference refA = new ReferenceBuilder().withStrValue("A").build();
        Reference refB = new ReferenceBuilder().withStrValue("B").build();
        AuftragVoipDNView emptyView = new AuftragVoipDNView();
        AuftragVoipDNView viewA = new AuftragVoipDNViewBuilder().withSipDomain(refA).build();
        AuftragVoipDNView viewB = new AuftragVoipDNViewBuilder().withSipDomain(refB).build();
        AuftragVoIPDN emptyDn = new AuftragVoIPDN();
        AuftragVoIPDN dnA = new AuftragVoIPDNBuilder().withSipDomain(refA).build();
        AuftragVoIPDN dnB = new AuftragVoIPDNBuilder().withSipDomain(refB).build();

        return new Object[][] {
                { emptyView, emptyDn, false },
                { emptyView, dnA, true },
                { viewA, emptyDn, true},
                { viewA, dnA, false },
                { viewB, dnB, false },
                { viewA, dnB, true },
                { viewB, dnA, true },
        };
    }

    @Test(dataProvider = "testHasSipDomainChangedDp")
    public void testHasSipDomainChanged(AuftragVoipDNView auftragVoipDNView, AuftragVoIPDN auftragVoIPDN,
            boolean expectedResult) {
        boolean result = cut.hasSipDomainChanged(auftragVoipDNView, auftragVoIPDN);
        assertTrue(result == expectedResult);
    }

    @Test
    public void testUpdateSipDomainForVoipDNView() {
        Date yesterday = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(1).toInstant());
        Date tomorrow = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant());
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        VoipDnPlan planPast = VoipDnPlanBuilder.aVoipDnPlan().withGueltigAb(yesterday).build();
        VoipDnPlan planPresent = VoipDnPlanBuilder.aVoipDnPlan().withGueltigAb(today).withSipLogin("+4989123450@present").build();
        VoipDnPlan planFuture = VoipDnPlanBuilder.aVoipDnPlan().withGueltigAb(tomorrow).withSipLogin("+4989123450@future").build();

        VoipDnPlanView planViewPast = new VoipDnPlanView("089", "12345", planPast);
        VoipDnPlanView planViewPresent = new VoipDnPlanView("089", "12345", planPresent);
        VoipDnPlanView planViewFuture = new VoipDnPlanView("089", "12345", planFuture);

        Reference sipDomain = new ReferenceBuilder().withStrValue("A").build();
        AuftragVoipDNView auftragVoipDNView = new AuftragVoipDNViewBuilder()
                .withVoipDnPlanViews(Lists.newArrayList(planViewPast, planViewPresent, planViewFuture))
                .withSipDomain(sipDomain)
                .build();

        cut.updateSipDomainForVoipDNView(auftragVoipDNView);
        assertTrue(StringUtils.isBlank(planViewPast.getSipLogin()));
        assertTrue(StringUtils.equals(planViewPresent.getSipLogin(), "+4989123450@A"));
        assertTrue(StringUtils.equals(planViewFuture.getSipLogin(), "+4989123450@A"));
    }
}
