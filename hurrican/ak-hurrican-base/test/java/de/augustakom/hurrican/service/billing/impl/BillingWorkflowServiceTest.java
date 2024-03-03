/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.06.2009 07:46:07
 */
package de.augustakom.hurrican.service.billing.impl;

import java.io.*;
import javax.annotation.*;
import ch.ergon.taifun.ws.messages.UpdateHurricanOrderStateResponseDocument;
import ch.ergon.taifun.ws.messages.UpdateHurricanOrderStateResponseType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.ws.test.client.RequestMatchers;
import org.springframework.ws.test.client.ResponseCreators;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.ws.MnetWebServiceTemplate;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.billing.BillingWorkflowService;

/**
 * TestCase fuer den Service <code>BillingWorkflowService</code>.
 *
 *
 */
@Test(groups = { BaseTest.SERVICE })
public class BillingWorkflowServiceTest extends AbstractHurricanBaseServiceTest {

    @Resource(name = "billingWSTemplate")
    private MnetWebServiceTemplate billingWsTemplate;

    @DataProvider(name = "testChangeOrderStateDP")
    public Object[][] testChangeOrderStateDP() {
        return new Object[][] {
                { Boolean.TRUE, true },
                { Boolean.FALSE, false },
                { null, false }
        };
    }

    @Test(dataProvider = "testChangeOrderStateDP")
    public void testChangeOrderState(Boolean statusmeldung, boolean mockWs) throws Exception {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder).withStatusmeldungen(statusmeldung)
                .withStatusId(AuftragStatus.ABSAGE);

        Long auftragId = auftragDatenBuilder.build().getAuftragId();
        flushAndClear();

        BillingWorkflowService bws = getBillingService(BillingWorkflowService.class);

        //mock webservice call if webservice call is expected (statusmeldung == true)
        if (BooleanTools.nullToFalse(mockWs)) {
            mockWS(bws);
        }

        bws.changeOrderState(auftragId, Long.valueOf(4100), "testcase", getSessionId());

        //no assertions necessary because test crashes if billing-webservice is called
        //and mockWS is false
    }

    private void mockWS(BillingWorkflowService bws) throws ServiceCommandException, IOException {
        MockWebServiceServer mockServer = MockWebServiceServer.createServer(billingWsTemplate);
        UpdateHurricanOrderStateResponseDocument respDoc = UpdateHurricanOrderStateResponseDocument.Factory
                .newInstance();
        UpdateHurricanOrderStateResponseType resp = UpdateHurricanOrderStateResponseType.Factory.newInstance();
        resp.setSuccessful(true);
        respDoc.setUpdateHurricanOrderStateResponse(resp);
        mockServer.expect(RequestMatchers.anything()).andRespond(
                ResponseCreators.withPayload(new InputStreamResource(respDoc.newInputStream())));
    }
}
