/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.08.2011 15:33:55
 */
package de.mnet.wita.service.impl;

import javax.jms.*;
import org.springframework.jms.core.MessageCreator;

/**
 * Erzeugt TextMessages für ausgehende plain XML JMS-Nachrichten
 */
public class WitaOutgoingXmlMessageCreator implements MessageCreator {

    private final String xmlString;

    public WitaOutgoingXmlMessageCreator(String xml) {
        this.xmlString = xml;
    }

    @Override
    public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(xmlString);
    }

}


