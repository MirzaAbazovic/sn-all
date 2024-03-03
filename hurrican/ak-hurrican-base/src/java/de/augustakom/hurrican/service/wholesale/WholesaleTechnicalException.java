/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.06.2012 09:56:09
 */
package de.augustakom.hurrican.service.wholesale;

/**
 * Exception-Klasse fuer technische Fehler. <br> Diese Exception soll dann geworfen werden, wenn der aufgetretene Fehler
 * (hoechst wahrscheinlich) durch einen erneuten Aufruf der Methode umgangen werden kann. Dies ist z.B. bei
 * Concurrency-Problemen notwendig.
 */
public class WholesaleTechnicalException extends RuntimeException {

    public WholesaleTechnicalException() {
        super();
    }

    public WholesaleTechnicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public WholesaleTechnicalException(String message) {
        super(message);
    }

    public WholesaleTechnicalException(Throwable cause) {
        super(cause);
    }

}


