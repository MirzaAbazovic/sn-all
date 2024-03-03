/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 07.11.13 
 */
package de.mnet.wbci.route.processor.carriernegotiation.in;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.route.helper.ExchangeHelper;
import de.mnet.wbci.route.processor.carriernegotiation.CustomerServiceProtocolProcessor;

/**
 * Apache Camel Processor, um die CustomerService Protocol fuer eine eingehende Request/Meldung zu generieren.
 */
@Component
public class CustomerServiceProtocolInProcessor extends CustomerServiceProtocolProcessor {

    @Autowired
    private ExchangeHelper exchangeHelper;

    @Override
    public <T> T getOriginalMessage(Exchange exchange) {
        return exchangeHelper.getOriginalMessageFromInMessage(exchange);
    }
}
