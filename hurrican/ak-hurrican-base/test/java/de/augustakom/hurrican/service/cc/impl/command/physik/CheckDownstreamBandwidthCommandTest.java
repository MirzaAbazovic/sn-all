/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.06.2012 14:48:37
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * TestNG Klasse fuer {@link CheckAnschlussuebernahmeBandwidth}
 */
@Test(groups = { BaseTest.UNIT })
public class CheckDownstreamBandwidthCommandTest extends BaseTest {
    @Spy
    private CheckDownstreamBandwidthCommand cut = new CheckDownstreamBandwidthCommand();
    @Mock
    private CCLeistungsService ccLeistungsServiceMock;
    @Mock
    private EndstellenService endstellenServiceMock;
    @Mock
    private RangierungsService rangierungsService;

    @BeforeMethod
    void setUp() throws ServiceNotFoundException, FindException {
        initMocks(this);
        doReturn(ccLeistungsServiceMock).when(cut).getCCService(CCLeistungsService.class);
        doReturn(endstellenServiceMock).when(cut).getCCService(EndstellenService.class);
        doReturn(rangierungsService).when(cut).getCCService(RangierungsService.class);
        doNothing().when(cut).loadRequiredData();
    }

    @DataProvider
    public Object[][] dataProviderExecute() {
        // @formatter:off
        return new Object[][] {
                //boolean rangierungAvailable,
                //       boolean isBandwidthPossible,
                //              boolean isExpectedStatusOK
                { false, false },
                { true,  true },
            };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderExecute")
    void testExecuteSuccess(boolean rangierungAvailable, boolean isBandwidthPossible) throws Exception {
        doReturn((rangierungAvailable) ? new Rangierung() : null).when(cut).getRangierung();
        when(rangierungsService.isBandwidthPossible4Rangierung(any(Rangierung.class), any(Bandwidth.class))).thenReturn(
                isBandwidthPossible);
        cut.execute();
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    void testExecuteWithBandwithNotPossible() throws Exception {
        doReturn(new Rangierung()).when(cut).getRangierung();
        when(rangierungsService.isBandwidthPossible4Rangierung(any(Rangierung.class), any(Bandwidth.class))).thenReturn(false);
        cut.execute();
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    void testExecuteWithFindException() throws Exception {
        doThrow(new FindException("Error")).when(cut).loadRequiredData();
        cut.execute();
    }

}


