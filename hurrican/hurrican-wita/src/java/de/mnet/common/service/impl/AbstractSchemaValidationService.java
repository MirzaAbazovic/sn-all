/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.10.14
 */
package de.mnet.common.service.impl;

import java.io.*;
import java.util.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import org.apache.log4j.Logger;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.xml.xsd.XsdSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;

import de.mnet.common.exceptions.ServiceException;
import de.mnet.common.service.SchemaValidationService;

/**
 * Abstract schema validation service implementation. Holds list of schema resources that one of should
 * match when validating a message payload.
 *
 *
 */
public abstract class AbstractSchemaValidationService implements SchemaValidationService {

    private final Logger LOGGER = Logger.getLogger(getClass()); // NOSONAR squid:S1312

    /**
     * List of available schema definitions known to this validation service
     */
    private List<XsdSchema> schemas = new ArrayList<>();

    @Override
    public void validatePayload(Source payloadSource) throws ServiceException {
        XsdSchema schema = findSchema(payloadSource);

        try {
            SAXParseException[] validationErrors = schema.createValidator().validate(payloadSource);
            if (!ObjectUtils.isEmpty(validationErrors)) {
                for (SAXParseException error : validationErrors) {
                    LOGGER.warn("Schema validation error: " + error.getMessage());
                }
                throw getSchemaValidationException("Schema validation failed!", validationErrors[0]);
            }
        }
        catch (IOException e) {
            throw getSchemaValidationException("Unable to load schema validator", e);
        }
    }

    /**
     * Finds matching schema definition in list of known schemas based on the target namespace uri. Message payload root
     * element namespace is used as target namespace.
     *
     * @param payloadSource
     * @return
     */
    private XsdSchema findSchema(Source payloadSource) {
        String rootNamespace = getRootNamespace(payloadSource);

        for (XsdSchema schema : schemas) {
            if (StringUtils.hasText(schema.getTargetNamespace()) &&
                    schema.getTargetNamespace().equals(rootNamespace)) {
                return schema;
            }
        }

        throw getSchemaValidationException("Unable to locate schema definition for message payload", null);
    }

    /**
     * Constructs new validation exception. Subclasses must use their own exception implementation.
     * @param errorMessage
     * @param error
     * @return
     */
    protected abstract ServiceException getSchemaValidationException(String errorMessage, Exception error);

    /**
     * Gets the namespace uri of payload source root element. At the moment only supports DOMSource types which is the
     * default source used in Camel and Saaj.
     *
     * @param payloadSource
     * @return
     */
    private String getRootNamespace(Source payloadSource) {
        if (payloadSource instanceof DOMSource) {
            Node rootNode = ((DOMSource) payloadSource).getNode();

            if (rootNode instanceof Document) {
                return rootNode.getFirstChild().getNamespaceURI();
            }
            else {
                return rootNode.getNamespaceURI();
            }
        }
        else {
            throw new ServiceException("Unsupported payload source type: " + payloadSource.getClass());
        }
    }

    public void setSchemas(List<XsdSchema> schemas) {
        this.schemas = schemas;
    }
}
