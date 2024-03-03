/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.02.14
 */
package de.mnet.wbci.route.helper;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.springframework.stereotype.Component;

import de.mnet.wbci.model.MessageProcessingMetadata;

/**
 *
 */
@Component
public class MessageProcessingMetadataHelper {
    public static final String MESSAGE_PROCESSING_METADATA_KEY = "MessageProcessingMetadata";

    /**
     * Returns a camel predicate for determining whether the incoming message was persisted to the database.
     *
     * @return
     */
    public Predicate isPostProcessMessage() {
        return new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                return getMessageProcessingMetadata(exchange).isPostProcessMessage();
            }
        };
    }

    /**
     * Returns a camel predicate for determining whether the incoming message is a duplicate VA request.
     *
     * @return
     */
    public Predicate isIncomingMessageDuplicateVaRequest() {
        return new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                return getMessageProcessingMetadata(exchange).isIncomingMessageDuplicateVaRequest();
            }
        };
    }

    /**
     * Returns a camel predicate for determining whether the outgoing message is a response to a duplicate VA request.
     *
     * @return
     */
    public Predicate isResponseToDuplicateVaRequest() {
        return new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                return getMessageProcessingMetadata(exchange).isResponseToDuplicateVaRequest();
            }
        };
    }

    /**
     * Returns the message processing results from the exchange.
     *
     * @param exchange
     * @return
     */
    public MessageProcessingMetadata getMessageProcessingMetadata(Exchange exchange) {
        return (MessageProcessingMetadata) exchange.getProperty(MESSAGE_PROCESSING_METADATA_KEY);
    }
}
