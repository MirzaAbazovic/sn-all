/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.05.2015
 */
package de.augustakom.hurrican.exceptions;

/**
 * Runtime-Exception, die im Kontext eines HVT-Umzugs verwendet werden kann.
 */
public class HvtUmzugException extends RuntimeException {

    private static final long serialVersionUID = -1714586372880187817L;

    public HvtUmzugException() {
        super();
    }

    public HvtUmzugException(String message, Throwable cause) {
        super(message, cause);
    }

    public HvtUmzugException(String message) {
        super(message);
    }

    public HvtUmzugException(Throwable cause) {
        super(cause);
    }

}
