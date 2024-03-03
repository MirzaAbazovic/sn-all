/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.10.14
 */
package de.mnet.common.customer.service;

import java.io.*;
import java.util.*;
import javax.xml.transform.dom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.xml.xsd.XsdSchema;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.augustakom.common.BaseTest;
import de.mnet.common.customer.marshal.CustomerServiceMarshaller;
import de.mnet.common.exceptions.ServiceException;
import de.mnet.common.xml.WsdlXsdSchema;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.builder.cdm.customer.AddCommunicationTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
@ContextConfiguration({ "classpath:de/mnet/common/customer/marshaller-test-context.xml" })
public class CustomerSchemaValidationServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private CustomerServiceMarshaller messageMarshaller;

    private CustomerSchemaValidationService schemaValidationService = new CustomerSchemaValidationService();

    @BeforeTest
    public void setUp() throws Exception {
        WsdlXsdSchema wsdl = new WsdlXsdSchema(new ClassPathResource("/wsdl/CustomerService.wsdl"));
        wsdl.afterPropertiesSet();
        List<XsdSchema> schemas = new ArrayList<>();
        schemas.add(wsdl);
        schemaValidationService.setSchemas(schemas);
    }

    @Test
    public void testSchemaValidationOk() throws IOException {
        AddCommunication csMessage = new AddCommunicationTestBuilder().buildValid();

        DOMResult result = new DOMResult();
        messageMarshaller.marshal(csMessage, result);

        schemaValidationService.validatePayload(new DOMSource(result.getNode()));
    }

    @Test(expectedExceptions = ServiceException.class,
            expectedExceptionsMessageRegExp = "Schema validation failed!")
    public void testSchemaValidationFailed() throws IOException {
        AddCommunication csMessage = new AddCommunication();

        DOMResult result = new DOMResult();
        messageMarshaller.marshal(csMessage, result);

        schemaValidationService.validatePayload(new DOMSource(result.getNode()));
    }

    @Test(expectedExceptions = ServiceException.class,
            expectedExceptionsMessageRegExp = "Unable to locate schema definition for message payload")
    public void testSchemaNotFound() throws IOException {
        AddCommunication csMessage = new AddCommunicationTestBuilder().buildValid();

        DOMResult result = new DOMResult();
        messageMarshaller.marshal(csMessage, result);

        Element wrong = ((Document)result.getNode()).createElementNS("http://test.org", "TestRequest");

        schemaValidationService.validatePayload(new DOMSource(wrong));
    }
}
