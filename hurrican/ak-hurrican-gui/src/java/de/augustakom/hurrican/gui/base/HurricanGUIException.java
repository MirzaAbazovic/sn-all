/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2004 08:45:40
 */
package de.augustakom.hurrican.gui.base;


/**
 * Exception kann verwendet werden, um innerhalb der GUI auf Fehler (z.B. falsche Eingabewerte) hinzuweisen.
 *
 *
 */
public class HurricanGUIException extends Exception {

    /**
     * Standardkonstruktor.
     */
    public HurricanGUIException() {
        super();
    }

    /**
     * @param message
     */
    public HurricanGUIException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public HurricanGUIException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public HurricanGUIException(Throwable cause) {
        super(cause);
    }
}


