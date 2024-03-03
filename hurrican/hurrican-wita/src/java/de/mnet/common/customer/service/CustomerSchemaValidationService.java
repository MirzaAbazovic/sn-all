/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.10.14
 */
package de.mnet.common.customer.service;

import de.mnet.common.exceptions.ServiceException;
import de.mnet.common.service.impl.AbstractSchemaValidationService;

/**
 *
 */
public class CustomerSchemaValidationService extends AbstractSchemaValidationService {

    @Override
    protected ServiceException getSchemaValidationException(String errorMessage, Exception error) {
        return new ServiceException(errorMessage, error);
    }
}
