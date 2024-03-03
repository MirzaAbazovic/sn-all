package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyCollectionOf;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.startsWith;
import static org.testng.Assert.*;

import java.util.*;
import javax.validation.*;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.AdresseBuilder;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.view.BAuftragVO;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.OrderMatchVO;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.FirmaBuilder;
import de.mnet.wbci.model.builder.PersonBuilder;
import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;
import de.mnet.wbci.model.builder.RufnummernblockBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueOrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallRrnpTestBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDeadlineService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciLocationService;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciSchedulerService;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.validation.helper.ConstraintViolationHelper;
import de.mnet.wita.service.WitaConfigService;

@Test(groups = UNIT)
public class WbciVaServiceImplTest extends BaseTest {

    @Mock
    protected RufnummerService rufnummerService;
    @InjectMocks
    @Spy
    private WbciVaKueMrnServiceImpl wbciVaKueMrnService;
    @InjectMocks
    @Spy
    private WbciVaRrnpServiceImpl wbciVaRrnpService;
    @InjectMocks
    @Spy
    private WbciVaKueOrnServiceImpl wbciVaKueOrnService;
    @Mock
    private WbciDao wbciDao;
    @Mock
    private WitaConfigService witaConfigService;
    @Mock
    private WbciCommonService wbciCommonService;
    @Mock
    private WbciDeadlineService wbciDeadlineService;
    @Mock
    private WbciValidationService wbciValidationService;
    @Mock
    private ConstraintViolationHelper constraintViolationHelper;
    @Mock
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;
    @Mock
    private WbciSchedulerService wbciSchedulerService;
    @Mock
    private WbciMeldungService wbciMeldungService;
    @Mock
    private WbciLocationService wbciLocationService;
    @Mock
    private VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> vaMock;

    @Mock
    private WbciGeschaeftsfallKueMrn gfMock;

    @Mock
    private BillingAuftragService billingAuftragService;

    @BeforeMethod
    public void setUp() {
        wbciVaKueMrnService = new WbciVaKueMrnServiceImpl();
        wbciVaRrnpService = new WbciVaRrnpServiceImpl();
        wbciVaKueOrnService = new WbciVaKueOrnServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateWbciKueMrnVorgang() throws Exception {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValidWithoutVorabstimmungsId(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        testCreateWbciVorgang(wbciGeschaeftsfallKueMrn, wbciVaKueMrnService);
    }

    @Test
    public void testCreateWbciRrnpVorgang() throws Exception {
        WbciGeschaeftsfallRrnp wbciGeschaeftsfallRrnp = new WbciGeschaeftsfallRrnpTestBuilder()
                .buildValidWithoutVorabstimmungsId(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_RRNP);
        testCreateWbciVorgang(wbciGeschaeftsfallRrnp, wbciVaRrnpService);
    }

    @Test
    public void testCreateWbciKueOrnVorgang() throws Exception {
        WbciGeschaeftsfallKueOrn wbciGeschaeftsfallKueOrn = new WbciGeschaeftsfallKueOrnTestBuilder()
                .buildValidWithoutVorabstimmungsId(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN);
        testCreateWbciVorgang(wbciGeschaeftsfallKueOrn, wbciVaKueOrnService);
    }

    @Test
    public void testCreateWbciKueMrnVorgangWithVorabstimmungsId() throws Exception {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        testCreateWbciVorgang(wbciGeschaeftsfallKueMrn, wbciVaKueMrnService);
    }

    @Test
    public void testCreateWbciRrnpVorgangWithVorabstimmungsId() throws Exception {
        WbciGeschaeftsfallRrnp wbciGeschaeftsfallRrnp = new WbciGeschaeftsfallRrnpTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_RRNP);
        testCreateWbciVorgang(wbciGeschaeftsfallRrnp, wbciVaRrnpService);
    }

    @Test
    public void testCreateWbciKueOrnVorgangWithVorabstimmungsId() throws Exception {
        WbciGeschaeftsfallKueOrn wbciGeschaeftsfallKueOrn = new WbciGeschaeftsfallKueOrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN);
        testCreateWbciVorgang(wbciGeschaeftsfallKueOrn, wbciVaKueOrnService);
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testCreateVaWithValidationException() throws Exception {
        WbciGeschaeftsfallKueOrn wbciGeschaeftsfallKueOrn = new WbciGeschaeftsfallKueOrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN);
        Set<ConstraintViolation<VorabstimmungsAnfrage>> constraintsMock = new HashSet<>();
        constraintsMock.add(Mockito.mock(ConstraintViolation.class));
        when(wbciValidationService.checkWbciMessageForErrors(any(CarrierCode.class), any(VorabstimmungsAnfrage.class)))
                .thenReturn(constraintsMock);
        when(constraintViolationHelper.generateErrorMsg(anySet())).thenReturn("VALIDATION ERROR");

        testCreateWbciVorgang(wbciGeschaeftsfallKueOrn, wbciVaKueOrnService);
    }

