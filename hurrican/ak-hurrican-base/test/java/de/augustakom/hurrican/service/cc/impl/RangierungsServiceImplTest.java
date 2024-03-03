/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2010 15:34:27
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import javax.annotation.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.util.CollectionUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.dao.cc.RangierungDAO;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationBuilder;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.view.RangierungsEquipmentView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;

/**
 * TestNG Klasse fuer {@link RangierungsServiceImpl}
 */
@Test(groups = BaseTest.UNIT)
public class RangierungsServiceImplTest extends BaseTest {

    private static final String KVZ_NUMMER = "A031";
    private static final String KVZ_NUMMER_ALT = "A032";

    @Spy
    private RangierungsServiceImpl cut = new RangierungsServiceImpl();
    @Mock
    private HVTService hvtServiceMock;
    @Mock
    private AvailabilityService availabilityServiceMock;
    @Mock
    private EquipmentDAO equipmentDaoMock;
    @Mock
    private HWService hwServiceMock;
    @Mock
    private PhysikService physikServiceMock;
    @Mock
    private RangierungDAO rangierungDaoMock;
    @Mock
    private CCAuftragService auftragServiceMock;
    @Mock
    EndstellenService endstellenServiceMock;

    private HVTStandort hvtStandort;
    private GeoId geoId;
    private GeoId2TechLocation geoId2TechLocation;
    private Rangierung rangierung;
    private Rangierung rangierungAlt;
    private Equipment eqOut;
    private Equipment eqOutAlt;
    private Endstelle endstelle;

    @BeforeMethod
    public void prepareTest() throws FindException {
        MockitoAnnotations.initMocks(this);

        HVTGruppeBuilder hvtGruppeBuilder = new HVTGruppeBuilder();
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder()
                .withRandomId()
                .withHvtGruppeBuilder(hvtGruppeBuilder)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)
                .setPersist(false);
        hvtStandort = hvtStandortBuilder.build();

        GeoIdBuilder geoIdBuilder = new GeoIdBuilder()
                .setPersist(false);
        geoId = geoIdBuilder.build();
        geoId2TechLocation = new GeoId2TechLocationBuilder()
                .withGeoIdBuilder(geoIdBuilder)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withKvzNumber(KVZ_NUMMER)
                .build();

        endstelle = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withGeoIdBuilder(geoIdBuilder)
                .setPersist(false).build();

        EquipmentBuilder eqOutBuilder = new EquipmentBuilder()
                .withRandomId()
                .withKvzNummer(KVZ_NUMMER)
                .setPersist(false);
        eqOut = eqOutBuilder.build();

        rangierung = new RangierungBuilder()
                .withEqOutBuilder(eqOutBuilder)
                .withEqInBuilder(new EquipmentBuilder())
                .withHvtStandortBuilder(hvtStandortBuilder)
                .setPersist(false).build();

        EquipmentBuilder eqOutBuilderAlt = new EquipmentBuilder()
                .withRandomId()
                .withKvzNummer(KVZ_NUMMER_ALT)
                .setPersist(false);
        eqOutAlt = eqOutBuilderAlt.build();

        rangierungAlt = new RangierungBuilder()
                .withEqOutBuilder(eqOutBuilderAlt)
                .withEqInBuilder(new EquipmentBuilder())
                .withHvtStandortBuilder(hvtStandortBuilder)
                .setPersist(false).build();

        cut.setHvtService(hvtServiceMock);
        when(hvtServiceMock.findHVTStandort(hvtStandort.getId())).thenReturn(hvtStandort);

        cut.setEquipmentDAO(equipmentDaoMock);
        when(equipmentDaoMock.findById(eqOut.getId(), Equipment.class)).thenReturn(eqOut);

        cut.setAvailabilityService(availabilityServiceMock);
        when(availabilityServiceMock.findGeoId(geoId.getId())).thenReturn(geoId);
        when(availabilityServiceMock.findGeoId2TechLocation(geoId.getId(), endstelle.getHvtIdStandort())).thenReturn(
                geoId2TechLocation);

