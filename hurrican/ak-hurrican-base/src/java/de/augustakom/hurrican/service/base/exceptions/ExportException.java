/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.12.2005 10:48:59
 */
package de.augustakom.hurrican.service.base.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Wird geworfen, wenn beim Export von Daten ein Fehler auftritt.
 *
 *
 */
public class ExportException extends LanguageException {

    private static final String RESOURCE =
            "de.augustakom.hurrican.service.base.exceptions.ExportException";

    /**
     * Default Error-Message fuer einen Export-Fehler.
     */
    public static final String _UNEXPECTED_ERROR = "0";
    /**
     * Key fuer die Exception-Message, dass keine zu exportierenden Daten ermittelt werden konnten.
     */
    public static final String EXPORT_FAILURE_NO_DATA = "1";

    /**
     * Key fuer die Exception-Message, dass der Strassen-Export fehlgeschlagen hat.
     */
    public static final String EXPORT_SL_FAILURE = "100";
    /**
     * Key fuer die Exception-Message, wenn beim Export eines best. Verlaufs ein Fehler auftritt.
     */
    public static final String EXPORT_VERLAUF_FAILURE = "101";
    /**
     * Key fuer die Exception-Message, wenn beim Export eines best. Auftrags ein Fehler auftritt.
     */
    public static final String EXPORT_AUFTRAG_FAILURE = "102";
    /**
     * Key fuer die Exception-Message, wenn beim Erstellen des Export-Files ein Fehler auftritt.
     */
    public static final String EXPORT_CREATE_FILE_FAILURE = "103";
    /**
     * Key fuer die Exception-Message, wenn das Export-Datum nicht definiert wurde.
     */
    public static final String EXPORT_DATE_NOT_DEFINED = "104";

    /**
     * Default-Konstruktor.
     */
    public ExportException() {
        super();
    }

    /**
     * @param msgKey
     * @param locale
     */
    public ExportException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param locale
     */
    public ExportException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     * @param locale
     */
    public ExportException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     */
    public ExportException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @param msgKey
     * @param params
     */
    public ExportException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @param msgKey
     * @param cause
     * @param locale
     */
    public ExportException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @param msgKey
     * @param cause
     */
    public ExportException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @param msgKey
     */
    public ExportException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    public String getResourceString() {
        return RESOURCE;
    }

}


