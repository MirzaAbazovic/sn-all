/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.mnet.hurrican.atlas.simulator.ffm.builder;

import com.consol.citrus.actions.SendMessageAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import com.consol.citrus.dsl.definition.SendMessageActionDefinition;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.message.Message;
import com.consol.citrus.validation.interceptor.MessageConstructionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.mnet.hurrican.atlas.simulator.AbstractSimulatorTestBuilder;
import de.mnet.hurrican.atlas.simulator.SimulatorMessageHeaders;
import de.mnet.hurrican.atlas.simulator.SimulatorVariableNames;
import de.mnet.hurrican.atlas.simulator.ffm.FFMOperation;

/**
 * Basic FFM simulator test builder able to receive incoming order request such as save order. Based
 * on use case implementation sends 1-n order notifications to notification endpoint.
 *
 *
 */
public abstract class AbstractFFMTestBuilder extends AbstractSimulatorTestBuilder {

    @Autowired
    @Qualifier("simInboundEndpoint")
    private Endpoint simInboundEndpoint;

    @Autowired
    @Qualifier("ffmNotificationOutboundEndpoint")
    private Endpoint notificationOutboundEndpoint;

    /**
     * Receive save order request.
     * @return
     */
    protected ReceiveMessageActionDefinition receiveCreateOrder() {
        return (ReceiveMessageActionDefinition) receive(simInboundEndpoint)
                .selector(SimulatorMessageHeaders.ORDER_ID + " = '" + getOrderId() + "'")
                .header(SOAP_ACTION, FFMOperation.CREATE_ORDER.getSoapAction())
                .extractFromHeader(SimulatorMessageHeaders.ORDER_ID.getName(), SimulatorVariableNames.ORDER_ID.getName())
                .description("Wait for createOrder REQ ...");
    }

    /**
     * Receive save order request with expected payload template.
     * @return
     */
    protected ReceiveMessageActionDefinition receiveCreateOrder(String templateName) {
        return (ReceiveMessageActionDefinition) receiveCreateOrder()
                .payload(getXmlTemplate(templateName))
                .description("Wait for " + templateName + " ...");
    }

    /**
     * Receive save order request.
     * @return
     */
    protected ReceiveMessageActionDefinition receiveDeleteOrder() {
        return (ReceiveMessageActionDefinition) receive(simInboundEndpoint)
                .selector(SimulatorMessageHeaders.ORDER_ID + " = '" + getOrderId() + "'")
                .header(SOAP_ACTION, FFMOperation.DELETE_ORDER.getSoapAction())
                .extractFromHeader(SimulatorMessageHeaders.ORDER_ID.getName(), SimulatorVariableNames.ORDER_ID.getName())
                .description("Wait for deleteOrder REQ ...");
    }

    /**
     * Receive save order request with expected payload template.
     * @return
     */
    protected ReceiveMessageActionDefinition receiveDeleteOrder(String templateName) {
        return (ReceiveMessageActionDefinition) receiveDeleteOrder()
                .payload(getXmlTemplate(templateName))
                .description("Wait for " + templateName + " ...");
    }

    protected SendMessageActionDefinition sendUpdateNotification(String templateName) {
        return  sendNotification(templateName)
                    .header(SOAP_ACTION, FFMOperation.NOTIFY_ORDER_UPDATE.getSoapAction());
    }

    protected SendMessageActionDefinition sendFeedbackNotification(String templateName) {
        return  sendNotification(templateName)
                    .header(SOAP_ACTION, FFMOperation.NOTIFY_ORDER_FEEDBACK.getSoapAction());
    }

    /**
     * Sends notification message to notification endpoint.
     *
     * @param templateName
     * @return
     */
    private SendMessageActionDefinition sendNotification(String templateName) {
        SendMessageActionDefinition actionDefinition = (SendMessageActionDefinition) send(notificationOutboundEndpoint)
                .payload(getXmlTemplate(templateName))
                .header(SimulatorMessageHeaders.ESB_TRACKING_ID.getName(), getEsbTrackingId())
                .description("Send notification " + templateName);

        SendMessageAction sendMessageAction = (SendMessageAction) actionDefinition.getAction();
        sendMessageAction.getMessageBuilder().add(new MessageConstructionInterceptor() {
            @Override
            public Message interceptMessageConstruction(Message message, String messageType, TestContext context) {
                return soapMessageHelper.createSoapMessage(message);
            }

            @Override
            public boolean supportsMessageType(String messageType) {
                return true;
            }
        });

        return actionDefinition;
    }

    @Override
    protected String getInterfaceName() {
        return "ffm";
    }
}
