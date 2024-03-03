package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.exceptions.NoDataFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Auftrag2PeeringPartner;
import de.augustakom.hurrican.model.cc.Auftrag2PeeringPartnerBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN2EGPort;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGConfigBuilder;
import de.augustakom.hurrican.model.cc.EndgeraetPort;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IpMode;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VoipDnBlock;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.model.cc.VoipDnPlanBuilder;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartnerBuilder;
import de.augustakom.hurrican.model.cc.view.AuftragVoipDNViewBuilder;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.BlockDNView;
import de.augustakom.hurrican.model.shared.view.voip.SelectedPortsView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.SipPeeringPartnerService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.augustakom.hurrican.service.cc.impl.logindata.LoginDataService;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginData;
import de.augustakom.hurrican.service.cc.impl.logindata.model.internet.LoginDataInternet;
import de.augustakom.hurrican.service.cc.impl.logindata.model.voip.LoginDataVoip;
import de.augustakom.hurrican.service.cc.impl.logindata.model.voip.LoginDataVoipDn;

@Test(groups = BaseTest.UNIT)
public class ReportingServiceImplUnitTest extends BaseTest {

    private static final long ORDER_NO = 4711L;
    @Mock
    private IPAddressService ipAddressService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private ProduktService produktService;
    @Mock
    private VoIPService voIPService;
    @Mock
    private EndstellenService endstellenService;
    @Mock
    private HVTService hvtService;
    @Mock
    private ReferenceService referenceService;
    @Mock
    private CCKundenService ccKundenService;
    @Mock
    private PhysikService physikService;
    @Mock
    private CCLeistungsService leistungsService;
    @Mock
    private EndgeraeteService endgeraeteService;
    @Mock
    private SipPeeringPartnerService sipPeeringPartnerService;
    @Mock
    private LoginDataService loginDataService;

    @InjectMocks
    @Spy
    private ReportingServiceImpl cut;

