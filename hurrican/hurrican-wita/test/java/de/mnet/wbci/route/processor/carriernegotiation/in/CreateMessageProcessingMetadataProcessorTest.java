/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.02.14
 */
package de.mnet.wbci.route.processor.carriernegotiation.in;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.apache.camel.Exchange;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.route.helper.MessageProcessingMetadataHelper;

@Test(groups = BaseTest.UNIT)
public class CreateMessageProcessingMetadataProcessorTest {
    @InjectMocks
    private CreateMessageProcessingMetadataProcessor testling;

    @Mock
    protected Exchange exchangeMock;

    @BeforeMethod
    public void setupMockListener() throws Exception {
        testling = new CreateMessageProcessingMetadataProcessor();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() throws Exception {
        testling.process(exchangeMock);
        verify(exchangeMock).setProperty(eq(MessageProcessingMetadataHelper.MESSAGE_PROCESSING_METADATA_KEY), any(MessageProcessingMetadata.class));
    }
}
