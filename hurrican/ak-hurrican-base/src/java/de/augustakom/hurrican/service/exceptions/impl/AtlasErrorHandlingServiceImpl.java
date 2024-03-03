/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2014
 */
package de.augustakom.hurrican.service.exceptions.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.exceptions.AtlasErrorHandlingService;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;
import de.augustakom.hurrican.service.exceptions.helper.HandleErrorHelper;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.ErrorHandlingService;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

/**
 *
 */
public class AtlasErrorHandlingServiceImpl implements AtlasErrorHandlingService {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AtlasErrorHandlingService.class);

    @Autowired
    private ErrorHandlingService errorHandlingService;
    @Autowired
    private ExceptionLogService exceptionLogService;

    @Override
    @CcTxRequiresNew
    public void handleError(HandleError error) {
        try {
            LOGGER.info(String.format("Calling ATLAS ESB ErrorHandlingService for error: %s - %s",
                    error.getError().getCode(), error.getError().getMessage()));
            errorHandlingService.handleError(error);
        }
        catch (Exception e) {
            LOGGER.info("Error during the call to the ATLAS ESB ErrorHandlingService. " +
                    "\n\t... try to log the error in the exception log table!");

            String errorMessageString = HandleErrorHelper.handleErrorToString(error);
            exceptionLogService.saveExceptionLogEntry(new ExceptionLogEntry(
                            ExceptionLogEntryContext.ATLAS_ERROR_SERVICE_ERROR,
                            new FFMServiceException("Unable to send message to ATLAS ESB ErrorHandlingService:\n" + errorMessageString, e))
            );
        }
    }
}
