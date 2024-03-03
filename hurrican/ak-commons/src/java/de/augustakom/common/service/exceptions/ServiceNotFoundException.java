/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.04.2004
 */
package de.augustakom.common.service.exceptions;


/**
 * Diese Exception wird geworfen, wenn ein Service nicht gefunden werden konnte.
 *
 *
 */
public class ServiceNotFoundException extends Exception {

    /**
     * @param message
     */
    public ServiceNotFoundException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public ServiceNotFoundException(Throwable cause) {
        super(cause);
    }
}
