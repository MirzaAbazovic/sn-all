/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.10.13
 */
package de.mnet.wbci.service.impl;

import java.io.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;

import de.mnet.common.errorhandling.ErrorCode;
import de.mnet.common.exceptions.ServiceException;
import de.mnet.common.service.impl.AbstractSchemaValidationService;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.marshal.MessageMarshallerDelegate;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.service.WbciSchemaValidationService;

/**
 * Schema validation service implementation first of all finds matching schema definition from list of known schemas and
 * performs schema validation on message payload.
 *
 *
 */
public class WbciSchemaValidationServiceImpl extends AbstractSchemaValidationService implements WbciSchemaValidationService {

    @Autowired
    private MessageMarshallerDelegate messageMarshaller;

    @Autowired
    @Qualifier("atlasEsbSoapMessageFactory")
    private SoapMessageFactory soapMessageFactory;

    @Override
    public void validateWbciMessage(WbciMessage wbciMessage, WbciCdmVersion wbciCdmVersion) throws WbciServiceException {
        SoapMessage soapMessage = soapMessageFactory.createWebServiceMessage();
        try {
            messageMarshaller.marshal(wbciMessage, soapMessage.getSoapBody().getPayloadResult(), wbciCdmVersion);
            // validates message payload with schema definition
            validatePayload(soapMessage.getSoapBody().getPayloadSource());
        }
        catch (IOException e) {
            throw new WbciServiceException("Unable to marshall message", e);
        }
    }

    @Override
    protected ServiceException getSchemaValidationException(String errorMessage, Exception error) {
        return new WbciServiceException(errorMessage, error)
                .setErrorCode(ErrorCode.WBCI_SCHEMA_VALIDATION);
    }
}
