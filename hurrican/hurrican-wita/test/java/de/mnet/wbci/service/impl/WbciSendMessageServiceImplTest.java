/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.12.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.hurrican.service.location.CamelProxyLookupService.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.hurrican.service.location.CamelProxyLookupService;
import de.mnet.wbci.integration.CarrierNegotationService;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.route.helper.MessageProcessingMetadataHelper;

@Test(groups = UNIT)
public class WbciSendMessageServiceImplTest {

    @InjectMocks
    private WbciSendMessageServiceImpl testling;

    @Mock
    private CamelProxyLookupService camelProxyLookupServiceMock;

    @Mock
    private WbciMessage wbciMessageMock;

    @Mock
    private CarrierNegotationService carrierNegotationServiceMock;

    @BeforeMethod
    public void setUp() {
        testling = new WbciSendMessageServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendMessage() throws Exception {
        when(camelProxyLookupServiceMock.lookupCamelProxy(
                PROXY_CARRIER_NEGOTIATION,
                CarrierNegotationService.class))
                .thenReturn(carrierNegotationServiceMock);

        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Assert.assertNotNull(args);
                Assert.assertEquals(args.length, 2);
                Assert.assertEquals(args[0], wbciMessageMock);
                Assert.assertNotNull(args[1]);
                Assert.assertTrue(args[1] instanceof Map);
                Map options = (Map) args[1];
                Assert.assertTrue(options.containsKey(MessageProcessingMetadataHelper.MESSAGE_PROCESSING_METADATA_KEY));
                Assert.assertEquals(options.get(MessageProcessingMetadataHelper.MESSAGE_PROCESSING_METADATA_KEY), metadata);
                return null;
            }
        }).when(carrierNegotationServiceMock).sendToWbci(any(WbciMessage.class), any(Map.class));

        testling.sendAndProcessMessage(metadata, wbciMessageMock);

    }
}
