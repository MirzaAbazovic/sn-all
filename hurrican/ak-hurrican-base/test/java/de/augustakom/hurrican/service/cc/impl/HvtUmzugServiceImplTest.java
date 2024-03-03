package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.dao.cc.HvtUmzugDAO;
import de.augustakom.hurrican.exceptions.HvtUmzugException;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.DSLAMProfileChangeReason;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HvtUmzugBuilder;
import de.augustakom.hurrican.model.cc.HvtUmzugDetailBuilder;
import de.augustakom.hurrican.model.cc.KvzSperre;
import de.augustakom.hurrican.model.cc.KvzSperreBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.ProduktGruppeBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetailView;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugMasterView;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugStatus;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.HvtUmzugService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;

@Test(groups = BaseTest.UNIT)
public class HvtUmzugServiceImplTest {

    @Mock
    private HvtUmzugDAO hvtUmzugDAO;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private EndstellenService endstellenService;
    @Mock
    private EndgeraeteService endgeraeteService;
    @Mock
    private CarrierService carrierService;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private HVTService hvtService;
    @Mock
    private ProduktService produktService;
    @Mock
    private PhysikService physikService;
    @Mock
    private AvailabilityService availabilityService;
    @Mock
    private CPSService cpsService;
    @Mock
    private EQCrossConnectionService ccService;
    @Mock
    private DSLAMService dslamService;
    @Mock
    private HWService hwService;

    @InjectMocks
    @Spy
    private HvtUmzugServiceImpl testling;

    @BeforeMethod
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoadAffectedHurricanOrdersByHvtUmzug() throws Exception {
        AuftragDaten erfassung = new AuftragDatenBuilder().withStatusId(AuftragStatus.ERFASSUNG).withAuftragId(1L).setPersist(false).build();
        AuftragDaten inRealisierung = new AuftragDatenBuilder().withStatusId(AuftragStatus.TECHNISCHE_REALISIERUNG).withAuftragId(4L).setPersist(false).build();
        AuftragDaten inBetrieb = new AuftragDatenBuilder().withStatusId(AuftragStatus.IN_BETRIEB).withAuftragId(5L).setPersist(false).build();
        AuftragDaten inKuendigung = new AuftragDatenBuilder().withStatusId(AuftragStatus.KUENDIGUNG_ERFASSEN).withAuftragId(6L).setPersist(false).build();

        Mockito.when(hvtUmzugDAO.findAuftraegeAndEsTypForHvtUmzug(Matchers.anyLong()))
                .thenReturn(Arrays.asList(
                        Pair.create(erfassung.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B),
                        Pair.create(inRealisierung.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B),
                        Pair.create(inBetrieb.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B),
                        Pair.create(inKuendigung.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B)
                ));

        HvtUmzug hvtUmzug = new HvtUmzugBuilder().setPersist(false).build();
        List<Pair<Long, String>> result = testling.loadAffectedHurricanOrdersByHvtUmzug(hvtUmzug);
        verify(hvtUmzugDAO).findAuftraegeAndEsTypForHvtUmzug(hvtUmzug.getId());
        Assert.assertNotNull(result);
        assertEquals(result.size(), 4);
    }

    @Test
    public void testLoadAffectedHurricanOrdersByHvtUmzugEmptyListWhenNoResult() throws Exception {
        Mockito.when(hvtUmzugDAO.findAuftraegeAndEsTypForHvtUmzug(Matchers.anyLong()))
                .thenReturn(Collections.emptyList());
        List<Pair<Long, String>> result = testling.loadAffectedHurricanOrdersByHvtUmzug(
                new HvtUmzugBuilder().setPersist(false).build());
        assertNotNull(result);
        Assert.assertTrue(result.isEmpty());

        verify(hvtUmzugDAO).findAuftraegeAndEsTypForHvtUmzug(anyLong());
    }


    @Test
    public void testMatchHurricanOrders4HvtUmzug() throws FindException, StoreException {
        HvtUmzugDetail detailMatchExpected = new HvtUmzugDetailBuilder()
                .withUevtStiftAlt("0101-9-8")
                .withAuftragBuilder(new AuftragBuilder().withId(1L))
                .setPersist(false).build();
        HvtUmzugDetail detailNoMatchExpected = new HvtUmzugDetailBuilder()
                .withUevtStiftAlt("0102-7-9")
                .setPersist(false).build();

        HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withHvtUmzugDetails(Sets.newHashSet(detailMatchExpected, detailNoMatchExpected))
                .setPersist(false).build();

        Pair<Long, String> matchingOrder = Pair.create(1L, Endstelle.ENDSTELLEN_TYP_B);
        Pair<Long, String> additionalOrder = Pair.create(2L, Endstelle.ENDSTELLEN_TYP_A);
        doReturn(Arrays.asList(matchingOrder, additionalOrder)).when(testling)
                .loadAffectedHurricanOrdersByHvtUmzug(hvtUmzug);
        when(auftragService.findAuftragDatenByAuftragId(matchingOrder.getFirst())).thenReturn(
                new AuftragDatenBuilder().withProdId(15L).setPersist(false).build());
        when(auftragService.findAuftragDatenByAuftragId(additionalOrder.getFirst())).thenReturn(
                new AuftragDatenBuilder().withProdId(321L).setPersist(false).build());

        EquipmentBuilder matchingEqOutBuilder = new EquipmentBuilder()
                .withRangVerteiler("0101").withRangLeiste1("09").withRangStift1("08")
                .withRandomId().setPersist(false);
        RangierungBuilder matchingRangierungBuilder = new RangierungBuilder()
                .withRandomId().withEqOutBuilder(matchingEqOutBuilder)
                .setPersist(false);
        doReturn(Optional.of(Pair.create(new Rangierung[] { matchingRangierungBuilder.get(), null },
                new Equipment[] { null, matchingEqOutBuilder.get() })))
                .when(testling).loadPhysics(matchingOrder.getFirst(), matchingOrder.getSecond());

        EquipmentBuilder additionalEqOutBuilder = new EquipmentBuilder()
                .withRangVerteiler("0909").withRangLeiste1("05").withRangStift1("100")
                .withRandomId().setPersist(false);
        EquipmentBuilder additionalEqInBuilder = new EquipmentBuilder()
                .withManualConfiguration(true).withRandomId().setPersist(false);
        RangierungBuilder additionalRangierungBuilder = new RangierungBuilder()
                .withRandomId().withEqInBuilder(additionalEqInBuilder).withEqOutBuilder(additionalEqOutBuilder)
                .setPersist(false);
        doReturn(Optional.of(Pair.create(new Rangierung[] { additionalRangierungBuilder.get(), null },
                new Equipment[] { additionalEqInBuilder.get(), additionalEqOutBuilder.get() })))
                .when(testling).loadPhysics(additionalOrder.getFirst(), additionalOrder.getSecond());

        testling.matchHurricanOrders4HvtUmzug(hvtUmzug);

        verify(hvtUmzugDAO).store(hvtUmzug);

        assertEquals(hvtUmzug.getHvtUmzugDetails().size(), 3);

        Optional<HvtUmzugDetail> additionalDetail = hvtUmzug.getHvtUmzugDetails().stream()
                .filter(d -> additionalOrder.getFirst().equals(d.getAuftragId()))
                .findFirst();
        Assert.assertTrue(additionalDetail.isPresent());
        assertEquals(additionalDetail.get().getUevtStiftAlt(), "0909-5-00");
        Assert.assertTrue(additionalDetail.get().getAdditionalOrder());
        Assert.assertTrue(additionalDetail.get().getManualCc());
    }

    public void testDisableUmzugChangesStatusOffenToDeaktiviert() {
        KvzSperre kvzsperre = new KvzSperreBuilder().setPersist(false).build();
        final HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withStatus(HvtUmzugStatus.OFFEN)
                .withKvzSperre(kvzsperre)
                .build();

        when(hvtUmzugDAO.findById(anyLong(), any())).thenReturn(hvtUmzug);
        when(hvtUmzugDAO.store(hvtUmzug)).thenReturn(hvtUmzug);

        final HvtUmzug result = testling.disableUmzug(1234L);

        assertThat(result.getStatus(), equalTo(HvtUmzugStatus.DEAKTIVIERT));
        verify(testling).saveHvtUmzug(hvtUmzug);
        verify(hvtUmzugDAO).store(hvtUmzug);
        verify(hvtService).deleteKvzSperre(kvzsperre.getId());
        verify(testling).unlockRangierungen(hvtUmzug);
    }


    @Test
    public void testUnlockRangierungen() {
        HvtUmzugDetail detail1 = new HvtUmzugDetailBuilder().setPersist(false).build();
        HvtUmzugDetail detail2 = new HvtUmzugDetailBuilder().setPersist(false).build();

        final HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withHvtUmzugDetails(Sets.newHashSet(detail1, detail2))
                .build();

        testling.unlockRangierungen(hvtUmzug);
        verify(testling).unlockRangierung(detail1, false);
        verify(testling).unlockRangierung(detail2, false);
    }


