/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.mnet.hurrican.atlas.simulator.ffm;

import javax.xml.namespace.*;
import com.consol.citrus.dsl.CitrusTestBuilder;
import com.consol.citrus.message.Message;

import de.mnet.hurrican.atlas.simulator.AbstractSimulatorTestBuilder;
import de.mnet.hurrican.atlas.simulator.SimulatorMessageHeaders;
import de.mnet.hurrican.simulator.exception.SimulatorException;
import de.mnet.hurrican.simulator.handler.SimulatorEndpointAdapter;

/**
 * Message handler implementation does poll on a JMS destination for incoming messages. When message was received dispatches message
 * to a Citrus test builder for further processing logic.
 *
 * Handler automatically removes SOAP Envelope and just passes SOAP body content as message payload to the test builders.
 *
 *
 */
public class FFMSimulatorEndpointAdapter extends SimulatorEndpointAdapter {

    @Override
    public Message prepareRequestMessage(Message request) {
        try {
            QName rootQName = getRootQName(request);
            FFMVersion ffmVersion = FFMVersion.fromNamespace(rootQName.getNamespaceURI());

            request.setHeader(SimulatorMessageHeaders.INTERFACE_NAMESPACE.getName(), ffmVersion.getNamespace())
                   .setHeader(SimulatorMessageHeaders.NOTIFICATION_NAMESPACE.getName(), ffmVersion.getNotificationNamespace())
                   .setHeader(SimulatorMessageHeaders.INTERFACE_VERSION.getName(), ffmVersion)
                   .setHeader(SimulatorMessageHeaders.ORDER_ID.getName(),
                            extractId(request, FFMXpathExpressions.ORDER_ID.getXpath()));
            return request;
        }
        catch (Exception e) {
            throw new SimulatorException("Failed to extract SOAP body from message", e);
        }
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void prepareExecution(Message request, CitrusTestBuilder testBuilder) {
        if (testBuilder instanceof AbstractSimulatorTestBuilder) {
            AbstractSimulatorTestBuilder simulatorTestBuilder = ((AbstractSimulatorTestBuilder) testBuilder);

            simulatorTestBuilder.setInterfaceVersion(request.getHeader(SimulatorMessageHeaders.INTERFACE_VERSION.getName()).toString());

            if (request.copyHeaders().containsKey(SimulatorMessageHeaders.ESB_TRACKING_ID.getName())) {
                simulatorTestBuilder.setEsbTrackingId(request.getHeader(SimulatorMessageHeaders.ESB_TRACKING_ID.getName()).toString());
            }

            if (request.copyHeaders().containsKey(SimulatorMessageHeaders.ORDER_ID.getName())) {
                simulatorTestBuilder.setOrderId(request.getHeader(SimulatorMessageHeaders.ORDER_ID.getName()).toString());
            }
        }
    }

}
