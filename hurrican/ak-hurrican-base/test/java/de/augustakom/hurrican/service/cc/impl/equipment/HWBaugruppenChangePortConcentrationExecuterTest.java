package de.augustakom.hurrican.service.cc.impl.equipment;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Sets;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2PortBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortView;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortViewBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * TestNG Klasse fuer {@link HWBaugruppenChangePortConcentrationExecuter}
 */
@Test(groups = BaseTest.UNIT)
public class HWBaugruppenChangePortConcentrationExecuterTest extends BaseTest {

    @Spy
    private HWBaugruppenChangePortConcentrationExecuter cut = new HWBaugruppenChangePortConcentrationExecuter();

    @Mock
    private HWService hwService;
    @Mock
    private HWBaugruppenChange toExecute;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private PhysikService physikService;
    @Mock
    private HwBaugruppenChangeCardHelper helper;

    @Mock
    private HWBaugruppenChangeService hwBaugruppenChangeService;
    @Mock
    private DSLAMService dslamService;

    @Mock
    CCAuftragService ccAuftragService;

    @Mock
    Map<Long, Rangierung> aktualisierteRangierungen;

    @BeforeMethod
    public void setup() throws FindException {
        initMocks(this);

        List<HWBaugruppenChangePort2PortView> portMappings = Collections.singletonList(new HWBaugruppenChangePort2PortViewBuilder()
                .setPersist(false).withAuftragId(1L).build());
        when(hwBaugruppenChangeService.findPort2PortViews(toExecute)).thenReturn(portMappings);

        IstRangierungFreiFuerPortkonzentration istRangierungFrei = new IstQuellRangierungFreiFuerPortkonzentrationImpl(ccAuftragService);
        cut.configure(toExecute, hwService, hwBaugruppenChangeService, rangierungsService, null, null, dslamService,
                physikService, null, -1L, "dummyUser", istRangierungFrei);

        cut.helper = helper;
        cut.aktualisierteRangierungen = aktualisierteRangierungen;

        when(ccAuftragService.findAuftragDatenByEndstelleTx(anyLong())).thenReturn(
                new AuftragDatenBuilder().withStatusId(AuftragStatus.ABSAGE).build());
    }

