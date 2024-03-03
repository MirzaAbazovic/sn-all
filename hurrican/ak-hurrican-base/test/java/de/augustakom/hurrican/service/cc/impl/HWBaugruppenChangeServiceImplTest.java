package de.augustakom.hurrican.service.cc.impl;

import static com.google.common.collect.Iterables.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.mockito.InjectMocks;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.HWBaugruppenChangeDAO;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeCard;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeCardBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.validation.cc.HWBaugruppenChangeValidator;

@Test(groups = BaseTest.UNIT)
public class HWBaugruppenChangeServiceImplTest {

    @InjectMocks
    HWBaugruppenChangeServiceImpl hwBaugruppenChangeService;

    @Mock
    HWBaugruppenChangeDAO hwBaugruppenChangeDAO;

    @Mock
    RangierungsService rangierungsService;

    @Mock
    ReferenceService referenceService;

    @Mock
    HWBaugruppenChangeValidator hwBaugruppenChangeValidator;

    @Mock
    CCAuftragService auftragService;

    @Mock
    HWService hwService;

    Reference changeType;
    Reference changeState;
    HWBaugruppenChangePort2Port port2Port;
    HWBaugruppenChange in;
    HWBaugruppe bgSrc;
    HWBaugruppe bgDest;
    Rangierung belegteRangierung;

    Equipment srcDslamPortIn1;
    Equipment srcDslamPortOut1;
    Equipment srcDslamPortIn2;
    Equipment srcDslamPortOut2;
    Equipment srcDslamPortIn3;
    Equipment srcDslamPortOut3;

    Equipment destDslamPortIn1;
    Equipment destDslamPortOut1;
    Equipment destDslamPortIn2;
    Equipment destDslamPortOut2;

    @BeforeMethod
    void setUp() throws Exception {
        hwBaugruppenChangeService = new HWBaugruppenChangeServiceImpl();
        initMocks(this);

        changeType = new ReferenceBuilder()
                .withId(HWBaugruppenChange.ChangeType.PORT_CONCENTRATION.refId())
                .build();
        changeState = new ReferenceBuilder()
                .withId(HWBaugruppenChange.ChangeState.CHANGE_STATE_PLANNING.refId())
                .build();
        port2Port = new HWBaugruppenChangePort2Port();
        in = new HWBaugruppenChange();
        bgSrc = new HWBaugruppeBuilder().withRandomId().build();
        bgDest = new HWBaugruppeBuilder().withRandomId().build();
        HWBaugruppenChangeCard hwBgChangeCard = new HWBaugruppenChangeCardBuilder()
                .withHwBaugruppenSource(bgSrc)
                .withHWBaugruppeNew(bgDest)
                .build();

        in.setChangeType(changeType);
        in.setPort2Port(Sets.newHashSet(port2Port));
        in.setChangeState(changeState);
        in.setHwBgChangeCard(Sets.newHashSet(hwBgChangeCard));
        belegteRangierung = new RangierungBuilder()
                .withEsId(1L)
                .build();

        srcDslamPortIn1 = createEquipment("00", Equipment.HW_SCHNITTSTELLE_ADSL_IN);
        srcDslamPortOut1 = createEquipment("00", Equipment.HW_SCHNITTSTELLE_ADSL_OUT);
        srcDslamPortIn2 = createEquipment("01", Equipment.HW_SCHNITTSTELLE_ADSL_IN);
        srcDslamPortOut2 = createEquipment("01", Equipment.HW_SCHNITTSTELLE_ADSL_OUT);
        srcDslamPortIn3 = createEquipment("02", Equipment.HW_SCHNITTSTELLE_ADSL_IN);
        srcDslamPortOut3 = createEquipment("02", Equipment.HW_SCHNITTSTELLE_ADSL_OUT);

        destDslamPortIn1 = createEquipment("00", Equipment.HW_SCHNITTSTELLE_ADSL_IN);
        destDslamPortOut1 = createEquipment("00", Equipment.HW_SCHNITTSTELLE_ADSL_OUT);
        destDslamPortIn2 = createEquipment("01", Equipment.HW_SCHNITTSTELLE_ADSL_IN);
        destDslamPortOut2 = createEquipment("01", Equipment.HW_SCHNITTSTELLE_ADSL_OUT);

        when(auftragService.findAuftragDatenByEndstelleTx(belegteRangierung.getEsId()))
                .thenReturn(new AuftragDatenBuilder().withStatusId(AuftragStatus.IN_BETRIEB).build());

        when(hwService.findRackForBaugruppe(anyLong())).thenReturn(
                new HWDslamBuilder()
                        .withRandomId()
                        .withGeraeteBez("asdf")
                        .build());
    }

