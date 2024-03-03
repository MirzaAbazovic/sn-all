/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 06.11.13 
 */
package de.mnet.wbci.ticketing.customerservice;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.ticketing.customerservice.converter.CustomerServiceProtocolEnricher;

@Test(groups = BaseTest.UNIT)
public class CustomerServiceProtocolGeneratorTest {
    @Mock
    private WbciMessage wbciMessageMock;

    @Mock
    private WbciGeschaeftsfall wbciGeschaeftsfallMock;

    @Mock
    private CustomerServiceProtocolEnricher customerServiceProtocolEnricherMock;

    @Mock
    private List<CustomerServiceProtocolEnricher> protocolEnricherListMock;

    @Mock
    private BillingAuftragService billingAuftragServiceMock;

    @Mock
    private BAuftrag bAuftragMock;

    @InjectMocks
    private CustomerServiceProtocolGenerator testling = new CustomerServiceProtocolGenerator();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLookupBillingAuftrag() throws Exception {
        Long expAuftragId = 123L;
        initMocks(expAuftragId, 1L, 1L);
        BAuftrag bAuftrag = testling.lookupBillingAuftrag(wbciMessageMock);
        Assert.assertEquals(bAuftrag, bAuftragMock);
    }

    @Test
    public void testLookupBillingAuftragWhenNoTaifunOrderIdSet() throws Exception {
        initMocks(null, null, null);
        BAuftrag bAuftrag = testling.lookupBillingAuftrag(wbciMessageMock);
        Assert.assertNull(bAuftrag);
    }

    @Test
    public void testGenerateBsiProtocol() throws Exception {
        Long expAuftragId = 123L;
        Long expCustomerNr = 234L;
        Long expContractNr = 345L;
        initMocks(expAuftragId, expCustomerNr, expContractNr);
        when(protocolEnricherListMock.iterator()).thenReturn(Arrays.asList(customerServiceProtocolEnricherMock).iterator());
        when(customerServiceProtocolEnricherMock.supports(any(WbciMessage.class))).thenReturn(true);
        AddCommunication csProtocol = testling.generateCustomerServiceProtocol(wbciMessageMock);

        verify(customerServiceProtocolEnricherMock).enrich(any(WbciMessage.class), any(AddCommunication.class));

        Assert.assertEquals(csProtocol.getContext().getContractId(), String.valueOf(expContractNr));
        Assert.assertEquals(csProtocol.getCustomerId(), String.valueOf(expCustomerNr));
        Assert.assertNotNull(csProtocol.getTime());
    }

    @Test
    public void testGenerateBsiProtocolWhenNoMatchingEnricherFound() throws Exception {
        Long expAuftragId = 123L;
        Long expCustomerNr = 234L;
        Long expContractNr = 345L;
        initMocks(expAuftragId, expCustomerNr, expContractNr);
        when(protocolEnricherListMock.iterator()).thenReturn(Arrays.asList(customerServiceProtocolEnricherMock).iterator());
        when(customerServiceProtocolEnricherMock.supports(any(WbciMessage.class))).thenReturn(false);

        AddCommunication csProtocol = testling.generateCustomerServiceProtocol(wbciMessageMock);
        Assert.assertNull(csProtocol);
    }

    @Test
    public void testGenerateBsiProtocolWhenNoAuftragIdSet() throws Exception {
        initMocks(null, null, null);
        testling.generateCustomerServiceProtocol(wbciMessageMock);
    }

    private void initMocks(Long taifunAuftragId, Long customerNr, Long contractNr) throws FindException {
        when(wbciMessageMock.getWbciGeschaeftsfall()).thenReturn(wbciGeschaeftsfallMock);
        when(wbciGeschaeftsfallMock.getBillingOrderNoOrig()).thenReturn(taifunAuftragId);
        when(billingAuftragServiceMock.findAuftrag(taifunAuftragId)).thenReturn(bAuftragMock);
        when(bAuftragMock.getKundeNo()).thenReturn(customerNr);
        when(bAuftragMock.getAuftragNoOrig()).thenReturn(contractNr);
    }
}
