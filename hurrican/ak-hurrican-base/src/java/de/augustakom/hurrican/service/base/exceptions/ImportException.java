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
 * Wird geworfen, wenn beim Import von Daten ein Fehler auftritt.
 *
 *
 */
public class ImportException extends LanguageException {

    private static final String RESOURCE =
            "de.augustakom.hurrican.service.base.exceptions.ImportException";

    /**
     * Default Error-Message fuer einen Export-Fehler.
     */
    public static final String _UNEXPECTED_ERROR = "0";

    /**
     * Default-Konstruktor.
     */
    public ImportException() {
        super();
    }

    /**
     * @param msgKey
     * @param locale
     */
    public ImportException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param locale
     */
    public ImportException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     * @param locale
     */
    public ImportException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     */
    public ImportException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @param msgKey
     * @param params
     */
    public ImportException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @param msgKey
     * @param cause
     * @param locale
     */
    public ImportException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @param msgKey
     * @param cause
     */
    public ImportException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @param msgKey
     */
    public ImportException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    public String getResourceString() {
        return RESOURCE;
    }

}


