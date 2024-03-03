/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.10.14
 */
package de.mnet.common.service;

import javax.xml.transform.*;

import de.augustakom.hurrican.service.cc.ICCService;
import de.mnet.common.exceptions.ServiceException;

/**
 *
 */
public interface SchemaValidationService extends ICCService {

    /**
     * Verifies that the SOAP payload is valid with respect to the CDM schema specification
     *
     * @param payloadSource
     * @throws ServiceException
     */
    void validatePayload(Source payloadSource) throws ServiceException;
}
