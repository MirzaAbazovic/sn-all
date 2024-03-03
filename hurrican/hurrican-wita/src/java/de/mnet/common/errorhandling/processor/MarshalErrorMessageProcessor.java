/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.13
 */
package de.mnet.common.errorhandling.processor;

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
import de.mnet.common.errorhandling.marshal.ErrorHandlingServiceMarshaller;
import de.mnet.common.errorhandling.service.ErrorHandlingSchemaValidationService;
import de.mnet.common.exceptions.MessageProcessingException;
import de.mnet.common.exceptions.ServiceException;
import de.mnet.common.route.HurricanInProcessor;
import de.mnet.common.route.helper.ExceptionHelper;
import de.mnet.common.tools.XmlPrettyFormatter;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

@Component
public class MarshalErrorMessageProcessor extends HurricanInProcessor {
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MarshalErrorMessageProcessor.class);

    @Autowired
    private ExceptionHelper exceptionHelper;

    @Autowired
    private ErrorHandlingServiceMarshaller errorHandlingServiceMarshaller;

    @Autowired
    private ErrorHandlingSchemaValidationService schemaValidationService;

    @Autowired
    private ExceptionLogService exceptionLogService;

    @Autowired
    @Qualifier("atlasEsbSoapMessageFactory")
    private SoapMessageFactory soapMessageFactory;

    @Value("${atlas.errorhandlingservice.handleError}")
    private String soapAction;

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            HandleError handleError = (HandleError) exceptionHelper.getErrorHandlingServiceMessage(exchange);

            LOG.debug(String.format("Marshalling AtlasESB ErrorHandlingService message: %s", handleError));

            Message out = exchange.getOut();

            SoapMessage soapMessage = soapMessageFactory.createWebServiceMessage();
            errorHandlingServiceMarshaller.marshal(handleError, soapMessage.getSoapBody().getPayloadResult());

            if (isPayloadValid(soapMessage)) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                soapMessage.writeTo(bos);

                String soapXml = bos.toString(System.getProperty("file.encoding", "UTF-8"));
                out.setBody(XmlPrettyFormatter.prettyFormat(soapXml));

                out.setHeader(AtlasEsbConstants.SOAPACTION_TIBCO, soapAction);
                out.setHeader(AtlasEsbConstants.HUR_ERROR_CODE, (handleError.getError() == null ? null : handleError.getError().getCode()));
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
        catch (ServiceException e) {
            exceptionLogService.saveExceptionLogEntry(new ExceptionLogEntry(ATLAS_ERROR_SERVICE_ERROR,
                    "Error marshalling the error service request!", e));

            return false;
        }
    }

}
