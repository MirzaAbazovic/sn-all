/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.11.2011 15:36:28
 */
package de.augustakom.hurrican.service.exceptions.impl;

import java.time.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.hurrican.annotation.CcTxNotSupported;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.exceptions.ExceptionLogEntryDAO;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;

@CcTxRequired
public class ExceptionLogServiceImpl implements ExceptionLogService {

    @Autowired
    private ExceptionLogEntryDAO exceptionLogEntryDAO;

    @Override
    public String getMailtextForThrownExceptions() {
        Long exceptionCount = exceptionLogEntryDAO.getNewExceptionLogEntriesCount();
        if (exceptionCount > 0) {
            return String.format("Es sind %s fachlich relevante Fehler in Hurrican aufgetreten. Bitte in der Tabelle " +
                    "T_EXCEPTION_LOG nachschauen!", exceptionCount);
        }
        return null;
    }

    @Override
    public List<ExceptionLogEntry> findNewExceptionLogEntries(ExceptionLogEntryContext context, int maxResults) {
        return exceptionLogEntryDAO.findNewExceptionLogEntries(context, maxResults);
    }

    @Override
    @CcTxRequiresNew
    public ExceptionLogEntry saveExceptionLogEntry(ExceptionLogEntry exception) {
        Preconditions.checkNotNull(exception);
        Preconditions.checkNotNull(exception.getHost());
        Preconditions.checkNotNull(exception.getErrorMessage());
        if (exception.getDateOccurred() == null) {
            exception.setDateOccurred(new Date());
        }
        exceptionLogEntryDAO.store(exception);
        return exception;
    }

    @Override
    public ExceptionLogEntry findExceptionLogEntryById(Long id) {
        Preconditions.checkNotNull(id);
        return exceptionLogEntryDAO.findById(id, ExceptionLogEntry.class);
    }

}
