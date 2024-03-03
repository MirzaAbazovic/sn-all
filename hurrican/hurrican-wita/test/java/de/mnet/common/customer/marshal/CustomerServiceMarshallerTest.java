/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.common.customer.marshal;

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
import de.mnet.common.xml.WsdlXsdSchema;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.builder.cdm.customer.AddCommunicationTestBuilder;

@Test(groups = BaseTest.UNIT)
@ContextConfiguration({ "classpath:de/mnet/common/customer/marshaller-test-context.xml" })
public class CustomerServiceMarshallerTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private CustomerServiceMarshaller testling;

    private WsdlXsdSchema customerServiceXsd;

    @Test
    public void testMarshal() throws Exception {
        AddCommunication csComminication = new AddCommunicationTestBuilder().buildValid();

        StringResult result = new StringResult();
        testling.marshal(csComminication, result);

        assertThat(result.toString(), notNullValue());

        assertSchemaValidCustomerService(result.toString());
    }

    protected void assertSchemaValidCustomerService(String message) throws IOException, ParserConfigurationException, SAXException {
        XsdSchema xsdSchema = getCustomerServiceXsd();
        SAXParseException[] validationErrors = xsdSchema.createValidator().validate(new StringSource(message));
        Assert.assertEquals(validationErrors, new SAXParseException[] { }, String.format("Expecting no exceptions but got %s", Arrays.toString(validationErrors)));
    }

    private XsdSchema getCustomerServiceXsd() throws IOException, SAXException, ParserConfigurationException {
        if (customerServiceXsd == null) {
            customerServiceXsd = new WsdlXsdSchema(new ClassPathResource("/wsdl/CustomerService.wsdl"));
            customerServiceXsd.afterPropertiesSet();
        }

        return customerServiceXsd;
    }

}