    @Test
    public void testCreatePort2Port4PortConcentration_LoeschtBestehendePortMappings() throws Exception {
        when(rangierungsService.findEquipments4HWBaugruppe(bgDest.getId())).thenReturn(Collections.<Equipment>emptyList());
        when(rangierungsService.findEquipments4HWBaugruppe(bgSrc.getId())).thenReturn(Collections.<Equipment>emptyList());

        hwBaugruppenChangeService.createPort2Port4PortConcentration(in);

        assertTrue(CollectionUtils.isEmpty(in.getPort2Port()));
        verify(hwBaugruppenChangeDAO).deletePort2Port(port2Port);
    }

    @Test
    public void testCreatePort2Port4PortConcentration_KeinPortmappingFuerNichtBelegtePorts() throws Exception {
        when(rangierungsService.findEquipments4HWBaugruppe(bgDest.getId())).thenReturn(Lists.newArrayList(destDslamPortIn1, destDslamPortOut1));
        when(rangierungsService.findEquipments4HWBaugruppe(bgSrc.getId())).thenReturn(Lists.newArrayList(srcDslamPortIn1, srcDslamPortOut1));

        //freie Rangierung/unrangiert
        when(rangierungsService.findRangierung4Equipment(srcDslamPortIn1.getId())).thenReturn(null);

        final Set<HWBaugruppenChangePort2Port> result = hwBaugruppenChangeService.createPort2Port4PortConcentration(in);
        assertTrue(result.isEmpty());
    }

    @DataProvider
    Object[][] testCreatePort2Port4PortConcentration_KeinPortmappingFuerBelegtePortsDerenAuftraegeNichtAktivSind_DataProvider() {
        return new Object[][] {
                { AuftragStatus.ABSAGE },
                { AuftragStatus.STORNO },
                { AuftragStatus.AUFTRAG_GEKUENDIGT },
        };
    }

