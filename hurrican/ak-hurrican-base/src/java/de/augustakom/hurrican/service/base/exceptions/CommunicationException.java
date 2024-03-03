/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2009 11:09:59
 */
package de.augustakom.hurrican.service.base.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Exception wird geworfen, wenn bei der Kommunikation mit einem anderen System (z.B. CPS) ein Fehler auftritt.
 *
 *
 */
public class CommunicationException extends LanguageException {

    public static final String _UNEXPECTED_ERROR = "0";

    private static final String RESOURCE =
            "de.augustakom.hurrican.service.base.exceptions.CommunicationException";

    /**
     * Default-Const.
     */
    public CommunicationException() {
        super();
    }

    /**
     * @param msgKey
     * @param locale
     */
    public CommunicationException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param locale
     */
    public CommunicationException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     * @param locale
     */
    public CommunicationException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     */
    public CommunicationException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @param msgKey
     * @param params
     */
    public CommunicationException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @param msgKey
     * @param cause
     * @param locale
     */
    public CommunicationException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @param msgKey
     * @param cause
     */
    public CommunicationException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @param msgKey
     */
    public CommunicationException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    public String getResourceString() {
        return RESOURCE;
    }

}


