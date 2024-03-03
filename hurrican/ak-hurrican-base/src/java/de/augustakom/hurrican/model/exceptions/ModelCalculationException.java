/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 01.02.2011 14:14:30
 */
package de.augustakom.hurrican.model.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Wird geworfen, wenn bei einer Berechnung ein Fehler auftritt.
 */
public class ModelCalculationException extends LanguageException {

    /**
     * MessageKey, dass ein nicht erwarteter Fehler aufgetreten ist.
     */
    public static final String _UNEXPECTED_ERROR = "100";

    private static final String RESOURCE =
            "de.augustakom.hurrican.model.exceptions.ModelCalculationException";

    /**
     * @see LanguageException()
     */
    public ModelCalculationException() {
        super();
    }

    /**
     * @see LanguageException(String)
     */
    public ModelCalculationException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see LanguageException(String, Locale)
     */
    public ModelCalculationException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @see LanguageException(String, Throwable)
     */
    public ModelCalculationException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @see LanguageException(String, Throwable, Loccale)
     */
    public ModelCalculationException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @see LanguageException(String, Object[])
     */
    public ModelCalculationException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @see LanguageException(String, Object[], Locale)
     */
    public ModelCalculationException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @see LanguageException(String, Object[], Throwable)
     */
    public ModelCalculationException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @see LanguageException(String, Object[], Throwable, Locale)
     */
    public ModelCalculationException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    @Override
    public String getResourceString() {
        return RESOURCE;
    }

}
