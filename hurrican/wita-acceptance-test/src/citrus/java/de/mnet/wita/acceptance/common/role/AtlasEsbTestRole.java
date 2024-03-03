/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2015
 */
package de.mnet.wita.acceptance.common.role;

import com.consol.citrus.TestActor;
import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import com.consol.citrus.dsl.definition.SendMessageActionDefinition;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import com.consol.citrus.jms.message.JmsMessageHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;

/**
 *
 */
public class AtlasEsbTestRole extends AbstractTestRole {

    @Autowired
    @Qualifier("atlasEsbTestActor")
    protected TestActor atlasEsbTestActor;

    @Autowired
    @Qualifier("atlasLineOrderServiceSrcEndpoint")
    private JmsEndpoint atlasLineOrderServiceSrcEndpoint;

    @Autowired
    @Qualifier("atlasLineOrderNotificationServiceDstEndpoint")
    private JmsEndpoint atlasLineOrderNotificationServiceDstEndpoint;

    @Autowired
    @Qualifier("atlasLineOrderServiceSrcEndpointV2")
    private JmsEndpoint atlasLineOrderServiceSrcEndpointV2;

    @Autowired
    @Qualifier("atlasLineOrderNotificationServiceDstEndpointV2")
    private JmsEndpoint atlasLineOrderNotificationServiceDstEndpointV2;

    @Autowired
    @Qualifier("errorHandlingServiceEndpoint")
    private JmsEndpoint errorHandlingServiceEndpoint;

    @Autowired
    @Qualifier("atlasDocumentArchiveServiceSrcEndpoint")
    private JmsEndpoint atlasDocumentArchiveServiceSrcEndpoint;


    public ReceiveMessageActionDefinition receiveGetDocumentRequest(String payloadTemplate) {
        return receiveDocumentArchiveRequest(payloadTemplate, "/DocumentArchiveService/getDocument",
                "Receiving 'getDocument' request");
    }

    public SendMessageActionDefinition sendGetDocumentResponse(String payloadTemplate) {
        return sendDocumentArchiveResponse("Sending 'getDocument' response", payloadTemplate);
    }

    private SendMessageActionDefinition sendDocumentArchiveResponse(String description, String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(atlasDocumentArchiveServiceSrcEndpoint)
                .payload(getXmlTemplate(payloadTemplate))
                .header(JmsMessageHeaders.CORRELATION_ID, "${jmsCorrelationId}")
                .description(description);
    }

