/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.14
 */
package de.mnet.hurrican.simulator.helper;

import java.io.*;
import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import org.w3c.dom.Document;

/**
 *
 */
public class SoapMessageHelper {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(SoapMessageHelper.class);

    @Autowired
    private SoapMessageFactory soapMessageFactory;

    /** Transformer */
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();

    /**
     * Method reads SOAP body element from SOAP Envelope and transforms body payload to String.
     *
     * @param request
     * @return
     * @throws SOAPException
     * @throws IOException
     * @throws TransformerException
     */
    public String getSoapBody(Message request) throws SOAPException, IOException, TransformerException {
        MessageFactory msgFactory = MessageFactory.newInstance();
        MimeHeaders mimeHeaders = new MimeHeaders();
        mimeHeaders.addHeader("Content-Type", "text/xml; charset=UTF-8");

        SOAPMessage message = msgFactory.createMessage(mimeHeaders, new ByteArrayInputStream(request.getPayload().toString().getBytes(System.getProperty("citrus.file.encoding", "UTF-8"))));
        SOAPBody soapBody = message.getSOAPBody();

        Document body = soapBody.extractContentAsDocument();

        StringResult result = new StringResult();
        transformerFactory.newTransformer().transform(new DOMSource(body), result);

        return result.toString();
    }

    /**
     * Creates a new SOAP message representation from given payload resource. Constructs a SOAP envelope
     * with empty header and payload as body.
     *
     * @param message
     * @return
     * @throws IOException
     */
    public Message createSoapMessage(Message message) {
        try {
            String payload = message.getPayload().toString();

            LOG.info("Creating SOAP message from payload: " + payload);

            SoapMessage soapMessage = soapMessageFactory.createWebServiceMessage();
            transformerFactory.newTransformer().transform(
                    new StringSource(payload), soapMessage.getPayloadResult());

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            soapMessage.writeTo(bos);

            message.setPayload(new String(bos.toByteArray()));
            return message;
        } catch (Exception e) {
            throw new CitrusRuntimeException("Failed to create SOAP message from payload resource", e);
        }
    }
}
