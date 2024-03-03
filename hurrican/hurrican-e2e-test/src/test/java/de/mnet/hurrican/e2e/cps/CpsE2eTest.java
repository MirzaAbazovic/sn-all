/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2012 10:38:42
 */
package de.mnet.hurrican.e2e.cps;

import static de.augustakom.common.BaseTest.*;

import java.time.*;
import javax.annotation.*;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.testng.annotations.Test;

import de.mnet.hurrican.cps.GetSoDataRequest;
import de.mnet.hurrican.cps.OrderQuery;
import de.mnet.hurrican.e2e.common.BaseHurricanE2ETest;
import de.mnet.hurrican.webservice.cps.CpsEndpointUnitTest;

/**
 * Prüft für Gut und Fail-Fall die Funktion des Endpoints und die Serialisierbarkeit der Daten. Funktionale Test des
 * Endpoints werden im Acceptance-Test 'Hurrican_CPS_Endpoint_Test' bzw. {@link CpsEndpointUnitTest} geprueft.
 */
@Test(groups = E2E)
public class CpsE2eTest extends BaseHurricanE2ETest {

    @Resource(name = "cpsSupportWebServiceTemplate")
    protected WebServiceTemplate webServiceTemplate;

    @SuppressWarnings("unchecked")
    private <T> T doRequest(Object request) {
        Object respose = webServiceTemplate.marshalSendAndReceive(request);
        return (T) respose;
    }

    @Test(expectedExceptions = SoapFaultClientException.class)
    public void negativeOrderNoYieldsException() throws Exception {
        GetSoDataRequest request = new GetSoDataRequest();
        OrderQuery query = new OrderQuery();
        query.setBillingOrderNo(-1L);
        request.setQuery(query);
        request.setWhen(LocalDate.now());

        doRequest(request);
    }

    @Test(expectedExceptions = SoapFaultClientException.class)
    public void dateInPastYieldsException() throws Exception {
        GetSoDataRequest request = new GetSoDataRequest();
        OrderQuery query = new OrderQuery();
        query.setBillingOrderNo(1L);
        request.setQuery(query);
        request.setWhen(LocalDate.now().minusDays(1));

        doRequest(request);
    }

}
