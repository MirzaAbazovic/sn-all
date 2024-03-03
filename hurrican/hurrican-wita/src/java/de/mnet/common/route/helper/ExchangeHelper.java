/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.13
 */
package de.mnet.common.route.helper;

import static de.augustakom.hurrican.service.exceptions.helper.AtlasErrorMessageHelper.*;

import java.io.*;
import java.util.*;
import javax.jms.*;
import javax.jms.Queue;
import javax.xml.transform.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.bean.BeanInvocation;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;

import de.mnet.common.exceptions.MessageProcessingException;
import de.mnet.common.webservice.tools.AtlasEsbConstants;

/**
 * Hilfsklasse fuer Operationen auf dem Camel {@link Exchange} Objekt
 */
@Component
public class ExchangeHelper {

    public static final String OPTION_WRITE_TO_IO_ARCHIVE = "OPTION_WRITE_TO_IO_ARCHIVE";

    /**
     * Ermittelt aus dem {@link Exchange} Objekt die {@link org.apache.camel.spi.UnitOfWork} und daraus die original
     * Message. Unterstützt auch Camel proxy Aufrufe mit entsprechender {@link org.apache.camel.component.bean.BeanInvocation}
     * Instanz.
     *
     * @param exchange
     * @return
     */
    public <T> T getOriginalMessageFromOutMessage(Exchange exchange) {
        Message message = exchange.getUnitOfWork().getOriginalInMessage();
        Object body = message.getBody();

        try {
            if (body instanceof BeanInvocation) {
                BeanInvocation beanInvocation = ((BeanInvocation) body);

                Assert.notEmpty(beanInvocation.getMethod().getParameterTypes());
                Assert.notEmpty(beanInvocation.getArgs());

                return (T) beanInvocation.getArgs()[0];
            }
            else {
                return (T) body;
            }
        }
        catch (ClassCastException e) {
            throw new MessageProcessingException("Failed to get original message from camel exchange", e);
        }
    }

    /**
     * Returns the exchange options, if any found. Options are only present in exchanges that have been started with a
     * camel proxy and that have been passed a {@link Map} containing options - for example: {@link
     * de.mnet.wbci.integration.CarrierNegotationService#sendToWbci(de.mnet.wbci.model.WbciMessage, java.util.Map)}
     *
     * @param exchange
     * @return the options or an empty map if none exist
     */
    public Map<String, Object> getExchangeOptions(Exchange exchange) {
        Message message = exchange.getUnitOfWork().getOriginalInMessage();
        Object body = message.getBody();

        if (body instanceof BeanInvocation) {
            BeanInvocation beanInvocation = ((BeanInvocation) body);

            Assert.notEmpty(beanInvocation.getMethod().getParameterTypes());
            Assert.notEmpty(beanInvocation.getArgs());

            if (beanInvocation.getArgs().length >= 2 && beanInvocation.getArgs()[1] instanceof Map) {
                return (Map) beanInvocation.getArgs()[1];
            }
        }
        return Collections.emptyMap();
    }

    /**
     * see {@link #getOriginalMessageFromOutMessage(org.apache.camel.Exchange)}
     *
     * @param exchange
     * @return
     */
    public <T> T getOriginalMessageFromInMessage(Exchange exchange) {
        try {
            return (T) exchange.getIn().getBody();
        }
        catch (ClassCastException e) {
            throw new MessageProcessingException("Failed to get original message from camel exchange", e);
        }
    }

    /**
     * This is typically used for extracting the original Soap / XML Request from an exchange, which was triggered by an
     * inbound CDM SOAP message.
     *
     * @param exchange
     * @return the original CDM payload
     */
    public String getOriginalCdmPayloadFromInMessage(Exchange exchange) {
        Object body = getOriginalInMessageFromExchange(exchange).getBody();
        if (body == null) {
            return null;
        }
        return body.toString();
    }

    /**
     * Extracts the original in message from the exchange, using the exchanges unit of work.
     *
     * @param exchange
     * @return
     */
    private Message getOriginalInMessageFromExchange(Exchange exchange) {
        return exchange.getUnitOfWork().getOriginalInMessage();
    }

    /**
     * Returns all in headers in the original In-Message as a List of Key/Value pairs. Non-string values are converted
     * to their string equivalent.
     *
     * @param exchange
     * @return
     * @throws JMSException
     */
    public List<String[]> getOriginalInHeadersFromExchange(Exchange exchange) throws JMSException {
        return getOriginalInHeadersFromExchange(exchange, null);
    }

