/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.12.13
 */
package de.mnet.wbci.route.processor.carriernegotiation;

import static org.mockito.Mockito.*;

import java.util.*;
import org.apache.camel.Exchange;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.route.ExtractExchangeOptionsProcessor;
import de.mnet.common.route.helper.ExchangeHelper;

@Test(groups = BaseTest.UNIT)
public class ExtractExchangeOptionsProcessorTest {
    @Mock
    private Exchange exchangeMock;
    @Mock
    private ExchangeHelper exchangeHelperMock;

    @InjectMocks
    @Spy
    private ExtractExchangeOptionsProcessor testling = new ExtractExchangeOptionsProcessor();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() throws Exception {
        Map<String, Object> options = new HashMap<>();
        options.put("a", "1");
        when(exchangeHelperMock.getExchangeOptions(exchangeMock)).thenReturn(options);

        testling.process(exchangeMock);

        verify(exchangeMock).setProperty("a", "1");
    }
}
