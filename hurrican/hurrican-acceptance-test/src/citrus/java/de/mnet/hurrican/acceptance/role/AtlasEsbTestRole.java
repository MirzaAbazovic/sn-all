/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.14
 */
package de.mnet.hurrican.acceptance.role;

import com.consol.citrus.dsl.definition.IterateDefinition;
import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import com.consol.citrus.dsl.definition.SendMessageActionDefinition;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import com.consol.citrus.jms.message.JmsMessageHeaders;
import com.consol.citrus.ws.client.WebServiceClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Atlas test role holds all esb related test actions such as sending and receiving messages.
 */
public class AtlasEsbTestRole extends AbstractTestRole {

    private static final Logger LOGGER = Logger.getLogger(AtlasEsbTestRole.class);

    /**
     * Soap action header name
     */
    public static final String SOAP_ACTION = "SoapAction";

    /**
     * ESB Tracking Id
     */
    public static final String ESB_TRACKING_ID = "ESB_TrackingId";

    @Autowired
    @Qualifier("atlasWorkforceServiceSrcEndpoint")
    private JmsEndpoint atlasWorkforceServiceSrcEndpoint;

    @Autowired
    @Qualifier("atlasWorkforceNotificationServiceDstEndpoint")
    private JmsEndpoint atlasWorkforceNotificationServiceDstEndpoint;

    @Autowired
    @Qualifier("atlasLocationNotificationServiceDstEndpoint")
    private JmsEndpoint atlasLocationNotificationServiceDstEndpoint;

    @Autowired
    @Qualifier("atlasLocationServiceEndpoint")
    private JmsEndpoint atlasLocationServiceEndpoint;

    @Autowired
    @Qualifier("atlasCustomerOrderServiceDstEndpoint")
    private JmsEndpoint atlasCustomerOrderServiceDstEndpoint;

    @Autowired
    @Qualifier("atlasDocumentArchiveServiceSrcEndpoint")
    private JmsEndpoint atlasDocumentArchiveServiceSrcEndpoint;

    @Autowired
    @Qualifier("atlasErrorHandlingServiceEndpoint")
    private JmsEndpoint atlasErrorHandlingServiceEndpoint;

    @Autowired
    @Qualifier("atlasResourceOrderManagementNotificationServiceDstEndpoint")
    private JmsEndpoint atlasResourceOrderManagementNotificationServiceEndpoint;

    @Autowired
    @Qualifier("atlasWholesaleOrderServiceEndpoint")
    private JmsEndpoint atlasWholesaleOrderServiceEndpoint;

    @Autowired
    @Qualifier("resourceWholesaleOrderServiceClient")
    private WebServiceClient resourceWholesaleOrderServiceClient;

