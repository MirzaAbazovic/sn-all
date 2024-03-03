/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.07.13
 */
package de.mnet.hurrican.simulator.handler;

import javax.xml.namespace.*;
import javax.xml.transform.*;
import com.consol.citrus.dsl.endpoint.TestExecutingEndpointAdapter;
import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.Message;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.ws.server.endpoint.support.PayloadRootUtils;
import org.springframework.xml.transform.StringSource;

import de.mnet.hurrican.simulator.config.SimulatorConfiguration;
import de.mnet.hurrican.simulator.helper.XPathHelper;

/**
 * Message handler extracts data from incoming message payload and enriches message object. Message object forwards to a
 * test builder for further processing.
 *
 *
 */
public class SimulatorEndpointAdapter extends TestExecutingEndpointAdapter {

    @Autowired
    private SimulatorConfiguration configuration;

    @Autowired
    private XPathHelper xPathHelper;

    @Autowired
    private SimulatorOrderManager orderManager;

    private ApplicationContext applicationContext;

    /**
     * Transformer factory for request qname evaluation
     */
    private static TransformerFactory transformerFactory;

    /**
     * Explicit message handler for intermediate notification messages
     */
    private EndpointAdapter notificationEndpointAdapter;

    static {
        transformerFactory = TransformerFactory.newInstance();
    }

    /**
     * Gets root QName from incoming request and automatically sets special message headers for version and namespace
     * URI. Next message processing steps do use these header information for test builder preparation according to
     * request version.
     *
     * @param request
     * @return
     */
    @Override
    public Message dispatchMessage(Message request, String mappingKey) {
        Message simulatorRequest = prepareRequestMessage(request);

        if (orderManager.knowsOrderId(getOrderId(simulatorRequest))) {
            return notificationEndpointAdapter.handleMessage(simulatorRequest);
        }
        else if (applicationContext.containsBean(mappingKey)) {
            return super.dispatchMessage(simulatorRequest, mappingKey);
        }
        else {
            log.warn("Unknown use case name identified '" + mappingKey + "' using default simulator behavior");
            return super.dispatchMessage(simulatorRequest, configuration.getDefaultBuilder());
        }
    }

    /**
     * Extract id from request message via XPath expression.
     * @param request
     * @param expression
     * @return
     */
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected String extractId(Message request, String expression) {
        return xPathHelper.evaluateAsString(request, expression);
    }

    /**
     * Prepare request message and enrich message with custom data before simulator processing.
     *
     * @param request
     * @return
     */
    public Message prepareRequestMessage(Message request) {
        return request;
    }

    /**
     * Gets the order id from request message in order to correlate messages to running use cases in simulator.
     *
     * @param request
     * @return
     */
    public String getOrderId(Message request) {
        return request.getId().toString();
    }

    /**
     * Gets the qualified name of the request message root element.
     *
     * @param message
     * @return
     */
    public QName getRootQName(Message message) {
        return getRootQName(message.getPayload().toString());
    }

    /**
     * Gets the qualified name of the message payload root element.
     *
     * @param payload
     * @return
     */
    public QName getRootQName(String payload) {
        try {
            return PayloadRootUtils.getPayloadRootQName(new StringSource(payload), transformerFactory);
        }
        catch (TransformerException e) {
            throw new CitrusRuntimeException("Failed to parse message payload", e);
        }
    }

    /**
     * Sets the intermediate message handler. usually injected by Spring application context.
     *
     * @param notificationEndpointAdapter
     */
    public void setNotificationEndpointAdapter(EndpointAdapter notificationEndpointAdapter) {
        this.notificationEndpointAdapter = notificationEndpointAdapter;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        super.setApplicationContext(applicationContext);
    }

    /**
     * Sets the xPath helper.
     *
     * @param xPathHelper
     */
    public void setXPathHelper(XPathHelper xPathHelper) {
        this.xPathHelper = xPathHelper;
    }

    /**
     * Gets the xpath helper.
     *
     * @return
     */
    public XPathHelper getXPathHelper() {
        return xPathHelper;
    }

    /**
     * Gets the simulator configuration.
     *
     * @return
     */
    public SimulatorConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Gets the Spring application context.
     *
     * @return
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
