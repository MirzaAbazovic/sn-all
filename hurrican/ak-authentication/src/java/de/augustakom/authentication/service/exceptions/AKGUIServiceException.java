/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.06.2004 08:25:35
 */
package de.augustakom.authentication.service.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageRuntimeException;


/**
 * Exception wird geworfen, wenn waehrend der Rechte-Auswertung fuer eine GUI-Komponente ein Fehler auftrat. <br> <br>
 * Zu beachten: die Exception ist vom Typ RuntimeException!
 *
 *
 */
public class AKGUIServiceException extends LanguageRuntimeException {

    /**
     * MessageKey fuer einen unbekannten Fehler.
     */
    public static final String _UNEXPECTED_ERROR = "unexpected";
    /**
     * MessageKey, wenn bei der Rechte-Ermittlung von GUI-Komponenten ein Fehler auftritt.
     */
    public static final String ERROR_EVALUATE_COMPONENT_RIGHTS = "100";

    private static final String RESOURCE = "de.augustakom.authentication.service.exceptions.AKGUIServiceException";

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageRuntimeException()
     */
    public AKGUIServiceException() {
        super();
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageRuntimeException(String)
     */
    public AKGUIServiceException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageRuntimeException(String, Locale)
     */
    public AKGUIServiceException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageRuntimeException(String, Throwable)
     */
    public AKGUIServiceException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageRuntimeException(String, Throwable, Locale)
     */
    public AKGUIServiceException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageRuntimeException(String, Object[])
     */
    public AKGUIServiceException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageRuntimeException(String, Object[], Locale)
     */
    public AKGUIServiceException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageRuntimeException(String, Object[], Throwable)
     */
    public AKGUIServiceException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageRuntimeException(String, Object[], Throwable, Locale)
     */
    public AKGUIServiceException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    @Override
    public String getResourceString() {
        return RESOURCE;
    }

}


