/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 28.06.2010 14:03:47
  */

package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.dao.cc.AuftragAktionDAO;
import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBuilder;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragAktionBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EG2AuftragBuilder;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGConfigBuilder;
import de.augustakom.hurrican.model.cc.EGTypeBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.GeoIdSource;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HWSwitchBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.SdslNdraht;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.augustakom.hurrican.service.cc.utils.CalculatedSwitch4VoipAuftrag;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class CCAuftragServiceImplTest extends BaseTest {

    @Spy
    @InjectMocks
    CCAuftragServiceImpl sut;

    @Mock
    AuftragDatenDAO auftragDatenDAO;
    @Mock
    CCAuftragDAO auftragDAO;
    @Mock
    AuftragAktionDAO auftragAktionDAO;
    @Mock
    AuftragTechnikDAO auftragTechnikDAO;
    @Mock
    CCLeistungsService ccLeistungsService;
    @Mock
    EndgeraeteService endgeraeteService;
    @Mock
    EndstellenService endstellenService;
    @Mock
    KundenService kundenService;
    @Mock
    ProduktService produktService;
    @Mock
    BillingAuftragService billingAuftragService;
    @Mock
    HVTService hvtService;
    @Mock
    AvailabilityService availabilityService;
    @Mock
    RangierungsService rangierungsService;
    @Mock
    HWService hwService;
    @Mock
    VoIPService voIPService;
    @Mock
    CPSService cpsService;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private AuftragDaten falschesProduktInBetrieb() {
        return aDatenSdslNdraht(-1L, AuftragStatus.IN_BETRIEB);
    }

    private AuftragDaten nDrahtOptionRichtigerStatusUndProdukt() {
        return aDatenSdslNdraht(Produkt.PROD_ID_SDSL_N_DRAHT_OPTION, AuftragStatus.IN_BETRIEB);
    }

    private AuftragDaten nDrahtOptionFalscherStatus() {
        return aDatenSdslNdraht(Produkt.PROD_ID_SDSL_N_DRAHT_OPTION, AuftragStatus.AUFTRAG_GEKUENDIGT);
    }

    private AuftragDaten aDatenSdslNdraht(final long prodId, final long statusId) {
        return new AuftragDatenBuilder()
                .withRandomId()
                .withProdId(prodId)
                .withStatusId(statusId)
                .build();

    }

    @DataProvider
    Object[][] testCheckAnzahlNdrahtOptionAuftraegeDataProvider() {
        // @formatter:off
        return new Object[][] {
                { null, Collections.emptyList(), CCAuftragService.CheckAnzNdrahtResult.NO_NDRAHT_CONFIG},
                { SdslNdraht.ZWEI, Collections.emptyList(), CCAuftragService.CheckAnzNdrahtResult.AS_EXPECTED},
                { SdslNdraht.VIER, Collections.emptyList(), CCAuftragService.CheckAnzNdrahtResult.LESS_THAN_EXPECTED},
                { SdslNdraht.VIER, Lists.newArrayList(falschesProduktInBetrieb(), nDrahtOptionFalscherStatus()),
                        CCAuftragService.CheckAnzNdrahtResult.LESS_THAN_EXPECTED},
                { SdslNdraht.VIER, Lists.newArrayList(nDrahtOptionRichtigerStatusUndProdukt()),
                        CCAuftragService.CheckAnzNdrahtResult.AS_EXPECTED},
                { SdslNdraht.VIER, Lists.newArrayList(nDrahtOptionRichtigerStatusUndProdukt(), nDrahtOptionFalscherStatus()),
                        CCAuftragService.CheckAnzNdrahtResult.AS_EXPECTED},
                { SdslNdraht.VIER, Lists.newArrayList(nDrahtOptionRichtigerStatusUndProdukt(), nDrahtOptionRichtigerStatusUndProdukt()),
                        CCAuftragService.CheckAnzNdrahtResult.MORE_THAN_EXPECTED},
                { SdslNdraht.ACHT, Lists.newArrayList(nDrahtOptionRichtigerStatusUndProdukt(), nDrahtOptionRichtigerStatusUndProdukt(),
                        nDrahtOptionRichtigerStatusUndProdukt()),
                        CCAuftragService.CheckAnzNdrahtResult.AS_EXPECTED},
                { SdslNdraht.OPTIONAL_BONDING, Lists.newArrayList(), CCAuftragService.CheckAnzNdrahtResult.AS_EXPECTED},
                { SdslNdraht.OPTIONAL_BONDING, Lists.newArrayList(nDrahtOptionRichtigerStatusUndProdukt(), nDrahtOptionRichtigerStatusUndProdukt()),
                        CCAuftragService.CheckAnzNdrahtResult.AS_EXPECTED},
        };
        // @formatter:on
    }

    @Test(dataProvider = "testCheckAnzahlNdrahtOptionAuftraegeDataProvider")
    public void testCheckAnzahlNdrahtOptionAuftraege(final SdslNdraht sdslNdraht,
            final List<AuftragDaten> nDrahtOptions,
            final CCAuftragService.CheckAnzNdrahtResult expectedResult) throws Exception {
        final AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withRandomId()
                .withRandomAuftragId()
                .withAuftragNoOrig(987612234L)
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .withProdId(99999999L)
                .build();
        final Produkt produkt = new ProduktBuilder()
                .withRandomId()
                .withSdslNdraht(sdslNdraht)
                .build();

        when(auftragDatenDAO.findByAuftragId(auftragDaten.getAuftragId())).thenReturn(auftragDaten);
        when(produktService.findProdukt(auftragDaten.getProdId())).thenReturn(produkt);
        if (!nDrahtOptions.isEmpty()) {
            when(auftragDatenDAO.findByOrderNoOrig(auftragDaten.getAuftragNoOrig(), false))
                    .thenReturn(Lists.asList(auftragDaten, nDrahtOptions.toArray(new AuftragDaten[nDrahtOptions.size()])));
        }

        final Pair<CCAuftragService.CheckAnzNdrahtResult, Collection<AuftragDaten>> result =
                sut.checkAnzahlNdrahtOptionAuftraege(auftragDaten.getAuftragId());
        assertThat(result.getFirst(), equalTo(expectedResult));
        if (result.getFirst().equals(CCAuftragService.CheckAnzNdrahtResult.NO_NDRAHT_CONFIG)) {
            assertThat(result.getSecond(), Matchers.<AuftragDaten>empty());
        }
        else {
            assertThat(result.getSecond(), not(Matchers.<AuftragDaten>empty()));
        }
    }

    public void testChangeCustomerIdOnAuftrag() throws Exception {
        AuftragDaten ad1 = new AuftragDaten();
        ad1.setAuftragId(23L);
        ad1.setAuftragNoOrig(1337L);
        AuftragDaten ad2 = new AuftragDaten();
        ad2.setAuftragId(42L);
        ad2.setAuftragNoOrig(1337L);

        Auftrag a1 = new Auftrag();
        a1.setKundeNo(2342L);
        Auftrag a2 = new Auftrag();
        a2.setKundeNo(2342L);

        Kunde kunde = new Kunde();
        kunde.setKundeNo(4223L);

        when(auftragDatenDAO.findByOrderNoOrig(1337L, false)).thenReturn(Arrays.asList(ad1, ad2));
        when(auftragDAO.findById(23L, Auftrag.class)).thenReturn(a1);
        when(auftragDAO.findById(42L, Auftrag.class)).thenReturn(a2);
        when(kundenService.findKunde(4223L)).thenReturn(kunde);

        sut.changeCustomerIdOnAuftrag(1337L, 2342L, 4223L);

        assertEquals(a1.getKundeNo(), Long.valueOf(4223L));
        assertEquals(a2.getKundeNo(), Long.valueOf(4223L));
    }

    @Test(expectedExceptions = StoreException.class,
            expectedExceptionsMessageRegExp = "Kein Kunde für Kundennummer 4223 gefunden!")
    public void testChangeCustomerIdOnAuftragCustomerNotFound() throws Exception {
        when(kundenService.findKunde(4223L)).thenReturn(null);
        sut.changeCustomerIdOnAuftrag(1337L, 2342L, 4223L);
    }

    @Test(expectedExceptions = StoreException.class)
    public void testChangeCustomerIdOnAuftragAuftragNotFound() throws Exception {
        AuftragDaten ad = new AuftragDaten();
        ad.setAuftragId(23L);
        ad.setAuftragNoOrig(1337L);
        Auftrag a = new Auftrag();
        a.setAuftragId(23L);
        a.setKundeNo(1231L);

        Kunde kunde = new Kunde();
        kunde.setKundeNo(4223L);

        when(auftragDatenDAO.findByOrderNoOrig(1337L, false)).thenReturn(Collections.singletonList(ad));
        when(auftragDAO.findById(23L, Auftrag.class)).thenReturn(a);
        when(kundenService.findKunde(4223L)).thenReturn(kunde);

        sut.changeCustomerIdOnAuftrag(1337L, 2342L, 4223L);
    }

    public void getSwitchKennung4Auftrag_InproperAuftragId() {
        assertNull(sut.getSwitchKennung4Auftrag(null));
    }

    public void getSwitchKennung4Auftrag_NoEndstelleFound() throws FindException {
        final Long auftragId = 2000L;
        // @formatter:off
        doReturn(null).when(sut).getSwitchFromProdukt(auftragId);
        when(endstellenService.findEndstelle4Auftrag(auftragId, Mockito.eq(Mockito.anyString())))
            .thenReturn(null);
        // @formatter:on
        assertNull(sut.getSwitchKennung4Auftrag(auftragId));
    }

    public void getSwitchKennung4Auftrag_FoundPortWithSwitchForEndstelle() throws FindException {
        final Long auftragId = 2000L;
        Endstelle endstelle = new Endstelle();
        final String switchKennung = "TESTKENNUNG";
        HWSwitch hwSwitch = new HWSwitchBuilder().setPersist(false)
                .withName(switchKennung)
                .build();
        Equipment port = new EquipmentBuilder().withHwSwitch(hwSwitch).setPersist(false).build();
        // @formatter:off
        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B))
            .thenReturn(endstelle);
        when(ccLeistungsService.hasVoipLeistungFromNowOn(auftragId)).thenReturn(false);
        doReturn(null).when(sut).getSwitchFromProdukt(auftragId);
        doReturn(port).when(sut).getAdditionalPortAndIfNotAvailableDefault(endstelle);
        // @formatter:on
        HWSwitch result = sut.getSwitchKennung4Auftrag(auftragId);
        assertNotNull(result);
        assertEquals(result.getName(), switchKennung);
    }

    public void calculateSwitch4VoipAuftrag_WithProduktSwitchIdSet() throws FindException {
        final Long auftragId = 2000L;
        final Long produktId = 1L;
        final String switchKennung = "TESTKENNUNG";
        HWSwitch hwSwitch = new HWSwitchBuilder().withName(switchKennung).setPersist(false).build();
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withProdId(produktId).setPersist(false).build();
        Produkt produkt = new ProduktBuilder().withHwSwitch(hwSwitch).setPersist(false).build();

        when(ccLeistungsService.hasVoipLeistungFromNowOn(auftragId)).thenReturn(true);
        doReturn(auftragDaten).when(sut).findAuftragDatenByAuftragIdTx(auftragId);
        when(produktService.findProdukt(produktId)).thenReturn(produkt);

        CalculatedSwitch4VoipAuftrag result = sut.calculateSwitch4VoipAuftrag(auftragId).getLeft();
        assertNotNull(result);
        assertEquals(result.calculatedHwSwitch.getName(), switchKennung);
    }

    public void calculateSwitch4VoipAuftrag_WithSwitchOnEg() throws FindException {
        final Long auftragId = 2000L;
        final Long produktId = 1L;
        final Long eg2AuftragId = 4711L;
        final String switchKennung = "TESTKENNUNG";
        HWSwitch hwSwitch = new HWSwitchBuilder().withName(switchKennung).setPersist(false).build();
        HWSwitch prodHwSwitch = new HWSwitchBuilder().withName("PRODKENNUNG").setPersist(false).build();
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withProdId(produktId).setPersist(false).build();
        Produkt produkt = new ProduktBuilder().withHwSwitch(prodHwSwitch).setPersist(false).build();
        final EG2Auftrag eg2Auftrag = new EG2AuftragBuilder().withId(eg2AuftragId).setPersist(false).build();
        final EGConfig egConfig = new EGConfigBuilder().withEGTypeBuilder(
                new EGTypeBuilder().withOrderedCertifiedSwitches(Collections.singletonList(hwSwitch))).setPersist(false).build();

        doReturn(auftragDaten).when(sut).findAuftragDatenByAuftragIdTx(auftragId);
        when(ccLeistungsService.hasVoipLeistungFromNowOn(auftragId)).thenReturn(true);
        when(produktService.findProdukt(produktId)).thenReturn(produkt);
        when(endgeraeteService.findEGs4Auftrag(eq(auftragId))).thenReturn(Collections.singletonList(eg2Auftrag));
        when(endgeraeteService.findEGConfig(eq(eg2AuftragId))).thenReturn(egConfig);

        CalculatedSwitch4VoipAuftrag result = sut.calculateSwitch4VoipAuftrag(auftragId).getLeft();
        assertNotNull(result);
        assertEquals(result.calculatedHwSwitch.getName(), switchKennung);
    }

    @DataProvider
    public Object[][] dataProviderIsAutomationPossible() {
        AuftragDaten auftragDaten = new AuftragDaten();
        Long prodId = 1L;
        Long auftragId = 1337L;
        Long auftragNoOrig = 5678L;
        auftragDaten.setProdId(prodId);
        auftragDaten.setAuftragId(auftragId);
        auftragDaten.setAuftragNoOrig(auftragNoOrig);

        Produkt produktAutomation = new Produkt();
        produktAutomation.setAutomationPossible(true);
        Produkt produktNoAutomation = new Produkt();
        produktNoAutomation.setAutomationPossible(false);

        AuftragTechnik auftragTechVPN = new AuftragTechnik();
        auftragTechVPN.setVpnId(425L);
        AuftragTechnik auftragTechNoVPN = new AuftragTechnik();

        BAuftrag bAuftragNew = new BAuftrag();
        bAuftragNew.setHistStatus(BillingConstants.HIST_STATUS_NEU);
        bAuftragNew.setHistCnt(0);
        BAuftrag bAuftragNotNew1 = new BAuftrag();
        bAuftragNotNew1.setHistStatus(BillingConstants.HIST_STATUS_NEU);
        bAuftragNotNew1.setHistCnt(1);
        BAuftrag bAuftragNotNew2 = new BAuftrag();
        bAuftragNotNew2.setHistStatus(BillingConstants.HIST_STATUS_AKT);
        bAuftragNotNew2.setHistCnt(0);
        BAuftrag bAuftragKuendigung = new BAuftrag();
        bAuftragKuendigung.setAtyp(BillingConstants.ATYP_KUEND);

        // @formatter:off
        return new Object[][] {
                {null, produktAutomation, auftragTechVPN, bAuftragNew, CBVorgang.TYP_NEU, false },
                {auftragDaten,  produktAutomation,      auftragTechNoVPN,   bAuftragNew,        CBVorgang.TYP_NEU,              true },
                {auftragDaten,  produktNoAutomation,    auftragTechNoVPN,   bAuftragNew,        CBVorgang.TYP_NEU,              false },
                {auftragDaten,  produktAutomation,      auftragTechVPN,     bAuftragNew,        CBVorgang.TYP_NEU,              false },
                {auftragDaten,  produktAutomation,      auftragTechNoVPN,   bAuftragNotNew1,    CBVorgang.TYP_NEU,              false },
                {auftragDaten,  produktAutomation,      auftragTechNoVPN,   bAuftragNotNew2,    CBVorgang.TYP_NEU,              false },
                {auftragDaten,  produktAutomation,      auftragTechNoVPN,   bAuftragNew,        CBVorgang.TYP_ANBIETERWECHSEL,  true },
                {auftragDaten,  produktAutomation,      auftragTechNoVPN,   bAuftragNew,        CBVorgang.TYP_PORTWECHSEL,      true },
                {auftragDaten,  produktAutomation,      auftragTechNoVPN,   bAuftragNew,        CBVorgang.TYP_KUENDIGUNG,       false },
                {auftragDaten,  produktAutomation,      auftragTechNoVPN,   bAuftragKuendigung, CBVorgang.TYP_KUENDIGUNG,       true }
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderIsAutomationPossible")
    public void testIsAutomationPossible(AuftragDaten auftragDaten, Produkt prod, AuftragTechnik auftragTechnik,
            BAuftrag bAuftrag, Long cbVorgangTyp,
            boolean expectedResult) throws Exception {
        Long prodId = auftragDaten == null ? null : auftragDaten.getProdId();
        Long auftragId = auftragDaten == null ? null : auftragDaten.getAuftragId();
        Long auftragNoOrig = auftragDaten == null ? null : auftragDaten.getAuftragNoOrig();
        when(produktService.findProdukt(prodId)).thenReturn(prod);
        when(auftragTechnikDAO.findByAuftragId(auftragId)).thenReturn(auftragTechnik);
        when(billingAuftragService.findAuftrag(auftragNoOrig)).thenReturn(bAuftrag);
        boolean result = sut.isAutomationPossible(auftragDaten, cbVorgangTyp);
        assertEquals(result, expectedResult);
    }

    public void moveModifyPortAktion() {
        AuftragBuilder baseOrderBuilder = createOrder();
        AuftragBuilder newOrderBuilder = createOrder();

        AuftragAktion aktion = new AuftragAktionBuilder().withAuftragBuilder(baseOrderBuilder).setPersist(false)
                .build();
        sut.moveModifyPortAktion(aktion, newOrderBuilder.get().getId());
        assertThat(aktion.getAuftragId(), equalTo(newOrderBuilder.get().getId()));
        assertThat(aktion.getPreviousAuftragId(), equalTo(baseOrderBuilder.get().getId()));
        verify(auftragAktionDAO).store(aktion);
    }

    private AuftragBuilder createOrder() {
        return new AuftragBuilder().withRandomId().setPersist(false);
    }

    @DataProvider
    public Object[][] dataProviderFindOnkzAsbFromHvtStandort() {
        // @formatter:off
        return new Object[][] {
                {7   },
                {null}  // Fuer ISDN MSN Auftraege, die an einem technischen Standort realisiert sind, welcher rein
                        // durch M-Net betriebenen ist, gibt es keinen ASB.
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderFindOnkzAsbFromHvtStandort")
    public void testFindOnkzAsbFromHvtStandort(Integer asb) throws Exception {
        final Produkt produkt = new ProduktBuilder().withGeoIdSource(GeoIdSource.HVT).withProduktGruppeId(1234L)
                .build();
        final HVTGruppeBuilder hvtGruppeBuilder = new HVTGruppeBuilder().withRandomId().withOnkz("0821");
        final HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withRandomId()
                .withHvtGruppeBuilder(hvtGruppeBuilder)
                .withAsb(asb);
        final Endstelle esB = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().withRandomId())
                .withHvtStandortBuilder(hvtStandortBuilder).build();
        final AuftragDaten auftragDaten = newAuftragDatenWithProdiId(987654321L);

        mock4FindOnkzAsb(produkt, esB, auftragDaten);
        when(hvtService.findHVTStandort(esB.getHvtIdStandort())).thenReturn(hvtStandortBuilder.get());
        when(hvtService.findHVTGruppeById(hvtStandortBuilder.get().getHvtGruppeId()))
                .thenReturn(hvtGruppeBuilder.get());

        final Pair<String, Integer> result = sut.findOnkzAsb4Auftrag(auftragDaten.getAuftragId());

        verify(hvtService).findHVTStandort(esB.getHvtIdStandort());
        verify(hvtService).findHVTGruppeById(hvtStandortBuilder.get().getHvtGruppeId());

        assertThat(result.getFirst(), equalTo(hvtGruppeBuilder.get().getOnkz()));
        assertThat(result.getSecond(), equalTo(asb));
    }

    private void mock4FindOnkzAsb(final Produkt produkt, final Endstelle esB, final AuftragDaten auftragDaten)
            throws FindException {
        when(auftragDatenDAO.findByAuftragId(auftragDaten.getAuftragId())).thenReturn(auftragDaten);
        when(produktService.findProdukt(auftragDaten.getProdId())).thenReturn(produkt);
        when(endstellenService.findEndstelle4Auftrag(auftragDaten.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B))
                .thenReturn(esB);
    }

    private AuftragDaten newAuftragDatenWithProdiId(long prodId) {
        return new AuftragDatenBuilder()
                .withRandomAuftragId()
                .withRandomId()
                .withProdId(prodId)
                .build();
    }

    public void testFindOnkzAsbFromGeoIdCache() throws Exception {
        final Produkt produkt = new ProduktBuilder()
                .withGeoIdSource(GeoIdSource.GEO_ID)
                .withProduktGruppeId(1234L)
                .withAutoHvtZuordnung(false)
                .build();

        final GeoIdBuilder geoIdBuilder = new GeoIdBuilder().withId(1234L).withAsb("3").withOnkz("0823");
        final Endstelle esB = new EndstelleBuilder()
                .withRandomId()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().withRandomId())
                .withHvtStandortBuilder(new HVTStandortBuilder().withRandomId())
                .withGeoIdBuilder(geoIdBuilder)
                .build();
        final AuftragDaten auftragDaten = newAuftragDatenWithProdiId(123456789L);

        mock4FindOnkzAsb(produkt, esB, auftragDaten);
        when(availabilityService.findGeoId(esB.getGeoId())).thenReturn(geoIdBuilder.get());

        final Pair<String, Integer> result = sut.findOnkzAsb4Auftrag(auftragDaten.getAuftragId());

        verify(availabilityService).findGeoId(esB.getGeoId());

        assertOnkzAsb(geoIdBuilder.get().getOnkz(), Integer.valueOf(geoIdBuilder.get().getAsb()), result);
    }

    private void assertOnkzAsb(final String onkz, final int asb, final Pair<String, Integer> result) {
        assertThat(result.getFirst(), equalTo(onkz));
        assertThat(result.getSecond(), equalTo(asb));
    }

    public void testFindOnkzAsbNoResult() throws Exception {
        final Produkt produkt = new ProduktBuilder()
                .withGeoIdSource(GeoIdSource.HVT)
                .withProduktGruppeId(1234L)
                .withAutoHvtZuordnung(false)
                .build();

        final GeoIdBuilder geoIdBuilder = new GeoIdBuilder().withId(1234L).withAsb("3").withOnkz("0823");
        final Endstelle esB = new EndstelleBuilder()
                .withRandomId()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().withRandomId())
                .withHvtStandortBuilder(new HVTStandortBuilder().withRandomId())
                .withGeoIdBuilder(geoIdBuilder)
                .build();
        final AuftragDaten auftragDaten = newAuftragDatenWithProdiId(123456789L);

        mock4FindOnkzAsb(produkt, esB, auftragDaten);
        when(availabilityService.findGeoId(esB.getGeoId())).thenReturn(geoIdBuilder.get());

        final Pair<String, Integer> result = sut.findOnkzAsb4Auftrag(auftragDaten.getAuftragId());

        verify(availabilityService, never()).findGeoId(esB.getGeoId());

        assertThat(result, equalTo(null));
    }

    @DataProvider
    Object[][] dataProviderCheckAgsn4Auftrag() {
        // @formatter:off
        return new Object[][] {
                { null, null, null, true },
                { 1L, null, null, true },
                { 1L, new GeoId(), null, true },
                { 1L, new GeoId(), "ags_n", false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderCheckAgsn4Auftrag")
    public void testCheckAgsn4Auftrag(Long id, GeoId geoId, String agsn, boolean expectException) throws FindException {
        Endstelle endstelleB = new Endstelle();
        endstelleB.setGeoId(id);
        if (geoId != null) {
            geoId.setAgsn(agsn);
        }
        when(endstellenService.findEndstelle4Auftrag(eq(1L), eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(endstelleB);
        when(availabilityService.findGeoId(eq(id))).thenReturn(geoId);
        try {
            sut.checkAgsn4Auftrag(1L);
        }
        catch (FindException e) {
            if (!expectException) {
                fail();
            }
            return;
        }
        if (expectException) {
            fail();
        }
    }

    @Test
    public void testCreateHauptaufragsBuendel() throws Exception {
        BAuftrag baAuftrag = new BAuftragBuilder().withAuftragNoOrig(101L).withHauptAuftragNo(100L).build();
        AuftragDaten adMain = new AuftragDatenBuilder().withAuftragId(7100L).build();
        AuftragDaten ad = new AuftragDatenBuilder().withAuftragId(7101L).build();
        when(sut.findAllAuftragDaten4OrderNoOrig(baAuftrag.getHauptAuftragNo())).thenReturn(Collections.singletonList(adMain));

        //check create new bundle with non predefined bundle nr
        sut.createBillingHauptauftragsBuendel(ad, baAuftrag);
        verify(sut).saveAuftragDaten(ad, false);
        verify(sut).saveAuftragDaten(adMain, false);
        checkHauptAuftragBuendlNr(ad, 100);
        checkHauptAuftragBuendlNr(adMain, 100);

        //check if non main hurican orders will found, that nothing will done
        when(sut.findAllAuftragDaten4OrderNoOrig(baAuftrag.getHauptAuftragNo())).thenReturn(null);
        sut.createBillingHauptauftragsBuendel(ad, baAuftrag);
        verify(sut, times(2)).saveAuftragDaten(ad, false);
        verify(sut).saveAuftragDaten(adMain, false);
        checkHauptAuftragBuendlNr(ad, 100);

        //check if non main hurican orders with same id
        adMain.setBuendelNr(110);
        baAuftrag.setHauptAuftragNo(110L);
        when(sut.findAllAuftragDaten4OrderNoOrig(baAuftrag.getHauptAuftragNo())).thenReturn(Collections.singletonList(adMain));
        sut.createBillingHauptauftragsBuendel(ad, baAuftrag);
        verify(sut, times(3)).saveAuftragDaten(ad, false);
        verify(sut).saveAuftragDaten(adMain, false);
        checkHauptAuftragBuendlNr(ad, 110);
        checkHauptAuftragBuendlNr(adMain, 110);
    }

    @Test(expectedExceptions = StoreException.class, expectedExceptionsMessageRegExp = ".*Billing-Hauptauftrag \'100\'.*")
    public void testCreateHauptaufragsBuendelUnequalBuendelNo() throws Exception {
        BAuftrag baAuftrag = new BAuftragBuilder().withAuftragNoOrig(101L).withHauptAuftragNo(100L).build();
        AuftragDaten adMain = new AuftragDatenBuilder().withAuftragId(7100L).withBuendelNr(273732).build();
        AuftragDaten ad = new AuftragDatenBuilder().withAuftragId(7101L).build();
        when(sut.findAllAuftragDaten4OrderNoOrig(baAuftrag.getHauptAuftragNo())).thenReturn(Collections.singletonList(adMain));

        sut.createBillingHauptauftragsBuendel(ad, baAuftrag);
    }

    private void checkHauptAuftragBuendlNr(AuftragDaten adMain, Integer buendelNr) {
        assertEquals(adMain.getBuendelNr(), buendelNr);
        assertEquals(adMain.getBuendelNrHerkunft(), AuftragDaten.BUENDEL_HERKUNFT_BILLING_HAUPTAUFTRAG);
    }

    @DataProvider
    Object[][] testFindHwRackByAuftragsIdDataProvider() {
        final Long auftragsId = 1L;
        final Long hwBgId = 2L;

        Endstelle endstelle = Mockito.mock(Endstelle.class);
        Equipment equipment = getEquipmentMock(hwBgId, true);
        Equipment invalidEquipment = getEquipmentMock(hwBgId, false);
        HWBaugruppe hwBaugruppe = getHwBaugruppeMock(hwBgId);
        HWRack rack = getHwRackMock(true);
        HWRack invalidRack = getHwRackMock(false);

        // @formatter:off
        return new Object[][] {
				// auftragsId, hwBgId, Endstelle, Equipment, HWBaugruppe, HWRack, expectedResult
                { auftragsId, hwBgId, endstelle, equipment, hwBaugruppe, rack, true},
                { auftragsId, hwBgId, null, equipment, hwBaugruppe, rack, false},               // no matching endstelle
                { auftragsId, hwBgId, endstelle, null, hwBaugruppe, rack, false},               // no matching equipment
                { auftragsId, hwBgId, endstelle, invalidEquipment, hwBaugruppe, rack, false},   // equipment not valid
                { auftragsId, hwBgId, endstelle, equipment, null, rack, false},                 // no matching baugruppe
                { auftragsId, hwBgId, endstelle, equipment, hwBaugruppe, null, false},          // no matching rack
                { auftragsId, hwBgId, endstelle, equipment, hwBaugruppe, invalidRack, false}    // rack not valid
        };
        // @formatter:on
    }

    @Test(dataProvider = "testFindHwRackByAuftragsIdDataProvider")
    public void testFindHwRackByAuftragsId(Long auftragsId, Long hwBgId, Endstelle endstelleMock, Equipment equipmentMock,
            HWBaugruppe hwBaugruppeMock, HWRack rackMock, boolean shouldFindRack) throws Exception {
        when(endstellenService.findEndstelle4Auftrag(auftragsId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelleMock);
        when(rangierungsService.findEquipment4Endstelle(endstelleMock, false, false)).thenReturn(equipmentMock);
        when(hwService.findBaugruppe(hwBgId)).thenReturn(hwBaugruppeMock);
        when(hwService.findRackForBaugruppe(hwBgId)).thenReturn(rackMock);

        HWRack rack = sut.findHwRackByAuftragId(auftragsId);
        if (shouldFindRack) {
            Assert.assertEquals(rack, rackMock);
        }
        else {
            Assert.assertNull(rack);
        }
    }

    private HWBaugruppe getHwBaugruppeMock(Long hwBgId) {
        HWBaugruppe hwBaugruppeMock = Mockito.mock(HWBaugruppe.class);
        when(hwBaugruppeMock.getId()).thenReturn(hwBgId);
        return hwBaugruppeMock;
    }

    private HWRack getHwRackMock(boolean isValid) {
        HWRack rackMock = Mockito.mock(HWRack.class);
        when(rackMock.isValid()).thenReturn(isValid);
        return rackMock;
    }

    private Equipment getEquipmentMock(Long hwBgId, boolean isValid) {
        Equipment equipmentMock = Mockito.mock(Equipment.class);
        when(equipmentMock.getHwBaugruppenId()).thenReturn(hwBgId);
        when(equipmentMock.isValid()).thenReturn(isValid);
        return equipmentMock;
    }

}
