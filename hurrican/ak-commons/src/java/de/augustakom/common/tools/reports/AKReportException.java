/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2004 10:14:10
 */
package de.augustakom.common.tools.reports;


/**
 * Exception wird geworfen, wenn beim Erzeugen eines Reports ein Fehler auftritt.
 *
 *
 */
public class AKReportException extends Exception {

    /**
     * Standardkonstruktor.
     */
    public AKReportException() {
        super();
    }

    /**
     * @param message
     */
    public AKReportException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public AKReportException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public AKReportException(Throwable cause) {
        super(cause);

    }
}


