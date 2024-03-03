/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.wbci.route.processor.location;

import static de.augustakom.common.tools.exceptions.ExceptionLogEntryContext.*;

import java.io.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;

import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;
import de.mnet.common.route.HurricanOutProcessor;
import de.mnet.esb.cdm.resource.locationservice.v1.ObjectFactory;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchBuildingsResponse;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchResponse;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.service.WbciSchemaValidationService;
import de.mnet.wbci.unmarshal.location.LocationServiceUnmarshaller;

/**
 *
 */
@Component
public class UnmarshalLocationServiceProcessor extends HurricanOutProcessor {

    @Autowired
    private LocationServiceUnmarshaller locationServiceUnmarshaller;

    @Autowired
    private WbciSchemaValidationService schemaValidationService;

    @Autowired
    private ExceptionLogService exceptionLogService;

    @Autowired
    @Qualifier("atlasEsbSoapMessageFactory")
    private SoapMessageFactory soapMessageFactory;

    @Override
    public void process(Exchange exchange) throws Exception {
        Object body = exchange.getIn().getBody();
        InputStream soapInputStream = new ByteArrayInputStream(body.toString().getBytes("UTF-8"));
        SoapMessage soapMessage = soapMessageFactory.createWebServiceMessage(soapInputStream);

        Message out = exchange.getOut();
        out.copyFrom(exchange.getIn());

        if (isPayloadValid(soapMessage)) {
            SearchResponse searchResponse = locationServiceUnmarshaller.unmarshal(soapMessage.getPayloadSource());

            // das von CDM ins Hurrican Format konvertierte Objekt im Exchange-Out speichern
            out.setBody(searchResponse, SearchResponse.class);
        }
        else {
            // leeres Response-Objekt zurueck geben
            out.setBody(new ObjectFactory().createSearchBuildingsResponse(), SearchBuildingsResponse.class);
        }
    }

    public boolean isPayloadValid(SoapMessage soapMessage) {
        try {
            // validates message payload with schema definition
            schemaValidationService.validatePayload(soapMessage.getSoapBody().getPayloadSource());
            return true;
        }
        catch (WbciServiceException e) {
            exceptionLogService.saveExceptionLogEntry(new ExceptionLogEntry(WBCI_LOCATION_SEARCH_RESPONSE_ERROR,
                    "Error unmarshalling the location service response!", e));

            return false;
        }
    }

}
