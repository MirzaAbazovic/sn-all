/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004 13:04:45
 */
package de.augustakom.authentication.service.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Standard-Exception fuer den AuthenticationService. <br> Diese Exception wird z.B. geworfen, wenn sich ein User mit
 * ungueltiger Kombination von Benutzername/Passwort einloggt.
 *
 *
 */
public class AKAuthenticationException extends LanguageException {

    /**
     * MessageKey fuer einen unbekannten Fehler.
     */
    public static final String _UNEXPECTED_ERROR = "unexpected";

    /**
     * MessageKey, dass Kombination von Benutzername/-Passwort ungueltig ist.
     */
    public static final String MSG_INVALID_USER_OR_PASSWORD = "100";
    /**
     * MessageKey, dass der Applikationsname ungueltig ist.
     */
    public static final String MSG_INVALID_APPLICATION_NAME = "101";
    /**
     * MessageKey, dass der HostName ungueltig ist.
     */
    public static final String MSG_INVALID_HOST_NAME = "118";
    /**
     * MessageKey, dass die Session-ID ungueltig ist.
     */
    public static final String MSG_INVALID_SESSION_ID = "102";
    /**
     * MessageKey, dass die Session abgelaufen ist.
     */
    public static final String MSG_SESSION_TIMED_OUT = "103";
    /**
     * MessageKey, dass Login-Daten fehlen.
     */
    public static final String MSG_NO_LOGIN_DATA = "104";
    /**
     * MessageKey, dass eine UserSession nicht angelegt werden konnte.
     */
    public static final String MSG_USERSESSION_NOT_CREATED = "105";
    /**
     * MessageKey, dass ein gesuchter User nicht existiert.
     */
    public static final String MSG_USER_NOT_EXIST = "106";
    /**
     * MessageKey, dass ein Logout nicht erfolgreich war.
     */
    public static final String MSG_LOGOUT_FAILES = "107";
    /**
     * MessageKey, dass der Benutzer nicht aktiv geschalten ist. Benoetigte Parameter: <br> 0: Loginname
     */
    public static final String MSG_USER_INACTIVE = "108";
    /**
     * MessageKey, dass kein User oder keine Rollen uebergeben wurden.
     */
    public static final String MSG_NO_USER_OR_ROLES = "109";
    /**
     * MessageKey, dass beim Schreiben der Benutzer-Rollen-Zuordnung ein Fehler auftrat.
     */
    public static final String MSG_ERROR_SETTING_ROLES = "110";
    /**
     * MessageKey, dass kein User oder keine Accounts uebergeben wurden.
     */
    public static final String MSG_NO_USER_OR_ACCOUNTS = "111";

    /**
     * MessageKey, dass ein Benutzer nicht geloescht werden konnte. <br> Benoetigte Parameter: <br> 0: ID des zu
     * loeschenden Benutzers.
     */
    public static final String MSG_ERROR_DELETING_USER = "112";

    /**
     * MessageKey, dass eine Rolle nicht geloescht werden konnte. <br> Benoetigte Parameter: <br> 0: ID der zu
     * loeschenden Rolle.
     */
    public static final String MSG_ERROR_DELETING_ROLE = "113";

    /**
     * MessageKey, dass ein DB-Account nicht geloescht werden konnte. <br> Benoetigte Parameter: <br> 0: ID des zu
     * loeschenden Accounts.
     */
    public static final String MSG_ERROR_DELETE_ACCOUNT = "114";

    /**
     * MessageKey, dass dem User fuer eine Applikation keine Rolle zugeordnet ist.
     */
    public static final String MSG_USER_HAS_NO_ROLES = "115";
    /**
     * MessageKey, dass ein User nicht gefunden/ermittelt werden konnte.
     */
    public static final String MSG_USER_NOT_FOUND = "116";

    /**
     * MessageKey, dass eine DB nicht geloescht werden konnte. <br> Benoetigte Parameter: <br> 0: ID der zu loeschenden
     * DB.
     */
    public static final String MSG_ERROR_DELETE_DB = "117";

    /**
     * MessageKey, dass das Passwort nicht verschluesselt werden konnte
     */
    public static final String MSG_ENCRYPTION_ERROR = "200";

    /**
     * MessageKey, wenn ein ungueltiges Expiration-Date angegeben wird.
     */
    public static final String MSG_EXPIRATION_DATE_INVALID = "300";

    private static final String RESOURCE =
            "de.augustakom.authentication.service.exceptions.AKAuthenticationException";

    /**
     * @see LanguageException()
     */
    public AKAuthenticationException() {
        super();
    }

    /**
     * @see LanguageException(String)
     */
    public AKAuthenticationException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see LanguageException(String, Locale)
     */
    public AKAuthenticationException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @see LanguageException(String, Object[])
     */
    public AKAuthenticationException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @see LanguageException(String, Object[], Locale)
     */
    public AKAuthenticationException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @see LanguageException(String, Object[], Throwable)
     */
    public AKAuthenticationException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @see LanguageException(String, Object[], Throwable, Locale)
     */
    public AKAuthenticationException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @see LanguageException(String, Throwable)
     */
    public AKAuthenticationException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @see LanguageException(String, Throwable, Locale)
     */
    public AKAuthenticationException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    @Override
    public String getResourceString() {
        return RESOURCE;
    }
}
