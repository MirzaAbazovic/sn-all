/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import static de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalSiteCommand.*;
import static de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams.Site.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.CrossingBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.DeviceTestBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.LocationTestBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.SiteBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.SiteTestBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.WiringDataTestBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDpoBuilder;
import de.augustakom.hurrican.model.cc.HWDpuBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWOltChildBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.PhysikUebernahme;
import de.augustakom.hurrican.model.cc.PhysikUebernahmeBuilder;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmTechnicalSiteCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private HVTService hvtService;
    @Mock
    private CCKundenService kundenService;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private HWService hwService;
    @Mock
    private CCLeistungsService ccLeistungsService;
    @Mock
    private CCAuftragService ccAuftragService;
    @Mock
    private PhysikService physikService;
    @Mock
    private CarrierService carrierService;
    @Mock
    private EndstellenService endstellenService;
    @Mock
    private DSLAMService dslamService;

    @InjectMocks
    @Spy
    private AggregateFfmTechnicalSiteCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmTechnicalSiteCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteFailure() throws Exception {
        prepareFfmCommand(testling);
        Object result = testling.execute();

        assertNotNull(result);
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(!((ServiceCommandResult) result).isOk());

        verify(testling).checkThatWorkforceOrderHasTechnicalParams();
    }

    @Test
    public void testExecute() throws Exception {
        prepareFfmCommand(testling, true);

        Endstelle endstelleB = createEndstelle(Endstelle.ENDSTELLEN_TYP_B, "Endstelle B");
        Endstelle endstelleA = createEndstelle(Endstelle.ENDSTELLEN_TYP_A, "Endstelle A");

        doReturn(new AuftragDatenBuilder().withAuftragNoOrig(1L).build()).when(testling).getAuftragDaten();

        when(endstellenService.findEndstellen4AuftragBasedOnProductConfig(anyLong()))
                .thenReturn(Arrays.asList(endstelleA, endstelleB));

        OrderTechnicalParams.Site siteA = new SiteTestBuilder().withType("Endstelle_A").build();
        doReturn(siteA).when(testling).buildSite(eq(endstelleA), anyLong());
        OrderTechnicalParams.Site siteB = new SiteTestBuilder().withType("Endstelle_B").build();
        doReturn(siteB).when(testling).buildSite(eq(endstelleB), anyLong());

        Object result = testling.execute();

        assertNotNull(result);
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(((ServiceCommandResult) result).isOk());

        List<OrderTechnicalParams.Site> site = workforceOrder.getDescription().getTechParams().getSite();
        assertNotNull(site);
        assertEquals(site.size(), 2);
        assertEquals(site.get(0).getType(), siteA.getType());
        assertEquals(site.get(1).getType(), siteB.getType());

        verify(testling).checkThatWorkforceOrderHasTechnicalParams();
        verify(endstellenService).findEndstellen4AuftragBasedOnProductConfig(anyLong());
        verify(testling).buildSite(eq(endstelleA), anyLong());
        verify(testling).buildSite(eq(endstelleB), anyLong());
    }

    @Test
    public void testExecuteMultipleOrders() throws Exception {
        prepareFfmCommand(testling, true);

        doReturn(new HashSet<>(Arrays.asList(1L, 2L))).when(testling).getAllAuftragIds();
        doNothing().when(testling).aggregateSiteForOrder(anyLong());

        testling.execute();

        verify(testling).aggregateSiteForOrder(1L);
        verify(testling).aggregateSiteForOrder(2L);
    }

    @DataProvider
    private Object[][] buildSiteDataProvider() {
        return new Object[][] {
                {Endstelle.ENDSTELLEN_TYP_A, "Endstelle A"},
                {Endstelle.ENDSTELLEN_TYP_B, "Endstelle B"}
        };
    }

    @Test(dataProvider = "buildSiteDataProvider")
    public void testBuildSite(String endstelleTyp, String endstelleName) throws FindException {
        prepareFfmCommand(testling, true);

        final Long auftragId = 1L;
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragNoOrig(auftragId)
                .build();
        doReturn(auftragDaten).when(testling).getAuftragDaten();

        CCAddressBuilder addressBuilder = new CCAddressBuilder()
                .withRandomId().setPersist(false);

        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder()
                .withRandomId()
                .withClusterId("cluster")
                .setPersist(false);

        HVTStandort hvtStandort = hvtStandortBuilder.build();
        Endstelle endstelle = createEndstelle(endstelleTyp, addressBuilder, hvtStandortBuilder, endstelleName);

        when(hvtService.findHVTStandort(anyLong())).thenReturn(hvtStandort);
        HVTGruppe hvtGruppe = new HVTGruppeBuilder().setPersist(false).build();
        when(hvtService.findHVTGruppeById(anyLong())).thenReturn(hvtGruppe);

        when(dslamService.findDSLAMProfile4AuftragNoEx(eq(auftragId), any(Date.class), eq(true)))
                .thenReturn(new DSLAMProfileBuilder().withName("dslamP").build());

        Location location = new LocationTestBuilder().build();
        doReturn(location).when(testling).createLocation(endstelle);
        Device device = new DeviceTestBuilder().build();
        doReturn(device).when(testling).createDevice(endstelle);
        List<WiringData> wiringData = Arrays.asList(new WiringDataTestBuilder().build(), new WiringDataTestBuilder().build());
        doReturn(wiringData).when(testling).createWiringData(endstelle, auftragId);

        OrderTechnicalParams.Site site = testling.buildSite(endstelle, auftragId);
        verify(testling).defineHvtInformation(any(SiteBuilder.class), eq(endstelle), eq(hvtGruppe), eq(hvtStandort));
        verify(testling).defineUpDownstream(any(SiteBuilder.class), eq(auftragId));
        verify(testling).defineCarrierbestellungData(any(SiteBuilder.class), eq(endstelle), eq(hvtStandort));
        verify(testling).createLocation(endstelle);
        verify(testling).createDevice(endstelle);
        verify(testling).createWiringData(endstelle, auftragId);
        assertEquals(site.getLocation(), location);
        assertEquals(site.getDevice(), device);
        assertEquals(site.getLastValidDSLAMProfile(), "dslamP");
        assertEquals(site.getWiringData(), wiringData);
        assertEquals(site.getType(), "Endstelle_" + endstelleTyp);
        verify(ccAuftragService).getSwitchKennung4Auftrag(auftragId);
    }

    private Endstelle createEndstelle(String endstelleTyp, String endstelleName) {
        CCAddressBuilder addressBuilder = new CCAddressBuilder()
                .withRandomId().setPersist(false);

        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder()
                .withRandomId()
                .withClusterId("cluster")
                .setPersist(false);
        return createEndstelle(endstelleTyp, addressBuilder, hvtStandortBuilder, endstelleName);
    }

    private Endstelle createEndstelle(String endstelleTyp, CCAddressBuilder addressBuilder,
            HVTStandortBuilder hvtStandortBuilder, String endstelleName) {
        return new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().setPersist(false))
                .withEndstelle(endstelleName)
                .withEndstelleTyp(endstelleTyp)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withAddressBuilder(addressBuilder)
                .setPersist(false)
                .build();
    }

    @Test
    public void testCreateLocation() throws FindException {
        CCAddressBuilder addressBuilder = new CCAddressBuilder()
                .withRandomId().setPersist(false);

        Endstelle endstelle = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().setPersist(false))
                .withAddressBuilder(addressBuilder)
                .setPersist(false).build();

        when(kundenService.findCCAddress(endstelle.getAddressId())).thenReturn(addressBuilder.get());

        OrderTechnicalParams.Site.Location result = testling.createLocation(endstelle);
        assertNotNull(result);
        assertEquals(result.getCity(), addressBuilder.get().getCombinedOrtOrtsteil());
        assertEquals(result.getZipCode(), addressBuilder.get().getPlzTrimmed());
        assertEquals(result.getStreet(), addressBuilder.get().getStrasse());
        assertEquals(result.getHouseNumber(), addressBuilder.get().getCombinedHausnummer());
        assertEquals(result.getTAE1(), addressBuilder.get().getStrasseAdd());
    }

    @Test
    public void testCreateDevice() throws FindException {
        HVTTechnikBuilder hvtTechnikBuilder = new HVTTechnikBuilder().withRandomId().withHersteller("hersteller").setPersist(false);

        HWMduBuilder rackBuilder = new HWMduBuilder().withSerialNo("serial-no")
                .withHwProducerBuilder(hvtTechnikBuilder).setPersist(false);
        HWBaugruppeBuilder hwBgBuilder = new HWBaugruppeBuilder().withRandomId().withRackBuilder(rackBuilder).setPersist(false);
        EquipmentBuilder equipmentBuilder = new EquipmentBuilder().withBaugruppeBuilder(hwBgBuilder).withRandomId().setPersist(false);
        RangierungBuilder rangierungBuilder = new RangierungBuilder()
                .withRandomId()
                .withEqInBuilder(equipmentBuilder).setPersist(false);

        Endstelle endstelle = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().setPersist(false))
                .withRangierungBuilder(rangierungBuilder)
                .setPersist(false).build();

        when(rangierungsService.findRangierung(endstelle.getRangierId())).thenReturn(rangierungBuilder.get());
        when(rangierungsService.findEquipment(rangierungBuilder.get().getEqInId())).thenReturn(equipmentBuilder.get());
        when(hwService.findRackForBaugruppe(equipmentBuilder.get().getHwBaugruppenId())).thenReturn(rackBuilder.get());
        when(hvtService.findHVTTechnik(rackBuilder.get().getHwProducer())).thenReturn(hvtTechnikBuilder.get());

        OrderTechnicalParams.Site.Device result = testling.createDevice(endstelle);
        assertNotNull(result);
        assertEquals(result.getManufacturer(), hvtTechnikBuilder.get().getHersteller());
        assertEquals(result.getSerialNumber(), rackBuilder.get().getSerialNo());
    }


    @Test
    public void testDefineUpDownstream() throws FindException {
        prepareFfmCommand(testling);

        TechLeistung techLsDownstream = new TechLeistungBuilder().withLongValue(50000L).setPersist(false).build();
        TechLeistung techLsUpstream = new TechLeistungBuilder().withLongValue(6000L).setPersist(false).build();

        final Long auftragId = 1L;
        doReturn(new AuftragDatenBuilder().withAuftragNoOrig(auftragId).build()).when(testling).getAuftragDaten();
        when(ccLeistungsService.findTechLeistung4Auftrag(anyLong(), eq(TechLeistung.TYP_DOWNSTREAM), any(Date.class)))
                .thenReturn(techLsDownstream);
        when(ccLeistungsService.findTechLeistung4Auftrag(anyLong(), eq(TechLeistung.TYP_UPSTREAM), any(Date.class)))
                .thenReturn(techLsUpstream);

        SiteBuilder sb = new SiteBuilder();
        testling.defineUpDownstream(sb, auftragId);
        OrderTechnicalParams.Site site = sb.build();
        assertEquals("" + site.getDownstream(), "" + techLsDownstream.getLongValue());
        assertEquals("" + site.getUpstream(), "" + techLsUpstream.getLongValue());
    }

    @Test
    public void testDefineCarrierbestellung() throws FindException {
        prepareFfmCommand(testling);

        Carrierbestellung cb = new CarrierbestellungBuilder()
                .withVtrNr("3012645321")
                .withLbz("96W/821/821/00000000123")
                .withLl("100/100")
                .withKundeVorOrt(false)
                .withBereitstellungAm(new Date())
                .setPersist(false).build();

        CCAddressBuilder addressBuilder = new CCAddressBuilder()
                .withRandomId().setPersist(false);

        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder()
                .withRandomId()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .withClusterId("cluster")
                .setPersist(false);

        HVTStandort hvtStandort = hvtStandortBuilder.build();
        Endstelle endstelle = createEndstelle(Endstelle.ENDSTELLEN_TYP_B, addressBuilder, hvtStandortBuilder, "Endstelle B");

        when(carrierService.findLastCB4Endstelle(anyLong())).thenReturn(cb);

        SiteBuilder sb = new SiteBuilder();
        testling.defineCarrierbestellungData(sb, endstelle, hvtStandort);
        OrderTechnicalParams.Site site = sb.build();
        assertEquals(site.getVtrNr(), cb.getVtrNr());
        assertEquals(site.getLBZ(), cb.getLbz());
        assertEquals(site.getLL(), cb.getLl());
        assertEquals(site.getAQS(), cb.getAqs());
        assertEquals(site.getCustomerOnSite(), "Nein");
        assertEquals(site.getTALProvisioningDate(), DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR));
    }

    @Test
    public void testDefineCarrierbestellungDataWithoutHvtStandort() throws FindException {
        Long endstelleId = 1L;
        Carrierbestellung cb = new CarrierbestellungBuilder()
                .withVtrNr("3012645321")
                .withLbz("96W/821/821/00000000123")
                .withLl("100/100")
                .withKundeVorOrt(false)
                .withBereitstellungAm(new Date())
                .setPersist(false).build();
        Endstelle endstelle = new EndstelleBuilder().withId(endstelleId).build();
        SiteBuilder sb = new SiteBuilder();

        when(carrierService.findLastCB4Endstelle(endstelleId)).thenReturn(cb);

        testling.defineCarrierbestellungData(sb, endstelle, null);
        OrderTechnicalParams.Site site = sb.build();
        assertNull(site.getTALProvisioningDate());
    }

    @DataProvider
    private Object[][] createWiringDataDP() {
        HVTTechnikBuilder hvtTechnikBuilder =
                new HVTTechnikBuilder().withRandomId().withHersteller("hersteller").setPersist(false);
        return new Object[][] {
                {
                        new HWOntBuilder()
                                .withSerialNo("serial-no")
                                .withManagementBez("ont-management-bez")
                                .withHwProducerBuilder(hvtTechnikBuilder)
                                .setPersist(false),
                        true
                },
                {
                        new HWDpoBuilder()
                                .withSerialNo("serial-no")
                                .withManagementBez("dpo-management-bez")
                                .withHwProducerBuilder(hvtTechnikBuilder)
                                .withChassisIdentifier("chassis-identifier")
                                .withChassisSlot("chassis-slot")
                                .setPersist(false),
                        false
                },
                {
                        new HWDpuBuilder()
                                .withSerialNo("serial-no")
                                .withManagementBez("dpu-management-bez")
                                .withHwProducerBuilder(hvtTechnikBuilder)
                                .withReversePower(Boolean.TRUE)
                                .setPersist(false),
                        false
                }
        };
    }

    @Test(dataProvider = "createWiringDataDP")
    public void testCreateWiringData(HWOltChildBuilder rackBuilder, boolean isOnt) throws FindException {
        HWBaugruppenTypBuilder hwBgTypBuilder = new HWBaugruppenTypBuilder().setPersist(false);
        HWBaugruppeBuilder hwBgBuilder = new HWBaugruppeBuilder().withRandomId().withBaugruppenTypBuilder(hwBgTypBuilder).withRackBuilder(rackBuilder).setPersist(false);
        EquipmentBuilder eqInBuilder = new EquipmentBuilder()
                .withRangBucht("B")
                .withRangLeiste1("L1").withRangStift1("S1")
                .withRangLeiste2("L2").withRangStift2("S2")
                .withSchicht2Protokoll(Schicht2Protokoll.ATM)
                .withBaugruppeBuilder(hwBgBuilder)
                .withRandomId().setPersist(false);

        EquipmentBuilder eqOutBuilder = new EquipmentBuilder()
                .withRangVerteiler("0201")
                .withRangLeiste1("13").withRangStift1("65")
                .withRandomId().setPersist(false);

        PhysikTypBuilder physikTypBuilder = new PhysikTypBuilder().withRandomId().setPersist(false);
        RangierungBuilder rangierungBuilder = new RangierungBuilder()
                .withRandomId()
                .withPhysikTypBuilder(physikTypBuilder)
                .withEqInBuilder(eqInBuilder)
                .withEqOutBuilder(eqOutBuilder)
                .setPersist(false);

        Endstelle endstelle = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().setPersist(false))
                .withRangierungBuilder(rangierungBuilder)
                .setPersist(false).build();

        when(physikService.findPhysikTyp(physikTypBuilder.getId())).thenReturn(physikTypBuilder.get());

        WiringDataStore wiringDataStoreIn = new WiringDataStore();
        wiringDataStoreIn.rangierung = rangierungBuilder.get();
        wiringDataStoreIn.equipment = eqInBuilder.get();
        wiringDataStoreIn.bgTyp = hwBgTypBuilder.get();
        HWRack hwRack = (HWRack) rackBuilder.get();
        wiringDataStoreIn.hwRack = hwRack;
        wiringDataStoreIn.rangierungTyp = "EQ-In";

        WiringDataStore wiringDataStoreOut = new WiringDataStore();
        wiringDataStoreOut.rangierung = rangierungBuilder.get();
        wiringDataStoreOut.equipment = eqOutBuilder.get();
        wiringDataStoreOut.rangierungTyp = "EQ-Out";

        doReturn(Arrays.asList(wiringDataStoreIn, wiringDataStoreOut)).when(testling).loadHardwareData(endstelle);

        List<WiringData> result = testling.createWiringData(endstelle, 1L);

        assertNotEmpty(result);
        assertEquals(result.size(), 2);
        WiringData wiringDataEqIn = result.get(0);
        assertEquals(wiringDataEqIn.getHWEQN(), eqInBuilder.get().getHwEQN());
        assertEquals(wiringDataEqIn.getPanelPin1(), "B L1 S1");
        assertEquals(wiringDataEqIn.getPanelPin2(), "B L2 S2");
        assertEquals(wiringDataEqIn.getLayer2Protocol(), Schicht2Protokoll.ATM.name());
        assertEquals(wiringDataEqIn.getDeviceName(), hwRack.getGeraeteBez());
        assertEquals(wiringDataEqIn.getManagementDescription(), hwRack.getManagementBez());
        assertEquals(wiringDataEqIn.getModuleType(), hwBgTypBuilder.get().getName());
        assertEquals(wiringDataEqIn.getPhysicType(), physikTypBuilder.get().getName());
        assertEquals(wiringDataEqIn.getONTID(), isOnt ? ((HWOnt) rackBuilder.get()).getSerialNo() : null);
        assertEquals(wiringDataEqIn.getChassisIdentifier(), !isOnt && "dpo-management-bez".equals(wiringDataEqIn.getManagementDescription()) ? ((HWDpo) rackBuilder.get()).getChassisIdentifier() : null);
        assertEquals(wiringDataEqIn.getChassisSlot(), !isOnt && "dpo-management-bez".equals(wiringDataEqIn.getManagementDescription()) ? ((HWDpo) rackBuilder.get()).getChassisSlot() : null);
        assertEquals(wiringDataEqIn.getType(), "EQ-In");
        //TODO HUR-27351 PSE asserten

        WiringData wiringDataEqOut = result.get(1);
        assertEquals(wiringDataEqOut.getHWEQN(), eqOutBuilder.get().getHwEQN());
        assertEquals(wiringDataEqOut.getDistributor(), eqOutBuilder.get().getRangVerteiler());
        assertEquals(wiringDataEqOut.getPanelPin1(), "13 65");
        assertEquals(wiringDataEqOut.getType(), "EQ-Out");
        Assert.assertNull(wiringDataEqOut.getONTID());
    }

    @Test
    public void testCreateKreuzungData() throws Exception {
        prepareFfmCommand(testling);
        final WiringDataStore wiringDataStore = new WiringDataStore();
        wiringDataStore.eqIn = true;
        wiringDataStore.rangierung = new RangierungBuilder()
                .withRandomId()
                .build();

        final EquipmentBuilder eqIn = new EquipmentBuilder()
                .withRandomId()
                .withRangBucht("B")
                .withRangLeiste1("L1")
                .withRangStift1("S1")
                .withHwEQN("9-8-07-006");
        final PhysikTypBuilder physikTyp = new PhysikTypBuilder()
                .withRandomId()
                .withNameRandomSuffix("asdf");
        final Rangierung rangierungForKreuzung = new RangierungBuilder()
                .withRandomId()
                .withPhysikTypBuilder(physikTyp)
                .withEqInBuilder(eqIn)
                .build();
        final PhysikUebernahme physikUebernahme = new PhysikUebernahmeBuilder()
                .withAenderungstyp(PhysikaenderungsTyp.STRATEGY_DSL_KREUZUNG)
                .build();
        rangierungForKreuzung.setEquipmentIn(eqIn.get());

        doReturn(Optional.of(new VerlaufBuilder().build())).when(testling).getBauauftrag();
        when(physikService.findPhysikUebernahme4Verlauf(anyLong(), anyLong())).thenReturn(physikUebernahme);
        when(rangierungsService.findKreuzung(wiringDataStore.rangierung.getId(), !wiringDataStore.eqIn))
                .thenReturn(rangierungForKreuzung);
        when(physikService.findPhysikTyp(rangierungForKreuzung.getPhysikTypId())).thenReturn(physikTyp.get());

        Optional<CrossingBuilder> result = testling.createCrossingData(wiringDataStore, 1L);

        final WiringData.Crossing kreuzung = result.get().build();
        assertThat(kreuzung.getDistributor(), equalTo(eqIn.get().getRangVerteiler()));
        assertThat(kreuzung.getHWEQN(), equalTo(eqIn.get().getHwEQN()));
        assertThat(kreuzung.getPanelPin1(), equalTo("B L1 S1"));
        assertThat(kreuzung.getPanelPin2(), nullValue(String.class));
        assertThat(kreuzung.getPhysicType(), equalTo(physikTyp.get().getName()));
    }

    @Test
    public void testLoadHardwareDataWithNull() throws FindException {
        List<WiringDataStore> result = testling.loadHardwareData(null);
        assertEmpty(result);
    }


    @Test
    public void testLoadHardwareData() throws FindException {
        Endstelle endstelle = new EndstelleBuilder().setPersist(false).build();
        endstelle.setRangierId(1L);
        endstelle.setRangierIdAdditional(2L);

        doReturn(new WiringDataStore()).when(testling).loadHardwareData4Equipment(anyLong(), anyBoolean(), anyString());

        List<WiringDataStore> result = testling.loadHardwareData(endstelle);
        assertNotEmpty(result);
        assertEquals(result.size(), 4);

        verify(testling).loadHardwareData4Equipment(1L, true, "EQ-In");
        verify(testling).loadHardwareData4Equipment(1L, false, "EQ-Out");
        verify(testling).loadHardwareData4Equipment(2L, true, "EQ-In2");
        verify(testling).loadHardwareData4Equipment(2L, false, "EQ-Out2");
    }

}
