/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.07.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.AKTeam;
import de.augustakom.authentication.model.AKTeamBuilder;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.authentication.service.AKTeamService;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBuilder;
import de.augustakom.hurrican.model.billing.DNTNB;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.wbci.dao.VorabstimmungIdsByBillingOrderNoCriteria;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.service.WbciCustomerService;
import de.mnet.wbci.service.WbciGeschaeftsfallStatusUpdateService;

/**
 *
 */
@Test(groups = UNIT)
public class WbciCommonServiceImplTest {

    @InjectMocks
    @Spy
    private WbciCommonServiceImpl testling;

    @Mock
    private WbciCustomerService wbciCustomerService;

    @Mock
    private WbciDao wbciDao;

    @Mock
    private RufnummerService rufnummerService;

    @Mock
    private EndstellenService endstellenService;

    @Mock
    private RangierungsService rangierungsService;

    @Mock
    private HVTService hvtService;

    @Mock
    private ReferenceService referenceService;

    @Mock
    private CarrierElTALService carrierElTALService;

    @Mock
    private CarrierService carrierService;

    @Mock
    private PhysikService physikService;

    @Mock
    private BillingAuftragService billingAuftragService;

    @Mock
    private KundenService kundenService;

    @Mock
    private AKUserService userService;

    @Mock
    private AKTeamService akTeamService;

    @Mock
    private CCAuftragService ccAuftragService;

    @Mock
    private WbciGeschaeftsfallStatusUpdateService wbciGeschaeftsfallStatusUpdateServiceMock;

    @Mock
    private FeatureService featureService;

    @BeforeMethod
    public void setUp() {
        testling = new WbciCommonServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider(name = "preAgreementIdTestData")
    public Object[][] createPreAgreementIdTestData() {
        return new Object[][] {
                { RequestTyp.VA, 10L, "DEU.MNET.VH00000010" },
                { RequestTyp.VA, 12345678L, "DEU.MNET.VH12345678" },
                { RequestTyp.TV, 11L, "DEU.MNET.TH00000011" },
                { RequestTyp.STR_AEN_ABG, 12L, "DEU.MNET.SH00000012" },
                { RequestTyp.STR_AEN_AUF, 13L, "DEU.MNET.SH00000013" },
                { RequestTyp.STR_AUFH_ABG, 14L, "DEU.MNET.SH00000014" },
                { RequestTyp.STR_AUFH_AUF, 15L, "DEU.MNET.SH00000015" }
        };
    }

    @Test(dataProvider = "preAgreementIdTestData")
    public void testGetNextPreAgreementIdVA(RequestTyp requestTyp, Long nextSequenceValue, String expectedPreAgreementId)
            throws Exception {
        when(wbciDao.getNextSequenceValue(eq(requestTyp))).thenReturn(nextSequenceValue);
        assertEquals(testling.getNextPreAgreementId(requestTyp), expectedPreAgreementId);
    }

    @Test
    public void getRufnummerPortierungList() throws FindException {
        Long auftragNoOrig = 123L;
        List<Rufnummer> rufnummernKommend = Arrays.asList(
                new RufnummerBuilder().withDnBase("12345678").withOnKz("089").build(),
                new RufnummerBuilder().withDnBase("12345679").withOnKz("0921").build(),
                new RufnummerBuilder().withDnBase("12345679").withOnKz("0151").build());
        when(rufnummerService.findDnsKommendForWbci(eq(auftragNoOrig))).thenReturn(rufnummernKommend);
        final List<RufnummerPortierungSelection> rufnummerPortierungList = testling
                .getRufnummerPortierungList(auftragNoOrig);
        assertEquals(rufnummerPortierungList.size(), 2);
        assertEquals(rufnummerPortierungList.get(0).getRufnummer(), rufnummernKommend.get(0));
        assertEquals(rufnummerPortierungList.get(1).getRufnummer(), rufnummernKommend.get(1));
    }

    @Test
    public void testGetTnbKennung_NGN_Feature_Off() throws FindException {
        Long auftragId = 1000L;
        Long hvtIdStandort = 2000L;
        Endstelle endstelleMock = mock(Endstelle.class);

        HVTGruppe hvtGruppe = new HVTGruppeBuilder()
                .withOnkz("0123")
                .setPersist(false)
                .build();

        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelleMock);
        when(endstelleMock.getHvtIdStandort()).thenReturn(hvtIdStandort);
        when(hvtService.findHVTGruppe4Standort(endstelleMock.getHvtIdStandort())).thenReturn(hvtGruppe);
        when(rufnummerService.findTnbKennung4Onkz(hvtGruppe.getOnkz())).thenReturn(
                TNB.AKOM.tnbKennung);
        when(featureService.isFeatureOnline(Feature.FeatureName.NGN_PORTIERUNGSKENNUNG_FUNCTIONALITY_ENABLED)).thenReturn(Boolean.FALSE);

        String portierungskennungPKIauf = testling.getTnbKennung(auftragId);

        assertEquals(portierungskennungPKIauf, TNB.AKOM.tnbKennung);
    }

