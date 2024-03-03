/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.13
 */
package de.mnet.wbci.route.helper;

import static org.mockito.Mockito.*;

import org.apache.camel.Exchange;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.route.helper.ExceptionHelper;

@Test(groups = BaseTest.UNIT)
public class ExceptionHelperTest {
    @InjectMocks
    private ExceptionHelper testling;

    @Mock
    protected Exchange exchangeMock;

    @BeforeMethod
    public void setupMockListener() throws Exception {
        testling = new ExceptionHelper();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testContainsExceptionInExchange() throws Exception {
        when(exchangeMock.getProperty(Exchange.EXCEPTION_CAUGHT)).thenReturn("Yes");
        Assert.assertTrue(testling.containsExceptionInExchange(exchangeMock));

        reset(exchangeMock);
        when(exchangeMock.getProperty(Exchange.EXCEPTION_CAUGHT)).thenReturn(null);
        Assert.assertFalse(testling.containsExceptionInExchange(exchangeMock));
    }

    @Test
    public void testGetExceptionFromExchange() throws Exception {
        Throwable throwableMock = mock(Throwable.class);
        when(exchangeMock.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class)).thenReturn(throwableMock);
        Assert.assertEquals(testling.getExceptionFromExchange(exchangeMock), throwableMock);

        // test also null
        reset(exchangeMock);
        when(exchangeMock.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class)).thenReturn(null);
        Assert.assertNull(testling.getExceptionFromExchange(exchangeMock));
    }

    @Test
    public void testContainsErrorHandlingServiceMessage() throws Exception {
        when(exchangeMock.getProperty(ExceptionHelper.ERROR_SERVICE_MESSAGE_KEY)).thenReturn("Yes");
        Assert.assertTrue(testling.containsErrorHandlingServiceMessage(exchangeMock));
    }

    @Test
    public void testGetErrorHandlingServiceMessage() throws Exception {
        Object handleErrorMessage = "";
        when(exchangeMock.getProperty(ExceptionHelper.ERROR_SERVICE_MESSAGE_KEY)).thenReturn(handleErrorMessage);
        Assert.assertEquals(testling.getErrorHandlingServiceMessage(exchangeMock), handleErrorMessage);
    }

    @Test
    public void testSetErrorHandlingServiceMessage() throws Exception {
        Object handleErrorMessage = "";
        testling.setErrorHandlingServiceMessage(exchangeMock, handleErrorMessage);
        verify(exchangeMock).setProperty(ExceptionHelper.ERROR_SERVICE_MESSAGE_KEY, handleErrorMessage);
    }

}
