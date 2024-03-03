/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.exception;

/**
 * Exception to be used within remote service implementations.
 *
 *
 */
public class WbciServiceException extends WbciBaseException {

    private static final long serialVersionUID = 1L;

    public WbciServiceException() {
        super();
    }

    public WbciServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WbciServiceException(String message) {
        super(message);
    }

    public WbciServiceException(Throwable cause) {
        super(cause);
    }
}
