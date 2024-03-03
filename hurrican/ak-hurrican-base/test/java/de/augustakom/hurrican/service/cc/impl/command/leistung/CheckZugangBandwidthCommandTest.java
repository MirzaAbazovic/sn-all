/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.04.2012 13:10:20
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * {@link CheckZugangBandwidthCommand} UNIT Test
 */
@Test(groups = BaseTest.UNIT)
public class CheckZugangBandwidthCommandTest extends BaseTest {

    @Spy
    private CheckZugangBandwidthCommand cut = new CheckZugangBandwidthCommand();
    @Mock
    private EndstellenService endstellenServiceMock;
    @Mock
    private RangierungsService rangierungsService;

    @BeforeMethod
    void setUp() throws ServiceNotFoundException, FindException {
        initMocks(this);
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
                { false, false, true  },
                { true,  false, false },
                { true,  true,  true  },
            };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderExecute")
    void testExecute(boolean rangierungAvailable, boolean isBandwidthPossible, boolean isExpectedStatusOK)
            throws Exception {
        Rangierung rangierung = null;
        if (rangierungAvailable) {
            rangierung = new Rangierung();
        }
        doReturn(rangierung).when(cut).getRangierung();

        TechLeistung techLeistung = new TechLeistung();
        techLeistung.setLongValue(0L);
        doReturn(techLeistung).when(cut).getTechLeistung();

        when(rangierungsService.isBandwidthPossible4Rangierung(any(Rangierung.class), any(Bandwidth.class))).thenReturn(
                isBandwidthPossible);

        ServiceCommandResult result = cut.execute();

        assertNotNull(result);
        assertThat(result.isOk(), equalTo(isExpectedStatusOK));
    }

    @Test(expectedExceptions = { FindException.class })
    void testExecuteWithFindException() throws Exception {
        doThrow(new FindException("Error")).when(cut).loadRequiredData();
        cut.execute();
    }

}


