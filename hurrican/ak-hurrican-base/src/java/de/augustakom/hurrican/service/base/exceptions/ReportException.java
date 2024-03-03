/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 27.03.2007 09:54:14
 */
package de.augustakom.hurrican.service.base.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Wird geworfen, wenn im Report-Service ein Fehler auftritt. <br>
 *
 *
 */
public class ReportException extends LanguageException {

    /**
     * MessageKey, dass ein nicht erwarteter Fehler aufgetreten ist.
     */
    public static final String _UNEXPECTED_ERROR = "0";

    /**
     * MessageKey, wenn ungueltige Parameter uebergeben wurden.
     */
    public static final String INVALID_PARAMETER = "100";

    /**
     * MessageKey, wenn keine Verbindung zu OpenOffice besteht / aufgebaut werden kann.
     */
    public static final String NO_OO_CONNECTION = "200";
    /**
     * MessageKey, wenn keine Verbindung zum JMS besteht / aufgebaut werden kann.
     */
    public static final String NO_JMS_CONNECTION = "201";

    /**
     * MessageKey, wenn ein Fehler im Report-Template vorliegt
     */
    public static final String TEMPLATE_ERROR = "300";

    private static final String RESOURCE =
            "de.augustakom.hurrican.service.base.exceptions.ReportException";

    /**
     * @see LanguageException()
     */
    public ReportException() {
        super();
    }

    /**
     * @see LanguageException(String)
     */
    public ReportException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see LanguageException(String, Locale)
     */
    public ReportException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @see LanguageException(Throwable)
     */
    public ReportException(Throwable cause) {
        super("", cause);
    }

    /**
     * @see LanguageException(String, Throwable)
     */
    public ReportException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @see LanguageException(String, Throwable, Loccale)
     */
    public ReportException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @see LanguageException(String, Object[])
     */
    public ReportException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @see LanguageException(String, Object[], Locale)
     */
    public ReportException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @see LanguageException(String, Object[], Throwable)
     */
    public ReportException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @see LanguageException(String, Object[], Throwable, Locale)
     */
    public ReportException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    public String getResourceString() {
        return RESOURCE;
    }

}
