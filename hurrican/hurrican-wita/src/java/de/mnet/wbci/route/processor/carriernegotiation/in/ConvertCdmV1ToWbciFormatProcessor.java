/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.13
 */
package de.mnet.wbci.route.processor.carriernegotiation.in;

import org.apache.camel.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.SoapMessageFactory;

import de.mnet.common.route.ConvertCdmToHurricanFormatProcessor;
import de.mnet.common.service.SchemaValidationService;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.service.WbciSchemaValidationService;

/**
 * Apache Camel Processor, um eingehende CDM v1 Messages (Meldungen und Requests) in das Hurrican WBCI Format zu
 * konvertieren.
 */
@Component
public class ConvertCdmV1ToWbciFormatProcessor extends ConvertCdmToHurricanFormatProcessor {

    @Autowired
    private de.mnet.wbci.unmarshal.v1.MessageUnmarshaller messageUnmarshallerV1;

    @Autowired
    @Qualifier("atlasEsbSoapMessageFactory")
    private SoapMessageFactory soapMessageFactory;

    @Autowired
    private WbciSchemaValidationService schemaValidationService;

    @Override
    public Unmarshaller getUnmarshaller() {
        return messageUnmarshallerV1;
    }

    @Override
    public SoapMessageFactory getSoapMessageFactory() {
        return soapMessageFactory;
    }

    @Override
    public SchemaValidationService getSchemaValidationService() {
        return schemaValidationService;
    }

    @Override
    public void setBody(Message out, Object value) {
        out.setBody(value, WbciMessage.class);
    }

}
