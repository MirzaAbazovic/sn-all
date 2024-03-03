/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.10.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.xml.xsd.XsdSchema;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import de.mnet.common.xml.WsdlXsdSchema;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.marshal.MessageMarshallerDelegate;
import de.mnet.wbci.marshal.v1.MessageMarshaller;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;

/**
 *
 */
@Test(groups = UNIT)
public class WbciSchemaValidationServiceImplTest extends AbstractWbciMarshallerTest {

    @Autowired
    private MessageMarshaller messageMarshaller;

    private WbciSchemaValidationServiceImpl schemaValidationService = new WbciSchemaValidationServiceImpl();

    @BeforeTest
    public void setUp() throws Exception {
        WsdlXsdSchema wsdl = new WsdlXsdSchema(new ClassPathResource("/wsdl/CarrierNegotiationService.wsdl"));
        wsdl.afterPropertiesSet();
        List<XsdSchema> schemas = new ArrayList<>();
        schemas.add(wsdl);
        schemaValidationService.setSchemas(schemas);
    }

    @Test
    public void testSchemaValidationOk() throws IOException {
        WbciRequest wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        DOMResult result = new DOMResult();
        messageMarshaller.marshal(wbciRequest, result);

        schemaValidationService.validatePayload(new DOMSource(result.getNode()));
    }

    @Test(expectedExceptions = WbciServiceException.class,
            expectedExceptionsMessageRegExp = "Schema validation failed!")
    public void testSchemaValidationFailed() throws IOException {
        WbciRequest wbciRequest = new VorabstimmungsAnfrageTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        wbciRequest.getWbciGeschaeftsfall().setVorabstimmungsId("INVALID");

        DOMResult result = new DOMResult();
        messageMarshaller.marshal(wbciRequest, result);

        schemaValidationService.validatePayload(new DOMSource(result.getNode()));
    }

    @Test
    public void testSchemaValidationWbciMessageOk() throws IOException {
        DOMResult result = new DOMResult();
        WbciRequest wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        messageMarshaller.marshal(wbciRequest, result);

        SoapMessageFactory soapMessageFactory = mock(SoapMessageFactory.class);
        SoapMessage soapMessage = mock(SoapMessage.class);
        SoapBody soapBody = mock(SoapBody.class);
        MessageMarshallerDelegate messageMarshallerDelegate = mock(MessageMarshallerDelegate.class);
        when(soapMessageFactory.createWebServiceMessage()).thenReturn(soapMessage);
        when(soapMessage.getSoapBody()).thenReturn(soapBody);
        ReflectionTestUtils.setField(schemaValidationService, "messageMarshaller", messageMarshallerDelegate);
        ReflectionTestUtils.setField(schemaValidationService, "soapMessageFactory", soapMessageFactory);

        when(soapBody.getPayloadSource()).thenReturn(new DOMSource(result.getNode()));

        schemaValidationService.validateWbciMessage(wbciRequest, WbciCdmVersion.V1);
        verify(messageMarshallerDelegate).marshal(wbciRequest, null, WbciCdmVersion.V1);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Unable to marshall message")
    public void testSchemaValidationWbciMessageIOException() throws IOException {
        WbciRequest wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        SoapMessageFactory soapMessageFactory = mock(SoapMessageFactory.class);
        SoapMessage soapMessage = mock(SoapMessage.class);
        SoapBody soapBody = mock(SoapBody.class);
        MessageMarshallerDelegate messageMarshallerDelegate = mock(MessageMarshallerDelegate.class);
        when(soapMessageFactory.createWebServiceMessage()).thenReturn(soapMessage);
        when(soapMessage.getSoapBody()).thenReturn(soapBody);
        ReflectionTestUtils.setField(schemaValidationService, "messageMarshaller", messageMarshallerDelegate);
        ReflectionTestUtils.setField(schemaValidationService, "soapMessageFactory", soapMessageFactory);

        doThrow(IOException.class).when(messageMarshallerDelegate).marshal(anyObject(), any(Result.class), any(WbciCdmVersion.class));
        schemaValidationService.validateWbciMessage(wbciRequest, WbciCdmVersion.V1);
    }
}
