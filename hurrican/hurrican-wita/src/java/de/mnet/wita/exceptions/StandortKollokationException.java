/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.07.2012 17:09:30
 */
package de.mnet.wita.exceptions;

/**
 * Exception, die anzeigt, dass die Pruefung einer Standort-Kollokation fehlgeschlagen ist
 */
public class StandortKollokationException extends WitaBaseException {

    private static final long serialVersionUID = 1L;

    public StandortKollokationException() {
        super();
    }

    public StandortKollokationException(String message, Throwable cause) {
        super(message, cause);
    }

    public StandortKollokationException(String message) {
        super(message);
    }

    public StandortKollokationException(Throwable cause) {
        super(cause);
    }
}


