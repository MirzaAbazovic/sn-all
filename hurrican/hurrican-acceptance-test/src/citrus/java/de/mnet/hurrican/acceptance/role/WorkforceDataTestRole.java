/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2015
 */
package de.mnet.hurrican.acceptance.role;

import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import com.consol.citrus.dsl.definition.SendMessageActionDefinition;
import com.consol.citrus.ws.client.WebServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 */
public class WorkforceDataTestRole  extends AbstractTestRole {

    @Autowired
    @Qualifier("workforceDataV1Client")
    private WebServiceClient workforceDataClient;

    public SendMessageActionDefinition sendGetWorkforceDataRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(workforceDataClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Sending getWorkforceData Web Service request %s", payloadTemplate));
    }

    public ReceiveMessageActionDefinition receiveGetWorkforceDataResponse(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(workforceDataClient)
                // TODO einschalten, wenn WSDL import von citrus erkannt wird.
                .schemaValidation(false)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Receiving getWorkforceData Web Service response %s", payloadTemplate));
    }

}