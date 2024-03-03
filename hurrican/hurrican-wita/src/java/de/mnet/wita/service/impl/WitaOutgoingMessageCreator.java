/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2011 09:22:08
 */
package de.mnet.wita.service.impl;

import javax.jms.*;
import org.springframework.jms.core.MessageCreator;

import de.mnet.wita.WitaMessage;

/**
 * MessageCreator, um die ausgehenden WITA JMS Messages zu generieren.
 */
public class WitaOutgoingMessageCreator implements MessageCreator {

    private final WitaMessage message;

    public WitaOutgoingMessageCreator(final WitaMessage witaMessage) {
        this.message = witaMessage;
    }

    @Override
    public Message createMessage(Session session) throws JMSException {
        ObjectMessage jmsMessage = session.createObjectMessage(message);
        jmsMessage.setJMSCorrelationID(message.toString());
        return jmsMessage;
    }

    public WitaMessage getMessage() {
        return message;
    }

}


