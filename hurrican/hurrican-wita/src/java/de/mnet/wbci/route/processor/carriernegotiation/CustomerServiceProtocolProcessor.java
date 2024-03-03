/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.11.13
 */
package de.mnet.wbci.route.processor.carriernegotiation;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.common.route.HurricanProcessor;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.route.WbciCamelConstants;
import de.mnet.wbci.service.WbciCustomerService;

/**
 * This processor is responsible for getting the original message (incoming or outgoing WBCI message) from the exchange
 * and then calling the business service {@link WbciCustomerService} that takes care of generating and sending the
 * customer service protocol.
 */
public abstract class CustomerServiceProtocolProcessor implements HurricanProcessor, WbciCamelConstants {

    @Autowired
    private WbciCustomerService wbciCustomerService;

    @Override
    public void process(Exchange exchange) throws Exception {
        WbciMessage wbciMessage = getOriginalMessage(exchange);
        wbciCustomerService.sendCustomerServiceProtocol(wbciMessage);
    }
}
