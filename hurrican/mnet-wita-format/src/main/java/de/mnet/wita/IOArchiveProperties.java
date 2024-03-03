/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2011 08:21:36
 */
package de.mnet.wita;

/**
 * Klasse, um verschiedene Konstanten bzw. Enums fuer JMS-Properties fuer den Austausch der I/O Archive Daten zu
 * definieren.
 */
public final class IOArchiveProperties {

    /**
     * Definiert, ob es sich um eine - aus M-net Sicht - eingehende oder ausgehende Nachricht handelt.
     */
    public enum IOType {
        UNDEFINED,
        IN,
        OUT,
    }

    /**
     * Definiert, ob es sich um eine WBCI oder WITA Nachricht handelt.
     */
    public enum IOSource {
        UNDEFINED,
        WITA,
        WBCI,
    }

    /*
     * common meta data properties
     */

    /**
     * Angabe des Zeitpunkts der Erstellung der JMS-Nachricht an das Archiv
     */
    public static final String MAP_MESSAGE_KEY_TIMESTAMP_SENT = "timestamp";

    /**
     * Angabe, ob es sich um eine ein IOType.IN oder ausgehende IOType.OUT Nachricht handelt
     */
    public static final String MAP_MESSAGE_KEY_IO_TYPE = "ioType";

    /**
     * Angabe, ob es sich um eine WITA oder WBCI Nachricht handelt
     */
    public static final String MAP_MESSAGE_KEY_IO_SOURCE = "ioSource";

    /**
     * Angabe der externen Auftragsnummer des WITA-Vorgangs oder des WBCI VorabstimmungsID
     */
    public static final String MAP_MESSAGE_KEY_EXT_ORDER_NO = "ext.order.no";

    /**
     * Angabe der Vertragsnummer fuer abgebende Anbieterwechsel
     */
    public static final String MAP_MESSAGE_KEY_VERTRAGSNUMMER = "vertragsnummer";

    /**
     * Angabe der Vertragsnummer fuer abgebende Anbieterwechsel
     */
    public static final String MAP_MESSAGE_KEY_IS_MELDUNG = "is.meldung";

    /*
     * request meta data properties
     */

    /**
     * Angabe des Requests im Klartext (kompletter SOAP-Envelope)
     */
    public static final String MAP_MESSAGE_KEY_REQUEST = "request";

    /**
     * Angabe um welche Art von Meldung/Auftrag es sich handelt (ABM, ABBM, ...)/(Storno, Terminverschiebung,...)
     */
    public static final String MAP_MESSAGE_KEY_REQUEST_TYP = "request.typ";

    /**
     * Angabe des Request-Zeitstempels (im Format {@code DATE_TIME_FORMAT_LONG})
     */
    public static final String MAP_MESSAGE_KEY_REQUEST_TIMESTAMP = "request.timestamp";

    /**
     * Angabe zu welchem Geschaeftsfall die Meldung gehoert z.B. TAL_NEU (NEU)
     */
    public static final String MAP_MESSAGE_KEY_REQUEST_GESCHAEFTSFALL = "request.geschaeftsfall";

    /**
     * Angabe des Meldungscodes eines Requests
     */
    public static final String MAP_MESSAGE_KEY_REQUEST_MELDUNGSCODE = "request.meldungscode";

    /**
     * Angabe des Meldungstexts eines Requests
     */
    public static final String MAP_MESSAGE_KEY_REQUEST_MELDUNGSTEXT = "request.meldungstext";

    /*
     * other properties
     */

    /**
     * Allgemeines Timestamp-Format fuer das IO-Archiv
     */
    public static final String DATE_TIME_FORMAT_LONG = "dd.MM.yyyy HH:mm:ss:SSS";

}
