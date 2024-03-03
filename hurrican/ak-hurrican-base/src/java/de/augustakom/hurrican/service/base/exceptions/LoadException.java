/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 12.05.2004 09:48:22
 */
package de.augustakom.hurrican.service.base.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Wird geworfen, wenn beim Laden eines Objetks ein Fehler aufgetreten ist (z.B. ungueltige ID).
 *
 *
 */
public class LoadException extends LanguageException {

    /**
     * MessageKey, dass ein nicht erwarteter Fehler aufgetreten ist.
     */
    public static final String _UNEXPECTED_ERROR = "100";
    /**
     * MessageKey, wenn ein Datensatz nicht geladen werden konnte. <br> Parameter: <ul> <li>0: Name des zu ladenden
     * Objekts. <li>1: ID des zu ladenden Objekts. </ul>
     */
    public static final String COULD_NOT_LOAD = "101";

    /**
     * MessageKey, wenn ungueltige Parameter uebergeben wurden. <br> Parameter: <ul> <li>0: Name des zu ladenden
     * Objekts. </ul>
     */
    public static final String INVALID_LOAD_PARAMS = "102";

    private static final String RESOURCE =
            "de.augustakom.hurrican.service.base.exceptions.LoadException";

    /**
     * @see LanguageException()
     */
    public LoadException() {
        super();
    }

    /**
     * @see LanguageException(String)
     */
    public LoadException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see LanguageException(String, Locale)
     */
    public LoadException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @see LanguageException(String, Throwable)
     */
    public LoadException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @see LanguageException(String, Throwable, Loccale)
     */
    public LoadException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @see LanguageException(String, Object[])
     */
    public LoadException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @see LanguageException(String, Object[], Locale)
     */
    public LoadException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @see LanguageException(String, Object[], Throwable)
     */
    public LoadException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @see LanguageException(String, Object[], Throwable, Locale)
     */
    public LoadException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    public String getResourceString() {
        return RESOURCE;
    }

}