        cut.setHwService(hwServiceMock);
        cut.setPhysikService(physikServiceMock);
        cut.setRangierungDAO(rangierungDaoMock);
        cut.setCcAuftragService(auftragServiceMock);
        cut.setEndstellenService(endstellenServiceMock);
    }

    public void testDoesKvzNrMatch() throws FindException {
        boolean result = cut.doesKvzNrMatch(rangierung, endstelle);
        assertTrue(result);
    }

    public void testDoesKvzNrMatchNoKvz() throws FindException {
        hvtStandort.setStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT);
        boolean result = cut.doesKvzNrMatch(rangierung, endstelle);
        assertTrue(result);
    }

    @Test(expectedExceptions = FindException.class)
    public void testDoesKvzNrMatchGeoId2TechLocationWithoutKvz() throws FindException {
        geoId2TechLocation.setKvzNumber(null);
        cut.doesKvzNrMatch(rangierung, endstelle);
    }

    public void testDoesKvzNrMatchEqOutWithoutKvz() throws FindException {
        eqOut.setKvzNummer(null);
        boolean result = cut.doesKvzNrMatch(rangierung, endstelle);
        assertFalse(result);
    }

    @Test(expectedExceptions = FindException.class)
    public void testDoesKvzNrNoEqOut() throws FindException {
        when(equipmentDaoMock.findById(eqOut.getId(), Equipment.class)).thenReturn(null);
        cut.doesKvzNrMatch(rangierung, endstelle);
    }

    @DataProvider
    public Object[][] portGenerator() {
        EquipmentBuilder portBuilder = new EquipmentBuilder();

        HWBaugruppeBuilder hwBaugruppeBuilder1 = new HWBaugruppeBuilder();
        HWDslamBuilder dslamBuilder = new HWDslamBuilder();
        dslamBuilder.withRandomId().setPersist(false).build();
        HWBaugruppe baugruppe1 = hwBaugruppeBuilder1.withRandomId().withBaugruppenTypBuilder(
                new HWBaugruppenTypBuilder().withRandomId().withHvtTechnikBuilder(
                        new HVTTechnikBuilder().withId(HVTTechnik.ALCATEL).setPersist(false))
                        .setPersist(false)
        )
                .withRackBuilder(dslamBuilder).setPersist(false).build();

        Equipment port1 = portBuilder.withRandomId().withHwEQN("1-1-1-1").withBaugruppeBuilder(hwBaugruppeBuilder1)
                .setPersist(false).build();
        Equipment port2 = portBuilder.withRandomId().withHwEQN("1-1-1-2").withBaugruppeBuilder(hwBaugruppeBuilder1)
                .setPersist(false).build();
        Equipment port3 = portBuilder.withRandomId().withHwEQN("1-1-1-3").withBaugruppeBuilder(hwBaugruppeBuilder1)
                .setPersist(false).build();
        Equipment port4 = portBuilder.withRandomId().withHwEQN("1-1-1-4").withBaugruppeBuilder(hwBaugruppeBuilder1)
                .setPersist(false).build();

        Equipment port11 = portBuilder.withRandomId().withHwEQN("1-1-1-05").withBaugruppeBuilder(hwBaugruppeBuilder1)
                .setPersist(false).build();
        Equipment port12 = portBuilder.withRandomId().withHwEQN("1-1-1-06").withBaugruppeBuilder(hwBaugruppeBuilder1)
                .setPersist(false).build();
        Equipment port13 = portBuilder.withRandomId().withHwEQN("1-1-1-07").withBaugruppeBuilder(hwBaugruppeBuilder1)
                .setPersist(false).build();
        Equipment port14 = portBuilder.withRandomId().withHwEQN("1-1-1-08").withBaugruppeBuilder(hwBaugruppeBuilder1)
                .setPersist(false).build();

        HWBaugruppeBuilder hwBaugruppeBuilder2 = new HWBaugruppeBuilder();
        HWBaugruppe baugruppe2 = hwBaugruppeBuilder2.withRandomId().withBaugruppenTypBuilder(
                new HWBaugruppenTypBuilder().withRandomId().withHvtTechnikBuilder(
                        new HVTTechnikBuilder().withId(HVTTechnik.HUAWEI).setPersist(false))
                        .setPersist(false)
        )
                .withRackBuilder(dslamBuilder).setPersist(false).build();

        Equipment port5 = portBuilder.withRandomId().withHwEQN("U05-3-014-01")
                .withBaugruppeBuilder(hwBaugruppeBuilder2).setPersist(false).build();
        Equipment port6 = portBuilder.withRandomId().withHwEQN("U05-3-014-02")
                .withBaugruppeBuilder(hwBaugruppeBuilder2).setPersist(false).build();
        Equipment port7 = portBuilder.withRandomId().withHwEQN("U05-3-014-03")
                .withBaugruppeBuilder(hwBaugruppeBuilder2).setPersist(false).build();
        Equipment port8 = portBuilder.withRandomId().withHwEQN("U05-3-014-04")
                .withBaugruppeBuilder(hwBaugruppeBuilder2).setPersist(false).build();

        return new Object[][] {
                new Object[] { port1, port2, port3, port4, baugruppe1 },
                new Object[] { port11, port12, port13, port14, baugruppe1 },
                new Object[] { port5, port6, port7, port8, baugruppe2 },
        };
    }

    @Test(dataProvider = "portGenerator")
    public void testFindConsecutivePortsSucess(Equipment firstPort, Equipment port2, Equipment port3, Equipment port4,
            HWBaugruppe baugruppe) throws Exception {
        when(hwServiceMock.findBaugruppe(firstPort.getHwBaugruppenId())).thenReturn(baugruppe);

        when(equipmentDaoMock.findEquipment(baugruppe.getRackId(), port2.getHwEQN(), firstPort.getRangSSType()))
                .thenReturn(Arrays.asList(port2));
        when(equipmentDaoMock.findEquipment(baugruppe.getRackId(), port3.getHwEQN(), firstPort.getRangSSType()))
                .thenReturn(Arrays.asList(port3));
        when(equipmentDaoMock.findEquipment(baugruppe.getRackId(), port4.getHwEQN(), firstPort.getRangSSType()))
                .thenReturn(Arrays.asList(port4));

        List<Equipment> result = cut.findConsecutivePorts(firstPort, 4);
        assertNotNull(result);
        assertEquals(result.size(), 4);
    }

    @Test(dataProvider = "portGenerator")
    public void testFindConsecutivePortsFail(Equipment firstPort, Equipment port2, Equipment port3, Equipment port4,
            HWBaugruppe baugruppe) throws Exception {
        when(hwServiceMock.findBaugruppe(firstPort.getHwBaugruppenId())).thenReturn(baugruppe);

        when(equipmentDaoMock.findEquipment(baugruppe.getRackId(), port2.getHwEQN(), firstPort.getRangSSType()))
                .thenReturn(Arrays.asList(port2));
        when(equipmentDaoMock.findEquipment(baugruppe.getRackId(), port3.getHwEQN(), firstPort.getRangSSType()))
                .thenReturn(Arrays.asList(port3));
        when(equipmentDaoMock.findEquipment(baugruppe.getRackId(), port4.getHwEQN(), firstPort.getRangSSType()))
                .thenReturn(null);

        try {
            cut.findConsecutivePorts(firstPort, 4);
        }
        catch (FindException e) {
            return;
        }
        fail("Da die Ermittlung der Ports fehlschlaegt, muss eine Exception fliegen!");
    }

    @DataProvider
    public Object[][] dataProviderFindConsecutiveUEVTStifteSuccess() {
        Equipment stift1 = new Equipment();
        stift1.setRangStift1("01");
        Equipment stift2 = new Equipment();
        stift2.setRangStift1("02");
        Equipment stift3 = new Equipment();
        stift3.setRangStift1("03");
        // @formatter:off
        return new Object[][] {
                //ersterStift
                //        anzahl
                //           stifte
                { stift1, 1, null},
                { stift1, 2, new Equipment[] {stift2}},
                { stift1, 3, new Equipment[] {stift2, stift3}},
                { stift2, 2, new Equipment[] {stift3}},
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderFindConsecutiveUEVTStifteSuccess")
    public void testFindConsecutiveUEVTStifteSuccess(@Nonnull Equipment ersterStift, int anzahl, Equipment[] stifte)
            throws FindException {
        doReturn(ersterStift).when(cut).findEQByVerteilerLeisteStift(any(Long.class), any(String.class),
                any(String.class), eq((ersterStift != null) ? ersterStift.getRangStift1() : null));
        if ((stifte != null) && (stifte.length > 0)) {
            for (Equipment stift : stifte) {
                doReturn(ersterStift).when(cut).findEQByVerteilerLeisteStift(any(Long.class), any(String.class),
                        any(String.class), eq((stift != null) ? stift.getRangStift1() : null));
            }
        }
        List<Equipment> result = cut.findConsecutiveUEVTStifte(ersterStift, anzahl);
        assertNotNull(result);
        assertEquals(result.size(), anzahl);
        assertThat(result.get(0), equalTo(ersterStift));
    }

    @DataProvider
    public Object[][] dataProviderFindConsecutiveUEVTStifteFail() {
        Equipment stift1 = new Equipment();
        stift1.setRangStift1("01");
        Equipment stift2 = new Equipment();
        stift2.setRangStift1("02");
        Equipment stift2Invalid = new Equipment();
        stift2Invalid.setRangStift1("2");
        Equipment stift3 = new Equipment();
        stift3.setRangStift1("03");
        // @formatter:off
        return new Object[][] {
                //ersterStift
                //        anzahl
                //           stifte
                { stift1,  0, null},
                { stift1, -1, null},
                { stift1,  2, null},
                { stift1,  2, new Equipment[] {stift2Invalid}},
                { stift1,  2, new Equipment[] {stift3}},
                { stift1,  3, new Equipment[] {stift3}},
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderFindConsecutiveUEVTStifteFail")
    public void testFindConsecutiveUEVTStifteFail(@Nonnull Equipment ersterStift, int anzahl, Equipment[] stifte)
            throws FindException {
        doReturn(ersterStift).when(cut).findEQByVerteilerLeisteStift(any(Long.class), any(String.class),
                any(String.class), eq((ersterStift != null) ? ersterStift.getRangStift1() : null));
        if ((stifte != null) && (stifte.length > 0)) {
            for (Equipment stift : stifte) {
                doReturn(ersterStift).when(cut).findEQByVerteilerLeisteStift(any(Long.class), any(String.class),
                        any(String.class), eq((stift != null) ? stift.getRangStift1() : null));
            }
        }

        try {
            cut.findConsecutiveUEVTStifte(ersterStift, anzahl);
        }
        catch (FindException e) {
            return;
        }
        catch (NullPointerException e) {
            return;
        }
        catch (IllegalArgumentException e) {
            return;
        }
        fail("Da die Ermittlung der Ports fehlschlaegt, muss eine Exception fliegen!");
    }

    @DataProvider
    public Object[][] dataProviderFilterAndSortRangierungenByMaxBandwidth() {
        Rangierung eins = new Rangierung();
        eins.setId(1L);
        Rangierung zwei = new Rangierung();
        zwei.setId(2L);
        // @formatter:off
        return new Object[][] {
                //Integer downstreamBandwidth,
                //                      List<Rangierung> in,      List<Integer> bandwidths4Rangierung,                         List<Rangierung> expectedOrder
                { null,                 Collections.emptyList(),  null,                                                     null                     },
                { null,                 Arrays.asList(eins,zwei), Arrays.asList((Integer)null, (Integer)null)                   , Arrays.asList(eins,zwei) },
                { null,                 Arrays.asList(eins,zwei), Arrays.asList(Integer.valueOf(6000), (Integer)null)          , Arrays.asList(eins,zwei) },
                { null,                 Arrays.asList(eins,zwei), Arrays.asList((Integer)null, Integer.valueOf(6000))          , Arrays.asList(zwei,eins) },
                { null,                 Arrays.asList(eins,zwei), Arrays.asList(Integer.valueOf(6000), Integer.valueOf(6000)) , Arrays.asList(eins,zwei) },
                { null,                 Arrays.asList(eins,zwei), Arrays.asList(Integer.valueOf(18000), Integer.valueOf(6000)), Arrays.asList(zwei,eins) },
                { null,                 Arrays.asList(eins,zwei), Arrays.asList(Integer.valueOf(6000), Integer.valueOf(18000)), Arrays.asList(eins,zwei) },
                { Integer.valueOf(18000), Arrays.asList(eins,zwei), Arrays.asList(Integer.valueOf(18000), Integer.valueOf(6000)), Arrays.asList(eins)      },
                { Integer.valueOf(18000), Arrays.asList(eins,zwei), Arrays.asList(Integer.valueOf(6000), Integer.valueOf(18000)), Arrays.asList(zwei)      },
                { Integer.valueOf(18000), Arrays.asList(eins,zwei), Arrays.asList(Integer.valueOf(6000), Integer.valueOf(6000)) , null                     },
                { Integer.valueOf(18000), Arrays.asList(eins,zwei), Arrays.asList((Integer)null, Integer.valueOf(18000))         , Arrays.asList(zwei, eins)},
                { Integer.valueOf(18000), Arrays.asList(eins,zwei), Arrays.asList((Integer)null, Integer.valueOf(6000))          , Arrays.asList(eins)      },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderFilterAndSortRangierungenByMaxBandwidth")
    public void testFilterAndSortRangierungenByMaxBandwidth(Integer downstreamBandwidth, List<Rangierung> in,
            List<Integer> bandwidths4Rangierung, List<Rangierung> expectedOrder) throws FindException,
            ServiceNotFoundException {
        if (!CollectionUtils.isEmpty(bandwidths4Rangierung)) {
            for (int i = 0; i < bandwidths4Rangierung.size(); i++) {
                doReturn(Bandwidth.create(bandwidths4Rangierung.get(i))).when(cut).getMaxBandwidth4Rangierung(eq(in.get(i)));
            }
        }
        else {
            doReturn(null).when(cut).getMaxBandwidth4Rangierung(any(Rangierung.class));
        }

        Bandwidth bandwidth = Bandwidth.create(downstreamBandwidth);
        List<Rangierung> result = cut.filterAndSortRangierungenByMaxBandwidth(in, bandwidth);

        if (!CollectionUtils.isEmpty(expectedOrder)) {
            for (int i = 0; i < expectedOrder.size(); i++) {
                assertTrue(NumberTools.equal(expectedOrder.get(i).getId(), result.get(i).getId()));
            }
        }
        else {
            assertTrue(CollectionUtils.isEmpty(result));
        }
    }

    @DataProvider
    public Object[][] dataProviderGetBandwidth4Rangierung() {
        Rangierung eins = new Rangierung();
        Rangierung zwei = new Rangierung();
        zwei.setPhysikTypId(1L);
        Rangierung drei = new Rangierung();
        drei.setEqInId(2L);
        Rangierung vier = new Rangierung();
        vier.setPhysikTypId(3L);
        vier.setEqInId(3L);
        // @formatter:off
        return new Object[][] {
                //Rangierung in,
                //      Integer maxBandwidthPT,  Integer maxBandwidthBGT, boolean ptIsNull,
                //                                                         boolean eqIsNull,
                //                                                                boolean bgIsNull,
                //                                                                       boolean bgtIsNull,
                //                                                                              Integer expectedBandwidth
                { eins, null,                 null,                 true,  true,  true,  true,  null                 },
                { zwei, null,                 null,                 true,  true,  true,  true,  null                 },
                { zwei, null,                 null,                 false, true,  true,  true,  null                 },
                { zwei, Integer.valueOf(18000), null,                 false, true,  true,  true,  Integer.valueOf(18000) },
                { drei, null,                 null,                 true,  true,  true,  true,  null                 },
                { drei, null,                 Integer.valueOf(18000), true,  false, true,  true,  null                 },
                { drei, null,                 Integer.valueOf(18000), true,  false, false, true,  null                 },
                { drei, null,                 null,                 true,  false, false, false, null                 },
                { drei, null,                 Integer.valueOf(18000), true,  false, false, false, Integer.valueOf(18000) },
                { vier, null,                 null,                 true,  true,  true,  true,  null                 },
                { vier, null,                 Integer.valueOf(18000), false, true,  true,  true,  null                 },
                { vier, null,                 Integer.valueOf(18000), false, false, true,  true,  null                 },
                { vier, null,                 Integer.valueOf(18000), false, false, false, true,  null                 },
                { vier, null,                 null,                 false, false, false, false, null                 },
                { vier, Integer.valueOf(18000), null,                 false, false, false, false, Integer.valueOf(18000) },
                { vier, null,                 Integer.valueOf(18000), false, false, false, false, Integer.valueOf(18000) },
                { vier, Integer.valueOf(18000), Integer.valueOf(18000), false, false, false, false, Integer.valueOf(18000) },
                { vier, Integer.valueOf(6000),  Integer.valueOf(18000), false, false, false, false, Integer.valueOf(6000)  },
                { vier, Integer.valueOf(18000), Integer.valueOf(6000),  false, false, false, false, Integer.valueOf(6000)  },
        };
        // @formatter:on
    }

    Bandwidth bandwidth(Integer downstream) {
        return Bandwidth.create(downstream);
    }

    @Test(dataProvider = "dataProviderGetBandwidth4Rangierung")
    public void testGetBandwidth4Rangierung(Rangierung in, Integer maxBandwidthPT, Integer maxBandwidthBGT, boolean ptIsNull,
            boolean eqIsNull, boolean bgIsNull, boolean bgtIsNull, Integer expectedBandwidth) throws FindException,
            ServiceNotFoundException {
        PhysikTyp physikTyp = (ptIsNull) ? null : new PhysikTyp();
        if (physikTyp != null) {
            physikTyp.setMaxBandwidth(Bandwidth.create(maxBandwidthPT));
        }
        when(physikServiceMock.findPhysikTyp(any(Long.class))).thenReturn(physikTyp);
        Equipment equipment = (eqIsNull) ? null : new Equipment();
        if (equipment != null) {
            equipment.setHwBaugruppenId(1L);
        }
        doReturn(equipment).when(cut).findEquipment(any(Long.class));
        HWBaugruppe hwBaugruppe = (bgIsNull) ? null : new HWBaugruppe();
        HWBaugruppenTyp hwBaugruppenTyp = (bgtIsNull) ? null : new HWBaugruppenTyp();
        if (hwBaugruppenTyp != null) {
            hwBaugruppenTyp.setMaxBandwidth(Bandwidth.create(maxBandwidthBGT));
        }
        if (hwBaugruppe != null) {
            hwBaugruppe.setHwBaugruppenTyp(hwBaugruppenTyp);
        }
        when(hwServiceMock.findBaugruppe(any(Long.class))).thenReturn(hwBaugruppe);

        Bandwidth result = cut.getMaxBandwidth4Rangierung(in);

        assertThat(result, equalTo(bandwidth(expectedBandwidth)));
    }

    @DataProvider
    public Object[][] dataProviderIsBandwidthPossible4Rangierung() {
        // @formatter:off
        return new Object[][] {
                //Long requiredDownstreamBandwidth,
                //                     Integer maxConfiguredBandwidth,
                //                                          boolean expectedResult
                { null,                null,                true  },
                { Integer.valueOf(16000), null,                true  },
                { Integer.valueOf(16000), Integer.valueOf(16000), true  },
                { Integer.valueOf(16000), Integer.valueOf(18000), true  },
                { Integer.valueOf(16000), Integer.valueOf(6000),  false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderIsBandwidthPossible4Rangierung")
    public void testIsBandwidthPossible4Rangierung(Integer requiredDownstreamBandwidth, Integer maxConfiguredBandwidth,
            boolean expectedResult) throws FindException, ServiceNotFoundException {
        doReturn(bandwidth(maxConfiguredBandwidth)).when(cut).getMaxBandwidth4Rangierung(any(Rangierung.class));

        boolean result = cut.isBandwidthPossible4Rangierung(new Rangierung(), bandwidth(requiredDownstreamBandwidth));

        assertTrue(result == expectedResult);
    }

    @DataProvider
    public Object[][] dataProviderCreateRangierungsEquipmentViewSuccess() {
        // @formatter:off
        return new Object[][] {
                //Long kvzNummer
                { null       },
                { KVZ_NUMMER },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderCreateRangierungsEquipmentViewSuccess")
    public void testCreateRangierungsEquipmentViewSuccess(String kvzNummer) throws FindException {
        doReturn(eqOut).when(cut).findEquipment(rangierung.getEqOutId());
        doReturn(null).when(cut).findEquipment(rangierung.getEqInId());
        when(physikServiceMock.findPhysikTyp(any(Long.class))).thenReturn(null);
        when(hwServiceMock.findBaugruppe(any(Long.class))).thenReturn(null);

        List<RangierungsEquipmentView> result = cut
                .createRangierungsEquipmentView(Arrays.asList(rangierung), kvzNummer);

        assertNotNull(result);
        assertTrue(result.size() == 1);
        assertTrue(NumberTools.equal(result.get(0).getRangierId(), rangierung.getId()));
    }

    public void testCreateRangierungsEquipmentViewFiltered() throws FindException {
        doReturn(eqOut).when(cut).findEquipment(rangierung.getEqOutId());
        doReturn(null).when(cut).findEquipment(rangierung.getEqInId());
        doReturn(eqOutAlt).when(cut).findEquipment(rangierungAlt.getEqOutId());
        doReturn(null).when(cut).findEquipment(rangierungAlt.getEqInId());
        when(physikServiceMock.findPhysikTyp(any(Long.class))).thenReturn(null);
        when(hwServiceMock.findBaugruppe(any(Long.class))).thenReturn(null);

        List<RangierungsEquipmentView> result = cut.createRangierungsEquipmentView(
                Arrays.asList(rangierung, rangierungAlt), KVZ_NUMMER);

        assertNotNull(result);
        assertTrue(result.size() == 1);
        assertTrue(NumberTools.equal(result.get(0).getRangierId(), rangierung.getId()));
    }
}
