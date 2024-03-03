/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2014
 */
package de.mnet.wita.service.impl;

import java.io.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;

import de.mnet.common.errorhandling.ErrorCode;
import de.mnet.common.exceptions.ServiceException;
import de.mnet.common.service.impl.AbstractSchemaValidationService;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.marshal.MessageMarshallerDelegate;
import de.mnet.wita.service.WitaSchemaValidationService;

/**
 * Schema validation service implementation first of all finds matching schema definition from list of known schemas and
 * performs schema validation on message payload.
 */
public class WitaSchemaValidationServiceImpl extends AbstractSchemaValidationService implements WitaSchemaValidationService {

    @Autowired
    private MessageMarshallerDelegate messageMarshaller;
    
    @Autowired
    @Qualifier("atlasEsbSoapMessageFactory")
    private SoapMessageFactory soapMessageFactory;

    @Override
    public void validateWitaMessage(WitaMessage witaMessage, WitaCdmVersion witaCdmVersion) throws WitaBaseException {
        SoapMessage soapMessage = soapMessageFactory.createWebServiceMessage();
        try {
            messageMarshaller.marshal(witaMessage, soapMessage.getSoapBody().getPayloadResult(), witaCdmVersion);
            // validates message payload with schema definition
            validatePayload(soapMessage.getSoapBody().getPayloadSource());
        }
        catch (IOException e) {
            throw new WitaBaseException("Unable to marshall message", e);
        }
    }

    @Override
    protected ServiceException getSchemaValidationException(String errorMessage, Exception error) {
        return new WitaBaseException(errorMessage, error)
                .setErrorCode(ErrorCode.WITA_SCHEMA_VALIDATION);
    }
}