    @Test
    public void testUnlockRangierungNewlyGeneratedRangierung() throws StoreException, FindException {
        RangierungBuilder primaryBuilder = new RangierungBuilder().withRandomId()
                .withFreigegeben(Rangierung.Freigegeben.gesperrt).setPersist(false);
        Rangierung primary = primaryBuilder.get();
        RangierungBuilder secondaryBuilder = new RangierungBuilder().withRandomId()
                .withFreigegeben(Rangierung.Freigegeben.gesperrt).setPersist(false);
        Rangierung secondary = secondaryBuilder.get();

        HvtUmzugDetail detail = new HvtUmzugDetailBuilder()
                .withRangierBuilderNeu(primaryBuilder)
                .withRangierBuilderAddNeu(secondaryBuilder)
                .withRangNeuErzeugt()
                .setPersist(false).build();

        when(rangierungsService.findRangierung(primary.getId())).thenReturn(primary);
        when(rangierungsService.findRangierung(secondary.getId())).thenReturn(secondary);

        testling.unlockRangierung(detail, false);

        assertEquals(primary.getFreigegeben(), Rangierung.Freigegeben.deactivated);
        Assert.assertTrue(DateTools.isDateBefore(primary.getGueltigBis(), DateTools.getHurricanEndDate()));
        assertEquals(secondary.getFreigegeben(), Rangierung.Freigegeben.deactivated);
        Assert.assertTrue(DateTools.isDateBefore(secondary.getGueltigBis(), DateTools.getHurricanEndDate()));

        verify(rangierungsService).saveRangierung(primary, false);
        verify(rangierungsService).saveRangierung(secondary, false);

        verify(rangierungsService).loadEquipments(primary);
        verify(rangierungsService).loadEquipments(secondary);

        verify(testling, times(4)).setEquipmentToStatusFrei(any(Optional.class));
    }


    @Test
    public void testUnlockRangierungExistingRangierung() throws StoreException, FindException {
        RangierungBuilder primaryBuilder = new RangierungBuilder().withRandomId()
                .withFreigegeben(Rangierung.Freigegeben.gesperrt).setPersist(false);
        Rangierung primary = primaryBuilder.get();
        RangierungBuilder secondaryBuilder = new RangierungBuilder().withRandomId()
                .withFreigegeben(Rangierung.Freigegeben.gesperrt).setPersist(false);
        Rangierung secondary = secondaryBuilder.get();

        HvtUmzugDetail detail = new HvtUmzugDetailBuilder()
                .withRangierBuilderNeu(primaryBuilder)
                .withRangierBuilderAddNeu(secondaryBuilder)
                .setPersist(false).build();

        when(rangierungsService.findRangierung(primary.getId())).thenReturn(primary);
        when(rangierungsService.findRangierung(secondary.getId())).thenReturn(secondary);

        testling.unlockRangierung(detail, true);

        assertEquals(primary.getFreigegeben(), Rangierung.Freigegeben.freigegeben);
        Assert.assertNull(primary.getBemerkung());
        assertEquals(secondary.getFreigegeben(), Rangierung.Freigegeben.freigegeben);
        Assert.assertNull(secondary.getBemerkung());
        Assert.assertNull(detail.getRangierIdNeu());
        Assert.assertNull(detail.getRangierAddIdNeu());
        Assert.assertNull(detail.getUevtStiftNeu());


        verify(rangierungsService).saveRangierung(primary, false);
        verify(rangierungsService).saveRangierung(secondary, false);
        verify(hvtUmzugDAO).store(detail);
    }


    @Test
    public void testSetEquipmentToStatusFrei() throws StoreException {
        Equipment equipment = new EquipmentBuilder().withStatus(EqStatus.rang).setPersist(false).build();
        testling.setEquipmentToStatusFrei(Optional.of(equipment));
        assertEquals(equipment.getStatus(), EqStatus.frei);
        verify(rangierungsService).saveEquipment(equipment);
    }


    @Test
    public void testCreateHvtUmzug() throws Exception {
        final HVTGruppeBuilder hvtGruppeBuilder = new HVTGruppeBuilder().withRandomId().setPersist(false);
        final HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder()
                .withRandomId()
                .withHvtGruppeBuilder(hvtGruppeBuilder)
                .setPersist(false);
        HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withStatus(HvtUmzugStatus.OFFEN)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withKvzNr("KVZ_NR")
                .setPersist(false)
                .build();
        when(hvtService.findHVTGruppe4Standort(hvtStandortBuilder.getId())).thenReturn(hvtGruppeBuilder.build());
        when(hvtService.createKvzSperre(anyLong(), anyString(), anyString())).thenReturn(new KvzSperre());
        when(hvtUmzugDAO.store(any(HvtUmzug.class))).thenReturn(hvtUmzug);

        final HvtUmzug result = testling.createHvtUmzug(hvtUmzug);
        Assert.assertNotNull(result.getKvzSperre());
        assertEquals(result.getStatus(), HvtUmzugStatus.OFFEN);
    }

    @Test(expectedExceptions = StoreException.class, expectedExceptionsMessageRegExp = "HVT-Umzug konnte nicht erzeugt werden, die Felder 'HVT-Standort' und 'KVZ-Nr.'.*")
    public void testCreateHvtUmzugNoStandort() throws Exception {
        HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withStatus(HvtUmzugStatus.OFFEN)
                .withKvzNr("KVZ_NR")
                .setPersist(false)
                .build();
        testling.createHvtUmzug(hvtUmzug);
    }

