/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.2015
 */
package de.augustakom.hurrican.service.cc.impl;

import com.google.common.collect.Lists;
import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.HWBaugruppenChangeDAO;
import de.augustakom.hurrican.model.cc.*;
import de.augustakom.hurrican.model.cc.equipment.*;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.validation.cc.HWBaugruppenChangeValidator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@Test(groups = BaseTest.UNIT)
public class HWBaugruppenChangeServiceImplTestMergeCards {

    @InjectMocks
    HWBaugruppenChangeServiceImpl cut;

    @Mock
    HWService hwService;

    @Mock
    RangierungsService rangierungsService;

    @Mock
    ReferenceService referenceService;

    @Mock
    HWBaugruppenChangeValidator hwBaugruppenChangeValidator;

    @Mock
    HWBaugruppenChangeDAO hwBaugruppenChangeDAO;

    @BeforeMethod
    void setUp() throws Exception {
        cut = new HWBaugruppenChangeServiceImpl();
        MockitoAnnotations.initMocks(this);

        when(hwService.findRackForBaugruppe(anyLong())).thenReturn(dslamWithGeraeteBez("a0"));
    }

    @Test
    void testCreatePort2Port4MergeCards_OneToOne() throws Exception {
        final HWBaugruppe srcBaugruppe = new HWBaugruppeBuilder()
                .withRandomId().setPersist(false).build();

        final HWBaugruppe destBaugruppe = new HWBaugruppeBuilder()
                .withRandomId().setPersist(false).build();

        final HWBaugruppenChangeCard changeCard = new HWBaugruppenChangeCardBuilder()
                .withHwBaugruppenSource(srcBaugruppe)
                .withHWBaugruppeNew(destBaugruppe)
                .setPersist(false).build();

        final HWBaugruppenChange bgChange = hwBaugruppenChangeBuilder()
                .withHWBaugruppenChangeCard(changeCard)
                .build();

        final ArrayList<Equipment> srcEquipments = Lists.newArrayList(
                equipmentWithHwEQN("1-1-1-1"),
                equipmentWithHwEQN("1-1-1-2"));

        final ArrayList<Equipment> destEquipments = Lists.newArrayList(
                equipmentWithHwEQN("2-1-1-1"),
                equipmentWithHwEQN("2-1-1-2"));

        when(rangierungsService.findEquipments4HWBaugruppe(eq(srcBaugruppe.getId()))).thenReturn(srcEquipments);
        when(rangierungsService.findEquipments4HWBaugruppe(eq(destBaugruppe.getId()))).thenReturn(destEquipments);

        final List<HWBaugruppenChangePort2Port> p2p = cut.createPort2Port4ChangeCard(bgChange);
        assertEquals(2, p2p.size());
        assertEquals(srcEquipments.get(0), p2p.get(0).getEquipmentOld());
        assertEquals(destEquipments.get(0), p2p.get(0).getEquipmentNew());
    }

    @Test
    void testCreatePort2Port4MergeCards_OneToOne_2ndDslPortIsIgnored() throws Exception {
        final HWBaugruppe srcBaugruppe = new HWBaugruppeBuilder()
                .withRandomId().setPersist(false).build();

        final HWBaugruppe destBaugruppe = new HWBaugruppeBuilder()
                .withRandomId().setPersist(false).build();

        final HWBaugruppenChangeCard changeCard = new HWBaugruppenChangeCardBuilder()
                .withHwBaugruppenSource(srcBaugruppe)
                .withHWBaugruppeNew(destBaugruppe)
                .setPersist(false).build();

        final HWBaugruppenChange bgChange = hwBaugruppenChangeBuilder()
                .withHWBaugruppenChangeCard(changeCard)
                .build();

        final ArrayList<Equipment> srcEquipments = Lists.newArrayList(
                equipmentWithHwEQN("1-1-1-1"),
                equipmentWithHwEQN("1-1-1-2"));

        final ArrayList<Equipment> destEquipments = Lists.newArrayList(
                equipmentWithHwEQN("2-2-2-1"),
                new EquipmentBuilder()
                        .withHwEQN("2-2-2-2")
                        .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_ADSL_IN)
                        .setPersist(false)
                        .build(),
                equipmentWithHwEQN("2-2-2-3"));

