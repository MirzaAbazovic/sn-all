/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.2011 13:13:05
 */
package de.augustakom.hurrican.model.cc;

import java.time.*;
import java.util.*;

import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;

/**
 * Builder fuer
 */
@SuppressWarnings("unused")
public class ExceptionLogEntryBuilder extends AbstractCCIDModelBuilder<ExceptionLogEntryBuilder, ExceptionLogEntry> implements IServiceObject {

    private String context = "hurrican";
    private String host = "host";
    private String errorMessage = "Error";
    private String stacktrace = "Error Message: Stacktrace";
    private Date dateOccurred = new Date();
    private String bearbeiter;
    private String solution;

    public ExceptionLogEntryBuilder withContext(String context) {
        this.context = context;
        return this;
    }

    public ExceptionLogEntryBuilder withHost(String host) {
        this.host = host;
        return this;
    }
}


