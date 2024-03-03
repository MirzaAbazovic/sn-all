/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.07.13 13:21
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.RegistryService;

/**
 *
 */
@Test(groups = { BaseTest.UNIT })
public class HVTToolServiceImplTest {

    private static final long OLT_ID = 1L;

    @Spy
    @InjectMocks
    HVTToolServiceImpl cut;

    @Mock
    HWService hwService;
    @Mock
    AKUserService userService;
    @Mock
    RangierungsService rs;
    @Mock
    EndstellenService ess;
    @Mock
    RegistryService regService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(hwService).when(cut).getCCService(HWService.class);
        doReturn(rs).when(cut).getCCService(RangierungsService.class);
        doReturn(ess).when(cut).getCCService(EndstellenService.class);
        doReturn(regService).when(cut).getCCService(RegistryService.class);
        when(userService.findUserBySessionId(anyLong())).thenReturn(new AKUser());
    }

}
