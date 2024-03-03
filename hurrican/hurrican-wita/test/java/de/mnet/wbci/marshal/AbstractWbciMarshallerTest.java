/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.13
 */
package de.mnet.wbci.marshal;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.xml.transform.StringSource;
import org.springframework.xml.xsd.XsdSchema;
import org.testng.Assert;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.mnet.common.xml.WsdlXsdSchema;
import de.mnet.wbci.model.WbciCdmVersion;

/**
 *
 */
@ContextConfiguration({ "classpath:de/mnet/wbci/marshal/wbci-marshaller-test-context.xml" })
public abstract class AbstractWbciMarshallerTest extends AbstractTestNGSpringContextTests {

    private WsdlXsdSchema carrierNegotiationServiceV1Xsd;
    private WsdlXsdSchema locationServiceXsd;

    protected void assertSchemaValidCarrierNegotiationService(WbciCdmVersion version, String message) throws IOException, ParserConfigurationException, SAXException {
        XsdSchema xsdSchema = getCarrierNegotiationServiceXsd(version);
        SAXParseException[] validationErrors = xsdSchema.createValidator().validate(new StringSource(message));
        Assert.assertEquals(validationErrors, new SAXParseException[] { }, String.format("Expecting no exceptions but got %s", Arrays.toString(validationErrors)));
    }

    protected void assertSchemaValidLocationService(String message) throws IOException, ParserConfigurationException, SAXException {
        XsdSchema xsdSchema = getLocationServiceXsd();
        SAXParseException[] validationErrors = xsdSchema.createValidator().validate(new StringSource(message));
        Assert.assertEquals(validationErrors, new SAXParseException[] { }, String.format("Expecting no exceptions but got %s", Arrays.toString(validationErrors)));
    }

    private XsdSchema getCarrierNegotiationServiceXsd(WbciCdmVersion version) throws IOException, SAXException, ParserConfigurationException {
        if (version.equals(WbciCdmVersion.V1)) {
            if (carrierNegotiationServiceV1Xsd == null) {
                carrierNegotiationServiceV1Xsd = new WsdlXsdSchema(new ClassPathResource("/wsdl/CarrierNegotiationService.wsdl"));
                carrierNegotiationServiceV1Xsd.afterPropertiesSet();
            }

            return carrierNegotiationServiceV1Xsd;
        }
        else {
            throw new IllegalArgumentException("Unsupported cdm version for CarrierNegotiationService");
        }
    }

    private XsdSchema getLocationServiceXsd() throws IOException, SAXException, ParserConfigurationException {
        if (locationServiceXsd == null) {
            locationServiceXsd = new WsdlXsdSchema(new ClassPathResource("/wsdl/LocationService.wsdl"));
            locationServiceXsd.afterPropertiesSet();
        }

        return locationServiceXsd;
    }
}
