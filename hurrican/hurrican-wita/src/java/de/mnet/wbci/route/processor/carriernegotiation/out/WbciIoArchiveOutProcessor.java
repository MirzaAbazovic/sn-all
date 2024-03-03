/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.13
 */
package de.mnet.wbci.route.processor.carriernegotiation.out;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.route.helper.ExchangeHelper;
import de.mnet.wbci.route.processor.carriernegotiation.WbciIoArchiveProcessor;

/**
 * IoArchiveProcessor for outgoing Messages;
 *
 *
 */
@Component
public class WbciIoArchiveOutProcessor extends WbciIoArchiveProcessor {

    @Autowired
    private ExchangeHelper exchangeHelper;

    @Override
    public <T> T getOriginalMessage(Exchange exchange) {
        return exchangeHelper.getOriginalMessageFromOutMessage(exchange);
    }

    @Override
    protected String getRequestXml(Exchange exchange) {
        return exchange.getIn().getBody().toString();
    }
}
