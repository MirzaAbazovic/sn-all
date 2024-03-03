/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.05.2015
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.model.cc.RsmPortUsage;
import de.augustakom.hurrican.model.cc.RsmPortUsageBuilder;
import de.augustakom.hurrican.model.cc.RsmRangCount;
import de.augustakom.hurrican.model.cc.RsmRangCountBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;

@Test(groups = BaseTest.UNIT)
public class MonitorCalculationServiceImplTest extends BaseTest {

    @InjectMocks
    private MonitorCalculationServiceImpl sut;

    @Mock
    private EquipmentDAO equipmentDao;

    @SuppressWarnings("unused")
    @BeforeMethod(groups = "unit")
    private void prepareTest() throws FindException {
        sut = new MonitorCalculationServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    public void calculatePortReach() {
        RsmRangCount rangierungsCount = new RsmRangCountBuilder()
                .withFrei(Integer.valueOf(5))
                .withImAufbau(Integer.valueOf(32))
                .setPersist(false).build();

        RsmPortUsage usage1 = new RsmPortUsageBuilder()
                .withDiffCount(10).setPersist(false).build();
        RsmPortUsage usage2 = new RsmPortUsageBuilder()
                .withDiffCount(5).setPersist(false).build();
        RsmPortUsage usage3 = new RsmPortUsageBuilder()
                .withDiffCount(-2).setPersist(false).build();

        List<RsmPortUsage> usageOfLastMonths = Arrays.asList(usage1, usage2, usage3);

        sut.calculatePortReach(rangierungsCount, usageOfLastMonths);
        assertEquals(rangierungsCount.getPortReach(), Integer.valueOf(256), "Anzahl Tage fuer Port-Reichweite nicht wie erwartet!");
    }

    public void calculatePortNegativ() {
        RsmRangCount rangierungsCount = new RsmRangCountBuilder()
                .withFrei(Integer.valueOf(5))
                .withImAufbau(Integer.valueOf(32))
                .setPersist(false).build();

        RsmPortUsage usage1 = new RsmPortUsageBuilder()
                .withDiffCount(-10).setPersist(false).build();
        RsmPortUsage usage2 = new RsmPortUsageBuilder()
                .withDiffCount(5).setPersist(false).build();
        RsmPortUsage usage3 = new RsmPortUsageBuilder()
                .withDiffCount(-2).setPersist(false).build();

        List<RsmPortUsage> usageOfLastMonths = Arrays.asList(usage1, usage2, usage3);

        sut.calculatePortReach(rangierungsCount, usageOfLastMonths);
        assertEquals(rangierungsCount.getPortReach(), Integer.valueOf(-1), "Negativer Durchschnitt sollte als Port-Reach -1 liefern!");
    }


    public void testGetPortUsageOfMonth() {
        RsmPortUsage usage201001 = new RsmPortUsageBuilder()
                .withYear(Integer.valueOf(2010)).withMonth(Integer.valueOf(1)).setPersist(false).build();
        RsmPortUsage usage201002 = new RsmPortUsageBuilder()
                .withYear(Integer.valueOf(2010)).withMonth(Integer.valueOf(2)).setPersist(false).build();
        RsmPortUsage usage201003 = new RsmPortUsageBuilder()
                .withYear(Integer.valueOf(2010)).withMonth(Integer.valueOf(3)).setPersist(false).build();

        List<RsmPortUsage> usages = Arrays.asList(usage201001, usage201002, usage201003);
        RsmPortUsage result = sut.getPortUsageOfMonth(usages, Integer.valueOf(2010), Integer.valueOf(2));
        assertNotNull(result);
        assertEquals(result.getYear(), usage201002.getYear());
        assertEquals(result.getMonth(), usage201002.getMonth());
    }

}
