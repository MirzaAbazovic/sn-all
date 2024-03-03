/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 */
package de.augustakom.common.tools.exceptions;

import java.util.*;


/**
 * Ableitung von LanguageException, um den Test durchfuehren zu koennen.
 *
 *
 */
public class LanguageExceptionTestImpl extends LanguageException {

    public LanguageExceptionTestImpl(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    public LanguageExceptionTestImpl(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    public LanguageExceptionTestImpl(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    public LanguageExceptionTestImpl(String msgKey, Object[] params,
            Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    @Override
    public String getResourceString() {
        return "de.augustakom.common.tools.exceptions.LanguageException";
    }
}
