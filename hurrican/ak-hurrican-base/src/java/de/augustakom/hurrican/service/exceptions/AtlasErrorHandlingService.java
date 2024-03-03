/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2014
 */
package de.augustakom.hurrican.service.exceptions;

import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

/**
 * Error handling service of the ATLAS ESB.
 *
 *
 */
public interface AtlasErrorHandlingService {

    /**
     * Try to send the assigned error to the ATLAS ESB error-queue. If ATLAS won't  be reachable, the Error will be
     * stored at the T_EXCEPTION_LOG table over the {@link ExceptionLogService}.
     *
     * @param error {@link HandleError} object
     */
    void handleError(HandleError error);
}
