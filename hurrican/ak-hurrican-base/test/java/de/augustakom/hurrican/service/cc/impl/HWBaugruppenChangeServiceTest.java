/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.04.2010 13:54:52
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWDluBuilder;
import de.augustakom.hurrican.model.cc.HWSwitchBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDlu;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDluV5;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDluV5Builder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2PortBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Tests fuer HWBaugruppenChangeService.
 */
@Test(groups = BaseTest.SERVICE)
public class HWBaugruppenChangeServiceTest extends AbstractHurricanBaseServiceTest {

    public void testSaveHWBaugruppenChange() throws StoreException, ValidationException {
        HWBaugruppenChange toSave = getBuilder(HWBaugruppenChangeBuilder.class)
                .withHvtStandort(getBuilder(HVTStandortBuilder.class).build())
                .withChangeTypeReference(getBuilder(ReferenceBuilder.class).withId(Long.MAX_VALUE).build())
                .setPersist(false)
                .build();

        getCCService(HWBaugruppenChangeService.class).saveHWBaugruppenChange(toSave);
        assertNotNull(toSave.getId(), "Object was not saved!");
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testSaveHWBaugruppenChangeWithValidationException() throws StoreException, ValidationException {
        HWBaugruppenChange toSave = getBuilder(HWBaugruppenChangeBuilder.class)
                .withHvtStandort(null)
                .withChangeTypeReference(getBuilder(ReferenceBuilder.class).withId(Long.MAX_VALUE).build())
                .setPersist(false)
                .build();

        getCCService(HWBaugruppenChangeService.class).saveHWBaugruppenChange(toSave);
        assertNotNull(toSave.getId(), "Object was not saved!");
    }


    public void testFindOpenHWBaugruppenChanges() throws FindException {
        Reference type = getBuilder(ReferenceBuilder.class).withId(Long.MAX_VALUE).build();

        getBuilder(HWBaugruppenChangeBuilder.class)
                .withHvtStandort(getBuilder(HVTStandortBuilder.class).build())
                .withChangeTypeReference(type)
                .build();

        HWBaugruppenChange cancelled = getBuilder(HWBaugruppenChangeBuilder.class)
                .withHvtStandort(getBuilder(HVTStandortBuilder.class).build())
                .withChangeTypeReference(type)
                .withCancelledAt(new Date())
                .build();

        HWBaugruppenChange closed = getBuilder(HWBaugruppenChangeBuilder.class)
                .withHvtStandort(getBuilder(HVTStandortBuilder.class).build())
                .withChangeTypeReference(type)
                .withClosedAt(new Date())
                .build();

        List<HWBaugruppenChange> open = getCCService(HWBaugruppenChangeService.class).findOpenHWBaugruppenChanges();
        assertNotEmpty(open, "keine offenen Port-Planungen gefunden!");

        for (HWBaugruppenChange openChange : open) {
            if (NumberTools.isIn(openChange.getId(), new Number[] { cancelled.getId(), closed.getId() })) {
                fail("stornierte Planung wird als offene Planung erkannt!");
            }
        }
    }

    public void testPrepareHWBaugruppenChange() throws StoreException, ValidationException, FindException {
        List<Equipment> equipments = new ArrayList<>();
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class);

        EquipmentBuilder eqSrc1Builder = getBuilder(EquipmentBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder).withHwEQN("1-1-1-1").withStatus(EqStatus.rang);
        Equipment eqSrc1 = eqSrc1Builder.build();
        equipments.add(eqSrc1);
        Rangierung rangSrc1 = getBuilder(RangierungBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withEqInBuilder(eqSrc1Builder)
                .withFreigegeben(Freigegeben.freigegeben)
                .build();

        EquipmentBuilder eqSrc2Builder = getBuilder(EquipmentBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder).withHwEQN("1-1-1-2").withStatus(EqStatus.rang);
        Equipment eqSrc2 = eqSrc2Builder.build();
        equipments.add(eqSrc2);
        Rangierung rangSrc2 = getBuilder(RangierungBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withEqInBuilder(eqSrc2Builder)
                .withFreigegeben(Freigegeben.freigegeben)
                .build();

        Equipment eqDest1 = getBuilder(EquipmentBuilder.class).withHvtStandortBuilder(hvtStandortBuilder).withHwEQN("1-1-2-1").build();
        equipments.add(eqDest1);
        Equipment eqDest2 = getBuilder(EquipmentBuilder.class).withHvtStandortBuilder(hvtStandortBuilder).withHwEQN("1-1-2-2").build();
        equipments.add(eqDest2);

        HWBaugruppenChangePort2Port port2Port1 = getBuilder(HWBaugruppenChangePort2PortBuilder.class)
                .withEquipmentOld(eqSrc1)
                .withEquipmentNew(eqDest1)
                .setPersist(false)
                .build();
        HWBaugruppenChangePort2Port port2Port2 = getBuilder(HWBaugruppenChangePort2PortBuilder.class)
                .withEquipmentOld(eqSrc2)
                .withEquipmentNew(eqDest2)
                .setPersist(false)
                .build();

        Reference refType = getCCService(ReferenceService.class).findReference(HWBaugruppenChange.ChangeType.CHANGE_CARD.refId());
        Reference refState = getCCService(ReferenceService.class).findReference(HWBaugruppenChange.ChangeState.CHANGE_STATE_PLANNING.refId());
        HWBaugruppenChange hwBgChange = getBuilder(HWBaugruppenChangeBuilder.class)
                .withHvtStandort(hvtStandortBuilder.build())
                .withChangeTypeReference(refType)
                .withChangeStateReference(refState)
                .withHWBaugruppenChangePort2Port(port2Port1)
                .withHWBaugruppenChangePort2Port(port2Port2)
                .build();

        HWBaugruppenChangeService service = getCCService(HWBaugruppenChangeService.class);
        service.prepareHWBaugruppenChange(hwBgChange);

        assertEquals(hwBgChange.getChangeState().getId(), HWBaugruppenChange.ChangeState.CHANGE_STATE_PREPARED.refId(),
                "Status der Planung nicht korrekt umgesetzt!");

        for (Equipment eq : equipments) {
            assertEquals(eq.getStatus(), EqStatus.WEPLA, "Status von Equipment " + eq.getHwEQN() + " nicht <WEPLA>!");
        }
        assertEquals(rangSrc1.getFreigegeben(), Freigegeben.WEPLA, "Rangierung 1 hat nicht den Status <WEPLA>!");
        assertEquals(rangSrc2.getFreigegeben(), Freigegeben.WEPLA, "Rangierung 2 hat nicht den Status <WEPLA>!");
    }

    public void testCloseHWBaugruppenChange() throws StoreException, FindException {
        Reference refType = getCCService(ReferenceService.class).findReference(HWBaugruppenChange.ChangeType.CHANGE_CARD.refId());
        Reference refState = getCCService(ReferenceService.class).findReference(HWBaugruppenChange.ChangeState.CHANGE_STATE_EXECUTED.refId());
        HWBaugruppenChange hwBgChange = getBuilder(HWBaugruppenChangeBuilder.class)
                .withHvtStandort(getBuilder(HVTStandortBuilder.class).build())
                .withChangeStateReference(refState)
                .withChangeTypeReference(refType)
                .build();

        HWBaugruppenChangeService service = getCCService(HWBaugruppenChangeService.class);
        service.closeHWBaugruppenChange(hwBgChange, -1L);

        assertEquals(hwBgChange.getChangeState().getId(), HWBaugruppenChange.ChangeState.CHANGE_STATE_CLOSED.refId());
        assertNotNull(hwBgChange.getClosedAt(), "Datum, zu dem der Datensatz geschlossen wurde ist nicht gesetzt");
        assertNotNull(hwBgChange.getClosedFrom(), "User, von dem der Datensatz geschlossen wurde ist nicht gesetzt");
    }

    @Test(expectedExceptions = StoreException.class)
    public void testCloseHWBaugruppenChangeWithInvalidState() throws StoreException, FindException {
        Reference refType = getCCService(ReferenceService.class).findReference(HWBaugruppenChange.ChangeType.CHANGE_CARD.refId());
        Reference refState = getCCService(ReferenceService.class).findReference(HWBaugruppenChange.ChangeState.CHANGE_STATE_PREPARED.refId());
        HWBaugruppenChange hwBgChange = getBuilder(HWBaugruppenChangeBuilder.class)
                .withHvtStandort(getBuilder(HVTStandortBuilder.class).build())
                .withChangeStateReference(refState)
                .withChangeTypeReference(refType)
                .build();

        HWBaugruppenChangeService service = getCCService(HWBaugruppenChangeService.class);
        service.closeHWBaugruppenChange(hwBgChange, -1L);
    }

    public void testCancelHWBaugruppenChange() throws StoreException, FindException {
        Reference refType = getCCService(ReferenceService.class).findReference(HWBaugruppenChange.ChangeType.CHANGE_CARD.refId());
        Reference refState = getCCService(ReferenceService.class).findReference(HWBaugruppenChange.ChangeState.CHANGE_STATE_PREPARED.refId());
        HWBaugruppenChange hwBgChange = getBuilder(HWBaugruppenChangeBuilder.class)
                .withHvtStandort(getBuilder(HVTStandortBuilder.class).build())
                .withChangeStateReference(refState)
                .withChangeTypeReference(refType)
                .build();

        HWBaugruppenChangeService service = getCCService(HWBaugruppenChangeService.class);
        service.cancelHWBaugruppenChange(hwBgChange, -1L);

        assertEquals(hwBgChange.getChangeState().getId(), HWBaugruppenChange.ChangeState.CHANGE_STATE_CANCELLED.refId());
        assertNotNull(hwBgChange.getCancelledAt(), "Datum, zu dem der Datensatz storniert wurde ist nicht gesetzt");
        assertNotNull(hwBgChange.getCancelledFrom(), "User, von dem der Datensatz storniert wurde ist nicht gesetzt");
    }

    @Test(expectedExceptions = StoreException.class)
    public void testCancelHWBaugruppenChangeWithInvalidState() throws StoreException, FindException {
        Reference refType = getCCService(ReferenceService.class).findReference(HWBaugruppenChange.ChangeType.CHANGE_CARD.refId());
        Reference refState = getCCService(ReferenceService.class).findReference(HWBaugruppenChange.ChangeState.CHANGE_STATE_EXECUTED.refId());
        HWBaugruppenChange hwBgChange = getBuilder(HWBaugruppenChangeBuilder.class)
                .withHvtStandort(getBuilder(HVTStandortBuilder.class).build())
                .withChangeStateReference(refState)
                .withChangeTypeReference(refType)
                .build();

        HWBaugruppenChangeService service = getCCService(HWBaugruppenChangeService.class);
        service.cancelHWBaugruppenChange(hwBgChange, -1L);
    }

    public void deletePort2Ports() throws DeleteException {
        HWBaugruppenChange hwBgChange = getBuilder(HWBaugruppenChangeBuilder.class)
                .withHvtStandort(getBuilder(HVTStandortBuilder.class).build())
                .withChangeTypeReference(getBuilder(ReferenceBuilder.class).withId(Long.MAX_VALUE).build())
                .withHWBaugruppenChangePort2Port(getBuilder(HWBaugruppenChangePort2PortBuilder.class).setPersist(false).build())
                .build();

        HWBaugruppenChangeService service = getCCService(HWBaugruppenChangeService.class);
        service.deletePort2Ports(hwBgChange);
        assertEmpty(hwBgChange.getPort2Port(), "Die Port-Mappings wurden nicht von der Planung entfernt!");

    }

    public void testCreatePort2Port4HwBaugruppe() throws FindException, StoreException {
        HWBaugruppeBuilder hwBgBuilder = getBuilder(HWBaugruppeBuilder.class)
                .setPersist(false);

        List<Equipment> equipments = new ArrayList<>();
        equipments.add(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-1-1").setPersist(false).build());
        equipments.add(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-1-2").setPersist(false).build());
        equipments.add(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-1-3").setPersist(false).build());
        equipments.add(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-1-4").setPersist(false).build());

        RangierungsService rangierungsService = mock(RangierungsService.class);
        when(rangierungsService.findEquipments4HWBaugruppe(any(Long.class))).thenReturn(equipments);

        HWBaugruppenChangeServiceImpl service = new HWBaugruppenChangeServiceImpl();
        service.setRangierungsService(rangierungsService);

        List<HWBaugruppenChangePort2Port> result = service.createPort2Port4HwBaugruppe(hwBgBuilder.get().getId());
        assertNotEmpty(result, "Fuer die Baugruppe wurde kein Port-Mapping erstellt");
        assertEquals(result.size(), 4, "Anzahl der generierten Port-Mappings stimmt nicht mit der Vorgabe ueberein!");
    }

    public void testCreatePortMapping() throws FindException {
        List<HWBaugruppenChangePort2Port> port2Ports = new ArrayList<>();
        port2Ports.add(getBuilder(HWBaugruppenChangePort2PortBuilder.class)
                .withEquipmentOld(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-1-1").setPersist(false).build())
                .setPersist(false).build());
        port2Ports.add(getBuilder(HWBaugruppenChangePort2PortBuilder.class)
                .withEquipmentOld(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-1-2").setPersist(false).build())
                .setPersist(false).build());
        port2Ports.add(getBuilder(HWBaugruppenChangePort2PortBuilder.class)
                .withEquipmentOld(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-1-3").setPersist(false).build())
                .setPersist(false).build());
        port2Ports.add(getBuilder(HWBaugruppenChangePort2PortBuilder.class)
                .withEquipmentOld(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-1-4").setPersist(false).build())
                .setPersist(false).build());

        List<Equipment> destEquipments = new ArrayList<>();
        destEquipments.add(getBuilder(EquipmentBuilder.class).withId(1L).withHwEQN("2-1-1-1")
                .withStatus(EqStatus.frei).setPersist(false).build());
        destEquipments.add(getBuilder(EquipmentBuilder.class).withId(2L).withHwEQN("2-1-1-2")
                .withStatus(EqStatus.frei).setPersist(false).build());
        destEquipments.add(getBuilder(EquipmentBuilder.class).withId(3L).withHwEQN("2-1-1-3")
                .withStatus(EqStatus.frei).setPersist(false).build());
        destEquipments.add(getBuilder(EquipmentBuilder.class).withId(4L).withHwEQN("2-1-1-4")
                .withStatus(EqStatus.frei).setPersist(false).build());

        RangierungsService rangierungsService = mock(RangierungsService.class);
        when(rangierungsService.findEquipments4HWBaugruppe(any(Long.class))).thenReturn(destEquipments);
        when(rangierungsService.findRangierung4Equipment(any(Long.class))).thenReturn(null);

        HWBaugruppenChangeServiceImpl service = new HWBaugruppenChangeServiceImpl();
        service.setRangierungsService(rangierungsService);

        service.createPortMapping4ChangeCard(port2Ports,
                Collections.singletonList(getBuilder(HWBaugruppeBuilder.class).setPersist(false).build()));
        Iterator<HWBaugruppenChangePort2Port> port2PortIterator = port2Ports.iterator();
        while (port2PortIterator.hasNext()) {
            HWBaugruppenChangePort2Port port2port = port2PortIterator.next();
            assertNotNull(port2port.getEquipmentNew(), "Neues Equipment wurde nicht definiert!");
            assertNotNull(port2port.getEqStateOrigNew(), "Status des neuen Equipments wurde nicht definiert!");
        }
    }

    @Test(groups = BaseTest.SERVICE, expectedExceptions = FindException.class)
    public void testCreatePortMappingWithInvalidPortSize() throws FindException {
        List<HWBaugruppenChangePort2Port> port2Ports = new ArrayList<>();
        port2Ports.add(getBuilder(HWBaugruppenChangePort2PortBuilder.class)
                .withEquipmentOld(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-1-1").setPersist(false).build())
                .setPersist(false).build());
        port2Ports.add(getBuilder(HWBaugruppenChangePort2PortBuilder.class)
                .withEquipmentOld(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-1-2").setPersist(false).build())
                .setPersist(false).build());

        List<Equipment> destEquipments = new ArrayList<>();
        destEquipments.add(getBuilder(EquipmentBuilder.class).withId(1L).withHwEQN("2-1-1-1")
                .withStatus(EqStatus.frei).setPersist(false).build());

        RangierungsService rangierungsService = mock(RangierungsService.class);
        when(rangierungsService.findEquipments4HWBaugruppe(any(Long.class))).thenReturn(destEquipments);
        when(rangierungsService.findRangierung4Equipment(any(Long.class))).thenReturn(null);

        HWBaugruppenChangeServiceImpl service = new HWBaugruppenChangeServiceImpl();
        service.setRangierungsService(rangierungsService);

        service.createPortMapping4ChangeCard(port2Ports,
                Collections.singletonList(getBuilder(HWBaugruppeBuilder.class).setPersist(false).build()));
    }

    public void testCheckCrossConnectionDefinitions() throws FindException {
        Set<HWBaugruppenChangePort2Port> port2Ports = new HashSet<>();
        port2Ports.add(getBuilder(HWBaugruppenChangePort2PortBuilder.class)
                .withEquipmentOld(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-1-1").withManualConfiguration(Boolean.TRUE).setPersist(false).build())
                .withEquipmentNew(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-2-1").withManualConfiguration(Boolean.TRUE).setPersist(false).build())
                .setPersist(false).build());
        port2Ports.add(getBuilder(HWBaugruppenChangePort2PortBuilder.class)
                .withEquipmentOld(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-1-2").withManualConfiguration(Boolean.TRUE).setPersist(false).build())
                .withEquipmentNew(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-2-2").withManualConfiguration(Boolean.FALSE).setPersist(false).build())
                .setPersist(false).build());
        port2Ports.add(getBuilder(HWBaugruppenChangePort2PortBuilder.class)
                .withEquipmentOld(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-1-3").withManualConfiguration(Boolean.FALSE).setPersist(false).build())
                .withEquipmentNew(getBuilder(EquipmentBuilder.class).withHwEQN("1-1-2-3").withManualConfiguration(Boolean.TRUE).setPersist(false).build())
                .setPersist(false).build());

        HWBaugruppenChangeService service = getCCService(HWBaugruppenChangeService.class);
        Set<HWBaugruppenChangePort2Port> invalidPorts = service.checkCrossConnectionDefinitions(port2Ports);
        assertNotEmpty(invalidPorts, "Es wurden keine inkorrekten CrossConnection-Definitionen gefunden!");
        assertEquals(invalidPorts.size(), 1, "Anzahl der gefunden inkorrekten CrossConnections ist nicht korrekt!");
        assertEquals(invalidPorts.iterator().next().getEquipmentOld().getHwEQN(), "1-1-1-2",
                "falsches Port-Mapping in CrossConnection-Pruefung als <falsch> ermittelt!");
    }

    public void testFindDluV5Mappings() throws FindException {
        HWDluBuilder hwDluBuilder = getBuilder(HWDluBuilder.class)
                .withRackTyp(HWRack.RACK_TYPE_DLU)
                .setPersist(true);

        HWBaugruppenChangeDlu hwBgChangeDlu = new HWBaugruppenChangeDlu();
        hwBgChangeDlu.setDluAccessControllerNew("AC123");
        hwBgChangeDlu.setDluMediaGatewayNew("MG123");
        hwBgChangeDlu.setDluNumberNew("9999");
        hwBgChangeDlu.setDluRackOld(hwDluBuilder.get());
        HWSwitch hwSwitch = getBuilder(HWSwitchBuilder.class)
                .withName("XXX01")
                .withType(HWSwitchType.EWSD)
                .build();
        hwBgChangeDlu.setDluSwitchNew(hwSwitch);

        getBuilder(HWBaugruppenChangeBuilder.class)
                .withHvtStandort(getBuilder(HVTStandortBuilder.class).build())
                .withHWBaugruppenChangeDlu(hwBgChangeDlu)
                .withChangeTypeReference(getBuilder(ReferenceBuilder.class).withId(Long.MAX_VALUE).build())
                .setPersist(true)
                .build();

        HWBaugruppenChangeDluV5 v5 = getBuilder(HWBaugruppenChangeDluV5Builder.class)
                .withHwBaugruppenChangeDluId(hwBgChangeDlu.getId())
                .withV5Port("8888-88-88-88")
                .withEquipment(getBuilder(EquipmentBuilder.class).withHwEQN("0001-00-01-02").setPersist(true).build())
                .setPersist(true)
                .build();

        HWBaugruppenChangeService service = getCCService(HWBaugruppenChangeService.class);
        List<HWBaugruppenChangeDluV5> result = service.findDluV5Mappings(hwBgChangeDlu.getId());
        assertNotEmpty(result, "V5 Mappings nicht gefunden!");
        assertEquals(result.size(), 1, "Anzahl der gefundenen V5-Mappings nicht i.O.");
        assertEquals(result.get(0).getHwEqn(), v5.getHwEqn());
    }

    public void testDeleteDluV5Mappings() throws DeleteException, FindException {
        HWDluBuilder hwDluBuilder = getBuilder(HWDluBuilder.class)
                .withRackTyp(HWRack.RACK_TYPE_DLU)
                .setPersist(true);

        HWBaugruppenChangeDlu hwBgChangeDlu = new HWBaugruppenChangeDlu();
        hwBgChangeDlu.setDluAccessControllerNew("AC123");
        hwBgChangeDlu.setDluMediaGatewayNew("MG123");
        hwBgChangeDlu.setDluNumberNew("9999");
        hwBgChangeDlu.setDluRackOld(hwDluBuilder.get());
        HWSwitch hwSwitch = getBuilder(HWSwitchBuilder.class)
                .withName("XXX01")
                .withType(HWSwitchType.EWSD)
                .build();
        hwBgChangeDlu.setDluSwitchNew(hwSwitch);

        getBuilder(HWBaugruppenChangeBuilder.class)
                .withHvtStandort(getBuilder(HVTStandortBuilder.class).build())
                .withHWBaugruppenChangeDlu(hwBgChangeDlu)
                .withChangeTypeReference(getBuilder(ReferenceBuilder.class).withId(Long.MAX_VALUE).build())
                .setPersist(true)
                .build();

        getBuilder(HWBaugruppenChangeDluV5Builder.class)
                .withHwBaugruppenChangeDluId(hwBgChangeDlu.getId())
                .withV5Port("8888-88-88-88")
                .withEquipment(getBuilder(EquipmentBuilder.class).withHwEQN("0001-00-01-02").setPersist(true).build())
                .setPersist(true)
                .build();

        HWBaugruppenChangeService service = getCCService(HWBaugruppenChangeService.class);
        List<HWBaugruppenChangeDluV5> result = service.findDluV5Mappings(hwBgChangeDlu.getId());
        assertNotEmpty(result, "V5 Mappings nicht gefunden!");

        service.deleteDluV5MappingsInTx(hwBgChangeDlu.getId());

        result = service.findDluV5Mappings(hwBgChangeDlu.getId());
        assertEmpty(result, "V5 Mappings gefunden, obwohl geloescht!");
    }

}
