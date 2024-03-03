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
 * Exception fuer die SchedulerService, wenn bei einer Speicher-Aktion ein Fehler auftritt.
 *
 *
 */
public class AKSchedulerStoreException extends LanguageException {

    private static final long serialVersionUID = -6912661794478375111L;

    private static final String RESOURCE = "de.mnet.hurrican.scheduler.exceptions.AKSchedulerStoreException";

    /**
     * Key fuer die Fehlermeldung, dass ein unerwarteter Fehler aufgetreten ist.
     */
    public static final String _UNEXPECTED_ERROR = "0";

    /**
     * Key fuer die Fehlermeldung, wenn aus einem JobExecutionContext die JobDetails nicht ermittelt werden konnten.
     */
    public static final String JOB_DETAIL_NOT_AVAILABLE = "100";

    public AKSchedulerStoreException() {
        super();
    }

    public AKSchedulerStoreException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    public AKSchedulerStoreException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    public AKSchedulerStoreException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    public AKSchedulerStoreException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    public AKSchedulerStoreException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    public AKSchedulerStoreException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    public AKSchedulerStoreException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    public AKSchedulerStoreException(String msgKey) {
        super(msgKey);
    }

    @Override
    public String getResourceString() {
        return RESOURCE;
    }

}
