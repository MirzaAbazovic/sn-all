/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.14
 */
package de.mnet.wita.route.processor;

import static org.mockito.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.service.HistoryService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.IOArchiveProperties;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.builder.meldung.QualifizierteEingangsBestaetigungBuilder;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.IoArchive;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class WitaIoArchiveProcessorTest extends BaseTest {

    private final String expectedCdm = "<xml>";
    private WitaMessage testdata;
    @Mock
    private Exchange exchange;
    @Mock
    private Message messageMock;
    @Mock
    private HistoryService historyServiceMock;

    @InjectMocks
    @Spy
    private WitaIoArchiveProcessor testling = new WitaIoArchiveProcessor() {
        @Override
        public WitaMessage getOriginalMessage(Exchange exchange) {
            return testdata;
        }

        @Override
        protected IOArchiveProperties.IOType getIOType() {
            return IOArchiveProperties.IOType.OUT;
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
        testdata = new QualifizierteEingangsBestaetigungBuilder().build();
        testling.process(exchange);
        Mockito.verify(messageMock, Mockito.times(0)).setBody(any(IoArchive.class));
        Mockito.verify(historyServiceMock).saveIoArchive(any(IoArchive.class));
    }

    @Test
    public void testCreateIoArchiveMeldung() throws Exception {
        testdata = new QualifizierteEingangsBestaetigungBuilder().build();
        LocalDateTime versandZeitstempel = DateConverterUtils.asLocalDateTime(((Meldung) testdata).getVersandZeitstempel());
        assertNotNull(versandZeitstempel);

        IoArchive result = testling.createIoArchive(exchange, testdata);
        checkGeneralIOFields(result, testdata, MeldungsType.QEB.name());
        assertEquals(DateConverterUtils.asLocalDateTime(result.getRequestTimestamp()), versandZeitstempel);
        assertEquals(DateConverterUtils.asLocalDateTime(result.getTimestampSent()), versandZeitstempel);
    }

    @Test
    public void testCreateIoArchiveErlmKueDt() throws Exception {
        String externeAuftragsnummer = "12345678";
        testdata = new ErledigtMeldungBuilder()
                .withGeschaeftsfallTyp(GeschaeftsfallTyp.KUENDIGUNG_TELEKOM)
                .withExterneAuftragsnummer(externeAuftragsnummer)
                .build();
        LocalDateTime versandZeitstempel = DateConverterUtils.asLocalDateTime(((Meldung) testdata).getVersandZeitstempel());
        assertNotNull(versandZeitstempel);

        IoArchive result = testling.createIoArchive(exchange, testdata);
        checkGeneralIOFields(result, testdata, MeldungsType.ERLM.name());
        assertEquals(DateConverterUtils.asLocalDateTime(result.getRequestTimestamp()), versandZeitstempel);
        assertEquals(DateConverterUtils.asLocalDateTime(result.getTimestampSent()), versandZeitstempel);
    }

    @Test
    public void testCreateIoArchiveMeldungNoVersandZeitstempel() throws Exception {
        testdata = new QualifizierteEingangsBestaetigungBuilder().build();
        ((Meldung) testdata).setVersandZeitstempel(null);

        IoArchive result = testling.createIoArchive(exchange, testdata);
        checkGeneralIOFields(result, testdata, MeldungsType.QEB.name());
    }

    @Test
    public void testCreateIoArchiveRequest() {
        testdata = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).withSentAt(LocalDateTime.now()).buildValid();
        LocalDateTime sentAt = DateConverterUtils.asLocalDateTime(((MnetWitaRequest) testdata).getSentAt());
        LocalDateTime creationDate = DateConverterUtils.asLocalDateTime(((MnetWitaRequest) testdata).getMwfCreationDate());
        assertNotNull(sentAt);
        assertNotNull(creationDate);

        IoArchive result = testling.createIoArchive(exchange, testdata);
        checkGeneralIOFields(result, testdata, null);
        assertEquals(DateConverterUtils.asLocalDateTime(result.getTimestampSent()), sentAt);
        assertEquals(DateConverterUtils.asLocalDateTime(result.getRequestTimestamp()), creationDate);
    }

    @Test
    public void testCreateIoArchiveRequestNoSentAt() {
        testdata = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();
        LocalDateTime sentAt = ((MnetWitaRequest) testdata).getSentAt() != null ? DateConverterUtils.asLocalDateTime(((MnetWitaRequest) testdata).getSentAt()) : null;
        assertNull(sentAt);

        IoArchive result = testling.createIoArchive(exchange, testdata);
        checkGeneralIOFields(result, testdata, null);
    }

    private void checkGeneralIOFields(IoArchive result, WitaMessage message, String expectedRequestMeldungTyp) {
        assertNotNull(result);
        assertEquals(result.getIoSource(), IOArchiveProperties.IOSource.WITA);

        if (message instanceof MnetWitaRequest) {
            assertEquals(result.getIoType().name(), IOArchiveProperties.IOType.OUT.name());
            assertEquals(result.getWitaExtOrderNo(), ((MnetWitaRequest) message).getExterneAuftragsnummer());
            assertEquals(result.getRequestGeschaeftsfall(), ((MnetWitaRequest) message).getGeschaeftsfall().getGeschaeftsfallTyp().name());
        } else if (message instanceof Meldung) {
            Meldung meldung = (Meldung) message;
            assertEquals(result.getIoType().name(), IOArchiveProperties.IOType.OUT.name());
            String externeAuftragsnummer = meldung.getExterneAuftragsnummer();
            assertEquals(result.getWitaExtOrderNo(), externeAuftragsnummer);
            assertEquals(result.getRequestMeldungscode(), meldung.getMeldungsCodes());
            assertEquals(result.getRequestMeldungstext(), meldung.getMeldungsTexte());
        }

        assertNotNull(result.getTimestampSent());
        assertNotNull(result.getRequestTimestamp());
        assertEquals(result.getRequestMeldungstyp(), expectedRequestMeldungTyp);
        assertEquals(result.getRequestXml(), expectedCdm);
    }

}
