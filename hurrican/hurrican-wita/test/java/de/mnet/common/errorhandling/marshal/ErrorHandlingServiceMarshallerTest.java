/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.13
 */
package de.mnet.common.errorhandling.marshal;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import org.springframework.xml.xsd.XsdSchema;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.builder.cdm.errorhandling.v1.HandleErrorTestBuilder;
import de.mnet.common.xml.WsdlXsdSchema;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

@Test(groups = BaseTest.UNIT)
@ContextConfiguration({ "classpath:de/mnet/common/errorhandling/marshaller-test-context.xml" })
public class ErrorHandlingServiceMarshallerTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private ErrorHandlingServiceMarshaller testling;

    private WsdlXsdSchema errorHandlingServiceXsd;

    @Test
    public void testMarshal() throws Exception {
        HandleError he = new HandleErrorTestBuilder().buildValid();

        StringResult result = new StringResult();
        testling.marshal(he, result);

        assertThat(result.toString(), notNullValue());

        assertSchemaValidErrorHandlingService(result.toString());
    }

    protected void assertSchemaValidErrorHandlingService(String message) throws IOException, ParserConfigurationException, SAXException {
        XsdSchema xsdSchema = getErrorhandlingServiceXsd();
        SAXParseException[] validationErrors = xsdSchema.createValidator().validate(new StringSource(message));
        Assert.assertEquals(validationErrors, new SAXParseException[] { }, String.format("Expecting no exceptions but got %s", Arrays.toString(validationErrors)));
    }

    private XsdSchema getErrorhandlingServiceXsd() throws IOException, SAXException, ParserConfigurationException {
        if (errorHandlingServiceXsd == null) {
            errorHandlingServiceXsd = new WsdlXsdSchema(new ClassPathResource("/xsd/esb/errorhandling/ErrorHandlingService.wsdl"));
            errorHandlingServiceXsd.afterPropertiesSet();
        }

        return errorHandlingServiceXsd;
    }

}
