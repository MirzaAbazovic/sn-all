/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 14:58:32
 */
package de.augustakom.authentication.gui.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Exception wird verwendet, um innerhalb der GUI auf Fehler hinzuweisen.
 *
 *
 */
public class GUIException extends LanguageException {

    private static final String RESOURCE = "de.augustakom.authentication.gui.exceptions.GUIException";

    /**
     * MessageKey, wenn der urspruengliche Fehler nicht bekannt ist.
     */
    public static final String _UNKNOWN = "unknown";

    /* **************** Messages fuer User (100-199) ******************** */
    /**
     * MessageKey, wenn beim Speichern eines Users ein Fehler aufgetreten ist.
     */
    public static final String USER_SAVING_ERROR = "100";
    public static final String USER_VALIDATE_LOGINNAME = "101";
    public static final String USER_VALIDATE_NAME = "102";
    public static final String USER_VALIDATE_FIRSTNAME = "103";
    public static final String USER_VALIDATE_DEPARTMENT = "104";
    public static final String USER_VALIDATE_PASSWORD = "105";
    public static final String USER_VALIDATE_DATE = "106";
    public static final String USER_VALIDATE_ROLES = "107";
    public static final String USERS_FOR_ROLE_NOT_FOUND = "108";
    public static final String USERS_FOR_ACCOUNT_NOT_FOUND = "109";
    public static final String USER_VALIDATE_NIEDERLASSUNG = "110";
    public static final String USER_VALIDATE_EXT_PARTNER = "111";
    public static final String USER_VALIDATE_DEP_EXTERN = "112";

    /* **************** Messages fuer Role (200-299) ******************** */
    /**
     * MessageKey, wenn beim Speichern einer Rolle ein Fehler aufgetreten ist.
     */
    public static final String ROLE_SAVING_ERROR = "200";
    public static final String ROLE_VALIDATE_NAME = "201";
    public static final String ROLE_VALIDATE_APPLICATION = "202";

    /* **************** Messages fuer Account (300-399) ******************** */
    public static final String ACCOUNT_SAVING_ERROR = "300";
    public static final String ACCOUNT_VALIDATE_NAME = "301";
    public static final String ACCOUNT_VALIDATE_APPLICATION = "302";
    public static final String ACCOUNT_VALIDATE_USER = "303";

    /* **************** Messages fuer DB (400-499) ******************** */
    public static final String DB_SAVING_ERROR = "400";
    public static final String DB_VALIDATE_NAME = "401";
    public static final String DB_VALIDATE_DRIVER = "402";
    public static final String DB_VALIDATE_URL = "403";

    /* **************** Messages fuer Department (500-599) ******************** */
    /**
     * MessageKey, wenn beim Speichern einer Rolle ein Fehler aufgetreten ist.
     */
    public static final String DEPARTMENT_SAVING_ERROR = "500";
    public static final String DEPARTMENT_VALIDATE_NAME = "501";
    public static final String DEPARTMENT_TREE_NODE_MISSING = "502";

    /* **************** Messages fuer Team (600-699) ******************** */
    /**
     * MessageKey, wenn beim Speichern einer Rolle ein Fehler aufgetreten ist.
     */
    public static final String TEAM_VALIDATE_NAME = "600";

    /**
     *
     */
    public GUIException() {
        super();
    }

    /**
     * @param msgKey
     */
    public GUIException(String msgKey) {
        super(msgKey);
    }

    /**
     * @param msgKey
     * @param locale
     */
    public GUIException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @param msgKey
     * @param cause
     */
    public GUIException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @param msgKey
     * @param cause
     * @param locale
     */
    public GUIException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @param msgKey
     * @param params
     */
    public GUIException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @param msgKey
     * @param params
     * @param locale
     */
    public GUIException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     */
    public GUIException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @param msgKey
     * @param params
     * @param cause
     * @param locale
     */
    public GUIException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    @Override
    public String getResourceString() {
        return RESOURCE;
    }

}
