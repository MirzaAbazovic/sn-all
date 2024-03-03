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

import de.mnet.common.route.HurricanInProcessor;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.message.IncomingMessage;
import de.mnet.wita.service.WitaReceiveMessageService;

@Component
public class StoreWitaMessageInProcessor extends HurricanInProcessor {
    @Autowired
    private WitaReceiveMessageService witaReceiveMessageService;

    @Override
    public void process(Exchange exchange) throws Exception {
        IncomingMessage witaMessage = getOriginalMessage(exchange);
        boolean success = witaReceiveMessageService.handleWitaMessage(witaMessage);

        if(!success) {
            throw new WitaBaseException("WITA Message cannot be processed by Hurrican");
        }
    }
}
