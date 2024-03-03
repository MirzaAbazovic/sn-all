/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 16:54:46
 */
package de.mnet.hurrican.scheduler.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;

/**
 * Exception fuer die SchedulerService, wenn bei einer Delete-Aktion ein Fehler auftritt.
 *
 *
 */
public class AKSchedulerDeleteException extends LanguageException {

    private static final long serialVersionUID = -1897104931137965033L;

    private static final String RESOURCE = "de.mnet.hurrican.scheduler.exceptions.AKSchedulerDeleteException";

    /**
     * Key fuer die Fehlermeldung, dass ein unerwarteter Fehler aufgetreten ist.
     */
    public static final String _UNEXPECTED_ERROR = "0";

    public AKSchedulerDeleteException() {
        super();
    }

    public AKSchedulerDeleteException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    public AKSchedulerDeleteException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    public AKSchedulerDeleteException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    public AKSchedulerDeleteException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    public AKSchedulerDeleteException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    public AKSchedulerDeleteException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    public AKSchedulerDeleteException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    public AKSchedulerDeleteException(String msgKey) {
        super(msgKey);
    }

    @Override
    public String getResourceString() {
        return RESOURCE;
    }

}
