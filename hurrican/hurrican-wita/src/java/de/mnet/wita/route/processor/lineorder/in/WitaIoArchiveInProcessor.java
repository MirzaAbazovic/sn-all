/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package de.mnet.wita.route.processor.lineorder.in;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.route.helper.ExchangeHelper;
import de.mnet.wita.IOArchiveProperties;
import de.mnet.wita.route.processor.WitaIoArchiveProcessor;

/**
 * IoArchiveProcessor for incoming messages.
 */
@Component
public class WitaIoArchiveInProcessor extends WitaIoArchiveProcessor {

    @Autowired
    private ExchangeHelper exchangeHelper;

    @Override
    protected IOArchiveProperties.IOType getIOType() {
        return IOArchiveProperties.IOType.IN;
    }

    @Override
    protected String getRequestXml(Exchange exchange) {
        return exchangeHelper.getOriginalCdmPayloadFromInMessage(exchange);
    }

    @Override
    public <T> T getOriginalMessage(Exchange exchange) {
        return exchangeHelper.getOriginalMessageFromInMessage(exchange);
    }
}
