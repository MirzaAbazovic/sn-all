/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2004 10:15:27
 */
package de.augustakom.authentication.service.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Exception wird geworfen, wenn das Passwort des Benutzers abgelaufen ist oder der Benutzer ein Passwort vergeben will,
 * das er bereits einmal verwendet hat. <br>
 *
 *
 */
public class AKPasswordException extends LanguageException {

    /**
     * MessageKey, dass Passwort-Gueltigkeit abgelaufen ist.
     */
    public static final String MSG_PASSWORD_EXPIRED = "100";
    /**
     * MessageKey, dass das Passwort bereits einmal verwendet wurde.
     */
    public static final String MSG_PASSWORD_ALREADY_USED = "101";
    /**
     * MessageKey, dass das Passwort in der Datenbank und das angegebene Passwort nicht gleich sind.
     */
    public static final String MSG_PASSWORDS_NOT_EQUAL = "102";

    /**
     * MessageKey, dass das Passwort zu kurz ist. <br> Benoetigte Parameter: <br> 0: Mindestlaenge des Passwortes.
     */
    public static final String MSG_PASSWORT_TO_SHORT = "103";
    /**
     * MessageKey, dass ein Benutzer-Passwort nicht geaendert werden konnte.
     */
    public static final String MSG_PASSWORD_NOT_CHANGED = "104";

    private static final String RESOURCE =
            "de.augustakom.authentication.service.exceptions.AKPasswordException";

    /**
     * @see LanguageException()
     */
    public AKPasswordException() {
        super();
    }

    /**
     * @see LanguageException(String)
     */
    public AKPasswordException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see LanguageException(String, Locale)
     */
    public AKPasswordException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @see LanguageException(String, Throwable)
     */
    public AKPasswordException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @see LanguageException(String, Throwable, Locale)
     */
    public AKPasswordException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @see LanguageException(String, Object[])
     */
    public AKPasswordException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @see LanguageException(String, Object[], Locale)
     */
    public AKPasswordException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @see LanguageException(String, Object[], Throwable)
     */
    public AKPasswordException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @see LanguageException(String, Object[], Throwable, Locale)
     */
    public AKPasswordException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    @Override
    public String getResourceString() {
        return RESOURCE;
    }

}
