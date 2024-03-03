/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package de.mnet.wita.route.processor.lineorder.out;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.route.helper.ExchangeHelper;
import de.mnet.wita.IOArchiveProperties;
import de.mnet.wita.route.processor.WitaIoArchiveProcessor;

/**
 * IoArchiveProcessor for outgoing Messages
 *
 *
 */
@Component
public class WitaIoArchiveOutProcessor extends WitaIoArchiveProcessor {

    @Autowired
    private ExchangeHelper exchangeHelper;

    @Override
    protected IOArchiveProperties.IOType getIOType() {
        return IOArchiveProperties.IOType.OUT;
    }

    @Override
    protected String getRequestXml(Exchange exchange) {
        return exchange.getIn().getBody().toString();
    }

    @Override
    public <T> T getOriginalMessage(Exchange exchange) {
        return exchangeHelper.getOriginalMessageFromOutMessage(exchange);
    }

}
