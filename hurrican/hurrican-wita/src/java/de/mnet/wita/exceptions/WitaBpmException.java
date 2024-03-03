/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2011 10:10:52
 */
package de.mnet.wita.exceptions;

/**
 * Exception zeigt an, dass innerhalb des WITA-BPM-Bereichs ein Fehler aufgetreten ist.
 */
public class WitaBpmException extends WitaBaseException {

    private static final long serialVersionUID = 1L;

    public WitaBpmException() {
        super();
    }

    public WitaBpmException(String message, Throwable cause) {
        super(message, cause);
    }

    public WitaBpmException(String message) {
        super(message);
    }

    public WitaBpmException(Throwable cause) {
        super(cause);
    }

}
