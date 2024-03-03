/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.11.2008 11:36:59
 */
package de.augustakom.hurrican.service.base.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Exception fuer den Ressourcen-Monitor.
 *
 *
 */
public class MonitorException extends LanguageException {

    private static final String RESOURCE =
            "de.augustakom.hurrican.service.base.exceptions.MonitorException";

    /**
     * Default Error-Message fuer einen Export-Fehler.
     */
    public static final String _UNEXPECTED_ERROR = "0";
    /**
     * Key fuer die Exception-Message, dass bereits ein Monitor-Run lauft.
     */
    public static final String RS_MONITOR_RUNNING_FAILURE = "100";

    /**
     * Default-Konstruktor.
     */
    public MonitorException() {
        super();
    }

    /**
     * @param msgKey
     * @param locale
     */
    public MonitorException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param locale
     */
    public MonitorException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     * @param locale
     */
    public MonitorException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     */
    public MonitorException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @param msgKey
     * @param params
     */
    public MonitorException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @param msgKey
     * @param cause
     * @param locale
     */
    public MonitorException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @param msgKey
     * @param cause
     */
    public MonitorException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @param msgKey
     */
    public MonitorException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    public String getResourceString() {
        return RESOURCE;
    }

}