    @Test
    public void testFindSourcePortsToUnravelWithNoBgs() throws FindException {
        when(toExecute.getHWBaugruppen4ChangeCard()).thenReturn(new ArrayList<>());
        Map<HWBaugruppe, List<Equipment>> result = cut.findSourcePortsToUnravel();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindSourcePortsToUnravelWithNoPortMappings() throws FindException {
        mockBaugruppenList(1L);
        mockEquipmentList(1L, 100L, 101L);
        when(toExecute.getPort2Port()).thenReturn(new HashSet<>());

        Map<HWBaugruppe, List<Equipment>> result = cut.findSourcePortsToUnravel();
        assertTrue(result.size() == 1);
        assertTrue(result.entrySet().iterator().next().getValue().size() == 2);
    }

    @Test
    public void testFindSourcePortsToUnravelNoMatch() throws FindException {
        mockBaugruppenList(1L);
        mockEquipmentList(1L, 100L, 101L);
        mockPort2Port(200L, 201L, 100L, 101L);

        Map<HWBaugruppe, List<Equipment>> result = cut.findSourcePortsToUnravel();
        assertTrue(result.size() == 1);
        assertTrue(result.entrySet().iterator().next().getValue().size() == 0);
    }

    // Baugruppe mit 2 Ports und 2 Mappings, aber nur einem Match
    // Port2Port: Planung fuer 2 Baugruppen, Baugruppe: nur 1 Port ist geplant
    @Test
    public void testFindSourcePortsToUnravel2By2OneMatch() throws FindException {
        mockBaugruppenList(1L);
        mockEquipmentList(1L, 100L, 101L);
        mockEquipmentList(2L, 102L, 103L);
        mockPort2Port(200L, 201L, 102L, 103L);

        Map<HWBaugruppe, List<Equipment>> result = cut.findSourcePortsToUnravel();
        assertTrue(result.size() == 1);
        assertTrue(result.entrySet().iterator().next().getValue().size() == 2);
    }

    @Test
    public void testUnravelRangierungen4SrcBaugruppe() throws FindException, StoreException {
        HWBaugruppe hwBaugruppe = new HWBaugruppeBuilder()
                .withBaugruppenTypBuilder(new HWBaugruppenTypBuilder()).build();
        List<Equipment> equipments = mockEquipmentList(1L, 100L, 101L);

        Rangierung rangierungNichtFrei = mock(Rangierung.class);
        when(rangierungNichtFrei.getEsId()).thenReturn(-1L);
        when(rangierungsService.findRangierung4Equipment(equipments.get(0).getId())).thenReturn(rangierungNichtFrei);

        Rangierung rangierungFrei = mock(Rangierung.class);
        Equipment uevt = new EquipmentBuilder().withId(200L).build();
        when(rangierungFrei.getEsId()).thenReturn(null);
        when(rangierungFrei.getEqInId()).thenReturn(equipments.get(1).getId());
        when(rangierungFrei.getEqOutId()).thenReturn(uevt.getId());
        when(rangierungsService.findEquipment(uevt.getId())).thenReturn(uevt);
        when(rangierungsService.findRangierung4Equipment(equipments.get(1).getId())).thenReturn(rangierungFrei);

        cut.unravelRangierungen4SrcBaugruppe(hwBaugruppe, equipments);
        assertTrue(cut.getWarnings().getAKMessages().size() == 2);
        verify(helper, times(1)).modifyEquipmentState(eq(uevt), eq(EqStatus.frei));
        verify(rangierungFrei, times(1)).setFreigegeben(eq(Rangierung.Freigegeben.deactivated));
        verify(rangierungsService, times(1)).saveRangierung(eq(rangierungFrei), eq(false));
        verify(helper, times(1)).modifyEquipmentState(eq(equipments.get(1)), eq(EqStatus.abgebaut));
    }

    @Test
    public void testUnravelRangierungen4DestBaugruppe() throws FindException, StoreException {

        final Equipment equipmentNew = new EquipmentBuilder()
                .withRandomId()
                .build();

        final HWBaugruppenChangePort2Port port2Port = new HWBaugruppenChangePort2PortBuilder()
                .withEquipmentNew(equipmentNew)
                .build();
        when(toExecute.getPort2Port()).thenReturn(Sets.newHashSet(port2Port));

        Rangierung rangierungNichtFrei = mock(Rangierung.class);
        when(rangierungNichtFrei.getEsId()).thenReturn(-1L);
        when(rangierungsService.findRangierung4Equipment(equipmentNew.getId())).thenReturn(rangierungNichtFrei);

        Rangierung rangierungFrei = mock(Rangierung.class);
        Equipment uevt = new EquipmentBuilder().withId(200L).build();
        when(rangierungFrei.getEsId()).thenReturn(null);
        when(rangierungFrei.getEqInId()).thenReturn(equipmentNew.getId());
        when(rangierungFrei.getEqOutId()).thenReturn(uevt.getId());
        when(rangierungsService.findEquipment(uevt.getId())).thenReturn(uevt);
        when(rangierungsService.findRangierung4Equipment(equipmentNew.getId())).thenReturn(rangierungFrei);

        cut.unravelRangierungenFromDestBaugruppe();
        verify(helper).modifyEquipmentState(eq(uevt), eq(EqStatus.frei));
        verify(rangierungFrei).setFreigegeben(eq(Rangierung.Freigegeben.deactivated));
        verify(rangierungFrei).setGueltigBis(any(Date.class));
        verify(rangierungsService).saveRangierung(eq(rangierungFrei), eq(false));
    }

    private Set<HWBaugruppenChangePort2Port> mockPort2Port(Long... ids) {
        Set<HWBaugruppenChangePort2Port> port2Port = new HashSet<>();
        Equipment eqOld = null;
        for (Long id : ids) {
            if (eqOld == null) {
                eqOld = new EquipmentBuilder().withId(id).build();
            }
            else {
                HWBaugruppenChangePort2Port port = new HWBaugruppenChangePort2PortBuilder().withEquipmentOld(eqOld)
                        .withEquipmentOldIn(new EquipmentBuilder().withId(id).build()).build();
                port2Port.add(port);
                eqOld = null;
            }
        }
        when(toExecute.getPort2Port()).thenReturn(port2Port);
        return port2Port;
    }

    private List<Equipment> mockEquipmentList(Long hwBaugruppeId, Long... ids) throws FindException {
        List<Equipment> equipments = new ArrayList<>();
        for (Long id : ids) {
            equipments.add(new EquipmentBuilder().withId(id).build());
        }
        when(rangierungsService.findEquipments4HWBaugruppe(hwBaugruppeId)).thenReturn(equipments);
        return equipments;
    }

    private List<HWBaugruppe> mockBaugruppenList(Long id) {
        List<HWBaugruppe> hwBaugruppen = new ArrayList<>();
        hwBaugruppen.add(new HWBaugruppeBuilder().withId(id).build());
        when(toExecute.getHWBaugruppen4ChangeCard()).thenReturn(hwBaugruppen);
        return hwBaugruppen;
    }


}
