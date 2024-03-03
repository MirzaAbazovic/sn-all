/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 12.05.2004 09:56:40
 */
package de.augustakom.hurrican.service.base.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Wird geworfen, wenn beim Loeschen eines Objetks ein Fehler aufgetreten ist.
 *
 *
 */
public class DeleteException extends LanguageException {

    private static final long serialVersionUID = -9056144233960704135L;

    public static final String _UNEXPECTED_ERROR = "0";
    public static final String DELETE_FAILED = "100";
    public static final String INVALID_PARAMETERS = "101";
    public static final String INVALID_SESSION_ID = "102";

    private static final String RESOURCE =
            "de.augustakom.hurrican.service.base.exceptions.DeleteException";

    /**
     * @see LanguageException()
     */
    public DeleteException() {
        super();
    }

    /**
     * @see LanguageException(String)
     */
    public DeleteException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see LanguageException(String, Locale)
     */
    public DeleteException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @see LanguageException(String, Throwable)
     */
    public DeleteException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @see LanguageException(String, Throwable, Loccale)
     */
    public DeleteException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @see LanguageException(String, Object[])
     */
    public DeleteException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @see LanguageException(String, Object[], Locale)
     */
    public DeleteException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @see LanguageException(String, Object[], Throwable)
     */
    public DeleteException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @see LanguageException(String, Object[], Throwable, Locale)
     */
    public DeleteException(String msgKey, Object[] params, Throwable cause, Locale locale) {
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
