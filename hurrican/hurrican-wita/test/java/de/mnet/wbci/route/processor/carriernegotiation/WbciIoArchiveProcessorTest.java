/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.08.13
 */
package de.mnet.wbci.route.processor.carriernegotiation;

import static org.mockito.Matchers.*;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.service.HistoryService;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.route.WbciCamelConstants;
import de.mnet.wita.IOArchiveProperties;
import de.mnet.wita.model.IoArchive;

@Test(groups = BaseTest.UNIT)
public class WbciIoArchiveProcessorTest extends BaseTest implements WbciCamelConstants {

    private final String expectedCdm = "<xml>";
    private WbciMessage testdata;
    @Mock
    private Exchange exchange;
    @Mock
    private Message messageMock;
    @Mock
    private HistoryService historyServiceMock;

    @InjectMocks
    @Spy
    private WbciIoArchiveProcessor testling = new WbciIoArchiveProcessor() {
        @Override
        public WbciMessage getOriginalMessage(Exchange exchange) {
            return testdata;
        }

        @Override
        protected String getRequestXml(Exchange exchange) {
            return expectedCdm;
        }
    };

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(exchange.getIn()).thenReturn(messageMock);
        Mockito.when(messageMock.getBody()).thenReturn(expectedCdm);
    }

    @Test
    public void testProcess() throws Exception {
        testdata = new RueckmeldungVorabstimmungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        testling.process(exchange);
        Mockito.verify(messageMock, Mockito.times(0)).setBody(any(IoArchive.class));
        Mockito.verify(historyServiceMock).saveIoArchive(any(IoArchive.class));
    }

    @Test
    public void testCreateIoArchiveMeldung() throws Exception {
        testdata = new RueckmeldungVorabstimmungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        IoArchive result = testling.createIoArchive(exchange, testdata);
        checkGeneralIOFields(result, testdata, testdata.getTyp().toString());
    }

    @Test
    public void testCreateIoArchiveRequest() {
        testdata = new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        IoArchive result = testling.createIoArchive(exchange, testdata);
        checkGeneralIOFields(result, testdata, testdata.getTyp().name());
    }

    private void checkGeneralIOFields(IoArchive result, WbciMessage message, String expectedRequestMeldungTyp) {
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getIoSource(), IOArchiveProperties.IOSource.WBCI);
        Assert.assertEquals(result.getIoType().name(), message.getIoType().name());
        Assert.assertEquals(result.getWitaExtOrderNo(), message.getWbciGeschaeftsfall().getVorabstimmungsId());
        Assert.assertEquals(result.getTimestampSent(), message.getProcessedAt());
        Assert.assertEquals(result.getRequestTimestamp(), message.getProcessedAt());
        Assert.assertEquals(result.getRequestMeldungstyp(), expectedRequestMeldungTyp);
        if (message instanceof Meldung) {
            Assert.assertEquals(result.getRequestMeldungscode(), ((Meldung) message).getMeldungsCodes());
            Assert.assertEquals(result.getRequestMeldungstext(), ((Meldung) message).getMeldungsTexte());
        }
        Assert.assertEquals(result.getRequestGeschaeftsfall(), message.getWbciGeschaeftsfall().getTyp().name());
        Assert.assertEquals(result.getRequestXml(), expectedCdm);
    }

}
