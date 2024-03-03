/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.08.2015
 */
package de.augustakom.hurrican.exceptions;

/**
 * Created by glinkjo on 17.08.2015.
 */
public class AvailabilityException extends RuntimeException {

    private static final long serialVersionUID = -3114786958240587417L;


    public AvailabilityException() {
        super();
    }

    public AvailabilityException(String message, Throwable cause) {
        super(message, cause);
    }

    public AvailabilityException(String message) {
        super(message);
    }

    public AvailabilityException(Throwable cause) {
        super(cause);
    }
}
