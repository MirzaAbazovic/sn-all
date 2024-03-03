/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.wbci.route.processor.carriernegotiation.out;

import static de.augustakom.common.tools.exceptions.ExceptionLogEntryContext.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchBuildingsResponse;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchResponse;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.builder.cdm.location.v1.SearchBuildingsResponseTestBuilder;
import de.mnet.wbci.route.WbciCamelConstants;
import de.mnet.wbci.route.processor.location.UnmarshalLocationServiceProcessor;
import de.mnet.wbci.service.WbciSchemaValidationService;
import de.mnet.wbci.unmarshal.location.LocationServiceUnmarshaller;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class UnmarshalLocationServiceProcessorTest extends BaseTest implements WbciCamelConstants {
    @Mock
    private Exchange exchange;
    @Mock
    private Message message;
    @Mock
    private LocationServiceUnmarshaller locationServiceUnmarshaller;
    @Mock
    private SoapMessageFactory soapMessageFactory;
    @Mock
    private SoapMessage soapMessage;
    @Mock
    private SoapBody soapBody;
    @Mock
    private WbciSchemaValidationService schemaValidationService;
    @Mock
    private ExceptionLogService exceptionLogService;

    @InjectMocks
    @Spy
    private UnmarshalLocationServiceProcessor testling = new UnmarshalLocationServiceProcessor();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() throws Exception {
        StringResult soapBodyTransformationResult = new StringResult();

        SearchBuildingsResponse response = new SearchBuildingsResponseTestBuilder().build();

        prepareCamelMocks("Some search request dummy");

        when(locationServiceUnmarshaller.unmarshal(any(Source.class))).thenReturn(response);
        when(exchange.getOut()).thenReturn(message);
        Mockito.doReturn(true).when(testling).isPayloadValid(soapMessage);

        testling.process(exchange);

        verify(locationServiceUnmarshaller).unmarshal(any(Source.class));
        verify(message).setBody(response, SearchResponse.class);
    }

    @Test
    public void testProcessInvalidPayload() throws Exception {
        SearchBuildingsResponse response = new SearchBuildingsResponseTestBuilder().build();

        prepareCamelMocks("Some search request dummy");

        when(locationServiceUnmarshaller.unmarshal(any(Source.class))).thenReturn(response);
        when(exchange.getOut()).thenReturn(message);
        Mockito.doReturn(false).when(testling).isPayloadValid(soapMessage);

        testling.process(exchange);

        verify(locationServiceUnmarshaller, times(0)).unmarshal(any(Source.class));
        verify(message, times(1)).setBody(any(SearchBuildingsResponse.class), eq(SearchBuildingsResponse.class));
    }

    @Test
    public void testPayloadValid() {
        Source payloadSource = mock(Source.class);
        when(soapMessage.getSoapBody()).thenReturn(soapBody);
        when(soapBody.getPayloadSource()).thenReturn(payloadSource);
        assertTrue(testling.isPayloadValid(soapMessage));

        verify(schemaValidationService).validatePayload(payloadSource);
        verify(exceptionLogService, times(0)).saveExceptionLogEntry(any(ExceptionLogEntry.class));
    }

    @Test
    public void testPayloadInvalid() {
        Source payloadSource = mock(Source.class);
        when(soapMessage.getSoapBody()).thenReturn(soapBody);
        when(soapBody.getPayloadSource()).thenReturn(payloadSource);
        final WbciServiceException validationException = new WbciServiceException();
        doThrow(validationException).when(schemaValidationService).validatePayload(payloadSource);
        assertFalse(testling.isPayloadValid(soapMessage));

        verify(schemaValidationService).validatePayload(payloadSource);
        verify(exceptionLogService).saveExceptionLogEntry(argThat(new BaseMatcher<ExceptionLogEntry>() {
            @Override
            public boolean matches(Object o) {
                final ExceptionLogEntry exceptionLogEntry = (ExceptionLogEntry) o;
                return "Error unmarshalling the location service response!".equals(exceptionLogEntry.getErrorMessage())
                        && WBCI_LOCATION_SEARCH_RESPONSE_ERROR.identifier.equals(exceptionLogEntry.getContext())
                        && ExceptionUtils.getStackTrace(validationException).equals(exceptionLogEntry.getStacktrace());
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
    }

    private void prepareCamelMocks(String requestPayload) throws IOException {
        reset(exchange, soapMessageFactory, soapMessage, soapBody);

        when(exchange.getIn()).thenReturn(message);
        when(soapMessageFactory.createWebServiceMessage(any(InputStream.class))).thenReturn(soapMessage);
        when(soapMessage.getSoapBody()).thenReturn(soapBody);
        when(soapBody.getPayloadSource()).thenReturn(new StringSource(requestPayload));
        when(message.getBody()).thenReturn(requestPayload);
    }
}
