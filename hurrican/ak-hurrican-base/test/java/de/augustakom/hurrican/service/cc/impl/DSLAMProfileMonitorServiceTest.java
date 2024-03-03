/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.01.2012 08:16:25
 */
package de.augustakom.hurrican.service.cc.impl;

import static de.augustakom.common.tools.lang.DateTools.*;
import static de.augustakom.hurrican.service.cc.impl.DSLAMProfileMonitorServiceTest.DateHelper.*;
import static de.augustakom.hurrican.service.cc.impl.DSLAMProfileMonitorServiceTest.TestDSLAMProfileMonitor.*;
import static de.augustakom.hurrican.service.cc.impl.DSLAMProfileMonitorServiceTest.TestDSLAMProfileMonitors.*;
import static org.testng.Assert.*;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.DSLAMProfileMonitor;
import de.augustakom.hurrican.model.cc.DSLAMProfileMonitorBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.DSLAMProfileMonitorService;

/**
 *
 */
@Test(groups = { BaseTest.SERVICE })
public class DSLAMProfileMonitorServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(DSLAMProfileMonitorServiceTest.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.DSLAMProfileMonitorService")
    private DSLAMProfileMonitorService sut;

    public void createDSLAMProfileMonitor() throws StoreException, FindException {
        Auftrag auftrag = getBuilder(AuftragBuilder.class).build();
        sut.createDSLAMProfileMonitor(auftrag.getId());
        flushAndClear();
        Collection<Long> auftragIds = sut.findCurrentlyMonitoredAuftragIds();
        assertNotNull(auftragIds);
        assertTrue(auftragIds.contains(auftrag.getId()));
    }

    static class TestDSLAMProfileMonitors {
        private Collection<TestDSLAMProfileMonitor> monitors;

        static TestDSLAMProfileMonitors entryList() {
            TestDSLAMProfileMonitors instance = new TestDSLAMProfileMonitors();
            instance.monitors = new ArrayList<DSLAMProfileMonitorServiceTest.TestDSLAMProfileMonitor>();
            return instance;
        }

        TestDSLAMProfileMonitors add(TestDSLAMProfileMonitor monitor) {
            monitors.add(monitor);
            return this;
        }

        TestDSLAMProfileMonitors addMultiple(int times, TestDSLAMProfileMonitor monitor) {
            for (int i = 0; i < times; i++) {
                monitors.add(monitor);
            }
            return this;
        }
    }

    static class TestDSLAMProfileMonitor {
        private boolean deleted = Boolean.FALSE;
        private Date ends = DateTools.plusWorkDays(2);

        static TestDSLAMProfileMonitor testObj() {
            return new TestDSLAMProfileMonitor();
        }

        TestDSLAMProfileMonitor withDeleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        TestDSLAMProfileMonitor withEnds(Date ends) {
            this.ends = ends;
            return this;
        }

    }

    static class DateHelper {
        private Calendar calendar = Calendar.getInstance();

        static DateHelper now() {
            return new DateHelper();
        }

        DateHelper hoursLater(int howMany) {
            return hoursEarlier(-howMany);
        }

        DateHelper hoursEarlier(int howMany) {
            return manipulation(-howMany, Calendar.HOUR_OF_DAY);
        }

        DateHelper minutesLater(int howMany) {
            return minutesEarlier(-howMany);
        }

        DateHelper minutesEarlier(int howMany) {
            return manipulation(-howMany, Calendar.MINUTE);
        }

        private DateHelper manipulation(int howMany, int what) {
            calendar.add(what, howMany);
            return this;
        }

        Date date() {
            return calendar.getTime();
        }

    }

    @DataProvider(name = "dataProviderFindCurrentlyMonitoredAuftragIds")
    protected Object[][] dataProviderFindCurrentlyMonitoredAuftragIds() {
        // @formatter:off
        return new Object[][] {
                { 0, entryList().add(testObj().withDeleted(true).withEnds(plusWorkDays(2))) }, // gelöscht und Ende in zwei Tagen
                { 0, entryList().addMultiple(5, testObj().withDeleted(true).withEnds(plusWorkDays(2))) }, // 5x gelöscht und Ende in zwei Tagen
                { 0, entryList().add(testObj().withDeleted(true).withEnds(minusWorkDays(1))) }, // gelöscht und Ende vor einem Tag
                { 0, entryList().add(testObj().withDeleted(false).withEnds(minusWorkDays(1))) }, // nicht gelöscht und Ende vor einem Tag
                { 0, entryList().add(testObj().withDeleted(false).withEnds(now().hoursEarlier(1).date())) }, // nicht gelöscht und Ende vor einer Stunde
                { 0, entryList().add(testObj().withDeleted(false).withEnds(now().minutesEarlier(1).date())) }, // nicht gelöscht und Ende vor einer Minute
                { 1, entryList().add(testObj().withDeleted(false).withEnds(plusWorkDays(2))) }, // nicht gelöscht und Ende in zwei Tagen
                { 1, entryList().add(testObj().withDeleted(false).withEnds(now().hoursLater(2).date())) }, // nicht gelöscht und Ende in zwei Stunden
                { 1, entryList().add(testObj().withDeleted(false).withEnds(now().minutesLater(2).date())) }, // nicht gelöscht und Ende in zwei Minuten
                { 5, entryList().addMultiple(5, testObj().withDeleted(false).withEnds(plusWorkDays(2))) }, // 5x nicht gelöscht und Ende in zwei Tagen
        };
        // @formatter:on
    }

    /**
     * Testet {@link DSLAMProfileMonitorService#findCurrentlyMonitoredAuftragIds()}.
     */
    @Test(dataProvider = "dataProviderFindCurrentlyMonitoredAuftragIds")
    public void findCurrentlyMonitoredAuftragIds(int expectedResult, TestDSLAMProfileMonitors entryList)
            throws FindException {
        Date now = new Date();
        Collection<DSLAMProfileMonitor> buildedMonitors = new ArrayList<DSLAMProfileMonitor>();
        Collection<Long> auftragIdsMonitored = new ArrayList<Long>();
        Collection<Long> auftragIdsNotMonitored = new ArrayList<Long>();
        for (TestDSLAMProfileMonitor monitor : entryList.monitors) {
            AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withRandomId();
            buildedMonitors.add(getBuilder(DSLAMProfileMonitorBuilder.class).withAuftragBuilder(auftragBuilder)
                    .withMonitoringEnds(monitor.ends).withDeleted(monitor.deleted).build());

            if (!monitor.deleted && DateTools.isAfter(monitor.ends, now)) {
                auftragIdsMonitored.add(auftragBuilder.get().getId());
            }
            else {
                auftragIdsNotMonitored.add(auftragBuilder.get().getId());
            }
        }
        flushAndClear();
        LOGGER.info(String.format("Erfolgreiches Speichern von %s.", buildedMonitors));
        Collection<Long> result = sut.findCurrentlyMonitoredAuftragIds();
        assertNotNull(result);
        assertTrue(result.size() >= expectedResult, "Anzahl Auftraege im DSLAM-Monitor zu gering!");

        assertTrue(CollectionUtils.isSubCollection(auftragIdsMonitored, result),
                "Es sind nicht alle erwarteten Auftraege in der DSLAM-Ueberwachung!");
        assertFalse(CollectionUtils.containsAny(auftragIdsNotMonitored, result),
                "Es sind nicht erwartete Auftraege in der DSLAM-Ueberwachung!");
    }

}
