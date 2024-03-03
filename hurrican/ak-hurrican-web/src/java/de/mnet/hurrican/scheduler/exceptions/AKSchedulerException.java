/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2005 16:33:23
 */
package de.mnet.hurrican.scheduler.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;

/**
 * Basis-Exception fuer Fehlermeldungen des AK-Schedulers.
 *
 *
 */
public class AKSchedulerException extends LanguageException {

    private static final long serialVersionUID = -3687882706000007985L;
    private static final String RESOURCE = "de.mnet.hurrican.scheduler.exceptions.AKSchedulerException";

    public AKSchedulerException() {
        super();
    }

    public AKSchedulerException(String msgKey) {
        super(msgKey);
    }

    public AKSchedulerException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    public AKSchedulerException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    public AKSchedulerException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    public AKSchedulerException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    public AKSchedulerException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    public AKSchedulerException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    @Override
    public String getResourceString() {
        return RESOURCE;
    }

}
