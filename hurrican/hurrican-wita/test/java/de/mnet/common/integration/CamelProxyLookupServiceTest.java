/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.13
 */
package de.mnet.common.integration;

import static de.augustakom.hurrican.service.location.CamelProxyLookupService.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.location.CamelProxyLookupService;

@Test(groups = BaseTest.UNIT)
public class CamelProxyLookupServiceTest {
    @InjectMocks
    private CamelProxyLookupService testling = new CamelProxyLookupService();

    @Mock
    protected ApplicationContext applicationContextMock;

    @Mock
    protected Object proxyMock;

    @BeforeMethod
    public void setupMockListener() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLookupCamelProxy() throws Exception {
        when(applicationContextMock.getBean(PROXY_CARRIER_NEGOTIATION, Object.class)).thenReturn(proxyMock);
        Object camelProxy = testling.lookupCamelProxy(PROXY_CARRIER_NEGOTIATION, Object.class);
        Assert.assertEquals(camelProxy, proxyMock);
    }
}
