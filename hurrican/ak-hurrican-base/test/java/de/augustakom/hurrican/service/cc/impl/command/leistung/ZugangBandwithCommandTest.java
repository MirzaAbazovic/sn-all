/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.04.2012 14:14:34
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * {@link ZugangBandwithCommand} UNIT Test
 */
public class ZugangBandwithCommandTest extends BaseTest {

    @Spy
    private ZugangBandwithCommand cut = new ZugangBandwithCommand();
    @Mock
    private EndstellenService endstellenServiceMock;
    @Mock
    private RangierungsService rangierungsService;

    @BeforeMethod
    void setUp() throws ServiceNotFoundException, FindException {
        initMocks(this);
        doReturn(endstellenServiceMock).when(cut).getCCService(EndstellenService.class);
        doReturn(rangierungsService).when(cut).getCCService(RangierungsService.class);
    }

    private void mockData4SuperExecute(Rangierung rangierung, boolean isBandwidthPossible) throws FindException {
        if (rangierung != null) {
            rangierung = new Rangierung();
        }
        doReturn(rangierung).when((CheckZugangBandwidthCommand) cut).getRangierung();

        TechLeistung techLeistung = new TechLeistung();
        techLeistung.setLongValue(0L);
        doReturn(techLeistung).when(cut).getTechLeistung();

        when(rangierungsService.isBandwidthPossible4Rangierung(any(Rangierung.class), any(Bandwidth.class))).thenReturn(
                isBandwidthPossible);
    }

    @Test
    public void testExecuteSuccess() throws Exception {
        doNothing().when(cut).loadRequiredData();
        mockData4SuperExecute(new Rangierung(), true);

        ServiceCommandResult result = cut.execute();

        assertNotNull(result);
        assertTrue(result.isOk());
    }

    @Test
    public void testExecuteSuccessWithFindException() throws Exception {
        doThrow(new FindException("Exception in loadRequiredData")).when(cut).loadRequiredData();
        mockData4SuperExecute(new Rangierung(), true);

        ServiceCommandResult result = cut.execute();

        assertNotNull(result);
        assertTrue(result.isOk());
    }

    @Test(expectedExceptions = HurricanServiceCommandException.class)
    public void testExecuteStatusInvalid() throws Exception {
        doNothing().when(cut).loadRequiredData();
        mockData4SuperExecute(new Rangierung(), false);

        cut.execute();

        fail();
    }
}


