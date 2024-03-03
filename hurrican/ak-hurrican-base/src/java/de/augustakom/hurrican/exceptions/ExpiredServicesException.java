/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.08.2015
 */
package de.augustakom.hurrican.exceptions;

/**
 * Runtime-Exception, die im Kontext der Prozessierung auf 'AUTO_EXPIRE' Leistungen verwendet werden kann.
 */
public class ExpiredServicesException extends RuntimeException {

    private static final long serialVersionUID = -3932746372440187417L;

    public ExpiredServicesException() {
        super();
    }

    public ExpiredServicesException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiredServicesException(String message) {
        super(message);
    }

    public ExpiredServicesException(Throwable cause) {
        super(cause);
    }

}
