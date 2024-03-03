/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.02.14
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wbci.model.GeschaeftsfallTyp.*;
import static de.mnet.wbci.model.WbciCdmVersion.*;
import static de.mnet.wbci.service.impl.WbciAutomationServiceImpl.*;
import static de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciAutomationValidationException;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.OverdueWitaOrderVO;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciWitaOrderDataVO;
import de.mnet.wbci.model.builder.ErledigtmeldungTestBuilder;
import de.mnet.wbci.model.builder.LeitungBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueOrnBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.model.CbVorgangData;
import de.mnet.wita.model.VorabstimmungAbgebend;
import de.mnet.wita.model.VorabstimmungAbgebendBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.model.WitaSendLimitBuilder;
import de.mnet.wita.service.TalAnbieterwechseltypService;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaVorabstimmungService;
import de.mnet.wita.service.WitaWbciServiceFacade;

/**
 *
 */
@Test(groups = UNIT)
public class WbciWitaServiceFacadeImplTest {

    @Mock
    private WitaTalOrderService witaTalOrderService;
    @Mock
    private CommonWorkflowService commonWorkflowService;
    @Mock
    private WitaVorabstimmungService witaVorabstimmungService;
    @Mock
    private CarrierService carrierService;
    @Mock
    private WbciCommonService wbciCommonService;
    @Mock
    private ReferenceService referenceService;
    @Mock
    private WbciDao wbciDao;
    @Mock
    private EndstellenService endstellenService;
    @Mock
    private CCAuftragService ccAuftragService;
    @Mock
    private AKUserService akUserService;
    @Mock
    private WitaConfigService witaConfigService;
    @Mock
    private TalAnbieterwechseltypService talAnbieterwechseltypService;
    @Mock
    private WitaWbciServiceFacade witaWbciServiceFacade;
    @Mock
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;
    @Mock
    private CarrierElTALService carrierElTALService;

    @Spy
    @InjectMocks
    private WbciWitaServiceFacadeImpl testling;

    @DataProvider(name = "vorabstimmungen")
    public static Object[][] vorabstimmungen() {
        return new Object[][] {
                { CarrierRole.ABGEBEND, 1L, WbciRequestStatus.VA_VERSENDET, null, false, false },
                { CarrierRole.AUFNEHMEND, 1L, WbciRequestStatus.RUEM_VA_VERSENDET, null, false, false },
                { CarrierRole.ABGEBEND, null, WbciRequestStatus.RUEM_VA_VERSENDET, null, false, false },
                { CarrierRole.ABGEBEND, 1L, WbciRequestStatus.RUEM_VA_VERSENDET, "positive WBCI-Vorabstimmung", true, false },
                { CarrierRole.ABGEBEND, 1L, WbciRequestStatus.ABBM_VERSENDET, "negative WBCI-Vorabstimmung", false, true },
                { CarrierRole.ABGEBEND, 1L, WbciRequestStatus.TV_ERLM_VERSENDET, "positive WBCI-Terminverschiebung", true, true },
                { CarrierRole.ABGEBEND, 1L, WbciRequestStatus.STORNO_ERLM_VERSENDET, "stornierte WBCI-Vorabstimmung", false, true },
                { CarrierRole.ABGEBEND, 1L, WbciRequestStatus.STORNO_ERLM_EMPFANGEN, "stornierte WBCI-Vorabstimmung", false, true },
        };
    }

    @DataProvider(name = "wbciGFs")
    public static Object[][] wbciGFs() {
        return new Object[][] {
                { VA_KUE_MRN, AkmTrAutomationTask.ANBIETERWECHSEL },
                { VA_KUE_MRN, AkmTrAutomationTask.NEUBESTELLUNG },
                { VA_KUE_ORN, AkmTrAutomationTask.NEUBESTELLUNG },
                { VA_KUE_ORN, AkmTrAutomationTask.ANBIETERWECHSEL },
                { VA_RRNP, AkmTrAutomationTask.NEUBESTELLUNG },
                { VA_RRNP, AkmTrAutomationTask.ANBIETERWECHSEL } };
    }

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(dataProvider = "vorabstimmungen")
    public void testUpdateOrCreateWitaVorabstimmungAbgebend(CarrierRole carrierRole, Long auftragId, WbciRequestStatus requestStatus, String bemerkungPrefix, Boolean active, Boolean hasAkmTr) throws Exception {
        String vaId = "DEU.DTAG.V00000000001";
        LocalDateTime abgestimmerTermin = LocalDateTime.now();
        Carrier carrier = new Carrier();
        doReturn(carrier).when(testling).getCarrier(CarrierCode.DTAG);

        WbciRequest requestMock = mock(WbciRequest.class);
        WbciGeschaeftsfall wbciGfMock = mock(WbciGeschaeftsfall.class);
        when(requestMock.getRequestStatus()).thenReturn(requestStatus);
        when(requestMock.getWbciGeschaeftsfall()).thenReturn(wbciGfMock);
        when(wbciGfMock.getVorabstimmungsId()).thenReturn(vaId);
        when(wbciGfMock.getAuftragId()).thenReturn(auftragId);
        when(wbciGfMock.getWechseltermin()).thenReturn(abgestimmerTermin.toLocalDate());
        when(wbciGfMock.getEKPPartner()).thenReturn(CarrierCode.DTAG);
        if (carrierRole.equals(CarrierRole.AUFNEHMEND)) {
            when(wbciGfMock.getAufnehmenderEKP()).thenReturn(CarrierCode.MNET);
            when(wbciGfMock.getAbgebenderEKP()).thenReturn(CarrierCode.DTAG);
        }
        else {
            when(wbciGfMock.getAufnehmenderEKP()).thenReturn(CarrierCode.DTAG);
            when(wbciGfMock.getAbgebenderEKP()).thenReturn(CarrierCode.MNET);
        }
        //mock should return the assigned object
        when(witaVorabstimmungService.saveVorabstimmungAbgebend(any(VorabstimmungAbgebend.class))).thenAnswer(new Answer<VorabstimmungAbgebend>() {
            @Override
            public VorabstimmungAbgebend answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (VorabstimmungAbgebend) invocationOnMock.getArguments()[0];
            }
        });

