/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2011 13:44:11
 */
package de.mnet.wita.exceptions;

public class WitaUserException extends WitaBaseException {

    private static final long serialVersionUID = 457910691160718343L;

    public WitaUserException() {
        super();
    }

    public WitaUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public WitaUserException(String message) {
        super(message);
    }

    public WitaUserException(Throwable cause) {
        super(cause);
    }
}


