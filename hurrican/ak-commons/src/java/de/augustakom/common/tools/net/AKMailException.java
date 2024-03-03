/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.10.2004 11:34:27
 */
package de.augustakom.common.tools.net;


/**
 * Exception wird verwendet, um Fehler beim Senden von EMails zu signalisieren.
 *
 *
 */
public class AKMailException extends Exception {

    /**
     * @see java.lang.Exception()
     */
    public AKMailException() {
        super();
    }

    /**
     * @param message
     * @see java.lang.Exception(String)
     */
    public AKMailException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     * @see java.lang.Exception(String, Throwable)
     */
    public AKMailException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     * @see java.lang.Exception(Throwable)
     */
    public AKMailException(Throwable cause) {
        super(cause);
    }
}