        when(wbciCommonService.findLastForVaId(anyString(), eq(UebernahmeRessourceMeldung.class))).thenReturn(hasAkmTr ? mock(UebernahmeRessourceMeldung.class) : null);
        VorabstimmungAbgebend result = testling.updateOrCreateWitaVorabstimmungAbgebend(requestMock);
        assertVorabstimmungAbgebend(result, bemerkungPrefix, auftragId, abgestimmerTermin, carrier, vaId, active);
        verify(witaVorabstimmungService, times(bemerkungPrefix == null ? 0 : 1)).findVorabstimmungAbgebend("B", auftragId);

        when(witaVorabstimmungService.findVorabstimmungAbgebend("B", auftragId)).thenReturn(result);
        assertVorabstimmungAbgebend(testling.updateOrCreateWitaVorabstimmungAbgebend(requestMock), bemerkungPrefix, auftragId, abgestimmerTermin, carrier, vaId, active);
        verify(witaVorabstimmungService, times(bemerkungPrefix == null ? 0 : 2)).findVorabstimmungAbgebend("B", auftragId);

        //test for ABBM - wechseltermin isn't set
        when(wbciGfMock.getWechseltermin()).thenReturn(null);
        when(wbciGfMock.getKundenwunschtermin()).thenReturn(abgestimmerTermin.toLocalDate());
        assertVorabstimmungAbgebend(testling.updateOrCreateWitaVorabstimmungAbgebend(requestMock), bemerkungPrefix, auftragId, abgestimmerTermin, carrier, vaId, active);
    }

    private void assertVorabstimmungAbgebend(VorabstimmungAbgebend result, String bemerkungPrefix, Long auftragId, LocalDateTime wechseltermin, Carrier carrier, String vaId, Boolean active) {
        if (bemerkungPrefix == null) {
            assertNull(result);
            return;
        }
        assertNotNull(result);
        assertEquals(result.getAuftragId(), auftragId);
        assertEquals(result.getAbgestimmterProdiverwechsel(), wechseltermin.toLocalDate());
        assertEquals(result.getCarrier(), carrier);
        assertEquals(result.getEndstelleTyp(), "B");
        assertEquals(result.getBemerkung().substring(0, result.getBemerkung().indexOf(" -")), bemerkungPrefix);
        assertTrue(result.getBemerkung().endsWith(" (" + vaId + ")"));
        assertEquals(result.getRueckmeldung(), active);
    }

    @Test
    public void testUpdateWitaVorabstimmungAbgebend() {
        final String portierungskennung = "D001";
        final Carrier carrier = new CarrierBuilder().setPersist(false).withPortierungskennung(portierungskennung).build();
        final WbciGeschaeftsfall wbciGf = getKueMrn();
        final UebernahmeRessourceMeldung akmtr = getAkmTr(wbciGf, false, true, portierungskennung);
        final String bemerkung = "Bemerkung zu der Vorabstimmung";
        final VorabstimmungAbgebend vorabstimmungAbgebend =
                new VorabstimmungAbgebendBuilder()
                        .setPersist(false)
                        .withBemerkung(bemerkung)
                        .withCarrier(null)
                        .build();
        when(witaVorabstimmungService.findVorabstimmungAbgebend(Endstelle.ENDSTELLEN_TYP_B, wbciGf.getAuftragId()))
                .thenReturn(vorabstimmungAbgebend);
        when(witaVorabstimmungService.saveVorabstimmungAbgebend(vorabstimmungAbgebend)).thenReturn(vorabstimmungAbgebend);
        doReturn(carrier).when(testling).getCarrier(portierungskennung);

        final VorabstimmungAbgebend witaVorabstimmungAbgebend = testling.updateWitaVorabstimmungAbgebend(akmtr);

        assertEquals(witaVorabstimmungAbgebend.getCarrier(), carrier);
        verify(witaVorabstimmungService).findVorabstimmungAbgebend(Endstelle.ENDSTELLEN_TYP_B, wbciGf.getAuftragId());
        verify(witaVorabstimmungService).saveVorabstimmungAbgebend(vorabstimmungAbgebend);
        verify(testling).getCarrier(portierungskennung);
    }

    @Test
    public void testUpdateWitaVorabstimmungAbgebendUebernahmeNein() {
        final WbciGeschaeftsfall wbciGf = getKueMrn();
        final UebernahmeRessourceMeldung akmtr = getAkmTr(wbciGf, false, false, null);
        final VorabstimmungAbgebend witaVorabstimmungAbgebend = testling.updateWitaVorabstimmungAbgebend(akmtr);

        assertNull(witaVorabstimmungAbgebend);
        verify(witaVorabstimmungService, times(0)).findVorabstimmungAbgebend(anyString(), anyLong());
        verify(witaVorabstimmungService, times(0)).saveVorabstimmungAbgebend(any(VorabstimmungAbgebend.class));
    }

    @Test
    public void testUpdateWitaVorabstimmungAbgebendWithKueOrn() {
        final WbciGeschaeftsfall wbciGf = getKueOrn();
        final UebernahmeRessourceMeldung akmtr = getAkmTr(wbciGf, false, true, null);

        final VorabstimmungAbgebend witaVorabstimmungAbgebend = testling.updateWitaVorabstimmungAbgebend(akmtr);

        assertNull(witaVorabstimmungAbgebend);
        verify(witaVorabstimmungService, times(0)).findVorabstimmungAbgebend(anyString(), anyLong());
        verify(witaVorabstimmungService, times(0)).saveVorabstimmungAbgebend(any(VorabstimmungAbgebend.class));
    }

    @Test
    public void testUpdateWitaVorabstimmungAbgebendMnetAufnehmend() {
        final WbciGeschaeftsfall wbciGf = getKueMrn(CarrierCode.EINS_UND_EINS, CarrierCode.MNET);
        final UebernahmeRessourceMeldung akmtr = getAkmTr(wbciGf, false, true, null);
        final VorabstimmungAbgebend witaVorabstimmungAbgebend = testling.updateWitaVorabstimmungAbgebend(akmtr);

        assertNull(witaVorabstimmungAbgebend);
        verify(witaVorabstimmungService, times(0)).findVorabstimmungAbgebend(anyString(), anyLong());
        verify(witaVorabstimmungService, times(0)).saveVorabstimmungAbgebend(any(VorabstimmungAbgebend.class));
    }

    @Test
    public void testUpdateWitaVorabstimmungAbgebendNoVaFound() {
        final WbciGeschaeftsfall wbciGf = getKueMrn();
        final UebernahmeRessourceMeldung akmtr = getAkmTr(wbciGf, false, true, null);

        when(witaVorabstimmungService.findVorabstimmungAbgebend(Endstelle.ENDSTELLEN_TYP_B, wbciGf.getAuftragId()))
                .thenReturn(null);

        final VorabstimmungAbgebend witaVorabstimmungAbgebend = testling.updateWitaVorabstimmungAbgebend(akmtr);

        assertNull(witaVorabstimmungAbgebend);
        verify(witaVorabstimmungService).findVorabstimmungAbgebend(Endstelle.ENDSTELLEN_TYP_B, wbciGf.getAuftragId());
        verify(witaVorabstimmungService, times(0)).saveVorabstimmungAbgebend(any(VorabstimmungAbgebend.class));
        verify(testling, times(0)).getCarrier(anyString());
    }

    @Test
    public void isWitaCbVorgangPresentFound() throws FindException {
        String vorabstimmungsId = "DEU.MNET.VH12345678";
        when(witaTalOrderService.findCBVorgaengeByVorabstimmungsId(vorabstimmungsId)).thenReturn(Collections.singletonList(new WitaCBVorgang()));
        assertEquals(testling.findWitaCbVorgaenge(vorabstimmungsId).size(), 1);
        verify(witaTalOrderService).findCBVorgaengeByVorabstimmungsId(vorabstimmungsId);
    }

    @Test
    public void isWitaCbVorgangPresentNotFound() throws FindException {
        String vorabstimmungsId = "DEU.MNET.VH12345678";
        when(witaTalOrderService.findCBVorgaengeByVorabstimmungsId(vorabstimmungsId)).thenReturn(Collections.emptyList());
        assertTrue(CollectionUtils.isEmpty(testling.findWitaCbVorgaenge(vorabstimmungsId)));
        verify(witaTalOrderService).findCBVorgaengeByVorabstimmungsId(vorabstimmungsId);
    }

    @Test
    public void testFindSingleActiveWitaCbVorgang() throws FindException {
        String vorabstimmungsId = "DEU.MNET.VH12345678";
        when(witaTalOrderService.findCBVorgaengeByVorabstimmungsId(vorabstimmungsId)).thenReturn(Collections.singletonList(new WitaCBVorgang()));
        when(commonWorkflowService.isProcessInstanceAlive(anyString())).thenReturn(true);
        assertNotNull(testling.findSingleActiveWitaCbVorgang(vorabstimmungsId));
    }

    @Test
    public void testFindSingleActiveWitaCbVorgangMultipleActive() throws FindException {
        String vorabstimmungsId = "DEU.MNET.VH12345678";
        when(witaTalOrderService.findCBVorgaengeByVorabstimmungsId(vorabstimmungsId)).thenReturn(
                Arrays.asList(new WitaCBVorgang(), new WitaCBVorgang()));
        when(commonWorkflowService.isProcessInstanceAlive(anyString())).thenReturn(true);
        assertNull(testling.findSingleActiveWitaCbVorgang(vorabstimmungsId));
    }

    @Test
    public void testGetCarrierByCarrierCode() throws Exception {
        Carrier expectedCarrier = new Carrier();
        when(carrierService.findCarrierByCarrierCode(CarrierCode.DTAG)).thenReturn(expectedCarrier);
        assertEquals(testling.getCarrier(CarrierCode.DTAG), expectedCarrier);
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testGetCarrierByCarrierCodeException() throws Exception {
        when(carrierService.findCarrierByCarrierCode(any(CarrierCode.class))).thenThrow(FindException.class);
        testling.getCarrier(CarrierCode.DTAG);
    }

    @Test
    public void testGetCarrierByPortierungskennung() throws Exception {
        Carrier expectedCarrier = new Carrier();
        when(carrierService.findCarrierByPortierungskennung("D001")).thenReturn(expectedCarrier);
        assertEquals(testling.getCarrier("D001"), expectedCarrier);
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testGetCarrierByPortierungskennungException() throws Exception {
        when(carrierService.findCarrierByPortierungskennung(anyString())).thenThrow(FindException.class);
        testling.getCarrier("D001");
    }

    @Test
    public void testGetOverduePreagreementsWithMissingWitaOrder() throws Exception {
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder()
                .withWbciGeschaeftsfall(new WbciGeschaeftsfallKueMrnTestBuilder().build())
                .buildValid(V1, VA_KUE_MRN);
        when(wbciDao.findOverdueAkmTrsNearToWechseltermin(WbciWitaServiceFacadeImpl.OVERDUE_DAYS_FOR_MISSING_WITA_ORDERS, CarrierCode.MNET, Technologie.getWitaOrderRelevantTechnologies()))
                .thenReturn(Collections.singletonList(akmTr));

        OverdueWitaOrderVO testVo = new OverdueWitaOrderVO();
        List<WitaCBVorgang> cbVorgangList = new ArrayList<>();
        doReturn(cbVorgangList).when(testling).findWitaCbVorgaenge(akmTr.getVorabstimmungsId());
        doReturn(testVo).when(testling).checkWitaVorgangIsCreated(cbVorgangList, akmTr);

        List<OverdueWitaOrderVO> result = testling.getOverduePreagreementsWithMissingWitaOrder();
        assertEquals(result.size(), 1);
        assertEquals(result.iterator().next(), testVo);
        verify(wbciDao).findOverdueAkmTrsNearToWechseltermin(WbciWitaServiceFacadeImpl.OVERDUE_DAYS_FOR_MISSING_WITA_ORDERS, CarrierCode.MNET, Technologie.getWitaOrderRelevantTechnologies());
        verify(testling).findWitaCbVorgaenge(akmTr.getVorabstimmungsId());
        verify(testling).checkWitaVorgangIsCreated(cbVorgangList, akmTr);

        //on empty list, return empty result-List
        when(wbciDao.findOverdueAkmTrsNearToWechseltermin(WbciWitaServiceFacadeImpl.OVERDUE_DAYS_FOR_MISSING_WITA_ORDERS, CarrierCode.MNET, Technologie.getWitaOrderRelevantTechnologies()))
                .thenReturn(new ArrayList<>());
        result = testling.getOverduePreagreementsWithMissingWitaOrder();
        assertEquals(result.size(), 0);
    }

    @Test
    public void testIsWitaRelevantOrderCreated() throws Exception {
        doReturn("answered").when(testling).getStatusName(WitaCBVorgang.STATUS_ANSWERED, true);
        doReturn("storno").when(testling).getStatusName(WitaCBVorgang.STATUS_STORNO, false);
        String bezMnet = "BEZ_MNET";

        WitaCBVorgang witaCBVorgang_OK = mock(WitaCBVorgang.class);
        when(witaCBVorgang_OK.getStatus()).thenReturn(WitaCBVorgang.STATUS_ANSWERED);
        when(witaCBVorgang_OK.getBezeichnungMnet()).thenReturn(bezMnet);
        when(witaCBVorgang_OK.getReturnOk()).thenReturn(true);
        WitaCBVorgang witaCBVorgang_CLOSED_OK = mock(WitaCBVorgang.class);
        when(witaCBVorgang_CLOSED_OK.getStatus()).thenReturn(WitaCBVorgang.STATUS_CLOSED);
        when(witaCBVorgang_CLOSED_OK.getBezeichnungMnet()).thenReturn(bezMnet);
        when(witaCBVorgang_CLOSED_OK.getReturnOk()).thenReturn(true);
        WitaCBVorgang witaCBVorgang_NOT_OK = mock(WitaCBVorgang.class);
        when(witaCBVorgang_NOT_OK.getStatus()).thenReturn(WitaCBVorgang.STATUS_STORNO);
        when(witaCBVorgang_NOT_OK.getBezeichnungMnet()).thenReturn(bezMnet);
        when(witaCBVorgang_OK.getReturnOk()).thenReturn(false);

        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder()
                .addLeitung(new LeitungBuilder()
                        .withVertragsnummer("V500").build())
                .buildValid(V1, VA_KUE_MRN);
        akmTr.getWbciGeschaeftsfall().setWechseltermin(DateCalculationHelper.getDateInWorkingDaysFromNow(3).toLocalDate());

        //no wita fitting order created
        OverdueWitaOrderVO result = testling.checkWitaVorgangIsCreated(
                Collections.singletonList(witaCBVorgang_NOT_OK),
                akmTr);
        assertNotNull(result);
        assertOverdueWitaOrderVoAkmTrData(result, akmTr);
        assertWitaInformationIncluded(result, bezMnet, "storno");

        //1 wita fitting order and 1 not fitting created
        result = testling.checkWitaVorgangIsCreated(
                Arrays.asList(witaCBVorgang_NOT_OK, witaCBVorgang_OK),
                akmTr);
        assertNull(result);

        //1 wita fitting order created
        result = testling.checkWitaVorgangIsCreated(
                Arrays.asList(witaCBVorgang_NOT_OK, witaCBVorgang_OK),
                akmTr);
        assertNull(result);

        //1 wita fitting order (closed, positive answered) created
        result = testling.checkWitaVorgangIsCreated(
                Arrays.asList(witaCBVorgang_NOT_OK, witaCBVorgang_CLOSED_OK),
                akmTr);
        assertNull(result);
    }

    @Test
    public void testGenerateWitaDataForPreAggreement() throws Exception {
        final Long hurricanOrderId = 111L;
        WbciGeschaeftsfallKueMrn wbciGf = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAuftragId(hurricanOrderId)
                .buildValid(V1, VA_KUE_MRN);
        when(wbciCommonService.findWbciGeschaeftsfall(wbciGf.getVorabstimmungsId())).thenReturn(wbciGf);
        final Long endstellId = 55L;
        final Long hvtID = 552L;
        Endstelle endstelle = mock(Endstelle.class);
        when(endstelle.getId()).thenReturn(endstellId);
        when(endstelle.getHvtIdStandort()).thenReturn(hvtID);
        when(endstellenService.findEndstelle4Auftrag(hurricanOrderId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle);
        Carrierbestellung cb = mock(Carrierbestellung.class);
        when(carrierService.findLastCB4Endstelle(endstellId)).thenReturn(cb);
        Carrier carrier = mock(Carrier.class);
        when(carrierService.findCarrier4HVT(hvtID)).thenReturn(carrier);
        AuftragDaten ad = mock(AuftragDaten.class);
        when(ccAuftragService.findAuftragDatenByAuftragId(hurricanOrderId)).thenReturn(ad);

        WbciWitaOrderDataVO result = testling.generateWitaDataForPreAggreement(wbciGf.getVorabstimmungsId());
        assertEquals(result.getWbciGeschaeftsfall(), wbciGf);
        assertEquals(result.getVorabstimmungsId(), wbciGf.getVorabstimmungsId());
        assertEquals(result.getAuftragDaten(), ad);
        assertEquals(result.getCarrier(), carrier);
        assertEquals(result.getCarrierbestellung(), cb);
        assertEquals(result.getEndstelle(), endstelle);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Unexpected error during the generation of the wita order data for VA-ID '.*'.*")
    public void testGenerateWitaDataForPreAggreementException() throws Exception {
        final Long hurricanOrderId = 111L;
        WbciGeschaeftsfallKueMrn wbciGf = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAuftragId(hurricanOrderId)
                .buildValid(V1, VA_KUE_MRN);
        when(wbciCommonService.findWbciGeschaeftsfall(wbciGf.getVorabstimmungsId())).thenReturn(wbciGf);
        testling.generateWitaDataForPreAggreement(wbciGf.getVorabstimmungsId());
    }

    private void assertWitaInformationIncluded(OverdueWitaOrderVO result, String bezMnet, String status) {
        boolean found = false;
        for (Pair<String, String> p : result.getAssignedWitaOrders()) {
            if (bezMnet.equals(p.getFirst()) && status.equals(p.getSecond())) {
                found = true;
            }
        }
        assertTrue(found, String.format("the expected wita information pair [%s,%s] have not be found in %s",
                bezMnet, status, result.getAssignedWitaOrders().toString()));
    }

    private void assertOverdueWitaOrderVoAkmTrData(OverdueWitaOrderVO result, UebernahmeRessourceMeldung akmTr) {
        assertEquals(result.getmNetTechnologie(), akmTr.getWbciGeschaeftsfall().getMnetTechnologie());
        assertEquals(result.getAuftragId(), akmTr.getWbciGeschaeftsfall().getAuftragId());
        assertEquals(result.getAuftragNoOrig(), akmTr.getWbciGeschaeftsfall().getBillingOrderNoOrig());
        assertEquals(result.getVaid(), akmTr.getWbciGeschaeftsfall().getVorabstimmungsId());
        assertEquals(result.getEkpAuf(), CarrierCode.MNET);
        assertEquals(result.getWechseltermin(), Date.from(akmTr.getWbciGeschaeftsfall().getWechseltermin().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    @Test
    public void testGetStatusName() throws Exception {
        assertEquals("UNKNOWN", testling.getStatusName(null, null));

        String strValue = "beantwortet";
        when(referenceService.findReference(WitaCBVorgang.STATUS_ANSWERED)).thenReturn(new ReferenceBuilder().withStrValue(strValue).build());
        assertEquals(strValue, testling.getStatusName(WitaCBVorgang.STATUS_ANSWERED, null));

        when(referenceService.findReference(WitaCBVorgang.STATUS_ANSWERED)).thenReturn(new ReferenceBuilder().withStrValue(strValue).build());
        assertEquals(strValue + ", positive Rückmeldung", testling.getStatusName(WitaCBVorgang.STATUS_ANSWERED, true));

        when(referenceService.findReference(WitaCBVorgang.STATUS_ANSWERED)).thenReturn(new ReferenceBuilder().withStrValue(strValue).build());
        assertEquals(strValue + ", negative Rückmeldung", testling.getStatusName(WitaCBVorgang.STATUS_ANSWERED, false));
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Waehrend der Suche nach CB-Vorgangs-Status '1000' ist ein unerwarteter Fehler aufgetreten\\.")
    public void testGetStatusNameException() throws Exception {
        when(referenceService.findReference(anyLong())).thenThrow(new FindException("TEST"));
        testling.getStatusName(1000L, null);
    }

    @Test
    public void testCreateWitaVorgangAutomationEntryLog() throws Exception {
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder().buildValid(V1, VA_KUE_MRN);
        when(wbciCommonService.findLastForVaId(akmTr.getVorabstimmungsId(), UebernahmeRessourceMeldung.class)).thenReturn(akmTr);

        WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder().withCarrierRefNr("22").build();
        doReturn(new WbciWitaOrderDataVO()).when(testling).generateWitaDataForPreAggreement(anyString());
        doReturn(witaCBVorgang).when(testling).createWitaVorgang(eq(AkmTrAutomationTask.NEUBESTELLUNG), any(WbciWitaOrderDataVO.class));

        AKUser user = new AKUser();
        WitaCBVorgang result = testling.createWitaVorgang(AkmTrAutomationTask.NEUBESTELLUNG, akmTr.getVorabstimmungsId(), user);
        assertEquals(result, witaCBVorgang);
        verify(wbciGeschaeftsfallService).createOrUpdateAutomationTask(akmTr.getWbciGeschaeftsfall(), akmTr,
                AutomationTask.TaskName.WITA_SEND_NEUBESTELLUNG,
                AutomationTask.AutomationStatus.COMPLETED,
                "WITA-Vorgang mit der externen Auftragsnummer '22' automatisch erzeugt", user);
    }


    @Test
    public void testCreateWitaCancellations() {
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder().buildValid(V1, VA_KUE_MRN);

        WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder().withCarrierRefNr("22").build();
        WbciWitaOrderDataVO vo = new WbciWitaOrderDataVO();

        SortedSet<String> witaVtrNrs = new TreeSet<>();
        witaVtrNrs.add("123456");

        when(wbciCommonService.getHurricanOrderIdForWitaVtrNrAndCurrentVA(anyString(), anyLong(), any(Set.class))).thenReturn(1L);
        when(wbciCommonService.findWbciGeschaeftsfall(anyString())).thenReturn(akmTr.getWbciGeschaeftsfall());
        doReturn(vo).when(testling).generateWitaDataForPreAggreement(any(WbciGeschaeftsfall.class), anyLong());
        doReturn(witaCBVorgang).when(testling).createWitaVorgang(eq(AkmTrAutomationTask.KUENDIGUNG), eq(vo));

        AKUser user = new AKUser();
        List<WitaCBVorgang> result = testling.createWitaCancellations(akmTr, witaVtrNrs, user);
        assertEquals(result.size(), 1);
        verify(wbciGeschaeftsfallService).createOrUpdateAutomationTask(akmTr.getWbciGeschaeftsfall(), akmTr,
                AutomationTask.TaskName.WITA_SEND_KUENDIGUNG,
                AutomationTask.AutomationStatus.COMPLETED,
                "WITA-Vorgang mit der externen Auftragsnummer '22' automatisch erzeugt", user);
    }


    @Test(dataProvider = "wbciGFs")
    public void testCreateWitaVorgang(de.mnet.wbci.model.GeschaeftsfallTyp wbciGf, AkmTrAutomationTask task) throws Exception {
        Long auftragId = 234L;
        Date wt = new Date();
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder().buildValid(V1, wbciGf);
        akmTr.getWbciGeschaeftsfall().setAuftragId(auftragId);
        akmTr.getWbciGeschaeftsfall().setWechseltermin(DateConverterUtils.asLocalDate(wt));
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(auftragId);

        Long carrierID = Carrier.ID_DTAG;
        Carrier carrier = new Carrier();
        carrier.setId(carrierID);

        Long cbID = 55L;
        Carrierbestellung cb = new Carrierbestellung();
        cb.setId(cbID);

        WbciWitaOrderDataVO vo = new WbciWitaOrderDataVO(akmTr.getWbciGeschaeftsfall(), auftragDaten, cb, carrier, null);
        AKUser user = new AKUser();
        doReturn(vo).when(testling).generateWitaDataForPreAggreement(akmTr.getVorabstimmungsId());
        doReturn(WitaCBVorgang.ANBIETERWECHSEL_46TKG).when(testling).determineMontagehinweis(vo, task.getCbVorgangTyp());
        doNothing().when(testling).checkIfTalOrderExists(any(Carrierbestellung.class));
        when(witaTalOrderService.checkAutoClosingAllowed(auftragDaten, carrierID, task.getCbVorgangTyp())).thenReturn(true);
        doReturn(user).when(testling).findUser(vo.getWbciGeschaeftsfall());
        if (!wbciGf.equals(VA_KUE_ORN)) {
            doReturn(Sets.newHashSet(1L, 2L)).when(testling).getRufnummerIds(akmTr.getVorabstimmungsId());
        }

        CBVorgang cbVorgang = new WitaCBVorgang();
        ArgumentCaptor<CbVorgangData> cbVorgangDataArgumentCaptor = ArgumentCaptor.forClass(CbVorgangData.class);
        when(witaTalOrderService.createCBVorgang(cbVorgangDataArgumentCaptor.capture())).thenReturn(Collections.singletonList(cbVorgang));

        WitaCBVorgang result = testling.createWitaVorgang(task, vo);
        assertEquals(result, cbVorgang);
        CbVorgangData cbv = cbVorgangDataArgumentCaptor.getValue();
        assertEquals(cbv.getCbId(), cbID);
        assertEquals(cbv.getAuftragIds().iterator().next(), auftragId);
        assertEquals(cbv.getAuftragIds().size(), 1);
        assertEquals(cbv.getCarrierId(), carrierID);
        assertEquals(cbv.getVorgabe(), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        assertEquals(cbv.getCbVorgangTyp(), task.getCbVorgangTyp());
        assertEquals(cbv.getMontagehinweis(), WitaCBVorgang.ANBIETERWECHSEL_46TKG);
        assertTrue(cbv.getAnbieterwechselTkg46());
        assertTrue(cbv.getAutomation());
        assertEquals(cbv.getUser(), user);
        assertEquals(cbv.getVorabstimmungsId(), akmTr.getVorabstimmungsId());
        if (!wbciGf.equals(VA_KUE_ORN)) {
            assertEquals(cbv.getRufnummerIds().size(), 2);
        }
        else {
            assertTrue(CollectionUtils.isEmpty(cbv.getRufnummerIds()));
            verify(testling, never()).getRufnummerIds(anyString());
        }
    }


    @Test
    public void testCreateWitaVorgangNewCbCreatedIfNotExist() throws StoreException, AKAuthenticationException, FindException {
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder().buildValid(V1, VA_KUE_MRN);
        akmTr.getWbciGeschaeftsfall().setWechseltermin(LocalDate.now().plusDays(14));

        WbciWitaOrderDataVO vo = new WbciWitaOrderDataVO(
                akmTr.getWbciGeschaeftsfall(),
                new AuftragDatenBuilder().setPersist(false).build(),
                null,
                new CarrierBuilder().setPersist(false).build(),
                null);
        doReturn(vo).when(testling).generateWitaDataForPreAggreement(anyString());
        doReturn("montage").when(testling).determineMontagehinweis(eq(vo), anyLong());
        doReturn(new AKUser()).when(testling).findUser(any(WbciGeschaeftsfall.class));
        doReturn(null).when(testling).getRufnummerIds(anyString());
        doNothing().when(testling).checkIfTalOrderExists(any(Carrierbestellung.class));

        CBVorgang cbVorgang = new WitaCBVorgang();
        ArgumentCaptor<CbVorgangData> cbVorgangDataArgumentCaptor = ArgumentCaptor.forClass(CbVorgangData.class);
        when(witaTalOrderService.createCBVorgang(cbVorgangDataArgumentCaptor.capture())).thenReturn(Collections.singletonList(cbVorgang));

        testling.createWitaVorgang(AkmTrAutomationTask.ANBIETERWECHSEL, vo);

        assertNotNull(vo.getCarrierbestellung());
        assertEquals(vo.getCarrierbestellung().getCarrier(), Carrier.ID_DTAG);
        assertEquals(vo.getCarrierbestellung().getVorgabedatum(), Date.from(akmTr.getWbciGeschaeftsfall().getWechseltermin().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        assertNotNull(vo.getCarrierbestellung().getBestelltAm());
        assertNull(vo.getCarrierbestellung().getZurueckAm());
        assertNull(vo.getCarrierbestellung().getBereitstellungAm());
        assertNull(vo.getCarrierbestellung().getVtrNr());
    }


    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = ".*besitzt keinen Wechseltermin!")
    public void testCreateWitaVorgangWbciServiceException() throws Exception {
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder().buildValid(V1, VA_KUE_MRN);
        WbciWitaOrderDataVO vo = new WbciWitaOrderDataVO(akmTr.getWbciGeschaeftsfall(), null, null, null, null);

        doThrow(new WbciValidationException("TEST")).when(testling)
                .createNewCarrierbestellungIfNotExist(any(WbciGeschaeftsfall.class), any(WbciWitaOrderDataVO.class));
        testling.createWitaVorgang(AkmTrAutomationTask.ANBIETERWECHSEL, vo);
    }

    @Test(expectedExceptions = WbciAutomationValidationException.class, expectedExceptionsMessageRegExp = "TEST")
    public void testCreateWitaVorgangAutomationValidationException() throws Exception {
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder().buildValid(V1, VA_KUE_MRN);
        akmTr.getWbciGeschaeftsfall().setWechseltermin(LocalDate.now());

        WbciWitaOrderDataVO vo = new WbciWitaOrderDataVO(akmTr.getWbciGeschaeftsfall(),
                new AuftragDatenBuilder().withRandomAuftragId().setPersist(false).build(), null, null, null);

        doThrow(new WbciAutomationValidationException("TEST")).when(testling)
                .createNewCarrierbestellungIfNotExist(any(WbciGeschaeftsfall.class), any(WbciWitaOrderDataVO.class));
        testling.createWitaVorgang(AkmTrAutomationTask.ANBIETERWECHSEL, vo);
    }

    @Test(expectedExceptions = WbciAutomationValidationException.class, expectedExceptionsMessageRegExp = "TEST")
    public void testCreateWitaVorgangStoreException() throws Exception {
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder().buildValid(V1, VA_KUE_ORN);
        akmTr.getWbciGeschaeftsfall().setWechseltermin(LocalDate.now());

        WbciWitaOrderDataVO vo = new WbciWitaOrderDataVO(
                akmTr.getWbciGeschaeftsfall(),
                new AuftragDatenBuilder().setPersist(false).build(),
                null,
                new CarrierBuilder().setPersist(false).build(),
                null);

        doReturn(new WbciWitaOrderDataVO(akmTr.getWbciGeschaeftsfall(), null, null, null, null)).when(testling).generateWitaDataForPreAggreement(akmTr.getVorabstimmungsId());
        doReturn(WitaCBVorgang.ANBIETERWECHSEL_46TKG).when(testling).determineMontagehinweis(any(WbciWitaOrderDataVO.class), anyLong());
        when(witaTalOrderService.checkAutoClosingAllowed(any(AuftragDaten.class), anyLong(), anyLong())).thenReturn(true);
        doReturn(new AKUser()).when(testling).findUser(akmTr.getWbciGeschaeftsfall());
        doNothing().when(testling).checkIfTalOrderExists(any(Carrierbestellung.class));

        doThrow(new StoreException("TEST")).when(witaTalOrderService).createCBVorgang(any(CbVorgangData.class));
        testling.createWitaVorgang(AkmTrAutomationTask.ANBIETERWECHSEL, vo);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Unexpected error during the automatic creation of the WITA order of typ ANBIETERWECHSEL")
    public void testCreateWitaVorgangUnexpectedException() throws Exception {
        doThrow(new RuntimeException("runtime")).when(testling)
                .createNewCarrierbestellungIfNotExist(any(WbciGeschaeftsfall.class), any(WbciWitaOrderDataVO.class));
        testling.createWitaVorgang(AkmTrAutomationTask.ANBIETERWECHSEL, new WbciWitaOrderDataVO());
    }

    @Test
    public void testDetermineMontagehinweis() throws Exception {
        String vaID = "DEU.MNET.V0001";
        Long cbVorgangTyp = WitaCBVorgang.TYP_NEU;
        GeschaeftsfallTyp gfTyp = GeschaeftsfallTyp.BEREITSTELLUNG;
        Long hvtId = 2L;
        String montageHinweis = "#CC#";
        when(witaConfigService.findWitaSendLimit(gfTyp, hvtId)).thenReturn(new WitaSendLimitBuilder().withMontageHinweis(montageHinweis).build());

        Endstelle endstelle = mock(Endstelle.class);
        when(endstelle.getHvtIdStandort()).thenReturn(hvtId);

        WbciWitaOrderDataVO vo = new WbciWitaOrderDataVO(new WbciGeschaeftsfallKueMrnBuilder().withVorabstimmungsId(vaID).build(), null, null, null, endstelle);
        doReturn(gfTyp).when(testling).determineWitaGeschaeftsfall(vo, cbVorgangTyp);

        String result = testling.determineMontagehinweis(vo, cbVorgangTyp);
        assertEquals(result, WitaCBVorgang.ANBIETERWECHSEL_46TKG + montageHinweis);

        when(witaConfigService.findWitaSendLimit(gfTyp, hvtId)).thenReturn(null);
        result = testling.determineMontagehinweis(vo, cbVorgangTyp);
        assertEquals(result, WitaCBVorgang.ANBIETERWECHSEL_46TKG);
    }

    @Test
    public void testDetermineWitaGeschaeftsfall() throws Exception {
        //test NEUBESTELLUNG
        GeschaeftsfallTyp result = testling.determineWitaGeschaeftsfall(new WbciWitaOrderDataVO(null, null, null, null, null), WitaCBVorgang.TYP_NEU);
        assertEquals(result, GeschaeftsfallTyp.BEREITSTELLUNG);
        verify(talAnbieterwechseltypService, never()).getEquipment(any(Carrierbestellung.class), anyLong());
        verify(talAnbieterwechseltypService, never()).determineAnbieterwechseltyp(anyString(), any(Equipment.class));

        //test ANBIETERWECHSEL
        Long auftragId = 22L;
        Carrierbestellung cb = mock(Carrierbestellung.class);
        WbciGeschaeftsfallKueMrn wbciGf = new WbciGeschaeftsfallKueMrnBuilder().withVorabstimmungsId("DEU.MNET.V0001").build();
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withAuftragId(auftragId).build();
        WbciWitaOrderDataVO vo = new WbciWitaOrderDataVO(wbciGf, auftragDaten, cb, null, null);
        Equipment equipment = mock(Equipment.class);
        when(talAnbieterwechseltypService.getEquipment(cb, auftragId)).thenReturn(equipment);
        when(talAnbieterwechseltypService.determineAnbieterwechseltyp(vo.getVorabstimmungsId(), equipment)).thenReturn(GeschaeftsfallTyp.PROVIDERWECHSEL);

        result = testling.determineWitaGeschaeftsfall(vo, WitaCBVorgang.TYP_ANBIETERWECHSEL);
        verify(talAnbieterwechseltypService).getEquipment(cb, auftragId);
        verify(talAnbieterwechseltypService).determineAnbieterwechseltyp(wbciGf.getVorabstimmungsId(), equipment);
        assertEquals(result, GeschaeftsfallTyp.PROVIDERWECHSEL);
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "Die Ermittlung des WITA-Geschaeftsfalltyps auf Basis des Carrierbestellung-Typs '8007' wird derzeit nicht unterstützt")
    public void testDetermineWitaGeschaeftsfallNotConfigured() throws Exception {
        testling.determineWitaGeschaeftsfall(new WbciWitaOrderDataVO(null, null, null, null, null), WitaCBVorgang.TYP_HVT_KVZ);
    }

    @Test
    public void testValidateResultList() throws Exception {
        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        assertEquals(cbVorgang, testling.validateResultList(Collections.singletonList(cbVorgang)));
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Incorrect data type created expected 'WitaCBVorgang' but get 'CBVorgang'")
    public void testValidateResultListWrongTyp() throws Exception {
        testling.validateResultList(Collections.singletonList(new CBVorgang()));
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "There would have been 2 WITA orders created, but only 1 was expected")
    public void testValidateResultListToMuchEntries() throws Exception {
        testling.validateResultList(Arrays.asList(new WitaCBVorgang(), new WitaCBVorgang()));
    }

    @Test
    public void testGetRufnummerIDs() throws Exception {
        Long billingNo = 200L;
        Date wechseltermin = new Date();
        RueckmeldungVorabstimmung ruemVA = new RueckmeldungVorabstimmungTestBuilder().buildValid(V1, VA_KUE_MRN);
        ruemVA.getWbciGeschaeftsfall().setBillingOrderNoOrig(billingNo);
        ruemVA.getWbciGeschaeftsfall().setWechseltermin(DateConverterUtils.asLocalDate(wechseltermin));
        RufnummerPortierungSelection rps1 = new RufnummerPortierungSelection(new RufnummerBuilder()
                .withDnNoOrig(1L)
                .build());
        RufnummerPortierungSelection rps2 = new RufnummerPortierungSelection(new RufnummerBuilder()
                .withDnNoOrig(2L)
                .build());
        rps1.setSelected(true);
        when(wbciCommonService.findLastForVaId(ruemVA.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(ruemVA);
        List<RufnummerPortierungSelection> tempList = new ArrayList<>();
        when(witaTalOrderService.getRufnummerPortierungList(billingNo, wechseltermin)).thenReturn(tempList);
        when(witaWbciServiceFacade.updateRufnummernSelectionForVaId(tempList, ruemVA.getVorabstimmungsId())).thenReturn(Arrays.asList(rps1, rps2));

        Set<Long> result = testling.getRufnummerIds(ruemVA.getVorabstimmungsId());
        assertEquals(result.size(), 1);
        assertEquals(result.iterator().next().longValue(), 1L);
    }

    @Test
    public void testDoWitaTerminverschiebung() throws Exception {
        ErledigtmeldungTerminverschiebung erlmTv = new ErledigtmeldungTestBuilder().buildValidForTv(V1, VA_KUE_MRN);
        Long cbVorgangId = 22L;
        LocalDate tvTermin = erlmTv.getWechseltermin();
        AKUser user = new AKUser();
        when(wbciCommonService.findLastForVaId(erlmTv.getVorabstimmungsId(), ErledigtmeldungTerminverschiebung.class)).thenReturn(erlmTv);
        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder().withCarrierRefNr("10").build();
        when(witaTalOrderService.doTerminverschiebung(cbVorgangId, tvTermin, user, true, null, TV_60_TAGE)).thenReturn(cbVorgang);

        assertEquals(testling.doWitaTerminverschiebung(erlmTv.getVorabstimmungsId(), cbVorgangId, user, TV_60_TAGE), cbVorgang);
        verify(wbciGeschaeftsfallService).createOrUpdateAutomationTask(erlmTv.getWbciGeschaeftsfall(), erlmTv,
                AutomationTask.TaskName.WITA_SEND_TV, AutomationTask.AutomationStatus.COMPLETED,
                "Terminverschiebung fuer WITA-Vorgang '10' automatisch erzeugt bzw. KWT angepasst", user);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Fehler bei der Generierung einer WITA Terminverschiebung aufgetreten: TEST")
    public void testDoWitaTerminverschiebungException() throws Exception {
        ErledigtmeldungTerminverschiebung erlmTv = new ErledigtmeldungTestBuilder().buildValidForTv(V1, VA_KUE_MRN);
        Long cbVorgangId = 22L;
        LocalDate tvTermin = erlmTv.getWechseltermin();
        AKUser user = new AKUser();
        when(wbciCommonService.findLastForVaId(erlmTv.getVorabstimmungsId(), ErledigtmeldungTerminverschiebung.class)).thenReturn(erlmTv);
        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setCbVorgangRefId(10L);
        when(witaTalOrderService.doTerminverschiebung(cbVorgangId, tvTermin, user, true, null, TV_60_TAGE)).thenThrow(new RuntimeException("TEST"));

        testling.doWitaTerminverschiebung(erlmTv.getVorabstimmungsId(), cbVorgangId, user, TV_60_TAGE);
    }


    @DataProvider(name = "testCheckIfTalOrderExistsDP")
    public Object[][] checkIfTalOrderExistsDP() {
        return new Object[][] {
                { null, false, false },
                { new CarrierbestellungBuilder().setPersist(false).build(), false, false },
                { new CarrierbestellungBuilder().withLbz("123").setPersist(false).build(), false, true },
                { new CarrierbestellungBuilder().withVtrNr("123").setPersist(false).build(), false, true },
                { new CarrierbestellungBuilder().setPersist(false).build(), true, true },
        };
    }


    @Test(dataProvider = "testCheckIfTalOrderExistsDP")
    public void testCheckIfTalOrderExists(Carrierbestellung carrierbestellung, boolean returnActiveCb, boolean expectException) {
        boolean exceptionOccured = false;
        try {
            if (returnActiveCb) {
                CBVorgang activeCbv = new WitaCBVorgangBuilder().withStatus(CBVorgang.STATUS_SUBMITTED).setPersist(false).build();
                when(carrierElTALService.findCBVorgaenge4CB(anyLong())).thenReturn(Collections.singletonList(activeCbv));
            }

            testling.checkIfTalOrderExists(carrierbestellung);
        }
        catch (Exception e) {
            exceptionOccured = true;
        }

        assertEquals(exceptionOccured, expectException);
    }

    private UebernahmeRessourceMeldung getAkmTr(WbciGeschaeftsfall gf, boolean sichererhafen, boolean resourcenUebernahme, String portierungskennung) {
        return new UebernahmeRessourceMeldungBuilder()
                .withSichererhafen(sichererhafen)
                .withUebernahme(resourcenUebernahme)
                .withPortierungskennungPKIauf(portierungskennung)
                .withWbciGeschaeftsfall(gf)
                .build();

    }

    private WbciGeschaeftsfallKueOrn getKueOrn() {
        return new WbciGeschaeftsfallKueOrnBuilder()
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.EINS_UND_EINS)
                .withAuftragId(1L)
                .build();
    }

    private WbciGeschaeftsfallKueMrn getKueMrn() {
        return getKueMrn(CarrierCode.MNET, CarrierCode.EINS_UND_EINS);
    }

    private WbciGeschaeftsfallKueMrn getKueMrn(CarrierCode abgebenderEKP, CarrierCode aufnehmenderEKP) {
        return new WbciGeschaeftsfallKueMrnBuilder()
                .withAbgebenderEKP(abgebenderEKP)
                .withAufnehmenderEKP(aufnehmenderEKP)
                .withAuftragId(1L)
                .build();
    }

}
