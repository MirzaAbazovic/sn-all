/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.03.2005 16:27:03
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.NiederlassungBuilder;
import de.augustakom.hurrican.model.cc.RsmPortUsage;
import de.augustakom.hurrican.model.cc.RsmPortUsageBuilder;
import de.augustakom.hurrican.model.cc.RsmRangCountBuilder;
import de.augustakom.hurrican.model.cc.query.ResourcenMonitorQuery;
import de.augustakom.hurrican.model.cc.view.RsmRangCountView;
import de.augustakom.hurrican.model.cc.view.UevtCuDAView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.MonitorCalculationService;
import de.augustakom.hurrican.service.cc.MonitorService;


/**
 * TestNG fuer <code>MonitorService</code>.
 */
@Test
public class MonitorServiceTest extends AbstractHurricanBaseServiceTest {

    private static final String TEST_CLUSTER = "testCluster";

    @Test(groups = { BaseTest.SLOW} )
    public void testCreateUevtCuDAViews() throws Exception {
        MonitorService sut = getCCService(MonitorService.class);
        MonitorCalculationService calculationService = getCCService(MonitorCalculationService.class);
        calculationService.createUevtCuDAViews();

        List<UevtCuDAView> result = sut.findUevtCuDAViews();
        assertNotEmpty(result, "collection should not be empty");

        ResourcenMonitorQuery query1 = new ResourcenMonitorQuery();
        query1.setNiederlassungId(1L); // Augsburg
        List<UevtCuDAView> result1 = sut.findUevtCuDAViews(query1);
        assertTrue(result1.size() < result.size(), "does not filter by niederlassung");

        ResourcenMonitorQuery query2 = new ResourcenMonitorQuery();
        query2.addStandortType(11000L); // HVT
        List<UevtCuDAView> result2 = sut.findUevtCuDAViews(query2);
        assertTrue(result2.size() < result.size(), "does not filter by standortType");

        ResourcenMonitorQuery query3 = new ResourcenMonitorQuery();
        query3.setNiederlassungId(1L); // Augsburg
        query3.addStandortType(11000L); // HVT
        List<UevtCuDAView> result3 = sut.findUevtCuDAViews(query3);
        assertTrue(result3.size() < result1.size(), "does not filter by niederlassung and standortType");
        assertTrue(result3.size() < result2.size(), "does not filter by niederlassung and standortType");

        ResourcenMonitorQuery query4 = new ResourcenMonitorQuery();
        query4.setCluster("INVALID-CLUSTER-BECAUSE-NONE-EXIST-TO-TEST");
        List<UevtCuDAView> result4 = sut.findUevtCuDAViews(query4);
        assertEmpty(result4, "does not filter by cluster");
    }

    private Niederlassung setupRsmRangCount() {
        // @formatter:off
        Niederlassung niederlassung = getBuilder(NiederlassungBuilder.class)
                .withRandomId()
                .build();
        HVTGruppeBuilder hvtGruppeBuilder1 = getBuilder(HVTGruppeBuilder.class)
                .withRandomId()
                .withNiederlassungId(niederlassung.getId());
        HVTStandortBuilder hvtStandortBuilder1 = getBuilder(HVTStandortBuilder.class)
                .withRandomId()
                .withHvtGruppeBuilder(hvtGruppeBuilder1)
                .withStandortTypRefId(11000L);
        HVTStandortBuilder hvtStandortBuilder2 = getBuilder(HVTStandortBuilder.class)
                .withRandomId()
                .withHvtGruppeBuilder(hvtGruppeBuilder1)
                .withStandortTypRefId(11001L)
                .withClusterId(TEST_CLUSTER);
        HVTStandortBuilder hvtStandortBuilder3 = getBuilder(HVTStandortBuilder.class)
                .withRandomId()
                .withHvtGruppeBuilder(hvtGruppeBuilder1)
                .withStandortTypRefId(11002L)
                .withClusterId(TEST_CLUSTER);
        getBuilder(RsmRangCountBuilder.class).withHvtStandortBuilder(hvtStandortBuilder1)
                .withPhysiktyp(1L)
                .withVorhanden(1)
                .build();
        getBuilder(RsmRangCountBuilder.class).withHvtStandortBuilder(hvtStandortBuilder2)
                .withPhysiktyp(1L)
                .withVorhanden(1)
                .build();
        getBuilder(RsmRangCountBuilder.class).withHvtStandortBuilder(hvtStandortBuilder3)
                .withPhysiktyp(1L)
                .withVorhanden(1)
                .build();
        return niederlassung;
        // @formatter:on
    }

