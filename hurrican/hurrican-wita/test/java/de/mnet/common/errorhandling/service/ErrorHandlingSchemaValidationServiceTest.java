/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.14
 */
package de.mnet.common.errorhandling.service;

import java.io.*;
import javax.xml.transform.dom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.builder.cdm.errorhandling.v1.HandleErrorTestBuilder;
import de.mnet.common.errorhandling.marshal.ErrorHandlingServiceMarshaller;
import de.mnet.common.exceptions.ServiceException;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
@ContextConfiguration({ "classpath:de/mnet/common/errorhandling/marshaller-test-context.xml" })
public class ErrorHandlingSchemaValidationServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ErrorHandlingServiceMarshaller errorHandlingServiceMarshaller;

    private ErrorHandlingSchemaValidationService schemaValidationService = new ErrorHandlingSchemaValidationService();

    @Test(expectedExceptions = ServiceException.class,
            expectedExceptionsMessageRegExp = "Unable to locate schema definition for message payload")
    public void testSchemaNotFound() throws IOException {
        HandleError error = new HandleErrorTestBuilder().buildValid();

        DOMResult result = new DOMResult();
        errorHandlingServiceMarshaller.marshal(error, result);

        schemaValidationService.validatePayload(new DOMSource(result.getNode()));
    }
}
