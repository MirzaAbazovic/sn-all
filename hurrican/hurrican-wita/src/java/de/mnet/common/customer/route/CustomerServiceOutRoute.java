/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.13
 */
package de.mnet.common.customer.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.common.customer.route.processor.MarshalCustomerServiceMessageProcessor;
import de.mnet.common.route.helper.RouteConfigHelper;

/**
 * Camel-Route, um den 'CustomerService' vom Atlas aufzurufen. Ueber den CustomerService koennen z.B. sog.
 * Protokolleintraege in BSI angelegt werden.
 */
public class CustomerServiceOutRoute extends SpringRouteBuilder {

    /**
     * Route-ID fuer die CustomerOutRoute, ueber die Customer Service Nachrichten nach aussen verschickt werden.
     */
    public static final String CUSTOMER_OUT_ROUTE_ID = "atlasCustomerServiceOutRoute";

    @Autowired
    protected RouteConfigHelper routeConfigHelper;

    @Autowired
    private MarshalCustomerServiceMessageProcessor marshalCustomerServiceMessageProcessor;

    @Override
    public void configure() throws Exception {
        addCustomerServiceOutRoute();
    }

    /**
     * Builds Camel out route for sending customer service messages to Atlas ESB. Route expects as input WbciMessage and
     * takes care of converting input to CustomerService protocol message.
     */
    private void addCustomerServiceOutRoute() {
        // @formatter:off
        from("direct:customerService")
            .transacted("ems.required")
            .routeId(CUSTOMER_OUT_ROUTE_ID)
            .process(marshalCustomerServiceMessageProcessor)
            .inOnly(getCustomerServiceOutUri())
            .log(LoggingLevel.INFO, "CustomerService message processed successfully")
            .end()
        .end();
        // @formatter:on
    }

    private String getCustomerServiceOutUri() {
        return String.format("%s:%s", routeConfigHelper.getAtlasOutComponent(), routeConfigHelper.getCustomerServiceOut());
    }
}
