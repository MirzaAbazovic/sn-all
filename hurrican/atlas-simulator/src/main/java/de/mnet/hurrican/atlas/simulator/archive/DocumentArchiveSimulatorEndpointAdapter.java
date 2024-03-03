/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.14
 */
package de.mnet.hurrican.atlas.simulator.archive;

import java.util.*;
import javax.xml.namespace.*;
import com.consol.citrus.dsl.CitrusTestBuilder;
import com.consol.citrus.message.Message;

import de.mnet.hurrican.atlas.simulator.AbstractSimulatorTestBuilder;
import de.mnet.hurrican.atlas.simulator.SimulatorMessageHeaders;
import de.mnet.hurrican.simulator.exception.SimulatorException;
import de.mnet.hurrican.simulator.handler.SimulatorEndpointAdapter;

/**
 *
 */
public class DocumentArchiveSimulatorEndpointAdapter extends SimulatorEndpointAdapter {

    @Override
    public Message prepareRequestMessage(Message request) {
        try {
            QName rootQName = getRootQName(request);
            DocumentArchiveServiceVersion interfaceVersion = DocumentArchiveServiceVersion.fromNamespace(rootQName.getNamespaceURI());

            request.setHeader(SimulatorMessageHeaders.INTERFACE_NAMESPACE.getName(), interfaceVersion.getNamespace())
                   .setHeader(SimulatorMessageHeaders.INTERFACE_VERSION.getName(), interfaceVersion)
                   .setHeader(SimulatorMessageHeaders.ORDER_ID.getName(), UUID.randomUUID().toString());
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