    @BeforeMethod
    public void setupSut() {
        cut = new ReportingServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    private AuftragDaten mockFindAuftragDatenForReporting(Long auftragId, Long auftragNoOrig, Long produktId)
            throws FindException {
        AuftragDaten ad = new AuftragDaten();
        ad.setProdId(produktId);
        ad.setStatusId(AuftragStatus.IN_BETRIEB);
        ad.setAuftragNoOrig(ORDER_NO);
        ad.setAuftragId(auftragId);
        when(auftragService.findAuftragDaten4OrderNoOrigTx(auftragNoOrig)).thenReturn(ImmutableList.of(ad));
        when(produktService.findProdukt(ad.getProdId())).thenReturn(new Produkt());
        return ad;
    }

    public void testGetIpAddress() throws Exception {
        IPAddress ipAddress = new IPAddress();
        ipAddress.setAddress("10.0.0.1");
        ipAddress.setIpType(AddressTypeEnum.IPV4);
        when(ipAddressService.findAssignedIPs4BillingOrder(ORDER_NO)).thenReturn(
                ImmutableList.of(ipAddress));

        mockFindAuftragDatenForReporting(123L, ORDER_NO, 2L);

        String value = cut.getValue(ORDER_NO, "IP_ADDRESS");

        assertEquals(value, ipAddress.getAddress());
    }

    public void testGetNullOnException() throws FindException {
        when(ipAddressService.findAssignedIPs4TechnicalOrder(any(Long.class))).thenThrow(new FindException());
        assertNull(cut.getValue(ORDER_NO, "IP_ADDRESS"));
    }

    public void testGetNullOnMissingKey() {
        assertNull(cut.getValue(ORDER_NO, "HABEDIEEHRE"));
    }

    public void testGetNullOnBlankKey() {
        assertNull(cut.getValue(ORDER_NO, "   "));
    }

    public void testVoIPDNView() throws FindException {
        List<AuftragDaten> auftragDaten = new ArrayList<>();
        List<AuftragVoipDNView> auftragDatenViews1 = new ArrayList<>();
        List<AuftragVoipDNView> auftragDatenViews2 = new ArrayList<>();
        List<AuftragVoIPDN2EGPort> auftragVoIPDN2EGPorts11 = new ArrayList<>();
        List<AuftragVoIPDN2EGPort> auftragVoIPDN2EGPorts12 = new ArrayList<>();
        List<AuftragVoIPDN2EGPort> auftragVoIPDN2EGPorts21 = new ArrayList<>();

        // 2 Auftraege anlegen und der auftragDaten-Liste hinzufügen
        AuftragDaten ad1 = new AuftragDaten();
        ad1.setAuftragId(1L);
        AuftragDaten ad2 = new AuftragDaten();
        ad2.setAuftragId(2L);
        auftragDaten.add(ad1);
        auftragDaten.add(ad2);

        // 2 Listen mit DNs füllen (Liste 1 zwei DNs, Liste 2 eine DN)
        AuftragVoipDNView dnView11 = new AuftragVoipDNView();
        SelectedPortsView spView1 = SelectedPortsView.createNewSelectedPorts(new boolean[] { true, true },
                new Date(), DateTools.getHurricanEndDate());
        dnView11.setTaifunDescription("a1");
        dnView11.setAuftragId(1L);
        dnView11.setDnNoOrig(1L);
        dnView11.addSelectedPort(spView1);
        AuftragVoipDNView dnView12 = new AuftragVoipDNView();
        SelectedPortsView spView2 = SelectedPortsView.createNewSelectedPorts(new boolean[] { false, true },
                new Date(), DateTools.getHurricanEndDate());
        dnView12.setTaifunDescription("a2");
        dnView12.setAuftragId(2L);
        dnView12.setDnNoOrig(2L);
        dnView12.addSelectedPort(spView2);
        AuftragVoipDNView dnView21 = new AuftragVoipDNView();
        SelectedPortsView spView11 = SelectedPortsView.createNewSelectedPorts(new boolean[] { true, false },
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant()), DateTools.getHurricanEndDate());
        dnView21.setTaifunDescription("b1");
        dnView21.setAuftragId(3L);
        dnView21.setDnNoOrig(3L);
        dnView21.addSelectedPort(spView11);
        auftragDatenViews1.add(dnView11);
        auftragDatenViews1.add(dnView12);
        auftragDatenViews2.add(dnView21);

        // Ports erstellen
        EndgeraetPort port1 = new EndgeraetPort();
        port1.setName("Port1");
        port1.setNumber(1);
        EndgeraetPort port2 = new EndgeraetPort();
        port2.setName("Port2");
        port2.setNumber(2);

        // Zuordnung DN2EGPorts erstellen
        AuftragVoIPDN2EGPort dn2EgPort111 = new AuftragVoIPDN2EGPort();
        dn2EgPort111.setEgPort(port1);
        dn2EgPort111.setValidTo(DateTools.getHurricanEndDate());
        AuftragVoIPDN2EGPort dn2EgPort112 = new AuftragVoIPDN2EGPort();
        dn2EgPort112.setEgPort(port2);
        dn2EgPort112.setValidTo(DateTools.getHurricanEndDate());
        AuftragVoIPDN2EGPort dn2EgPort121 = new AuftragVoIPDN2EGPort();
        dn2EgPort121.setEgPort(port2);
        dn2EgPort121.setValidTo(DateTools.getHurricanEndDate());
        AuftragVoIPDN2EGPort dn2EgPort211 = new AuftragVoIPDN2EGPort();
        dn2EgPort211.setEgPort(port1);
        dn2EgPort211.setValidTo(DateTools.getHurricanEndDate());
        auftragVoIPDN2EGPorts11.add(dn2EgPort111);
        auftragVoIPDN2EGPorts11.add(dn2EgPort112);
        auftragVoIPDN2EGPorts12.add(dn2EgPort121);
        auftragVoIPDN2EGPorts21.add(dn2EgPort211);

        // 2 Auftraege und 3 Rufnummern
        AuftragVoIPDN dn11 = new AuftragVoIPDN();
        dn11.setId(1L);
        AuftragVoIPDN dn12 = new AuftragVoIPDN();
        dn12.setId(2L);
        AuftragVoIPDN dn21 = new AuftragVoIPDN();
        dn21.setId(3L);

        when(voIPService.findVoIPDNView(Mockito.eq(1L))).thenReturn(auftragDatenViews1);
        when(voIPService.findVoIPDNView(Mockito.eq(2L))).thenReturn(auftragDatenViews2);
        when(voIPService.findByAuftragIDDN(Mockito.eq(1L), Mockito.eq(1L))).thenReturn(dn11);
        when(voIPService.findByAuftragIDDN(Mockito.eq(1L), Mockito.eq(2L))).thenReturn(dn12);
        when(voIPService.findByAuftragIDDN(Mockito.eq(2L), Mockito.eq(3L))).thenReturn(dn21);
        when(voIPService.findAuftragVoIPDN2EGPorts(Mockito.eq(1L))).thenReturn(auftragVoIPDN2EGPorts11);
        when(voIPService.findAuftragVoIPDN2EGPorts(Mockito.eq(2L))).thenReturn(auftragVoIPDN2EGPorts12);
        when(voIPService.findAuftragVoIPDN2EGPorts(Mockito.eq(3L))).thenReturn(auftragVoIPDN2EGPorts21);

        final String todayAsString = DateTools.formatDate(new Date(), "dd.MM.yyyy");
        final String tomorrowAsString = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String resultDelivered = cut.findVoIPDN2EGPort(auftragDaten);
        String resultExpected = "Port1: a1 (Gültig ab: " + todayAsString + "), b1 (Gültig ab: " + tomorrowAsString
                + ")" + System.lineSeparator() +
                "Port2: a1 (Gültig ab: " + todayAsString + "), a2 (Gültig ab: " + todayAsString + ")";

        assertEquals(resultDelivered, resultExpected);
    }