        when(rangierungsService.findEquipments4HWBaugruppe(eq(srcBaugruppe.getId()))).thenReturn(srcEquipments);
        when(rangierungsService.findEquipments4HWBaugruppe(eq(destBaugruppe.getId()))).thenReturn(destEquipments);

        final List<HWBaugruppenChangePort2Port> p2p = cut.createPort2Port4ChangeCard(bgChange);
        assertEquals(2, p2p.size());
        assertEquals(srcEquipments.get(0), p2p.get(0).getEquipmentOld());
        assertEquals(destEquipments.get(0), p2p.get(0).getEquipmentNew());

        assertEquals(srcEquipments.get(1), p2p.get(1).getEquipmentOld());
        assertEquals(destEquipments.get(2), p2p.get(1).getEquipmentNew());
    }

    @Test
    void testCreatePort2Port4MergeCards_OneToMany() throws Exception {
        final HWBaugruppe srcBaugruppe = new HWBaugruppeBuilder()
                .withRandomId().setPersist(false).build();

        final HWBaugruppe destBaugruppe1 = new HWBaugruppeBuilder()
                .withRandomId().setPersist(false).build();

        final HWBaugruppe destBaugruppe2 = new HWBaugruppeBuilder()
                .withRandomId().setPersist(false).build();

        final HWBaugruppenChangeCard changeCard = new HWBaugruppenChangeCardBuilder()
                .withHwBaugruppenSource(srcBaugruppe)
                .withHWBaugruppeNew(destBaugruppe1)
                .withHWBaugruppeNew(destBaugruppe2)
                .setPersist(false).build();

        final HWBaugruppenChange bgChange = hwBaugruppenChangeBuilder()
                .withHWBaugruppenChangeCard(changeCard)
                .build();

        final ArrayList<Equipment> srcEquipments = Lists.newArrayList(
                equipmentWithHwEQN("1-1-1-1"),
                equipmentWithHwEQN("1-1-1-2"),
                equipmentWithHwEQN("1-1-1-3"),
                equipmentWithHwEQN("1-1-1-4"));

        final ArrayList<Equipment> destEquipments1 = Lists.newArrayList(
                equipmentWithHwEQN("2-2-2-1"),
                equipmentWithHwEQN("2-2-2-2"));

        final ArrayList<Equipment> destEquipments2 = Lists.newArrayList(
                equipmentWithHwEQN("3-3-3-1"),
                equipmentWithHwEQN("3-3-3-2"));

        when(hwService.findRackForBaugruppe(destBaugruppe1.getId())).thenReturn(dslamWithGeraeteBez("a1"));
        when(hwService.findRackForBaugruppe(destBaugruppe2.getId())).thenReturn(dslamWithGeraeteBez("a2"));

        when(rangierungsService.findEquipments4HWBaugruppe(eq(srcBaugruppe.getId()))).thenReturn(srcEquipments);
        when(rangierungsService.findEquipments4HWBaugruppe(eq(destBaugruppe1.getId()))).thenReturn(destEquipments1);
        when(rangierungsService.findEquipments4HWBaugruppe(eq(destBaugruppe2.getId()))).thenReturn(destEquipments2);

        final List<HWBaugruppenChangePort2Port> p2p = cut.createPort2Port4ChangeCard(bgChange);
        assertEquals(4, p2p.size());

        assertEquals(srcEquipments.get(1), p2p.get(1).getEquipmentOld());
        assertEquals(destEquipments1.get(1), p2p.get(1).getEquipmentNew());

        assertEquals(srcEquipments.get(2), p2p.get(2).getEquipmentOld());
        assertEquals(destEquipments2.get(0), p2p.get(2).getEquipmentNew());
    }

    @Test
    void testCreatePort2Port4MergeCards_ManyToOne() throws Exception {
        final HWBaugruppe srcBaugruppe1 = new HWBaugruppeBuilder()
                .withRandomId().setPersist(false).build();

        final HWBaugruppe srcBaugruppe2 = new HWBaugruppeBuilder()
                .withRandomId().setPersist(false).build();

        final HWBaugruppe destBaugruppe1 = new HWBaugruppeBuilder()
                .withRandomId().setPersist(false).build();

        final HWBaugruppenChangeCard changeCard = new HWBaugruppenChangeCardBuilder()
                .withHwBaugruppenSource(srcBaugruppe1)
                .withHwBaugruppenSource(srcBaugruppe2)
                .withHWBaugruppeNew(destBaugruppe1)
                .setPersist(false).build();

        final HWBaugruppenChange bgChange = hwBaugruppenChangeBuilder()
                .withHWBaugruppenChangeCard(changeCard)
                .build();

        final ArrayList<Equipment> srcEquipments1 = Lists.newArrayList(
                equipmentWithHwEQN("1-1-1-1"),
                equipmentWithHwEQN("1-1-1-2"));

        final ArrayList<Equipment> srcEquipments2 = Lists.newArrayList(
                equipmentWithHwEQN("2-2-2-1"),
                equipmentWithHwEQN("2-2-2-2"));

        final ArrayList<Equipment> destEquipments1 = Lists.newArrayList(
                equipmentWithHwEQN("3-3-3-1"),
                equipmentWithHwEQN("3-3-3-2"),
                equipmentWithHwEQN("3-3-3-3"),
                equipmentWithHwEQN("3-3-3-4"));

        when(hwService.findRackForBaugruppe(srcBaugruppe1.getId())).thenReturn(dslamWithGeraeteBez("a1"));
        when(hwService.findRackForBaugruppe(srcBaugruppe2.getId())).thenReturn(dslamWithGeraeteBez("a2"));

        when(rangierungsService.findEquipments4HWBaugruppe(eq(srcBaugruppe1.getId()))).thenReturn(srcEquipments1);
        when(rangierungsService.findEquipments4HWBaugruppe(eq(srcBaugruppe2.getId()))).thenReturn(srcEquipments2);
        when(rangierungsService.findEquipments4HWBaugruppe(eq(destBaugruppe1.getId()))).thenReturn(destEquipments1);

        final List<HWBaugruppenChangePort2Port> p2p = cut.createPort2Port4ChangeCard(bgChange);
        assertEquals(4, p2p.size());

        assertEquals(srcEquipments1.get(1), p2p.get(1).getEquipmentOld());
        assertEquals(destEquipments1.get(1), p2p.get(1).getEquipmentNew());

        assertEquals(srcEquipments2.get(0), p2p.get(2).getEquipmentOld());
        assertEquals(destEquipments1.get(2), p2p.get(2).getEquipmentNew());
    }

    private HWBaugruppenChangeBuilder hwBaugruppenChangeBuilder() {
        return new HWBaugruppenChangeBuilder()
                .withChangeTypeReference(mergeCardsRef())
                .withChangeStateReference(planningState())
                .setPersist(false);
    }

    private Reference mergeCardsRef() {
        return new ReferenceBuilder()
                .withId(HWBaugruppenChange.ChangeType.MERGE_CARDS.refId())
                .setPersist(false)
                .build();
    }

    private Reference planningState() {
        return new ReferenceBuilder()
                .withId(HWBaugruppenChange.ChangeState.CHANGE_STATE_PLANNING.refId())
                .setPersist(false)
                .build();
    }

    private Equipment equipmentWithHwEQN(String hwEQN) {
        return new EquipmentBuilder().withHwEQN(hwEQN).withRandomId().setPersist(false).build();
    }

    private HWDslam dslamWithGeraeteBez(String geraeteBez) {
        return new HWDslamBuilder().withGeraeteBez(geraeteBez).setPersist(false).build();
    }
}