    @Test
    public void testCreateLinkedVaWithAbgebenderEKPMatch() throws Exception {
        String strAenVorabstimmungsId = "DEU.MNET.V12345678";
        WbciGeschaeftsfallKueOrn newVa = new WbciGeschaeftsfallKueOrnTestBuilder()
                .withStrAenVorabstimmungsId(strAenVorabstimmungsId)
                .withAbgebenderEKP(CarrierCode.DTAG)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN);
        WbciGeschaeftsfallKueOrn cancelledGf = new WbciGeschaeftsfallKueOrnTestBuilder()
                .withAbgebenderEKP(CarrierCode.DTAG)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN);

        testCreateWbciVorgang(newVa, wbciVaKueOrnService);

        verify(wbciValidationService).assertLinkedVaHasNoFaultyAutomationTasks(strAenVorabstimmungsId);
        verify(wbciValidationService).assertAbgebenderEKPMatchesLinkedAbgebenderEKP(CarrierCode.DTAG, strAenVorabstimmungsId);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Ein unvorhergesehner Fehler bei der Ermittlung des Kundentyps ist aufgetreten: TEST")
    public void testCreateWbciVorgangWrappedFindException() throws Exception {
        WbciGeschaeftsfallKueOrn wbciGeschaeftsfallKueOrn = new WbciGeschaeftsfallKueOrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN);
        wbciGeschaeftsfallKueOrn.setBillingOrderNoOrig(9999L);
        testCreateWbciVorgang(wbciGeschaeftsfallKueOrn, wbciVaKueOrnService);
    }

    private <GF extends WbciGeschaeftsfall> void testCreateWbciVorgang(GF wbciGeschaeftsfall,
            WbciVaServiceImpl<GF> createVaService) throws Exception {

        final String vorabstimmungsId = wbciGeschaeftsfall.getVorabstimmungsId();
        // set gf properties to null that should be overridden in biz service
        wbciGeschaeftsfall.setWbciVersion(null);
        wbciGeschaeftsfall.setAufnehmenderEKP(null);
        wbciGeschaeftsfall.setAbsender(null);
        wbciGeschaeftsfall.setStatus(null);
        if (StringUtils.isEmpty(vorabstimmungsId)) {
            when(wbciCommonService.getNextPreAgreementId(RequestTyp.VA)).thenReturn("DEU.MNET.VH00000001");
        }

        when(wbciCommonService.getMnetTechnologie(wbciGeschaeftsfall.getAuftragId())).thenReturn(Technologie.TAL_ISDN);
        //mocks for determination of the customer typ
        if (wbciGeschaeftsfall.getBillingOrderNoOrig() != 9999L) {
            when(wbciCommonService.getKundenTyp(wbciGeschaeftsfall.getBillingOrderNoOrig())).thenReturn(KundenTyp.PK);
        }
        else {
            doThrow(new FindException("TEST")).when(wbciCommonService).getKundenTyp(anyLong());
        }

        when(wbciDao.store(any(VorabstimmungsAnfrage.class))).thenAnswer(new Answer<VorabstimmungsAnfrage>() {
            @Override
            public VorabstimmungsAnfrage answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (VorabstimmungsAnfrage) args[0];
            }
        });

        VorabstimmungsAnfrage<GF> anfrage = createVaService.createWbciVorgang(wbciGeschaeftsfall);

        GF geschaeftsfall = anfrage.getWbciGeschaeftsfall();

        if (StringUtils.isEmpty(vorabstimmungsId)) {
            verify(wbciCommonService).getNextPreAgreementId(RequestTyp.VA);
        }
        else {
            verify(wbciCommonService, times(0)).getNextPreAgreementId(RequestTyp.VA);
        }

        verify(wbciValidationService).checkWbciMessageForErrors(wbciGeschaeftsfall.getAbgebenderEKP(), anfrage);
        verify(wbciCommonService).getMnetTechnologie(wbciGeschaeftsfall.getAuftragId());

        assertEquals(geschaeftsfall, wbciGeschaeftsfall);
        assertNull(geschaeftsfall.getWbciVersion());
        assertEquals(geschaeftsfall.getAufnehmenderEKP(), CarrierCode.MNET);
        assertNotNull(geschaeftsfall.getAbgebenderEKP());
        assertEquals(geschaeftsfall.getAbsender(), CarrierCode.MNET);
        assertTrue(geschaeftsfall.getVorabstimmungsId().startsWith(CarrierCode.MNET.getITUCarrierCode()));
        assertTrue(geschaeftsfall.getVorabstimmungsId()
                .substring(geschaeftsfall.getVorabstimmungsId().lastIndexOf('.') + 1).startsWith("V"));
        assertTrue(geschaeftsfall.getVorabstimmungsId().matches("[0-9A-Z]{3}\\.[0-9A-Z]{1,6}\\.V[0-9A-Z]{9}"));
        assertEquals(geschaeftsfall.getStatus(), WbciGeschaeftsfallStatus.ACTIVE);
        assertNull(anfrage.getProcessedAt());
        assertEquals(anfrage.getTyp(), RequestTyp.VA);
        assertEquals(anfrage.getWbciGeschaeftsfall().getEndkunde().getKundenTyp(), KundenTyp.PK);
    }

    @Test
    public void testProcessIncomingVA() throws Exception {
        String vaId = "DEU.MNET.VH00000001";
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciValidationService.isDuplicateVaRequest(vaMock)).thenReturn(false);
        when(vaMock.getWbciGeschaeftsfall()).thenReturn(gfMock);
        when(gfMock.getVorabstimmungsId()).thenReturn(vaId);
        when(wbciDao.store(vaMock)).thenReturn(vaMock);

        when(wbciCommonService.findWbciGeschaeftsfall(vaId)).thenReturn(null);

        when(wbciValidationService.checkWbciMessageForErrors(any(CarrierCode.class), any(VorabstimmungsAnfrage.class)))
                .thenReturn(Collections.<ConstraintViolation<VorabstimmungsAnfrage>>emptySet());

        wbciVaKueMrnService.processIncomingVA(metadata, vaMock);

        verify(gfMock).setStatus(WbciGeschaeftsfallStatus.ACTIVE);
        verify(vaMock).setRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
        verify(wbciDeadlineService).updateAnswerDeadline(vaMock);
        verify(wbciValidationService).checkWbciMessageForErrors(any(CarrierCode.class), any(VorabstimmungsAnfrage.class));
        verify(wbciGeschaeftsfallService, times(0)).markGfForClarification(anyLong(), anyString(), isNull(AKUser.class));
    }

    @Test
    public void testProcessIncomingVAWithValidationErrors() throws Exception {
        String vaId = "DEU.MNET.VH00000001";
        final MessageProcessingMetadata results = new MessageProcessingMetadata();

        when(wbciValidationService.isDuplicateVaRequest(vaMock)).thenReturn(false);
        when(vaMock.getWbciGeschaeftsfall()).thenReturn(gfMock);
        when(gfMock.getVorabstimmungsId()).thenReturn(vaId);
        when(wbciDao.store(vaMock)).thenReturn(vaMock);

        when(wbciCommonService.findWbciGeschaeftsfall(vaId)).thenReturn(null);

        Set<ConstraintViolation<VorabstimmungsAnfrage>> constraintsMock = new HashSet<>();
        constraintsMock.add(Mockito.mock(ConstraintViolation.class));
        when(wbciValidationService.checkWbciMessageForErrors(any(CarrierCode.class), any(VorabstimmungsAnfrage.class)))
                .thenReturn(constraintsMock);
        when(constraintViolationHelper.generateErrorMsgForInboundMsg(anySet())).thenReturn("VALIDATION ERROR");

        wbciVaKueMrnService.processIncomingVA(results, vaMock);

        verify(gfMock).setStatus(WbciGeschaeftsfallStatus.ACTIVE);
        verify(vaMock).setRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
        verify(wbciDeadlineService).updateAnswerDeadline(vaMock);
        verify(wbciValidationService).checkWbciMessageForErrors(any(CarrierCode.class), any(VorabstimmungsAnfrage.class));
        verify(constraintViolationHelper).generateErrorMsgForInboundMsg(anySet());
        verify(wbciGeschaeftsfallService).markGfForClarification(anyLong(), eq("VALIDATION ERROR"), isNull(AKUser.class));
    }

    @Test
    public void testDuplicateVAId() throws Exception {
        String vaId = "DEU.MNET.VH00000001";
        final MessageProcessingMetadata results = new MessageProcessingMetadata();

        when(wbciValidationService.isDuplicateVaRequest(vaMock)).thenReturn(true);
        when(vaMock.getWbciGeschaeftsfall()).thenReturn(gfMock);
        when(gfMock.getVorabstimmungsId()).thenReturn(vaId);

        wbciVaKueMrnService.processIncomingVA(results, vaMock);

        ArgumentCaptor<MessageProcessingMetadata> metadataArgument = ArgumentCaptor.forClass(MessageProcessingMetadata.class);
        verify(wbciMeldungService, atLeastOnce()).sendErrorResponse(metadataArgument.capture(), any(Abbruchmeldung.class));
        Assert.assertFalse(metadataArgument.getValue().isPostProcessMessage());
        Assert.assertTrue(metadataArgument.getValue().isResponseToDuplicateVaRequest());

        verify(wbciGeschaeftsfallService, never()).closeGeschaeftsfall(gfMock.getId());
        verify(gfMock, never()).setStatus(any(WbciGeschaeftsfallStatus.class));
        verify(vaMock, never()).setRequestStatus(any(WbciRequestStatus.class));
        verify(wbciDao, never()).store(vaMock);
        verify(wbciDeadlineService, never()).updateAnswerDeadline(vaMock);
    }

    @Test
    public void getTaifunOrderAssignmentCandidatesMrn() throws Exception {
        WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.findWbciGeschaeftsfall(gf.getVorabstimmungsId())).thenReturn(gf);

        final Long orderNoOrig1 = 97L, orderNoOrig2 = 98L, orderNoOrig3 = 99L;
        final BAuftragVO bAuftrag1 = mock(BAuftragVO.class), bAuftrag2 = mock(BAuftragVO.class), bAuftrag3 = mock(BAuftragVO.class);
        Set<Long> orderNoOrigListRN = Sets.newHashSet(orderNoOrig1, orderNoOrig2);
        Set<Long> orderNoOrigListStandort = Sets.newHashSet(orderNoOrig1, orderNoOrig3);
        Set<Long> orderNoNames = Sets.newHashSet(orderNoOrig1);

        when(billingAuftragService.getBasicOrderInformation(orderNoOrig1)).thenReturn(bAuftrag1);
        when(billingAuftragService.getBasicOrderInformation(orderNoOrig2)).thenReturn(bAuftrag2);
        when(billingAuftragService.getBasicOrderInformation(orderNoOrig3)).thenReturn(bAuftrag3);
        doReturn(orderNoOrigListRN).when(wbciVaKueMrnService).getOrderNoOrigsByDNs(gf);
        doReturn(orderNoNames).when(wbciVaKueMrnService)
                .filterAuftragIdsByName(gf, Sets.newHashSet(orderNoOrig1, orderNoOrig2, orderNoOrig3));
        doReturn(orderNoOrigListStandort).when(wbciVaKueMrnService).getOrderNoOrigsByGeoId(gf);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        }).when(wbciVaKueMrnService).filterBillableTaifunOrders(anyCollectionOf(OrderMatchVO.class));

        final List<Pair<BAuftragVO, OrderMatchVO>> assignmentCandidates = wbciVaKueMrnService
                .getTaifunOrderAssignmentCandidates(gf.getVorabstimmungsId(), false);
        verify(wbciVaKueMrnService).getOrderNoOrigsByDNs(gf);
        verify(wbciVaKueMrnService).filterAuftragIdsByName(gf,
                Sets.newHashSet(orderNoOrig1, orderNoOrig2, orderNoOrig3));
        verify(wbciVaKueMrnService).getOrderNoOrigsByGeoId(gf);
        verify(wbciVaKueMrnService).filterBillableTaifunOrders(anyCollectionOf(OrderMatchVO.class));
        verify(wbciVaRrnpService, never()).getOrderNoOrigsByGeoId(gf);
        assertNotEmpty(assignmentCandidates);
        assertEquals(assignmentCandidates.size(), 3);
        assertOrderMatchVo(assignmentCandidates, bAuftrag1, orderNoOrig1, OrderMatchVO.BasicSearch.DN);
        assertOrderMatchVo(assignmentCandidates, bAuftrag2, orderNoOrig2, OrderMatchVO.BasicSearch.DN,
                OrderMatchVO.MatchViolation.LOCATION, OrderMatchVO.MatchViolation.NAME);
        assertOrderMatchVo(assignmentCandidates, bAuftrag3, orderNoOrig3, OrderMatchVO.BasicSearch.LOCATION,
                OrderMatchVO.MatchViolation.DN, OrderMatchVO.MatchViolation.NAME);

        //check nothing found
        doReturn(Sets.newHashSet()).when(wbciVaKueMrnService).getOrderNoOrigsByDNs(gf);
        doReturn(Sets.newHashSet()).when(wbciVaKueMrnService).filterAuftragIdsByName(gf, Sets.<Long>newHashSet());
        doReturn(Sets.newHashSet()).when(wbciVaKueMrnService).getOrderNoOrigsByGeoId(gf);
        Assert.assertTrue(CollectionUtils.isEmpty(wbciVaKueMrnService.getTaifunOrderAssignmentCandidates(gf.getVorabstimmungsId(), false)));
    }

    @Test
    public void getTaifunOrderAssignmentCandidatesOrn() throws Exception {
        WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueOrnTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_ORN);
        when(wbciCommonService.findWbciGeschaeftsfall(gf.getVorabstimmungsId())).thenReturn(gf);

        final Long orderNoOrig1 = 97L, orderNoOrig2 = 98L, orderNoOrig3 = 99L;
        final BAuftragVO bAuftrag1 = mock(BAuftragVO.class), bAuftrag2 = mock(BAuftragVO.class), bAuftrag3 = mock(BAuftragVO.class);
        Set<Long> orderNoOrigListRN = Sets.newHashSet(orderNoOrig3);
        Set<Long> orderNoOrigListStandort = Sets.newHashSet(orderNoOrig1, orderNoOrig2, orderNoOrig3);
        Set<Long> orderNoNames = Sets.newHashSet(orderNoOrig1, orderNoOrig3);

        when(billingAuftragService.getBasicOrderInformation(orderNoOrig1)).thenReturn(bAuftrag1);
        when(billingAuftragService.getBasicOrderInformation(orderNoOrig2)).thenReturn(bAuftrag2);
        when(billingAuftragService.getBasicOrderInformation(orderNoOrig3)).thenReturn(bAuftrag3);
        doReturn(orderNoOrigListRN).when(wbciVaKueOrnService).getOrderNoOrigsByDNs(gf);
        doReturn(orderNoNames).when(wbciVaKueOrnService)
                .filterAuftragIdsByName(gf, Sets.newHashSet(orderNoOrig1, orderNoOrig2, orderNoOrig3));
        doReturn(orderNoOrigListStandort).when(wbciVaKueOrnService).getOrderNoOrigsByGeoId(gf);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        }).when(wbciVaKueOrnService).filterBillableTaifunOrders(anyCollectionOf(OrderMatchVO.class));

        final List<Pair<BAuftragVO, OrderMatchVO>> assignmentCandidates = wbciVaKueOrnService
                .getTaifunOrderAssignmentCandidates(gf.getVorabstimmungsId(), false);
        verify(wbciVaKueOrnService).getOrderNoOrigsByDNs(gf);
        verify(wbciVaKueOrnService).filterAuftragIdsByName(gf,
                Sets.newHashSet(orderNoOrig1, orderNoOrig2, orderNoOrig3));
        verify(wbciVaKueOrnService).getOrderNoOrigsByGeoId(gf);
        verify(wbciVaKueOrnService).filterBillableTaifunOrders(anyCollectionOf(OrderMatchVO.class));
        verify(wbciVaRrnpService, never()).getOrderNoOrigsByGeoId(gf);
        assertNotEmpty(assignmentCandidates);
        assertEquals(assignmentCandidates.size(), 3);
        assertOrderMatchVo(assignmentCandidates, bAuftrag1, orderNoOrig1, OrderMatchVO.BasicSearch.LOCATION, OrderMatchVO.MatchViolation.DN);
        assertOrderMatchVo(assignmentCandidates, bAuftrag2, orderNoOrig2, OrderMatchVO.BasicSearch.LOCATION, OrderMatchVO.MatchViolation.NAME, OrderMatchVO.MatchViolation.DN);
        assertOrderMatchVo(assignmentCandidates, bAuftrag3, orderNoOrig3, OrderMatchVO.BasicSearch.DN);
    }

    @Test
    public void getTaifunOrderAssignmentCandidatesRrnp() throws Exception {

        WbciGeschaeftsfall gf = new WbciGeschaeftsfallRrnpTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_RRNP);
        when(wbciCommonService.findWbciGeschaeftsfall(gf.getVorabstimmungsId())).thenReturn(gf);

        final Long orderNoOrig1 = 97L, orderNoOrig2 = 98L, orderNoOrig3 = 99L;
        final BAuftragVO bAuftrag1 = mock(BAuftragVO.class), bAuftrag2 = mock(BAuftragVO.class), bAuftrag3 = mock(BAuftragVO.class);
        Set<Long> orderNoOrigListRN = Sets.newHashSet(orderNoOrig1, orderNoOrig2, orderNoOrig3);
        Set<Long> orderNoOrigListStandort = null;
        Set<Long> orderNoNames = Sets.newHashSet(orderNoOrig1, orderNoOrig3);

        when(billingAuftragService.getBasicOrderInformation(orderNoOrig1)).thenReturn(bAuftrag1);
        when(billingAuftragService.getBasicOrderInformation(orderNoOrig2)).thenReturn(bAuftrag2);
        when(billingAuftragService.getBasicOrderInformation(orderNoOrig3)).thenReturn(bAuftrag3);
        doReturn(orderNoOrigListRN).when(wbciVaRrnpService).getOrderNoOrigsByDNs(gf);
        doReturn(orderNoNames).when(wbciVaRrnpService)
                .filterAuftragIdsByName(gf, Sets.newHashSet(orderNoOrig1, orderNoOrig2, orderNoOrig3));
        doReturn(orderNoOrigListStandort).when(wbciVaRrnpService).getOrderNoOrigsByGeoId(gf);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        }).when(wbciVaRrnpService).filterBillableTaifunOrders(anyCollectionOf(OrderMatchVO.class));

        final List<Pair<BAuftragVO, OrderMatchVO>> assignmentCandidates = wbciVaRrnpService
                .getTaifunOrderAssignmentCandidates(gf.getVorabstimmungsId(), false);
        verify(wbciVaRrnpService).getOrderNoOrigsByDNs(gf);
        verify(wbciVaRrnpService).filterAuftragIdsByName(gf,
                Sets.newHashSet(orderNoOrig1, orderNoOrig2, orderNoOrig3));
        verify(wbciVaRrnpService, never()).getOrderNoOrigsByGeoId(gf);
        verify(wbciVaRrnpService).filterBillableTaifunOrders(anyCollectionOf(OrderMatchVO.class));

        assertNotEmpty(assignmentCandidates);
        assertEquals(assignmentCandidates.size(), 3);
        assertOrderMatchVo(assignmentCandidates, bAuftrag1, orderNoOrig1, OrderMatchVO.BasicSearch.DN);
        assertOrderMatchVo(assignmentCandidates, bAuftrag2, orderNoOrig2, OrderMatchVO.BasicSearch.DN, OrderMatchVO.MatchViolation.NAME);
        assertOrderMatchVo(assignmentCandidates, bAuftrag3, orderNoOrig3, OrderMatchVO.BasicSearch.DN);

        //test filter out function
        List<Pair<BAuftragVO, OrderMatchVO>> assignmentCandidatesWithoutViolations = wbciVaRrnpService.getTaifunOrderAssignmentCandidates(gf.getVorabstimmungsId(), true);
        assertEquals(assignmentCandidatesWithoutViolations.size(), 2);
        assertOrderMatchVo(assignmentCandidatesWithoutViolations, bAuftrag1, orderNoOrig1, OrderMatchVO.BasicSearch.DN);
        assertOrderMatchVo(assignmentCandidatesWithoutViolations, bAuftrag3, orderNoOrig3, OrderMatchVO.BasicSearch.DN);
        verify(wbciVaRrnpService, never()).getOrderNoOrigsByGeoId(gf);
    }

    @Test
    public void getTaifunOrderAssignmentCandidatesOrnWithFilter() throws Exception {
        WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueOrnTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_ORN);
        when(wbciCommonService.findWbciGeschaeftsfall(gf.getVorabstimmungsId())).thenReturn(gf);

        final Long orderNoOrig1 = 97L, orderNoOrig2 = 98L, orderNoOrig3 = 99L;
        final BAuftragVO bAuftrag1 = mock(BAuftragVO.class), bAuftrag2 = mock(BAuftragVO.class), bAuftrag3 = mock(BAuftragVO.class);
        Set<Long> orderNoOrigListRN = Sets.newHashSet(orderNoOrig1, orderNoOrig3);
        Set<Long> orderNoNames = Sets.newHashSet(orderNoOrig3);

        when(billingAuftragService.getBasicOrderInformation(orderNoOrig1)).thenReturn(bAuftrag1);
        when(billingAuftragService.getBasicOrderInformation(orderNoOrig2)).thenReturn(bAuftrag2);
        when(billingAuftragService.getBasicOrderInformation(orderNoOrig3)).thenReturn(bAuftrag3);
        doReturn(orderNoOrigListRN).when(wbciVaKueOrnService).getOrderNoOrigsByDNs(gf);
        doReturn(orderNoOrigListRN).when(wbciVaKueOrnService).getOrderNoOrigsByGeoId(gf);
        doReturn(orderNoNames).when(wbciVaKueOrnService)
                .filterAuftragIdsByName(any(WbciGeschaeftsfall.class), anySet());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        }).when(wbciVaKueOrnService).filterBillableTaifunOrders(anyCollectionOf(OrderMatchVO.class));

        final List<Pair<BAuftragVO, OrderMatchVO>> assignmentCandidates = wbciVaKueOrnService
                .getTaifunOrderAssignmentCandidates(gf.getVorabstimmungsId(), true);
        verify(wbciVaKueOrnService).getOrderNoOrigsByDNs(gf);
        verify(wbciVaKueOrnService).filterAuftragIdsByName(gf, Sets.newHashSet(orderNoOrig1, orderNoOrig3));
        verify(wbciVaKueOrnService).getOrderNoOrigsByGeoId(gf);
        verify(wbciVaKueOrnService).filterBillableTaifunOrders(anyCollectionOf(OrderMatchVO.class));
        assertNotEmpty(assignmentCandidates);
        assertEquals(assignmentCandidates.size(), 1);
        assertOrderMatchVo(assignmentCandidates, bAuftrag3, orderNoOrig3, OrderMatchVO.BasicSearch.DN);

        //Test if nothing have been found in DN-Search
        Set<Long> orderNoOrigListStandort = Sets.newHashSet(orderNoOrig2);
        doReturn(Sets.newHashSet()).when(wbciVaKueOrnService).getOrderNoOrigsByDNs(gf);
        doReturn(orderNoOrigListStandort).when(wbciVaKueOrnService).getOrderNoOrigsByGeoId(gf);
        doReturn(orderNoOrigListStandort).when(wbciVaKueOrnService)
                .filterAuftragIdsByName(any(WbciGeschaeftsfall.class), anySet());

        final List<Pair<BAuftragVO, OrderMatchVO>> assignmentCandidatesWithStandort = wbciVaKueOrnService
                .getTaifunOrderAssignmentCandidates(gf.getVorabstimmungsId(), true);
        verify(wbciVaKueOrnService, times(2)).getOrderNoOrigsByGeoId(gf);
        verify(wbciVaKueOrnService).filterAuftragIdsByName(gf, orderNoOrigListStandort);
        assertNotEmpty(assignmentCandidatesWithStandort);
        assertEquals(assignmentCandidatesWithStandort.size(), 1);
        assertOrderMatchVo(assignmentCandidatesWithStandort, bAuftrag2, orderNoOrig2, OrderMatchVO.BasicSearch.LOCATION);
    }


    @Test
    public void testAutoAssignTaifunOrderToVA() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> va = new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        final Long orderNoOrig = 99L;
        OrderMatchVO orderMatchVO = new OrderMatchVO(orderNoOrig);

        final List<OrderMatchVO> orderNoOrigList = Arrays.asList(orderMatchVO);
        doReturn(orderNoOrigList).when(wbciVaKueMrnService).getSuggestedOrderMatchVOs(va.getWbciGeschaeftsfall(), true);

        when(wbciCommonService.findActiveGfByTaifunId(orderNoOrig, true)).thenReturn(Collections.<WbciGeschaeftsfall>emptyList());

        boolean result = wbciVaKueMrnService.autoAssignTaifunOrderToVA(va);
        verify(wbciVaKueMrnService).getSuggestedOrderMatchVOs(va.getWbciGeschaeftsfall(), true);
        verify(wbciCommonService).findActiveGfByTaifunId(orderNoOrig, true);
        verify(wbciCommonService).assignTaifunOrder(va.getVorabstimmungsId(), orderNoOrig, false);
        assertTrue(result);
    }
    
    
    @Test
    public void autoAssignTaifunOrderToVA_ActiveGfStrAenExistsAndShouldBeClosed() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> va = new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Long orderNoOrig = 99L;
        List<OrderMatchVO> orderMatches = Arrays.asList(new OrderMatchVO(orderNoOrig));
        doReturn(orderMatches).when(wbciVaKueMrnService).getSuggestedOrderMatchVOs(va.getWbciGeschaeftsfall(), true);

        WbciGeschaeftsfallKueMrn activeWbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        activeWbciGeschaeftsfall.setStatus(WbciGeschaeftsfallStatus.NEW_VA);
        
        when(wbciCommonService.findActiveGfByTaifunId(orderNoOrig, true))
                .thenReturn(Arrays.<WbciGeschaeftsfall>asList(activeWbciGeschaeftsfall));
        when(wbciGeschaeftsfallService.isLinkedToStrAenGeschaeftsfall(va.getVorabstimmungsId(), activeWbciGeschaeftsfall.getId()))
                .thenReturn(true);
        when(wbciGeschaeftsfallService.isGeschaeftsfallWechselRrnpToMrnOrn(activeWbciGeschaeftsfall, va.getWbciGeschaeftsfall()))
                .thenReturn(false);

        boolean result = wbciVaKueMrnService.autoAssignTaifunOrderToVA(va);
        verify(wbciVaKueMrnService).getSuggestedOrderMatchVOs(va.getWbciGeschaeftsfall(), true);
        verify(wbciCommonService).findActiveGfByTaifunId(orderNoOrig, true);
        verify(wbciGeschaeftsfallService).isGeschaeftsfallWechselRrnpToMrnOrn(activeWbciGeschaeftsfall, va.getWbciGeschaeftsfall());
        verify(wbciGeschaeftsfallService).assignTaifunOrderAndCloseStrAenGeschaeftsfall(
                va.getVorabstimmungsId(), orderNoOrig, activeWbciGeschaeftsfall.getId(), false);
        verify(wbciCommonService, never()).assignTaifunOrder(va.getVorabstimmungsId(), orderNoOrig, false);
        assertTrue(result);
    }


    @Test
    public void autoAssignTaifunOrderToVA_ActiveGfStrAenExists_NewGfTypInvalid() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> va = new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Long orderNoOrig = 99L;
        List<OrderMatchVO> orderMatches = Arrays.asList(new OrderMatchVO(orderNoOrig));
        doReturn(orderMatches).when(wbciVaKueMrnService).getSuggestedOrderMatchVOs(va.getWbciGeschaeftsfall(), true);

        WbciGeschaeftsfallRrnp activeWbciGeschaeftsfall = new WbciGeschaeftsfallRrnpTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_RRNP);
        activeWbciGeschaeftsfall.setStatus(WbciGeschaeftsfallStatus.NEW_VA);

        when(wbciCommonService.findActiveGfByTaifunId(orderNoOrig, true))
                .thenReturn(Arrays.<WbciGeschaeftsfall>asList(activeWbciGeschaeftsfall));
        when(wbciGeschaeftsfallService.isLinkedToStrAenGeschaeftsfall(va.getVorabstimmungsId(), activeWbciGeschaeftsfall.getId()))
                .thenReturn(true);
        when(wbciGeschaeftsfallService.isGeschaeftsfallWechselRrnpToMrnOrn(activeWbciGeschaeftsfall, va.getWbciGeschaeftsfall()))
                .thenReturn(true);

        boolean result = wbciVaKueMrnService.autoAssignTaifunOrderToVA(va);
        verify(wbciVaKueMrnService).getSuggestedOrderMatchVOs(va.getWbciGeschaeftsfall(), true);
        verify(wbciCommonService).findActiveGfByTaifunId(orderNoOrig, true);
        verify(wbciGeschaeftsfallService).isGeschaeftsfallWechselRrnpToMrnOrn(activeWbciGeschaeftsfall, va.getWbciGeschaeftsfall());
        verify(wbciMeldungService).createAndSendWbciMeldung(any(Abbruchmeldung.class), eq(va.getVorabstimmungsId()));
        verify(wbciGeschaeftsfallService, never()).assignTaifunOrderAndCloseStrAenGeschaeftsfall(
                va.getVorabstimmungsId(), orderNoOrig, activeWbciGeschaeftsfall.getId(), false);
        verify(wbciCommonService, never()).assignTaifunOrder(va.getVorabstimmungsId(), orderNoOrig, false);
        assertFalse(result);
    }


    @Test
    public void autoAssignTaifunOrderToVA_ActiveGfStrAenExists_WithStatusNewVaExpired() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> va = new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Long orderNoOrig = 99L;
        List<OrderMatchVO> orderMatches = Arrays.asList(new OrderMatchVO(orderNoOrig));
        doReturn(orderMatches).when(wbciVaKueMrnService).getSuggestedOrderMatchVOs(va.getWbciGeschaeftsfall(), true);

        WbciGeschaeftsfallRrnp activeWbciGeschaeftsfall = new WbciGeschaeftsfallRrnpTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_RRNP);
        activeWbciGeschaeftsfall.setStatus(WbciGeschaeftsfallStatus.NEW_VA_EXPIRED);

        when(wbciCommonService.findActiveGfByTaifunId(orderNoOrig, true))
                .thenReturn(Arrays.<WbciGeschaeftsfall>asList(activeWbciGeschaeftsfall));
        when(wbciGeschaeftsfallService.isLinkedToStrAenGeschaeftsfall(va.getVorabstimmungsId(), activeWbciGeschaeftsfall.getId()))
                .thenReturn(true);
        when(wbciGeschaeftsfallService.isGeschaeftsfallWechselRrnpToMrnOrn(activeWbciGeschaeftsfall, va.getWbciGeschaeftsfall()))
                .thenReturn(true);

        boolean result = wbciVaKueMrnService.autoAssignTaifunOrderToVA(va);
        verify(wbciVaKueMrnService).getSuggestedOrderMatchVOs(va.getWbciGeschaeftsfall(), true);
        verify(wbciCommonService).findActiveGfByTaifunId(orderNoOrig, true);
        verify(wbciMeldungService, never()).createAndSendWbciMeldung(any(Abbruchmeldung.class), eq(va.getVorabstimmungsId()));
        verify(wbciGeschaeftsfallService, never()).assignTaifunOrderAndCloseStrAenGeschaeftsfall(
                va.getVorabstimmungsId(), orderNoOrig, activeWbciGeschaeftsfall.getId(), false);
        verify(wbciCommonService, never()).assignTaifunOrder(va.getVorabstimmungsId(), orderNoOrig, false);
        assertFalse(result);
    }


    @Test
    public void testAutoAssignTaifunOrderToVA_ActiveGFExists() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> va = new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Long orderNoOrig = 99L;
        List<OrderMatchVO> orderMatches = Arrays.asList(new OrderMatchVO(orderNoOrig));
        doReturn(orderMatches).when(wbciVaKueMrnService).getSuggestedOrderMatchVOs(va.getWbciGeschaeftsfall(), true);

        WbciGeschaeftsfallKueMrn activeWbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.findActiveGfByTaifunId(orderNoOrig, true))
                .thenReturn(Arrays.<WbciGeschaeftsfall>asList(activeWbciGeschaeftsfall));

        boolean result = wbciVaKueMrnService.autoAssignTaifunOrderToVA(va);
        verify(wbciVaKueMrnService).getSuggestedOrderMatchVOs(va.getWbciGeschaeftsfall(), true);
        verify(wbciCommonService).findActiveGfByTaifunId(orderNoOrig, true);
        verify(wbciCommonService, never()).assignTaifunOrder(va.getVorabstimmungsId(), orderNoOrig, false);
        assertFalse(result);
    }

    @Test
    public void testAutoAssignTaifunOrderToVANotUniqueOrder() throws FindException {
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> va = new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        List<OrderMatchVO> orderMatches = Arrays.asList(new OrderMatchVO(98L), new OrderMatchVO(99L));
        doReturn(orderMatches).when(wbciVaKueMrnService).getSuggestedOrderMatchVOs(va.getWbciGeschaeftsfall(), true);

        boolean result = wbciVaKueMrnService.autoAssignTaifunOrderToVA(va);
        verify(wbciVaKueMrnService).getSuggestedOrderMatchVOs(va.getWbciGeschaeftsfall(), true);
        verify(wbciCommonService, never()).findActiveGfByTaifunId(anyLong(), eq(true));
        verify(wbciCommonService, never()).assignTaifunOrder(eq(va.getVorabstimmungsId()), anyLong(), anyBoolean());
        assertFalse(result);
    }

    @Test
    public void testQueryOrderNoOrigsByDNsVaKueOrn() throws FindException {
        WbciGeschaeftsfallKueOrn wbciGeschaeftsfallKueOrn = new WbciGeschaeftsfallKueOrnTestBuilder()
                .withAnschlussIdentifikation(
                        new RufnummerOnkzBuilder()
                                .withOnkz("089")
                                .withRufnummer("1234567890")
                                .build()
                )
                .buildValidWithoutVorabstimmungsId(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN);

        Set<Long> expectedOrderNoOrigs = Sets.newHashSet(1L);
        when(rufnummerService.findAuftragIdsByEinzelrufnummer("89", "1234567890")).thenReturn(expectedOrderNoOrigs);

        Set<Long> result = wbciVaKueOrnService.getOrderNoOrigsByDNs(wbciGeschaeftsfallKueOrn);
        verify(rufnummerService).findAuftragIdsByEinzelrufnummer("89", "1234567890");
        verify(rufnummerService, never()).findAuftragIdsByBlockrufnummer(any(String.class), any(String.class));
        assertNotNull(result);
        assertEquals(result.size(), expectedOrderNoOrigs.size());
        assertTrue(result.containsAll(expectedOrderNoOrigs));
    }

    @Test
    public void testQueryOrderNoOrigsByDNsAnlage() throws FindException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withRufnummernportierung(new RufnummernportierungAnlageTestBuilder()
                        .withOnkz("089")
                        .withDurchwahlnummer("12345678")
                        .addRufnummernblock(
                                new RufnummernblockBuilder()
                                        .withRnrBlockVon("00")
                                        .withRnrBlockBis("50")
                                        .build()
                        )
                        .addRufnummernblock(
                                new RufnummernblockBuilder()
                                        .withRnrBlockVon("80")
                                        .withRnrBlockBis("99")
                                        .build()
                        )
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                .buildValidWithoutVorabstimmungsId(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Set<Long> expectedOrderNoOrigs = Sets.newHashSet(1L);
        when(rufnummerService.findAuftragIdsByBlockrufnummer("89", "12345678")).thenReturn(expectedOrderNoOrigs);
        Set<Long> result = wbciVaKueMrnService.getOrderNoOrigsByDNs(wbciGeschaeftsfallKueMrn);
        verify(rufnummerService).findAuftragIdsByBlockrufnummer("89", "12345678");
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertTrue(result.containsAll(expectedOrderNoOrigs));
    }

    @Test
    public void testQueryOrderNoOrigsByDNsEinzeln() throws FindException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withRufnummernportierung(new RufnummernportierungEinzelnTestBuilder()
                        .addRufnummer(new RufnummerOnkzBuilder()
                                .withOnkz("089")
                                .withRufnummer("1234567890")
                                .build())
                        .addRufnummer(new RufnummerOnkzBuilder()
                                .withOnkz("089")
                                .withRufnummer("1234567891")
                                .build())
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                .buildValidWithoutVorabstimmungsId(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Set<Long> expectedOrderNoOrigs1 = Sets.newHashSet(1L);
        when(rufnummerService.findAuftragIdsByEinzelrufnummer("89", "1234567890")).thenReturn(expectedOrderNoOrigs1);
        Set<Long> expectedOrderNoOrigs2 = Sets.newHashSet(2L);
        when(rufnummerService.findAuftragIdsByEinzelrufnummer("89", "1234567891")).thenReturn(expectedOrderNoOrigs2);
        Set<Long> result = wbciVaKueMrnService.getOrderNoOrigsByDNs(wbciGeschaeftsfallKueMrn);
        verify(rufnummerService, times(2)).findAuftragIdsByEinzelrufnummer(eq("89"), startsWith("123456789"));
        assertNotNull(result);
        assertEquals(result.size(), expectedOrderNoOrigs1.size() + expectedOrderNoOrigs2.size());
        assertTrue(result.containsAll(expectedOrderNoOrigs1));
        assertTrue(result.containsAll(expectedOrderNoOrigs2));
    }

    @Test
    public void testGetOrderNoOrigsByGeoId() throws FindException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValidWithoutVorabstimmungsId(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Long geoId_1 = 1L;
        Long geoId_2 = 2L;
        List<Long> expectedGeoIds = Arrays.asList(geoId_1, geoId_2);
        when(wbciLocationService.getLocationGeoIds(wbciGeschaeftsfallKueMrn.getStandort())).thenReturn(expectedGeoIds);
        Set<Long> expectedOrderNoOrigsGeoId1 = Sets.newHashSet(11L, 12L);
        Set<Long> expectedOrderNoOrigsGeoId2 = Sets.newHashSet(13L);
        when(billingAuftragService.findAuftragIdsByGeoId(geoId_1, true)).thenReturn(expectedOrderNoOrigsGeoId1);
        when(billingAuftragService.findAuftragIdsByGeoId(geoId_2, true)).thenReturn(expectedOrderNoOrigsGeoId2);

        Set<Long> result = wbciVaKueMrnService.getOrderNoOrigsByGeoId(wbciGeschaeftsfallKueMrn);
        verify(wbciLocationService).getLocationGeoIds(wbciGeschaeftsfallKueMrn.getStandort());
        verify(billingAuftragService).findAuftragIdsByGeoId(geoId_1, true);
        verify(billingAuftragService).findAuftragIdsByGeoId(geoId_2, true);
        assertNotNull(result);
        assertEquals(result.size(), expectedOrderNoOrigsGeoId1.size() + expectedOrderNoOrigsGeoId2.size());
        assertTrue(result.containsAll(expectedOrderNoOrigsGeoId1));
        assertTrue(result.containsAll(expectedOrderNoOrigsGeoId2));
    }

    @Test
    public void shouldFindCancelledOrdersByGeoIdSinceNoActiveOrdersFoundMatchingGeoId() throws FindException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValidWithoutVorabstimmungsId(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Long geoId = 1L;
        List<Long> expectedGeoIds = Arrays.asList(geoId);
        when(wbciLocationService.getLocationGeoIds(wbciGeschaeftsfallKueMrn.getStandort())).thenReturn(expectedGeoIds);

        // simulate no active orders found matching geoId
        when(billingAuftragService.findAuftragIdsByGeoId(geoId, true)).thenReturn(Collections.<Long>emptySet());

        // simulate no active orders found matching geoId
        when(billingAuftragService.findAuftragIdsByGeoId(geoId, false)).thenReturn(Sets.newHashSet(geoId));

        Set<Long> result = wbciVaKueMrnService.getOrderNoOrigsByGeoId(wbciGeschaeftsfallKueMrn);
        verify(wbciLocationService).getLocationGeoIds(wbciGeschaeftsfallKueMrn.getStandort());
        verify(billingAuftragService).findAuftragIdsByGeoId(geoId, true);
        verify(billingAuftragService).findAuftragIdsByGeoId(geoId, false);
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertTrue(result.containsAll(expectedGeoIds));
    }

    @Test
    public void testGetOrderNoOrigsByGeoIdNoGeoIdFound() throws FindException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValidWithoutVorabstimmungsId(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciLocationService.getLocationGeoIds(any(Standort.class))).thenReturn(Collections.<Long>emptyList());

        Set<Long> result = wbciVaKueMrnService.getOrderNoOrigsByGeoId(wbciGeschaeftsfallKueMrn);
        verify(wbciLocationService).getLocationGeoIds(any(Standort.class));
        verify(billingAuftragService, never()).findAuftragIdsByGeoId(any(Long.class), eq(true));
        assertNotNull(result);
        assertEquals(result.size(), 0);
    }

    @Test
    public void testGetOrderNoOrigsByDNsVaRrnp() throws FindException {
        final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
        final GeschaeftsfallTyp gfTyp = GeschaeftsfallTyp.VA_RRNP;
        final String onkz = "89", durchwahlnummer = "100";
        WbciGeschaeftsfallRrnp wbciGeschaeftsfallRrnp = new WbciGeschaeftsfallRrnpTestBuilder()
                .withRufnummernportierung(
                        new RufnummernportierungAnlageTestBuilder()
                                .withOnkz(onkz)
                                .withDurchwahlnummer(durchwahlnummer)
                                .buildValid(wbciCdmVersion, gfTyp)
                )
                .buildValidWithoutVorabstimmungsId(wbciCdmVersion, gfTyp);

        Set<Long> expectedOrderNoOrigs = Sets.newHashSet(1L);
        when(rufnummerService.findAuftragIdsByBlockrufnummer(onkz, durchwahlnummer))
                .thenReturn(expectedOrderNoOrigs);

        Set<Long> result = wbciVaRrnpService.getOrderNoOrigsByDNs(wbciGeschaeftsfallRrnp);
        verify(rufnummerService).findAuftragIdsByBlockrufnummer(onkz, durchwahlnummer);
        verify(billingAuftragService, never()).findAuftragIdsByGeoId(any(Long.class), anyBoolean());
        assertEquals(result.size(), 1);
    }

    @DataProvider(name = "filterBillableDNs")
    public static Object[][] filterBillableDNs() {
        Rufnummer billable = new Rufnummer();
        billable.setNonBillable(null);
        Rufnummer billable2 = new Rufnummer();
        billable2.setNonBillable(false);
        Rufnummer nonBillable = new Rufnummer();
        nonBillable.setNonBillable(true);
        return new Object[][] {
                { Arrays.asList(billable, nonBillable), Arrays.asList(billable2, nonBillable), Arrays.asList(1L, 2L) },
                { Arrays.asList(nonBillable, nonBillable, billable2), Arrays.asList(billable, nonBillable), Arrays.asList(1L, 2L) },
                { Arrays.asList(billable, billable2), Arrays.asList(nonBillable), Arrays.asList(1L) },
                { Arrays.asList(nonBillable, billable2), Arrays.asList(nonBillable, nonBillable), Arrays.asList(1L) },
                { Arrays.asList(), Arrays.asList(billable2), Arrays.asList(2L) },
                { Arrays.asList(nonBillable, nonBillable), Arrays.asList(), Arrays.<Long>asList() },
        };
    }

    @Test(dataProvider = "filterBillableDNs")
    public void testFilterBillableTaifunOrders(List<Rufnummer> rufnummern1, List<Rufnummer> rufnummern2, List<Long> expectedBillingNos) throws Exception {
        Long billingNo1 = 1L;
        Long billingNo2 = 2L;
        when(rufnummerService.findAllRNs4Auftrag(billingNo1)).thenReturn(rufnummern1);
        when(rufnummerService.findAllRNs4Auftrag(billingNo2)).thenReturn(rufnummern2);

        //test only with one service is enough. The calls of the method have been already verified.
        Collection<OrderMatchVO> result = wbciVaKueOrnService.filterBillableTaifunOrders(Arrays.asList(new OrderMatchVO(1L), new OrderMatchVO(2L)));
        assertEquals(result.size(), expectedBillingNos.size());

        for (OrderMatchVO aResult : result) {
            assertTrue(expectedBillingNos.contains(aResult.getOrderNoOrig()));
        }
    }

    @Test
    public void testFilterBillableTaifunOrdersEmpty() throws Exception {
        List<OrderMatchVO> matchVOs = new ArrayList<>();
        assertEquals(wbciVaKueMrnService.filterBillableTaifunOrders(matchVOs), matchVOs);
        assertNull(wbciVaKueMrnService.filterBillableTaifunOrders(null));
    }


    /* Adress-Objekte fuer den Namens-Vergleich */
    private Map<Long, Adresse> buildTaifunAddressValues() {
        Map<Long, Adresse> orderToAddressMap = new HashMap<>();
        orderToAddressMap.put(1L, new AdresseBuilder().withName("Miller").withVorname("Hans").build());
        orderToAddressMap.put(2L, new AdresseBuilder().withName("Müller").withVorname("Hans").build());
        orderToAddressMap.put(3L, new AdresseBuilder().withName("Neubauer").withVorname("Martin").build());
        orderToAddressMap.put(4L, new AdresseBuilder().withName("Zirngibl").withVorname("Thomas").build());
        orderToAddressMap.put(5L, new AdresseBuilder().withName("Weigel").withVorname("Rolf").build());
        orderToAddressMap.put(6L, new AdresseBuilder().withName("Petz").withVorname("Adam").build());
        orderToAddressMap.put(7L, new AdresseBuilder().withName("Hartmann").withVorname("Sabine").build());
        orderToAddressMap.put(8L, new AdresseBuilder().withName("Kästle").withVorname("Yasmin").build());
        orderToAddressMap.put(9L, new AdresseBuilder().withName("Müller").withVorname("Franz-Josef").build());
        orderToAddressMap.put(10L, new AdresseBuilder().withName("Wertachklinik").withVorname("Bobingen").build());
        orderToAddressMap.put(11L, new AdresseBuilder().withName("Wertachklinik").withVorname("Bobingen und Schwabmünchen").build());
        orderToAddressMap.put(12L, new AdresseBuilder().withName("Meissner").withVorname("Ralf").build());
        orderToAddressMap.put(13L, new AdresseBuilder().withName("Meißner").withVorname("Ralph").build());
        orderToAddressMap.put(14L, new AdresseBuilder().withName("Firma ohne Zusatz").withVorname(null).build());
        orderToAddressMap.put(15L, new AdresseBuilder().withName("Firma").withVorname("mit Zusatz").build());
        orderToAddressMap.put(16L, new AdresseBuilder().withName("von der Gönna").withVorname("Ursula").build());
        orderToAddressMap.put(17L, new AdresseBuilder().withName("von Gönna").withVorname("Ursula").build());
        orderToAddressMap.put(18L, new AdresseBuilder().withName("Hupfburg").withVorname("Ursula von der").build());
        orderToAddressMap.put(19L, new AdresseBuilder().withName("Maier & Sohn").withVorname(null).build());
        orderToAddressMap.put(20L, new AdresseBuilder().withName("Maier u. Sohn").withVorname(null).build());
        orderToAddressMap.put(21L, new AdresseBuilder().withName("Meier und Sohn").withVorname(null).build());
        return orderToAddressMap;
    }

    @DataProvider(name = "filterAuftragIdsByNameDataProvider")
    public Object[][] filterAuftragIdsByNameDataProvider() {
        //@formatter:off
        return new Object[][] {
            { new PersonBuilder().withNachname("Müller").withVorname("Hans").build(), 2 }, // should match to 'Müller' and 'Miller'
            { new PersonBuilder().withNachname("Müller").withVorname(null).build(), 0 }, // shouldn't match to any 'Müller' and 'Miller'
            { new PersonBuilder().withNachname("Hartmann").withVorname("Sabine").build(), 1 }, // exact match
            { new PersonBuilder().withNachname("does-not-exist").withVorname("xyz").build(), 0 }, // no match
            { new PersonBuilder().withNachname("Waigl").withVorname("Ralf").build(), 1 }, // should match to 'Weigel Rolf'
            { new PersonBuilder().withNachname("Weigl").withVorname("Ralph").build(), 0 }, // should not match to 'Weigel Rolf' because Levenshtein distance to big!
            { new FirmaBuilder().withFirmename("Wertachklinik Bobingen").withFirmennamenZusatz(null).build(), 1 },
            { new FirmaBuilder().withFirmename("Wertachklinik").withFirmennamenZusatz("Bobingen").build(), 1 },
            { new PersonBuilder().withNachname("Meissner").withVorname("Ralf").build(), 2 }, // should match to 'Meissner Ralf' and 'Meißner Ralph'
            { new FirmaBuilder().withFirmename("Firma ohne Zusatz").withFirmennamenZusatz(null).build(), 1 }, // should match to 'Firma ohne Zusatz'
            { new FirmaBuilder().withFirmename("Firma").withFirmennamenZusatz(null).build(), 0 }, // should not match because 'Zusatz' is not defined
            { new PersonBuilder().withNachname("Gönna").withVorname("Ursula").build(), 2 }, // should match to "Ursula" /  "von der Gönna" and "Ursula" / "von Gönna"
            { new PersonBuilder().withNachname("Hupfburg").withVorname("Ursula").build(), 1 }, // should match to "Ursula von der" / "Hupfburg"
            { new FirmaBuilder().withFirmename("Maier und Sohn").withFirmennamenZusatz(null).build(), 3 }, // should match to "Maier & Sohn" and "Maier u. Sohn" and "Meier und Sohn"
            //@formatter:on
        };
    }

    @Test(dataProvider = "filterAuftragIdsByNameDataProvider")
    public void testFilterAuftragIdsByName(PersonOderFirma personOderFirma, int expectedSize) throws FindException {
        Map<Long, Adresse> orderToAddressMap = buildTaifunAddressValues();
        Set<Long> orderNoOrigs = new HashSet<>(orderToAddressMap.keySet());

        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withEndkunde(personOderFirma)
                .buildValidWithoutVorabstimmungsId(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        for (Long orderNoOrig : orderToAddressMap.keySet()) {
            when(billingAuftragService.findAnschlussAdresse4Auftrag(orderNoOrig, Endstelle.ENDSTELLEN_TYP_B))
                    .thenReturn(orderToAddressMap.get(orderNoOrig));
        }

        orderNoOrigs = wbciVaKueMrnService.filterAuftragIdsByName(wbciGeschaeftsfallKueMrn, orderNoOrigs);

        verify(billingAuftragService, times(orderToAddressMap.size())).findAnschlussAdresse4Auftrag(any(Long.class),
                eq(Endstelle.ENDSTELLEN_TYP_B));
        assertEquals(orderNoOrigs.size(), expectedSize);
    }

    @Test
    public void testFilterAuftragIdsByNameWithNullOriginalOrderNos() throws FindException {
        final Set<Long> orderNoOrigs = wbciVaKueMrnService.filterAuftragIdsByName(
                new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(WbciCdmVersion.V1,
                        GeschaeftsfallTyp.VA_KUE_MRN),
                null
        );
        assertNull(orderNoOrigs);
    }

    @Test
    public void testFilterAuftragIdsByNameWithEmptyOriginalOrderNos() throws FindException {
        final Set<Long> orderNoOrigs = wbciVaKueMrnService.filterAuftragIdsByName(
                new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(WbciCdmVersion.V1,
                        GeschaeftsfallTyp.VA_KUE_MRN),
                Collections.<Long>emptySet()
        );
        assertNotNull(orderNoOrigs);
        assertTrue(orderNoOrigs.isEmpty());
    }


    /**
     * asserts the found assignment candidates for machting *
     */
    private void assertOrderMatchVo(List<Pair<BAuftragVO, OrderMatchVO>> assignmentCandidates, BAuftragVO bAuftragVO,
            Long orderNoOrig, OrderMatchVO.BasicSearch baseSearch, OrderMatchVO.MatchViolation... matchViolations) {
        boolean bAuftragFound;
        boolean orderNoFound;
        boolean basicSearchMatch;
        boolean violationMatch;
        for (Pair<BAuftragVO, OrderMatchVO> candidate : assignmentCandidates) {
            bAuftragFound = candidate.getFirst().equals(bAuftragVO);
            orderNoFound = candidate.getSecond().getOrderNoOrig().compareTo(orderNoOrig) == 0;
            basicSearchMatch = candidate.getSecond().getBasicSearch().equals(baseSearch);
            if (CollectionUtils.isEmpty(candidate.getSecond().getMatchViolations())) {
                violationMatch = CollectionUtils.isEmpty(Sets.newHashSet(matchViolations));
            }
            else {
                violationMatch = Sets.newHashSet(matchViolations).containsAll(
                        candidate.getSecond().getMatchViolations());
            }
            // if at least one have found check that all categories match
            if (bAuftragFound || orderNoFound) {
                assertTrue(bAuftragFound, "billingAuftragVO not equal");
                assertTrue(orderNoFound, "billing order number not equal");
                assertTrue(basicSearchMatch, "BasicSearch attribute not equal");
                assertTrue(violationMatch, "Set of violationMatchtes not equal");
                return;
            }
        }
        // if nothing have found
        assertTrue(false, "No matching order assignment candidate have been found!");
    }
}
