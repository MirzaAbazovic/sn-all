/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.13
 */
package de.mnet.wbci.route.processor.location;

import static de.augustakom.common.tools.exceptions.ExceptionLogEntryContext.*;

import java.io.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;

import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;
import de.mnet.common.exceptions.MessageProcessingException;
import de.mnet.common.route.HurricanOutProcessor;
import de.mnet.common.tools.XmlPrettyFormatter;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchBuildings;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchRequest;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.marshal.location.LocationServiceMarshaller;
import de.mnet.wbci.service.WbciSchemaValidationService;

/**
 *
 */
@Component
public class MarshalLocationServiceProcessor extends HurricanOutProcessor {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MarshalLocationServiceProcessor.class);

    @Autowired
    private LocationServiceMarshaller locationServiceMarshaller;

    @Autowired
    private WbciSchemaValidationService schemaValidationService;

    @Autowired
    private ExceptionLogService exceptionLogService;

    @Autowired
    @Qualifier("atlasEsbSoapMessageFactory")
    private SoapMessageFactory soapMessageFactory;

    @Value("${atlas.locationservice.searchBuilding}")
    private String soapActionSearchBuilding;

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            SearchRequest searchRequest = getOriginalMessage(exchange);

            LOG.debug(String.format("Marshalling AtlasESB LocationService message: %s", searchRequest));

            Message out = exchange.getOut();

            SoapMessage soapMessage = soapMessageFactory.createWebServiceMessage();
            locationServiceMarshaller.marshal(searchRequest, soapMessage.getSoapBody().getPayloadResult());

            if (isPayloadValid(soapMessage)) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                soapMessage.writeTo(bos);

                String soapXml = bos.toString(System.getProperty("file.encoding", "UTF-8"));
                out.setBody(XmlPrettyFormatter.prettyFormat(soapXml));

                out.setHeader(AtlasEsbConstants.SOAPACTION_TIBCO, getSoapAction(searchRequest));
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new MessageProcessingException(e);
        }
    }

    public boolean isPayloadValid(SoapMessage soapMessage) {
        try {
            // validates message payload with schema definition
            schemaValidationService.validatePayload(soapMessage.getSoapBody().getPayloadSource());
            return true;
        }
        catch (WbciServiceException e) {
            exceptionLogService.saveExceptionLogEntry(new ExceptionLogEntry(WBCI_LOCATION_SEARCH_REQUEST_ERROR,
                    "Error marshalling the location service request!", e));

            return false;
        }
    }

    private String getSoapAction(SearchRequest searchRequest) {
        if (searchRequest instanceof SearchBuildings) {
            return soapActionSearchBuilding;
        }
        else {
            throw new MessageProcessingException("Failed to evaluate SOAP action for location search request: " + searchRequest);
        }
    }

    public void setSoapActionSearchBuilding(String soapActionSearchBuilding) {
        this.soapActionSearchBuilding = soapActionSearchBuilding;
    }
}
