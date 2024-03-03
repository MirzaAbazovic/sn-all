/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2014
 */
package de.mnet.wita.route.processor.lineorder.in;

import org.apache.camel.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.SoapMessageFactory;

import de.mnet.common.route.ConvertCdmToHurricanFormatProcessor;
import de.mnet.common.service.SchemaValidationService;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.service.WitaSchemaValidationService;
import de.mnet.wita.unmarshal.v1.MessageUnmarshaller;

/**
 * Apache Camel Processor, um eingehende CDM v1 Meldungen in das MNET WITA Format zu konvertieren.
 */
@Component
public class ConvertCdmV1ToWitaFormatProcessor extends ConvertCdmToHurricanFormatProcessor {

    @Autowired
    @Qualifier("witaMessageUnmarshallerV1")
    private MessageUnmarshaller messageUnmarshallerV1;

    @Autowired
    @Qualifier("atlasEsbSoapMessageFactory")
    private SoapMessageFactory soapMessageFactory;

    @Autowired
    private WitaSchemaValidationService schemaValidationService;

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
        out.setBody(value, WitaMessage.class);
    }

}

