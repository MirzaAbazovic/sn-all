/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
 */
package de.mnet.wbci.exception;

/**
 * Exception-Klasse zeigt an, dass innerhalb eines Camel-Prozessors ein Fehler aufgetreten ist.
 */
public class WbciProcessorException extends WbciBaseException {

    private static final long serialVersionUID = 7558302551378797489L;

    public WbciProcessorException() {
        super();
    }

    public WbciProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public WbciProcessorException(String message) {
        super(message);
    }

    public WbciProcessorException(Throwable cause) {
        super(cause);
    }
}
