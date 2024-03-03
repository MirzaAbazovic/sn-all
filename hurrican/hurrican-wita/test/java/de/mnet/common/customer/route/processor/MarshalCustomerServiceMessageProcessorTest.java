/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.common.customer.route.processor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.io.*;
import javax.xml.transform.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.customer.marshal.CustomerServiceMarshaller;
import de.mnet.common.customer.service.CustomerSchemaValidationService;
import de.mnet.common.exceptions.MessageProcessingException;
import de.mnet.common.route.helper.ExchangeHelper;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;

@Test(groups = BaseTest.UNIT)
public class MarshalCustomerServiceMessageProcessorTest {
    @InjectMocks
    private MarshalCustomerServiceMessageProcessor testling;

    @Mock
    private CustomerServiceMarshaller customerServiceMarshallerMock;

    @Mock
    private ExchangeHelper exchangeHelperMock;

    @Mock
    private AddCommunication addCommunicationMock;

    @Mock
    private CustomerSchemaValidationService schemaValidationServiceMock;

    @Mock
    protected Exchange exchangeMock;

    @Mock
    protected Message messageMock;

    @Mock
    protected SoapMessage soapMessageMock;

    @Mock
    protected SoapBody soapBodyMock;

    @Mock
    private SoapMessageFactory soapMessageFactoryMock;

    @BeforeMethod
    public void setupMockListener() throws Exception {
        testling = new MarshalCustomerServiceMessageProcessor();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() throws Exception {
        when(exchangeHelperMock.getOriginalMessageFromOutMessage(exchangeMock)).thenReturn(addCommunicationMock);
        when(exchangeMock.getOut()).thenReturn(messageMock);
        when(soapMessageFactoryMock.createWebServiceMessage()).thenReturn(soapMessageMock);
        when(soapMessageMock.getSoapBody()).thenReturn(soapBodyMock);

        testling.process(exchangeMock);

        verify(soapMessageMock).writeTo(any(ByteArrayOutputStream.class));
        verify(messageMock).setBody(any(Object.class));
        verify(messageMock).setHeader(eq(AtlasEsbConstants.SOAPACTION_TIBCO), any(Object.class));
        verify(schemaValidationServiceMock).validatePayload(any(Source.class));
    }

    @Test(expectedExceptions = MessageProcessingException.class)
    public void testProcessWithException() throws Exception {
        when(exchangeHelperMock.getOriginalMessageFromOutMessage(exchangeMock)).thenThrow(new RuntimeException("simulated exception"));
        testling.process(exchangeMock);
    }
}
