/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.11.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.hurrican.service.location.CamelProxyLookupService.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.hurrican.service.location.CamelProxyLookupService;
import de.mnet.common.customer.service.CustomerService;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.ticketing.customerservice.CustomerServiceProtocolGenerator;

@Test(groups = UNIT)
public class WbciCustomerServiceImplTest {
    @InjectMocks
    private WbciCustomerServiceImpl testling = new WbciCustomerServiceImpl();

    @Mock
    private CustomerService customerServiceMock;

    @Mock
    private CamelProxyLookupService camelProxyLookupServiceMock;

    @Mock
    private CustomerServiceProtocolGenerator customerServiceProtocolGenerator;

    @Mock
    private WbciMessage wbciMessageMock;

    @Mock
    private AddCommunication csMessageMock;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(camelProxyLookupServiceMock.lookupCamelProxy(PROXY_CUSTOMER_SERVICE, CustomerService.class)).thenReturn(customerServiceMock);
    }

    @Test
    public void testSendCustomerServiceProtocol() throws Exception {
        when(customerServiceProtocolGenerator.generateCustomerServiceProtocol(wbciMessageMock)).thenReturn(csMessageMock);
        testling.sendCustomerServiceProtocol(wbciMessageMock);
        verify(customerServiceMock).sendCustomerServiceProtocol(csMessageMock);
    }

    @Test
    public void testSkipWhenSendCustomerServiceProtocolIsEmpty() throws Exception {
        when(customerServiceProtocolGenerator.generateCustomerServiceProtocol(wbciMessageMock)).thenReturn(null);
        testling.sendCustomerServiceProtocol(wbciMessageMock);
        verify(customerServiceMock, never()).sendCustomerServiceProtocol(csMessageMock);
    }

    @Test
    public void testNoExceptionIsPropagated() throws Exception {
        when(customerServiceProtocolGenerator.generateCustomerServiceProtocol(wbciMessageMock)).thenReturn(csMessageMock);
        doThrow(new RuntimeException()).when(customerServiceMock).sendCustomerServiceProtocol(csMessageMock);
        testling.sendCustomerServiceProtocol(wbciMessageMock);
    }
}
