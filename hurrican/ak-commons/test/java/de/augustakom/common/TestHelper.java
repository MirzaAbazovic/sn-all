/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.11.13
 */
package de.augustakom.common;

import java.util.*;
import org.springframework.jms.core.JmsTemplate;

/**
 * Misc test helpers, for encapsulating common or useful test functionality.
 */
public class TestHelper {
    private TestHelper() {
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> readAllMessagesFromQueue(JmsTemplate jmsTemplate, String queue) {
        List<T> msgs = new ArrayList<T>();
        T nextMessage;
        do {
            // we set an explicit receive timeout here, otherwise the unit tests hang forever when
            // started with ant and an error occurs initialising the spring context
            jmsTemplate.setReceiveTimeout(2000L);
            nextMessage = (T) jmsTemplate.receiveAndConvert(queue);
            if (nextMessage != null) {
                msgs.add(nextMessage);
            }
        }
        while (nextMessage != null);
        return msgs;
    }

}
