/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.2004 14:19:34
 */
package de.augustakom.common.gui.exceptions;


/**
 * Exception kann verwendet werden, um innerhalb einer GUI auf Fehler (z.B. falsche Eingabewerte) hinzuweisen.
 */
public class AKGUIException extends Exception {

    /**
     * Standardkonstruktor.
     */
    public AKGUIException() {
        super();
    }

    /**
     * @param message
     */
    public AKGUIException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public AKGUIException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public AKGUIException(Throwable cause) {
        super(cause);
    }
}


