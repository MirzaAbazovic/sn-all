/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2011 13:07:16
 */
package de.mnet.wita.bpm.impl;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wita.bpm.WorkflowTaskName.*;
import static de.mnet.wita.message.meldung.position.AenderungsKennzeichen.*;
import static java.util.Arrays.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import org.activiti.engine.test.Deployment;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.OutgoingWitaMessage;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.bpm.WorkflowTaskName;
import de.mnet.wita.bpm.converter.MwfCbVorgangConverterService;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.EntgeltMeldungBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.builder.meldung.MessageBuilder;
import de.mnet.wita.message.builder.meldung.QualifizierteEingangsBestaetigungBuilder;
import de.mnet.wita.message.builder.meldung.TerminAnforderungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.VerzoegerungsMeldungBuilder;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.EntgeltMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.IncomingTalOrderMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.TalOrderMeldung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.service.WitaUsertaskService;

@Test(groups = SERVICE)
public class TalOrderWorkflowServiceImplActivitiTest extends AbstractTalOrderWorkflowServiceActivitiImplTest {

    @Autowired
    private WitaUsertaskService witaUsertaskService;
    @Autowired
    private MwfCbVorgangConverterService mwfCbVorgangConverterService;

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testSend() throws Exception {
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object message = invocation.getArguments()[0];
                assertTrue(message instanceof OutgoingWitaMessage);

                MnetWitaRequest auftrag = (MnetWitaRequest) message;
                assertNotNull(auftrag);
                assertTrue(auftrag.isAuftrag());
                assertNotNull(auftrag.getMwfCreationDate());
                assertNotNull(auftrag.getExterneAuftragsnummer());
                return null;
            }
        }).when(lineOrderService).sendToWita(any(WitaMessage.class));

        startProcessInstance();
    }

    @SuppressWarnings("unchecked")
    @DataProvider
    public Object[][] dataProviderReceiveMeldung() {
        // @formatter:off
        return new Object[][] {
                // Einzelne Meldungen nach der Teq
                { WAIT_FOR_MESSAGE,     Arrays.asList(Pair.create(new QualifizierteEingangsBestaetigungBuilder(), QualifizierteEingangsBestaetigung.class))},
                { WAIT_FOR_MESSAGE,     Arrays.asList(pair(new VerzoegerungsMeldungBuilder(), VerzoegerungsMeldung.class))},
                { null,                 Arrays.asList(pair(new AbbruchMeldungBuilder(), AbbruchMeldung.class))},
                { WAIT_FOR_MESSAGE,     Arrays.asList(pair(new AuftragsBestaetigungsMeldungBuilder(), AuftragsBestaetigungsMeldung.class))},
        };
        // @formatter:on
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    @Test(dataProvider = "dataProviderReceiveMeldung")
    public <T extends Meldung<?>, V extends MessageBuilder<T, V, MeldungsPosition>> void testReceiveMeldung(
            WorkflowTaskName processInState, List<Pair<V, Class<T>>> pairs) throws Exception {
        startProcessInstance();

        for (Pair<V, Class<T>> pair : pairs) {
            V builder = pair.getFirst();
            Class<T> clazz = pair.getSecond();

            T meldung = builder.withExterneAuftragsnummer(cbVorgang.getCarrierRefNr()).build();
            when(mwfEntityDao.findById(any(Long.class), eq(clazz))).thenReturn(meldung);

            assertTrue(meldung instanceof IncomingTalOrderMeldung);
            underTest.handleWitaMessage((IncomingTalOrderMeldung) meldung);
        }

        if (processInState == null) {
            assertProcessInstanceFinished();
        }
        else {
            assertProcessInstanceInState(processInState);
        }
    }

    public void testReceiveErlmAndEntm() throws FindException {
        startProcessInstance();
        sendAbm();
        sendErlm();
        sendEntm();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void tamWithErlmk() throws Exception {
        startProcessInstance();

        sendTam();
        sendErlmk();
        verify(lineOrderService).sendToWita(any(Auftrag.class));
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void tamTvAbbmErlmEntmStandard() throws Exception {
        startProcessInstance();
        sendQeb();
        sendAbm();
        sendTam();

        sendTv();

        sendAbbm(TERMINVERSCHIEBUNG, WAIT_FOR_MESSAGE);

        sendStorno();

        sendErlm();
        sendEntm();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void tamTvAbbmErlmEntmStorno() throws Exception {
        startProcessInstance();
        sendQeb();
        sendAbm();
        sendTam();

        sendTv();

        sendAbbm(TERMINVERSCHIEBUNG, WAIT_FOR_MESSAGE);

        sendStorno();

        sendErlm(STORNO);
        sendEntm(STORNO);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testErlmk() throws Exception {
        startProcessInstance();
        sendQeb();
        assertProcessInstanceInState(WAIT_FOR_MESSAGE);

        reset(lineOrderService);
        sendErlmk();
        verify(lineOrderService).sendToWita(any(WitaMessage.class));
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void tamTvAbbmErlmk() throws Exception {
        startProcessInstance();
        sendQeb();
        sendTam();

        sendTv();

        sendAbbm(AenderungsKennzeichen.TERMINVERSCHIEBUNG, WAIT_FOR_MESSAGE);

        reset(lineOrderService);
        sendErlmk();
        verify(lineOrderService).sendToWita(any(WitaMessage.class));

        sendEntm();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void tamWithSecondTamAndErlmk() throws Exception {
        startProcessInstance();

        sendTam();
        when(mwfEntityService.isLastMeldungTam(any(Meldung.class))).thenReturn(true);
        sendTam();
        assertTrue(cbVorgang.getTamUserTask().isMahnTam());
        sendErlmk();
        verify(lineOrderService).sendToWita(any(Auftrag.class));
    }

    @DataProvider
    public Object[][] dataProviderTamWithMessage() {
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder().build();
        AbbruchMeldung abbm = new AbbruchMeldungBuilder().build();
        ErledigtMeldung erlm = new ErledigtMeldungBuilder().build();
        EntgeltMeldung entm = new EntgeltMeldungBuilder().build();
        VerzoegerungsMeldung vzm = new VerzoegerungsMeldungBuilder().build();

        // @formatter:off
        return new Object[][] {
                { abm, AuftragsBestaetigungsMeldung.class, true, false, true, false, WAIT_FOR_MESSAGE },
                { abbm, AbbruchMeldung.class, true, false, true, true, null },

                // answered auf true prüfen, da evtl. ABM ausbleibended
                { erlm, ErledigtMeldung.class, true, false, true, false, WAIT_FOR_ENTM },
                { vzm, VerzoegerungsMeldung.class, false, true, false, false, WAIT_FOR_MESSAGE },

                { entm, EntgeltMeldung.class, false, true, false, false, WORKFLOW_ERROR },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderTamWithMessage")
    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public <T extends Meldung<?> & TalOrderMeldung> void testTamWithMessage(T meldung, Class<T> clazz,
            boolean cbVorgangAnswered, boolean cbVorgangClosed, boolean userTaskClosed, boolean piClosed,
            WorkflowTaskName piState) throws Exception {
        startProcessInstance();
        sendAbm();
        sendTam();

        meldung.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());
        when(mwfEntityDao.findById(1L, clazz)).thenReturn(meldung);

        reset(witaUsertaskService);
        cbVorgang.setAnsweredAt(null);
        cbVorgang.close();

        underTest.handleWitaMessage(meldung);

        assertCbVorgangStatusIsAnsweredAndClosed(cbVorgangAnswered, cbVorgangClosed);
        assertUserTaskStatusIsClosed(userTaskClosed);
        if (piClosed) {
            assertProcessInstanceFinished();
        }
        else {
            assertProcessInstanceInState(piState);
        }
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void abbmShouldCloseWorkflow() throws Exception {
        startProcessInstance();

        sendAbbm();
    }

    @DataProvider
    public Object[][] aenderungsKennzeichenStornoOrTv() {
        return new Object[][] { { AenderungsKennzeichen.STORNO }, { AenderungsKennzeichen.TERMINVERSCHIEBUNG }, };
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    @Test(dataProvider = "aenderungsKennzeichenStornoOrTv")
    public void abbmOnStornoOrTvShouldNotCloseWorkflow(AenderungsKennzeichen aenderungsKennzeichen) throws Exception {
        startProcessInstance();

        sendAbbm(aenderungsKennzeichen, WAIT_FOR_MESSAGE);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void tamWithTv() throws Exception {
        startProcessInstance();
        sendTam();

        sendTv();

        // Check, dass die TV gesendet wird
        verify(lineOrderService).sendToWita(any(TerminVerschiebung.class));
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    void tamWithStorno() throws Exception {
        startProcessInstance();
        sendTam();

        when(
                mwfEntityDao.queryByExample(Matchers.any(QualifizierteEingangsBestaetigung.class),
                        eq(QualifizierteEingangsBestaetigung.class))
        ).thenReturn(
                Collections.singletonList(new QualifizierteEingangsBestaetigung()));
        cbVorgang.setReturnRealDate(Date.from(LocalDateTime.now().plusDays(14).with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY)).atZone(ZoneId.systemDefault()).toInstant()));

        sendStorno();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testReceiveMeldungWithInvalidChars() throws Exception {
        startProcessInstance();

        AuftragsBestaetigungsMeldung invalidMeldung = new AuftragsBestaetigungsMeldungBuilder()
                .withExterneAuftragsnummer(cbVorgang.getCarrierRefNr()).withVertragsnummer("|||01234").build();

        when(mwfEntityDao.findById(any(Long.class), eq(AuftragsBestaetigungsMeldung.class))).thenReturn(invalidMeldung);
        when(workflowTaskValidationService.validateMwfInput(invalidMeldung)).thenReturn("error");

        underTest.handleWitaMessage(invalidMeldung);

        assertProcessInstanceInState(WORKFLOW_ERROR);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testReceiveAbbmWithoutMeldungspositionen() throws Exception {
        startProcessInstance();

        AbbruchMeldung abbm = new AbbruchMeldung(cbVorgang.getCarrierRefNr(), "5920312290");
        abbm.setGeschaeftsfallTyp(GeschaeftsfallTyp.BEREITSTELLUNG);
        abbm.setAenderungsKennzeichen(AenderungsKennzeichen.STANDARD);
        // don't add MeldungsPosition -> invalid ABBM

        when(mwfEntityDao.findById(1L, AbbruchMeldung.class)).thenReturn(abbm);
        when(workflowTaskValidationService.validateMwfInput(abbm)).thenReturn("error");

        underTest.handleWitaMessage(abbm);
        assertNotNull(abbm.getId());

        assertProcessInstanceInState(WORKFLOW_ERROR);
    }

    @SuppressWarnings("unchecked")
    @DataProvider
    public Object[][] dataProviderIgnoreMeldungenAfterClosedWorkflow() {
        AbbruchMeldung abbm = new AbbruchMeldungBuilder().withAenderungsKennzeichen(STANDARD).build();
        ErledigtMeldung erlm = new ErledigtMeldungBuilder().withAenderungsKennzeichen(STANDARD).build();
        EntgeltMeldung entm = new EntgeltMeldungBuilder().withAenderungsKennzeichen(STANDARD).build();

        AbbruchMeldung abbmStorno = new AbbruchMeldungBuilder().withAenderungsKennzeichen(STORNO).build();
        AbbruchMeldung abbmTv = new AbbruchMeldungBuilder().withAenderungsKennzeichen(TERMINVERSCHIEBUNG).build();
        VerzoegerungsMeldung vzm = new VerzoegerungsMeldungBuilder().build();

        // @formatter:off
        return new Object[][] {
                { asList(abbm), STORNO, vzm },
                { asList(abbm), TERMINVERSCHIEBUNG, vzm },
                { asList(erlm, entm), STORNO, vzm },
                { asList(erlm, entm), TERMINVERSCHIEBUNG, vzm },

                { asList(abbm), STORNO, abbmStorno },
                { asList(abbm), TERMINVERSCHIEBUNG, abbmTv },

                { asList(erlm, entm), STORNO, abbmStorno },
                { asList(erlm, entm), TERMINVERSCHIEBUNG, abbmTv },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderIgnoreMeldungenAfterClosedWorkflow")
    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public <T extends TalOrderMeldung & IncomingTalOrderMeldung, V extends Meldung<POS> & TalOrderMeldung, POS extends MeldungsPosition> void testIgnoreMeldungenAfterClosedWorkflow(
            List<V> meldungenToCloseWorkflow, AenderungsKennzeichen aenderungsKennzeichen, T message)
            throws FindException {
        startProcessInstance();
        message.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());
        sendQeb();
        sendAbm();

        if (aenderungsKennzeichen == STORNO) {
            sendStorno();
        }
        else if (aenderungsKennzeichen == TERMINVERSCHIEBUNG) {
            sendTv();
        }
        assertProcessInstanceInState(WAIT_FOR_MESSAGE);

        for (V meldung : meldungenToCloseWorkflow) {
            meldung.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());
            doReturn(meldung).when(mwfEntityDao).findById(1L, meldung.getClass());
            underTest.handleWitaMessage(meldung);
        }
        assertProcessInstanceFinished();

        when(cbVorgangDao.findCBVorgangByCarrierRefNr(any(String.class))).thenReturn(cbVorgang);

        Mockito.reset(mwfEntityDao);
        underTest.handleWitaMessage(message);
        assertProcessInstanceFinished();
        verify(mwfEntityDao).store(message);
    }

    @Test(dataProvider = "dataProviderIgnoreMeldungenAfterClosedWorkflow")
    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public <T extends TalOrderMeldung & IncomingTalOrderMeldung, V extends Meldung<POS> & TalOrderMeldung, POS extends MeldungsPosition> void testIgnoreMeldungenAfterClosedWorkflowAndCheckClosedCbVorgang(
            List<V> meldungenToCloseWorkflow, AenderungsKennzeichen aenderungsKennzeichen, T message) throws Exception {
        startProcessInstance();
        message.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());
        sendQeb();
        assertCbVorgangStatusIsAnsweredAndClosed(false, false);

        sendAbm();
        assertCbVorgangStatusIsAnsweredAndClosed(true, false);
        cbVorgang.close();
        assertCbVorgangStatusIsAnsweredAndClosed(true, true);

        if (aenderungsKennzeichen == STORNO) {
            sendStorno();
            mwfCbVorgangConverterService.writeStorno(cbVorgang, null);
        }
        else if (aenderungsKennzeichen == TERMINVERSCHIEBUNG) {
            sendTv();
            mwfCbVorgangConverterService.writeTerminVerschiebung(cbVorgang, LocalDate.now(), null);
        }
        assertCbVorgangStatusIsAnsweredAndClosed(false, false);
        assertProcessInstanceInState(WAIT_FOR_MESSAGE);

        for (V meldung : meldungenToCloseWorkflow) {
            meldung.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());
            doReturn(meldung).when(mwfEntityDao).findById(1L, meldung.getClass());
            underTest.handleWitaMessage(meldung);
        }
        assertProcessInstanceFinished();
        assertCbVorgangStatusIsAnsweredAndClosed(true, false);
        cbVorgang.close();
        assertCbVorgangStatusIsAnsweredAndClosed(true, true);

        when(cbVorgangDao.findCBVorgangByCarrierRefNr(any(String.class))).thenReturn(cbVorgang);

        Mockito.reset(mwfEntityDao);
        underTest.handleWitaMessage(message);
        assertProcessInstanceFinished();
        verify(mwfEntityDao).store(message);
        assertCbVorgangStatusIsAnsweredAndClosed(true, true);
    }

    private void assertCbVorgangStatusIsAnsweredAndClosed(boolean answered, boolean closed) {
        assertThat("cbVorgang answered status incorrect", cbVorgang.isAnswered(), equalTo(answered));
        assertThat("cbVorgang closed status incorrect", cbVorgang.isClosed(), equalTo(closed));
    }

    private void assertUserTaskStatusIsClosed(boolean closed) throws Exception {
        TamUserTask tamUserTask = cbVorgang.getTamUserTask();
        if (tamUserTask == null) {
            return;
        }
        if (closed) {
            verify(witaUsertaskService).closeUserTask(cbVorgang.getTamUserTask());
        }
        verifyNoMoreInteractions(witaUsertaskService);
    }

    @SuppressWarnings("unchecked")
    @DataProvider
    public Object[][] dataProviderDoNotIgnoreAfterClosedWorkflow() {
        AbbruchMeldung abbm = new AbbruchMeldungBuilder().withAenderungsKennzeichen(STANDARD).build();
        ErledigtMeldung erlm = new ErledigtMeldungBuilder().withAenderungsKennzeichen(STANDARD).build();
        EntgeltMeldung entm = new EntgeltMeldungBuilder().withAenderungsKennzeichen(STANDARD).build();

        QualifizierteEingangsBestaetigung qeb2 = new QualifizierteEingangsBestaetigungBuilder().build();
        AuftragsBestaetigungsMeldung abm2 = new AuftragsBestaetigungsMeldungBuilder().build();
        ErledigtMeldung erlm2 = new ErledigtMeldungBuilder().build();
        EntgeltMeldung entm2 = new EntgeltMeldungBuilder().build();

        // @formatter:off
        return new Object[][] {
                { asList(abbm), STORNO, qeb2 },
                { asList(abbm), STORNO, abm2 },
                { asList(abbm), STORNO, erlm2 },
                { asList(abbm), STORNO, entm2 },

                { asList(abbm), TERMINVERSCHIEBUNG, qeb2 },
                { asList(abbm), TERMINVERSCHIEBUNG, abm2 },
                { asList(abbm), TERMINVERSCHIEBUNG, erlm2 },
                { asList(abbm), TERMINVERSCHIEBUNG, entm2 },

                { asList(erlm, entm), STORNO, qeb2 },
                { asList(erlm, entm), STORNO, abm2 },
                { asList(erlm, entm), STORNO, erlm2 },
                { asList(erlm, entm), STORNO, entm2 },

                { asList(erlm, entm), TERMINVERSCHIEBUNG, qeb2 },
                { asList(erlm, entm), TERMINVERSCHIEBUNG, abm2 },
                { asList(erlm, entm), TERMINVERSCHIEBUNG, erlm2 },
                { asList(erlm, entm), TERMINVERSCHIEBUNG, entm2 },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderDoNotIgnoreAfterClosedWorkflow", expectedExceptions = WitaBpmException.class, expectedExceptionsMessageRegExp = "Konnte keinen aktiven WITA-Workflow mit dem Business-Key: .* finden\\. Wahrscheinlich wurde der Workflow bereits abgeschlossen oder abgebrochen\\.")
    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public <T extends TalOrderMeldung & IncomingTalOrderMeldung, V extends Meldung<POS> & TalOrderMeldung, POS extends MeldungsPosition> void testDoNotIgnoreAfterClosedWorkflow(
            List<V> meldungenToCloseWorkflow, AenderungsKennzeichen aenderungsKennzeichen, T message)
            throws FindException {
        startProcessInstance();
        message.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());
        sendQeb();
        sendAbm();

        if (aenderungsKennzeichen == STORNO) {
            sendStorno();
        }
        else if (aenderungsKennzeichen == TERMINVERSCHIEBUNG) {
            sendTv();
        }
        assertProcessInstanceInState(WAIT_FOR_MESSAGE);

        for (V meldung : meldungenToCloseWorkflow) {
            meldung.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());
            doReturn(meldung).when(mwfEntityDao).findById(1L, meldung.getClass());
            underTest.handleWitaMessage(meldung);
        }
        assertProcessInstanceFinished();

        when(cbVorgangDao.findCBVorgangByCarrierRefNr(any(String.class))).thenReturn(cbVorgang);

        Mockito.reset(mwfEntityDao);
        underTest.handleWitaMessage(message);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testStorno() throws Exception {
        startProcessInstance();

        sendQeb();
        sendStorno();

        verify(lineOrderService, times(1)).sendToWita(any(Storno.class));
//        verify(lineOrderService, times(2)).sendToWita(any(WitaMessage.class));
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testEntmOnStorno() throws Exception {
        startProcessInstance();
        sendQeb();

        sendStorno();

        sendErlm(AenderungsKennzeichen.STORNO, WorkflowTaskName.WAIT_FOR_ENTM);

    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testErlmOnRexMkShouldCloseWorkflow() throws Exception {
        startProcessInstance();
        sendQeb();
        sendAbm();

        ErledigtMeldung erlm = new ErledigtMeldungBuilder().withExterneAuftragsnummer(cbVorgang.getCarrierRefNr())
                .withGeschaeftsfallTyp(GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG)
                .withAenderungsKennzeichen(STANDARD).build();
        when(mwfEntityDao.findById(any(Long.class), eq(ErledigtMeldung.class))).thenReturn(erlm);
        underTest.handleWitaMessage(erlm);

        assertProcessInstanceFinished();

    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testTerminVerschiebung() throws Exception {
        startProcessInstance();
        sendQeb();
        sendTv();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testStornoWithErlmForAuftrag() throws Exception {
        startProcessInstance();
        sendQeb();
        sendAbm();
        sendStorno();
        cbVorgang.setAenderungsKennzeichen(STORNO);

        sendErlm(AenderungsKennzeichen.STANDARD);
        assertEquals(cbVorgang.getAenderungsKennzeichen(), STANDARD);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testStornoWithErlmForStorno() throws Exception {
        startProcessInstance();
        sendQeb();
        sendStorno();
        cbVorgang.setAenderungsKennzeichen(STORNO);

        sendErlm(AenderungsKennzeichen.STORNO);
        assertTrue(cbVorgang.isStorno());
    }

    private TerminAnforderungsMeldung sendTam() {
        TerminAnforderungsMeldung tam = new TerminAnforderungsMeldungBuilder().withExterneAuftragsnummer(
                cbVorgang.getCarrierRefNr()).build();
        when(mwfEntityDao.findById(any(Long.class), eq(TerminAnforderungsMeldung.class))).thenReturn(tam);

        underTest.handleWitaMessage(tam);

        assertProcessInstanceInState(PROCESS_TAM);
//        verify(mwfEntityDao).store(tam);
//        reset(mwfEntityDao);
        return tam;
    }

}
