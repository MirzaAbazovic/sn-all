/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2014
 */
package de.augustakom.hurrican.exceptions;

import java.util.*;

import de.augustakom.hurrican.service.cc.MailService;

/**
 * Represents a collection of Exceptions which could be thrown during the {@link MailService#processPendingEmails()}
 * call.
 */
public class ProcessPendingEmailsException extends Exception {

    private static final long serialVersionUID = -3989986316327087501L;
    private final List<Exception> nestedExceptions;

    public ProcessPendingEmailsException(String message, List<Exception> exceptions) {
        super(message);
        nestedExceptions = exceptions;
    }

    public List<Exception> getNestedExceptions() {
        return nestedExceptions;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        if (nestedExceptions != null) {
            for (Exception e : nestedExceptions) {
                sb.append("; ").append(e.getMessage());
            }
        }
        return sb.toString();
    }
}
