/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2010 16:33:13
 */
package de.augustakom.hurrican.service.cc.impl;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.RsmPortUsage;
import de.augustakom.hurrican.model.cc.RsmPortUsageBuilder;
import de.augustakom.hurrican.model.cc.RsmRangCount;
import de.augustakom.hurrican.model.cc.RsmRangCountBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * TestNG Unit Test fuer MonitorServiceImpl
 */
@Test(groups = UNIT)
public class MonitorServiceImplUnitTest extends BaseTest {

    @InjectMocks
    private MonitorServiceImpl sut;

    @Mock
    private EquipmentDAO equipmentDao;

    @SuppressWarnings("unused")
    @BeforeMethod(groups = "unit")
    private void prepareTest() throws FindException {
        sut = new MonitorServiceImpl();
        MockitoAnnotations.initMocks(this);
    }


    public void testSplitRangierungenByKvzNummer() {
        EquipmentBuilder eqOut1Kvz1 = new EquipmentBuilder().withRandomId().withRangStift1("1").withKvzNummer("A001").setPersist(false);
        EquipmentBuilder eqOut2Kvz1 = new EquipmentBuilder().withRandomId().withRangStift1("2").withKvzNummer("A001").setPersist(false);
        EquipmentBuilder eqOut1Kvz2 = new EquipmentBuilder().withRandomId().withRangStift1("1").withKvzNummer("A002").setPersist(false);
        EquipmentBuilder eqOut2Kvz2 = new EquipmentBuilder().withRandomId().withRangStift1("2").withKvzNummer("A002").setPersist(false);
        EquipmentBuilder eqOut3Kvz2 = new EquipmentBuilder().withRandomId().withRangStift1("3").withKvzNummer("A002").setPersist(false);

        Rangierung rang1Kvz1 = new RangierungBuilder().withEqOutBuilder(eqOut1Kvz1).setPersist(false).build();
        Rangierung rang2Kvz1 = new RangierungBuilder().withEqOutBuilder(eqOut2Kvz1).setPersist(false).build();
        Rangierung rang1Kvz2 = new RangierungBuilder().withEqOutBuilder(eqOut1Kvz2).setPersist(false).build();
        Rangierung rang2Kvz2 = new RangierungBuilder().withEqOutBuilder(eqOut2Kvz2).setPersist(false).build();
        Rangierung rang3Kvz2 = new RangierungBuilder().withEqOutBuilder(eqOut3Kvz2).setPersist(false).build();
        List<Rangierung> rangierungen = Arrays.asList(rang1Kvz1, rang2Kvz1, rang1Kvz2, rang2Kvz2, rang3Kvz2);

        when(equipmentDao.findById(rang1Kvz1.getEqOutId(), Equipment.class)).thenReturn(eqOut1Kvz1.get());
        when(equipmentDao.findById(rang2Kvz1.getEqOutId(), Equipment.class)).thenReturn(eqOut2Kvz1.get());
        when(equipmentDao.findById(rang1Kvz2.getEqOutId(), Equipment.class)).thenReturn(eqOut1Kvz2.get());
        when(equipmentDao.findById(rang2Kvz2.getEqOutId(), Equipment.class)).thenReturn(eqOut2Kvz2.get());
        when(equipmentDao.findById(rang3Kvz2.getEqOutId(), Equipment.class)).thenReturn(eqOut3Kvz2.get());

        Map<String, List<Rangierung>> result = sut.splitRangierungenByKvzNummer(rangierungen);
        assertNotNull(result);
        assertThat(result.keySet().size(), equalTo(2));
        assertTrue(result.keySet().contains("A001"));
        assertTrue(result.keySet().contains("A002"));
        assertThat(result.get("A001").size(), equalTo(2));
        assertThat(result.get("A002").size(), equalTo(3));
    }

}
