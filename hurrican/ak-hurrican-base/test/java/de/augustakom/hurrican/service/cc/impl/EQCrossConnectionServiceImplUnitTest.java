package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.dao.cc.EQCrossConnectionDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.BrasPoolBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWSubrackBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;

@Test(groups = { BaseTest.UNIT })
public class EQCrossConnectionServiceImplUnitTest extends BaseTest {
    @Mock
    private EQCrossConnectionDAO dao;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private ProduktService produktService;
    @Mock
    private FeatureService featureService;
    @Mock
    private HWService hwService;

    @Spy
    @InjectMocks
    private EQCrossConnectionServiceImpl sut;

    private BrasPoolBuilder brasPoolBuilder;
    private List<Integer> used;

    @BeforeMethod
    public void setUp() {
        //sut = new EQCrossConnectionServiceImpl();
        initMocks(this);
        used = new ArrayList<Integer>();
        used.add(50);
        used.add(51);
        used.add(52);
        used.add(55);
        brasPoolBuilder = new BrasPoolBuilder().withVp(5);
    }

    public void testGetVcFromPool() throws FindException {
        when(dao.findUsedBrasVcs(brasPoolBuilder.get())).thenReturn(used);
        Integer vc = sut.getVcFromPool(brasPoolBuilder.get());
        assertEquals(vc, Integer.valueOf(53));
    }

    public void testGetVcFromPoolExhausted() throws FindException {
        brasPoolBuilder.withVcMax(52);
        when(dao.findUsedBrasVcs(brasPoolBuilder.get())).thenReturn(used);
        Integer vc = sut.getVcFromPool(brasPoolBuilder.get());
        assertNull(vc);
    }

    public void testGetVcFromPoolMin() throws FindException {
        used.clear();
        when(dao.findUsedBrasVcs(brasPoolBuilder.get())).thenReturn(used);
        Integer vc = sut.getVcFromPool(brasPoolBuilder.get());
        assertEquals(vc, Integer.valueOf(50));
    }

    public void testGetVcFromPoolMax() throws FindException {
        used.add(53);
        used.add(54);
        brasPoolBuilder.withVcMax(56);
        when(dao.findUsedBrasVcs(brasPoolBuilder.get())).thenReturn(used);
        Integer vc = sut.getVcFromPool(brasPoolBuilder.get());
        assertEquals(vc, Integer.valueOf(56));
    }

    public void testIsCrossConnectionEnabled() throws FindException {
        HWDslamBuilder siemensDslam = new HWDslamBuilder().withHwProducerBuilder(new HVTTechnikBuilder().toSiemens())
                .withId(HVTTechnik.SIEMENS).setPersist(false);
        HWDslamBuilder huaweiDslam = new HWDslamBuilder().withHwProducerBuilder(new HVTTechnikBuilder().toHuawei())
                .withId(HVTTechnik.HUAWEI).setPersist(false);
        HWBaugruppeBuilder bgSiemensWithSubrack = new HWBaugruppeBuilder()
                .withSubrackBuilder(new HWSubrackBuilder().withRandomId()).withId(1L)
                .withRackBuilder(siemensDslam)
                .withBaugruppenTypBuilder(new HWBaugruppenTypBuilder())
                .setPersist(false);
        HWBaugruppeBuilder bgSiemensNoSubrack = new HWBaugruppeBuilder()
                .withId(2L)
                .withRackBuilder(siemensDslam)
                .withBaugruppenTypBuilder(new HWBaugruppenTypBuilder())
                .setPersist(false);
        HWBaugruppeBuilder bgHuaweiWithSubrack = new HWBaugruppeBuilder()
                .withSubrackBuilder(new HWSubrackBuilder().withRandomId()).withId(11L)
                .withRackBuilder(huaweiDslam)
                .withBaugruppenTypBuilder(new HWBaugruppenTypBuilder())
                .setPersist(false);
        HWBaugruppeBuilder bgHuaweiNoSubrack = new HWBaugruppeBuilder().withId(12L).withRackBuilder(huaweiDslam)
                .withBaugruppenTypBuilder(new HWBaugruppenTypBuilder())
                .setPersist(false);
        HWBaugruppeBuilder bgWithTunnelingVlan = new HWBaugruppeBuilder()
                .withId(13L)
                .withBaugruppenTypBuilder(new HWBaugruppenTypBuilder().withTunneling(HWBaugruppenTyp.Tunneling.VLAN))
                .setPersist(false);

        when(hwService.findBaugruppe(Mockito.eq(1L))).thenReturn(bgSiemensWithSubrack.get());
        when(hwService.findBaugruppe(Mockito.eq(2L))).thenReturn(bgSiemensNoSubrack.get());
        when(hwService.findBaugruppe(Mockito.eq(11L))).thenReturn(bgHuaweiWithSubrack.get());
        when(hwService.findBaugruppe(Mockito.eq(12L))).thenReturn(bgHuaweiNoSubrack.get());
        when(hwService.findBaugruppe(Mockito.eq(13L))).thenReturn(bgWithTunnelingVlan.get());

        when(hwService.findRackById(Mockito.eq(HVTTechnik.SIEMENS))).thenReturn(siemensDslam.get());
        when(hwService.findRackById(Mockito.eq(HVTTechnik.HUAWEI))).thenReturn(huaweiDslam.get());

        assertFalse(sut.isCrossConnectionEnabled(new EquipmentBuilder().withBaugruppeBuilder(bgSiemensWithSubrack)
                .build()));
        assertFalse(sut.isCrossConnectionEnabled(new EquipmentBuilder().withBaugruppeBuilder(bgSiemensNoSubrack)
                .build()));
        assertFalse(sut.isCrossConnectionEnabled(new EquipmentBuilder().withBaugruppeBuilder(bgWithTunnelingVlan)
                .build()));
        assertTrue(sut.isCrossConnectionEnabled(new EquipmentBuilder().withBaugruppeBuilder(bgHuaweiWithSubrack)
                .build()));
        assertTrue(sut.isCrossConnectionEnabled(new EquipmentBuilder().withBaugruppeBuilder(bgHuaweiNoSubrack)
                .build()));
    }

