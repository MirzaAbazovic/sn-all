/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.10.13
 */
package de.mnet.common.xml;

import java.io.*;
import java.util.*;
import javax.wsdl.*;
import javax.wsdl.factory.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import com.ibm.wsdl.extensions.schema.SchemaImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.xml.validation.XmlValidator;
import org.springframework.xml.validation.XmlValidatorFactory;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.mnet.common.exceptions.ServiceException;

/**
 * Xsd schema represents schema definitions inside a WSDL. Schema automatically locates all schema definitions inside
 * the WSDL so usual schema validation mechanism in Spring can use those.
 *
 *
 */
public class WsdlXsdSchema extends SimpleXsdSchema implements InitializingBean {

    /**
     * WSDL file resource
     */
    private Resource wsdl;

    /**
     * SOAP Envelope schema definition to use. Either SOAP 1.1 or 1.2.
     */
    private Resource soapSchema;

    /**
     * List of schemas that are loaded as single schema instance
     */
    private List<Resource> schemas = new ArrayList<Resource>();

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WsdlXsdSchema.class);

    /**
     * Official xmlns namespace
     */
    private static final String WWW_W3_ORG_2000_XMLNS = "http://www.w3.org/2000/xmlns/";
    public static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";

    /**
     * Default constructor
     */
    public WsdlXsdSchema() {
        super();
    }

    /**
     * Constructor using wsdl resource.
     *
     * @param wsdl
     */
    public WsdlXsdSchema(Resource wsdl) {
        super();
        this.wsdl = wsdl;
    }

    @Override
    public XmlValidator createValidator() {
        try {
            return XmlValidatorFactory.createValidator(schemas.toArray(new Resource[schemas.size()]), W3C_XML_SCHEMA_NS_URI);
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Loads nested schema type definitions from wsdl.
     *
     * @throws IOException
     * @throws javax.wsdl.WSDLException
     * @throws javax.xml.transform.TransformerFactoryConfigurationError
     * @throws javax.xml.transform.TransformerException
     * @throws javax.xml.transform.TransformerConfigurationException
     */
    private void loadSchemas() throws WSDLException, IOException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
        Definition definition = WSDLFactory.newInstance().newWSDLReader().readWSDL(wsdl.getURI().getPath(), new InputSource(wsdl.getInputStream()));

        Types types = definition.getTypes();
        List<?> schemaTypes = types.getExtensibilityElements();

        if (soapSchema != null) {
            schemas.add(soapSchema);
        }

        for (Object schemaObject : schemaTypes) {
            if (schemaObject instanceof SchemaImpl) {
                SchemaImpl schema = (SchemaImpl) schemaObject;

                inheritNamespaces(schema, definition);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Source source = new DOMSource(schema.getElement());
                Result result = new StreamResult(bos);

                TransformerFactory.newInstance().newTransformer().transform(source, result);
                Resource schemaResource = new ByteArrayResource(bos.toByteArray());

                schemas.add(schemaResource);

                if (definition.getTargetNamespace().equals(schema.getElement().getAttribute("targetNamespace"))) {
                    setXsd(schemaResource);
                }
            }
            else {
                LOGGER.warn("Found unsupported schema type implementation " + schemaObject.getClass());
            }
        }
    }

    /**
     * Adds WSDL level namespaces to schema definition if necessary.
     *
     * @param schema
     * @param wsdl
     */
    @SuppressWarnings("unchecked")
    private void inheritNamespaces(SchemaImpl schema, Definition wsdl) {
        Map<String, String> wsdlNamespaces = wsdl.getNamespaces();

        for (Map.Entry<String, String> nsEntry : wsdlNamespaces.entrySet()) {
            if (StringUtils.hasText(nsEntry.getKey())) {
                if (!schema.getElement().hasAttributeNS(WWW_W3_ORG_2000_XMLNS, nsEntry.getKey())) {
                    schema.getElement().setAttributeNS(WWW_W3_ORG_2000_XMLNS, "xmlns:" + nsEntry.getKey(), nsEntry.getValue());
                }
            }
            else { // handle default namespace
                if (!schema.getElement().hasAttribute("xmlns")) {
                    schema.getElement().setAttributeNS(WWW_W3_ORG_2000_XMLNS, "xmlns" + nsEntry.getKey(), nsEntry.getValue());
                }
            }

        }
    }

    @Override
    public void afterPropertiesSet() throws ParserConfigurationException, IOException, SAXException {
        Assert.notNull(wsdl, "wsdl file resource is required");
        Assert.isTrue(wsdl.exists(), "wsdl file resource '" + wsdl + " does not exist");

        try {
            loadSchemas();
        }
        catch (Exception e) {
            throw new BeanCreationException("Failed to load schema types from WSDL file", e);
        }

        Assert.isTrue(!schemas.isEmpty(), "no schema types found in wsdl file resource");

        super.afterPropertiesSet();
    }

    /**
     * Sets the wsdl.
     *
     * @param wsdl the wsdl to set
     */
    public void setWsdl(Resource wsdl) {
        this.wsdl = wsdl;
    }

    /**
     * The SOAP envelope schema.
     * @param soapSchema
     */
    public void setSoapSchema(Resource soapSchema) {
        this.soapSchema = soapSchema;
    }

    /**
     * Gets the schemas.
     *
     * @return the schemas the schemas to get.
     */
    public List<Resource> getSchemas() {
        return schemas;
    }

    public String toString() {
        return String.format("WsdlXsdSchema { wsdl:%s }", wsdl.getFilename());
    }
}
