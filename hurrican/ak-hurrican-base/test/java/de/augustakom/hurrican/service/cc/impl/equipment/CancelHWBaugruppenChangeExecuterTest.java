/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.04.2010 13:27:19
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeCardBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2PortBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * TestNG Klasse fuer {@link CancelHWBaugruppenChangeExecuter}
 */
@Test(groups = BaseTest.UNIT)
public class CancelHWBaugruppenChangeExecuterTest extends BaseTest {

    private CancelHWBaugruppenChangeExecuter cut;
    private RangierungsService rangierungsService;

    private HWBaugruppenChange hwBgChange;
    private HWBaugruppeBuilder hwBgSrcBuilder;
    private HWBaugruppeBuilder hwBgDestBuilder;
    private Equipment eqOld1;
    private Equipment eqOld2;
    private Equipment eqNew1;
    private Equipment eqNew2;

    @BeforeMethod
    public void setUp() {
        cut = new CancelHWBaugruppenChangeExecuter();
        rangierungsService = mock(RangierungsService.class);
        cut.setRangierungsService(rangierungsService);

        buildTestData();
    }

    private void buildTestData() {
        hwBgSrcBuilder = new HWBaugruppeBuilder()
                .withRandomId().withEingebaut(Boolean.TRUE).setPersist(false);

        hwBgDestBuilder = new HWBaugruppeBuilder()
                .withRandomId().withEingebaut(Boolean.FALSE).setPersist(false);

        HVTStandortBuilder hvtStdBuilder = new HVTStandortBuilder().setPersist(false);

        eqOld1 = new EquipmentBuilder()
                .withRandomId()
                .withHvtStandortBuilder(hvtStdBuilder).withBaugruppeBuilder(hwBgSrcBuilder).withStatus(EqStatus.WEPLA)
                .setPersist(false).build();
        Long randomId = eqOld1.getId();
        eqOld2 = new EquipmentBuilder()
                .withId(++randomId)
                .withHvtStandortBuilder(hvtStdBuilder).withBaugruppeBuilder(hwBgSrcBuilder).withStatus(EqStatus.WEPLA)
                .setPersist(false).build();

        eqNew1 = new EquipmentBuilder()
                .withId(++randomId)
                .withHvtStandortBuilder(hvtStdBuilder).withBaugruppeBuilder(hwBgSrcBuilder).withStatus(EqStatus.WEPLA)
                .setPersist(false).build();
        eqNew2 = new EquipmentBuilder()
                .withId(++randomId)
                .withHvtStandortBuilder(hvtStdBuilder).withBaugruppeBuilder(hwBgSrcBuilder).withStatus(EqStatus.WEPLA)
                .setPersist(false).build();

        HWBaugruppenChangePort2Port p2p1 = new HWBaugruppenChangePort2PortBuilder()
                .withEquipmentOld(eqOld1).withEquipmentNew(eqNew1)
                .withEqStateOrigOld(EqStatus.rang)
                .withEqStateOrigNew(EqStatus.frei)
                .withRangStateOrigOld(Freigegeben.freigegeben)
                .setPersist(false).build();
        HWBaugruppenChangePort2Port p2p2 = new HWBaugruppenChangePort2PortBuilder()
                .withEquipmentOld(eqOld2).withEquipmentNew(eqNew2)
                .withEqStateOrigOld(EqStatus.rang)
                .withEqStateOrigNew(EqStatus.frei)
                .withRangStateOrigOld(Freigegeben.freigegeben)
                .setPersist(false).build();

        hwBgChange = new HWBaugruppenChangeBuilder()
                .withChangeTypeReference(new ReferenceBuilder().withId(HWBaugruppenChange.ChangeType.CHANGE_CARD.refId()).setPersist(false).build())
                .withHWBaugruppenChangeCard(new HWBaugruppenChangeCardBuilder()
                        .withHWBaugruppeNew(hwBgDestBuilder.build())
                        .withHwBaugruppenSource(hwBgSrcBuilder.build())
                        .setPersist(false).build())
                .withHWBaugruppenChangePort2Port(p2p1)
                .withHWBaugruppenChangePort2Port(p2p2)
                .setPersist(false)
                .build();
    }


    public void testDoCancelChangeCardOrBgTyp() throws FindException, StoreException {
        Rangierung rangierung1 = new RangierungBuilder()
                .withFreigegeben(Freigegeben.WEPLA)
                .setPersist(false)
                .build();
        Rangierung rangierung2 = new RangierungBuilder()
                .withFreigegeben(Freigegeben.WEPLA)
                .setPersist(false)
                .build();
        when(rangierungsService.findRangierung4Equipment(eqOld1.getId())).thenReturn(rangierung1);
        when(rangierungsService.findRangierung4Equipment(eqOld2.getId())).thenReturn(rangierung2);
        when(rangierungsService.findRangierung4Equipment(eqNew1.getId())).thenReturn(null);
        when(rangierungsService.findRangierung4Equipment(eqNew2.getId())).thenReturn(null);

        cut.setHwBgChange(hwBgChange);
        cut.doCancelChangeCardOrBgTyp();

        assertEquals(eqOld1.getStatus(), EqStatus.rang);
        assertEquals(eqOld2.getStatus(), EqStatus.rang);
        assertEquals(eqNew1.getStatus(), EqStatus.frei);
        assertEquals(eqNew2.getStatus(), EqStatus.frei);
        assertEquals(rangierung1.getFreigegeben(), Freigegeben.freigegeben);
        assertEquals(rangierung2.getFreigegeben(), Freigegeben.freigegeben);
    }


    public void testRollbackPortState() throws StoreException, FindException {
        Rangierung rangierung = new RangierungBuilder()
                .withFreigegeben(Freigegeben.WEPLA)
                .setPersist(false)
                .build();
        when(rangierungsService.findRangierung4Equipment(eqNew1.getId())).thenReturn(rangierung);

        cut.rollbackPortState(eqNew1, EqStatus.frei, Freigegeben.freigegeben);

        assertEquals(eqNew1.getStatus(), EqStatus.frei);
        assertEquals(rangierung.getFreigegeben(), Freigegeben.freigegeben);
    }

}


