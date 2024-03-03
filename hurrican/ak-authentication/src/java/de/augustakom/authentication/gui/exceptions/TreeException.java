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
 * Exception wird geworfen, wenn bei Operationen im Admin-Tree Fehler auftreten.
 *
 *
 */
public class TreeException extends LanguageException {

    private static final String RESOURCE = "de.augustakom.authentication.gui.exceptions.TreeException";

    /**
     * MessageKey, wenn der urspruengliche Fehler nicht bekannt ist.
     */
    public static final String _UNKNOWN = "unknown";
    /**
     * MessageKey, dass fuer einen Node kein Frame gefunden wurde. <br> Benoetigte Parameter: <br> 0: Objekt-Name
     */
    public static final String MSG_FRAME_NOT_FOUND = "101";

    /**
     *
     */
    public TreeException() {
        super();
    }

    /**
     * @see LanguageException#LanguageException(String)
     */
    public TreeException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see LanguageException#LanguageException(String, Locale)
     */
    public TreeException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @see LanguageException#LanguageException(String, Throwable)
     */
    public TreeException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @see LanguageException#LanguageException(String, Throwable, Locale)
     */
    public TreeException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @see LanguageException#LanguageException(String, Object[])
     */
    public TreeException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @see LanguageException#LanguageException(String, Object[], Locale)
     */
    public TreeException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @see LanguageException#LanguageException(String, Object[], Throwable)
     */
    public TreeException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @see LanguageException#LanguageException(String, Object[], Throwable, Locale)
     */
    public TreeException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    @Override
    public String getResourceString() {
        return RESOURCE;
    }

}
