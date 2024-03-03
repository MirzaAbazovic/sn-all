/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.acceptance.common.role;

import com.consol.citrus.TestActor;
import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import com.consol.citrus.dsl.definition.SendMessageActionDefinition;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import com.consol.citrus.jms.message.JmsMessageHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Atlas ESB test behavior holds several convenience methods for acting as Atlas ESB in Citrus tests. Test building
 * methods in this class do delegate back to test builder which is injected automatically by TestNG test builder class
 * before execution.
 *
 *
 */
public class AtlasEsbTestRole extends AbstractTestRole {

    /**
     * Location search operation names
     */
    public static final String LOCATION_SEARCH_BUILDINGS = "searchBuildings";
    @Autowired
    @Qualifier("atlasEsbTestActor")
    protected TestActor atlasEsbTestActor;
    @Autowired
    @Qualifier("atlasCarrierNegotiationServiceSrcEndpoint")
    private JmsEndpoint atlasCarrierNegotiationSrcEndpoint;
    @Autowired
    @Qualifier("atlasCarrierNegotiationServiceDstEndpoint")
    private JmsEndpoint atlasCarrierNegotiationDstEndpoint;
    @Autowired
    @Qualifier("atlasLocationServiceEndpoint")
    private JmsEndpoint atlasLocationServiceEndpoint;
    @Autowired
    @Qualifier("atlasErrorHandlingServiceEndpoint")
    private JmsEndpoint atlasErrorHandlingServiceEndpoint;
    @Autowired
    @Qualifier("atlasCustomerServiceEndpoint")
    private JmsEndpoint atlasCustomerServiceEndpoint;

    /**
     * Receive carrier change request message on ATLAS ESB via JMS message receiver.
     *
     * @return
     */
    public ReceiveMessageActionDefinition receiveCarrierChangeRequest(String payloadTemplate) {
        return receiveCarrierChange(payloadTemplate, "request");
    }

    /**
     * Receive carrier change update message on ATLAS ESB via JMS message receiver.
     *
     * @return
     */
    public ReceiveMessageActionDefinition receiveCarrierChangeUpdate(String payloadTemplate) {
        return receiveCarrierChange(payloadTemplate, "update");
    }

    public ReceiveMessageActionDefinition receiveCarrierChangeReschedule(String payloadTemplate) {
        return receiveCarrierChange(payloadTemplate, "reschedule");
    }

    public ReceiveMessageActionDefinition receiveCarrierChangeCancel(String payloadTemplate) {
        return receiveCarrierChange(payloadTemplate, "cancel");
    }

