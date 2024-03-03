/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.10.13
 */
package de.mnet.common.errorhandling.processor;

import java.time.*;
import javax.jms.*;
import javax.xml.datatype.*;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.model.builder.cdm.errorhandling.v1.ErrorBuilder;
import de.augustakom.hurrican.model.builder.cdm.errorhandling.v1.HandleErrorBuilder;
import de.augustakom.hurrican.model.builder.cdm.errorhandling.v1.MessageBuilder;
import de.augustakom.hurrican.service.exceptions.helper.AtlasErrorMessageHelper;
import de.mnet.common.errorhandling.ErrorCode;
import de.mnet.common.exceptions.ServiceException;
import de.mnet.common.route.HurricanInProcessor;
import de.mnet.common.route.helper.ExceptionHelper;
import de.mnet.common.route.helper.ExchangeHelper;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

/**
 * This processor should only be used in the event of an error occurring within a camel route. The processor handles the
 * exception by extracting the exception and inbound message details from the exchange and generating an AtlasESB
 * ErrorMessage. This ErrorMessage can then be used for communicating all route errors back to AtlasESB.
 */
@Component
public class GenerateErrorMessageProcessor extends HurricanInProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateErrorMessageProcessor.class);

    @Autowired
    private ExchangeHelper exchangeHelper;

    @Autowired
    private ExceptionHelper exceptionHelper;

    @Override
    public void process(Exchange exchange) {
        HandleErrorBuilder handleErrorBuilder = new HandleErrorBuilder();
        try {
            handleErrorBuilder
                    .withTrackingId(getTrackingId(exchange))
                    .withMessage(getMessage(exchange))
                    .withComponent(getComponent(exchange))
                    .withError(getError(exchange))
                    .build();
        }
        catch (Exception e) {
            // All thrown exceptions *must* be caught here, since this processor is called within the OnException block
            // of the route. Any uncaught exceptions here would result in the JMS transaction being rolled back
            // which in turn would result in the Message being re-delivered, i.e. in an endless loop
            LOG.error("Exception thrown preparing Message for the ErrorhandlingService", e);
        }

        exceptionHelper.setErrorHandlingServiceMessage(exchange, handleErrorBuilder.build());
    }

    private String getTrackingId(Exchange exchange) throws JMSException {
        String trackingId = exchangeHelper.getOriginalEsbTrackingIdFromExchange(exchange);
        if (trackingId == null) {
            return "";
        }
        return trackingId;
    }

    private HandleError.Error getError(Exchange exchange) throws DatatypeConfigurationException {
        ErrorCode errorCode = getErrorCodeFromExchange(exchange);
        ErrorBuilder eb = new ErrorBuilder();
        eb.withCode(errorCode.getCode());
        eb.withMessage(errorCode.getMessage());
        eb.withErrorDetails(getExceptionStacktraceFromExchange(exchange));
        eb.withTime(LocalDateTime.now());
        return eb.build();
    }

    private HandleError.Component getComponent(Exchange exchange) throws JMSException {
        String soapAction = exchangeHelper.getOriginalSoapActionFromExchange(exchange);
        return AtlasErrorMessageHelper.buildComponent(soapAction, getProcessNameFromExchange(exchange));
    }

    private HandleError.Message getMessage(Exchange exchange) throws JMSException {
        // Filter out headers that are not relevant for the error handling service:
        // - JMS_TIBCO_SENDER is only set when using EMS for messaging. ActiveMQ does not set this JMS Header. This
        //   is filtered out since the Citrus payload validation fails when using ActiveMQ
        // - breadcrumbId is set by Camel and is therefore filtered out

        MessageBuilder mb = new MessageBuilder();
        mb.withPayload(exchangeHelper.getOriginalCdmPayloadFromInMessage(exchange));
        mb.withJMSEndpoint(exchangeHelper.getOriginalJmsEndpointFromExchange(exchange));
        mb.withJMSProperties(exchangeHelper.getOriginalInHeadersFromExchange(exchange, AtlasErrorMessageHelper.FILTER_OUT_JMS_HEADERS));
        addRetryInfo(exchange, mb);
        return mb.build();
    }

    private void addRetryInfo(Exchange exchange, MessageBuilder mb) throws JMSException {
        String esbErrorId = exchangeHelper.getOriginalEsbErrorIdFromExchange(exchange);
        String esbRetryCount = exchangeHelper.getOriginalEsbRetryCountFromExchange(exchange);

        // only add the retry info if error count and id are both present within the original exchange
        if (esbErrorId != null && esbRetryCount != null) {
            mb.withRetryInfo(esbErrorId, Integer.parseInt(esbRetryCount));
        }
    }

    private String getExceptionStacktraceFromExchange(Exchange exchange) {
        Throwable t = exceptionHelper.getExceptionFromExchange(exchange);
        return exceptionHelper.throwableToString(t);
    }

    private ErrorCode getErrorCodeFromExchange(Exchange exchange) {
        Throwable t = exceptionHelper.getExceptionFromExchange(exchange);

        if (t instanceof ServiceException) {
            return ((ServiceException) t).getErrorCode();
        }

        return ErrorCode.HUR_DEFAULT;
    }

    private String getProcessNameFromExchange(Exchange exchange) {
        Throwable t = exceptionHelper.getExceptionFromExchange(exchange);

        if (t instanceof ServiceException) {
            return ((ServiceException) t).getProcessName();
        }

        return "Unknown";
    }
}
