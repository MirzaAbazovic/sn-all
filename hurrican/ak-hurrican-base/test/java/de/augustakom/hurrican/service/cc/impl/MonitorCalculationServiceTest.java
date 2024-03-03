/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.05.2015
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.RsmPortUsage;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.MonitorCalculationService;

@Test(groups = BaseTest.SERVICE)
public class MonitorCalculationServiceTest extends AbstractHurricanBaseServiceTest {

    @Test(groups = SERVICE)
    public void testCalculatePortUsage() throws FindException, StoreException {
        // TODO Test dynamisieren, um die ermittelten Port-Counts besser pruefen zu koennen
        MonitorCalculationService ms = getCCService(MonitorCalculationService.class);
        List<RsmPortUsage> result = ms.calculatePortUsage(6, Long.valueOf(1), Long.valueOf(513), Long.valueOf(508), null);
        assertNotEmpty(result, "Kein Port-Verbrauch ermittelt!");
    }

}
