/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2011 07:39:01
 */
package de.mnet.wita.exceptions;

/**
 * Exception zeigt an, dass bei der Ermittlung der Daten fuer einen WITA-Geschaeftsfall ein Fehler aufgetreten ist.
 */
public class WitaDataAggregationException extends WitaUserException {

    private static final long serialVersionUID = -3731562378284621843L;

    public WitaDataAggregationException() {
        super();
    }

    public WitaDataAggregationException(String message, Throwable cause) {
        super(message, cause);
    }

    public WitaDataAggregationException(String message) {
        super(message);
    }

    public WitaDataAggregationException(Throwable cause) {
        super(cause);
    }

}
