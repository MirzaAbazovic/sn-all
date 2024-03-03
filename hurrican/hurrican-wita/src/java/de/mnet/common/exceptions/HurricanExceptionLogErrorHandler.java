/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.11.2011 12:46:56
 */
package de.mnet.common.exceptions;

import org.apache.camel.ExchangeException;
import org.apache.camel.Handler;
import org.springframework.util.ErrorHandler;

import de.augustakom.hurrican.service.exceptions.ExceptionLogService;

/**
 * Spezieller Error-Handler, der es zulaesst den ExceptionLogService fuer Tests zu ueberschreiben.
 */
public interface HurricanExceptionLogErrorHandler extends ErrorHandler {

    /**
     * Is Spring-Error Handler and Bean-Processor for Camel. Wird von Camel via ParameterBinding benutzt.
     */
    @Override
    @Handler
    void handleError(@ExchangeException Throwable throwable);

    /**
     * For testing only
     */
    void setExceptionLogService(ExceptionLogService exceptionLogService);

}
