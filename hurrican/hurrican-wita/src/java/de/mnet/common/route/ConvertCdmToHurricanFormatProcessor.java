/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2014
 */
package de.mnet.common.route;

import javax.xml.transform.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ws.soap.SoapMessageFactory;

import de.mnet.common.route.helper.ExchangeHelper;
import de.mnet.common.service.SchemaValidationService;

/**
 * Apache Camel Processor, um eingehende CDM Meldungen in das Hurrican Format zu konvertieren.
 */
public abstract class ConvertCdmToHurricanFormatProcessor extends HurricanInProcessor {
    @Autowired
    protected ExchangeHelper exchangeHelper;

    @Override
    public void process(Exchange exchange) throws Exception {
        Source payload = exchangeHelper.getSoapPayloadFromExchange(exchange, getSoapMessageFactory());

        // validate with schema definition
        getSchemaValidationService().validatePayload(payload);

        Object incomingConvertedCdmMessage = getUnmarshaller().unmarshal(payload);

        // das von CDM ins Hurrican Format konvertierte Objekt im Exchange-Out speichern
        Message out = exchange.getOut();
        out.copyFrom(exchange.getIn());
        setBody(out, incomingConvertedCdmMessage);
    }

    public abstract Unmarshaller getUnmarshaller();

    public abstract SoapMessageFactory getSoapMessageFactory();

    public abstract SchemaValidationService getSchemaValidationService();

    public abstract void setBody(Message out, Object value);
}
