/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 12.05.2004 09:54:14
 */
package de.augustakom.hurrican.service.base.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Wird geworfen, wenn beim Suchen von Objekten ein Fehler aufgetreten ist. <br> Diese Exception sollte nicht geworfen
 * werden, wenn ueber die Suchparameter keine Treffermenge gefunden werden konnte. Sie sollte nur dann verwendet werden,
 * wenn nicht vorhergesehene Fehler (z.B. keine DB-Verbindung oder ungueltige Parameterliste) auftreten.
 *
 *
 */
public class FindException extends LanguageException {

    /**
     * MessageKey, dass die Such-Strategie vom Service nicht unterstuetzt wird.
     */
    public static final String FIND_STRATEGY_NOT_SUPPORTED = "0";

    /**
     * MessageKey, dass ein nicht erwarteter Fehler aufgetreten ist.
     */
    public static final String _UNEXPECTED_ERROR = "100";

    /**
     * MessageKey, dass keine Suchparameter uebergeben wurden bzw. ein Query-Objekt 'leer' ist.
     */
    public static final String EMPTY_FIND_PARAMETER = "101";
    /**
     * MessageKey, wenn ungueltige Suchparameter uebergeben wurden.
     */
    public static final String INVALID_FIND_PARAMETER = "102";
    /**
     * MessageKey, wenn zu wenige Suchparameter uebergeben wurden.
     */
    public static final String INSUFFICIENT_FIND_PARAMETER = "103";

    /**
     * MessageKey, wenn fuer eine Endstelle keine Rangierung ermittelt werden konnte.
     */
    public static final String RANGIERUNG_4_ES_NOT_FOUND = "200";

    /**
     * MessageKey, wenn ein Registry-Eintrag nicht gefunden werden konnte. (Parameter 0: Registry-ID)
     */
    public static final String REGISTRY_KEY_NOT_FOUND = "300";

    /**
     * MessageKey, wenn die Ergebnismenge einer Abfrage eine ungueltige Menge liefert. <br> Parameter: <br> 0: Anzahl
     * der erwarteten Results <br> 1: Anzahl der erhaltenen Results <br>
     */
    public static final String INVALID_RESULT_SIZE = "400";

    /**
     * MessageKey, wenn die Ergebnismenge einer Abfrage eine ungueltige Menge liefert. <br> Parameter: <br> 0: Anzahl
     * der erwarteten Results <br> 1: Anzahl der erhaltenen Results <br>
     */
    public static final String INVALID_RESULT_SIZE_WITH_FILTER = "401";

    private static final String RESOURCE =
            "de.augustakom.hurrican.service.base.exceptions.FindException";

    /**
     * @see LanguageException()
     */
    public FindException() {
        super();
    }

    /**
     * @see LanguageException(String)
     */
    public FindException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see LanguageException(String, Locale)
     */
    public FindException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @see LanguageException(Throwable)
     */
    public FindException(Throwable cause) {
        super("", cause);
    }

    /**
     * @see LanguageException(String, Throwable)
     */
    public FindException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @see LanguageException(String, Throwable, Loccale)
     */
    public FindException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @see LanguageException(String, Object[])
     */
    public FindException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @see LanguageException(String, Object[], Locale)
     */
    public FindException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @see LanguageException(String, Object[], Throwable)
     */
    public FindException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @see LanguageException(String, Object[], Throwable, Locale)
     */
    public FindException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    @Override
    public String getResourceString() {
        return RESOURCE;
    }

}
