/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 12.05.2004 09:50:30
 */
package de.augustakom.hurrican.service.base.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Wird geworfen, wenn fuer eine Operation keine Berechtigungen vorhanden sind.
 *
 *
 */
public class RoleException extends LanguageException {

    /**
     * MessageKey, wenn fuer eine Operation keine ausreichenden Berechtigungen vorhanden sind.
     */
    public static final String NOT_ENOUGH_RIGHTS = "100";

    private static final String RESOURCE =
            "de.augustakom.hurrican.service.base.exceptions.RoleException";

    /**
     * @see LanguageException()
     */
    public RoleException() {
        super();
    }

    /**
     * @see LanguageException(String)
     */
    public RoleException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see LanguageException(String, Locale)
     */
    public RoleException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @see LanguageException(String, Throwable)
     */
    public RoleException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @see LanguageException(String, Throwable, Loccale)
     */
    public RoleException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @see LanguageException(String, Object[])
     */
    public RoleException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @see LanguageException(String, Object[], Locale)
     */
    public RoleException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @see LanguageException(String, Object[], Throwable)
     */
    public RoleException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @see LanguageException(String, Object[], Throwable, Locale)
     */
    public RoleException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    public String getResourceString() {
        return RESOURCE;
    }

}
