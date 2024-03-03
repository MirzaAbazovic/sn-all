/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2012 11:01:40
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.dao.cc.DSLAMProfileMonitorDAO;
import de.augustakom.hurrican.model.cc.DSLAMProfileMonitor;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.impl.command.dslamprofilemonitor.CheckNeedsDSLAMProfileMonitoringCommand;
import de.mnet.common.service.locator.ServiceLocator;

/**
 *
 */
@Test(groups = { BaseTest.UNIT })
public class DSLAMProfileMonitorServiceImplTest extends BaseTest {

    private DSLAMProfileMonitorServiceImpl sut;

    @Mock
    private ServiceLocator serviceLocator;
    @Mock
    private CheckNeedsDSLAMProfileMonitoringCommand needsMonitorinCmdMock;
    @Mock
    private DSLAMProfileMonitorDAO dslamProfileMonitorDao;
    @Mock
    private CPSService cpsService;

    @BeforeMethod
    void setUp() {
        sut = new DSLAMProfileMonitorServiceImpl();
        initMocks(this);
        ReflectionTestUtils.setField(sut, "dslamProfileMonitorDao", dslamProfileMonitorDao);
        ReflectionTestUtils.setField(sut, "cpsService", cpsService);
    }

    @DataProvider(name = "needsMonitoringDP")
    public Object[][] needsMonitoring() {
        return new Object[][] { { true }, { false } };
    }

    @Test(dataProvider = "needsMonitoringDP")
    public void needsMonitoring(boolean expected) throws Exception {
        final Long ccAuftragId = Long.MAX_VALUE;
        ReflectionTestUtils.setField(sut, "serviceLocator", serviceLocator);
        when(serviceLocator.getCmdBean(eq(CheckNeedsDSLAMProfileMonitoringCommand.class))).thenReturn(
                needsMonitorinCmdMock);
        when(needsMonitorinCmdMock.execute()).thenReturn(expected);
        assertEquals(sut.needsMonitoring(ccAuftragId), expected);
        verify(needsMonitorinCmdMock, times(1)).prepare(eq(CheckNeedsDSLAMProfileMonitoringCommand.CCAUFTRAG_ID),
                eq(ccAuftragId));
        verify(needsMonitorinCmdMock, times(1)).execute();
    }

    public void deactivateMonitoring() throws FindException {
        final Long auftragId = RandomTools.createLong();
        final DSLAMProfileMonitor profileMonitor = new DSLAMProfileMonitor();
        profileMonitor.setDeleted(false);
        when(dslamProfileMonitorDao.findByAuftragId(eq(auftragId))).thenReturn(profileMonitor);
        sut.deactivateMonitoring(auftragId);
        assertTrue(profileMonitor.getDeleted());
        verify(dslamProfileMonitorDao, times(1)).findByAuftragId(eq(auftragId));
        verify(dslamProfileMonitorDao, times(1)).store(eq(profileMonitor));
    }

    public void cpsQueryAttainableBitrate() throws FindException {
        final Long auftragId = RandomTools.createLong();
        final Long sessionId = RandomTools.createLong();
        Pair<Integer, Integer> expected = new Pair<Integer, Integer>(RandomTools.createInteger(),
                RandomTools.createInteger());
        when(cpsService.queryAttainableBitrate(eq(auftragId), eq(sessionId))).thenReturn(expected);
        Pair<Integer, Integer> result = sut.cpsQueryAttainableBitrate(auftragId, sessionId);
        assertEquals(result.getFirst(), expected.getFirst());
        assertEquals(result.getSecond(), expected.getSecond());
    }
}
