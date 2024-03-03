/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.2014
 */
package de.augustakom.hurrican.exceptions;

/**
 *
 */
public class FFMServiceException extends RuntimeException {
    private static final long serialVersionUID = -5514786972480587413L;


    public FFMServiceException() {
        super();
    }

    public FFMServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FFMServiceException(String message) {
        super(message);
    }

    public FFMServiceException(Throwable cause) {
        super(cause);
    }
}
