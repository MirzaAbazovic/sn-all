/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.10.13
 */
package de.mnet.wbci.service;

import de.mnet.common.service.SchemaValidationService;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciMessage;

/**
 *
 */
public interface WbciSchemaValidationService extends SchemaValidationService, WbciService {

    /**
     * Check if the request is valid, outside of a Camel-Route.
     *
     * @param wbciMessage any type of {@link WbciMessage}s
     * @param wbciCdmVersion  specifies the based {@link WbciCdmVersion} for the validation
     * @throws WbciServiceException if the wbciMessage is not valid
     */
    void validateWbciMessage(WbciMessage wbciMessage, WbciCdmVersion wbciCdmVersion) throws WbciServiceException;
}
