/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.13
 */
package de.mnet.common.errorhandling.processor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.*;
import org.apache.camel.Exchange;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.errorhandling.ErrorCode;
import de.mnet.common.exceptions.ServiceException;
import de.mnet.common.route.helper.ExceptionHelper;
import de.mnet.common.route.helper.ExchangeHelper;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

@Test(groups = BaseTest.UNIT)
public class GenerateErrorMessageProcessorTest {
    @InjectMocks
    private GenerateErrorMessageProcessor testling;

    @Mock
    protected Exchange exchangeMock;

    @Mock
    private ExchangeHelper exchangeHelperMock;

    @Mock
    private ExceptionHelper exceptionHelperMock;

    private final String soapAction = "/svc/op";
    private final String payload = "payload";
    private final String svc = "svc";
    private final String op = "op";
    private final String jmsDestination = String.format("/%s/%s", svc, op);
    private final String esbTrackingId = "T123";
    private final String esbErrorId = "123";
    private final String esbRetryCount = "2";
    private List<String[]> jmsHeaders = Arrays.asList(new String[][] { { "key", "val" } });
    private ServiceException exception = new ServiceException("some reason")
                                                .setErrorCode(ErrorCode.WBCI_DEFAULT)
                                                .setProcessName(GenerateErrorMessageProcessor.class);

    @BeforeMethod
    public void setupMockListener() throws Exception {
        testling = new GenerateErrorMessageProcessor();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessWithAllDataAvailableWithinExchange() throws Exception {
        when(exchangeHelperMock.getOriginalSoapActionFromExchange(exchangeMock)).thenReturn(soapAction);
        when(exchangeHelperMock.getOriginalCdmPayloadFromInMessage(exchangeMock)).thenReturn(payload);
        when(exchangeHelperMock.getOriginalJmsEndpointFromExchange(exchangeMock)).thenReturn(jmsDestination);
        when(exchangeHelperMock.getOriginalInHeadersFromExchange(eq(exchangeMock), any(String[].class))).thenReturn(jmsHeaders);
        when(exchangeHelperMock.getOriginalEsbTrackingIdFromExchange(exchangeMock)).thenReturn(esbTrackingId);
        when(exchangeHelperMock.getOriginalEsbErrorIdFromExchange(exchangeMock)).thenReturn(esbErrorId);
        when(exchangeHelperMock.getOriginalEsbRetryCountFromExchange(exchangeMock)).thenReturn(esbRetryCount);
        when(exceptionHelperMock.getExceptionFromExchange(exchangeMock)).thenReturn(exception);
        when(exceptionHelperMock.throwableToString(exception)).thenReturn(exception.getMessage());

        testling.process(exchangeMock);

        ArgumentCaptor<HandleError> argument = ArgumentCaptor.forClass(HandleError.class);
        verify(exceptionHelperMock).setErrorHandlingServiceMessage(eq(exchangeMock), argument.capture());

        HandleError handleError = argument.getValue();
        Assert.assertEquals(handleError.getTrackingId(), esbTrackingId);
        Assert.assertEquals(handleError.getComponent().getName(), AtlasEsbConstants.COMPONENT_NAME);
        Assert.assertEquals(handleError.getComponent().getProcessName(),
                exception.getProcessName());
        Assert.assertNotNull(handleError.getComponent().getHost());
        Assert.assertEquals(handleError.getComponent().getService(), svc);
        Assert.assertEquals(handleError.getComponent().getOperation(), op);
        Assert.assertNotNull(handleError.getComponent().getProcessId());
        Assert.assertEquals(handleError.getError().getCode(), exception.getErrorCode().getCode());
        Assert.assertEquals(handleError.getError().getMessage(), exception.getErrorCode().getMessage());
        Assert.assertNotNull(handleError.getError().getTime());
        Assert.assertTrue(handleError.getError().getErrorDetails().contains(exception.getMessage()));
        Assert.assertEquals(handleError.getMessage().getPayload(), payload);
        Assert.assertEquals(handleError.getMessage().getJMSEndpoint(), jmsDestination);
        Assert.assertEquals(handleError.getMessage().getJMSProperty().size(), 1);
        Assert.assertEquals(handleError.getMessage().getJMSProperty().get(0).getKey(), "key");
        Assert.assertEquals(handleError.getMessage().getJMSProperty().get(0).getValue(), "val");
        Assert.assertEquals(handleError.getMessage().getRetryInfo().getOrigErrorId(), esbErrorId);
        Assert.assertEquals(handleError.getMessage().getRetryInfo().getRetryCount().toString(), esbRetryCount);
    }

    /**
     * The ErrorHandlingService message should still be generated even when no data is available within the exchange.
     *
     * @throws Exception
     */
    @Test
    public void testProcessWithNoDataInExchange() throws Exception {
        testling.process(exchangeMock);
        ArgumentCaptor<HandleError> argument = ArgumentCaptor.forClass(HandleError.class);
        verify(exceptionHelperMock).setErrorHandlingServiceMessage(eq(exchangeMock), argument.capture());

        HandleError handleError = argument.getValue();

        Assert.assertEquals(handleError.getComponent().getName(), AtlasEsbConstants.COMPONENT_NAME);
        Assert.assertEquals(handleError.getComponent().getProcessName(), "Unknown");
        Assert.assertNotNull(handleError.getComponent().getHost());
        Assert.assertNotNull(handleError.getComponent().getProcessId());
        Assert.assertEquals(handleError.getError().getCode(), ErrorCode.HUR_DEFAULT.getCode());
        Assert.assertEquals(handleError.getError().getMessage(), ErrorCode.HUR_DEFAULT.getMessage());
        Assert.assertNotNull(handleError.getError().getTime());
    }

    /**
     * The ErrorHandlingService message should still be generated even when an exception is thrown during message
     * generation.
     *
     * @throws Exception
     */
    @Test
    public void testProcessWhenExceptionThrown() throws Exception {
        when(exchangeHelperMock.getOriginalCdmPayloadFromInMessage(exchangeMock)).thenThrow(
                new RuntimeException("Some ex"));
        testling.process(exchangeMock);
        ArgumentCaptor<HandleError> argument = ArgumentCaptor.forClass(HandleError.class);
        verify(exceptionHelperMock).setErrorHandlingServiceMessage(eq(exchangeMock), argument.capture());

        HandleError handleError = argument.getValue();
        Assert.assertNotNull(handleError);
    }

}
