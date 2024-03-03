/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.06.2012 11:15:05
 */
package de.augustakom.hurrican.exceptions;

/**
 * Exception zeigt an, dass (wahrscheinlich) ein Multi-Threading Problem aufgetreten ist.
 */
public class HurricanConcurrencyException extends RuntimeException {

    public HurricanConcurrencyException() {
        super();
    }

    public HurricanConcurrencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public HurricanConcurrencyException(String message) {
        super(message);
    }

    public HurricanConcurrencyException(Throwable cause) {
        super(cause);
    }

}


