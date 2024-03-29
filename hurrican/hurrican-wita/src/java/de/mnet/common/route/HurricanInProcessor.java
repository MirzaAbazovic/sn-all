/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2014
 */
package de.mnet.common.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.common.route.helper.ExchangeHelper;

/**
 * Abstract Apache Camel {@link Processor} for in route
 */
public abstract class HurricanInProcessor implements HurricanProcessor {

    @Autowired
    private ExchangeHelper exchangeHelper;

    /**
     * Gets the original message of the in route.
     *
     * @param exchange {@link Exchange} object
     * @return the original message
     */
    public <T> T getOriginalMessage(Exchange exchange) {
        return exchangeHelper.getOriginalMessageFromInMessage(exchange);
    }

}

