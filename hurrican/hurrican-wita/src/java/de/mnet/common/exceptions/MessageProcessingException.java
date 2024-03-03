/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.common.exceptions;

/**
 * Exception to be used in message processing within Camel routes.
 *
 *
 */
public class MessageProcessingException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public MessageProcessingException() {
        super();
    }

    public MessageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageProcessingException(String message) {
        super(message);
    }

    public MessageProcessingException(Throwable cause) {
        super(cause);
    }
}
