/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.2012 18:19:58
 */
package de.mnet.hurrican.e2e.monline;

import static de.augustakom.common.BaseTest.*;

import java.util.*;
import javax.annotation.*;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.testng.annotations.Test;

import de.mnet.hurrican.e2e.common.BaseHurricanE2ETest;
import de.mnet.hurricanweb.netid.customer.GetCustomerByIpRequestDocument;
import de.mnet.hurricanweb.netid.customer.GetCustomerByIpRequestDocument.GetCustomerByIpRequest;

@Test(groups = E2E)
public class IpAddressEndpointTest extends BaseHurricanE2ETest {

    @Resource(name = "ipAddressWebServiceTemplate")
    private WebServiceTemplate ipAddressWs;

    public void testGetCustomer() {
        GetCustomerByIpRequestDocument requestDocument = GetCustomerByIpRequestDocument.Factory.newInstance();
        GetCustomerByIpRequest request = requestDocument.addNewGetCustomerByIpRequest();
        request.setNetId(15);
        request.setDate(Calendar.getInstance());

        ipAddressWs.marshalSendAndReceive(requestDocument);
    }
}


