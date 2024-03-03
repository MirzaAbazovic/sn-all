/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.13
 */
package de.mnet.wbci.route.processor.carriernegotiation.out;

import static de.augustakom.common.tools.exceptions.ExceptionLogEntryContext.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import javax.xml.transform.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.xml.transform.StringResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchBuildings;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchRequest;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.marshal.location.LocationServiceMarshaller;
import de.mnet.wbci.model.builder.cdm.location.v1.SearchBuildingsTestBuilder;
import de.mnet.wbci.route.WbciCamelConstants;
import de.mnet.wbci.route.processor.location.MarshalLocationServiceProcessor;
import de.mnet.wbci.service.WbciSchemaValidationService;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class MarshalLocationServiceProcessorTest extends BaseTest implements WbciCamelConstants {
    @Mock
    private Exchange exchange;
    @Mock
    private Message message;
    @Mock
    private LocationServiceMarshaller locationServiceMarshaller;
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
    private MarshalLocationServiceProcessor testling = new MarshalLocationServiceProcessor();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testling.setSoapActionSearchBuilding("soapActionSearchBuildings");
    }

    @Test
    public void testProcess() throws Exception {
        StringResult soapBodyTransformationResult = new StringResult();

        SearchBuildings request = new SearchBuildingsTestBuilder().build();

        prepareCamelMocks(request, soapBodyTransformationResult);
        Mockito.doReturn(request).when(testling).getOriginalMessage(Matchers.any(Exchange.class));
        Mockito.doReturn(true).when(testling).isPayloadValid(soapMessage);

        when(exchange.getIn()).thenReturn(message);

        testling.process(exchange);

        verify(locationServiceMarshaller).marshal(any(SearchRequest.class), any(Result.class));
        verify(message).setBody(soapBodyTransformationResult.toString());
        verify(message).setHeader(AtlasEsbConstants.SOAPACTION_TIBCO, "soapActionSearchBuildings");
    }

    @Test
    public void testProcessInvalidPayload() throws Exception {
        StringResult soapBodyTransformationResult = new StringResult();

        SearchBuildings request = new SearchBuildingsTestBuilder().build();

        prepareCamelMocks(request, soapBodyTransformationResult);
        Mockito.doReturn(request).when(testling).getOriginalMessage(Matchers.any(Exchange.class));
        Mockito.doReturn(false).when(testling).isPayloadValid(soapMessage);

        testling.process(exchange);

        verify(locationServiceMarshaller).marshal(any(SearchRequest.class), any(Result.class));
        verify(message, times(0)).setBody(anyObject());
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
                return "Error marshalling the location service request!".equals(exceptionLogEntry.getErrorMessage())
                        && WBCI_LOCATION_SEARCH_REQUEST_ERROR.identifier.equals(exceptionLogEntry.getContext())
                        && ExceptionUtils.getStackTrace(validationException).equals(exceptionLogEntry.getStacktrace());
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
    }

    private void prepareCamelMocks(SearchBuildings originalMessage, StringResult payloadResult) {
        reset(exchange, soapMessageFactory, soapMessage, soapBody);

        when(exchange.getOut()).thenReturn(message);
        when(soapMessageFactory.createWebServiceMessage()).thenReturn(soapMessage);
        when(soapMessage.getSoapBody()).thenReturn(soapBody);
        when(soapBody.getPayloadResult()).thenReturn(payloadResult);
        when(message.getBody()).thenReturn(originalMessage);
    }

}
