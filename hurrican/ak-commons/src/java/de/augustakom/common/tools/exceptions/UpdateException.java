/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 17.03.2008 10:11:14
 */
package de.augustakom.common.tools.exceptions;

import java.util.*;


/**
 * Wird geworfen, wenn beim Update-Vorgang ein Fehler auftritt. <br>
 *
 *
 */
public class UpdateException extends LanguageException {

    /**
     * MessageKey, dass ein nicht erwarteter Fehler aufgetreten ist.
     */
    public static final String _UNEXPECTED_ERROR = "0";

    /**
     * MessageKey, wenn ungueltige Versionsangaben uebergeben wurden.
     */
    public static final String INVALID_VERSION = "100";

    private static final String RESOURCE =
            "de.augustakom.common.tools.exceptions.UpdateException";

    /**
     * @see LanguageException()
     */
    public UpdateException() {
        super();
    }

    /**
     * @see LanguageException(String)
     */
    public UpdateException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see LanguageException(String, Locale)
     */
    public UpdateException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @see LanguageException(Throwable)
     */
    public UpdateException(Throwable cause) {
        super("", cause);
    }

    /**
     * @see LanguageException(String, Throwable)
     */
    public UpdateException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @see LanguageException(String, Throwable, Loccale)
     */
    public UpdateException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @see LanguageException(String, Object[])
     */
    public UpdateException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @see LanguageException(String, Object[], Locale)
     */
    public UpdateException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @see LanguageException(String, Object[], Throwable)
     */
    public UpdateException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @see LanguageException(String, Object[], Throwable, Locale)
     */
    public UpdateException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    public String getResourceString() {
        return RESOURCE;
    }

}
