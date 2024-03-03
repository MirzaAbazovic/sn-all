/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.12.13
 */
package de.mnet.common.route;

import java.util.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.route.helper.ExchangeHelper;

/**
 * Checks to see if the exchange was started with options and if any options are detected, adds these as properties to
 * the exchange.
 */
@Component
public class ExtractExchangeOptionsProcessor implements Processor {
    @Autowired
    private ExchangeHelper exchangeHelper;

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> options = exchangeHelper.getExchangeOptions(exchange);

        for (Map.Entry<String, Object> entry : options.entrySet()) {
            exchange.setProperty(entry.getKey(), entry.getValue());
        }
    }
}