    @Test
    public void testGetTnbKennung_NGN_Feature_Online() throws FindException {
        Long auftragId = 1000L;
        Long hvtIdStandort = 2000L;
        Endstelle endstelleMock = mock(Endstelle.class);

        HVTGruppe hvtGruppe = new HVTGruppeBuilder()
                .withOnkz("0123")
                .setPersist(false)
                .build();

        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelleMock);
        when(endstelleMock.getHvtIdStandort()).thenReturn(hvtIdStandort);
        when(hvtService.findHVTGruppe4Standort(endstelleMock.getHvtIdStandort())).thenReturn(hvtGruppe);
        when(rufnummerService.findTnbKennung4Onkz(hvtGruppe.getOnkz())).thenReturn(TNB.AKOM.tnbKennung);
        when(featureService.isFeatureOnline(Feature.FeatureName.NGN_PORTIERUNGSKENNUNG_FUNCTIONALITY_ENABLED)).thenReturn(Boolean.TRUE);
        when(ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(mock(AuftragDaten.class, Mockito.RETURNS_DEEP_STUBS));

        final Rufnummer dn1 = new RufnummerBuilder().setPersist(false).withActCarrier(TNB.AKOM.carrierName, TNB.AKOM.tnbKennung).withOnKz(TNB.AKOM.tnbKennung).build();
        when(rufnummerService.findRNs4Auftrag(anyLong())).thenReturn(Collections.singletonList(dn1));
        when(rufnummerService.findTnbKennung4Onkz(TNB.AKOM.tnbKennung)).thenReturn(TNB.AKOM.tnbKennung);

        final DNTNB dnTnb = new DNTNB();
        dnTnb.setPortKennung(TNB.AKOM.tnbKennung);
        when(rufnummerService.findTNB(TNB.AKOM.tnbKennung)).thenReturn(dnTnb);

        final CarrierKennung carrierKennung = new CarrierKennung();
        carrierKennung.setPortierungsKennung(TNB.AKOM.tnbKennung);
        when(carrierService.findCarrierKennung(anyString())).thenReturn(carrierKennung);

        String portierungskennungPKIauf = testling.getTnbKennung(auftragId);

        assertEquals(portierungskennungPKIauf, TNB.AKOM.tnbKennung);
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testGetTnbKennungWhenExceptionThrown() throws FindException {
        Long auftragId = 1000L;

        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenThrow(
                NullPointerException.class);

        testling.getTnbKennung(auftragId);
    }

    @Test
    public void testGetTnbKennungForTechnischeRessource() throws FindException {
        Long hvtIdStandort = 2000L;
        String elTalAbsenderId = "12345";
        Endstelle endstelleMock = mock(Endstelle.class);
        CarrierKennung carrierKennungMock = mock(CarrierKennung.class);

        when(endstelleMock.getHvtIdStandort()).thenReturn(hvtIdStandort);
        when(carrierService.findCarrierKennung4Hvt(hvtIdStandort)).thenReturn(carrierKennungMock);
        when(carrierKennungMock.getElTalAbsenderId()).thenReturn(elTalAbsenderId);

        String portierungskennungPKIauf = testling.getTnbKennung(endstelleMock);

        assertEquals(portierungskennungPKIauf, "1234");
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testGetTnbKennungForTechnischeRessourceWhenExceptionThrown() throws FindException {
        when(carrierService.findCarrierKennung4Hvt(anyLong())).thenThrow(NullPointerException.class);
        testling.getTnbKennung(new Endstelle());
    }

    @Test
    public void getMnetTechnologie() throws FindException {
        HVTStandortBuilder hvtStandort = getHvtStandortBuilder();
        Endstelle endstelle = getEndstelle(hvtStandort);
        List<Endstelle> endstellen = Collections.singletonList(endstelle);
        Reference hvtTypeRef = new ReferenceBuilder()
                .withWbciTechnologieCode(Technologie.FTTB.getWbciTechnologieCode()).setPersist(false).build();

        when(endstellenService.findEndstellen4Auftrag(any(Long.class))).thenReturn(endstellen);
        when(hvtService.findHVTStandort(hvtStandort.get().getHvtIdStandort())).thenReturn(hvtStandort.get());
        when(referenceService.findReference(hvtStandort.get().getStandortTypRefId())).thenReturn(hvtTypeRef);
        when(carrierService.findLastCB4Endstelle(endstelle.getId())).thenReturn(null);

        assertEquals(testling.getMnetTechnologie(1L),
                Technologie.lookUpWbciTechnologieCode(hvtTypeRef.getWbciTechnologieCode()));
        verify(endstellenService).findEndstellen4Auftrag(any(Long.class));
        verify(hvtService).findHVTStandort(hvtStandort.get().getHvtIdStandort());
        verify(referenceService).findReference(hvtStandort.get().getStandortTypRefId());
    }

    @Test
    public void testMnetTechnologieWithNoEndstelle() throws FindException {
        when(endstellenService.findEndstellen4Auftrag(any(Long.class))).thenReturn(null);
        assertEquals(testling.getMnetTechnologie(1L),Technologie.SONSTIGES);
    }

    @Test
    public void testGetEndstelle4Auftrag() throws Exception {

        HVTStandortBuilder hvtStandortB = getHvtStandortBuilder();
        Endstelle endstelleB = getEndstelle(hvtStandortB, Endstelle.ENDSTELLEN_TYP_B);

        List<Endstelle> endstellen = Collections.singletonList(endstelleB);

        when(endstellenService.findEndstellen4Auftrag(any(Long.class))).thenReturn(endstellen);

        Endstelle endstelle4AuftragResult = testling.getEndstelle4Auftrag(1L);

        assertEquals(endstelle4AuftragResult.getEndstelleTyp(), endstelleB.getEndstelleTyp());

        verify(carrierService, never()).findLastCB4Endstelle(any(Long.class));
        verify(carrierElTALService, never()).findCBVorgang(any(Long.class));
    }

    @Test
    public void testGetEndstelle4AuftragWithoutCarrierbestellungInEndstelleA() throws Exception {

        HVTStandortBuilder hvtStandortA = getHvtStandortBuilder();
        Endstelle endstelleA = getEndstelle(hvtStandortA, Endstelle.ENDSTELLEN_TYP_A);

        HVTStandortBuilder hvtStandortB = getHvtStandortBuilder();
        Endstelle endstelleB = getEndstelle(hvtStandortB, Endstelle.ENDSTELLEN_TYP_B);

        List<Endstelle> endstellen = Arrays.asList(endstelleA, endstelleB);

        when(endstellenService.findEndstellen4Auftrag(any(Long.class))).thenReturn(endstellen);

        Endstelle endstelle4AuftragResult = testling.getEndstelle4Auftrag(1L);

        assertEquals(endstelle4AuftragResult.getEndstelleTyp(), endstelleB.getEndstelleTyp());

        verify(carrierService).findLastCB4Endstelle(any(Long.class));
        verify(carrierElTALService, never()).findCBVorgang(any(Long.class));
    }

    @Test
    public void testGetEndstelle4AuftragWithCarrierbestellungInEndstelleA() throws Exception {

        HVTStandortBuilder hvtStandortA = getHvtStandortBuilder();
        Endstelle endstelleA = getEndstelle(hvtStandortA, Endstelle.ENDSTELLEN_TYP_A);

        HVTStandortBuilder hvtStandortB = getHvtStandortBuilder();
        Endstelle endstelleB = getEndstelle(hvtStandortB, Endstelle.ENDSTELLEN_TYP_B);

        List<Endstelle> endstellen = Arrays.asList(endstelleA, endstelleB);

        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().withId(1L).build();
        CBVorgang cbVorgang = new CBVorgangBuilder().withAuftragId(1L).build();

        when(endstellenService.findEndstellen4Auftrag(any(Long.class))).thenReturn(endstellen);
        when(carrierService.findLastCB4Endstelle(endstelleA.getId())).thenReturn(carrierbestellung);
        when(carrierElTALService.findCBVorgang(carrierbestellung.getId())).thenReturn(cbVorgang);

        Endstelle endstelle4AuftragResult = testling.getEndstelle4Auftrag(1L);

        assertEquals(endstelle4AuftragResult.getEndstelleTyp(), endstelleA.getEndstelleTyp());

        verify(carrierService).findLastCB4Endstelle(any(Long.class));
        verify(carrierElTALService).findCBVorgang(any(Long.class));
    }

    @Test
    public void testGetEndstelle4AuftragWithCarrierbestellungInEndstelleAWithoutCbVorgang() throws Exception {

        HVTStandortBuilder hvtStandortA = getHvtStandortBuilder();
        Endstelle endstelleA = getEndstelle(hvtStandortA, Endstelle.ENDSTELLEN_TYP_A);

        HVTStandortBuilder hvtStandortB = getHvtStandortBuilder();
        Endstelle endstelleB = getEndstelle(hvtStandortB, Endstelle.ENDSTELLEN_TYP_B);

        List<Endstelle> endstellen = Arrays.asList(endstelleA, endstelleB);

        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().withId(1L).build();

        when(endstellenService.findEndstellen4Auftrag(any(Long.class))).thenReturn(endstellen);
        when(carrierService.findLastCB4Endstelle(endstelleA.getId())).thenReturn(carrierbestellung);
        when(carrierElTALService.findCBVorgang(carrierbestellung.getId())).thenReturn(null);

        Endstelle endstelle4AuftragResult = testling.getEndstelle4Auftrag(1L);

        assertEquals(endstelle4AuftragResult.getEndstelleTyp(), endstelleB.getEndstelleTyp());

        verify(carrierService).findLastCB4Endstelle(any(Long.class));
        verify(carrierElTALService).findCBVorgang(any(Long.class));
        verify(carrierElTALService).findCBVorgang(any(Long.class));
    }

    @Test
    public void testGetEndstelle4AuftragWithNotEqualAuftragIdInEndstelleA() throws Exception {

        HVTStandortBuilder hvtStandortA = getHvtStandortBuilder();
        Endstelle endstelleA = getEndstelle(hvtStandortA, Endstelle.ENDSTELLEN_TYP_A);

        HVTStandortBuilder hvtStandortB = getHvtStandortBuilder();
        Endstelle endstelleB = getEndstelle(hvtStandortB, Endstelle.ENDSTELLEN_TYP_B);

        List<Endstelle> endstellen = Arrays.asList(endstelleA, endstelleB);

        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().withId(1L).build();
        CBVorgang cbVorgang = new CBVorgangBuilder().withAuftragId(2L).build();

        when(endstellenService.findEndstellen4Auftrag(any(Long.class))).thenReturn(endstellen);
        when(carrierService.findLastCB4Endstelle(endstelleA.getId())).thenReturn(carrierbestellung);
        when(carrierElTALService.findCBVorgang(carrierbestellung.getId())).thenReturn(cbVorgang);

        Endstelle endstelle4AuftragResult = testling.getEndstelle4Auftrag(1L);

        assertEquals(endstelle4AuftragResult.getEndstelleTyp(), endstelleB.getEndstelleTyp());

        verify(carrierService).findLastCB4Endstelle(any(Long.class));
        verify(carrierElTALService).findCBVorgang(any(Long.class));
    }

    @DataProvider(name = "getMnetTechnologieForStandortTypHvtAndCarrierDtagTestData")
    public Object[][] getMnetTechnologieForStandortTypHvtAndCarrierDtagTestData() {
        return new Object[][] {
                { Uebertragungsverfahren.N01, Technologie.TAL_ISDN },
                { Uebertragungsverfahren.H04, Technologie.TAL_DSL },
                { Uebertragungsverfahren.H13, Technologie.TAL_DSL },
                { Uebertragungsverfahren.H16, Technologie.TAL_DSL },
                { Uebertragungsverfahren.H18, Technologie.TAL_VDSL },
                { Uebertragungsverfahren.LWL, Technologie.SONSTIGES },
        };
    }

    @Test(dataProvider = "getMnetTechnologieForStandortTypHvtAndCarrierDtagTestData")
    public void getMnetTechnologieForStandortTypHvtAndCarrierDtag(Uebertragungsverfahren uetv, Technologie expected)
            throws FindException {
        HVTStandortBuilder hvtStandort = new HVTStandortBuilder()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .withCarrierId(Carrier.ID_DTAG)
                .withRandomId()
                .setPersist(false);
        Endstelle endstelle = getEndstelle(hvtStandort);
        List<Endstelle> endstellen = Collections.singletonList(endstelle);
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().withId(1L).build();
        CBVorgang cbVorgang = new CBVorgangBuilder().withAuftragId(1L).build();

        EquipmentBuilder eqOutBuilder = new EquipmentBuilder().withRandomId().withUETV(uetv).setPersist(false);
        Rangierung rangierung = new RangierungBuilder()
                .withRandomId()
                .withEqOutBuilder(eqOutBuilder)
                .setPersist(false)
                .build();
        endstelle.setRangierId(rangierung.getId());

        when(endstellenService.findEndstellen4Auftrag(any(Long.class))).thenReturn(endstellen);
        when(hvtService.findHVTStandort(hvtStandort.get().getHvtIdStandort())).thenReturn(hvtStandort.get());
        when(rangierungsService.findRangierung(endstelle.getRangierId())).thenReturn(rangierung);
        when(rangierungsService.findEquipment(rangierung.getEqOutId())).thenReturn(eqOutBuilder.get());
        when(carrierService.findLastCB4Endstelle(endstelle.getId())).thenReturn(carrierbestellung);
        when(carrierElTALService.findCBVorgang(carrierbestellung.getId())).thenReturn(cbVorgang);

        assertEquals(testling.getMnetTechnologie(1L), expected);
        verify(referenceService, times(0)).findReference(hvtStandort.get().getStandortTypRefId());
    }

    @Test
    public void getMnetTechnologieForStandortTypHvtAndCarrierNotDtag() throws FindException {
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .withCarrierId(Carrier.ID_MNET)
                .withRandomId()
                .setPersist(false);
        Endstelle endstelle = getEndstelle(hvtStandortBuilder);
        endstelle.setRangierId(99L);
        List<Endstelle> endstellen = Collections.singletonList(endstelle);
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().withId(1L).build();
        CBVorgang cbVorgang = new CBVorgangBuilder().withAuftragId(1L).build();

        when(endstellenService.findEndstellen4Auftrag(any(Long.class))).thenReturn(endstellen);
        when(hvtService.findHVTStandort(hvtStandortBuilder.get().getHvtIdStandort())).thenReturn(
                hvtStandortBuilder.get());
        when(carrierService.findLastCB4Endstelle(endstelle.getId())).thenReturn(carrierbestellung);
        when(carrierElTALService.findCBVorgang(carrierbestellung.getId())).thenReturn(cbVorgang);

        assertEquals(testling.getMnetTechnologie(1L), Technologie.KUPFER);
    }

    @Test
    public void getMnetTechnologieWithoutHvtStandort() throws FindException {
        HVTStandortBuilder hvtStandort = getHvtStandortBuilder();
        Endstelle endstelle = getEndstelle(hvtStandort);
        List<Endstelle> endstellen = Collections.singletonList(endstelle);

        when(endstellenService.findEndstellen4Auftrag(any(Long.class))).thenReturn(endstellen);
        when(hvtService.findHVTStandort(hvtStandort.get().getHvtIdStandort())).thenReturn(null);
        when(carrierService.findLastCB4Endstelle(endstelle.getId())).thenReturn(null);

        assertEquals(testling.getMnetTechnologie(1L), Technologie.SONSTIGES);
        verify(endstellenService).findEndstellen4Auftrag(any(Long.class));
        verify(hvtService).findHVTStandort(hvtStandort.get().getHvtIdStandort());
    }

    @Test
    public void getMnetTechnologieForSpecialConnections() throws FindException {
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .withCarrierId(Carrier.ID_MNET)
                .withRandomId()
                .setPersist(false);
        Endstelle endstelle = getEndstelle(hvtStandortBuilder);
        endstelle.setAnschlussart(Anschlussart.ANSCHLUSSART_VIRTUELL);
        List<Endstelle> endstellen = Collections.singletonList(endstelle);

        when(endstellenService.findEndstellen4Auftrag(any(Long.class))).thenReturn(endstellen);
        when(hvtService.findHVTStandort(hvtStandortBuilder.get().getHvtIdStandort())).thenReturn(
                hvtStandortBuilder.get());
        when(carrierService.findLastCB4Endstelle(endstelle.getId())).thenReturn(null);

        assertEquals(testling.getMnetTechnologie(1L), Technologie.SONSTIGES);
        verify(referenceService, times(0)).findReference(anyLong());
    }

    private HVTStandortBuilder getHvtStandortBuilder() {
        return new HVTStandortBuilder()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withRandomId()
                .setPersist(false);
    }

    private Endstelle getEndstelle(HVTStandortBuilder hvtStandortBuilder) {
        return new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().setPersist(false))
                .withHvtStandortBuilder(hvtStandortBuilder)
                .setPersist(false)
                .build();
    }

    private Endstelle getEndstelle(HVTStandortBuilder hvtStandortBuilder, String endstelleTyp) {
        return new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().setPersist(false))
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withEndstelleTyp(endstelleTyp)
                .setPersist(false)
                .build();
    }

    @DataProvider(name = "technischeRessourcen")
    public Object[][] technischeRessourcenDataProvider() {
        return new Object[][] {
                // carrierId, witaVrtrNr, wbciLineId, tnbKennung
                { Carrier.ID_DTAG, "123456789", null, TNB.MNET_NGN.tnbKennung },
                { Carrier.ID_MNET, null, "DEU.MNET.W123456789", TNB.MNET_NGN.tnbKennung },
        };
    }

