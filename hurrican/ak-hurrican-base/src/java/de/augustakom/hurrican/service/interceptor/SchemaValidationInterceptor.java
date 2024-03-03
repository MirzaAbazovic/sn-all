/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.14
 */
package de.augustakom.hurrican.service.interceptor;

import java.io.*;
import javax.xml.transform.*;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedWriter;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.xml.namespace.QNameUtils;
import org.springframework.xml.transform.StringSource;
import org.springframework.xml.xsd.XsdSchema;
import org.xml.sax.SAXParseException;

import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.service.exceptions.AtlasErrorHandlingService;
import de.augustakom.hurrican.service.exceptions.helper.HandleErrorFactory;

/**
 * Apache CXF soap message interceptor implementation. Performs schema validation on incoming message
 * and calls Atlas ESB error handling in case of validation errors. Fills Atlas specific error information such as
 * tracking id, component and process name.
 *
 * Interceptor can be added to the JaxWS endpoint definition as inbound interceptor.
 *
 */
public class SchemaValidationInterceptor extends AbstractSoapInterceptor {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaValidationInterceptor.class);

    /** Xsd or wsdl schema to validate against */
    private XsdSchema schema;

    /** Atlas ESB error information */
    private String errorCode = "HUR-TECH-1000";
    private String processName = SchemaValidationInterceptor.class.getName();

    @Autowired
    private AtlasErrorHandlingService errorHandlingService;

    public SchemaValidationInterceptor() {
        super(Phase.RECEIVE);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        SAXParseException validationError;

        try {
            validationError = validatePayload(message);

            if (validationError != null) {
                errorHandlingService.handleError(new HandleErrorFactory(message).create(validationError, errorCode, processName));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to call ATLAS ESB error handling service!", e);
            throw new SoapFault("Failed to call ATLAS ESB error handling service!",
                    QNameUtils.createQName("http://www.mnet.de/esb/cdm/Resource/WorkforceNotificationService/v1", e.getMessage(), "FFM"));
        }

        if (validationError != null) {
            // break the phase interceptor chain and all further processing
            throw new SoapFault("Schema validation failed for incoming message",
                    QNameUtils.createQName("http://www.mnet.de/esb/cdm/Resource/WorkforceNotificationService/v1", validationError.getMessage(), "FFM"));
        }
    }

    /**
     * Perform schema validation on message payload.
     * @param message
     * @return
     */
    public SAXParseException validatePayload(SoapMessage message) {
        try {
            Source payloadSource;
            Reader reader = message.getContent(Reader.class);
            if (reader != null) {
                CachedWriter writer = new CachedWriter();
                IOUtils.copyAndCloseInput(reader, writer);
                message.setContent(Reader.class, writer.getReader());

                StringBuilder payloadCache = new StringBuilder();
                writer.writeCacheTo(payloadCache);
                payloadSource = new StringSource(payloadCache.toString());
            } else {
                throw new FFMServiceException("Unable to access message content");
            }

            SAXParseException[] validationErrors = schema.createValidator().validate(payloadSource);
            if (!ObjectUtils.isEmpty(validationErrors)) {
                for (SAXParseException error : validationErrors) {
                    LOGGER.warn("Schema validation error: " + error.getMessage());
                }

                return validationErrors[0];
            }
        }
        catch (IOException e) {
            throw new FFMServiceException("Unable to load schema validator", e);
        }

        return null;
    }

    public void setSchema(XsdSchema schema) {
        this.schema = schema;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}
