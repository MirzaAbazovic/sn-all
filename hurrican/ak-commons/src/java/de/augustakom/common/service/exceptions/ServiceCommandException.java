/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2004 09:36:03
 */
package de.augustakom.common.service.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Exception, die von ServiceCommand-Klassen geworfen werden kann.
 *
 *
 */
public abstract class ServiceCommandException extends LanguageException {

    /**
     * Standardkonstruktor.
     */
    public ServiceCommandException() {
        super();
    }

    /**
     * @param msgKey
     */
    public ServiceCommandException(String msgKey) {
        super(msgKey);
    }

    /**
     * @param msgKey
     * @param locale
     */
    public ServiceCommandException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @param msgKey
     * @param params
     */
    public ServiceCommandException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @param msgKey
     * @param params
     * @param locale
     */
    public ServiceCommandException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     */
    public ServiceCommandException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     * @param locale
     */
    public ServiceCommandException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @param msgKey
     * @param cause
     */
    public ServiceCommandException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @param msgKey
     * @param cause
     * @param locale
     */
    public ServiceCommandException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }
}