    @Test(dataProvider = "technischeRessourcen")
    public void testGetTechnischeRessource(Long carrierId, String witaVrtNr, String wbciLineId, String tnbKennung)
            throws FindException {
        final Long billingOrderNoOrig = 1L;
        final Long auftragId = 2L;
        final Long hvtStandortId = 3L;
        final String onkz = "0821";
        final Set<Long> auftragIdList = new HashSet<>(Collections.singletonList(auftragId));
        doReturn(auftragIdList).when(testling).getWbciRelevantHurricanOrderNos(Sets.newHashSet(billingOrderNoOrig));
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withId(hvtStandortId).withCarrierId(carrierId);
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withAuftragId(2L).build();
        List<Rufnummer> rufnummern = Collections.singletonList(new RufnummerBuilder().withOnKz("0821").withDnBase("12345").build());
        final DNTNB dnTnb = new DNTNB();
        dnTnb.setPortKennung(TNB.MNET_NGN.tnbKennung);
        final CarrierKennung carrierKennung = new CarrierKennung();
        carrierKennung.setPortierungsKennung(TNB.MNET_NGN.tnbKennung);
        final Endstelle endstelle = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .build();
        List<Endstelle> endstellen = Collections.singletonList(endstelle);
        when(endstellenService.findEndstellen4Auftrag(auftragId)).thenReturn(endstellen);
        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle);
        when(hvtService.findHVTStandort(hvtStandortId)).thenReturn(hvtStandortBuilder.build());
        when(hvtService.findHVTGruppe4Standort(hvtStandortId)).thenReturn(mock(HVTGruppe.class));
        when(rufnummerService.findTnbKennung4Onkz(anyString())).thenReturn(tnbKennung);
        when(ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten);
        when(rufnummerService.findRNs4Auftrag(auftragDaten.getAuftragNoOrig())).thenReturn(rufnummern);
        when(rufnummerService.findTnbKennung4Onkz(onkz)).thenReturn(TNB.MNET_NGN.tnbKennung);
        when(rufnummerService.findTNB(anyString())).thenReturn(dnTnb);
        when(carrierService.findCarrierKennung(anyString())).thenReturn(carrierKennung);
        when(featureService.isFeatureOnline(Feature.FeatureName.NGN_PORTIERUNGSKENNUNG_FUNCTIONALITY_ENABLED)).thenReturn(true);
        doReturn(witaVrtNr).when(testling).getWitaVtrNr(endstelle);
        doReturn(wbciLineId).when(testling).getWbciLineId(auftragId);
        doReturn(tnbKennung).when(testling).getTnbKennung(endstelle);

