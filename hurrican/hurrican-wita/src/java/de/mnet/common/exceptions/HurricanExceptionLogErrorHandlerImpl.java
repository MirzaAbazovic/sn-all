/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.11.2011 14:11:34
 */
package de.mnet.common.exceptions;

import static de.augustakom.common.tools.exceptions.ExceptionLogEntryContext.*;

import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.annotation.CcTxNotSupported;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;

/**
 * ErrorHandler, der Fehler in das WITA-Exception-Log schreibt.
 */
public class HurricanExceptionLogErrorHandlerImpl implements HurricanExceptionLogErrorHandler {

    @Autowired
    private ExceptionLogService exceptionLogService;

    @Override
    @CcTxNotSupported
    public void handleError(Throwable throwable) {
        exceptionLogService.saveExceptionLogEntry(new ExceptionLogEntry(
                HURRICAN_SERVER, throwable));
    }

    @Override
    public void setExceptionLogService(ExceptionLogService exceptionLogService) {
        this.exceptionLogService = exceptionLogService;
    }


}
