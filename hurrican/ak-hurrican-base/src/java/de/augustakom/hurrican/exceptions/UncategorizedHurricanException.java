/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.2007 15:57:31
 */
package de.augustakom.hurrican.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Exception-Klasse fuer nicht naeher definierte Exceptions aus Hurrican.
 *
 *
 */
public class UncategorizedHurricanException extends LanguageException {

    private static final String RESOURCE =
            "de.augustakom.hurrican.exceptions.UncategorizedHurricanException";

    /**
     * Default-Const.
     */
    public UncategorizedHurricanException() {
        super();
    }

    /**
     * @param msgKey
     * @param locale
     */
    public UncategorizedHurricanException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param locale
     */
    public UncategorizedHurricanException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     * @param locale
     */
    public UncategorizedHurricanException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     */
    public UncategorizedHurricanException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @param msgKey
     * @param params
     */
    public UncategorizedHurricanException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @param msgKey
     * @param cause
     * @param locale
     */
    public UncategorizedHurricanException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @param msgKey
     * @param cause
     */
    public UncategorizedHurricanException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @param msgKey
     */
    public UncategorizedHurricanException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    public String getResourceString() {
        return RESOURCE;
    }

}