    @Test(groups = SERVICE)
    public void testFindAllRsmRangCount() throws Exception {
        Niederlassung niederlassung = setupRsmRangCount();
        MonitorService sut = getCCService(MonitorService.class);
        List<RsmRangCountView> list = sut.findAllRsmRangCount();

        boolean foundNew = false;
        for (RsmRangCountView view : list) {
            if (view.getNiederlassung().equals(niederlassung.getName())) {
                foundNew = true;
                break;
            }
        }
        assertTrue(foundNew, "newly created not part of all.");
    }

    @Test(groups = SERVICE)
    public void testFindFilteredRsmRangCountByNiederlassung() throws Exception {
        Niederlassung niederlassung = setupRsmRangCount();
        MonitorService sut = getCCService(MonitorService.class);
        ResourcenMonitorQuery query = new ResourcenMonitorQuery();
        query.setNiederlassungId(niederlassung.getId());
        List<RsmRangCountView> list = sut.findRsmRangCount(query);

        assertEquals(list.size(), 3);
        assertEquals(list.get(0).getNiederlassung(), niederlassung.getName());
    }

    @Test(groups = SERVICE)
    public void testFindFilteredRsmRangCountByStandortType() throws Exception {
        Niederlassung niederlassung = setupRsmRangCount();
        MonitorService sut = getCCService(MonitorService.class);
        ResourcenMonitorQuery query = new ResourcenMonitorQuery();
        query.setNiederlassungId(niederlassung.getId());
        query.addStandortType(11000L);
        query.addStandortType(11001L);
        List<RsmRangCountView> list = sut.findRsmRangCount(query);

        assertEquals(list.size(), 2);
        assertEquals(list.get(0).getNiederlassung(), niederlassung.getName());
    }

    @Test(groups = SERVICE)
    public void testFindFilteredRsmRangCountByStandortTypeAndClusterId() throws Exception {
        Niederlassung niederlassung = setupRsmRangCount();
        MonitorService sut = getCCService(MonitorService.class);
        ResourcenMonitorQuery query = new ResourcenMonitorQuery();
        query.setNiederlassungId(niederlassung.getId());
        query.addStandortType(11000L);
        query.addStandortType(11001L);
        query.setCluster(TEST_CLUSTER);
        List<RsmRangCountView> list = sut.findRsmRangCount(query);

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getCluster(), TEST_CLUSTER);
    }

    @Test(groups = SERVICE)
    public void testFindFilteredRsmRangCountByClusterIdOnly() throws Exception {
        Niederlassung niederlassung = setupRsmRangCount();
        MonitorService sut = getCCService(MonitorService.class);
        ResourcenMonitorQuery query = new ResourcenMonitorQuery();
        query.setNiederlassungId(niederlassung.getId());
        query.setCluster(TEST_CLUSTER);
        List<RsmRangCountView> list = sut.findRsmRangCount(query);

        assertEquals(list.size(), 2);
        assertEquals(list.get(0).getCluster(), TEST_CLUSTER);
    }

    @Test(groups = SERVICE)
    public void testFindRsmPortUsage() throws FindException {
        HVTStandortBuilder hvtStdBuilder = getBuilder(HVTStandortBuilder.class);
        RsmPortUsage portUsageDecember = getBuilder(RsmPortUsageBuilder.class)
                .withHvtStandortBuilder(hvtStdBuilder)
                .withYear(Integer.valueOf(2009))
                .withMonth(Integer.valueOf(12))
                .withDiffCount(Integer.valueOf(10))
                .withPhysikTypId(Long.valueOf(1))
                .build();
        RsmPortUsage portUsageJanuary = getBuilder(RsmPortUsageBuilder.class)
                .withHvtStandortBuilder(hvtStdBuilder)
                .withYear(Integer.valueOf(2010))
                .withMonth(Integer.valueOf(1))
                .withDiffCount(Integer.valueOf(8))
                .withPhysikTypId(Long.valueOf(1))
                .build();

        List<RsmPortUsage> result = getCCService(MonitorService.class).findRsmPortUsage(
                hvtStdBuilder.get().getId(), null, portUsageDecember.getPhysikTypId(), portUsageDecember.getPhysikTypIdAdditional());
        assertNotEmpty(result, "Keine PortUsage Daten gefunden!");
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getYear(), portUsageJanuary.getYear());
        assertEquals(result.get(1).getYear(), portUsageDecember.getYear());
    }

}