    @Test(dataProvider = "testCreatePort2Port4PortConcentration_KeinPortmappingFuerBelegtePortsDerenAuftraegeNichtAktivSind_DataProvider")
    public void testCreatePort2Port4PortConcentration_KeinPortmappingFuerBelegtePortsDerenAuftraegeNichtAktivSind(Long aStatus) throws Exception {
        when(rangierungsService.findEquipments4HWBaugruppe(bgDest.getId())).thenReturn(Lists.newArrayList(destDslamPortIn1, destDslamPortOut1));
        when(rangierungsService.findEquipments4HWBaugruppe(bgSrc.getId())).thenReturn(Lists.newArrayList(srcDslamPortIn1, srcDslamPortOut1));
        when(rangierungsService.findRangierung4Equipment(srcDslamPortOut1.getId())).thenReturn(belegteRangierung);
        when(auftragService.findAuftragDatenByEndstelleTx(belegteRangierung.getEsId()))
                .thenReturn(new AuftragDatenBuilder().withStatusId(aStatus).build());

        final Set<HWBaugruppenChangePort2Port> result = hwBaugruppenChangeService.createPort2Port4PortConcentration(in);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCreatePort2Port4PortConcentration_PortsAufFreigabebereitenRangierungenWerdenUmgezogen()
            throws Exception {
        when(rangierungsService.findEquipments4HWBaugruppe(bgDest.getId())).thenReturn(Lists.newArrayList(destDslamPortIn1, destDslamPortOut1));
        when(rangierungsService.findEquipments4HWBaugruppe(bgSrc.getId())).thenReturn(Lists.newArrayList(srcDslamPortIn1, srcDslamPortOut1));

        belegteRangierung.setEsId(Rangierung.RANGIERUNG_NOT_ACTIVE); // Freigabebereit
        when(rangierungsService.findRangierung4Equipment(srcDslamPortOut1.getId())).thenReturn(belegteRangierung);

        final Set<HWBaugruppenChangePort2Port> result = hwBaugruppenChangeService.createPort2Port4PortConcentration(in);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testCreatePort2Port4PortConcentration_BelegteRangierungenAufZielbaugruppenWerdenAusgelassen() throws Exception {
        when(rangierungsService.findEquipments4HWBaugruppe(bgDest.getId())).thenReturn(Lists.newArrayList(destDslamPortIn1, destDslamPortOut1));
        when(rangierungsService.findEquipments4HWBaugruppe(bgSrc.getId())).thenReturn(Lists.newArrayList(srcDslamPortIn1, srcDslamPortOut1));
        when(rangierungsService.findRangierung4Equipment(anyLong())).thenReturn(belegteRangierung);

        final Set<HWBaugruppenChangePort2Port> result = hwBaugruppenChangeService.createPort2Port4PortConcentration(in);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCreatePort2Port4PortConcentration_PortsAufFreienRangierungenderZielbaugruppeWerdenUmgezogen()
            throws Exception {
        when(rangierungsService.findEquipments4HWBaugruppe(bgDest.getId())).thenReturn(Lists.newArrayList(destDslamPortIn1, destDslamPortOut1));
        when(rangierungsService.findEquipments4HWBaugruppe(bgSrc.getId())).thenReturn(Lists.newArrayList(srcDslamPortIn1, srcDslamPortOut1));
        when(rangierungsService.findRangierung4Equipment(srcDslamPortOut1.getId())).thenReturn(belegteRangierung);

        final Rangierung freieRangierungAufZielbaugruppe = new RangierungBuilder()
                .withEsId(null)
                .build();
        when(rangierungsService.findRangierung4Equipment(destDslamPortOut1.getId())).thenReturn(freieRangierungAufZielbaugruppe);

        final Set<HWBaugruppenChangePort2Port> result = hwBaugruppenChangeService.createPort2Port4PortConcentration(in);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testCreatePort2Port4PortConcentration_PortsWerdenNachGerateBezUndHwEqnSortiertUmgezogen() throws Exception {
        final HWRack rackSrc1 = new HWDslamBuilder()
                .withRandomId()
                .withGeraeteBez("DSL-10001")
                .build();
        final HWRack rackSrc2 = new HWDslamBuilder()
                .withRandomId()
                .withGeraeteBez("DSL-10002")
                .build();
        final HWRack rackDest1 = new HWDslamBuilder()
                .withRandomId()
                .withGeraeteBez("DSL-10003")
                .build();
        final HWRack rackDest2 = new HWDslamBuilder()
                .withRandomId()
                .withGeraeteBez("DSL-10004")
                .build();
        final HWBaugruppe bgSrc2 = new HWBaugruppeBuilder()
                .withRandomId()
                .build();
        final HWBaugruppe bgDest2 = new HWBaugruppeBuilder()
                .withRandomId()
                .build();

        when(hwService.findRackForBaugruppe(bgSrc.getId())).thenReturn(rackSrc2);
        when(hwService.findRackForBaugruppe(bgSrc2.getId())).thenReturn(rackSrc1);
        when(hwService.findRackForBaugruppe(bgDest.getId())).thenReturn(rackDest1);
        when(hwService.findRackForBaugruppe(bgDest2.getId())).thenReturn(rackDest2);

        when(rangierungsService.findEquipments4HWBaugruppe(bgDest.getId()))
                .thenReturn(Lists.newArrayList(destDslamPortIn1, destDslamPortOut1));
        when(rangierungsService.findEquipments4HWBaugruppe(bgDest2.getId()))
                .thenReturn(Lists.newArrayList(destDslamPortIn2, destDslamPortOut2));

        when(rangierungsService.findEquipments4HWBaugruppe(bgSrc.getId()))
                .thenReturn(Lists.newArrayList(srcDslamPortIn1, srcDslamPortOut1));
        when(rangierungsService.findEquipments4HWBaugruppe(bgSrc2.getId()))
                .thenReturn(Lists.newArrayList(srcDslamPortIn2, srcDslamPortOut2));

        when(rangierungsService.findRangierung4Equipment(srcDslamPortOut1.getId())).thenReturn(belegteRangierung);
        when(rangierungsService.findRangierung4Equipment(srcDslamPortOut2.getId())).thenReturn(belegteRangierung);

        in.getHwBgChangeCard().iterator().next().addHwBaugruppeNew(bgDest2);
        in.getHwBgChangeCard().iterator().next().addHwBaugruppeSource(bgSrc2);

        final Set<HWBaugruppenChangePort2Port> result = hwBaugruppenChangeService.createPort2Port4PortConcentration(in);

        assertThat(result, hasSize(2));
        for (final HWBaugruppenChangePort2Port port2port : result) {
            if (port2port.getEquipmentOld().equals(srcDslamPortOut1)) {
                assertThat(port2port.getEquipmentNew(), equalTo(destDslamPortOut2));
                assertThat(port2port.getEquipmentNewIn(), equalTo(destDslamPortIn2));
            }
            else if (port2port.getEquipmentOld().equals(srcDslamPortOut2)) {
                assertThat(port2port.getEquipmentNew(), equalTo(destDslamPortOut1));
                assertThat(port2port.getEquipmentNewIn(), equalTo(destDslamPortIn1));
            }
            else {
                fail("Ports wurden in der falschen Reihenfolge umgezogen!");
            }
        }
    }

    @Test
    public void testCreatePort2Port4PortConcentration_KeinPortmappingFuerPortsInStatusWepla() throws Exception {
        when(rangierungsService.findEquipments4HWBaugruppe(bgDest.getId()))
                .thenReturn(Lists.newArrayList(destDslamPortIn2, destDslamPortOut2, destDslamPortIn1, destDslamPortOut1));
        when(rangierungsService.findEquipments4HWBaugruppe(bgSrc.getId()))
                .thenReturn(Lists.newArrayList(srcDslamPortIn1, srcDslamPortOut1, srcDslamPortIn2, srcDslamPortOut2));
        when(rangierungsService.findRangierung4Equipment(srcDslamPortOut1.getId())).thenReturn(belegteRangierung);
        when(rangierungsService.findRangierung4Equipment(srcDslamPortOut2.getId())).thenReturn(belegteRangierung);

        srcDslamPortOut1.setStatus(EqStatus.WEPLA);
        destDslamPortOut2.setStatus(EqStatus.WEPLA);

        final Set<HWBaugruppenChangePort2Port> result = hwBaugruppenChangeService.createPort2Port4PortConcentration(in);

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getEquipmentNew(), equalTo(destDslamPortOut1));
        assertThat(result.iterator().next().getEquipmentNewIn(), equalTo(destDslamPortIn1));
        assertThat(result.iterator().next().getEquipmentOld(), equalTo(srcDslamPortOut2));
    }

    private Equipment createEquipment(final String hwEqn, final String hwSs) {
        return new EquipmentBuilder()
                .withRandomId()
                .withHwSchnittstelle(hwSs)
                .withHwEQN(hwEqn)
                .build();
    }

    @Test
    public void testCreatePort2Port4PortConcentration_PortmappingEnthaeltDieRichtigenDaten() throws Exception {
        when(rangierungsService.findEquipments4HWBaugruppe(bgDest.getId()))
                .thenReturn(Lists.newArrayList(destDslamPortIn1, destDslamPortOut1));
        when(rangierungsService.findEquipments4HWBaugruppe(bgSrc.getId()))
                .thenReturn(Lists.newArrayList(srcDslamPortIn1, srcDslamPortOut1));
        when(rangierungsService.findRangierung4Equipment(srcDslamPortOut1.getId())).thenReturn(belegteRangierung);

        final HWBaugruppenChangePort2Port result = getOnlyElement(hwBaugruppenChangeService.createPort2Port4PortConcentration(in));

        assertThat(result.getEquipmentOld(), equalTo(srcDslamPortOut1));
        assertThat(result.getEquipmentOldIn(), equalTo(srcDslamPortIn1));
        assertThat(result.getEqStateOrigOld(), equalTo(srcDslamPortOut1.getStatus()));
        assertThat(result.getRangStateOrigOld(), equalTo(belegteRangierung.getFreigegeben()));

        assertThat(result.getEquipmentNew(), equalTo(destDslamPortOut1));
        assertThat(result.getEquipmentNewIn(), equalTo(destDslamPortIn1));
        assertThat(result.getEqStateOrigNew(), equalTo(result.getEqStateOrigOld()));
        assertThat(result.getRangStateOrigNew(), equalTo(Rangierung.Freigegeben.freigegeben));

        verify(hwBaugruppenChangeDAO).store(in);
    }

    @Test
    public void testSortBaugruppenByGerateBezAndModNr() throws Exception {
        final HWBaugruppe bg1InRack2 = new HWBaugruppeBuilder()
                .withRandomId()
                .withModNumber("R1/S1-LT1")
                .build();
        final HWBaugruppe bg2InRack2 = new HWBaugruppeBuilder()
                .withRandomId()
                .withModNumber("R1/S1-LT2")
                .build();
        final HWBaugruppe bg1InRack1 = new HWBaugruppeBuilder()
                .withRandomId()
                .withModNumber("R1/S1-LT14")
                .build();
        final HWDslam dslam1 = new HWDslamBuilder()
                .withRandomId()
                .withGeraeteBez("DSL-100026")
                .build();
        final HWDslam dslam2 = new HWDslamBuilder()
                .withRandomId()
                .withGeraeteBez("DSL-100145")
                .build();

        when(hwService.findRackForBaugruppe(bg1InRack2.getId())).thenReturn(dslam2);
        when(hwService.findRackForBaugruppe(bg2InRack2.getId())).thenReturn(dslam2);
        when(hwService.findRackForBaugruppe(bg1InRack1.getId())).thenReturn(dslam1);

        final List<HWBaugruppe> in = Lists.newArrayList(bg1InRack1, bg1InRack2, bg2InRack2);
        Collections.shuffle(in);

        final List<HWBaugruppe> result = hwBaugruppenChangeService.sortBaugruppenByGerateBezAndModNr(in);

        assertThat(result, contains(bg1InRack1, bg1InRack2, bg2InRack2));
    }

    @DataProvider
    Object[][] testCheckAndAddHwBaugruppe4Source_DataProvider() {
        return new Object[][] {
                //containsNew, containsSource, addBaugruppeSource, exceptionExpected
                { true,  false, true,  true },
                { false, true,  true,  true },
                { false, false, false, true },
                { false, false, true,  false },
        };
    }

    @Test(dataProvider = "testCheckAndAddHwBaugruppe4Source_DataProvider")
    public void testCheckAndAddHwBaugruppe4Source(boolean containsNew, boolean containsSource,
            boolean addBaugruppeSource, boolean exceptionExpected) throws Exception {
        HWBaugruppenChangeCard hwBaugruppenChangeCard = mock(HWBaugruppenChangeCard.class);
        HWBaugruppe hwBaugruppe = new HWBaugruppe();
        TreeSet<HWBaugruppe> hwBaugruppenNew = mock(TreeSet.class);
        TreeSet<HWBaugruppe> hwBaugruppenSource = mock(TreeSet.class);

        when(hwBaugruppenNew.contains(anyObject())).thenReturn(containsNew);
        when(hwBaugruppenChangeCard.getHwBaugruppenNew()).thenReturn(hwBaugruppenNew);
        when(hwBaugruppenSource.contains(anyObject())).thenReturn(containsSource);
        when(hwBaugruppenChangeCard.getHwBaugruppenSource()).thenReturn(hwBaugruppenSource);
        when(hwBaugruppenChangeCard.addHwBaugruppeSource(org.mockito.Matchers.any(HWBaugruppe.class)))
                .thenReturn(addBaugruppeSource);

        try {
            hwBaugruppenChangeService.checkAndAddHwBaugruppe4Source(hwBaugruppenChangeCard, hwBaugruppe);
        }
        catch (StoreException e) {
            assertTrue(exceptionExpected);
            return;
        }
        assertTrue(!exceptionExpected);
    }

    @DataProvider
    Object[][] testCheckAndAddHwBaugruppe4New_DataProvider() {
        return new Object[][] {
                //containsSource, containsNew, addBaugruppeSource, exceptionExpected
                { true,  false, true,  true },
                { false, true,  true,  true },
                { false, false, false, true },
                { false, false, true,  false },
        };
    }

    @Test(dataProvider = "testCheckAndAddHwBaugruppe4New_DataProvider")
    public void testCheckAndAddHwBaugruppe4New(boolean containsSource, boolean containsNew,
            boolean addBaugruppeNew, boolean exceptionExpected) throws Exception {
        HWBaugruppenChangeCard hwBaugruppenChangeCard = mock(HWBaugruppenChangeCard.class);
        HWBaugruppe hwBaugruppe = new HWBaugruppe();
        TreeSet<HWBaugruppe> hwBaugruppenSource = mock(TreeSet.class);
        TreeSet<HWBaugruppe> hwBaugruppenNew = mock(TreeSet.class);

        when(hwBaugruppenSource.contains(anyObject())).thenReturn(containsSource);
        when(hwBaugruppenChangeCard.getHwBaugruppenSource()).thenReturn(hwBaugruppenSource);
        when(hwBaugruppenNew.contains(anyObject())).thenReturn(containsNew);
        when(hwBaugruppenChangeCard.getHwBaugruppenNew()).thenReturn(hwBaugruppenNew);
        when(hwBaugruppenChangeCard.addHwBaugruppeNew(org.mockito.Matchers.any(HWBaugruppe.class)))
                .thenReturn(addBaugruppeNew);

        try {
            hwBaugruppenChangeService.checkAndAddHwBaugruppe4New(hwBaugruppenChangeCard, hwBaugruppe);
        }
        catch (StoreException e) {
            assertTrue(exceptionExpected);
            return;
        }
        assertTrue(!exceptionExpected);
    }
}