    @DataProvider
    public Object[][] findCcPhysicsByEndstelleDP() {
        Rangierung rangierung = new RangierungBuilder().setPersist(false).build();
        Equipment eqInPort = new EquipmentBuilder().setPersist(false).build();
        return new Object[][] {
                { null, null },
                { rangierung, null },
                { rangierung, eqInPort },
        };
    }

    @Test(dataProvider = "findCcPhysicsByEndstelleDP")
    public void testFindCcPhysicsByEndstelle(Rangierung rangierung, Equipment eqInPort) throws FindException {
        Endstelle endstelle = new EndstelleBuilder().withRangierungBuilder(new RangierungBuilder().withRandomId())
                .build();
        when(rangierungsService.findRangierung(anyLong())).thenReturn(rangierung);
        when(rangierungsService.findEquipment(anyLong())).thenReturn(eqInPort);

        Pair<Rangierung, Equipment> result = sut.findCcPhysicsByEndstelle(endstelle);

        assertNotNull(result);
        if (rangierung == null) {
            assertNull(result.getFirst(), "Rangierung muss 'null' sein");
            assertNull(result.getSecond(), "Port muss 'null' sein");
        }
        else if (eqInPort == null) {
            assertNotNull(result.getFirst(), "Rangierung darf nicht 'null' sein");
            assertNull(result.getSecond(), "Port muss 'null' sein");
        }
        else {
            assertNotNull(result.getFirst(), "Rangierung darf nicht 'null' sein");
            assertNotNull(result.getSecond(), "Port darf nicht 'null' sein");
        }
    }

    @DataProvider
    public Object[][] deactivateCrossConnections4EndstelleDP() {
        Rangierung rangierung = new RangierungBuilder().setPersist(false).build();
        Equipment eqInPort = new EquipmentBuilder().setPersist(false).build();
        return new Object[][] {
                { Pair.create(null, null) },
                { Pair.create(rangierung, null) },
                { Pair.create(rangierung, eqInPort) },
        };
    }

    @Test(dataProvider = "deactivateCrossConnections4EndstelleDP")
    public void testDeactivateCrossConnections4Endstelle(Pair<Rangierung, Equipment> ccPhysics) throws FindException, StoreException {
        Endstelle endstelle = new EndstelleBuilder().withRangierungBuilder(new RangierungBuilder().withRandomId())
                .build();
        Date now = new Date();
        doReturn(ccPhysics).when(sut).findCcPhysicsByEndstelle(any(Endstelle.class));
        doReturn(Collections.emptyList()).when(sut).findEQCrossConnections(anyLong(), any(Date.class));

        sut.deactivateCrossConnections4Endstelle(endstelle, now);

        if (ccPhysics.getSecond() != null) {
            verify(sut, times(1)).findEQCrossConnections(anyLong(), any(Date.class));
            verify(sut, times(1)).deactivateCrossConnections(any(List.class), any(Date.class));
        }
        else {
            verify(sut, times(0)).findEQCrossConnections(anyLong(), any(Date.class));
            verify(sut, times(0)).deactivateCrossConnections(any(List.class), any(Date.class));
        }
    }

    @DataProvider
    public Object[][] checkCcsAllowedDP() {
        Rangierung rangierung = new RangierungBuilder().setPersist(false).build();
        Equipment eqInPort = new EquipmentBuilder().setPersist(false).build();
        return new Object[][] {
                { "Physiken fehlen",                  Pair.create(null, null),           false, true,  true,  false },
                { "Port fehlt",                       Pair.create(rangierung, null),     false, true,  true,  false },
                { "ist VierDrahtProdukt",             Pair.create(rangierung, eqInPort), true,  true,  true,  false },
                { "Port ist nicht in einem DSLAM",    Pair.create(rangierung, eqInPort), false, false, true,  false },
                { "CC ist fuer Port nicht aktiviert", Pair.create(rangierung, eqInPort), false, true,  false, false },
                { "Erfolg!",                          Pair.create(rangierung, eqInPort), false, true,  true,  true  },
        };
    }

    @Test(dataProvider = "checkCcsAllowedDP")
    public void testCheckCcsAllowed(@SuppressWarnings("UnusedParameters") String testcasse,
            Pair<Rangierung, Equipment> ccPhysics, Boolean isVierDraht, boolean isInADslam, boolean isCcEnabled,
            boolean successExpected) throws FindException {
        Endstelle endstelle = new EndstelleBuilder().withRangierungBuilder(new RangierungBuilder().withRandomId())
                .build();
        AuftragDaten auftragDaten = new AuftragDatenBuilder().build();
        Produkt produkt = new ProduktBuilder().build();
        when(auftragService.findAuftragDatenByEndstelle(anyLong())).thenReturn(auftragDaten);
        when(produktService.findProdukt4Auftrag(anyLong())).thenReturn(produkt);
        when(produktService.isVierDrahtProdukt(anyLong())).thenReturn(isVierDraht);
        when(rangierungsService.isPortInADslam(any(Equipment.class))).thenReturn(isInADslam);
        doReturn(isCcEnabled).when(sut).isCrossConnectionEnabled(any(Equipment.class));
        doReturn(ccPhysics).when(sut).findCcPhysicsByEndstelle(any(Endstelle.class));

        String result = sut.checkCcsAllowed(endstelle);

        assertTrue((successExpected) ? result == null : result != null);
    }
}
