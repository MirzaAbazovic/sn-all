/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2010 09:19:19
 */
package de.augustakom.hurrican.service.cc.impl.command.rs.monitor;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.RSMonitorConfig;
import de.augustakom.hurrican.model.cc.RSMonitorConfigBuilder;
import de.augustakom.hurrican.model.cc.RsmRangCount;
import de.augustakom.hurrican.model.cc.RsmRangCountBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * TestNG Klasse fuer {@link AlarmierungRangCommand}
 */
@Test(groups = { UNIT })
public class AlarmierungRangCommandTest extends BaseTest {

    private AlarmierungRangCommand cut;

    @SuppressWarnings("unused")
    @BeforeMethod(groups = "unit")
    private void prepareTest() throws FindException {
        cut = new AlarmierungRangCommand();
    }

    public void testEvaluateRsMonitorConfig() {
        StringBuilder freePortThresholdReached = new StringBuilder();
        StringBuilder freePortThresholdNearlyReached = new StringBuilder();
        StringBuilder portUsageThresholdReached = new StringBuilder();
        StringBuilder portUsageThresholdNearlyReached = new StringBuilder();

        HVTGruppeBuilder hvtGruppeBuilder = new HVTGruppeBuilder()
                .withRandomId()
                .withOrtsteil("HVT-Augsburg").withOnkz("0821").setPersist(false);
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder()
                .withRandomId()
                .withHvtGruppeBuilder(hvtGruppeBuilder).withAsb(Integer.valueOf(666)).setPersist(false);
        Map<Long, String> physikTypen = new HashMap<Long, String>();
        physikTypen.put(Long.valueOf(1), "UK0");

        RSMonitorConfig monitorConfig = new RSMonitorConfigBuilder()
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withPhysiktyp(Long.valueOf(1))
                .withAlarmierung(Boolean.TRUE)
                .withMinCount(10)
                .withDayCount(30)
                .setPersist(false).build();

        RsmRangCount rsmRangCount = new RsmRangCountBuilder()
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withPhysiktyp(Long.valueOf(1))
                .withFrei(Integer.valueOf(8))
                .withPortReach(Integer.valueOf(20))
                .setPersist(false).build();

        List<RsmRangCount> exisitungRsmRangCounts = Arrays.asList(rsmRangCount);

        cut.evaluateRsMonitorConfig(
                monitorConfig,
                hvtGruppeBuilder.get(),
                hvtStandortBuilder.get(),
                physikTypen,
                exisitungRsmRangCounts,
                freePortThresholdReached,
                freePortThresholdNearlyReached,
                portUsageThresholdReached,
                portUsageThresholdNearlyReached);

        assertEquals(freePortThresholdNearlyReached.toString(), "");
        assertEquals(portUsageThresholdNearlyReached.toString(), "");
        assertEquals(freePortThresholdReached.toString(), "HVT-Augsburg (ONKZ 0821/ASB 666), UK0, Freie Stifte: 8, (Schwellwert 10)\n");
        assertEquals(portUsageThresholdReached.toString(), "HVT-Augsburg (ONKZ 0821/ASB 666), UK0, Reichweite: 20, (Schwellwert 30)\n");
    }

}


