/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.route.processor.carriernegotiation.in;


import static org.mockito.Mockito.*;

import org.apache.camel.Exchange;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.exceptions.MessageProcessingException;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.route.helper.MessageProcessingMetadataHelper;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciRequestService;

@Test(groups = BaseTest.UNIT)
public class StoreWbciMessageProcessorTest extends BaseTest {

    @Mock
    private MessageProcessingMetadataHelper resultsHelper;
    @Mock
    private WbciMeldungService wbciMeldungService;
    @Mock
    private WbciRequestService wbciRequestService;
    @Mock
    private Exchange exchange;
    @Mock
    private MessageProcessingMetadata metadataMock;

    @InjectMocks
    @Spy
    private StoreWbciMessageProcessor testling = new StoreWbciMessageProcessor();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessWbciRequest() throws Exception {
        WbciRequest request = new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        // reset data that should be overwritten
        request.setProcessedAt(null);
        request.setCreationDate(null);
        request.setUpdatedAt(null);
        request.setIoType(null);

        when(resultsHelper.getMessageProcessingMetadata(exchange)).thenReturn(metadataMock);
        doReturn(request).when(testling).getOriginalMessage(exchange);
        testling.process(exchange);

        Assert.assertNotNull(request.getProcessedAt());
        Assert.assertEquals(request.getProcessedAt(), request.getCreationDate());
        Assert.assertEquals(request.getProcessedAt(), request.getUpdatedAt());
        Assert.assertEquals(request.getIoType(), IOType.IN);

        verify(wbciRequestService).processIncomingRequest(metadataMock, request);
        verify(wbciMeldungService, times(0)).processIncomingMeldung(eq(metadataMock), any(Meldung.class));
    }

    @Test
    public void testProcessWbciMeldung() throws Exception {
        Meldung meldung = new RueckmeldungVorabstimmungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        // reset data that should be overwritten
        meldung.setProcessedAt(null);

        when(resultsHelper.getMessageProcessingMetadata(exchange)).thenReturn(metadataMock);
        doReturn(meldung).when(testling).getOriginalMessage(exchange);
        testling.process(exchange);

        Assert.assertNotNull(meldung.getProcessedAt());

        verify(wbciMeldungService).processIncomingMeldung(metadataMock, meldung);
        verify(wbciRequestService, times(0)).processIncomingRequest(eq(metadataMock), any(WbciRequest.class));
    }

    @Test(expectedExceptions = MessageProcessingException.class)
    public void testProcessNull() throws Exception {
        when(resultsHelper.getMessageProcessingMetadata(exchange)).thenReturn(metadataMock);
        doReturn(null).when(testling).getOriginalMessage(exchange);
        testling.process(exchange);
    }

}
