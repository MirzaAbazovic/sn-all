/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.01.2013 09:53:18
 */
package de.augustakom.hurrican.service.cc.impl.command.ipaddress;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.lang.reflect.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.IPAddressService;

/**
 * TestNG Klasse fuer {@link MoveIpAddressesCommand}
 */
@Test(groups = { BaseTest.UNIT })
public class MoveIpAddressesCommandTest extends BaseTest {

    @Mock
    CCAuftragService auftragServiceMock;
    @Mock
    IPAddressService ipAddressServiceMock;
    @InjectMocks
    @Spy
    MoveIpAddressesCommand cut;

    @BeforeMethod
    public void setUp(Method method) throws FindException, StoreException {
        cut = new MoveIpAddressesCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsMoveNecessary_ResultFalse() throws FindException, ServiceNotFoundException {
        Long billingOrderNoSrc = Long.valueOf(1L);
        doReturn(billingOrderNoSrc).when(cut).getBillingOrderNoSrc();
        when(ipAddressServiceMock.findAssignedIPs4BillingOrder(billingOrderNoSrc)).thenReturn(
                new ArrayList<IPAddress>());
        assertFalse(cut.isMoveNecessary());
        verify(cut, times(1)).getBillingOrderNoSrc();
        verify(ipAddressServiceMock, times(1)).findAssignedIPs4BillingOrder(billingOrderNoSrc);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIsMoveNecessary_ResultTrue() throws FindException, ServiceNotFoundException {
        Long billingOrderNoSrc = Long.valueOf(1L);
        doReturn(billingOrderNoSrc).when(cut).getBillingOrderNoSrc();
        when(ipAddressServiceMock.findAssignedIPs4BillingOrder(billingOrderNoSrc)).thenReturn(
                Arrays.asList(new IPAddress[] { new IPAddress() }));
        assertTrue(cut.isMoveNecessary());
        verify(cut, times(1)).getBillingOrderNoSrc();
        verify(ipAddressServiceMock, times(1)).findAssignedIPs4BillingOrder(billingOrderNoSrc);
    }

    @Test
    public void testIsSameBillingOrder_ResultFalse() throws FindException {
        Long billingOrderNoSrc = Long.valueOf(1L);
        Long billingOrderNoDest = Long.valueOf(2L);
        doReturn(billingOrderNoSrc).when(cut).getBillingOrderNoSrc();
        doReturn(billingOrderNoDest).when(cut).getBillingOrderNoDest();
        assertFalse(cut.isSameBillingOrder());
        verify(cut, times(1)).getBillingOrderNoSrc();
        verify(cut, times(1)).getBillingOrderNoDest();
    }

    @Test
    public void testIsSameBillingOrder_ResultTrue() throws FindException {
        Long billingOrderNoSrc = Long.valueOf(1L);
        Long billingOrderNoDest = Long.valueOf(1L);
        doReturn(billingOrderNoSrc).when(cut).getBillingOrderNoSrc();
        doReturn(billingOrderNoDest).when(cut).getBillingOrderNoDest();
        assertTrue(cut.isSameBillingOrder());
        verify(cut, times(1)).getBillingOrderNoSrc();
        verify(cut, times(1)).getBillingOrderNoDest();
    }

    @Test
    public void testIsSameCustomer_ResultFalse() throws FindException, ServiceNotFoundException {
        Auftrag auftragSrc = new AuftragBuilder().withRandomId().withKundeNo(Long.valueOf(1L)).setPersist(false)
                .build();
        Auftrag auftragDest = new AuftragBuilder().withRandomId().withKundeNo(Long.valueOf(2L)).setPersist(false)
                .build();
        when(auftragServiceMock.findAuftragById(auftragSrc.getAuftragId())).thenReturn(auftragSrc);
        when(auftragServiceMock.findAuftragById(auftragDest.getAuftragId())).thenReturn(auftragDest);
        doReturn(auftragSrc.getAuftragId()).when(cut).getAuftragIdSrc();
        doReturn(auftragDest.getAuftragId()).when(cut).getAuftragIdDest();
        assertFalse(cut.isSameCustomer());
        verify(auftragServiceMock, times(1)).findAuftragById(auftragSrc.getAuftragId());
        verify(auftragServiceMock, times(1)).findAuftragById(auftragDest.getAuftragId());
    }

    @Test
    public void testIsSameCustomer_ResultTrue() throws FindException, ServiceNotFoundException {
        Auftrag auftragSrc = new AuftragBuilder().withRandomId().withKundeNo(Long.valueOf(1L)).setPersist(false)
                .build();
        Auftrag auftragDest = new AuftragBuilder().withRandomId().withKundeNo(Long.valueOf(1L)).setPersist(false)
                .build();
        when(auftragServiceMock.findAuftragById(auftragSrc.getAuftragId())).thenReturn(auftragSrc);
        when(auftragServiceMock.findAuftragById(auftragDest.getAuftragId())).thenReturn(auftragDest);
        doReturn(auftragSrc.getAuftragId()).when(cut).getAuftragIdSrc();
        doReturn(auftragDest.getAuftragId()).when(cut).getAuftragIdDest();
        assertTrue(cut.isSameCustomer());
        verify(auftragServiceMock, times(1)).findAuftragById(auftragSrc.getAuftragId());
        verify(auftragServiceMock, times(1)).findAuftragById(auftragDest.getAuftragId());
    }

    @DataProvider
    public Object[][] dataProviderExecute() {
        List<IPAddress> empty = new ArrayList<IPAddress>();
        List<IPAddress> ipAddressesSrc = new ArrayList<IPAddress>();
        List<IPAddress> ipAddressesDest = new ArrayList<IPAddress>();
        ipAddressesSrc.add(new IPAddress());
        ipAddressesSrc.add(new IPAddress());
        ipAddressesDest.add(new IPAddress());
        // @formatter:off
        return new Object[][] {
                { false, false, true, empty, empty, 0, 0 },
                { true, true, true, empty, empty, 0, 0 },
                { true, false, false, empty, empty, 0, 0 },
                { true, false, true, ipAddressesSrc, empty, 2, 0 },
                { true, false, true, ipAddressesSrc, ipAddressesDest, 2, 1 },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderExecute")
    public void testExecute(boolean isSameCustomer, boolean isSameBillingOrder, boolean isMoveNecessary,
            List<IPAddress> ipAddressesSrc, List<IPAddress> ipAddressesDest, int moveCount, int releaseCount)
            throws Exception {
        Long billingOrderNoSrc = Long.valueOf(1L);
        Long billingOrderNoDest = Long.valueOf(2L);
        Long sessionId = Long.valueOf(1L);
        doReturn(billingOrderNoSrc).when(cut).getBillingOrderNoSrc();
        doReturn(billingOrderNoDest).when(cut).getBillingOrderNoDest();
        doReturn(sessionId).when(cut).getSessionId();
        doReturn(isSameCustomer).when(cut).isSameCustomer();
        doReturn(isSameBillingOrder).when(cut).isSameBillingOrder();
        doReturn(isMoveNecessary).when(cut).isMoveNecessary();
        when(ipAddressServiceMock.findAssignedIPs4BillingOrder(billingOrderNoSrc)).thenReturn(ipAddressesSrc);
        when(ipAddressServiceMock.findAssignedIPs4BillingOrder(billingOrderNoDest)).thenReturn(ipAddressesDest);
        when(ipAddressServiceMock.releaseIPAddressesNow(ipAddressesDest, sessionId)).thenReturn(new AKWarnings());

        cut.execute();

        verify(ipAddressServiceMock, times(moveCount)).moveIPAddress(any(IPAddress.class), eq(billingOrderNoDest),
                eq(sessionId));
        verify(ipAddressServiceMock, times(releaseCount)).releaseIPAddressesNow(ipAddressesDest, sessionId);
    }
}


