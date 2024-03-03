/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.11.2014
 */
package de.mnet.hurrican.acceptance.role;

import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import com.consol.citrus.dsl.definition.SendMessageActionDefinition;
import com.consol.citrus.ws.client.WebServiceClient;
import com.consol.citrus.ws.message.SoapMessageHeaders;
import com.consol.citrus.ws.server.WebServiceServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 * Citrus test role for simulating CPS
 */
public class CpsTestRole extends AbstractTestRole {
    @Autowired
    @Qualifier("cpsSyncWebServer")
    private WebServiceServer cpsSyncWebServer;

    @Autowired
    @Qualifier("cpsAsyncWebServer")
    private WebServiceServer cpsAsyncWebServer;

    @Autowired
    @Qualifier("cpsSourceAgentClient")
    private WebServiceClient cpsSourceAgentClient;

    @Autowired
    @Qualifier("cpsSubscriberClient")
    private WebServiceClient cpsSubscriberClient;

    /**
     * Receives sync-request from hurrican.
     * @return
     */
    public ReceiveMessageActionDefinition receiveCpsSyncRequest(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(cpsSyncWebServer)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .schemaValidation(false)
                .description(String.format("Receiving CPS sync-request %s", payloadTemplate));
    }

    /**
     * Sends sync-response to hurrican.
     * @return
     */
    public SendMessageActionDefinition sendCpsSyncResponse(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(cpsSyncWebServer)
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Sending CPS sync-response %s", payloadTemplate));
    }

    /**
     * Receives async-request from hurrican.
     * @return
     */
    public ReceiveMessageActionDefinition receiveCpsAsyncServiceRequest(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(cpsAsyncWebServer)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .extractFromPayload("//*[local-name() = 'TransactionId']", VariableNames.CPS_TX_ID)
                .schemaValidation(false)
                .description(String.format("Receiving CPS async-request %s", payloadTemplate));
    }

    /**
     * Sends async-acknowledgement to hurrican.
     * @return
     */
    public SendMessageActionDefinition sendCpsAsyncServiceRequestAck(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(cpsAsyncWebServer)
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Sending CPS async-request-ack %s", payloadTemplate));
    }

    /**
     * Sends Http error as service request response to hurrican.
     * @return
     */
    public SendMessageActionDefinition sendCpsAsyncServiceRequestHttpError(HttpStatus status) {
        return (SendMessageActionDefinition) testBuilder.send(cpsAsyncWebServer)
                .soap()
                .header(SoapMessageHeaders.HTTP_STATUS_CODE, String.valueOf(status.value()))
                .description(String.format("Sending CPS http error %s", status));
    }

    /**
     * Sends async-response to hurrican.
     * @return
     */
    public SendMessageActionDefinition sendCpsAsyncServiceResponse(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(cpsSourceAgentClient)
                .soap()
                .fork(true)
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Sending CPS async-response %s", payloadTemplate));
    }

    /**
     * Receives async-acknowledgement from hurrican.
     * @return
     */
    public ReceiveMessageActionDefinition receiveCpsAsyncServiceResponseAck(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(cpsSourceAgentClient)
                .payload(getXmlTemplate(payloadTemplate))
                .schemaValidation(false)
                .description(String.format("Receiving CPS async-response-ack %s", payloadTemplate));
    }

    public SendMessageActionDefinition sendSoDataRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(cpsSubscriberClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Sending CPS 'GetSoDataRequest' to Hurrican %s", payloadTemplate));

    }

    public ReceiveMessageActionDefinition receiveSoDataRepsonse(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(cpsSubscriberClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .schemaValidation(false)
                .description(String.format("Receiving CPS 'GetSoDataResponse' from Hurrican %s", payloadTemplate));
    }

    public SendMessageActionDefinition sendProvisionRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(cpsSubscriberClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Sending CPS 'ProvisionRequest' to Hurrican %s", payloadTemplate));

    }

    public ReceiveMessageActionDefinition receiveProvisionRepsonse(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(cpsSubscriberClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .schemaValidation(false)
                .description(String.format("Receiving CPS 'ProvisonResponse' from Hurrican %s", payloadTemplate));
    }
}
