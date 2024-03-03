/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.02.14
 */
package de.mnet.wbci.route.helper;

import static org.mockito.Mockito.*;

import org.apache.camel.Exchange;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.MessageProcessingMetadata;

@Test(groups = BaseTest.UNIT)
public class MessageProcessingMetadataHelperTest {
    @InjectMocks
    private MessageProcessingMetadataHelper testling;

    @Mock
    protected Exchange exchangeMock;

    @BeforeMethod
    public void setupMockListener() throws Exception {
        testling = new MessageProcessingMetadataHelper();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsIncomingMessageStoredPredicate() throws Exception {
        MessageProcessingMetadata metadata = new MessageProcessingMetadata();
        metadata.setPostProcessMessage(true);
        when(exchangeMock.getProperty(MessageProcessingMetadataHelper.MESSAGE_PROCESSING_METADATA_KEY)).thenReturn(metadata);

        Assert.assertTrue(testling.isPostProcessMessage().matches(exchangeMock));
    }

    @Test
    public void testIsDuplicateVaRequestPredicate() throws Exception {
        MessageProcessingMetadata metadata = new MessageProcessingMetadata();
        metadata.setIncomingMessageDuplicateVaRequest(true);
        when(exchangeMock.getProperty(MessageProcessingMetadataHelper.MESSAGE_PROCESSING_METADATA_KEY)).thenReturn(metadata);

        Assert.assertTrue(testling.isIncomingMessageDuplicateVaRequest().matches(exchangeMock));
    }

    @Test
    public void testGetMessageProcessingResults() throws Exception {
        MessageProcessingMetadata metadata = new MessageProcessingMetadata();
        when(exchangeMock.getProperty(MessageProcessingMetadataHelper.MESSAGE_PROCESSING_METADATA_KEY)).thenReturn(metadata);
        Assert.assertEquals(testling.getMessageProcessingMetadata(exchangeMock), metadata);
    }


}
