/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 17.10.13 
 */
package de.mnet.common.errorhandling.processor;

import static de.augustakom.common.tools.exceptions.ExceptionLogEntryContext.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.*;
import javax.xml.transform.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;
import de.mnet.common.errorhandling.marshal.ErrorHandlingServiceMarshaller;
import de.mnet.common.errorhandling.service.ErrorHandlingSchemaValidationService;
import de.mnet.common.exceptions.MessageProcessingException;
import de.mnet.common.route.helper.ExceptionHelper;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchRequest;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;
import de.mnet.wbci.exception.WbciServiceException;

@Test(groups = BaseTest.UNIT)
public class MarshalErrorMessageProcessorTest {

    @Mock
    protected Exchange exchangeMock;
    @Mock
    protected HandleError handleErrorMock;
    @Mock
    protected Message messageMock;
    @Mock
    protected SoapMessage soapMessageMock;
    @Mock
    protected SoapBody soapBodyMock;
    @Spy
    @InjectMocks
    private MarshalErrorMessageProcessor testling;
    @Mock
    private ExceptionHelper exceptionHelperMock;
    @Mock
    private ErrorHandlingServiceMarshaller errorHandlingServiceMarshallerMock;
    @Mock
    private SoapMessageFactory soapMessageFactoryMock;
    @Mock
    private ErrorHandlingSchemaValidationService schemaValidationService;
    @Mock
    private ExceptionLogService exceptionLogService;

    @BeforeMethod
    public void setupMockListener() throws Exception {
        testling = new MarshalErrorMessageProcessor();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() throws Exception {
        when(exceptionHelperMock.getErrorHandlingServiceMessage(exchangeMock)).thenReturn(handleErrorMock);
        when(exchangeMock.getOut()).thenReturn(messageMock);
        when(soapMessageFactoryMock.createWebServiceMessage()).thenReturn(soapMessageMock);
        when(soapMessageMock.getSoapBody()).thenReturn(soapBodyMock);
        doReturn(true).when(testling).isPayloadValid(soapMessageMock);

        testling.process(exchangeMock);

        verify(soapMessageMock).writeTo(any(ByteArrayOutputStream.class));
        verify(messageMock).setBody(any(Object.class));
        verify(messageMock).setHeader(eq(AtlasEsbConstants.SOAPACTION_TIBCO), any(Object.class));
        verify(messageMock).setHeader(eq(AtlasEsbConstants.HUR_ERROR_CODE), any(String.class));
    }

    @Test
    public void testProcessInvalidPayload() throws Exception {
        when(exceptionHelperMock.getErrorHandlingServiceMessage(exchangeMock)).thenReturn(handleErrorMock);
        when(exchangeMock.getOut()).thenReturn(messageMock);
        when(soapMessageFactoryMock.createWebServiceMessage()).thenReturn(soapMessageMock);
        when(soapMessageMock.getSoapBody()).thenReturn(soapBodyMock);
        doReturn(false).when(testling).isPayloadValid(soapMessageMock);

        testling.process(exchangeMock);

        verify(errorHandlingServiceMarshallerMock).marshal(any(SearchRequest.class), any(Result.class));
        verify(messageMock, times(0)).setBody(anyObject());
        verify(messageMock, times(0)).setHeader(anyString(), any(Object.class));
    }

    @Test(expectedExceptions = MessageProcessingException.class)
    public void testProcessWithException() throws Exception {
        when(exceptionHelperMock.getErrorHandlingServiceMessage(exchangeMock)).thenThrow(new RuntimeException("simulated exception"));
        testling.process(exchangeMock);
    }

    @Test
    public void testPayloadValid() {
        Source payloadSource = mock(Source.class);
        when(soapMessageMock.getSoapBody()).thenReturn(soapBodyMock);
        when(soapBodyMock.getPayloadSource()).thenReturn(payloadSource);
        assertTrue(testling.isPayloadValid(soapMessageMock));

        verify(schemaValidationService).validatePayload(payloadSource);
        verify(exceptionLogService, times(0)).saveExceptionLogEntry(any(ExceptionLogEntry.class));
    }

    @Test
    public void testPayloadInvalid() {
        Source payloadSource = mock(Source.class);
        when(soapMessageMock.getSoapBody()).thenReturn(soapBodyMock);
        when(soapBodyMock.getPayloadSource()).thenReturn(payloadSource);
        final WbciServiceException validationException = new WbciServiceException();
        doThrow(validationException).when(schemaValidationService).validatePayload(payloadSource);
        assertFalse(testling.isPayloadValid(soapMessageMock));

        verify(schemaValidationService).validatePayload(payloadSource);
        verify(exceptionLogService).saveExceptionLogEntry(argThat(new BaseMatcher<ExceptionLogEntry>() {
            @Override
            public boolean matches(Object o) {
                final ExceptionLogEntry exceptionLogEntry = (ExceptionLogEntry) o;
                return "Error marshalling the error service request!".equals(exceptionLogEntry.getErrorMessage())
                        && ATLAS_ERROR_SERVICE_ERROR.identifier.equals(exceptionLogEntry.getContext())
                        && ExceptionUtils.getStackTrace(validationException).equals(exceptionLogEntry.getStacktrace());
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
    }

}
