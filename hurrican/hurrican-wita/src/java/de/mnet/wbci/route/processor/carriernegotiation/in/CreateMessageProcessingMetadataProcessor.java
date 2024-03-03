/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.02.14
 */
package de.mnet.wbci.route.processor.carriernegotiation.in;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.route.helper.MessageProcessingMetadataHelper;

/**
 * Creates a new {@link de.mnet.wbci.model.MessageProcessingMetadata} container and adds it to the exchange.
 */
@Component
public class CreateMessageProcessingMetadataProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.setProperty(MessageProcessingMetadataHelper.MESSAGE_PROCESSING_METADATA_KEY, new MessageProcessingMetadata());
    }
}
