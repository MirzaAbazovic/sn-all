/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 16:57:44
 */
package de.mnet.hurrican.scheduler.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;

/**
 * Exception wird geworfen, wenn ein Scheduler-Service eine Find-Operation nicht durchfuehren kann oder bei der
 * Ausfuehrung ein Fehler auftritt.
 *
 *
 */
public class AKSchedulerFindException extends LanguageException {

    private static final long serialVersionUID = -7601534108569458127L;

    private static final String RESOURCE = "de.mnet.hurrican.scheduler.exceptions.AKSchedulerFindException";

    /**
     * Key fuer die Fehlermeldung, dass ein unerwarteter Fehler aufgetreten ist.
     */
    public static final String _UNEXPECTED_ERROR = "0";

    public AKSchedulerFindException() {
        super();
    }

    public AKSchedulerFindException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    public AKSchedulerFindException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    public AKSchedulerFindException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    public AKSchedulerFindException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    public AKSchedulerFindException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    public AKSchedulerFindException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    public AKSchedulerFindException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    public AKSchedulerFindException(String msgKey) {
        super(msgKey);
    }

    @Override
    public String getResourceString() {
        return RESOURCE;
    }

}
