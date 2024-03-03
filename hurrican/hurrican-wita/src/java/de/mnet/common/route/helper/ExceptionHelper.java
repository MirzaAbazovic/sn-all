/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 16.10.13 
 */
package de.mnet.common.route.helper;

import org.apache.camel.Exchange;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Helper class for detecting and working with exceptions in an exchange. Support is also provided for retrieving and
 * adding ErrorHandlingService messages from/to the exchange.
 */
@Component
public class ExceptionHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHelper.class);

    public static final String ERROR_SERVICE_MESSAGE_KEY = "ErrorHandlingServiceMessage";

    public boolean containsExceptionInExchange(Exchange exchange) {
        return exchange.getProperty(Exchange.EXCEPTION_CAUGHT) != null;
    }

    public Throwable getExceptionFromExchange(Exchange exchange) {
        return exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
    }

    public boolean containsErrorHandlingServiceMessage(Exchange exchange) {
        return exchange.getProperty(ERROR_SERVICE_MESSAGE_KEY) != null;
    }

    public Object getErrorHandlingServiceMessage(Exchange exchange) {
        return exchange.getProperty(ERROR_SERVICE_MESSAGE_KEY);
    }

    public void setErrorHandlingServiceMessage(Exchange exchange, Object errorHandlingServiceMessage) {
        LOG.info(String.format("Adding ErrorHandlingService message to exchange: '%s'",
                ReflectionToStringBuilder.toString(errorHandlingServiceMessage)));
        exchange.setProperty(ERROR_SERVICE_MESSAGE_KEY, errorHandlingServiceMessage);
    }

    public String throwableToString(Throwable t) {
        if (t != null) {
            return org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t);
        }
        return null;
    }

}