    private ReceiveMessageActionDefinition receiveDocumentArchiveRequest(String payloadTemplate, String soapAction,
            String description) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasDocumentArchiveServiceSrcEndpoint)
                .header("SoapAction", soapAction)
                .payload(getXmlTemplate(payloadTemplate))
                .extractFromHeader(JmsMessageHeaders.CORRELATION_ID, "jmsCorrelationId")
                .description(description);
    }

    /**
     * Receive Line Order Request message on Atlas ESB endpoint.
     *
     * @return
     */
    public ReceiveMessageActionDefinition receiveOrder() {
        return (ReceiveMessageActionDefinition) testBuilder.receive(getAtlasLineOrderServiceSrcEndpoint())
                .extractFromPayload(WitaLineOrderXpathExpressions.EXTERNAL_ORDER_ID.getXpath(), VariableNames.EXTERNAL_ORDER_ID)
                .extractFromPayload(WitaLineOrderXpathExpressions.CUSTOMER_ID.getXpath(), VariableNames.CUSTOMER_ID)
                .extractFromPayload(WitaLineOrderXpathExpressions.THIRD_PARTY_SALESMAN_CUSTOMER_ID.getXpath(), VariableNames.THIRD_PARTY_SALESMAN_CUSTOMER_ID)
                .description("Wait for ORDER REQ ...");
    }

    public ReceiveMessageActionDefinition receiveOrderWithRequestedCustomerDate() {
        return (ReceiveMessageActionDefinition) testBuilder.receive(getAtlasLineOrderServiceSrcEndpoint())
                .extractFromPayload(WitaLineOrderXpathExpressions.EXTERNAL_ORDER_ID.getXpath(), VariableNames.EXTERNAL_ORDER_ID)
                .extractFromPayload(WitaLineOrderXpathExpressions.CUSTOMER_ID.getXpath(), VariableNames.CUSTOMER_ID)
                .extractFromPayload(WitaLineOrderXpathExpressions.THIRD_PARTY_SALESMAN_CUSTOMER_ID.getXpath(), VariableNames.THIRD_PARTY_SALESMAN_CUSTOMER_ID)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE)
                .description("Wait for ORDER REQ ...");
    }

    public ReceiveMessageActionDefinition receiveOrderWithRequestedCustomerDate(String templateName) {
        return (ReceiveMessageActionDefinition) receiveOrder()
                .payload(getXmlTemplate(templateName))
                .extractFromPayload(WitaLineOrderXpathExpressions.EXTERNAL_ORDER_ID.getXpath(), VariableNames.EXTERNAL_ORDER_ID)
                .extractFromPayload(WitaLineOrderXpathExpressions.CUSTOMER_ID.getXpath(), VariableNames.CUSTOMER_ID)
                .extractFromPayload(WitaLineOrderXpathExpressions.THIRD_PARTY_SALESMAN_CUSTOMER_ID.getXpath(), VariableNames.THIRD_PARTY_SALESMAN_CUSTOMER_ID)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE)
                .description("Wait for ORDER REQ ...");
    }

    /**
     * Receive Line Order Request message on Atlas ESB endpoint and compare payload with template.
     *
     * @param templateName
     * @return
     */
    public ReceiveMessageActionDefinition receiveOrder(String templateName) {
        return (ReceiveMessageActionDefinition) receiveOrder()
                .payload(getXmlTemplate(templateName))
                .description("Wait for ORDER REQ " + templateName);
    }

    public SendMessageActionDefinition sendNotificationWithNewVariables(String templateName, String... variableNames) {
        for (String variableName : variableNames) {
            testBuilder.variables().add(variableName, "citrus:randomNumber(10)");
        }
        return sendNotification(templateName);
    }

    /**
     * Sends Line Order Notification message.
     *
     * @param templateName
     * @return
     */
    public SendMessageActionDefinition sendNotification(String templateName) {
        return (SendMessageActionDefinition) testBuilder.send(getAtlasLineOrderNotificationServiceDstEndpoint())
                .payload(getXmlTemplate(templateName))
                .header(getNotificationSoapActionHeader(), getNotificationSoapAction())
                .description("Send NOTIFICATION " + templateName);
    }

    private String getNotificationSoapActionHeader() {
        if (cdmVersion != null) {
            return "SoapAction"; // return TIBCO JMS soap action header
        }
        else {
            return "citrus_soap_action"; // return Http soap action header
        }
    }

    private String getNotificationSoapAction() {
        if (cdmVersion != null) {
            return "/LineOrderNotificationService/notifyUpdateOrder";
        }

        return "";
    }

    /**
     * Puts an XML template to Atlas. Could be used for test cases, where an XML should be sent directly to Atlas
     * without using the Hurrican WITA services.
     *
     * @param msg
     * @param soapAction
     * @return
     */
    public SendMessageActionDefinition sendXmlToAtlas(String msg, String soapAction) {
        if (!atlasEsbTestActor.isDisabled()) {
            return (SendMessageActionDefinition) testBuilder.send(getAtlasLineOrderServiceSrcEndpoint())
                    .header("SoapAction", soapAction)
                    .payload(msg)
                    .description("Send predefined XML NOTIFICATION");
        }
        else {
            return null;
        }
    }


    /**
     * Receive Line Order Notification message.
     *
     * @return
     */
    public ReceiveMessageActionDefinition receiveNotification() {
        return testBuilder.receive(getAtlasLineOrderServiceSrcEndpoint());
    }

    /**
     * Receive Line Order Notification message and compare payload with template.
     *
     * @param templateName
     * @return
     */
    public ReceiveMessageActionDefinition receiveNotification(String templateName) {
        return receiveNotification(templateName, true);
    }

    /**
     * Receive Line Order Notification message and compare payload with template.
     *
     * @param templateName
     * @param templateValidation Disable the template validation for current test case. This is mostly needed for test
     *                           cases that send not implemented xml structures like some KFT tests
     * @return
     */
    public ReceiveMessageActionDefinition receiveNotification(String templateName, boolean templateValidation) {
        if (templateValidation) {
            return (ReceiveMessageActionDefinition)
                    receiveNotification()
                    .payload(getXmlTemplate(templateName))
                    .description("Wait for NOTIFICATION " + templateName);
        }
        else {
            return (ReceiveMessageActionDefinition) receiveNotification()
                    .description("Wait for NOTIFICATION " + templateName);
        }
    }

    public ReceiveMessageActionDefinition receiveError(String template, String jmsHeaderName, String jmsHeaderValue) {
        if (jmsHeaderName == null)
            return (ReceiveMessageActionDefinition) testBuilder.receive(errorHandlingServiceEndpoint)
                    .payload(getXmlTemplate(template))
                    .description("Wait for ERROR MESSAGE ...");
        else {
            String messageSelector = String.format("%s='%s'", jmsHeaderName, jmsHeaderValue);
            return (ReceiveMessageActionDefinition) testBuilder.receive(errorHandlingServiceEndpoint)
                    .selector(messageSelector)
                    .payload(getXmlTemplate(template))
                    .description("Wait for ERROR MESSAGE ... with selector " + messageSelector);
        }
    }

    private JmsEndpoint getAtlasLineOrderServiceSrcEndpoint() {
        if (getCdmVersion() == WitaCdmVersion.V2) {
            return atlasLineOrderServiceSrcEndpointV2;
        }
        else {
            return atlasLineOrderServiceSrcEndpoint;
        }
    }

    private JmsEndpoint getAtlasLineOrderNotificationServiceDstEndpoint() {
        if (getCdmVersion() == de.mnet.wita.WitaCdmVersion.V2) {
            return atlasLineOrderNotificationServiceDstEndpointV2;
        }
        else {
            return atlasLineOrderNotificationServiceDstEndpoint;
        }
    }

}
