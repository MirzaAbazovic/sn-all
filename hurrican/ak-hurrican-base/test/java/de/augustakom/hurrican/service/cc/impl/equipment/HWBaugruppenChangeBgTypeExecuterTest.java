/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2010 14:39:37
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeBgTypeBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2PortBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * TestNG Klasse fuer {@link HWBaugruppenChangeBgTypeExecuter}
 */
@Test(groups = BaseTest.UNIT)
public class HWBaugruppenChangeBgTypeExecuterTest extends BaseTest {

    private HWBaugruppenChangeBgTypeExecuter cut;
    private RangierungsService rangierungsService;
    private HWService hwService;
    private PhysikService physikService;

    private HWBaugruppenChange hwBgChange;
    private Equipment eqOld1;
    private Equipment eqOld2;
    private HWBaugruppeBuilder hwBgSrcBuilder;
    private HWBaugruppenTypBuilder hwBgTypBuilder;

    @BeforeMethod
    public void setUp() {
        cut = new HWBaugruppenChangeBgTypeExecuter();
        rangierungsService = mock(RangierungsService.class);
        hwService = mock(HWService.class);
        physikService = mock(PhysikService.class);

        cut.configure(null, hwService, null, rangierungsService, physikService);

        buildTestData();
    }

    private void buildTestData() {
        hwBgSrcBuilder = new HWBaugruppeBuilder()
                .withRandomId().withEingebaut(Boolean.TRUE)
                .withBaugruppenTypBuilder(new HWBaugruppenTypBuilder().withRandomId())
                .setPersist(false);

        hwBgTypBuilder = new HWBaugruppenTypBuilder()
                .withRandomId().withName("bgtyp").setPersist(false);

        HVTStandortBuilder hvtStdBuilder = new HVTStandortBuilder().setPersist(false);

        eqOld1 = new EquipmentBuilder()
                .withRandomId()
                .withHvtStandortBuilder(hvtStdBuilder).withBaugruppeBuilder(hwBgSrcBuilder).withStatus(EqStatus.WEPLA)
                .setPersist(false).build();
        eqOld2 = new EquipmentBuilder()
                .withRandomId()
                .withHvtStandortBuilder(hvtStdBuilder).withBaugruppeBuilder(hwBgSrcBuilder).withStatus(EqStatus.WEPLA)
                .setPersist(false).build();

        HWBaugruppenChangePort2Port p2p1 = new HWBaugruppenChangePort2PortBuilder()
                .withEquipmentOld(eqOld1).setPersist(false).build();
        HWBaugruppenChangePort2Port p2p2 = new HWBaugruppenChangePort2PortBuilder()
                .withEquipmentOld(eqOld2).setPersist(false).build();

        hwBgChange = new HWBaugruppenChangeBuilder()
                .withChangeTypeReference(new ReferenceBuilder().withId(HWBaugruppenChange.ChangeType.CHANGE_BG_TYPE.refId()).setPersist(false).build())
                .withHWBaugruppenChangeBgType(new HWBaugruppenChangeBgTypeBuilder()
                        .withHWBaugruppe(hwBgSrcBuilder.build())
                        .withHWBaugruppenTypNew(hwBgTypBuilder.build())
                        .build())
                .withHWBaugruppenChangePort2Port(p2p1)
                .withHWBaugruppenChangePort2Port(p2p2)
                .withPhysikTypNewBuilder(new PhysikTypBuilder()
                        .withHvtTechnikBuilder(new HVTTechnikBuilder().withRandomId().setPersist(false))
                        .setPersist(false)
                        .withRandomId())
                .setPersist(false)
                .build();
    }


    public void testChangeBgType() throws StoreException {
        cut.setHwBgChange(hwBgChange);
        cut.setHwBaugruppeToChange(hwBgSrcBuilder.get());
        cut.setHwBgChangeBgType(hwBgChange.getHwBgChangeBgType().iterator().next());
        cut.changeBgType();

        assertEquals(hwBgSrcBuilder.get().getHwBaugruppenTyp().getId(), hwBgTypBuilder.get().getId(),
                "Baugruppen-Typ wurde nicht korrekt geaendert!");
    }


    public void testModifyAndActivateRangierung() throws FindException, StoreException {
        HVTTechnikBuilder hvtTechnikBuilderOld = new HVTTechnikBuilder().withRandomId().setPersist(false);

        PhysikTypBuilder ptBuilderOld = new PhysikTypBuilder()
                .withRandomId()
                .withHvtTechnikBuilder(hvtTechnikBuilderOld);
        PhysikTypBuilder childPhysikTypBuilderNew = new PhysikTypBuilder()
                .withRandomId();

        Rangierung rangierung1 = new RangierungBuilder()
                .withRandomId()
                .withPhysikTypBuilder(ptBuilderOld)
                .setPersist(false).build();
        Rangierung rangierung2 = new RangierungBuilder()
                .withRandomId()
                .withPhysikTypBuilder(ptBuilderOld)
                .setPersist(false).build();

        List<Equipment> equipments = Arrays.asList(eqOld1);
        when(rangierungsService.findRangierung4Equipment(eqOld1.getId(), true)).thenReturn(rangierung1);
        when(rangierungsService.findRangierung4Equipment(eqOld2.getId(), true)).thenReturn(rangierung2);

        cut.setEquipmentsOfHwBaugruppe(equipments);
        cut.setHwBgChange(hwBgChange);
        cut.modifyAndActivateRangierung(rangierung1, hwBgChange.getPhysikTypNew(), true);
        assertEquals(rangierung1.getPhysikTypId(), hwBgChange.getPhysikTypNew().getId());

        when(physikService.findPhysikTyp(rangierung2.getPhysikTypId())).thenReturn(ptBuilderOld.get());
        when(physikService.manufacturerChanged(any(PhysikTyp.class), any(PhysikTyp.class))).thenReturn(true);
        when(physikService.findCorrespondingPhysiktyp(ptBuilderOld.get(), hwBgChange.getPhysikTypNew().getHvtTechnikId()))
                .thenReturn(childPhysikTypBuilderNew.get());

        cut.modifyAndActivateRangierung(rangierung2, hwBgChange.getPhysikTypNew(), false);
        assertEquals(rangierung2.getPhysikTypId(), childPhysikTypBuilderNew.getId());
    }

}


