/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.10.13
 */
package de.mnet.wbci.route;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.common.exceptions.HurricanExceptionLogErrorHandler;
import de.mnet.common.route.helper.RouteConfigHelper;
import de.mnet.wbci.route.processor.carriernegotiation.out.ConvertWbciMessageToCdmProcessor;
import de.mnet.wbci.route.processor.carriernegotiation.out.EvaluateCdmVersionProcessor;

/**
 *
 */
public abstract class AbstractCarrierNegotiationOutRoute extends SpringRouteBuilder implements WbciCamelConstants {

    @Autowired
    protected RouteConfigHelper routeConfigHelper;
    @Autowired
    protected ConvertWbciMessageToCdmProcessor convertWbciMessageToCdmProcessor;
    @Autowired
    protected EvaluateCdmVersionProcessor evaluateCdmVersionProcessor;

    @Autowired
    protected HurricanExceptionLogErrorHandler hurricanExceptionLogErrorHandler;

    @Override
    public void configure() throws Exception {
        onException(Throwable.class).bean(hurricanExceptionLogErrorHandler);
        addCarrierNegotiationOutRoutes();
    }

    /**
     * Adds route body with processing logic.
     */
    protected abstract void addCarrierNegotiationOutRoutes();

}
