/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.04.2004
 */
package de.augustakom.common.service.exceptions;

/**
 * Diese Exception wird geworfen, wenn ein Service/System nicht initialisiert werden konnte.
 *
 *
 */
public class InitializationException extends RuntimeException {

    /**
     * Konstruktor ohne Message
     */
    public InitializationException() {
        super();
    }

    /**
     * @param message
     */
    public InitializationException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public InitializationException(Throwable cause) {
        super(cause);
    }

}
