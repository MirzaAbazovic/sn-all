/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.common.customer.route.processor;

import java.io.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;

import de.mnet.common.customer.marshal.CustomerServiceMarshaller;
import de.mnet.common.customer.service.CustomerSchemaValidationService;
import de.mnet.common.exceptions.MessageProcessingException;
import de.mnet.common.route.helper.ExchangeHelper;
import de.mnet.common.tools.XmlPrettyFormatter;
import de.mnet.common.webservice.tools.AtlasEsbConstants;

@Component
public class MarshalCustomerServiceMessageProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(MarshalCustomerServiceMessageProcessor.class);

    @Autowired
    private CustomerServiceMarshaller customerServiceMarshaller;

    @Autowired
    private ExchangeHelper exchangeHelper;

    @Autowired
    @Qualifier("atlasEsbSoapMessageFactory")
    private SoapMessageFactory soapMessageFactory;

    @Autowired
    private CustomerSchemaValidationService schemaValidationService;

    @Value("${atlas.customerservice.addCommunication}")
    private String soapAction;

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            Object csMessage = exchangeHelper.getOriginalMessageFromOutMessage(exchange);

            LOG.debug(String.format("Marshalling CustomerService message: %s", csMessage));

            Message out = exchange.getOut();

            SoapMessage soapMessage = soapMessageFactory.createWebServiceMessage();
            customerServiceMarshaller.marshal(csMessage, soapMessage.getSoapBody().getPayloadResult());

            // validates message payload with schema definition
            schemaValidationService.validatePayload(soapMessage.getSoapBody().getPayloadSource());

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            soapMessage.writeTo(bos);

            String soapXml = bos.toString(System.getProperty("file.encoding", "UTF-8"));
            out.setBody(XmlPrettyFormatter.prettyFormat(soapXml));

            out.setHeader(AtlasEsbConstants.SOAPACTION_TIBCO, soapAction);
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new MessageProcessingException(e);
        }
    }

}