        final Set<TechnischeRessource> technischeRessourcen = testling
                .getTechnischeRessourcen(billingOrderNoOrig, null);
        assertEquals(technischeRessourcen.size(), 1);
        final TechnischeRessource technischeRessource = technischeRessourcen.iterator().next();
        assertEquals(technischeRessource.getVertragsnummer(), witaVrtNr);
        assertEquals(technischeRessource.getIdentifizierer(), wbciLineId);
        assertEquals(technischeRessource.getTnbKennungAbg(), tnbKennung);
        verify(hvtService).findHVTStandort(hvtStandortId);
    }

    @Test(dataProvider = "technischeRessourcen")
    public void testGetTechnischeRessourceWithNonBillingRelevantOrderNos(Long carrierId, String witaVrtNr,
            String wbciLineId, String tnbKennung)
            throws FindException {
        final Long billingOrderNoOrig = 1L;
        final String onkz = "0821";
        final Set<Long> nonBillingRelevantOrderNos = Sets.newHashSet(1001L, 1002L);

        final Long auftragId = 2L;
        final Long hvtStandortId = 3L;
        final Set<Long> auftragIdList = new HashSet<>(Collections.singletonList(auftragId));
        doReturn(auftragIdList).when(testling).getWbciRelevantHurricanOrderNos(
                Sets.newHashSet(billingOrderNoOrig, 1001L, 1002L));
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withAuftragId(2L).build();
        List<Rufnummer> rufnummern = Collections.singletonList(new RufnummerBuilder().withOnKz("0821").withDnBase("12345").build());
        final DNTNB dnTnb = new DNTNB();
        dnTnb.setPortKennung(TNB.MNET_NGN.tnbKennung);
        final CarrierKennung carrierKennung = new CarrierKennung();
        carrierKennung.setPortierungsKennung(TNB.MNET_NGN.tnbKennung);
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withId(hvtStandortId).withCarrierId(carrierId);
        final Endstelle endstelle = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                .withHvtStandortBuilder(hvtStandortBuilder)
                .build();
        List<Endstelle> endstellen = Collections.singletonList(endstelle);
        when(endstellenService.findEndstellen4Auftrag(auftragId)).thenReturn(endstellen);
        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle);
        when(hvtService.findHVTStandort(hvtStandortId)).thenReturn(hvtStandortBuilder.build());
        when(hvtService.findHVTGruppe4Standort(hvtStandortId)).thenReturn(mock(HVTGruppe.class));
        when(rufnummerService.findTnbKennung4Onkz(anyString())).thenReturn(tnbKennung);
        when(ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten);
        when(rufnummerService.findRNs4Auftrag(auftragDaten.getAuftragNoOrig())).thenReturn(rufnummern);
        when(rufnummerService.findTnbKennung4Onkz(onkz)).thenReturn(TNB.MNET_NGN.tnbKennung);
        when(rufnummerService.findTNB(anyString())).thenReturn(dnTnb);
        doReturn(witaVrtNr).when(testling).getWitaVtrNr(endstelle);
        doReturn(wbciLineId).when(testling).getWbciLineId(auftragId);
        doReturn(tnbKennung).when(testling).getTnbKennung(endstelle);

        final Set<TechnischeRessource> technischeRessourcen = testling.getTechnischeRessourcen(billingOrderNoOrig,
                nonBillingRelevantOrderNos);
        assertEquals(technischeRessourcen.size(), 1);
        final TechnischeRessource technischeRessource = technischeRessourcen.iterator().next();
        assertEquals(technischeRessource.getVertragsnummer(), witaVrtNr);
        assertEquals(technischeRessource.getIdentifizierer(), wbciLineId);
        assertEquals(technischeRessource.getTnbKennungAbg(), tnbKennung);
        verify(hvtService).findHVTStandort(hvtStandortId);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Die WITA-Vertrags-Nr. konnte nicht ermittelt werden; Aufbau der technischen Ressource ist deshalb nicht m.*glich.")
    public void testGetTechnischeRessourceNoWitaVrtNr() throws FindException {
        final Long billingOrderNoOrig = 1L;
        final Long auftragId = 2L;
        final Long hvtStandortId = 3L;
        final Set<Long> auftragIdList = new HashSet<>(Collections.singletonList(auftragId));
        doReturn(auftragIdList).when(testling).getWbciRelevantHurricanOrderNos(Sets.newHashSet(billingOrderNoOrig));
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withId(hvtStandortId).withCarrierId(
                Carrier.ID_DTAG);
        final Endstelle endstelle = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                .withHvtStandortBuilder(hvtStandortBuilder)
                .build();
        List<Endstelle> endstellen = Collections.singletonList(endstelle);
        when(endstellenService.findEndstellen4Auftrag(auftragId)).thenReturn(endstellen);
        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle);
        when(hvtService.findHVTStandort(hvtStandortId)).thenReturn(hvtStandortBuilder.build());
        doReturn(null).when(testling).getWitaVtrNr(endstelle);

        testling.getTechnischeRessourcen(billingOrderNoOrig, null);
    }


    @Test
    public void testGetTechnischeRessourceVirtuell() throws FindException {
        final Long billingOrderNoOrig = 1L;
        final Long auftragId = 2L;
        final Long hvtStandortId = 3L;
        final Set<Long> auftragIdList = new HashSet<>(Collections.singletonList(auftragId));
        doReturn(auftragIdList).when(testling).getWbciRelevantHurricanOrderNos(Sets.newHashSet(billingOrderNoOrig));
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withId(hvtStandortId).withCarrierId(
                Carrier.ID_DTAG);
        final Endstelle endstelle = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withAnschlussart(Anschlussart.ANSCHLUSSART_VIRTUELL)
                .build();
        List<Endstelle> endstellen = Collections.singletonList(endstelle);
        when(endstellenService.findEndstellen4Auftrag(auftragId)).thenReturn(endstellen);
        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle);
        when(hvtService.findHVTStandort(hvtStandortId)).thenReturn(hvtStandortBuilder.build());
        doReturn(null).when(testling).getWitaVtrNr(endstelle);
        doReturn("WBCILineId").when(testling).getWbciLineId(anyLong());

        testling.getTechnischeRessourcen(billingOrderNoOrig, null);
        verify(testling).getWbciLineId(anyLong());
    }


    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Die WBCI-Line-ID konnte nicht ermittelt werden; Aufbau der technischen Ressource ist deshalb nicht m.*glich.")
    public void testGetTechnischeRessourceNoWbciLineId() throws FindException {
        final Long billingOrderNoOrig = 1L;
        final Long auftragId = 2L;
        final Long hvtStandortId = 3L;
        final Set<Long> auftragIdList = new HashSet<>(Collections.singletonList(auftragId));
        doReturn(auftragIdList).when(testling).getWbciRelevantHurricanOrderNos(Sets.newHashSet(billingOrderNoOrig));
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withId(hvtStandortId).withCarrierId(
                Carrier.ID_MNET);
        final Endstelle endstelle = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                .withHvtStandortBuilder(hvtStandortBuilder)
                .build();
        List<Endstelle> endstellen = Collections.singletonList(endstelle);
        when(endstellenService.findEndstellen4Auftrag(auftragId)).thenReturn(endstellen);
        when(hvtService.findHVTStandort(hvtStandortId)).thenReturn(hvtStandortBuilder.build());
        doReturn(null).when(testling).getWbciLineId(auftragId);

        testling.getTechnischeRessourcen(billingOrderNoOrig, null);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Im System konnte kein "
            + "HvtStandort fuer die Endstelle zum Auftrag mit Nr .* gefunden werden. Dadurch ist das Erzeugen einer Vertragsnummer nicht moeglich.")
    public void testGetTechnischeRessourceNoHvtStandortFound() throws FindException {
        final Long billingOrderNoOrig = 1L;
        final Long auftragId = 2L;
        final Long hvtStandortId = 3L;
        final Set<Long> auftragIdList = new HashSet<>(Collections.singletonList(auftragId));
        doReturn(auftragIdList).when(testling).getWbciRelevantHurricanOrderNos(Sets.newHashSet(billingOrderNoOrig));
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withId(hvtStandortId).withCarrierId(
                Carrier.ID_MNET);
        final Endstelle endstelle = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                .withHvtStandortBuilder(hvtStandortBuilder)
                .build();
        List<Endstelle> endstellen = Collections.singletonList(endstelle);
        when(endstellenService.findEndstellen4Auftrag(auftragId)).thenReturn(endstellen);
        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle);
        when(hvtService.findHVTStandort(hvtStandortId)).thenReturn(null);
        doReturn(null).when(testling).getWbciLineId(auftragId);

        testling.getTechnischeRessourcen(billingOrderNoOrig, null);
    }

    @Test
    public void testGetTechnischeRessourcenMehrereHurricanOrders() throws FindException {
        final Long billingOrderNoOrig = 1L;
        final Long firstAuftragId = 2L;
        final Long secondAuftragId = 4L;
        final Long hvtStandortId = 3L;
        final String witaVrtNr = "123456789";
        final String wbciLineId = "DEU.MNET.W123456789";
        final Set<Long> auftragIdList = new HashSet<>();
        final String onkz = "0821";
        final String tnbKennung = TNB.MNET_NGN.tnbKennung;
        auftragIdList.add(firstAuftragId);
        auftragIdList.add(secondAuftragId);
        final Set<Long> billingOrderNoList = new HashSet<>(Collections.singletonList(billingOrderNoOrig));

        doReturn(auftragIdList).when(testling).getWbciRelevantHurricanOrderNos(billingOrderNoList);
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withId(hvtStandortId).withCarrierId(Carrier.ID_DTAG);
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withAuftragId(2L).build();
        List<Rufnummer> rufnummern = Collections.singletonList(new RufnummerBuilder().withOnKz("0821").withDnBase("12345").build());
        final DNTNB dnTnb = new DNTNB();
        dnTnb.setPortKennung(TNB.MNET_NGN.tnbKennung);
        final CarrierKennung carrierKennung = new CarrierKennung();
        carrierKennung.setPortierungsKennung(TNB.MNET_NGN.tnbKennung);
        final Endstelle endstelle1 = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                .withHvtStandortBuilder(hvtStandortBuilder)
                .build();
        final Endstelle endstelle2 = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                .withHvtStandortBuilder(hvtStandortBuilder)
                .build();
        List<Endstelle> endstellen1 = Collections.singletonList(endstelle1);
        List<Endstelle> endstellen2 = Collections.singletonList(endstelle2);
        when(endstellenService.findEndstellen4Auftrag(firstAuftragId)).thenReturn(endstellen1);
        when(endstellenService.findEndstellen4Auftrag(secondAuftragId)).thenReturn(endstellen2);
        when(endstellenService.findEndstelle4Auftrag(firstAuftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle1);
        when(endstellenService.findEndstelle4Auftrag(secondAuftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle2);
        when(hvtService.findHVTStandort(hvtStandortId)).thenReturn(hvtStandortBuilder.build());
        when(rufnummerService.findTnbKennung4Onkz(anyString())).thenReturn(tnbKennung);
        when(ccAuftragService.findAuftragDatenByAuftragIdTx(anyLong())).thenReturn(auftragDaten);
        when(rufnummerService.findRNs4Auftrag(auftragDaten.getAuftragNoOrig())).thenReturn(rufnummern);
        when(rufnummerService.findTnbKennung4Onkz(onkz)).thenReturn(TNB.MNET_NGN.tnbKennung);
        when(rufnummerService.findTNB(anyString())).thenReturn(dnTnb);
        when(carrierService.findCarrierKennung(anyString())).thenReturn(carrierKennung);
        when(featureService.isFeatureOnline(Feature.FeatureName.NGN_PORTIERUNGSKENNUNG_FUNCTIONALITY_ENABLED)).thenReturn(true);
        doReturn(witaVrtNr).when(testling).getWitaVtrNr(endstelle1);
        doReturn(null).when(testling).getWitaVtrNr(endstelle2);

        final Set<TechnischeRessource> technischeRessourcen = testling.getTechnischeRessourcen(billingOrderNoOrig, null);
        assertEquals(technischeRessourcen.size(), 1);

        doReturn(witaVrtNr).when(testling).getWitaVtrNr(endstelle2);
        final Set<TechnischeRessource> technischeRessourcen2 = testling.getTechnischeRessourcen(billingOrderNoOrig, null);
        assertEquals(technischeRessourcen2.size(), 2);
    }

    @Test
    public void testGetTechnischeRessourcenResultSetWitaAndWbci() throws FindException {
        final Long billingOrderNoOrig = 1L;
        final Long firstAuftragId = 2L;
        final Long secondAuftragId = 4L;
        final Long hvtStandortId = 3L;
        final Long hvtStandortId2 = 4L;
        final String witaVrtNr = "123456789";
        final String wbciLineId = "DEU.MNET.W123456789";
        final Set<Long> auftragIdList = new HashSet<>();
        final String onkz = "0821";
        final String tnbKennung = TNB.MNET_NGN.tnbKennung;
        auftragIdList.add(firstAuftragId);
        auftragIdList.add(secondAuftragId);
        final Set<Long> billingOrderNoList = new HashSet<>(Collections.singletonList(billingOrderNoOrig));

        doReturn(auftragIdList).when(testling).getWbciRelevantHurricanOrderNos(billingOrderNoList);
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withId(hvtStandortId).withCarrierId(Carrier.ID_DTAG);
        HVTStandortBuilder hvtStandortBuilder2 = new HVTStandortBuilder().withId(hvtStandortId2).withCarrierId(Carrier.ID_O2);
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withAuftragId(2L).build();
        List<Rufnummer> rufnummern = Collections.singletonList(new RufnummerBuilder().withOnKz("0821").withDnBase("12345").build());
        final DNTNB dnTnb = new DNTNB();
        dnTnb.setPortKennung(TNB.MNET_NGN.tnbKennung);
        final CarrierKennung carrierKennung = new CarrierKennung();
        carrierKennung.setPortierungsKennung(TNB.MNET_NGN.tnbKennung);
        final Endstelle endstelle1 = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                .withHvtStandortBuilder(hvtStandortBuilder)
                .build();
        final Endstelle endstelle2 = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                .withHvtStandortBuilder(hvtStandortBuilder2)
                .build();
        List<Endstelle> endstellen1 = Collections.singletonList(endstelle1);
        List<Endstelle> endstellen2 = Collections.singletonList(endstelle2);
        when(endstellenService.findEndstellen4Auftrag(firstAuftragId)).thenReturn(endstellen1);
        when(endstellenService.findEndstellen4Auftrag(secondAuftragId)).thenReturn(endstellen2);
        when(endstellenService.findEndstelle4Auftrag(firstAuftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle1);
        when(endstellenService.findEndstelle4Auftrag(secondAuftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle2);
        when(hvtService.findHVTStandort(hvtStandortId)).thenReturn(hvtStandortBuilder.build());
        when(hvtService.findHVTStandort(hvtStandortId2)).thenReturn(hvtStandortBuilder2.build());
        when(rufnummerService.findTnbKennung4Onkz(anyString())).thenReturn(tnbKennung);
        when(ccAuftragService.findAuftragDatenByAuftragIdTx(anyLong())).thenReturn(auftragDaten);
        when(rufnummerService.findRNs4Auftrag(auftragDaten.getAuftragNoOrig())).thenReturn(rufnummern);
        when(rufnummerService.findTnbKennung4Onkz(onkz)).thenReturn(TNB.MNET_NGN.tnbKennung);
        when(rufnummerService.findTNB(anyString())).thenReturn(dnTnb);
        when(carrierService.findCarrierKennung(anyString())).thenReturn(carrierKennung);
        when(featureService.isFeatureOnline(Feature.FeatureName.NGN_PORTIERUNGSKENNUNG_FUNCTIONALITY_ENABLED)).thenReturn(true);
        doReturn(witaVrtNr).when(testling).getWitaVtrNr(endstelle1);
        doReturn(wbciLineId).when(testling).getWbciLineId(secondAuftragId);

        final Set<TechnischeRessource> technischeRessourcen = testling.getTechnischeRessourcen(billingOrderNoOrig, null);
        assertEquals(technischeRessourcen.size(), 2);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Im System konnte keine gueltige Endstelle"
            + " für den Auftrag mit Nr .* gefunden werden. Dadurch ist das Erzeugen einer Vertragsnummer nicht moeglich.")
    public void testGetTechnischeRessourceNoEndstelleFound() throws FindException {
        final Long billingOrderNoOrig = 1L;
        final Long auftragId = 2L;
        final Long hvtStandortId = 3L;
        final Set<Long> auftragIdList = new HashSet<>(Collections.singletonList(auftragId));
        doReturn(auftragIdList).when(testling).getWbciRelevantHurricanOrderNos(Sets.newHashSet(billingOrderNoOrig));
        when(endstellenService.findEndstellen4Auftrag(auftragId)).thenReturn(null);
        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(null);

        testling.getTechnischeRessourcen(billingOrderNoOrig, null);
    }

    @Test
    public void getWitaVtrNrsExpectNull() throws Exception {
        Endstelle endstelleMock = Mockito.mock(Endstelle.class);
        when(carrierService.findLastCB4Endstelle(anyLong())).thenReturn(null);
        assertNull(testling.getWitaVtrNr(endstelleMock));
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void getWitaVtrNrsExpectThrowException() throws Exception {
        Endstelle endstelleMock = Mockito.mock(Endstelle.class);
        when(carrierService.findLastCB4Endstelle(anyLong())).thenThrow(NullPointerException.class);
        testling.getWitaVtrNr(endstelleMock);
    }

    @Test
    public void getWitaVtrNrs() throws FindException {
        Endstelle es1 = buildEndstelle();
        Endstelle es2 = buildEndstelle();

        Carrierbestellung cb1 = new CarrierbestellungBuilder().withVtrNr("vtr1").setPersist(false).build();
        Carrierbestellung cb2 = new CarrierbestellungBuilder().withVtrNr("vtr2").setPersist(false).build();

        when(carrierService.findLastCB4Endstelle(es1.getId())).thenReturn(cb1);
        when(carrierService.findLastCB4Endstelle(es2.getId())).thenReturn(cb2);

        assertEquals(testling.getWitaVtrNr(es1), cb1.getVtrNr());
        assertEquals(testling.getWitaVtrNr(es2), cb2.getVtrNr());
    }

    @Test
    public void getWbciLineIdExpectNull() throws Exception {
        when(physikService.findOrCreateVerbindungsBezeichnungForWbci(anyLong())).thenReturn(null);
        assertNull(testling.getWbciLineId(1L));
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void getWbciLineIdExpectThrowException() throws Exception {
        when(physikService.findOrCreateVerbindungsBezeichnungForWbci(anyLong())).thenThrow(NullPointerException.class);
        testling.getWbciLineId(1L);
    }

    @Test
    public void getWbciLineIdNrs() throws Exception {
        Long auftragId = 1L;
        String expectedLineId = "DEU.MNET.00001";
        VerbindungsBezeichnung vbz = new VerbindungsBezeichnungBuilder().withRandomUniqueCode()
                .withWbciLineId(expectedLineId).build();

        when(physikService.findOrCreateVerbindungsBezeichnungForWbci(auftragId)).thenReturn(vbz);

        assertEquals(testling.getWbciLineId(auftragId), expectedLineId);
    }

    private Endstelle buildEndstelle() {
        return new EndstelleBuilder()
                .withRandomId()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().setPersist(false))
                .setPersist(false).build();
    }

    @DataProvider(name = "getWbciRelevantHurricanOrdersDataProvider")
    public Object[][] getWbciRelevantHurricanOrdersDataProvider() {
        AuftragDaten active1 = new AuftragDatenBuilder().withStatusId(AuftragStatus.IN_BETRIEB).withRandomAuftragId()
                .setPersist(false).build();
        AuftragDaten active2 = new AuftragDatenBuilder().withStatusId(AuftragStatus.KUENDIGUNG_ERFASSEN)
                .withRandomAuftragId().setPersist(false).build();
        AuftragDaten active3 = new AuftragDatenBuilder().withStatusId(AuftragStatus.TECHNISCHE_REALISIERUNG)
                .withRandomAuftragId().setPersist(false).build();
        AuftragDaten active4 = new AuftragDatenBuilder().withStatusId(AuftragStatus.ERFASSUNG).withRandomAuftragId()
                .setPersist(false).build();
        AuftragDaten cancelled1 = new AuftragDatenBuilder().withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT)
                .withRandomAuftragId().setPersist(false).build();
        AuftragDaten cancelled2 = new AuftragDatenBuilder().withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT)
                .withRandomAuftragId().setPersist(false).build();

        return new Object[][] {
                { null, ImmutableSet.<Long>of() },
                { Collections.singletonList(active1), ImmutableSet.of(active1.getAuftragId()) },
                { Arrays.asList(active1, cancelled1), ImmutableSet.of(active1.getAuftragId()) },
                { Arrays.asList(active1, active2, active3),
                        ImmutableSet.of(active1.getAuftragId(), active2.getAuftragId(), active3.getAuftragId()) },
                { Arrays.asList(active1, active2, active3, active4),
                        ImmutableSet.of(active1.getAuftragId(), active2.getAuftragId(), active3.getAuftragId(),
                                active4.getAuftragId()) },
                { Collections.singletonList(cancelled1), ImmutableSet.of(cancelled1.getAuftragId()) },
                { Arrays.asList(cancelled1, cancelled2),
                        ImmutableSet.of(cancelled1.getAuftragId(), cancelled2.getAuftragId()) },
        };
    }

    @Test(dataProvider = "getWbciRelevantHurricanOrdersDataProvider")
    public void getWbciRelevantHurricanOrderNos(List<AuftragDaten> auftragDatenList, Set<Long> expectedResult)
            throws FindException {
        Long billingOrderNo = 1L;
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(billingOrderNo)).thenReturn(auftragDatenList);

        Set<Long> result = testling.getWbciRelevantHurricanOrderNos(Sets.newHashSet(billingOrderNo));
        assertEquals(result, expectedResult);
    }

    @Test(dataProvider = "getWbciRelevantHurricanOrdersDataProvider")
    public void getWbciRelevantHurricanOrderNosForNorders(List<AuftragDaten> auftragDatenList, Set<Long> expectedResult)
            throws FindException {

        if (CollectionUtils.isNotEmpty(auftragDatenList)) {
            for (Long i = 1L; i <= auftragDatenList.size(); i++) {
                when(ccAuftragService.findAuftragDaten4OrderNoOrig(i)).thenReturn(
                        Collections.singletonList(auftragDatenList.get(i.intValue() - 1)));
            }
        }
        Set<Long> result = testling.getWbciRelevantHurricanOrderNos(1L, Sets.newHashSet(2L, 3L, 4L, 5L, 6L));
        assertEquals(result, expectedResult);

        result = testling.getWbciRelevantHurricanOrderNos(1L, Sets.newHashSet(2L, 3L, 4L));
        assertEquals(result, expectedResult);
    }

    @Test
    public void testGetHurricanOrderIdForWitaVtrNrAndCurrentVAException() throws Exception {
        doThrow(WbciServiceException.class).when(testling).getWbciRelevantHurricanOrderNos(anyLong(), anySetOf(Long.class));
        assertNull(testling.getHurricanOrderIdForWitaVtrNrAndCurrentVA("V00001", 2L, new HashSet<>()));
    }

    @Test
    public void testGetHurricanOrderIdForWitaVtrNrAndCurrentVA() throws Exception {
        final Long hurricanId = 12345L;
        final String vertragsNr = "V00001";
        doReturn(Sets.newHashSet(10L, 20L, 30L)).when(testling).getWbciRelevantHurricanOrderNos(anyLong(), anySetOf(Long.class));

        when(endstellenService.findEndstelle4Auftrag(hurricanId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(null);
        assertNull(testling.getHurricanOrderIdForWitaVtrNrAndCurrentVA(vertragsNr, hurricanId, new HashSet<>()));
        verify(endstellenService).findEndstelle4Auftrag(10L, Endstelle.ENDSTELLEN_TYP_B);
        verify(endstellenService).findEndstelle4Auftrag(20L, Endstelle.ENDSTELLEN_TYP_B);
        verify(endstellenService).findEndstelle4Auftrag(30L, Endstelle.ENDSTELLEN_TYP_B);

        Long endstelleId = 999L;
        Endstelle endstelle = mock(Endstelle.class);
        when(endstelle.getId()).thenReturn(endstelleId);
        when(endstellenService.findEndstelle4Auftrag(30L, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle);
        Carrierbestellung carrierBestellung = mock(Carrierbestellung.class);
        when(carrierBestellung.getVtrNr()).thenReturn("V0000");
        when(carrierService.findLastCB4Endstelle(endstelleId)).thenReturn(carrierBestellung);

        assertNull(testling.getHurricanOrderIdForWitaVtrNrAndCurrentVA(vertragsNr, hurricanId, new HashSet<>()));
        verify(carrierService).findLastCB4Endstelle(endstelleId);

        when(carrierBestellung.getVtrNr()).thenReturn(vertragsNr);
        assertEquals(testling.getHurricanOrderIdForWitaVtrNrAndCurrentVA(vertragsNr, hurricanId, new HashSet<>()).longValue(), 30L);
    }

    @Test
    public void testFindPreAgreements() throws Exception {
        CarrierRole carrierRole = CarrierRole.ABGEBEND;
        doReturn(Collections.EMPTY_LIST).when(testling).findPreAgreements(carrierRole, null);

        List<PreAgreementVO> preAgreements = testling.findPreAgreements(carrierRole);

        Assert.assertTrue(preAgreements.isEmpty());

        verify(testling).findPreAgreements(carrierRole, null);
    }

    @Test
    public void testFindPreAgreementsByPreAgreementId() throws Exception {
        String preagreementId = "V0123456789";
        CarrierRole carrierRole = CarrierRole.ABGEBEND;
        when(wbciDao.findMostRecentPreagreements(carrierRole, preagreementId)).thenReturn(Collections.<PreAgreementVO>emptyList());

        List<PreAgreementVO> preAgreements = testling.findPreAgreements(carrierRole, preagreementId);

        Assert.assertTrue(preAgreements.isEmpty());

        verify(wbciDao).findMostRecentPreagreements(carrierRole, preagreementId);
        verify(testling).enrichTeamDescriptions(preAgreements);
    }

    @Test
    public void testEnrichTeamDescriptions() throws Exception {
        // init Mock
        AKTeam testTeam1 = new AKTeam("TestTeam1");
        AKTeam testTeam2 = new AKTeam("TestTeam2");
        Map<Long, AKTeam> teams = new HashMap<>();
        teams.put(10L, testTeam1);
        teams.put(20L, testTeam2);
        when(akTeamService.findAllAsMap()).thenReturn(teams);

        // testdata
        PreAgreementVO vo1 = new PreAgreementVO();
        vo1.setTeamId(10L);
        PreAgreementVO vo2 = new PreAgreementVO();
        vo2.setTeamId(20L);

        List<PreAgreementVO> enrichedVOs = testling.enrichTeamDescriptions(Arrays
                .asList(vo1, vo2, new PreAgreementVO()));
        Assert.assertEquals(enrichedVOs.get(0).getTeamName(), testTeam1.getName());
        Assert.assertEquals(enrichedVOs.get(1).getTeamName(), testTeam2.getName());
        assertNull(enrichedVOs.get(2).getTeamName());
    }

    @Test
    public void findVorabstimmung() {
        when(wbciDao.findWbciRequestByType("NULL", VorabstimmungsAnfrage.class)).thenReturn(new ArrayList<>());

        try {
            testling.findVorabstimmungsAnfrage("NULL");
            Assert.fail("Missing exception due to VA not found error");
        }
        catch (WbciServiceException e) {
            Assert.assertTrue(e.getMessage().contains("NULL"));
            Assert.assertTrue(e.getMessage().contains("nicht gefunden"));
        }

        final VorabstimmungsAnfrage va1 = new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciDao.findWbciRequestByType(va1.getVorabstimmungsId(), VorabstimmungsAnfrage.class)).thenReturn(Collections.singletonList(va1));
        Assert.assertEquals(testling.findVorabstimmungsAnfrage(va1.getVorabstimmungsId()), va1);
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void findVorabstimmungException() {
        final VorabstimmungsAnfrage va1 = new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        final VorabstimmungsAnfrage va2 = new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciDao.findWbciRequestByType(va1.getVorabstimmungsId(), VorabstimmungsAnfrage.class)).thenReturn(Arrays.asList(va1, va2));
        testling.findVorabstimmungsAnfrage(va1.getVorabstimmungsId());
    }

    @Test
    public void findLastForVaId() {
        String vorabstimmungsId = "DEU.MNET.TEST123456";
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn =
                new WbciGeschaeftsfallKueMrnBuilder().withVorabstimmungsId(vorabstimmungsId).build();
        List<? extends Meldung> wbciMeldungen = Arrays.asList(
                new RueckmeldungVorabstimmungBuilder().withWbciGeschaeftsfall(wbciGeschaeftsfallKueMrn).build(),
                new RueckmeldungVorabstimmungBuilder().withWbciGeschaeftsfall(wbciGeschaeftsfallKueMrn).build()
        );

        when(wbciDao.<Meldung>findMeldungenForVaId(eq(wbciGeschaeftsfallKueMrn.getVorabstimmungsId())))
                .thenReturn((List<Meldung>) wbciMeldungen);

        final Meldung result = testling.findLastForVaId(vorabstimmungsId);
        verify(wbciDao).<Meldung>findMeldungenForVaId(eq(wbciGeschaeftsfallKueMrn.getVorabstimmungsId()));
        assertNotNull(result);
        assertEquals(result, wbciMeldungen.get(0));
    }

    @Test
    public void findLastForVaIdWithTyp() {
        String vorabstimmungsId = "DEU.MNET.TEST123456";
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn =
                new WbciGeschaeftsfallKueMrnBuilder().withVorabstimmungsId(vorabstimmungsId).build();
        List<RueckmeldungVorabstimmung> wbciMeldungen = Arrays.asList(
                new RueckmeldungVorabstimmungBuilder().withWbciGeschaeftsfall(wbciGeschaeftsfallKueMrn).build(),
                new RueckmeldungVorabstimmungBuilder().withWbciGeschaeftsfall(wbciGeschaeftsfallKueMrn).build()
        );

        when(wbciDao.findMeldungenForVaIdAndMeldungClass(
                vorabstimmungsId,
                RueckmeldungVorabstimmung.class))
                .thenReturn(wbciMeldungen);

        final Meldung result = testling.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class);
        verify(wbciDao, only()).findMeldungenForVaIdAndMeldungClass(vorabstimmungsId, RueckmeldungVorabstimmung.class);
        assertNotNull(result);
        assertEquals(result, wbciMeldungen.get(0));
    }

    @Test
    public void getPreAgreementIdsByOrderNoOrig() {
        Long billingOrderNoOrig = 1000L;
        List<WbciGeschaeftsfall> wbciGeschaeftsfalls = Arrays.asList(
                (WbciGeschaeftsfall) new WbciGeschaeftsfallKueMrnTestBuilder()
                        .withBillingOrderNoOrig(billingOrderNoOrig)
                        .withVorabstimmungsId("DEU.MNET.V000000001")
                        .build(),
                new WbciGeschaeftsfallKueMrnTestBuilder()
                        .withBillingOrderNoOrig(billingOrderNoOrig)
                        .withVorabstimmungsId("DEU.MNET.V000000002")
                        .build(),
                new WbciGeschaeftsfallKueMrnTestBuilder()
                        .withBillingOrderNoOrig(billingOrderNoOrig + 1)
                        .withNonBillingRelevantOrderNos(Sets.newHashSet(billingOrderNoOrig))
                        .withVorabstimmungsId("DEU.MNET.V000000003")
                        .build()
        );
        when(wbciDao.<WbciGeschaeftsfall>findGfByOrderNoOrig(eq(billingOrderNoOrig)))
                .thenReturn(wbciGeschaeftsfalls);
        final Set<String> preAgreementIds = testling.getPreAgreementIdsByOrderNoOrig(billingOrderNoOrig);
        assertEquals(preAgreementIds.size(), 3);
        Assert.assertTrue(preAgreementIds.contains("DEU.MNET.V000000001"));
        Assert.assertTrue(preAgreementIds.contains("DEU.MNET.V000000002"));
        Assert.assertTrue(preAgreementIds.contains("DEU.MNET.V000000003"));
    }

    @Test
    public void testGetKundenTyp() throws Exception {
        Long taifunOrderNo = 1L;
        Long kundenNo = 22L;
        BAuftrag auftrag = mock(BAuftrag.class);
        Kunde taifunKunde = mock(Kunde.class);
        doReturn(auftrag).when(testling).getBillingAuftrag(taifunOrderNo);
        when(auftrag.getKundeNo()).thenReturn(kundenNo);
        when(kundenService.findKunde(kundenNo)).thenReturn(taifunKunde);

        // Privatkunde
        when(taifunKunde.isBusinessCustomer()).thenReturn(false);
        assertEquals(testling.getKundenTyp(taifunOrderNo), KundenTyp.PK);

        // Geschäftskunde
        when(taifunKunde.isBusinessCustomer()).thenReturn(true);
        assertEquals(testling.getKundenTyp(taifunOrderNo), KundenTyp.GK);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Der Kunde mit der ID '22' zum Taifun Auftrag '1' konnte nicht ermittelt werden!")
    public void testGetKundenTypNotFound() throws Exception {
        Long taifunOrderNo = 1L;
        Long kundenNo = 22L;
        BAuftrag auftrag = mock(BAuftrag.class);
        doReturn(auftrag).when(testling).getBillingAuftrag(taifunOrderNo);
        when(auftrag.getKundeNo()).thenReturn(kundenNo);
        when(kundenService.findKunde(kundenNo)).thenReturn(null);

        testling.getKundenTyp(taifunOrderNo);
    }

    @Test
    public void testGetBillingAuftrag() throws Exception {
        Long taifunOrderNo = 1L;
        BAuftrag auftrag = mock(BAuftrag.class);
        when(billingAuftragService.findAuftrag(taifunOrderNo)).thenReturn(auftrag);
        Assert.assertEquals(testling.getBillingAuftrag(taifunOrderNo), auftrag);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Es konnte kein Taifun Auftrag mit der Id '1' gefunden werden!")
    public void testGetBillingAuftragNotFound() throws Exception {
        Long taifunOrderNo = 1L;
        when(billingAuftragService.findAuftrag(taifunOrderNo)).thenReturn(null);
        testling.getBillingAuftrag(taifunOrderNo);
    }

    @Test
    public void testFindNonBillingRelevantTaifunOrderIds() {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        wbciGeschaeftsfall.setAbgebenderEKP(CarrierCode.MNET);
        wbciGeschaeftsfall.setAufnehmenderEKP(CarrierCode.DTAG);

        wbciGeschaeftsfall.setNonBillingRelevantOrderNoOrigs(Collections.singleton(1001L));

        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        Set<Long> auftragIds = testling
                .findNonBillingRelevantTaifunAuftragIds(wbciGeschaeftsfall.getVorabstimmungsId());
        Assert.assertEquals(auftragIds.size(), 1L);
        Assert.assertEquals(auftragIds.iterator().next().longValue(), 1001L);

        verify(wbciDao).findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId());
    }

    @Test
    public void testAssignTaifunOderNo() throws Exception {
        VorabstimmungsAnfrage vaRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        Long taifunOrderNo = 1L;
        Long kundenNo = 22L;
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        wbciGeschaeftsfall.setAbgebenderEKP(CarrierCode.MNET);
        wbciGeschaeftsfall.setAufnehmenderEKP(CarrierCode.DTAG);

        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        when(wbciDao.findWbciRequestByType(wbciGeschaeftsfall.getVorabstimmungsId(), VorabstimmungsAnfrage.class))
                .thenReturn(Collections.singletonList(vaRequest));

        BAuftrag auftrag = mock(BAuftrag.class);
        when(auftrag.getKundeNo()).thenReturn(kundenNo);
        doReturn(auftrag).when(testling).getBillingAuftrag(taifunOrderNo);
        doReturn(KundenTyp.GK).when(testling).getKundenTypForKundenNo(kundenNo, taifunOrderNo);

        List<AuftragDaten> auftragDatenList = new ArrayList<>();
        auftragDatenList.add(new AuftragDatenBuilder().withStatusId(AuftragStatus.IN_BETRIEB).withRandomAuftragId()
                .setPersist(false).build());
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(taifunOrderNo)).thenReturn(auftragDatenList);

        HVTStandortBuilder hvtStandort = getHvtStandortBuilder();
        Endstelle endstelle = getEndstelle(hvtStandort);
        List<Endstelle> endstellen = Collections.singletonList(endstelle);
        Reference hvtTypeRef = new ReferenceBuilder()
                .withWbciTechnologieCode(Technologie.FTTB.getWbciTechnologieCode()).setPersist(false).build();

        when(endstellenService.findEndstellen4Auftrag(any(Long.class))).thenReturn(endstellen);
        when(hvtService.findHVTStandort(hvtStandort.get().getHvtIdStandort())).thenReturn(hvtStandort.get());
        when(referenceService.findReference(hvtStandort.get().getStandortTypRefId())).thenReturn(hvtTypeRef);
        when(carrierService.findLastCB4Endstelle(endstelle.getId())).thenReturn(null);

        Set<Long> nonBillables = new HashSet<>();
        nonBillables.add(1001L);
        nonBillables.add(1002L);

        when(rufnummerService.getCorrespondingOrderNoOrigs(taifunOrderNo)).thenReturn(nonBillables);

        testling.assignTaifunOrder(wbciGeschaeftsfall.getVorabstimmungsId(), taifunOrderNo, true);

        assertEquals(wbciGeschaeftsfall.getBillingOrderNoOrig(), taifunOrderNo);
        assertEquals(wbciGeschaeftsfall.getAuftragId(), auftragDatenList.get(0).getAuftragId());
        assertEquals(wbciGeschaeftsfall.getMnetTechnologie(), Technologie.FTTB);
        assertEquals(wbciGeschaeftsfall.getEndkunde().getKundenTyp(), KundenTyp.GK);
        assertEquals(wbciGeschaeftsfall.getNonBillingRelevantOrderNoOrigs().size(), 2L);
        assertTrue(wbciGeschaeftsfall.getNonBillingRelevantOrderNoOrigs().contains(1001L));
        assertTrue(wbciGeschaeftsfall.getNonBillingRelevantOrderNoOrigs().contains(1002L));

        verify(wbciDao).store(wbciGeschaeftsfall);
        verify(wbciDao).findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId());
        verify(testling).getBillingAuftrag(taifunOrderNo);
        verify(testling).getKundenTypForKundenNo(kundenNo, taifunOrderNo);
        verify(wbciCustomerService, times(1)).sendCustomerServiceProtocol(vaRequest);
        verify(ccAuftragService).findAuftragDaten4OrderNoOrig(taifunOrderNo);
        verify(endstellenService).findEndstellen4Auftrag(any(Long.class));
        verify(hvtService).findHVTStandort(hvtStandort.get().getHvtIdStandort());
        verify(referenceService).findReference(hvtStandort.get().getStandortTypRefId());
        verify(rufnummerService).getCorrespondingOrderNoOrigs(taifunOrderNo);
    }

    @Test
    public void assignTaifunOrderIdWithMultipleHurricanOrders() throws FindException {
        VorabstimmungsAnfrage vaRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        Long taifunOrderId = 1L;
        Long kundenNo = 22L;
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        wbciGeschaeftsfall.setAbgebenderEKP(CarrierCode.MNET);
        wbciGeschaeftsfall.setAufnehmenderEKP(CarrierCode.DTAG);
        wbciGeschaeftsfall.setAuftragId(null);
        wbciGeschaeftsfall.setMnetTechnologie(null);

        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        BAuftrag auftrag = mock(BAuftrag.class);
        when(auftrag.getKundeNo()).thenReturn(kundenNo);
        Kunde taifunKunde = mock(Kunde.class);
        when(kundenService.findKunde(kundenNo)).thenReturn(taifunKunde);
        when(billingAuftragService.findAuftrag(taifunOrderId)).thenReturn(auftrag);
        when(wbciDao.findWbciRequestByType(wbciGeschaeftsfall.getVorabstimmungsId(), VorabstimmungsAnfrage.class))
                .thenReturn(Collections.singletonList(vaRequest));

        List<AuftragDaten> auftragDatenList = new ArrayList<>();
        auftragDatenList.add(new AuftragDatenBuilder().withStatusId(AuftragStatus.IN_BETRIEB).withRandomAuftragId()
                .setPersist(false).build());
        auftragDatenList.add(new AuftragDatenBuilder().withStatusId(AuftragStatus.IN_BETRIEB).withRandomAuftragId()
                .setPersist(false).build());
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(taifunOrderId)).thenReturn(auftragDatenList);
        doReturn(Technologie.TAL_DSL).when(testling).getMnetTechnologie(anyLong());

        testling.assignTaifunOrder(wbciGeschaeftsfall.getVorabstimmungsId(), taifunOrderId, true);

        assertEquals(wbciGeschaeftsfall.getBillingOrderNoOrig(), taifunOrderId);
        assertNotNull(wbciGeschaeftsfall.getAuftragId());
        Assert.assertEquals(wbciGeschaeftsfall.getMnetTechnologie(), Technologie.TAL_DSL);

        verify(wbciDao).store(wbciGeschaeftsfall);
        verify(wbciDao).findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId());
        verify(billingAuftragService).findAuftrag(taifunOrderId);
        verify(wbciCustomerService, times(1)).sendCustomerServiceProtocol(vaRequest);
        verify(ccAuftragService).findAuftragDaten4OrderNoOrig(taifunOrderId);
    }

    @Test
    public void assignTaifunOrderIdWithoutAddCommunication() throws FindException {
        VorabstimmungsAnfrage vaRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        Long taifunOrderId = 1L;
        Long kundenNo = 22L;
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        wbciGeschaeftsfall.setAbgebenderEKP(CarrierCode.MNET);
        wbciGeschaeftsfall.setAufnehmenderEKP(CarrierCode.DTAG);
        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        BAuftrag auftrag = mock(BAuftrag.class);
        when(auftrag.getKundeNo()).thenReturn(kundenNo);
        Kunde taifunKunde = mock(Kunde.class);
        when(kundenService.findKunde(kundenNo)).thenReturn(taifunKunde);
        when(billingAuftragService.findAuftrag(taifunOrderId)).thenReturn(auftrag);
        when(wbciDao.findWbciRequestByType(wbciGeschaeftsfall.getVorabstimmungsId(), VorabstimmungsAnfrage.class))
                .thenReturn(Collections.singletonList(vaRequest));
        testling.assignTaifunOrder(wbciGeschaeftsfall.getVorabstimmungsId(), taifunOrderId, false);
        assertEquals(taifunOrderId, wbciGeschaeftsfall.getBillingOrderNoOrig());
        verify(wbciDao).store(wbciGeschaeftsfall);
        verify(wbciDao).findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId());
        verify(billingAuftragService).findAuftrag(taifunOrderId);
        verify(wbciCustomerService, never()).sendCustomerServiceProtocol(vaRequest);
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void assignTaifunOrderId_MnetAsReceivingCarrier() throws FindException {
        Long taifunOrderId = 1L;
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        final BAuftrag auftrag = new BAuftrag();
        when(billingAuftragService.findAuftrag(taifunOrderId)).thenReturn(auftrag);
        testling.assignTaifunOrder(wbciGeschaeftsfall.getVorabstimmungsId(), taifunOrderId, false);
    }

    @Test
    public void getBAuftrag() throws FindException {
        Long taifunOrderId = 1L;
        BAuftrag taifunAuftrag = mock(BAuftrag.class);
        when(billingAuftragService.findAuftrag(taifunOrderId)).thenReturn(taifunAuftrag);
        Assert.assertEquals(taifunAuftrag, testling.getBillingAuftrag(taifunOrderId));
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Es konnte kein Taifun Auftrag mit der Id '1' gefunden werden!")
    public void getBAuftrag_NoTaifunOrderExists() throws FindException {
        Long taifunOrderId = 1L;
        when(billingAuftragService.findAuftrag(taifunOrderId)).thenReturn(null);
        testling.getBillingAuftrag(taifunOrderId);
    }

    @Test
    public void testInitialAssignTask() throws FindException, AKAuthenticationException {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        AKUser testUser = getTestUser(getTestTeam(1L));

        testling.assignTask(wbciGeschaeftsfall.getVorabstimmungsId(), testUser);

        assertEquals(wbciGeschaeftsfall.getUserId(), testUser.getId());
        assertEquals(wbciGeschaeftsfall.getUserName(), testUser.getLoginName());
        assertEquals(wbciGeschaeftsfall.getTeamId(), testUser.getTeam().getId());
        assertEquals(wbciGeschaeftsfall.getCurrentUserId(), testUser.getId());
        assertEquals(wbciGeschaeftsfall.getCurrentUserName(), testUser.getLoginName());
        verify(wbciDao).store(wbciGeschaeftsfall);
        verify(wbciDao).findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId());
    }

    @Test
    public void testAssignTask() throws FindException, AKAuthenticationException {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withBearbeiter(1L, "testperson", 2L)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        AKUser testUser = getTestUser(getTestTeam(1L));

        testling.assignTask(wbciGeschaeftsfall.getVorabstimmungsId(), testUser);

        assertEquals(wbciGeschaeftsfall.getUserId(), (Long) 1L);
        assertEquals(wbciGeschaeftsfall.getUserName(), "testperson");
        assertEquals(wbciGeschaeftsfall.getTeamId(), (Long) 2L);
        assertNotEquals(wbciGeschaeftsfall.getUserId(), wbciGeschaeftsfall.getCurrentUserId());
        assertNotEquals(wbciGeschaeftsfall.getUserName(), wbciGeschaeftsfall.getCurrentUserName());
        assertEquals(wbciGeschaeftsfall.getCurrentUserId(), testUser.getId());
        assertEquals(wbciGeschaeftsfall.getCurrentUserName(), testUser.getLoginName());
        verify(wbciDao).store(wbciGeschaeftsfall);
        verify(wbciDao).findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId());
    }

    @Test
    public void testAssignTaskSuperRoleOverwrite() throws FindException, AKAuthenticationException {
        final long teamId = 11L;
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withBearbeiter(999L, "someperson", teamId)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        AKUser testUser = getTestUser(getTestTeam(teamId));

        testling.assignTask(wbciGeschaeftsfall.getVorabstimmungsId(), testUser);

        assertEquals(wbciGeschaeftsfall.getUserId().longValue(), 999L);
        assertEquals(wbciGeschaeftsfall.getUserName(), "someperson");
        assertEquals(wbciGeschaeftsfall.getTeamId().longValue(), teamId);
        assertEquals(wbciGeschaeftsfall.getCurrentUserId(), testUser.getId());
        assertEquals(wbciGeschaeftsfall.getCurrentUserName(), testUser.getLoginName());
        verify(wbciDao).store(wbciGeschaeftsfall);
        verify(wbciDao).findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId());
    }

    @Test
    public void testReleaseTask() throws FindException, AKAuthenticationException {
        AKUser testUser = getTestUser(null);

        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withBearbeiter(testUser.getId(), testUser.getLoginName(), 11L)
                .withAktuellerBearbeiter(testUser.getId(), testUser.getLoginName())
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        testling.releaseTask(wbciGeschaeftsfall.getVorabstimmungsId());

        assertNotNull(wbciGeschaeftsfall.getUserId());
        assertNotNull(wbciGeschaeftsfall.getUserName());
        assertNotNull(wbciGeschaeftsfall.getTeamId());
        assertNull(wbciGeschaeftsfall.getCurrentUserId());
        assertNull(wbciGeschaeftsfall.getCurrentUserName());
        verify(wbciDao).store(wbciGeschaeftsfall);
        verify(wbciDao).findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId());
    }

    @Test
    public void testReleaseTaskAlreadyFromDifferentUser() throws FindException, AKAuthenticationException {
        final long teamId = 11L;
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withBearbeiter(999L, "someperson", teamId)
                .withAktuellerBearbeiter(999L, "someperson")
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        AKUser testUser = getTestUser(getTestTeam(teamId));

        List<AKRole> userRoles = new ArrayList<>();
        when(userService.getRoles(testUser.getId())).thenReturn(userRoles);

        testling.releaseTask(wbciGeschaeftsfall.getVorabstimmungsId());
        assertNull(wbciGeschaeftsfall.getCurrentUserId());
        assertNull(wbciGeschaeftsfall.getCurrentUserName());
        verify(wbciDao).store(wbciGeschaeftsfall);
        verify(wbciDao).findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId());
    }

    @Test
    public void testCloseProcessing() throws Exception {
        WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueMrnBuilder().build();

        VorabstimmungsAnfrage vaRequest = new VorabstimmungsAnfrageTestBuilder()
                .withWbciGeschaeftsfall(gf)
                .withRequestStatus(WbciRequestStatus.AKM_TR_VERSENDET)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.findById(vaRequest.getId(), WbciRequest.class)).thenReturn(vaRequest);
        testling.closeProcessing(vaRequest.getId());
        verify(wbciGeschaeftsfallStatusUpdateServiceMock).updateGeschaeftsfallStatus(vaRequest.getId(),
                WbciGeschaeftsfallStatus.PASSIVE);
    }

    @DataProvider
    public Object[][] testCloseProcessingNotCalledIfRequestStatusIsInitialDataProvider() {
        return new Object[][] {
                { WbciRequestStatus.VA_EMPFANGEN },
                { WbciRequestStatus.VA_VERSENDET },
                { WbciRequestStatus.VA_VORGEHALTEN },
                { WbciRequestStatus.TV_EMPFANGEN },
                { WbciRequestStatus.TV_VERSENDET },
                { WbciRequestStatus.TV_VORGEHALTEN },
                { WbciRequestStatus.STORNO_EMPFANGEN },
                { WbciRequestStatus.STORNO_VERSENDET },
                { WbciRequestStatus.STORNO_VORGEHALTEN }
        };
    }

    @Test(dataProvider = "testCloseProcessingNotCalledIfRequestStatusIsInitialDataProvider")
    public void testCloseProcessingNotCalledIfRequestStatusIsInitial(WbciRequestStatus wbciRequestStatus) throws Exception {
        WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueMrnBuilder().build();

        VorabstimmungsAnfrage vaRequest = new VorabstimmungsAnfrageTestBuilder()
                .withWbciGeschaeftsfall(gf)
                .withRequestStatus(wbciRequestStatus)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.findById(vaRequest.getId(), WbciRequest.class)).thenReturn(vaRequest);
        testling.closeProcessing(vaRequest.getId());
        verify(wbciGeschaeftsfallStatusUpdateServiceMock, times(0)).updateGeschaeftsfallStatus(vaRequest.getId(),
                WbciGeschaeftsfallStatus.PASSIVE);
    }

    @Test
    public void testCloseProcessingNotCalledIfStatusIsNewVa() throws Exception {
        WbciGeschaeftsfall gf = new WbciGeschaeftsfallKueMrnBuilder().withStatus(WbciGeschaeftsfallStatus.NEW_VA).build();

        VorabstimmungsAnfrage vaRequest = new VorabstimmungsAnfrageTestBuilder()
                .withWbciGeschaeftsfall(gf)
                .withRequestStatus(WbciRequestStatus.AKM_TR_VERSENDET)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.findById(vaRequest.getId(), WbciRequest.class)).thenReturn(vaRequest);

        testling.closeProcessing(vaRequest.getId());
        verify(wbciGeschaeftsfallStatusUpdateServiceMock, times(0))
                .updateGeschaeftsfallStatus(vaRequest.getId(), WbciGeschaeftsfallStatus.PASSIVE);
    }

    @Test
    public void testSaveComment() {
        final long teamId = 11L;
        final AKUser testUser = getTestUser(getTestTeam(teamId));
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withBearbeiter(testUser.getId(), testUser.getLoginName(), teamId)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        String newCommentData = testling.saveComment(wbciGeschaeftsfall.getVorabstimmungsId(), "First comment data",
                testUser);

        assertNotNull(wbciGeschaeftsfall.getBemerkungen());
        Assert.assertTrue(newCommentData.startsWith(String.format("First comment data (%s, ", testUser.getLoginName())));
        verify(wbciDao).store(wbciGeschaeftsfall);
        verify(wbciDao).findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId());
    }

    @Test
    public void testSaveCommentAppend() {
        final long teamId = 11L;
        final AKUser testUser = getTestUser(getTestTeam(teamId));
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withBearbeiter(testUser.getId(), testUser.getLoginName(), teamId)
                .withBemerkung("Some existing comment")
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        String newCommentData = testling.saveComment(wbciGeschaeftsfall.getVorabstimmungsId(),
                "Some existing comment New comment data", testUser);

        assertNotNull(wbciGeschaeftsfall.getBemerkungen());
        Assert.assertTrue(newCommentData.startsWith(String.format("Some existing comment New comment data (%s, ",
                testUser.getLoginName())));
        verify(wbciDao).store(wbciGeschaeftsfall);
        verify(wbciDao).findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId());
    }

    @Test
    public void testSaveCommentNothingChanged() {
        final long teamId = 11L;
        final AKUser testUser = getTestUser(getTestTeam(teamId));
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withBearbeiter(testUser.getId(), testUser.getLoginName(), teamId)
                .withBemerkung("Some existing comment")
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        String newCommentData = testling.saveComment(wbciGeschaeftsfall.getVorabstimmungsId(), "Some existing comment",
                testUser);

        assertNotNull(wbciGeschaeftsfall.getBemerkungen());
        assertEquals(newCommentData, "Some existing comment");
        verify(wbciDao, never()).store(wbciGeschaeftsfall);
        verify(wbciDao).findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId());
    }

    @Test
    public void testSaveCommentUnknownUser() {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        String newCommentData = testling.saveComment(wbciGeschaeftsfall.getVorabstimmungsId(), "First comment data",
                null);

        assertNotNull(wbciGeschaeftsfall.getBemerkungen());
        Assert.assertTrue(newCommentData.startsWith(String
                .format("First comment data (%s, ", HurricanConstants.UNKNOWN)));
        verify(wbciDao).store(wbciGeschaeftsfall);
        verify(wbciDao).findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId());
    }

    @Test
    public void addComment() {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        wbciGeschaeftsfall.setBemerkungen("Test (UserName, date)");

        String commentToAdd = "added comment";
        String expected = String.format("%s%n%s", wbciGeschaeftsfall.getBemerkungen(), commentToAdd);

        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        doReturn(expected).when(testling).saveComment(anyString(), anyString(), any(AKUser.class));

        testling.addComment(wbciGeschaeftsfall.getVorabstimmungsId(), commentToAdd, null);

        verify(testling).saveComment(wbciGeschaeftsfall.getVorabstimmungsId(), expected, null);
    }

    @Test
    public void addCommentWithNoPreviousCommentData() {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        wbciGeschaeftsfall.setBemerkungen(null);

        String commentToAdd = "added comment";

        when(wbciDao.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        doReturn(commentToAdd).when(testling).saveComment(anyString(), anyString(), any(AKUser.class));

        testling.addComment(wbciGeschaeftsfall.getVorabstimmungsId(), commentToAdd, null);

        verify(testling).saveComment(wbciGeschaeftsfall.getVorabstimmungsId(), commentToAdd, null);
    }

    @Test
    public void testGetLatestRealDate() throws FindException {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        final Date expectedRealDate = Date.from(LocalDateTime.now().plusMonths(2).atZone(ZoneId.systemDefault()).toInstant());
        List<Rufnummer> rns = Arrays.asList(
                new RufnummerBuilder()
                        .withRealDate(Date.from(LocalDateTime.now().plusMonths(1).atZone(ZoneId.systemDefault()).toInstant()))
                        .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                        .build(),
                new RufnummerBuilder()
                        .withRealDate(expectedRealDate)
                        .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                        .build(),
                new RufnummerBuilder()
                        .withRealDate(Date.from(LocalDateTime.now().plusMonths(3).atZone(ZoneId.systemDefault()).toInstant()))
                        .withPortMode(Rufnummer.PORT_MODE_ABGEHEND)
                        .build()
        );
        when(rufnummerService.findRNs4Auftrag(wbciGeschaeftsfall.getBillingOrderNoOrig())).thenReturn(rns);
        final Date latestRealDate = testling.getLatestRealDate(wbciGeschaeftsfall);
        assertEquals(latestRealDate, expectedRealDate);
        verify(rufnummerService).findRNs4Auftrag(wbciGeschaeftsfall.getBillingOrderNoOrig());
    }

    @Test
    public void testGetLatestRealDateNoRufnummer() throws FindException {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(rufnummerService.findRNs4Auftrag(wbciGeschaeftsfall.getBillingOrderNoOrig())).thenReturn(null);
        final Date latestRealDate = testling.getLatestRealDate(wbciGeschaeftsfall);
        assertNull(latestRealDate);
        verify(rufnummerService).findRNs4Auftrag(wbciGeschaeftsfall.getBillingOrderNoOrig());
    }

    @Test
    public void testGetLatestRealDateEmptyRufnummerlist() throws FindException {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(rufnummerService.findRNs4Auftrag(wbciGeschaeftsfall.getBillingOrderNoOrig())).thenReturn(
                Collections.EMPTY_LIST);
        final Date latestRealDate = testling.getLatestRealDate(wbciGeschaeftsfall);
        assertNull(latestRealDate);
        verify(rufnummerService).findRNs4Auftrag(wbciGeschaeftsfall.getBillingOrderNoOrig());
    }

    @Test
    public void testFindVorabstimmungIdsByBillingOrderNoOrig() throws Exception {
        VorabstimmungIdsByBillingOrderNoCriteria criteria = new VorabstimmungIdsByBillingOrderNoCriteria(1234L,
                WbciRequest.class);
        final String vaId1 = "DEU.MNET.001";
        final String vaId2 = "DEU.MNET.002";
        when(wbciDao.findVorabstimmungIdsByBillingOrderNoOrig(criteria)).thenReturn(Arrays.asList(vaId1, vaId2));

        Set<String> result = testling.findVorabstimmungIdsByBillingOrderNoOrig(criteria);
        Assert.assertTrue(result.contains(vaId1));
        Assert.assertTrue(result.contains(vaId2));
        Assert.assertEquals(result.size(), 2);
    }

    @Test
    public void testFindVorabstimmungIdsByBillingOrderNoOrigWithNull() throws Exception {
        VorabstimmungIdsByBillingOrderNoCriteria criteria = new VorabstimmungIdsByBillingOrderNoCriteria(1234L,
                WbciRequest.class);
        when(wbciDao.findVorabstimmungIdsByBillingOrderNoOrig(criteria)).thenReturn(null);

        Set<String> result = testling.findVorabstimmungIdsByBillingOrderNoOrig(criteria);
        Assert.assertEquals(result.size(), 0);
    }

    @Test
    public void shouldReturnSichererHafenIsTrue() {
        List<UebernahmeRessourceMeldung> list = Arrays.asList(
                buildUebernahmeRessourceMeldung(false, false, LocalDateTime.now().minusHours(1)),
                buildUebernahmeRessourceMeldung(true, false, LocalDateTime.now()), // latest
                buildUebernahmeRessourceMeldung(false, false, LocalDateTime.now().minusHours(2)));

        when(wbciDao.findMeldungenForVaIdAndMeldungClass(anyString(), eq(UebernahmeRessourceMeldung.class)))
                .thenReturn(list);

        Assert.assertTrue(testling.isSichererHafenRequested(""));
    }

    @Test
    public void shouldReturnSichererHafenIsFalse() {
        List<UebernahmeRessourceMeldung> list = Arrays.asList(
                buildUebernahmeRessourceMeldung(true, false, LocalDateTime.now().minusHours(1)),
                buildUebernahmeRessourceMeldung(false, false, LocalDateTime.now()), // latest
                buildUebernahmeRessourceMeldung(true, false, LocalDateTime.now().minusHours(2)));

        when(wbciDao.findMeldungenForVaIdAndMeldungClass(anyString(), eq(UebernahmeRessourceMeldung.class)))
                .thenReturn(list);

        Assert.assertFalse(testling.isSichererHafenRequested(""));
    }

    @Test
    public void shouldReturnSichererHafenIsFalseWhenNoAkmTrExists() {
        List<UebernahmeRessourceMeldung> list = Collections.emptyList();

        when(wbciDao.findMeldungenForVaIdAndMeldungClass(anyString(), eq(UebernahmeRessourceMeldung.class)))
                .thenReturn(list);

        Assert.assertFalse(testling.isSichererHafenRequested(""));
    }

    @Test
    public void shouldReturnrResourceuebernahmeIsTrue() {
        List<UebernahmeRessourceMeldung> list = Arrays.asList(
                buildUebernahmeRessourceMeldung(false, false, LocalDateTime.now().minusHours(1)),
                buildUebernahmeRessourceMeldung(false, true, LocalDateTime.now()), // latest
                buildUebernahmeRessourceMeldung(false, false, LocalDateTime.now().minusHours(2)));

        when(wbciDao.findMeldungenForVaIdAndMeldungClass(anyString(), eq(UebernahmeRessourceMeldung.class)))
                .thenReturn(list);

        Assert.assertTrue(testling.isResourceUebernahmeRequested(""));
    }

    @Test
    public void shouldReturnResourceuebernahmeIsFalse() {
        List<UebernahmeRessourceMeldung> list = Arrays.asList(
                buildUebernahmeRessourceMeldung(false, true, LocalDateTime.now().minusHours(1)),
                buildUebernahmeRessourceMeldung(false, false, LocalDateTime.now()), // latest
                buildUebernahmeRessourceMeldung(false, true, LocalDateTime.now().minusHours(2)));

        when(wbciDao.findMeldungenForVaIdAndMeldungClass(anyString(), eq(UebernahmeRessourceMeldung.class)))
                .thenReturn(list);

        Assert.assertFalse(testling.isResourceUebernahmeRequested(""));
    }

    @Test
    public void shouldReturnResourceuebernahmeIsFalseWhenNoAkmTrExists() {
        List<UebernahmeRessourceMeldung> list = Collections.emptyList();

        when(wbciDao.findMeldungenForVaIdAndMeldungClass(anyString(), eq(UebernahmeRessourceMeldung.class)))
                .thenReturn(list);

        Assert.assertFalse(testling.isResourceUebernahmeRequested(""));
    }

    @Test
    public void testGetRNsForCancelledOrder() throws FindException {
        BAuftrag order = new BAuftragBuilder().withAuftragNoOrig(99L).withGueltigBis(new Date()).build();

        Rufnummer rueckfall = new RufnummerBuilder().withRandomDnNoOrig().withOnKz("089")
                .withPortMode(Rufnummer.PORT_MODE_RUECKFALL).build();
        Rufnummer deaktivierung = new RufnummerBuilder().withRandomDnNoOrig().withOnKz("0821")
                .withPortMode(Rufnummer.PORT_MODE_DEAKTIVIERUNG).build();
        Rufnummer kommend = new RufnummerBuilder().withRandomDnNoOrig().withOnKz("0234")
                .withPortMode(Rufnummer.PORT_MODE_KOMMEND).build();
        Rufnummer abgebend = new RufnummerBuilder().withRandomDnNoOrig().withOnKz("0921")
                .withPortMode(Rufnummer.PORT_MODE_ABGEHEND).build();
        Rufnummer mobil = new RufnummerBuilder().withRandomDnNoOrig().withOnKz("0151").build();
        List<Rufnummer> rufnummern = Arrays.asList(abgebend, kommend, rueckfall, deaktivierung, mobil);

        when(rufnummerService.findRNs4Auftrag(anyLong())).thenReturn(null);
        when(billingAuftragService.findAuftrag(anyLong())).thenReturn(order);
        when(rufnummerService.findRNs4Auftrag(order.getAuftragNoOrig(), order.getGueltigBis())).thenReturn(rufnummern);
        when(rufnummerService.findLastRN(rueckfall.getDnNoOrig())).thenReturn(rueckfall);
        when(rufnummerService.findLastRN(deaktivierung.getDnNoOrig())).thenReturn(deaktivierung);
        when(rufnummerService.findLastRN(kommend.getDnNoOrig())).thenReturn(kommend);
        when(rufnummerService.findLastRN(abgebend.getDnNoOrig())).thenReturn(abgebend);

        Collection<Rufnummer> result = testling.getRNs(order.getAuftragNoOrig());
        assertNotNull(result);
        assertEquals(result.size(), 2);
        assertTrue(result.contains(rueckfall), "Rufnummer mit PortMode 'rueckfall' nicht im Result enthalten!");
        assertTrue(result.contains(deaktivierung), "Rufnummer mit PortMode 'deaktivierung' nicht im Result enthalten!");
        assertFalse(result.contains(mobil), "Mobilrufnummer darf nicht im Result enthalten sein!");
        verify(rufnummerService, never()).findLastRN(mobil.getDnNoOrig());
    }

    @Test
    public void testGetRNs() throws FindException {
        BAuftrag order = new BAuftragBuilder().withAuftragNoOrig(99L).withGueltigBis(new Date()).build();

        Rufnummer rueckfall = new RufnummerBuilder().withRandomDnNoOrig().withOnKz("089")
                .withPortMode(Rufnummer.PORT_MODE_RUECKFALL).build();
        Rufnummer deaktivierung = new RufnummerBuilder().withRandomDnNoOrig().withOnKz("0821")
                .withPortMode(Rufnummer.PORT_MODE_DEAKTIVIERUNG).build();
        Rufnummer mobil = new RufnummerBuilder().withRandomDnNoOrig().withOnKz("0151").build();
        List<Rufnummer> rufnummern = Arrays.asList(rueckfall, deaktivierung, mobil);

        when(rufnummerService.findRNs4Auftrag(order.getAuftragNoOrig())).thenReturn(rufnummern);

        Collection<Rufnummer> result = testling.getRNs(order.getAuftragNoOrig());
        assertNotNull(result);
        assertEquals(result.size(), 2);
        assertTrue(result.contains(rueckfall), "Rufnummer mit PortMode 'rueckfall' nicht im Result enthalten!");
        assertTrue(result.contains(deaktivierung), "Rufnummer mit PortMode 'deaktivierung' nicht im Result enthalten!");
        assertFalse(result.contains(mobil), "Mobilrufnummer darf nicht im Result enthalten sein!");
        verify(rufnummerService).findRNs4Auftrag(order.getAuftragNoOrig());
        verify(billingAuftragService, never()).findAuftrag(anyLong());
        verify(rufnummerService, never()).findLastRN(anyLong());
    }

    private UebernahmeRessourceMeldung buildUebernahmeRessourceMeldung(boolean sichererhafen, boolean uebernahme,
            LocalDateTime processedAt) {
        return new UebernahmeRessourceMeldungTestBuilder()
                .withSichererhafen(sichererhafen)
                .withUebernahme(uebernahme)
                .withProcessedAt(processedAt)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
    }

    private AKTeam getTestTeam(Long id) {
        return new AKTeamBuilder()
                .withId(id)
                .withName("TestTeam")
                .build();
    }

    private AKUser getTestUser(AKTeam team) {
        return new AKUserBuilder()
                .withRandomId()
                .withName("TestUser")
                .withLoginName("testuser")
                .withTeam(team)
                .build();
    }
}