    private ReceiveMessageActionDefinition receiveCarrierChange(String payloadTemplate, String actionPrefix) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasCarrierNegotiationSrcEndpoint)
                .header("SoapAction", String.format("/CarrierNegotiationService/%sCarrierChange", actionPrefix))
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Receiving carrier change %s", actionPrefix));
    }

    public ReceiveMessageActionDefinition receiveLocationSearch(String payloadTemplate, String searchOperation) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasLocationServiceEndpoint)
                .header("SoapAction", String.format("/LocationService/%s", searchOperation))
                .payload(getXmlTemplate(payloadTemplate))
                .extractFromHeader(JmsMessageHeaders.CORRELATION_ID, "jmsCorrelationId")
                .description(String.format("Receiving location search request %s", payloadTemplate));
    }

    public ReceiveMessageActionDefinition receiveAnyLocationSearch(String searchOperation) {
        String payloadTemplate = "SEARCH_BUILDINGS_REQUEST";
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasLocationServiceEndpoint)
                .header("SoapAction", String.format("/LocationService/%s", searchOperation))
                .payload(getXmlTemplateFromDefaultLocationServiceFileResource(payloadTemplate))
                .extractFromHeader(JmsMessageHeaders.CORRELATION_ID, "jmsCorrelationId")
                .description(String.format("Receiving location search request %s", payloadTemplate));
    }

    public SendMessageActionDefinition sendLocationSearchResponse(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(atlasLocationServiceEndpoint)
                .payload(getXmlTemplate(payloadTemplate))
                .header(JmsMessageHeaders.CORRELATION_ID, "${jmsCorrelationId}")
                .description(String.format("Sending location search response %s", payloadTemplate));
    }

    public SendMessageActionDefinition sendEmptyLocationSearchResponse() {
        String payloadTemplate = "SEARCH_BUILDINGS_RESPONSE";
        return (SendMessageActionDefinition) testBuilder.send(atlasLocationServiceEndpoint)
                .payload(getXmlTemplateFromDefaultLocationServiceFileResource(payloadTemplate))
                .header(JmsMessageHeaders.CORRELATION_ID, "${jmsCorrelationId}")
                .description(String.format("Sending location search response %s", payloadTemplate));
    }

    public ReceiveMessageActionDefinition receiveErrorHandlingServiceMessage(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasErrorHandlingServiceEndpoint)
                .header("SoapAction", "/ErrorHandlingService/handleError")
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Receiving error handling service message %s", payloadTemplate));
    }

    public ReceiveMessageActionDefinition receiveCustomerServiceMessage(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(atlasCustomerServiceEndpoint)
                .header("SoapAction", "/CustomerService/addCommunication")
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Receiving customer service message %s", payloadTemplate));
    }

    /**
     * Send carrier change request message as ATLAS ESB via JMS message sender
     *
     * @param payloadTemplate
     * @return
     */
    public SendMessageActionDefinition sendCarrierChangeRequest(String payloadTemplate) {
        return sendCarrierChange(payloadTemplate, "request");
    }

    /**
     * Send carrier change update message as ATLAS ESB via JMS message sender.
     *
     * @return
     */
    public SendMessageActionDefinition sendCarrierChangeUpdate(String payloadTemplate) {
        return sendCarrierChange(payloadTemplate, "update");
    }

    /**
     * Send carrier change cancel message as ATLAS ESB via JMS message sender.
     *
     * @return
     */
    public SendMessageActionDefinition sendCarrierChangeCancel(String payloadTemplate) {
        return sendCarrierChange(payloadTemplate, "cancel");
    }

    public SendMessageActionDefinition sendCarrierChangeReschedule(String payloadTemplate) {
        return sendCarrierChange(payloadTemplate, "reschedule");
    }

    private SendMessageActionDefinition sendCarrierChange(String payloadTemplate, String actionPrefix) {
        return (SendMessageActionDefinition) testBuilder.send(atlasCarrierNegotiationDstEndpoint)
                .header("SoapAction", String.format("/CarrierNegotiationService/%sCarrierChange", actionPrefix))
                .header("ESB_TrackingId", "Track123")
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Sending carrier change %s", actionPrefix));
    }

    /**
     * Gets a XML-file resource form the default templates in the folder "de/mnet/wbci/acceptance/locationService".
     *
     * @param fileName
     * @return
     */
    protected Resource getXmlTemplateFromDefaultLocationServiceFileResource(String fileName) {
        return new ClassPathResource("de/mnet/wbci/acceptance/locationService/cdm/" +
                wbciCdmVersion.name().toLowerCase() + "/" + fileName + ".xml");
    }

    public void purgeAllJmsQueues() {
        purgeJmsQueue(atlasCarrierNegotiationSrcEndpoint);
        purgeJmsQueue(atlasCarrierNegotiationDstEndpoint);
        purgeJmsQueue(atlasCustomerServiceEndpoint);
        purgeJmsQueue(atlasErrorHandlingServiceEndpoint);
        purgeJmsQueue(atlasLocationServiceEndpoint);
    }

    @Override
    public void purgeJmsQueue(JmsEndpoint jmsEndpoint) {
        if (!atlasEsbTestActor.isDisabled()) {
            super.purgeJmsQueue(jmsEndpoint);
        }
    }

}