    @DataProvider
    public Object[][] hvtStandortProvider() {
        return new Object[][] {
                { null, "" },
                { new HVTStandortBuilder().setPersist(false).build(), "" },
                {
                        new HVTStandortBuilder().withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB).setPersist(
                                false).build(), "FTTB" },
                {
                        new HVTStandortBuilder().withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH).setPersist(
                                false).build(), "FTTH"},
                {
                        new HVTStandortBuilder().withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB_H).setPersist(
                                false).build(), "FTTX"}
        };
    }

    @Test(dataProvider = "hvtStandortProvider")
    public void testGetEndstelleATechnologie(HVTStandort hvtStandort, String expected) throws FindException {
        initEndstelleWithTechnologie(hvtStandort, Endstelle.ENDSTELLEN_TYP_A, expected);
        assertEquals(cut.getValueOrException(ORDER_NO, ReportingServiceImpl.ENDSTELLE_A_PREFIX
                + "technologie"), expected);
    }

    @Test(dataProvider = "hvtStandortProvider")
    public void testGetEndstelleBTechnologie(HVTStandort hvtStandort, String expected) throws FindException {
        initEndstelleWithTechnologie(hvtStandort, Endstelle.ENDSTELLEN_TYP_B, expected);
        assertEquals(cut.getValueOrException(ORDER_NO, ReportingServiceImpl.ENDSTELLE_B_PREFIX
                + "technologie"), expected);
    }

    @Test(dataProvider = "hvtStandortProvider")
    public void testGetEndstelleAAnschlussart(HVTStandort hvtStandort, String expected) throws FindException {
        initEndstelleWithAnschlussart(hvtStandort, Endstelle.ENDSTELLEN_TYP_A, expected);

        assertEquals(cut.getValueOrException(ORDER_NO, ReportingServiceImpl.ENDSTELLE_A_PREFIX
                + "anschlussart"), expected);
        verify(hvtService, times(expected.equals("FTTX") ? 0 : 1)).findAnschlussart4HVTStandort(anyLong());
    }

    @Test(dataProvider = "hvtStandortProvider")
    public void testGetEndstelleBAnschlussart(HVTStandort hvtStandort, String expected) throws FindException {
        initEndstelleWithAnschlussart(hvtStandort, Endstelle.ENDSTELLEN_TYP_B, expected);

        assertEquals(cut.getValueOrException(ORDER_NO, ReportingServiceImpl.ENDSTELLE_B_PREFIX
                + "anschlussart"), expected);
        verify(hvtService, times(expected.equals("FTTX") ? 0 : 1)).findAnschlussart4HVTStandort(anyLong());
    }

    @DataProvider
    public Object[][] endstellenProvider() {
        return new Object[][] {
                { Endstelle.ENDSTELLEN_TYP_A, ReportingServiceImpl.ENDSTELLE_A_PREFIX },
                { Endstelle.ENDSTELLEN_TYP_B, ReportingServiceImpl.ENDSTELLE_B_PREFIX } };
    }

    @Test(dataProvider = "endstellenProvider")
    public void testGetEndstelleLeitungsart(String typ, String prefix) throws FindException {
        Endstelle endstelle = initEndstelle(typ);

        Leitungsart leitungsart = new Leitungsart();
        leitungsart.setName("leitungsart123");
        when(physikService.findLeitungsart4ES(endstelle.getId())).thenReturn(leitungsart);

        assertEquals(cut.getValueOrException(ORDER_NO, prefix
                + "leitungsart"), leitungsart.getName());
    }

    @Test(dataProvider = "endstellenProvider")
    public void testGetEndstelleBandbreite(String typ, String prefix) throws FindException {
        final String bandwidthString = "bandwidth123";
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(45762L);

        Endstelle endstelle = initEndstelle(typ);
        when(auftragService.findAuftragDatenByEndstelleTx(endstelle.getId())).thenReturn(
                auftragDaten);
        when(leistungsService.getBandwidthString(auftragDaten.getAuftragId())).thenReturn(bandwidthString);

        assertEquals(cut.getValueOrException(ORDER_NO, prefix
                + "bandbreite"), bandwidthString);
    }

    @DataProvider
    public Object[][] getRaumProvider() {
        return new Object[][] {
                { Endstelle.ENDSTELLEN_TYP_A, "Großer Raum Sonderzeichen ES A: %&/*-" },
                { Endstelle.ENDSTELLEN_TYP_B, "Großer Raum Sonderzeichen ES B: %&/*-" } };
    }

    @Test(dataProvider = "getRaumProvider")
    public void testGetRaum(String typ, String raum) throws Exception {
        initRaumOrGebaeude(ORDER_NO, typ, raum, null);

        String value = cut.getValue(ORDER_NO, ReportingServiceImpl.CPE_PREFIX + typ + ".RAUM");

        assertEquals(value, raum);
    }

    @DataProvider
    public Object[][] getGebaeudeProvider() {
        return new Object[][] {
                { Endstelle.ENDSTELLEN_TYP_A, "ES A, Keller, Raum 0815" },
                { Endstelle.ENDSTELLEN_TYP_B, "ES B, Keller, Raum 0815" } };
    }

    @Test(dataProvider = "getGebaeudeProvider")
    public void testGetGebaeude(String typ, String gebaeude) throws Exception {
        initRaumOrGebaeude(ORDER_NO, typ, null, gebaeude);

        String value = cut.getValue(ORDER_NO, ReportingServiceImpl.CPE_PREFIX + typ + ".GEBAEUDE");

        assertEquals(value, gebaeude);
    }

    private void initEndstelleWithAnschlussart(HVTStandort hvtStandort, String endstellenTyp, String expected)
            throws FindException {
        Long anschlussArtId = 8579L;

        Endstelle endstelle = initEndstelle(hvtStandort, endstellenTyp);
        endstelle.setAnschlussart(anschlussArtId);
        endstelle.setHvtIdStandort(hvtStandort != null ? hvtStandort.getHvtIdStandort() : null);
        when(hvtService.findHVTStandort(endstelle.getHvtIdStandort())).thenReturn(hvtStandort);
        when(hvtService.findAnschlussart4HVTStandort(endstelle.getHvtIdStandort())).thenReturn(anschlussArtId);

        Anschlussart anschlussart = new Anschlussart();
        anschlussart.setAnschlussart(expected);
        when(physikService.findAnschlussart(anschlussArtId)).thenReturn(anschlussart);
    }

    private Endstelle initEndstelleWithTechnologie(HVTStandort hvtStandort, String esTyp, String expected)
            throws FindException {
        Endstelle endstelle = initEndstelle(hvtStandort, esTyp);
        if (hvtStandort != null) {
            Reference reference = new Reference();
            reference.setStrValue(expected);
            when(referenceService.findReference(hvtStandort.getStandortTypRefId())).thenReturn(reference);
        }
        return endstelle;
    }

    private Endstelle initEndstelle(HVTStandort hvtStandort, String esTyp) throws FindException {
        Endstelle endstelle = initEndstelle(esTyp);
        when(hvtService.findHVTStandort(endstelle.getHvtIdStandort())).thenReturn(hvtStandort);
        return endstelle;
    }

    private Endstelle initEndstelle(String esTyp) throws FindException {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(45762L);
        auftragDaten.setStatusId(AuftragStatus.IN_BETRIEB);
        auftragDaten.setProdId(213L);
        when(auftragService.findAuftragDaten4OrderNoOrigTx(ORDER_NO)).thenReturn((ImmutableList.of(auftragDaten)));
        when(produktService.findProdukt(auftragDaten.getProdId())).thenReturn(new Produkt());

        Endstelle endstelle = new Endstelle();
        endstelle.setEndstelleTyp(esTyp);
        when(endstellenService.findEndstellen4Auftrag(auftragDaten.getAuftragId()))
                .thenReturn(ImmutableList.of(endstelle));
        return endstelle;
    }

    private void initRaumOrGebaeude(Long billingAuftragNo, String typ, String raum, String gebaeude)
            throws FindException {
        Produkt produkt = new Produkt();
        produkt.setId(1L);
        Auftrag auftrag = new Auftrag();
        auftrag.setAuftragId(1L);

        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragNoOrig(billingAuftragNo);
        auftragDaten.setAuftragId(auftrag.getId());
        auftragDaten.setProdId(produkt.getId());
        auftragDaten.setAuftragStatusId(AuftragStatus.IN_BETRIEB);

        Endstelle endstelle = new Endstelle();
        endstelle.setId(1L);
        endstelle.setEndstelleTyp(typ);
        EG2Auftrag eg2Auftrag = new EG2Auftrag();
        eg2Auftrag.setAuftragId(auftrag.getId());
        eg2Auftrag.setEndstelleId(endstelle.getId());
        eg2Auftrag.setRaum(raum);
        eg2Auftrag.setGebaeude(gebaeude);

        List<AuftragDaten> ads = new ArrayList<>();
        ads.add(auftragDaten);
        List<EG2Auftrag> eg2as = new ArrayList<>();
        eg2as.add(eg2Auftrag);

        when(endstellenService.findEndstelle4Auftrag(auftrag.getId(), typ)).thenReturn(endstelle);
        when(endgeraeteService.findEGs4Auftrag(auftrag.getId())).thenReturn(eg2as);
        when(auftragService.findAuftragDaten4OrderNoOrigTx(billingAuftragNo)).thenReturn(ads);
        when(produktService.findProdukt(produkt.getId())).thenReturn(produkt);
    }

    @DataProvider
    public Object[][] getMVSInvalidKeyProvider() {
        // @formatter:off
        return new Object[][] {
                { "mvs.enterprise.invalid"},
                { "mvs.site.invalid"},
        };
        // @formatter:on
    }

    @Test(dataProvider = "getMVSInvalidKeyProvider")
    public void testMVSInvalidKey(String key) throws FindException, NoDataFoundException {
        final String EMPTY = "";
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);

        String value = cut.getValue(ORDER_NO, key);

        assertEquals(value, EMPTY);
    }

    @DataProvider
    public Object[][] CPEKennungProvider() {
        EGConfig egConfig1 = new EGConfigBuilder().setPersist(false).withEgUser("Kennung1").build();
        EGConfig egConfig2 = new EGConfigBuilder().setPersist(false).withEgUser("Kennung2").build();
        // @formatter:off
        return new Object[][] {
                { "cpe.a.kennung", null                                , null                  },
                { "cpe.b.kennung", null                                , null                  },
                { "cpe.b.kennung", Collections.emptyList()             , ""                    },
                { "cpe.a.kennung", Collections.emptyList()             , ""                    },
                { "cpe.a.kennung", Collections.singletonList(egConfig1), "Kennung1"            },
                { "cpe.b.kennung", Collections.singletonList(egConfig1), "Kennung1"            },
                { "cpe.a.kennung", Arrays.asList(egConfig1, egConfig2) , "Kennung1 / Kennung2" },
                { "cpe.b.kennung", Arrays.asList(egConfig1, egConfig2) , "Kennung1 / Kennung2" },
        };
        // @formatter:on
    }

    @Test(dataProvider = "CPEKennungProvider")
    public void testCPEKennung(String key, List<EGConfig> egConfigs, String expectedResult) throws FindException {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);
        doReturn(egConfigs).when(cut).findEgConfigsForReporting(any(Long.class), any(String.class));

        String result = cut.getValue(ORDER_NO, key);

        assertTrue(StringUtils.equals(result, expectedResult));
    }

    @DataProvider
    public Object[][] CPEPasswortProvider() {
        EGConfig egConfig1 = new EGConfigBuilder().setPersist(false).withEgPassword("Passwort1").build();
        EGConfig egConfig2 = new EGConfigBuilder().setPersist(false).withEgPassword("Passwort2").build();
        // @formatter:off
        return new Object[][] {
                { "cpe.a.passwort", null                                 , null                  },
                { "cpe.b.passwort", null                                 , null                  },
                { "cpe.b.passwort", Collections.emptyList()              , ""                    },
                { "cpe.a.passwort", Collections.emptyList()              , ""                    },
                { "cpe.a.passwort", Collections.singletonList(egConfig1) , "Passwort1"            },
                { "cpe.b.passwort", Collections.singletonList(egConfig1) , "Passwort1"            },
                { "cpe.a.passwort", Arrays.asList(egConfig1, egConfig2)  , "Passwort1 / Passwort2" },
                { "cpe.b.passwort", Arrays.asList(egConfig1, egConfig2)  , "Passwort1 / Passwort2" },
        };
        // @formatter:on
    }

    @Test(dataProvider = "CPEPasswortProvider")
    public void testCPEPasswort(String key, List<EGConfig> egConfigs, String expectedResult) throws FindException {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);
        doReturn(egConfigs).when(cut).findEgConfigsForReporting(any(Long.class), any(String.class));

        String result = cut.getValue(ORDER_NO, key);

        assertTrue(StringUtils.equals(result, expectedResult));
    }

    @Test
    public void testGetVoipDnBlockLengths() throws Exception {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);
        final AuftragVoipDNView blockDnMitDurchwahllaenge = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "01", "10"))
                .withOnKz("0821")
                .withDnBase("1234")
                .build();

        when(voIPService.findVoIPDNView(1L))
                .thenReturn(ImmutableList.of(blockDnMitDurchwahllaenge));

        final String value = cut.getValue(ORDER_NO, "voipdnblocklength");
        assertThat(value, equalTo("0821/1234 01 - 10."));
    }

    @Test
    public void testGetVoipDnBlockLengthsWithTwoBlocks() throws Exception {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);
        final AuftragVoipDNView blockDnMitDurchwahllaenge = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "01", "10"))
                .withOnKz("0821")
                .withDnBase("1234")
                .build();
        final AuftragVoipDNView blockDnMitDurchwahllaenge2 = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "11", "20"))
                .withOnKz("0821")
                .withDnBase("4567")
                .build();
        final AuftragVoipDNView blockDnOhneDurchwahllaenge = new AuftragVoipDNViewBuilder()
                .build();

        when(voIPService.findVoIPDNView(1L))
                .thenReturn(
                        ImmutableList.of(blockDnMitDurchwahllaenge, blockDnOhneDurchwahllaenge,
                                blockDnMitDurchwahllaenge2)
                );

        final String value = cut.getValue(ORDER_NO, "voipdnblocklength");
        assertThat(value, equalTo("0821/1234 01 - 10." + System.lineSeparator()
                + "0821/4567 11 - 20."));
    }

    @Test
    public void testGetVoipDnSipData() throws Exception {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);

        Reference sipDomain = createSipDomain();

        final LoginDataVoip loginDataVoip =
                new LoginDataVoip(Collections.singletonList(new LoginDataVoipDn("+49821432144", "long-sip-domain.m-net.de", "qwertz")));

        final LoginData loginData = new LoginData(null, loginDataVoip);

        when(loginDataService.getLoginDataVoip(ORDER_NO, true, false, false)).thenReturn(loginData);

        final String value = cut.getValue(ORDER_NO, "voipdnsipdata");
        assertThat(value, equalTo(String.format(
                        "SIP-Benutzername:\t\t\t+49821432144%n" +
                        "SIP-Passwort:\t\t\t\tqwertz%n" +
                        "SIP-Registrar:\t\t\t\tlong-sip-domain.m-net.de%n")
        ));
    }

    private Reference createSipDomain() {
        Reference sipDomain = new Reference(Reference.REF_TYPE_SIP_DOMAIN_TYPE);
        sipDomain.setStrValue("long-sip-domain.m-net.de");
        return sipDomain;
    }

    private AuftragVoipDNView createAuftragVoipDnViewWithPlan(Reference sipDomain) {
        List<VoipDnBlock> blocks = createVoipDnBlocks();
        List<VoipDnPlanView> planViews = Lists.newArrayList(createVoipDnPlanView(blocks));
        return createAuftragVoipDnView(sipDomain, planViews);
    }

    private AuftragVoipDNView createAuftragVoipDnViewWithoutPlan(Reference sipDomain) {
        return createAuftragVoipDnView(sipDomain, Collections.emptyList());
    }

    private AuftragVoipDNView createAuftragVoipDnView(Reference sipDomain, List<VoipDnPlanView> planViews) {
        return new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("0", "40", "49"))
                .withOnKz("0821")
                .withDnBase("4321")
                .withSipDomain(sipDomain)
                .withSipPassword("qwertz")
                .withVoipDnPlanViews(planViews)
                .withGueltigVon(Date.from(ZonedDateTime.now().minusDays(10).toInstant()))
                .withGueltigBis(DateTools.getHurricanEndDate())
                .build();
    }

    private List<VoipDnBlock> createVoipDnBlocks() {
        return ImmutableList.of(
                new VoipDnBlock("44", null, true),
                new VoipDnBlock("400", "439", false),
                new VoipDnBlock("450", "499", false));
    }

    private VoipDnPlanView createVoipDnPlanView(List<VoipDnBlock> blocks) {
        return new VoipDnPlanView("0821", "4321", new VoipDnPlanBuilder()
                .withSipLogin("+49821432144@long-sip-domain.m-net.de")
                .withSipHauptrufnummer("+49821432144")
                .withVoipDnBlocks(blocks)
                .build());
    }

    @Test
    public void testGetSipLoginReturnsNextSipLoginInFuture() throws Exception {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);
        final String sipLoginInFuture = "Future-Sip-Login";
        final AuftragVoipDNView dnWithBlockAndPlan = createAuftragVoipDnViewWithPlan(createSipDomain());
        if(dnWithBlockAndPlan.getLatestVoipDnPlanView() != null){
            dnWithBlockAndPlan.getLatestVoipDnPlanView().setGueltigAb(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant()));
            dnWithBlockAndPlan.getLatestVoipDnPlanView().setSipLogin(sipLoginInFuture);
        }
        final VoipDnPlanView actualPlan = createVoipDnPlanView(createVoipDnBlocks());
        dnWithBlockAndPlan.addVoipDnPlanView(actualPlan);

        when(voIPService.findVoIPDNView(1L))
                .thenReturn(Lists.newArrayList(dnWithBlockAndPlan));

        assertThat(cut.getValue(ORDER_NO, "sip.login"), equalTo(sipLoginInFuture));
    }

    @Test
    public void testGetSipLoginReturnsActualSipLoginIfNoPlanInFutureExists() throws Exception {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);
        final String actualSipLogin = "Actual-Sip-Login";
        final AuftragVoipDNView dnWithBlockAndPlan = createAuftragVoipDnViewWithPlan(createSipDomain());
        if (dnWithBlockAndPlan.getLatestVoipDnPlanView() != null){
            dnWithBlockAndPlan.getLatestVoipDnPlanView().setSipLogin(actualSipLogin);
            dnWithBlockAndPlan.getLatestVoipDnPlanView().setGueltigAb(new Date());
        }
        final VoipDnPlanView planFromPast = createVoipDnPlanView(createVoipDnBlocks());
        planFromPast.setGueltigAb(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(1).toInstant()));
        dnWithBlockAndPlan.addVoipDnPlanView(planFromPast);

        when(voIPService.findVoIPDNView(1L))
                .thenReturn(Lists.newArrayList(dnWithBlockAndPlan));

        assertThat(cut.getValue(ORDER_NO, "sip.login"), equalTo(actualSipLogin + "  gültig ab: "
                + DateTools.formatDate(new Date(), ReportingServiceImpl.REPORTING_DATE_FORMAT)));
    }

    @Test
    public void testGetSipLoginReturnsEmptyStringIfNoFutureOrActualValidPlanExists() throws Exception {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);
        final AuftragVoipDNView dnWithBlockAndWithoutPlan = createAuftragVoipDnViewWithoutPlan(createSipDomain());

        when(voIPService.findVoIPDNView(1L))
                .thenReturn(Lists.newArrayList(dnWithBlockAndWithoutPlan));

        assertThat(cut.getValue(ORDER_NO, "sip.login"), equalTo(""));
    }

    @Test
    public void testGetSipLoginIgnoresNonBlockDns() throws Exception {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);
        final String sipLoginInFuture = "Future-Sip-Login";
        final AuftragVoipDNView dnWithoutBlock = createAuftragVoipDnViewWithPlan(createSipDomain());
        if(dnWithoutBlock.getLatestVoipDnPlanView() != null){
            dnWithoutBlock.getLatestVoipDnPlanView().setGueltigAb(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant()));
            dnWithoutBlock.getLatestVoipDnPlanView().setSipLogin(sipLoginInFuture);
        }
        dnWithoutBlock.setBlock(BlockDNView.NO_BLOCK);

        when(voIPService.findVoIPDNView(1L))
                .thenReturn(Lists.newArrayList(dnWithoutBlock));

        assertThat(cut.getValue(ORDER_NO, "sip.login"), equalTo(""));
    }


    @DataProvider(name = "getVoipDnPlanDP")
    public Object[][] getVoipDnPlanDP() {
        return new Object[][] {
                { true },
                { false }
        };
    }

    @Test(dataProvider = "getVoipDnPlanDP")
    public void testGetVoipDnPlan(boolean planIsValid) throws Exception {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("10", "19", false),
                new VoipDnBlock("20", "29", false));

        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        final Date date = sdf.parse("02.01.2014");

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(date)
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withVoipDnPlanViews(ImmutableList.of(planView))
                .withGueltigBis((planIsValid)
                        ? DateTools.getHurricanEndDate()
                        : Date.from(ZonedDateTime.now().minusDays(1).toInstant()))
                .setPersist(false)
                .build();

        when(voIPService.findVoIPDNView(1L)).thenReturn(ImmutableList.of(dnView));

        final String value = cut.getValue(ORDER_NO, "voipdnplan");
        System.out.println(value);

        if (planIsValid) {
            assertThat(value, equalTo(
                    "gültig ab 02.01.2014" + System.lineSeparator() +
                            "" + System.lineSeparator() +
                            "Rufnummer                Durchwahlbereich  " + System.lineSeparator() +
                            "-------------------------------------------" + System.lineSeparator() +
                            "012345 67890             0                 " + System.lineSeparator() +
                            "012345 67890             10-19             " + System.lineSeparator() +
                            "012345 67890             20-29             " + System.lineSeparator()
            ));
        }
        else {
            assertTrue(StringUtils.isBlank(value));
        }
    }

    @Test
    public void testGetPeeringPartnerReturnsNextPeeringPartnerInFuture() throws Exception {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);

        final SipPeeringPartner sppActual = new SipPeeringPartnerBuilder()
                .withRandomId()
                .withName("asdf")
                .build();
        final SipPeeringPartner sppNextInFuture = new SipPeeringPartnerBuilder()
                .withRandomId()
                .withName("qwer")
                .build();
        final Auftrag2PeeringPartner a2pNextInFuture = new Auftrag2PeeringPartnerBuilder()
                .withRandomId()
                .withGueltigVon(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant()))
                .withGueltigBis(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusDays(2).toInstant()))
                .withPeeringPartnerId(sppNextInFuture.getId())
                .build();
        final Auftrag2PeeringPartner a2pNextLaterFuture = new Auftrag2PeeringPartnerBuilder()
                .withRandomId()
                .withGueltigVon(a2pNextInFuture.getGueltigBis())
                .withGueltigBis(DateTools.getHurricanEndDate())
                .withPeeringPartnerId(sppActual.getId())
                .build();
        final Auftrag2PeeringPartner a2pActual = new Auftrag2PeeringPartnerBuilder()
                .withRandomId()
                .withGueltigVon(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(1).toInstant()))
                .withGueltigBis(a2pNextInFuture.getGueltigVon())
                .withPeeringPartnerId(sppActual.getId())
                .build();

        when(sipPeeringPartnerService.findAuftragPeeringPartners(1L))
                .thenReturn(Lists.newArrayList(a2pActual, a2pNextInFuture, a2pNextLaterFuture));
        when(sipPeeringPartnerService.findPeeringPartnerById(sppActual.getId())).thenReturn(sppActual);
        when(sipPeeringPartnerService.findPeeringPartnerById(sppNextInFuture.getId())).thenReturn(sppNextInFuture);

        final String result = cut.getValue(ORDER_NO, "sip.peering.partner");

        assertThat(result,
                equalTo(sppNextInFuture.getName() +
                        "  gültig ab: " +
                        DateTools.formatDate(a2pNextInFuture.getGueltigVon(), ReportingServiceImpl.REPORTING_DATE_FORMAT)));
    }

    @Test
    public void testGetPeeringPartnerReturnsActualPeeringPartnerWhenNoPeeringPartnerInFutureExists() throws Exception {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);

        final SipPeeringPartner sppActual = new SipPeeringPartnerBuilder()
                .withRandomId()
                .withName("asdf")
                .build();
        final SipPeeringPartner sppInPast = new SipPeeringPartnerBuilder()
                .withRandomId()
                .withName("qwer")
                .build();

        final Auftrag2PeeringPartner a2pActual = new Auftrag2PeeringPartnerBuilder()
                .withRandomId()
                .withGueltigVon(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withGueltigBis(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant()))
                .withPeeringPartnerId(sppActual.getId())
                .build();
        final Auftrag2PeeringPartner a2pInPast = new Auftrag2PeeringPartnerBuilder()
                .withRandomId()
                .withGueltigVon(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(2).toInstant()))
                .withGueltigBis(a2pActual.getGueltigVon())
                .withPeeringPartnerId(sppInPast.getId())
                .build();

        when(sipPeeringPartnerService.findAuftragPeeringPartners(1L))
                .thenReturn(Lists.newArrayList(a2pActual, a2pInPast));
        when(sipPeeringPartnerService.findPeeringPartnerById(sppActual.getId())).thenReturn(sppActual);
        when(sipPeeringPartnerService.findPeeringPartnerById(sppInPast.getId())).thenReturn(sppInPast);

        final String result = cut.getValue(ORDER_NO, "sip.peering.partner");

        assertThat(result,
                equalTo(sppActual.getName() +
                        "  gültig ab: " +
                        DateTools.formatDate(a2pActual.getGueltigVon(), ReportingServiceImpl.REPORTING_DATE_FORMAT)));
    }

    @Test
    public void testGetPeeringPartnerReturnsEmptyStringWhenNoActualAndNoPeeringPartnerInFutureExist() throws Exception {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);

        final SipPeeringPartner sppInPast = new SipPeeringPartnerBuilder()
                .withRandomId()
                .withName("qwer")
                .build();

        final Auftrag2PeeringPartner a2pInPast = new Auftrag2PeeringPartnerBuilder()
                .withRandomId()
                .withGueltigVon(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(2).toInstant()))
                .withGueltigBis(new Date())
                .withPeeringPartnerId(sppInPast.getId())
                .build();

        when(sipPeeringPartnerService.findAuftragPeeringPartners(1L))
                .thenReturn(Lists.newArrayList(a2pInPast));
        when(sipPeeringPartnerService.findPeeringPartnerById(sppInPast.getId())).thenReturn(sppInPast);

        final String result = cut.getValue(ORDER_NO, "sip.peering.partner");

        assertThat(result, equalTo(""));
    }

    @Test
    public void testGetReportingKeyLoginDataInternetOK() throws Exception {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);
        final LoginDataInternet loginDataInternet = new LoginDataInternet("pppUser","@mdsl.mnet-online.de","pppPassword",  IpMode.DUAL_STACK, 0, 5, 40, null, "aftr.prod.m-online.net", 1, 32 );
        final LoginData loginData = new LoginData(loginDataInternet,null);

        String resultExpected = "PPP-User:\tpppUser@mdsl.mnet-online.de" + SystemUtils.LINE_SEPARATOR
                + "PPP-Passwort:\tpppPassword" + SystemUtils.LINE_SEPARATOR
                + "IP Protokoll:\tIPv6 DualStack" + SystemUtils.LINE_SEPARATOR
                + "PCP Wert für Daten (p-bit):\t0" + SystemUtils.LINE_SEPARATOR
                + "PCP Wert für VoIP (p-bit):\t5" + SystemUtils.LINE_SEPARATOR
                + "VLAN-ID Daten:\t40" + SystemUtils.LINE_SEPARATOR
                + "AFTR Adresse:\taftr.prod.m-online.net" + SystemUtils.LINE_SEPARATOR
                + "ATM-Parameter VPI:\t1" + SystemUtils.LINE_SEPARATOR
                + "ATM-Parameter VCI:\t32" + SystemUtils.LINE_SEPARATOR;
        when(loginDataService.getLoginData(ORDER_NO,true,false)).thenReturn(loginData);

        final String result = cut.getValue(ORDER_NO, "logindata.internet");
        assertEquals(result, resultExpected);
    }

    @Test
    public void testGetReportingKeyLoginDataVoipOK() throws Exception {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);
        final LoginDataVoip loginDataVoip =
                new LoginDataVoip(Collections.singletonList(new LoginDataVoipDn("+49821999999", "biz.m-call.de", "x5faeg")));
        final LoginData loginData = new LoginData(null, loginDataVoip);

        String resultExpected = "SIP-Benutzername:\t\t\t+49821999999" + SystemUtils.LINE_SEPARATOR
                + "SIP-Passwort:\t\t\t\tx5faeg" + SystemUtils.LINE_SEPARATOR
                + "SIP-Registrar:\t\t\t\tbiz.m-call.de" + SystemUtils.LINE_SEPARATOR;

        when(loginDataService.getLoginDataVoip(ORDER_NO, true, true, true)).thenReturn(loginData);
        final String result = cut.getValue(ORDER_NO, "logindata.voip");
        assertEquals(result, resultExpected);
    }

    @Test
    public void testGetReportingKeyLoginDataVoipOK_Future() throws Exception {
        mockFindAuftragDatenForReporting(1L, ORDER_NO, 2L);
        final LoginDataVoip loginDataVoip =
                new LoginDataVoip(Arrays.asList(new LoginDataVoipDn("+498219999991", "biz.m-call.de", "x5faeg"),
                        new LoginDataVoipDn("+498219999992", "biz.m-call.de", "x5faeg", LocalDate.now().plusDays(1))));
        final LoginData loginData = new LoginData(null, loginDataVoip);

        final String resultExpected = "SIP-Benutzername:\t\t\t+498219999991" + SystemUtils.LINE_SEPARATOR
                + "SIP-Passwort:\t\t\t\tx5faeg" + SystemUtils.LINE_SEPARATOR
                + "SIP-Registrar:\t\t\t\tbiz.m-call.de" + SystemUtils.LINE_SEPARATOR
                + SystemUtils.LINE_SEPARATOR
                + "gültig ab " + LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(ReportingServiceImpl.REPORTING_DATE_FORMAT)) + SystemUtils.LINE_SEPARATOR
                + SystemUtils.LINE_SEPARATOR
                + "SIP-Benutzername:\t\t\t+498219999992" + SystemUtils.LINE_SEPARATOR
                + "SIP-Passwort:\t\t\t\tx5faeg" + SystemUtils.LINE_SEPARATOR
                + "SIP-Registrar:\t\t\t\tbiz.m-call.de" + SystemUtils.LINE_SEPARATOR;

        when(loginDataService.getLoginDataVoip(ORDER_NO, true, true, true)).thenReturn(loginData);
        final String result = cut.getValue(ORDER_NO, "logindata.voip");
        assertEquals(result, resultExpected);
    }
}
