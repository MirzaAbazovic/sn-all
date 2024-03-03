/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.2011 15:46:05
 */
package de.mnet.hurrican.webservice.monline.endpoint;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.mnet.hurricanweb.netid.customer.GetCustomerByIpRequestDocument;
import de.mnet.hurricanweb.netid.customer.GetCustomerByIpRequestDocument.GetCustomerByIpRequest;
import de.mnet.hurricanweb.netid.customer.GetCustomerByIpResponseDocument;

/**
 * Testet {@link IpAddressInfoEndpoint} samt allen Abhaengigkeiten.
 *
 *
 * @since 16.09.2011
 */
@Test(groups = BaseTest.UNIT)
public class IpAddressInfoEndpointServiceTest extends BaseTest {

    private IpAddressInfoEndpoint sut;

    @Mock
    IPAddressService ipAddressService;
    @Mock
    CCAuftragService ccAuftragService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new IpAddressInfoEndpoint();
        sut.setConverter(new CustomerOrderCombinationConverter());
        sut.auftragService = ccAuftragService;
        sut.ipAddressService = ipAddressService;
    }

    /**
     * Testet die Methode {@link IpAddressInfoEndpoint#getCustomerByIp(GetCustomerByIpRequestDocument)}.
     */
    public void getCustomerByIp_NothingFound() {
        GetCustomerByIpRequestDocument requestDocument = createRequestDocument(2000L, Calendar.getInstance());
        GetCustomerByIpResponseDocument responseDocument = sut.getCustomerByIp(requestDocument);
        Assert.assertNotNull(responseDocument);
        Assert.assertTrue(responseDocument.getGetCustomerByIpResponse().getCustomerOrderCombinationList().isEmpty());
    }

    /**
     * Testet die Methode {@link IpAddressInfoEndpoint#getCustomerByIp(GetCustomerByIpRequestDocument)}.
     *
     * @throws FindException
     */
    @SuppressWarnings("unchecked")
    public void getCustomerByIp_OneEntryFound() throws FindException {
        final long netId = 1000L;

        IPAddress ip = new IPAddressBuilder()
                .setPersist(false)
                .withAddressType(AddressTypeEnum.IPV4_prefix)
                .withAddress("192.168.0.0/16")
                .withBillingOrderNumber(123456L)
                .withNetId(netId)
                .build();

        when(ipAddressService.findAssignedIPs4NetId(any(Long.class), any(Date.class))).thenReturn(Arrays.asList(ip));

        GetCustomerByIpRequestDocument requestDocument = createRequestDocument(netId, Calendar.getInstance());
        GetCustomerByIpResponseDocument responseDocument = sut.getCustomerByIp(requestDocument);
        Assert.assertNotNull(responseDocument);
        Assert.assertTrue(responseDocument.getGetCustomerByIpResponse().getCustomerOrderCombinationList().isEmpty());
    }

    /**
     * liefert eine neue Instanz einer Webserviceanfrage mit den angegebenen Parametern.
     *
     * @param netId
     * @param dateActive
     * @return
     */
    private GetCustomerByIpRequestDocument createRequestDocument(Long netId, Calendar dateActive) {
        GetCustomerByIpRequestDocument requestDocument = GetCustomerByIpRequestDocument.Factory.newInstance();
        GetCustomerByIpRequest request = requestDocument.addNewGetCustomerByIpRequest();
        request.setNetId(netId);
        request.setDate(dateActive);
        return requestDocument;
    }


}


