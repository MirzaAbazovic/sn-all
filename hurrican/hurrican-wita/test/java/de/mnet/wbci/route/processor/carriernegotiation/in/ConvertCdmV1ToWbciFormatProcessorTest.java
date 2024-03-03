/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.13
 */
package de.mnet.wbci.route.processor.carriernegotiation.in;

import static org.mockito.Mockito.*;

import org.apache.camel.Message;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.WbciMessage;

@Test(groups = BaseTest.UNIT)
public class ConvertCdmV1ToWbciFormatProcessorTest extends BaseTest {

    @Mock
    private Message outMessage;
    @Mock
    private WbciMessage messageMock;

    @InjectMocks
    private ConvertCdmV1ToWbciFormatProcessor cut = new ConvertCdmV1ToWbciFormatProcessor();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    public void testSetBody() throws Exception {
        cut.setBody(outMessage, messageMock);
        verify(outMessage).setBody(messageMock, WbciMessage.class);
    }
}