    /**
     * Returns all in headers in the original In-Message as a List of Key/Value pairs. Non-string values are converted
     * to their string equivalent. A list of headers that should be filtered out from the returned list can be supplied
     * using the {@code filterOutHeaders} parameter.
     *
     * @param exchange
     * @param filterOutHeaders Headers to filter (can be regexp)
     * @return
     * @throws JMSException
     */
    public List<String[]> getOriginalInHeadersFromExchange(Exchange exchange, String[] filterOutHeaders) throws JMSException {
        Message message = getOriginalInMessageFromExchange(exchange);

        List<String[]> jmsProperties = new ArrayList<>();

        // sort the headers alphabetically (This makes it easier to validate the error message later on with Citrus)
        Map<String, Object> sortedHeaders = new TreeMap<>(message.getHeaders());
        for (Map.Entry<String, Object> entry : sortedHeaders.entrySet()) {
            String key = entry.getKey();
            if (!isValueInList(filterOutHeaders, key)) {
                String[] keyValue = { key, getHeaderPropertyValueAsString(entry.getValue()) };
                jmsProperties.add(keyValue);
            }
        }
        return jmsProperties;
    }

    public String getHeaderPropertyValueAsString(Object value) throws JMSException {
        if (value == null) {
            return null;
        }
        else if (value instanceof Queue) {
            return ((Queue) value).getQueueName();
        }
        else {
            return value.toString();
        }
    }

    /**
     * Using the header key {@link AtlasEsbConstants#JMS_DESTINATION_KEY} returns the JMS destination if found within the original in
     * message.
     *
     * @param exchange
     * @return
     * @throws JMSException
     */
    public String getOriginalJmsEndpointFromExchange(Exchange exchange) throws JMSException {
        Message message = getOriginalInMessageFromExchange(exchange);
        Object value = message.getHeader(AtlasEsbConstants.JMS_DESTINATION_KEY);
        return getHeaderPropertyValueAsString(value);
    }

    /**
     * Using the header key {@link AtlasEsbConstants#ESB_TRACKING_ID_KEY} returns the ESB tracking id if found within the original in
     * message.
     *
     * @param exchange
     * @return
     * @throws JMSException
     */
    public String getOriginalEsbTrackingIdFromExchange(Exchange exchange) throws JMSException {
        Message message = getOriginalInMessageFromExchange(exchange);
        Object value = message.getHeader(AtlasEsbConstants.ESB_TRACKING_ID_KEY);
        return getHeaderPropertyValueAsString(value);
    }

    /**
     * Using the header key {@link AtlasEsbConstants#ESB_ERROR_ID_KEY} returns the ESB error id if found within the original in message.
     *
     * @param exchange
     * @return
     * @throws JMSException
     */
    public String getOriginalEsbErrorIdFromExchange(Exchange exchange) throws JMSException {
        Message message = getOriginalInMessageFromExchange(exchange);
        Object value = message.getHeader(AtlasEsbConstants.ESB_ERROR_ID_KEY);
        return getHeaderPropertyValueAsString(value);
    }

    /**
     * Using the header key {@link AtlasEsbConstants#ESB_RETRY_COUNT_KEY} returns the ESB error count if found within the original in
     * message.
     *
     * @param exchange
     * @return
     * @throws JMSException
     */
    public String getOriginalEsbRetryCountFromExchange(Exchange exchange) throws JMSException {
        Message message = getOriginalInMessageFromExchange(exchange);
        Object value = message.getHeader(AtlasEsbConstants.ESB_RETRY_COUNT_KEY);
        return getHeaderPropertyValueAsString(value);
    }

    /**
     * Using the header key {@link AtlasEsbConstants#SOAPACTION_TIBCO} returns the soap action if found within the original in message.
     *
     * @param exchange
     * @return
     * @throws JMSException
     */
    public String getOriginalSoapActionFromExchange(Exchange exchange) throws JMSException {
        Message message = getOriginalInMessageFromExchange(exchange);
        Object value = message.getHeader(AtlasEsbConstants.SOAPACTION_TIBCO);
        return getHeaderPropertyValueAsString(value);
    }

    public Source getSoapPayloadFromExchange(Exchange exchange, SoapMessageFactory soapMessageFactory) throws IOException {
        Object body = exchange.getIn().getBody();
        InputStream soapInputStream = new ByteArrayInputStream(body.toString().getBytes("UTF-8"));
        SoapMessage soapMessage = soapMessageFactory.createWebServiceMessage(soapInputStream);
        return soapMessage.getPayloadSource();
    }

}
