/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.11.2011 10:57:23
 */
package de.mnet.wita;

/**
 * Klasse, um verschiedene Konstanten fuer den Austausch der Exception-Log-Daten zu definieren.
 */
public final class ExceptionLogProperties {

    /**
     * Bereich/Klasse, wo die Exception aufgetreten ist
     */
    public static final String MAP_MESSAGE_KEY_CONTEXT = "context";

    /**
     * Hostname, wo die Exception aufgetreten ist
     */
    public static final String MAP_MESSAGE_KEY_HOST = "host";

    /**
     * Fehlermeldung der Exception
     */
    public static final String MAP_MESSAGE_KEY_ERROR_MESSAGE = "error.message";

    /**
     * Stacktrace der Exception
     */
    public static final String MAP_MESSAGE_KEY_STACKTRACE = "stacktrace";

    /**
     * Datum, wann die Exception aufgetreten ist
     */
    public static final String MAP_MESSAGE_KEY_DATE_OCCURED = "date.occured";

    /*
     * other properties
     */

    /**
     * Allgemeines Timestamp-Format fuer das Exception-Log
     */
    public static final String DATE_TIME_FORMAT_LONG = "dd.MM.yyyy HH:mm:ss:SSS";
}