    @Test
    public void testLoadMasterView() throws Exception {
        final HvtUmzugStatus status = HvtUmzugStatus.OFFEN;
        final HvtUmzug src = new HvtUmzugBuilder()
                .withHvtStandortBuilder(new HVTStandortBuilder().withRandomId())
                .withHvtStandortDestinationBuilder(new HVTStandortBuilder().withRandomId())
                .build();
        when(hvtUmzugDAO.findHvtUmzuegeWithStatus(status)).thenReturn(Collections.singletonList(src));
        when(hvtService.findHVTGruppe4Standort(src.getHvtStandort())).thenReturn(new HVTGruppeBuilder().withOrtsteil("SRC-HVT").build());
        when(hvtService.findHVTGruppe4Standort(src.getHvtStandortDestination())).thenReturn(new HVTGruppeBuilder().withOrtsteil("DEST-HVT").build());
        final List<HvtUmzugMasterView> result = testling.loadHvtMasterData(status);

        final HvtUmzugMasterView view = result.get(0);
        assertNotNull(view);
        assertEquals(view.getStatus(), status.getDisplayName());
        assertEquals(view.getBearbeiter(), src.getBearbeiter());
        assertEquals(view.getHvtStandortName(), "SRC-HVT");
        assertEquals(view.getHvtStandortDestinationName(), "DEST-HVT");
        assertEquals(view.getKvzNr(), src.getKvzNr());
        assertEquals(view.getSchalttag(), Date.from(src.getSchalttag().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    @Test
    public void testLoadEmptyMasterView() throws Exception {
        when(hvtUmzugDAO.findHvtUmzuegeWithStatus(any(HvtUmzugStatus.class))).thenReturn(Collections.emptyList());
        final List<HvtUmzugMasterView> result = testling.loadHvtMasterData(HvtUmzugStatus.OFFEN);
        assertTrue(result != null && result.isEmpty());
    }

    @Test
    public void testLoadHvtUmzugDetailData() throws Exception {
        final HvtUmzug hvtUmzug = new HvtUmzugBuilder().withId(345L).build();
        List<HvtUmzugDetailView> result = testling.loadHvtUmzugDetailData(hvtUmzug.getId());
        assertTrue(result.isEmpty());

        doReturn(hvtUmzug).when(testling).findById(hvtUmzug.getId());
        when(auftragService.findAuftragStati()).thenReturn(Collections.singletonList(getAuftragStatusInBetrieb()));
        final ProduktBuilder produktBuilder = new ProduktBuilder().withAnschlussart("TEST-PRODUKT").withRandomId();
        final Produkt produkt = produktBuilder.build();
        when(produktService.findProdukte(false)).thenReturn(Collections.singletonList(produkt));
        final AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();

        final HvtUmzugDetail hvtUmzugDetail = new HvtUmzugDetailBuilder().withId(1L).withAuftragBuilder(auftragBuilder).build();
        hvtUmzugDetail.setAuftragNoOrig(90000L);
        final HvtUmzugDetail hvtUmzugDetail2 = new HvtUmzugDetailBuilder().withId(2L).withAuftragBuilder(auftragBuilder).build();
        hvtUmzugDetail2.setAuftragNoOrig(20L);
        hvtUmzug.setHvtUmzugDetails(Sets.newHashSet(hvtUmzugDetail, hvtUmzugDetail2));
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragBuilder(auftragBuilder)
                .withProdBuilder(produktBuilder)
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .build();
        when(auftragService.findAuftragDatenByAuftragId(anyLong())).thenReturn(auftragDaten);
        when(cpsService.isCPSProvisioningAllowed(anyLong(), any(CPSServiceOrderData.LazyInitMode.class), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(new CPSProvisioningAllowed(true, "allowed"));

        result = testling.loadHvtUmzugDetailData(hvtUmzug.getId());
        assertEquals(result.size(), 2);
        final HvtUmzugDetailView view = result.get(0);
        assertEquals(view.getAuftragNoOrig().longValue(), 20L);
        final HvtUmzugDetailView view2 = result.get(1);
        assertTrue(view2.getAuftragNoOrig() > view.getAuftragNoOrig());
        assertEquals(view2.getAuftragId(), auftragDaten.getAuftragId());
        assertEquals(view2.getId(), hvtUmzugDetail.getId());
        assertEquals(view2.getAuftragStatus(), "IN BETRIEB");
        assertEquals(view2.getCpsAllowed().booleanValue(), true);
        assertEquals(view2.getLbz(), hvtUmzugDetail.getLbz());
        assertEquals(view2.getProdukt(), "TEST-PRODUKT");
    }

    @Test
    public void testLoadHvtUmzugDetailDataExceptionDuringDataAggregation() throws Exception {
        final HvtUmzug hvtUmzug = new HvtUmzugBuilder().withId(345L).build();
        doReturn(hvtUmzug).when(testling).findById(hvtUmzug.getId());
        when(auftragService.findAuftragStati()).thenReturn(Collections.singletonList(getAuftragStatusInBetrieb()));
        final AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();

        final HvtUmzugDetail hvtUmzugDetail = new HvtUmzugDetailBuilder().withId(1L).withAuftragBuilder(auftragBuilder).build();
        hvtUmzugDetail.setAuftragNoOrig(90000L);
        final HvtUmzugDetail hvtUmzugDetail2 = new HvtUmzugDetailBuilder().withId(2L).withAuftragBuilder(auftragBuilder).build();
        hvtUmzugDetail2.setAuftragNoOrig(20L);
        hvtUmzug.setHvtUmzugDetails(Sets.newHashSet(hvtUmzugDetail, hvtUmzugDetail2));
        when(auftragService.findAuftragDatenByAuftragId(anyLong())).thenThrow(new FindException("TEST"));

        List<HvtUmzugDetailView> result = testling.loadHvtUmzugDetailData(hvtUmzug.getId());
        assertEquals(result.size(), 2);
        final HvtUmzugDetailView view = result.get(0);
        assertEquals(view.getAuftragNoOrig().longValue(), 20L);
        final HvtUmzugDetailView view2 = result.get(1);
        assertTrue(view2.getAuftragNoOrig() > view.getAuftragNoOrig());
        assertEquals(view2.getAuftragId(), view.getAuftragId());
        assertEquals(view2.getId(), hvtUmzugDetail.getId());
        assertEquals(view2.getAuftragStatus(), "NO AUFTRAG DATA FOUND");
        assertEquals(view2.getCpsAllowed(), null);
        assertEquals(view2.getProdukt(), "NO AUFTRAG DATA FOUND");
    }

    @Test(expectedExceptions = FindException.class, expectedExceptionsMessageRegExp = "TEST AUFTRAGSTATI")
    public void testLoadHvtUmzugMetaException() throws Exception {
        final HvtUmzug hvtUmzug = new HvtUmzugBuilder().withId(345L).build();
        doReturn(hvtUmzug).when(testling).findById(hvtUmzug.getId());
        when(auftragService.findAuftragStati()).thenThrow(new FindException("TEST AUFTRAGSTATI"));
        testling.loadHvtUmzugDetailData(hvtUmzug.getId());
    }

    @Test
    public void testHvtViewSorting() throws Exception {
        for (int i = 0; i < 100; i++) {
            System.out.println("TRY: " + i);
            final HvtUmzugDetailView view3 = new HvtUmzugDetailView();
            final HvtUmzugDetailView view2 = new HvtUmzugDetailView();
            final HvtUmzugDetailView view = new HvtUmzugDetailView();
            final HvtUmzugDetailView view4 = new HvtUmzugDetailView();
            final HvtUmzugDetailView view5 = new HvtUmzugDetailView();
            view.withAuftragNoOrig(100L);
            view2.withAuftragNoOrig(200000L);
            view3.withAuftragNoOrig(888888L);
            view4.withAuftragNoOrig(9999999L);

            final List<HvtUmzugDetailView> result = Arrays.asList(view3, view2, view, view4, view5).parallelStream()
                    .map(v -> new HvtUmzugDetailView().withAuftragNoOrig(v.getAuftragNoOrig()))
                    .sorted().collect(Collectors.toList());
            final Iterator<HvtUmzugDetailView> it = result.iterator();
            assertEquals(it.next().getAuftragNoOrig().longValue(), 100L);
            assertEquals(it.next().getAuftragNoOrig().longValue(), 200000L);
            assertEquals(it.next().getAuftragNoOrig().longValue(), 888888L);
            assertEquals(it.next().getAuftragNoOrig().longValue(), 9999999L);
            assertEquals(it.next().getAuftragNoOrig(), null);
        }
    }

    private AuftragStatus getAuftragStatusInBetrieb() {
        final AuftragStatus auftragStatus = new AuftragStatus();
        auftragStatus.setId(AuftragStatus.IN_BETRIEB);
        auftragStatus.setStatusText("IN BETRIEB");
        return auftragStatus;
    }

    @DataProvider
    public Object[][] hvtUmzugStatiWithoutOFFEN() {
        return new Object[][] {
                { HvtUmzugStatus.DEAKTIVIERT },
                { HvtUmzugStatus.AUSGEFUEHRT },
                { HvtUmzugStatus.BEENDET }
        };
    }

    @Test(dataProvider = "hvtUmzugStatiWithoutOFFEN")
    public void testDisableUmzugReturnsNullWhenNotInStatusOffen(final HvtUmzugStatus status) {
        final HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withStatus(status)
                .build();

        when(hvtUmzugDAO.findById(anyLong(), any())).thenReturn(hvtUmzug);
        when(hvtUmzugDAO.store(hvtUmzug)).thenReturn(hvtUmzug);

        final HvtUmzug result = testling.disableUmzug(1234L);
        assertThat(result, nullValue());
    }


    @DataProvider(name = "testAllowedForAutomaticPortAssignmentDP")
    public Object[][] testAllowedForAutomaticPortAssignmentDP() {
        ProduktGruppeBuilder pgConnect = new ProduktGruppeBuilder().withProduktGruppe("Connect").setPersist(false);
        ProduktGruppeBuilder pgConnectSdsl = new ProduktGruppeBuilder().withProduktGruppe("ConnectSDSL").setPersist(false);
        ProduktGruppeBuilder pgPartner = new ProduktGruppeBuilder().withProduktGruppe("Partnerprodukte").setPersist(false);
        ProduktGruppeBuilder pgVpn = new ProduktGruppeBuilder().withProduktGruppe("VPN").setPersist(false);
        ProduktGruppeBuilder pgSdsl = new ProduktGruppeBuilder().withProduktGruppe("SDSL").setPersist(false);
        ProduktGruppeBuilder pgCentrex = new ProduktGruppeBuilder().withProduktGruppe("Centrex").setPersist(false);
        ProduktGruppeBuilder pgMaxi = new ProduktGruppeBuilder().withProduktGruppe("Maxi").setPersist(false);

        List<Produkt2PhysikTyp> p2pts = Collections.singletonList(new Produkt2PhysikTypBuilder().setPersist(false).build());

        return new Object[][] {
                { pgConnect, null, false },
                { pgConnectSdsl, null, false },
                { pgPartner, null, false },
                { pgVpn, null, false },
                { pgSdsl, null, false },
                { pgCentrex, null, false },
                { pgMaxi, null, false },
                { pgMaxi, p2pts, true },
        };
    }

    @Test(dataProvider = "testAllowedForAutomaticPortAssignmentDP")
    public void testAllowedForAutomaticPortAssignment(ProduktGruppeBuilder pgBuilder, List<Produkt2PhysikTyp> p2pts, boolean expected) throws FindException {
        when(produktService.findProdukt4Auftrag(anyLong())).thenReturn(
                new ProduktBuilder().withProduktGruppeBuilder(pgBuilder).setPersist(false).build());
        when(produktService.findProduktGruppe(anyLong())).thenReturn(pgBuilder.get());
        when(physikService.findP2PTs4Produkt(anyLong(), eq(null))).thenReturn(p2pts);

        assertEquals(testling.allowedForAutomaticPortAssignment(1L), expected);
    }


    @Test
    public void testLockRangierung() throws StoreException {
        Rangierung toLock = new RangierungBuilder().setPersist(false).build();
        HvtUmzugDetail detail = new HvtUmzugDetailBuilder().setPersist(false).build();

        testling.lockRangierung(toLock, detail);
        verify(rangierungsService).saveRangierung(toLock, false);
        Assert.assertTrue(toLock.getBemerkung().startsWith(
                StringUtils.substringBeforeLast(HvtUmzugServiceImpl.RANGIERUNG_LOCK_MESSAGE, "%s")));
        assertEquals(toLock.getFreigegeben(), Rangierung.Freigegeben.gesperrt);
    }


    @Test
    public void testTransferUetv() throws FindException, StoreException {
        EquipmentBuilder eqOutBuilder = new EquipmentBuilder().withRandomId().withDtagValues()
                .withUETV(Uebertragungsverfahren.H13).setPersist(false);
        Rangierung toLock = new RangierungBuilder().withEqOutBuilder(eqOutBuilder).setPersist(false).build();

        EquipmentBuilder eqOutOldBuilder = new EquipmentBuilder().withRandomId().withDtagValues()
                .withUETV(Uebertragungsverfahren.H04).setPersist(false);
        RangierungBuilder rangierungAlt = new RangierungBuilder().withRandomId()
                .withEqOutBuilder(eqOutOldBuilder).setPersist(false);
        Endstelle endstelle = new EndstelleBuilder()
                .withRangierungBuilder(rangierungAlt)
                .setPersist(false).build();

        HvtUmzugDetail detail = new HvtUmzugDetailBuilder().setPersist(false).build();

        when(endstellenService.findEndstelle4Auftrag(anyLong(), anyString())).thenReturn(endstelle);
        when(rangierungsService.findRangierung(rangierungAlt.get().getId())).thenReturn(rangierungAlt.get());
        when(rangierungsService.findEquipment(eqOutBuilder.get().getId())).thenReturn(eqOutBuilder.get());
        when(rangierungsService.findEquipment(eqOutOldBuilder.get().getId())).thenReturn(eqOutOldBuilder.get());

        testling.transferUetv(toLock, detail);

        assertEquals(eqOutBuilder.get().getUetv(), eqOutOldBuilder.get().getUetv());
        verify(rangierungsService).saveEquipment(eqOutBuilder.get());
    }


    @Test
    public void testAutomatischePortPlanung() throws FindException, StoreException, ServiceNotFoundException {
        Long hvtUmzugId = 1L;
        Long auftragIdAllowed = 1L;
        Long auftragIdNotAllowed = 2L;
        Set<HvtUmzugDetail> hvtUmzugDetails = new HashSet<>();
        hvtUmzugDetails.add(new HvtUmzugDetailBuilder().withId(hvtUmzugId).withAuftragBuilder(new AuftragBuilder()
                .withId(auftragIdAllowed)).build());
        hvtUmzugDetails.add(new HvtUmzugDetailBuilder().withId(hvtUmzugId).withAuftragBuilder(new AuftragBuilder()
                .withId(auftragIdNotAllowed)).build());
        EquipmentBuilder uevtStiftBuilder = new EquipmentBuilder().withRangVerteiler("101").withRangLeiste1("02")
                .withRangStift1("03").withId(1L);
        Equipment uevtStift = uevtStiftBuilder.build();
        Rangierung rangierungNeu = new RangierungBuilder().withEqOutBuilder(uevtStiftBuilder).withId(1L).build();
        Rangierung rangierungAddNeu = new RangierungBuilder().withId(2L).build();
        Rangierung[] portPlanningResult = new Rangierung[] { rangierungNeu, rangierungAddNeu };
        HvtUmzug hvtUmzug = new HvtUmzugBuilder().withId(hvtUmzugId).withHvtUmzugDetails(hvtUmzugDetails).build();

        doReturn(hvtUmzug).when(testling).findById(hvtUmzugId);
        doReturn(true).when(testling).allowedForAutomaticPortAssignment(auftragIdAllowed);
        doReturn(false).when(testling).allowedForAutomaticPortAssignment(auftragIdNotAllowed);
        doReturn(portPlanningResult).when(testling).findAndLockNewRangierung(any(HvtUmzug.class), any(HvtUmzugDetail.class));
        when(rangierungsService.findEquipment(any(Long.class))).thenReturn(uevtStift);
        doReturn(testling).when(testling).getCCServiceRE(HvtUmzugService.class);

        AKWarnings warnings = testling.automatischePortPlanung(hvtUmzugId);

        Assert.assertTrue(warnings.isEmpty());
        HvtUmzugDetail hvtUmzugDetailAllowed = hvtUmzugDetails.stream()
                .filter(d -> d.getAuftragId().equals(auftragIdAllowed)).findAny().orElse(null);
        HvtUmzugDetail hvtUmzugDetailNotAllowed = hvtUmzugDetails.stream()
                .filter(d -> d.getAuftragId().equals(auftragIdNotAllowed)).findAny().orElse(null);
        assertNotNull(hvtUmzugDetailAllowed);
        assertNotNull(hvtUmzugDetailNotAllowed);
        Assert.assertTrue(rangierungNeu.getId().equals(hvtUmzugDetailAllowed.getRangierIdNeu()));
        Assert.assertTrue(rangierungAddNeu.getId().equals(hvtUmzugDetailAllowed.getRangierAddIdNeu()));
        Assert.assertTrue("101-2-3".equals(hvtUmzugDetailAllowed.getUevtStiftNeu()));
        Assert.assertNull(hvtUmzugDetailNotAllowed.getRangierIdNeu());
        Assert.assertNull(hvtUmzugDetailNotAllowed.getRangierAddIdNeu());
    }

    @Test
    public void testAutomatischePortPlanungWithWarning() throws Exception {
        Long hvtUmzugId = 1L;
        Set<HvtUmzugDetail> hvtUmzugDetails = new HashSet<>();
        hvtUmzugDetails.add(new HvtUmzugDetailBuilder().withAuftragBuilder(new AuftragBuilder().withId(1L)).build());
        hvtUmzugDetails.add(new HvtUmzugDetailBuilder().withAuftragBuilder(new AuftragBuilder().withId(2L)).build());
        HvtUmzug hvtUmzug = new HvtUmzugBuilder().withId(hvtUmzugId).withHvtUmzugDetails(hvtUmzugDetails).build();

        doReturn(hvtUmzug).when(testling).findById(hvtUmzugId);
        doThrow(new StoreException("Fehler!")).when(testling).reserveRangierung4HvtUmzugDetail(any(HvtUmzug.class), any(HvtUmzugDetail.class));
        doReturn(testling).when(testling).getCCServiceRE(HvtUmzugService.class);

        AKWarnings warnings = testling.automatischePortPlanung(hvtUmzugId);

        Assert.assertFalse(warnings.isEmpty());
        Assert.assertEquals(warnings.getAKMessages().size(), 2L);
        Assert.assertTrue(warnings.getWarningsAsText().contains("Auftrag 1"));
        Assert.assertTrue(warnings.getWarningsAsText().contains("Auftrag 2"));
    }

    @Test
    public void testAutomatischePortPlanungWithFutureWitaBereitstellung() throws Exception {
        Long hvtUmzugId = 1L;
        Set<HvtUmzugDetail> hvtUmzugDetails = new HashSet<>();
        hvtUmzugDetails.add(new HvtUmzugDetailBuilder().withAuftragBuilder(new AuftragBuilder().withId(1L)).build());
        hvtUmzugDetails.add(new HvtUmzugDetailBuilder()
                .withAuftragBuilder(new AuftragBuilder().withId(2L))
                .withWitaBereitstellungAm(LocalDate.now())
                .build());
        HvtUmzug hvtUmzug = new HvtUmzugBuilder().withId(hvtUmzugId)
                .withSchalttag(LocalDate.now().minusDays(1))
                .withHvtUmzugDetails(hvtUmzugDetails).build();

        doReturn(hvtUmzug).when(testling).findById(hvtUmzugId);
        doReturn(testling).when(testling).getCCServiceRE(HvtUmzugService.class);

        AKWarnings warnings = testling.automatischePortPlanung(hvtUmzugId);

        Assert.assertFalse(warnings.isEmpty());
        Assert.assertEquals(warnings.getAKMessages().size(), 1L);
        Assert.assertTrue(warnings.getWarningsAsText()
                .contains("Das Datum der Portplanung liegt nicht vor dem Datum der Wita Bereitstellung"));
    }

    @DataProvider
    public Object[][] testAutomatischePortPlanungWhenStatusIsNotOffenDataProvider() {
        return new Object[][] {
                { HvtUmzugStatus.AUSGEFUEHRT },
                { HvtUmzugStatus.BEENDET },
                { HvtUmzugStatus.DEAKTIVIERT },
        };
    }

    @Test(dataProvider = "testAutomatischePortPlanungWhenStatusIsNotOffenDataProvider")
    public void testAutomatischePortPlanungWhenStatusIsNotOffen(final HvtUmzugStatus status) throws Exception {
        final long hvtUmzugId = 1L;
        final HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withId(hvtUmzugId)
                .withStatus(status)
                .build();

        doReturn(hvtUmzug).when(testling).findById(hvtUmzugId);

        final AKWarnings result = testling.automatischePortPlanung(hvtUmzugId);
        assertThat(result.getWarningsAsText(), containsString("Status 'offen'"));
    }

    @Test
    public void testManuellePortPlanung() throws FindException, StoreException, ServiceNotFoundException {
        Long hvtUmzugId = 1L;
        HvtUmzugDetail hvtUmzugDetail = new HvtUmzugDetailBuilder().withId(hvtUmzugId)
                .withAuftragBuilder(new AuftragBuilder().withId(1L))
                .build();

        EquipmentBuilder uevtStiftBuilder = new EquipmentBuilder().withRangVerteiler("101").withRangLeiste1("02")
                .withRangStift1("03").withId(1L);
        Equipment uevtStift = uevtStiftBuilder.build();
        Rangierung rangierungNeu = new RangierungBuilder().withEqOutBuilder(uevtStiftBuilder).withId(1L).build();
        Rangierung rangierungAddNeu = new RangierungBuilder().withId(2L).build();

        doReturn(new HvtUmzugBuilder().withStatus(HvtUmzugStatus.OFFEN).build()).when(testling).findById(hvtUmzugId);
        doReturn(hvtUmzugDetail).when(testling).findDetailById(hvtUmzugDetail.getId());
        when(rangierungsService.findRangierung(rangierungNeu.getId())).thenReturn(rangierungNeu);
        when(rangierungsService.findRangierung(rangierungAddNeu.getId())).thenReturn(rangierungAddNeu);
        when(rangierungsService.findEquipment(any(Long.class))).thenReturn(uevtStift);
        doReturn(testling).when(testling).getCCServiceRE(HvtUmzugService.class);

        AKWarnings warnings = testling.manuellePortPlanung(hvtUmzugId, hvtUmzugDetail.getId(), rangierungNeu.getId(), true, true);
        Assert.assertTrue(warnings.isEmpty());

        warnings = testling.manuellePortPlanung(hvtUmzugId, hvtUmzugDetail.getId(), rangierungAddNeu.getId(), false, false);
        Assert.assertTrue(warnings.isEmpty());

        Assert.assertTrue(rangierungNeu.getId().equals(hvtUmzugDetail.getRangierIdNeu()));
        Assert.assertTrue(rangierungNeu.getFreigegeben().equals(Rangierung.Freigegeben.gesperrt));
        Assert.assertTrue(rangierungAddNeu.getId().equals(hvtUmzugDetail.getRangierAddIdNeu()));
        Assert.assertTrue(rangierungAddNeu.getFreigegeben().equals(Rangierung.Freigegeben.gesperrt));
        Assert.assertTrue(hvtUmzugDetail.getRangNeuErzeugt());
        Assert.assertTrue("101-2-3".equals(hvtUmzugDetail.getUevtStiftNeu()));
    }

    @Test
    public void testManuellePortPlanungInvalidHvtUmzugDetail() throws FindException, StoreException, ServiceNotFoundException {
        Long hvtUmzugId = 1L;
        HvtUmzugDetail hvtUmzugDetail = new HvtUmzugDetailBuilder().withId(hvtUmzugId)
                .withAuftragBuilder(new AuftragBuilder().withId(1L))
                .build();

        doReturn(null).when(testling).findDetailById(hvtUmzugDetail.getId());
        doReturn(testling).when(testling).getCCServiceRE(HvtUmzugService.class);

        AKWarnings warnings = testling.manuellePortPlanung(hvtUmzugId, hvtUmzugDetail.getId(), 1L, true, false);
        Assert.assertFalse(warnings.isEmpty());
        Assert.assertTrue(warnings.getWarningsAsText().contains("Die Daten zum Umzug Detail konnten nicht ermittelt werden"));
    }

    @Test
    public void testManuellePortPlanungInvalidRangierung() throws FindException, StoreException, ServiceNotFoundException {
        Long hvtUmzugId = 1L;
        HvtUmzugDetail hvtUmzugDetail = new HvtUmzugDetailBuilder().withId(hvtUmzugId)
                .withAuftragBuilder(new AuftragBuilder().withId(1L))
                .build();

        HvtUmzug hvtUmzug = new HvtUmzugBuilder().withId(hvtUmzugId)
                .withHvtUmzugDetails(Collections.singleton(hvtUmzugDetail)).build();

        doReturn(hvtUmzugDetail).when(testling).findDetailById(hvtUmzugDetail.getId());
        doReturn(hvtUmzug).when(testling).findById(hvtUmzug.getId());
        when(rangierungsService.findRangierung(1L)).thenThrow(new FindException());
        doReturn(testling).when(testling).getCCServiceRE(HvtUmzugService.class);

        AKWarnings warnings = testling.manuellePortPlanung(hvtUmzugId, hvtUmzugDetail.getId(), 1L, true, false);
        Assert.assertFalse(warnings.isEmpty());
        Assert.assertTrue(warnings.getWarningsAsText().startsWith("Fehler bei Rangierungs-Zuordnung fuer Auftrag"));
    }

    @Test
    public void testManuellePortPlanungNotAllowed() throws FindException, StoreException, ServiceNotFoundException {
        Long hvtUmzugId = 1L;
        HvtUmzugDetail hvtUmzugDetail = new HvtUmzugDetailBuilder().withId(hvtUmzugId)
                .withWitaBereitstellungAm(LocalDate.now())
                .withAuftragBuilder(new AuftragBuilder().withId(1L))
                .build();

        HvtUmzug hvtUmzug = new HvtUmzugBuilder().withId(hvtUmzugId)
                .withStatus(HvtUmzugStatus.DEAKTIVIERT)
                .withSchalttag(LocalDate.now().plusDays(10))
                .withHvtUmzugDetails(Collections.singleton(hvtUmzugDetail)).build();

        doReturn(hvtUmzugDetail).when(testling).findDetailById(hvtUmzugDetail.getId());
        doReturn(hvtUmzug).when(testling).findById(hvtUmzug.getId());
        when(rangierungsService.findRangierung(1L)).thenReturn(new RangierungBuilder().withRandomId().withEsId(1L).build());
        doReturn(testling).when(testling).getCCServiceRE(HvtUmzugService.class);

        AKWarnings warnings = testling.manuellePortPlanung(hvtUmzugId, hvtUmzugDetail.getId(), 1L, true, false);
        Assert.assertFalse(warnings.isEmpty());
        Assert.assertTrue(warnings.getWarningsAsText()
                .contains("manuelle Portplanung kann nur im Status 'offen' durchgefuehrt werden"));
    }

    @Test
    public void testManuellePortPlanungFutureWitaBereitstellung() throws FindException, StoreException, ServiceNotFoundException {
        Long hvtUmzugId = 1L;
        HvtUmzugDetail hvtUmzugDetail = new HvtUmzugDetailBuilder().withId(hvtUmzugId)
                .withWitaBereitstellungAm(LocalDate.now())
                .withAuftragBuilder(new AuftragBuilder().withId(1L))
                .build();

        HvtUmzug hvtUmzug = new HvtUmzugBuilder().withId(hvtUmzugId)
                .withSchalttag(LocalDate.now().minusDays(1))
                .withHvtUmzugDetails(Collections.singleton(hvtUmzugDetail)).build();

        doReturn(hvtUmzugDetail).when(testling).findDetailById(hvtUmzugDetail.getId());
        doReturn(hvtUmzug).when(testling).findById(hvtUmzug.getId());
        when(rangierungsService.findRangierung(1L)).thenReturn(new RangierungBuilder().withRandomId().withEsId(1L).build());
        doReturn(testling).when(testling).getCCServiceRE(HvtUmzugService.class);

        AKWarnings warnings = testling.manuellePortPlanung(hvtUmzugId, hvtUmzugDetail.getId(), 1L, true, false);
        Assert.assertFalse(warnings.isEmpty());
        Assert.assertTrue(warnings.getWarningsAsText()
                .contains("Der Wita Bereitstellung liegt nach dem geplanten Schalttag des KVZ!"), warnings.getWarningsAsText());
    }

    @Test
    public void testUnAttachRangierung() throws FindException, StoreException {
        Rangierung rangierung = new RangierungBuilder().withRandomId().withEsId(1L)
                .withFreigegeben(Rangierung.Freigegeben.deactivated).setPersist(false).build();
        when(rangierungsService.findRangierung(anyLong())).thenReturn(rangierung);

        testling.unAttachRangierung(rangierung.getId());

        assertEquals(rangierung.getFreigegeben(), Rangierung.Freigegeben.deactivated);
        Assert.assertNull(rangierung.getEsId());
        verify(rangierungsService).saveRangierung(rangierung, false);
    }


    @Test
    public void testActivateRangierung4Es() throws FindException, StoreException {
        Rangierung rangierung = new RangierungBuilder().withRandomId().withEsId(null)
                .withFreigegeben(Rangierung.Freigegeben.gesperrt).setPersist(false).build();
        when(rangierungsService.findRangierung(anyLong())).thenReturn(rangierung);

        Endstelle endstelle = new EndstelleBuilder().withRandomId().setPersist(false).build();

        testling.activateRangierung4Es(rangierung.getId(), endstelle.getId(), null);

        assertEquals(rangierung.getEsId(), endstelle.getId());
        assertEquals(rangierung.getFreigegeben(), Rangierung.Freigegeben.freigegeben);
        verify(rangierungsService).saveRangierung(rangierung, false);
    }


    @DataProvider(name = "transferRangierungenDP")
    public Object[][] transferRangierungenDP() {
        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();

        HvtUmzugDetail detailFull = new HvtUmzugDetailBuilder().setPersist(false)
                .withAuftragBuilder(auftragBuilder)
                .withRangierBuilderNeu(new RangierungBuilder().withRandomId().setPersist(false))
                .withRangierBuilderAddNeu(new RangierungBuilder().withRandomId().setPersist(false))
                .build();

        HvtUmzugDetail detailOnlyPrimeRangierung = new HvtUmzugDetailBuilder().setPersist(false)
                .withRangierBuilderNeu(new RangierungBuilder().withRandomId().setPersist(false))
                .build();

        HvtUmzugDetail detailFullAndPartial = new HvtUmzugDetailBuilder().setPersist(false)
                .withRangierBuilderNeu(new RangierungBuilder().withRandomId().setPersist(false))
                .build();

        return new Object[][] {
                { detailFull, true, true },
                { detailFull, true, false },
                { detailOnlyPrimeRangierung, false, true },
                { detailFullAndPartial, false, true },
        };
    }


    @Test(dataProvider = "transferRangierungenDP")
    public void testTransferRangierungen(HvtUmzugDetail detail, boolean hasAdditionalRangierung, boolean activeRangierung)
            throws StoreException, FindException {
        RangierungBuilder rangierungOrigBuilder = (activeRangierung)
                ? new RangierungBuilder().withRandomId().setPersist(false)
                : new RangierungBuilder().withRandomId().withEsId(-1L).withFreigabeAb(new Date()).setPersist(false);
        Endstelle endstelle = new EndstelleBuilder().withRandomId()
                .setPersist(false).build();
        endstelle.setRangierId(rangierungOrigBuilder.getId());
        endstelle.setRangierIdAdditional(new RangierungBuilder().withRandomId().setPersist(false).getId());

        Long rangierIdAlt = endstelle.getRangierId();
        Long rangierIdAddAlt = endstelle.getRangierIdAdditional();

        HVTStandortBuilder destination = new HVTStandortBuilder().withRandomId().setPersist(false);
        HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withHvtStandortDestinationBuilder(destination)
                .withStatus(HvtUmzugStatus.OFFEN)
                .setPersist(false).build();

        doReturn(new AKWarnings()).when(testling).calculateDefaultCcs(any(HvtUmzug.class), any(HvtUmzugDetail.class),
                any(Endstelle.class), any(Long.class));
        doNothing().when(testling).activateRangierung4Es(anyLong(), anyLong(), any(Date.class));
        doNothing().when(testling).unAttachRangierung(anyLong());
        doReturn(testling).when(testling).getCCServiceRE(HvtUmzugService.class);
        when(rangierungsService.findRangierung(endstelle.getRangierId())).thenReturn(rangierungOrigBuilder.get());

        testling.transferRangierungen(hvtUmzug, detail, endstelle, 1L);

        assertEquals(endstelle.getHvtIdStandort(), destination.get().getHvtIdStandort());
        assertEquals(endstelle.getRangierId(), detail.getRangierIdNeu());
        if (hasAdditionalRangierung) {
            assertEquals(endstelle.getRangierIdAdditional(), detail.getRangierAddIdNeu());
        }
        else {
            Assert.assertNull(endstelle.getRangierIdAdditional());
        }

        verify(testling).unAttachRangierung(rangierIdAlt);
        verify(testling).unAttachRangierung(rangierIdAddAlt);

        verify(endstellenService).saveEndstelle(endstelle);

        verify(testling).activateRangierung4Es(
                detail.getRangierIdNeu(), rangierungOrigBuilder.get().getEsId(), rangierungOrigBuilder.get().getFreigabeAb());
        verify(testling).activateRangierung4Es(
                detail.getRangierAddIdNeu(), rangierungOrigBuilder.get().getEsId(), rangierungOrigBuilder.get().getFreigabeAb());
        verify(testling, times(3)).getCCServiceRE(HvtUmzugService.class);
    }

    @Test
    public void testTransferDSLAMProfil() throws StoreException, FindException {
        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        HvtUmzugDetail detail = new HvtUmzugDetailBuilder().setPersist(false)
                .withAuftragBuilder(auftragBuilder)
                .withRangierBuilderNeu(new RangierungBuilder().withRandomId().setPersist(false))
                .withRangierBuilderAddNeu(new RangierungBuilder().withRandomId().setPersist(false))
                .build();

        HVTStandortBuilder destination = new HVTStandortBuilder().withRandomId().setPersist(false);
        HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withSchalttag(LocalDate.now())
                .withHvtStandortDestinationBuilder(destination)
                .withStatus(HvtUmzugStatus.OFFEN)
                .setPersist(false).build();

        HWBaugruppenTyp hwBaugruppenTypOld = new HWBaugruppenTypBuilder()
                .withRandomId()
                .build();

        HWBaugruppenTyp hwBaugruppenTypNew = new HWBaugruppenTypBuilder()
                .withRandomId()
                .build();

        DSLAMProfile oldDSLAMProfile = new DSLAMProfileBuilder()
                .withRandomId()
                .withBaugruppenTypId(hwBaugruppenTypOld.getId())
                .withUetv(Uebertragungsverfahren.H13.name())
                .withName("DSLAM_OLD")
                .build();

        DSLAMProfile newDSLAMProfile = new DSLAMProfileBuilder()
                .withRandomId()
                .withBaugruppenTypId(hwBaugruppenTypNew.getId())
                .withUetv(Uebertragungsverfahren.H13.name())
                .withName("DSLAM_NEW")
                .build();

        doReturn(hwBaugruppenTypNew.getId()).when(testling).getHwBaugruppenTypEqIn(detail);
        when(dslamService.findDSLAMProfile4AuftragNoEx(detail.getAuftragId(), Date.from(hvtUmzug.getSchalttag().atStartOfDay(ZoneId.systemDefault()).toInstant()), true)).thenReturn(oldDSLAMProfile);
        when(dslamService.findNewDSLAMProfileMatch(hwBaugruppenTypNew.getId(), oldDSLAMProfile.getId(), Uebertragungsverfahren.H13)).thenReturn(newDSLAMProfile);

        testling.transferDSLAMProfil(hvtUmzug, detail, 1L);

        verify(dslamService).changeDSLAMProfile(detail.getAuftragId(),
                newDSLAMProfile.getId(),
                Date.from(hvtUmzug.getSchalttag().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "unknown",
                DSLAMProfileChangeReason.CHANGE_REASON_ID_AUTOMATIC_SYNC,
                null);
        verify(dslamService).findDSLAMProfile4AuftragNoEx(detail.getAuftragId(), Date.from(hvtUmzug.getSchalttag().atStartOfDay(ZoneId.systemDefault()).toInstant()), true);
        verify(dslamService).findNewDSLAMProfileMatch(hwBaugruppenTypNew.getId(), oldDSLAMProfile.getId(), Uebertragungsverfahren.H13);
    }

    @Test
    public void testTransferDSLAMProfilError() throws StoreException, FindException {
        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        HvtUmzugDetail detail = new HvtUmzugDetailBuilder().setPersist(false)
                .withAuftragBuilder(auftragBuilder)
                .withRangierBuilderNeu(new RangierungBuilder().withRandomId().setPersist(false))
                .withRangierBuilderAddNeu(new RangierungBuilder().withRandomId().setPersist(false))
                .build();

        HVTStandortBuilder destination = new HVTStandortBuilder().withRandomId().setPersist(false);
        HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withSchalttag(LocalDate.now())
                .withHvtStandortDestinationBuilder(destination)
                .withStatus(HvtUmzugStatus.OFFEN)
                .setPersist(false).build();

        HWBaugruppenTyp hwBaugruppenTyp = new HWBaugruppenTypBuilder()
                .withRandomId()
                .build();

        DSLAMProfile oldDSLAMProfile = new DSLAMProfileBuilder()
                .withRandomId()
                .withBaugruppenTypId(hwBaugruppenTyp.getId())
                .withUetv(Uebertragungsverfahren.H13.name())
                .withName("DSLAM_OLD")
                .build();

        when(dslamService.findDSLAMProfile4Auftrag(detail.getAuftragId(), Date.from(hvtUmzug.getSchalttag().atStartOfDay(ZoneId.systemDefault()).toInstant()), true)).thenReturn(oldDSLAMProfile);
        when(dslamService.findNewDSLAMProfileMatch(hwBaugruppenTyp.getId(), oldDSLAMProfile.getId(), Uebertragungsverfahren.H13)).thenThrow(new FindException());

        testling.transferDSLAMProfil(hvtUmzug, detail, 1L);

        verify(dslamService, never()).changeDSLAMProfile(anyLong(), anyLong(), any(Date.class), anyString(), anyLong(), anyString());
    }


    @Test
    public void testExecutePlanning() throws FindException, StoreException {
        HvtUmzugDetail withOrder = new HvtUmzugDetailBuilder()
                .withAuftragBuilder(new AuftragBuilder().withRandomId().setPersist(false)).setPersist(false).build();
        HvtUmzugDetail withOtherOrder = new HvtUmzugDetailBuilder()
                .withAuftragBuilder(new AuftragBuilder().withRandomId().setPersist(false)).setPersist(false).build();
        HvtUmzugDetail withoutOrder = new HvtUmzugDetailBuilder().setPersist(false).build();

        Set<HvtUmzugDetail> hvtUmzugDetails = new HashSet<>();
        hvtUmzugDetails.add(withOrder);
        hvtUmzugDetails.add(withoutOrder);
        hvtUmzugDetails.add(withOtherOrder);

        final KvzSperre sperre = new KvzSperreBuilder().withRandomId().setPersist(false).build();

        HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withStatus(HvtUmzugStatus.PLANUNG_VOLLSTAENDIG)
                .withHvtUmzugDetails(hvtUmzugDetails)
                .withKvzSperre(sperre)
                .setPersist(false).build();

        Endstelle endstelle1 = new EndstelleBuilder().setPersist(false).build();
        Endstelle endstelle2 = new EndstelleBuilder().setPersist(false).build();

        doReturn(new AKWarnings()).when(testling).transferRangierungen(any(HvtUmzug.class), any(HvtUmzugDetail.class),
                any(Endstelle.class), anyLong());
        when(endstellenService.findEndstelle4AuftragNewTx(withOrder.getAuftragId(), withOrder.getEndstellenTyp()))
                .thenReturn(endstelle1);
        when(endstellenService.findEndstelle4AuftragNewTx(withOtherOrder.getAuftragId(), withOtherOrder.getEndstellenTyp()))
                .thenReturn(endstelle2);

        testling.executePlanning(hvtUmzug, 1L);

        assertEquals(hvtUmzug.getStatus(), HvtUmzugStatus.AUSGEFUEHRT);

        verify(endstellenService, times(2)).findEndstelle4AuftragNewTx(anyLong(), anyString());
        verify(testling).transferRangierungen(hvtUmzug, withOrder, endstelle1, 1L);
        verify(testling).transferRangierungen(hvtUmzug, withOtherOrder, endstelle2, 1L);
        verify(availabilityService).moveKvzLocationsToHvt(
                sperre,
                hvtUmzug.getHvtStandort(),
                hvtUmzug.getHvtStandortDestination(), 1L);
        verify(testling).saveHvtUmzug(hvtUmzug);
        verify(hvtService).deleteKvzSperre(sperre.getId());

    }

    @Test
    public void testCalculateDefaultCcsWithWarning() throws FindException, StoreException {
        HvtUmzugDetail detail = new HvtUmzugDetailBuilder().withManualCc(Boolean.TRUE)
                .withAuftragBuilder(new AuftragBuilder().setPersist(false)).setPersist(false).build();
        Endstelle endstelle = new EndstelleBuilder().setPersist(false).build();
        HvtUmzug hvtUmzug = new HvtUmzugBuilder().setPersist(false).build();

        when(produktService.findProdukt4Auftrag(any(Long.class)))
                .thenReturn(new ProduktBuilder().setPersist(false).build());
        when(ccService.deactivateCrossConnections4Endstelle(any(Endstelle.class), any(Date.class)))
                .thenReturn(Pair.create(new RangierungBuilder().setPersist(false).build(),
                        new EquipmentBuilder().setPersist(false).build()));
        when(ccService.checkCcsAllowed(any(Endstelle.class)))
                .thenReturn(null);

        AKWarnings warnings = testling.calculateDefaultCcs(hvtUmzug, detail, endstelle, 1L);

        verify(ccService, times(1)).defineDefaultCrossConnections4Port(any(Equipment.class),
                any(Long.class), any(Date.class), any(Boolean.class), any(Long.class));
        verify(rangierungsService, times(1)).saveEquipment(any(Equipment.class));
        assertNotNull(warnings.getWarningsAsText());
        assertTrue(warnings.getWarningsAsText().contains("manuell konfigurierte CrossConnections"));
    }

    @Test
    public void testCalculateDefaultCcsWithError() throws FindException, StoreException {
        HvtUmzugDetail detail = new HvtUmzugDetailBuilder().withManualCc(Boolean.TRUE)
                .withAuftragBuilder(new AuftragBuilder().setPersist(false)).setPersist(false).build();
        Endstelle endstelle = new EndstelleBuilder().setPersist(false).build();
        HvtUmzug hvtUmzug = new HvtUmzugBuilder().setPersist(false).build();
        String message = "CCs not allowed!";

        when(produktService.findProdukt4Auftrag(any(Long.class)))
                .thenReturn(new ProduktBuilder().setPersist(false).build());
        when(ccService.deactivateCrossConnections4Endstelle(any(Endstelle.class), any(Date.class)))
                .thenReturn(Pair.create(new RangierungBuilder().setPersist(false).build(),
                        new EquipmentBuilder().setPersist(false).build()));
        when(ccService.checkCcsAllowed(any(Endstelle.class)))
                .thenReturn(message);

        AKWarnings warnings = testling.calculateDefaultCcs(hvtUmzug, detail, endstelle, 1L);

        verify(ccService, times(0)).defineDefaultCrossConnections4Port(any(Equipment.class), any(Long.class),
                any(Date.class), any(Boolean.class), any(Long.class));
        verify(rangierungsService, times(0)).saveEquipment(any(Equipment.class));
        assertTrue(warnings.isEmpty());
    }

    @Test(dataProvider = "hvtUmzugStatiWithoutOFFEN", expectedExceptions = HvtUmzugException.class,
            expectedExceptionsMessageRegExp = HvtUmzugServiceImpl.EXECUTION_NOT_POSSIBLE)
    public void testExecutePlanningStatusNotOk(HvtUmzugStatus status) {
        HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withStatus(status)
                .setPersist(false).build();
        testling.executePlanning(hvtUmzug, 1L);
    }


    @Test
    public void testExecutePlanningWarningWhenNoEndstelleFound() throws FindException {
        HvtUmzugDetail withOrder = new HvtUmzugDetailBuilder()
                .withAuftragBuilder(new AuftragBuilder().withRandomId().setPersist(false)).setPersist(false).build();

        Set<HvtUmzugDetail> hvtUmzugDetails = new HashSet<>();
        hvtUmzugDetails.add(withOrder);

        HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withStatus(HvtUmzugStatus.PLANUNG_VOLLSTAENDIG)
                .withHvtUmzugDetails(hvtUmzugDetails)
                .withKvzSperre(new KvzSperreBuilder().withRandomId().setPersist(false).build())
                .setPersist(false).build();

        when(endstellenService.findEndstelle4Auftrag(anyLong(), anyString())).thenReturn(null);

        AKWarnings warnings = testling.executePlanning(hvtUmzug, 1L);
        Assert.assertTrue(warnings.isNotEmpty());
        Assert.assertTrue(warnings.getAKMessages().get(0).getMessage()
                .matches("Endstelle (.*) zu Auftrag (.*) nicht ermittelbar! Auftrag nicht umgeschrieben!"));
    }

    @Test
    public void testCloseHvtUmzugChangesStatusToAusgefuehrt() {
        final Either<AKWarnings, HvtUmzug> result = mockAndExecuteCloseHvtUmzug(HvtUmzugStatus.AUSGEFUEHRT);
        assertThat(result.getRight().getStatus(), equalTo(HvtUmzugStatus.BEENDET));
        verify(hvtUmzugDAO).store(any(HvtUmzug.class));
    }

    @DataProvider
    Object[][] testCloseHvtUmzugReturnsWarningForWrongStatusDataProvider() {
        return new Object[][] {
                { HvtUmzugStatus.BEENDET },
                { HvtUmzugStatus.DEAKTIVIERT },
                { HvtUmzugStatus.OFFEN },
                { HvtUmzugStatus.PLANUNG_VOLLSTAENDIG },
        };
    }

    @Test(dataProvider = "testCloseHvtUmzugReturnsWarningForWrongStatusDataProvider")
    public void testCloseHvtUmzugReturnsWarningForWrongStatus(final HvtUmzugStatus hvtUmzugStatus) {
        final Either<AKWarnings, HvtUmzug> result = mockAndExecuteCloseHvtUmzug(hvtUmzugStatus);
        assertThat(result.getLeft().getWarningsAsText(), containsString(hvtUmzugStatus.getDisplayName()));
        verify(hvtUmzugDAO, never()).store(any(HvtUmzug.class));
    }

    private Either<AKWarnings, HvtUmzug> mockAndExecuteCloseHvtUmzug(final HvtUmzugStatus hvtUmzugStatus) {
        final Long id = 1L;
        final HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withStatus(hvtUmzugStatus)
                .build();
        when(hvtUmzugDAO.findById(id, HvtUmzug.class)).thenReturn(hvtUmzug);
        when(hvtUmzugDAO.store(hvtUmzug)).thenReturn(hvtUmzug);

        return testling.closeHvtUmzug(id);
    }

    @Test
    public void testMarkHvtUmzugAsDefined() {
        final Either<AKWarnings, HvtUmzug> result = mockAndExecuteMarkHvtUmzugAsDefined(HvtUmzugStatus.OFFEN);
        assertThat(result.getRight().getStatus(), equalTo(HvtUmzugStatus.PLANUNG_VOLLSTAENDIG));
        verify(hvtUmzugDAO).store(any(HvtUmzug.class));
    }

    @DataProvider
    Object[][] testMarkHvtUmzugAsDefinedWrongStatusDataProvider() {
        return new Object[][] {
                { HvtUmzugStatus.BEENDET },
                { HvtUmzugStatus.DEAKTIVIERT },
                { HvtUmzugStatus.PLANUNG_VOLLSTAENDIG },
                { HvtUmzugStatus.AUSGEFUEHRT },
        };
    }

    @Test(dataProvider = "testMarkHvtUmzugAsDefinedWrongStatusDataProvider")
    public void testMarkHvtUmzugAsDefinedWrongStatus(final HvtUmzugStatus hvtUmzugStatus) {
        final Either<AKWarnings, HvtUmzug> result = mockAndExecuteMarkHvtUmzugAsDefined(hvtUmzugStatus);
        assertThat(result.getLeft().getWarningsAsText(), containsString(hvtUmzugStatus.getDisplayName()));
        verify(hvtUmzugDAO, never()).store(any(HvtUmzug.class));
    }

    private Either<AKWarnings, HvtUmzug> mockAndExecuteMarkHvtUmzugAsDefined(final HvtUmzugStatus hvtUmzugStatus) {
        final Long id = 1L;
        final HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withStatus(hvtUmzugStatus)
                .build();
        when(hvtUmzugDAO.findById(id, HvtUmzug.class)).thenReturn(hvtUmzug);
        when(hvtUmzugDAO.store(hvtUmzug)).thenReturn(hvtUmzug);

        return testling.markHvtUmzugAsDefined(id);
    }

    @Test
    public void testHasDetailWithoutNewPlanning() throws Exception {
        HvtUmzug hvtUmzug = new HvtUmzug();
        hvtUmzug.setHvtUmzugDetails(Sets.newHashSet(
                new HvtUmzugDetailBuilder().withAuftragBuilder(null).build()
        ));
        assertFalse(testling.hasDetailWithoutNewPlanning(hvtUmzug));

        //add element without rangierung
        hvtUmzug.getHvtUmzugDetails().add(new HvtUmzugDetailBuilder()
                        .withAuftragBuilder(new AuftragBuilder().withRandomId())
                        .withRangierBuilderNeu(null)
                        .build()
        );
        assertTrue(testling.hasDetailWithoutNewPlanning(hvtUmzug));

        hvtUmzug.setHvtUmzugDetails(Sets.newHashSet(new HvtUmzugDetailBuilder()
                .withAuftragBuilder(new AuftragBuilder().withRandomId())
                .withRangierBuilderNeu(new RangierungBuilder().withRandomId())
                .build()));
        assertFalse(testling.hasDetailWithoutNewPlanning(hvtUmzug));
    }

    @Test
    public void testCreateAdditionalOrderEntry() {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("new sheet");
        Row row = sheet.createRow(1);
        XlsPoiTool.setContent(row, HvtUmzugServiceImpl.COL_UEVT_STIFT_ALT, "");
        XlsPoiTool.setContent(row, HvtUmzugServiceImpl.COL_LSZ, "");
        XlsPoiTool.setContent(row, HvtUmzugServiceImpl.COL_ORD_NR, "");
        XlsPoiTool.setContent(row, HvtUmzugServiceImpl.COL_ONKZ_A, "");
        XlsPoiTool.setContent(row, HvtUmzugServiceImpl.COL_ONKZ_B, "");
        XlsPoiTool.setContent(row, HvtUmzugServiceImpl.COL_LBZ, "");
        XlsPoiTool.setContent(row, HvtUmzugServiceImpl.COL_UVT, "");
        XlsPoiTool.setContent(row, HvtUmzugServiceImpl.COL_EVS_NR, "");
        XlsPoiTool.setContent(row, HvtUmzugServiceImpl.COL_DOPPELADER, "");
        XlsPoiTool.setContent(row, HvtUmzugServiceImpl.COL_BEMERKUNG, "Bemerkung");
        HvtUmzugDetail hvtUmzugDetail = new HvtUmzugDetailBuilder().setPersist(false)
                .withLbz("96W/89/30/127946")
                .withUevtStiftAlt("0202-7-46")
                .withUevtIdStiftNeu("0204-22-38")
                .build();
        testling.createAdditionalOrderEntry(wb, sheet, hvtUmzugDetail);
        Row row2 = sheet.getRow(2);
        assertNotNull(row2);
        assertEquals(XlsPoiTool.getContentAsString(row2, HvtUmzugServiceImpl.COL_UEVT_STIFT_ALT), "0202-7-46");
        assertEquals(XlsPoiTool.getContentAsString(row2, HvtUmzugServiceImpl.COL_LSZ), "96W");
        assertEquals(XlsPoiTool.getContentAsString(row2, HvtUmzugServiceImpl.COL_ORD_NR), "127946");
        assertEquals(XlsPoiTool.getContentAsString(row2, HvtUmzugServiceImpl.COL_ONKZ_A), "89");
        assertEquals(XlsPoiTool.getContentAsString(row2, HvtUmzugServiceImpl.COL_ONKZ_B), "30");
        assertEquals(XlsPoiTool.getContentAsString(row2, HvtUmzugServiceImpl.COL_LBZ), "96W/89/30/127946");
        assertEquals(XlsPoiTool.getContentAsString(row2, HvtUmzugServiceImpl.COL_UVT), "0204");
        assertEquals(XlsPoiTool.getContentAsString(row2, HvtUmzugServiceImpl.COL_EVS_NR), "22");
        assertEquals(XlsPoiTool.getContentAsString(row2, HvtUmzugServiceImpl.COL_DOPPELADER), "38");
        assertEquals(XlsPoiTool.getContentAsString(row2, HvtUmzugServiceImpl.COL_BEMERKUNG), "zusätzlicher Auftrag");
    }

    @DataProvider
    public Object[][] setUevtStiftNeuDP() {
        return new Object[][] {
                { "0202-7-46", false, "0202", "7", "46" },
                { "0202-7-46", true, "0202", "7", "46" },
                { "0202-07-46", false, "0202", "7", "46" },
                { "0202-07-46", true, "0202", "7", "46" },
                { "0202-07-046", false, "0202", "7", "46" },
                { "0202-07-046", true, "0202", "7", "46" },
                { "0202-000070-0004060", false, "0202", "70", "4060" },
                { "0202-000070-0004060", true, "0202", "70", "4060" },
        };
    }

    @Test(dataProvider = "setUevtStiftNeuDP")
    public void testSetUevtStiftNeu(String uevtStiftNeu, boolean cellExists,
            String expectedUvt, String expectedEvs, String expectedDoppelader) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("new sheet");
        Row row = sheet.createRow(1);
        if (cellExists) {
            row.createCell(HvtUmzugServiceImpl.COL_UVT);
            row.createCell(HvtUmzugServiceImpl.COL_EVS_NR);
            row.createCell(HvtUmzugServiceImpl.COL_DOPPELADER);
        }

        testling.setUevtStiftNeu(row, uevtStiftNeu, cellExists);
        assertEquals(XlsPoiTool.getContentAsString(row, HvtUmzugServiceImpl.COL_UVT), expectedUvt);
        assertEquals(XlsPoiTool.getContentAsString(row, HvtUmzugServiceImpl.COL_EVS_NR), expectedEvs);
        assertEquals(XlsPoiTool.getContentAsString(row, HvtUmzugServiceImpl.COL_DOPPELADER), expectedDoppelader);
    }


    @DataProvider(name = "sendCpsModifiesDP")
    public Object[][] sendCpsModifiesDP() {
        return new Object[][] {
                { false },
                { true }
        };
    }

    @Test(dataProvider = "sendCpsModifiesDP")
    public void testSendCpsModifies(boolean simulate) throws StoreException, FindException {
        HvtUmzugDetailBuilder detail = new HvtUmzugDetailBuilder()
                .withAuftragBuilder(new AuftragBuilder().withRandomId().setPersist(false))
                .withUevtIdStiftNeu("NEW")
                .setPersist(false);
        HvtUmzugDetailBuilder detailWithoutOrder = new HvtUmzugDetailBuilder()
                .withUevtIdStiftNeu("NEW")
                .setPersist(false);
        HvtUmzugDetailBuilder detailWithCpsTx = new HvtUmzugDetailBuilder()
                .withAuftragBuilder(new AuftragBuilder().withRandomId().setPersist(false))
                .withCpsTransactionBuilder(new CPSTransactionBuilder().withRandomId().setPersist(false))
                .withUevtIdStiftNeu("NEW")
                .setPersist(false);
        HvtUmzugDetailBuilder detailWithOutNewUvt = new HvtUmzugDetailBuilder()
                .withAuftragBuilder(new AuftragBuilder().withRandomId().setPersist(false))
                .withCpsTransactionBuilder(new CPSTransactionBuilder().withRandomId().setPersist(false))
                .withUevtIdStiftNeu(null)
                .setPersist(false);

        HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withHvtUmzugDetails(Sets.newHashSet(detail.get(), detailWithoutOrder.get(), detailWithCpsTx.get(), detailWithOutNewUvt.get()))
                .setPersist(false).build();

        when(produktService.findProdukt4Auftrag(anyLong()))
                .thenReturn(new ProduktBuilder().withCpsProductName("test").setPersist(false).build());

        CPSTransaction cpsTx = new CPSTransactionBuilder().withRandomId().setPersist(false).build();
        CPSTransactionResult cpsTransactionResult = new CPSTransactionResult();
        cpsTransactionResult.setCpsTransactions(Collections.singletonList(cpsTx));
        when(cpsService.createCPSTransaction(any(CreateCPSTransactionParameter.class)))
                .thenReturn(cpsTransactionResult);

        testling.sendCpsModifies(hvtUmzug, simulate, 1L);

        assertEquals(detail.get().getCpsTxId(), (simulate) ? null : cpsTx.getId());

        int times = (simulate) ? 0 : 1;
        verify(cpsService, times(times)).sendCPSTx2CPS(cpsTx, 1L);
        verify(hvtUmzugDAO, times(times)).store(detail.get());

        if (simulate) {
            verify(cpsService).saveCPSTransaction(eq(cpsTx), anyLong());
        }
    }


    public void testSendCpsModifiesNoCpsProduct() throws StoreException, FindException {
        HvtUmzugDetailBuilder detail = new HvtUmzugDetailBuilder()
                .withAuftragBuilder(new AuftragBuilder().withRandomId().setPersist(false))
                .withUevtIdStiftNeu("NEW")
                .setPersist(false);
        HvtUmzugDetailBuilder detailWithoutOrder = new HvtUmzugDetailBuilder()
                .withUevtIdStiftNeu("NEW")
                .setPersist(false);
        HvtUmzugDetailBuilder detailWithCpsTx = new HvtUmzugDetailBuilder()
                .withAuftragBuilder(new AuftragBuilder().withRandomId().setPersist(false))
                .withCpsTransactionBuilder(new CPSTransactionBuilder().withRandomId().setPersist(false))
                .withUevtIdStiftNeu("NEW")
                .setPersist(false);
        HvtUmzugDetailBuilder detailWithOutNewUvt = new HvtUmzugDetailBuilder()
                .withAuftragBuilder(new AuftragBuilder().withRandomId().setPersist(false))
                .withCpsTransactionBuilder(new CPSTransactionBuilder().withRandomId().setPersist(false))
                .withUevtIdStiftNeu(null)
                .setPersist(false);

        HvtUmzug hvtUmzug = new HvtUmzugBuilder()
                .withHvtUmzugDetails(Sets.newHashSet(detail.get(), detailWithoutOrder.get(), detailWithCpsTx.get(), detailWithOutNewUvt.get()))
                .setPersist(false).build();

        when(produktService.findProdukt4Auftrag(anyLong()))
                .thenReturn(new ProduktBuilder().withCpsProductName(null).setPersist(false).build());

        testling.sendCpsModifies(hvtUmzug, false, 1L);

        verify(cpsService, never()).createCPSTransaction(any(CreateCPSTransactionParameter.class));
    }


    @DataProvider(name = "getUevtStiftFormattedDP")
    public Object[][] getUevtStiftFormattedDP() {
        return new Object[][] {
                { new EquipmentBuilder().withRangVerteiler("0202").withRangLeiste1("01").withRangStift1("00").setPersist(false).build(), "0202-1-00" }, //Sonderfall: Stift 100 will die DTAG als 00
                { new EquipmentBuilder().withRangVerteiler("0202").withRangLeiste1("01").withRangStift1("02").setPersist(false).build(), "0202-1-2" },
                { new EquipmentBuilder().withRangVerteiler("0202").withRangLeiste1(null).withRangStift1("02").setPersist(false).build(), "0202-XX-2" },
                { new EquipmentBuilder().withRangVerteiler("0202").withRangLeiste1("01").withRangStift1(null).setPersist(false).build(), "0202-1-XX" },
                { new EquipmentBuilder().withRangVerteiler(null).withRangLeiste1(null).withRangStift1(null).setPersist(false).build(), "XXXX-XX-XX" },
                { new EquipmentBuilder().withRangVerteiler("0202").withRangLeiste1("01").withRangStift1("02abc").setPersist(false).build(), "0202-1-2ab" },
        };
    }


    @Test(dataProvider = "getUevtStiftFormattedDP")
    public void testGetUevtStiftFormatted(Equipment eq, String expected) {
        Assert.assertEquals(testling.getUevtStiftFormatted(eq), expected);
    }


    @Test
    public void testGetHwBaugruppenTypEqIn() throws FindException {
        HWBaugruppenTypBuilder bgTypBuilder = new HWBaugruppenTypBuilder().withRandomId().setPersist(false);

        HWBaugruppeBuilder bgBuilder = new HWBaugruppeBuilder().withRandomId()
                .withBaugruppenTypBuilder(bgTypBuilder).setPersist(false);

        EquipmentBuilder eqInBuilder = new EquipmentBuilder()
                .withRandomId()
                .withBaugruppeBuilder(bgBuilder)
                .setPersist(false);

        RangierungBuilder rangierungBuilder = new RangierungBuilder()
                .withRandomId()
                .withEqInBuilder(eqInBuilder)
                .setPersist(false);

        HvtUmzugDetailBuilder detail = new HvtUmzugDetailBuilder()
                .withRangierBuilderNeu(rangierungBuilder)
                .setPersist(false);

        when(rangierungsService.findRangierung(detail.get().getRangierIdNeu())).thenReturn(rangierungBuilder.get());
        when(rangierungsService.findEquipment(rangierungBuilder.get().getEqInId())).thenReturn(eqInBuilder.get());
        when(hwService.findBaugruppe(eqInBuilder.get().getHwBaugruppenId())).thenReturn(bgBuilder.get());

        assertEquals(testling.getHwBaugruppenTypEqIn(detail.get()), bgTypBuilder.getId());
    }


    @Test
    public void testGetHwBaugruppenTypEqInReturnNullIfNoRangierung() throws FindException {
        assertNull(testling.getHwBaugruppenTypEqIn(new HvtUmzugDetail()));
    }

}