    public ReceiveMessageActionDefinition receiveLocationSearch(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasLocationServiceEndpoint)
                .header("SoapAction", "/LocationService/searchBuildings")
                .payload(getXmlTemplate(payloadTemplate))
                .extractFromHeader(JmsMessageHeaders.CORRELATION_ID, "jmsCorrelationId")
                .description(String.format("Receiving location search request %s", payloadTemplate));
    }

    public SendMessageActionDefinition sendLocationSearchResponse(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(atlasLocationServiceEndpoint)
                .payload(getXmlTemplate(payloadTemplate))
                .header(JmsMessageHeaders.CORRELATION_ID, "${jmsCorrelationId}")
                .description(String.format("Sending location search response %s", payloadTemplate));
    }

    public ReceiveMessageActionDefinition receiveCreateOrder(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasWorkforceServiceSrcEndpoint)
                .header(SOAP_ACTION, "/WorkforceService/createOrder")
                .payload(getXmlTemplate(payloadTemplate))
                .description("Receiving 'save order' request");
    }

    public ReceiveMessageActionDefinition receiveUpdateOrder(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasWorkforceServiceSrcEndpoint)
                .header(SOAP_ACTION, "/WorkforceService/updateOrder")
                .payload(getXmlTemplate(payloadTemplate))
                .description("Receiving 'update order' request");
    }

    public ReceiveMessageActionDefinition receiveDeleteOrder(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasWorkforceServiceSrcEndpoint)
                .header(SOAP_ACTION, "/WorkforceService/deleteOrder")
                .payload(getXmlTemplate(payloadTemplate))
                .description("Receiving 'delete order' request");
    }

    public ReceiveMessageActionDefinition receiveArchiveDocumentRequest(String payloadTemplate) {
        return receiveDocumentArchiveRequestAsync(payloadTemplate, "/DocumentArchiveService/archiveDocumentAsync",
                "Receiving 'archiveDocumentAsync' request");
    }

    public ReceiveMessageActionDefinition receiveGetDocumentRequest(String payloadTemplate) {
        return receiveDocumentArchiveRequest(payloadTemplate, "/DocumentArchiveService/getDocument",
                "Receiving 'getDocument' request");
    }

    public SendMessageActionDefinition sendGetDocumentResponse(String payloadTemplate) {
        return sendDocumentArchiveResponse("Sending 'getDocument' response", payloadTemplate);
    }

    public ReceiveMessageActionDefinition receiveSearchDocumentsRequest(String payloadTemplate) {
        return receiveDocumentArchiveRequest(payloadTemplate, "/DocumentArchiveService/searchDocuments",
                "Receiving 'searchDocuments' request");
    }

    public SendMessageActionDefinition sendSearchDocumentsResponse(String payloadTemplate) {
        return sendDocumentArchiveResponse("Sending 'searchDocuments' response", payloadTemplate);
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
                .header(SOAP_ACTION, soapAction)
                .payload(getXmlTemplate(payloadTemplate))
                .extractFromHeader(JmsMessageHeaders.CORRELATION_ID, "jmsCorrelationId")
                .description(description);
    }

    private ReceiveMessageActionDefinition receiveDocumentArchiveRequestAsync(String payloadTemplate, String soapAction,
            String description) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasDocumentArchiveServiceSrcEndpoint)
                .header(SOAP_ACTION, soapAction)
                .payload(getXmlTemplate(payloadTemplate))
                .description(description);
    }

    public SendMessageActionDefinition sendNotifyOrderUpdate(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(atlasWorkforceNotificationServiceDstEndpoint)
                .header(SOAP_ACTION, "/WorkforceNotificationService/notifyUpdateOrder")
                .header(ESB_TRACKING_ID, "citrus:randomString(10)")
                .payload(getXmlTemplate(payloadTemplate))
                .description("Sending 'notify update order' notification");
    }


    public IterateDefinition sendNotifyOrderUpdate(final String payloadTemplate, int times) {
        return testBuilder.iterate(sendNotifyOrderUpdate(payloadTemplate))
                .startsWith(0).condition("i lt " + times);
    }


    public SendMessageActionDefinition sendNotifyUpdateLocationNotification(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(atlasLocationNotificationServiceDstEndpoint)
                .header(SOAP_ACTION, "/LocationNotificationService/notifyUpdateLocation")
                .header(ESB_TRACKING_ID, "citrus:randomString(10)")
                .payload(getXmlTemplate(payloadTemplate))
                .description("Sending 'notify update location' notification");
    }

    public SendMessageActionDefinition sendNotifyReplaceLocationNotification(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(atlasLocationNotificationServiceDstEndpoint)
                .header(SOAP_ACTION, "/LocationNotificationService/notifyReplaceLocation")
                .header(ESB_TRACKING_ID, "citrus:randomString(10)")
                .payload(getXmlTemplate(payloadTemplate))
                .description("Sending 'notify replace location' notification");
    }

    public SendMessageActionDefinition sendOrderDetailsCustomerOrder(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(atlasCustomerOrderServiceDstEndpoint)
                .header(SOAP_ACTION, "/CustomerOrderService/getOrderDetails")
                .header(ESB_TRACKING_ID, "citrus:randomString(10)")
                .payload(getXmlTemplate(payloadTemplate))
                .description("Sending 'getOrderDetails' customer order request");
    }

    public ReceiveMessageActionDefinition receiveGetOrderDetailsCustomerOrderResponse(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasCustomerOrderServiceDstEndpoint)
                .payload(getXmlTemplate(payloadTemplate))
                .description("Receiving 'getOrderDetails' customer order response");
    }

    public SendMessageActionDefinition sendNotifyOrderFeedback(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(atlasWorkforceNotificationServiceDstEndpoint)
                .header(SOAP_ACTION, "/WorkforceNotificationService/notifyOrderFeedback")
                .header(ESB_TRACKING_ID, "citrus:randomString(10)")
                .payload(getXmlTemplate(payloadTemplate))
                .description("Sending 'notify order feedback' notification");
    }

    public ReceiveMessageActionDefinition receiveErrorHandlingServiceMessage(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasErrorHandlingServiceEndpoint)
                .header(SOAP_ACTION, "/ErrorHandlingService/handleError")
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Receiving error handling service message %s", payloadTemplate));
    }

    /**
     * Antwort seitens Atlas f&uuml;r eine erfolgreiche Portreservierung. Die LineId wird ignoriert, da diese f&uuml;r
     * jeden Test neu erzeugt wird.
     *
     * @param payloadTemplate Name des XML, das zur&uuml;ckerwartet wird.
     * @return Antwort seitens Atlas.
     */
    public ReceiveMessageActionDefinition receiveReservePortResourceOrderManagementNotificationServiceResponse(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasResourceOrderManagementNotificationServiceEndpoint)
                .header(SOAP_ACTION, "/ResourceOrderManagementNotificationService/notifyPortOrderUpdate")
                .payload(getXmlTemplate(payloadTemplate))
                .ignore("lineId")
                .description("Receiving 'reservePort' customer order response");
    }

    /**
     * Antwort seitens Atlas f&uuml;r eine erfolgreiche Portfreigabe.
     *
     * @param payloadTemplate Name des XML, das zur&uuml;ckerwartet wird.
     * @return Antwort seitens Atlas.
     */
    public ReceiveMessageActionDefinition receiveResourceOrderManagementNotification(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasResourceOrderManagementNotificationServiceEndpoint)
                .header(SOAP_ACTION, "/ResourceOrderManagementNotificationService/notifyPortOrderUpdate")
                .ignore("errorDescription")
                .payload(getXmlTemplate(payloadTemplate))
                .description("Receiving 'resourceOrderManagmentNotification' customer order response");
    }

    /**
     * Antwort seitens Atlas f&uuml;r eine erfolgreiche createorder.
     *
     * @param payloadTemplate Name des XML, das zur&uuml;ckerwartet wird.
     * @return Antwort seitens Atlas.
     */
    public ReceiveMessageActionDefinition receiveWholesaleOrderService(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasWholesaleOrderServiceEndpoint)
                .header(SOAP_ACTION, "/WholesaleOrderService/createOrder")
                .ignore("errorDescription")
                .payload(getXmlTemplate(payloadTemplate))
                .description("Receiving 'createOrder' wholesale");
    }

    public ReceiveMessageActionDefinition receiveWholesaleUpdateOrderService(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasWholesaleOrderServiceEndpoint)
                .header(SOAP_ACTION, "/WholesaleOrderService/updateOrder")
                .ignore("errorDescription")
                .payload(getXmlTemplate(payloadTemplate))
                .description("Receiving 'updateOrder' wholesale");
    }

    public SendMessageActionDefinition sendWholesaleUpdateOrderService(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(resourceWholesaleOrderServiceClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .description("Sending 'updateOrder' Wholesale order request");
    }

    public void purgeAllJmsQueues() {
        purgeJmsQueue(atlasWorkforceServiceSrcEndpoint);
        purgeJmsQueue(atlasWorkforceNotificationServiceDstEndpoint);
        purgeJmsQueue(atlasLocationNotificationServiceDstEndpoint);
        purgeJmsQueue(atlasCustomerOrderServiceDstEndpoint);
        purgeJmsQueue(atlasDocumentArchiveServiceSrcEndpoint);
        purgeJmsQueue(atlasErrorHandlingServiceEndpoint);
        purgeJmsQueue(atlasLocationServiceEndpoint);
        purgeJmsQueue(atlasResourceOrderManagementNotificationServiceEndpoint);
        purgeJmsQueue(atlasWholesaleOrderServiceEndpoint);
    }

    private void purgeJmsQueue(JmsEndpoint jmsEndpoint) {
        if (!atlasEsbTestActor.isDisabled()) {
            LOGGER.info(String.format("Purge JMS queue %s", jmsEndpoint.getEndpointConfiguration().getDefaultDestinationName()));

            testBuilder.purgeQueues(jmsEndpoint.getEndpointConfiguration().getConnectionFactory())
                    .queueNames(jmsEndpoint.getEndpointConfiguration().getDefaultDestinationName());
        }
    }
}
