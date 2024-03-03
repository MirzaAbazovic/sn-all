/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.11.2004 13:37:11
 */
package de.augustakom.hurrican.service.base.exceptions;

import java.util.*;

import de.augustakom.common.service.exceptions.ServiceCommandException;


/**
 * Implementierung von <code>ServiceCommandException</code> fuer die Hurrican-Applikation.
 *
 *
 */
public class HurricanServiceCommandException extends ServiceCommandException {

    /**
     * Msg-Key, wenn ungueltige Parameter an eine Command-Klasse uebergeben wurden.
     */
    public static final String INVALID_PARAMETER_4_COMMAND = "100";

    private static final String RESOURCE_FILE =
            "de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException";

    /**
     * Standardkonstruktor.
     */
    public HurricanServiceCommandException() {
        super();
    }

    /**
     * @param msgKey
     */
    public HurricanServiceCommandException(String msgKey) {
        super(msgKey);
    }

    /**
     * @param msgKey
     * @param locale
     */
    public HurricanServiceCommandException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @param msgKey
     * @param params
     */
    public HurricanServiceCommandException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @param msgKey
     * @param params
     * @param locale
     */
    public HurricanServiceCommandException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     */
    public HurricanServiceCommandException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     * @param locale
     */
    public HurricanServiceCommandException(String msgKey, Object[] params, Throwable cause,
            Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @param msgKey
     * @param cause
     */
    public HurricanServiceCommandException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @param cause
     */
    public HurricanServiceCommandException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    /**
     * @param msgKey
     * @param cause
     * @param locale
     */
    public HurricanServiceCommandException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    public String getResourceString() {
        return RESOURCE_FILE;
    }

}


