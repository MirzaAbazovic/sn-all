/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.07.2015
 */
package de.mnet.wita.acceptance.common.role;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.inject.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.Assert;

import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.AbbmMeldungsCode;
import de.mnet.wita.AbbmPvMeldungsCode;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.citrus.actions.AbstractWitaWorkflowTestAction;
import de.mnet.wita.citrus.actions.CreateCbVorgangTestAction;
import de.mnet.wita.citrus.actions.SelectWitaWorkflowTestAction;
import de.mnet.wita.citrus.actions.SendWitaBereitstellungTestAction;
import de.mnet.wita.citrus.actions.SendWitaLmaeTestAction;
import de.mnet.wita.citrus.actions.SendWitaSerPowTestAction;
import de.mnet.wita.citrus.actions.WaitForAbbmPvTestAction;
import de.mnet.wita.citrus.actions.WaitForEntmPvWithWorkflowErrorTestAction;
import de.mnet.wita.citrus.actions.WaitForEntmWithWorkflowErrorTestAction;
import de.mnet.wita.citrus.actions.WaitForErlmPvWithWorkflowErrorTestAction;
import de.mnet.wita.citrus.actions.WaitForErlmWithWorkflowErrorTestAction;
import de.mnet.wita.citrus.actions.WaitForNonClosingAbbmTestAction;
import de.mnet.wita.citrus.actions.WaitForWitaNotificationTestAction;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.common.portierung.RufnummernPortierungEinzelanschluss;
import de.mnet.wita.service.WitaTalOrderService;

@SuppressWarnings("Duplicates")
@Component
public class WorkflowTestRole extends AbstractTestRole {

    @Autowired
    protected Provider<AcceptanceTestDataBuilder> testDataBuilder;
    @Autowired
    protected Provider<AcceptanceTestWorkflow> accTestWorkflow;
    @Autowired
    private WitaTalOrderService talOrderService;
    @Autowired
    private CommonWorkflowService commonWorkflowService;
    @Autowired
    private AKLoginContext akLoginContext;

    public AcceptanceTestWorkflow get() {
        return accTestWorkflow.get();
    }

    public AcceptanceTestDataBuilder getTestDataBuilder() {
        return testDataBuilder.get();
    }

    public CreateCbVorgangTestAction send(CreatedData createdData, Long cbVorgangTyp) {
        CreateCbVorgangTestAction action = new CreateCbVorgangTestAction(createdData, cbVorgangTyp);
        testBuilder.action(action);
        return action;
    }

    public SendWitaBereitstellungTestAction sendBereitstellung(WitaSimulatorTestUser useCase) {
        AcceptanceTestDataBuilder testData = testDataBuilder.get().withUserName(useCase);
        testBuilder.variable(VariableNames.TEST_WORKFLOW, accTestWorkflow.get());

        SendWitaBereitstellungTestAction action = new SendWitaBereitstellungTestAction(testData);
        testBuilder.action(action);
        return action;
    }

    public SendWitaSerPowTestAction sendSerPow(WitaSimulatorTestUser useCase) {
        AcceptanceTestDataBuilder testData = testDataBuilder.get().withUserName(useCase);
        testBuilder.variable(VariableNames.TEST_WORKFLOW, accTestWorkflow.get());

        SendWitaSerPowTestAction action = new SendWitaSerPowTestAction(testData);
        testBuilder.action(action);
        return action;
    }

    public SendWitaLmaeTestAction sendLmae(WitaSimulatorTestUser useCase) {
        AcceptanceTestDataBuilder testData = testDataBuilder.get().withUserName(useCase);
        testBuilder.variable(VariableNames.TEST_WORKFLOW, accTestWorkflow.get());

        SendWitaLmaeTestAction action = new SendWitaLmaeTestAction(testData);
        testBuilder.action(action);
        return action;
    }

    /**
     * Send new Wita AEN-LAE via test action.
     */
    public AbstractWitaWorkflowTestAction sendLae (AcceptanceTestDataBuilder builder, Auftragsposition.ProduktBezeichner expectedProduktBezeichner) throws Exception {
        AbstractWitaWorkflowTestAction workflowTestAction = new AbstractWitaWorkflowTestAction("sendLae") {
            @Override
            public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                workflow.sendLae(builder,expectedProduktBezeichner);
                testContext.getVariables().put(VariableNames.TEST_DATA, workflow.getCreatedData());
            }
        };

        testBuilder.action(workflowTestAction);
        return workflowTestAction;
    }

    /**
     * Send new Wita Bereitstellung via test action.
     */
    public SendWitaBereitstellungTestAction sendBereitstellungWithLageplanAndKundenauftrag(WitaSimulatorTestUser useCase) {
        AcceptanceTestDataBuilder testData = testDataBuilder.get()
                .withLageplan()
                .withKundenauftrag();
        testBuilder.variable(VariableNames.TEST_WORKFLOW, accTestWorkflow.get());

        SendWitaBereitstellungTestAction action = new SendWitaBereitstellungTestAction(testData.withUserName(useCase));
        testBuilder.action(action);
        return action;
    }

    /**
     * Wait for QEB notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForQEB() {
        return waitForNotification(MeldungsType.QEB);
    }

    /**
     * Wait for VZM notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForVZM() {
        return waitForNotification(MeldungsType.VZM);
    }

    /**
     * Wait for VZM notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForVzmPv() {
        return waitForNotification(MeldungsType.VZM_PV);
    }

    /**
     * Wait for QEB notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForQEB(CBVorgang cbVorgang) {
        return waitForNotification(MeldungsType.QEB, cbVorgang);
    }

    /**
     * Wait for ABM notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForABM() {
        return waitForNotification(MeldungsType.ABM);
    }

    /**
     * Wait for TAM notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForTAM() {
        return waitForNotification(MeldungsType.TAM);
    }

    /**
     * Wait for MTAM notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForMTAM() {
        return waitForNotification(MeldungsType.TAM);
    }

    /**
     * Wait for ABBM notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForABBM() {
        return waitForNotification(MeldungsType.ABBM);
    }

    public WaitForNonClosingAbbmTestAction waitForNonClosingAbbm(AbbmMeldungsCode abbmMeldungsCode) {
        WaitForNonClosingAbbmTestAction action = new WaitForNonClosingAbbmTestAction(abbmMeldungsCode);
        testBuilder.action(action);
        return action;
    }

    /**
     * Wait for ERLM notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForERLM() {
        return waitForNotification(MeldungsType.ERLM);
    }

    public WaitForErlmWithWorkflowErrorTestAction waitForErlmInWorkflowError() {
        WaitForErlmWithWorkflowErrorTestAction action = new WaitForErlmWithWorkflowErrorTestAction();
        testBuilder.action(action);
        return action;
    }

    public WaitForEntmWithWorkflowErrorTestAction waitForEntmInWorkflowError() {
        WaitForEntmWithWorkflowErrorTestAction action = new WaitForEntmWithWorkflowErrorTestAction();
        testBuilder.action(action);
        return action;
    }

    /**
     * Wait for ENTM notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForENTM() {
        return waitForNotification(MeldungsType.ENTM);
    }

    /**
     * Wait for AKM-PV notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForAkmPv() {
        return waitForNotification(MeldungsType.AKM_PV);
    }

    /**
     * Wait for ABM_PV notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForAbmPv() {
        return waitForNotification(MeldungsType.ABM_PV);
    }

    /**
     * Wait for ABM_PV notification with workflow test action.
     */
    public AbstractWitaWorkflowTestAction waitForSecondAbmPv() {
        AbstractWitaWorkflowTestAction workflowTestAction = new AbstractWitaWorkflowTestAction("sendTv") {
            @Override
            public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                workflow.waitForSecondAbmPv();
            }
        };

        return workflowTestAction;
    }

    public WaitForAbbmPvTestAction waitForAbbmPv(AbbmPvMeldungsCode abbmPvMeldungsCode) {
        WaitForAbbmPvTestAction action = new WaitForAbbmPvTestAction(abbmPvMeldungsCode);
        testBuilder.action(action);
        return action;
    }

    /**
     * Wait for ERLM_PV notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForErlmPv() {
        return waitForNotification(MeldungsType.ERLM_PV);
    }

    public WaitForErlmPvWithWorkflowErrorTestAction waitForErlmPvInWorkflowError() {
        WaitForErlmPvWithWorkflowErrorTestAction action = new WaitForErlmPvWithWorkflowErrorTestAction();
        testBuilder.action(action);
        return action;
    }

    /**
     * Wait for ENTM_PV notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForEntmPv() {
        return waitForNotification(MeldungsType.ENTM_PV);
    }

    public WaitForEntmPvWithWorkflowErrorTestAction waitForEntmPvInWorkflowError() {
        WaitForEntmPvWithWorkflowErrorTestAction action = new WaitForEntmPvWithWorkflowErrorTestAction();
        testBuilder.action(action);
        return action;
    }

    /**
     * Wait for WITA notification with workflow test action.
     */
    public WaitForWitaNotificationTestAction waitForNotification(MeldungsType meldungsType) {
        WaitForWitaNotificationTestAction action = new WaitForWitaNotificationTestAction(meldungsType);
        testBuilder.action(action);
        return action;
    }

    /**
     * Wait for WITA notification with explicit cbVorgang test action.
     */
    public WaitForWitaNotificationTestAction waitForNotification(MeldungsType meldungsType, CBVorgang cbVorgang) {
        WaitForWitaNotificationTestAction action = new WaitForWitaNotificationTestAction(meldungsType);
        testBuilder.action(action);
        return action;
    }

    /**
     * Sends new Terminverschiebung with workflow instance in a test action.
     */
    public AbstractWitaWorkflowTestAction sendTv() {
        AbstractWitaWorkflowTestAction workflowTestAction = new AbstractWitaWorkflowTestAction("sendTv") {
            @Override
            public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                workflow.sendTv();
            }
        };

        testBuilder.action(workflowTestAction);
        return workflowTestAction;
    }

    // TODO: Refactor
    /**
     * Sends new Terminverschiebung with workflow instance in a test action.
     */
    public AbstractWitaWorkflowTestAction sendTv(LocalDate tvDate) {
        AbstractWitaWorkflowTestAction workflowTestAction = new AbstractWitaWorkflowTestAction("sendTv") {
            @Override
            public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                workflow.sendTv(tvDate);
            }
        };

        testBuilder.action(workflowTestAction);
        return workflowTestAction;
    }

    /**
     * Sends new VBL with workflow instance in a test action.
     */

    public AbstractWitaWorkflowTestAction sendVbl(AcceptanceTestDataBuilder builder) throws Exception {
        AbstractWitaWorkflowTestAction workflowTestAction = new AbstractWitaWorkflowTestAction("sendVbl") {
            @Override
            public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                workflow.sendVbl(builder);
                testContext.getVariables().put(VariableNames.TEST_DATA, workflow.getCreatedData());
            }
        };

        testBuilder.action(workflowTestAction);
        return workflowTestAction;
    }

    /**
     * Sends new Kuendigung with workflow instance in a test action.
     */

    public AbstractWitaWorkflowTestAction sendKueKd(AcceptanceTestDataBuilder builder) throws Exception {
        AbstractWitaWorkflowTestAction workflowTestAction = new AbstractWitaWorkflowTestAction("sendKueKd") {
            @Override
            public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                workflow.sendKueKd(builder);
                testContext.getVariables().put(VariableNames.TEST_DATA, workflow.getCreatedData());
            }
        };

        testBuilder.action(workflowTestAction);
        return workflowTestAction;
    }


    /**
     * Sends new RUEM-PV with workflow instance in a test action.
     */
    public AbstractWitaWorkflowTestAction sendRuemPv() {
        AbstractWitaWorkflowTestAction workflowTestAction = new AbstractWitaWorkflowTestAction("sendRuemPv") {
            @Override
            public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                workflow.sendRuemPv();
            }
        };

        testBuilder.action(workflowTestAction);
        return workflowTestAction;
    }

    public AbstractWitaWorkflowTestAction sendNegativeRuemPv(RuemPvAntwortCode antwortCode, String antwortText)
            throws Exception {
        AbstractWitaWorkflowTestAction workflowTestAction = new AbstractWitaWorkflowTestAction("sendRuemPv") {
            @Override
            public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                workflow.sendNegativeRuemPv(antwortCode, antwortText);
            }
        };

        testBuilder.action(workflowTestAction);
        return workflowTestAction;
    }


    /**
     * Provides Citrus test action access to Wita test workflow.
     */
    public AbstractWitaWorkflowTestAction doWithWorkflow(AbstractWitaWorkflowTestAction.WorkflowCallback callback) {
        AbstractWitaWorkflowTestAction workflowTestAction = new AbstractWitaWorkflowTestAction("workflowAction") {
            @Override
            public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                callback.doWithWorkflow(workflow);
            }
        };

        testBuilder.action(workflowTestAction);
        return workflowTestAction;
    }

    /**
     * Triggers new storno on cbVorgang.
     */
    public void sendStorno() {
        testBuilder.action(new AbstractWitaWorkflowTestAction("sendStorno") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                try {
                    talOrderService.doStorno(workflow.getCbVorgangId(), akLoginContext.getUser());
                }
                catch (Exception e) {
                    throw new CitrusRuntimeException("Failed to send storno on cbVorgang", e);
                }
            }
        });
    }

    /**
     * Saves workflow instance to test variables via test action. This has the effect that next test action will operate
     * workflow actions on this particular workflow instance. This way test case can work with multiple workflow
     * instances at the same time.
     */
    public WorkflowTestRole select(AcceptanceTestWorkflow workflow) {
        testBuilder.action(new SelectWitaWorkflowTestAction(workflow));
        return this;
    }

    public void assertWorkflowClosed() throws Exception {
        testBuilder.action(new AbstractWitaWorkflowTestAction("assertWorkflowState") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                assertTrue(commonWorkflowService.isProcessInstanceFinished(workflow.getCbVorgang().getBusinessKey()),
                        "Workflow not closed! RefId: " + workflow.getCbVorgang().getCarrierRefNr());
            }
        });
    }

    /**
     * Checks that workflow is in expected state.
     */
    public void assertWorkflowState(String state) {
        testBuilder.action(new AbstractWitaWorkflowTestAction("assertWorkflowState") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                Assert.assertEquals(commonWorkflowService.getWorkflowState(workflow.getCbVorgang().getBusinessKey()),
                        state, "Failed to validate workflow state");
            }
        });
    }

    /**
     * Checks that workflow has a Bestandssuche.
     */
    public void assertBestandssucheSet() {
        testBuilder.action(new AbstractWitaWorkflowTestAction("assertWorkflowState") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                assertNotNull(workflow.getAuftrag().getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt()
                        .getBestandsSuche());
            }
        });
    }

    /**
     * Checks that workflow did Portierung EinzelanschlussSet
     */
    public void assertRufnummernPortierungEinzelanschlussSet(int anzahlDns) {
        testBuilder.action(new AbstractWitaWorkflowTestAction("assertWorkflowState") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                RufnummernPortierung rufnummernPortierung = workflow.getAuftrag().getGeschaeftsfall().getAuftragsPosition()
                        .getGeschaeftsfallProdukt().getRufnummernPortierung();

                assertNotNull(rufnummernPortierung);
                assertTrue(rufnummernPortierung instanceof RufnummernPortierungEinzelanschluss);

                RufnummernPortierungEinzelanschluss rufnummernPortierungEinzelanschluss = (RufnummernPortierungEinzelanschluss) rufnummernPortierung;
                assertThat(rufnummernPortierungEinzelanschluss.getRufnummern(), hasSize(anzahlDns));
            }
        });
    }

    /**
     * Checks the workflow alive state.
     */
    public void assertProcessInstanceAlive(boolean alive) {
        testBuilder.action(new AbstractWitaWorkflowTestAction("assertProcessInstanceAlive") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                if (commonWorkflowService.isProcessInstanceAlive(workflow.getCbVorgang().getBusinessKey()) != alive) {
                    throw new CitrusRuntimeException("Workflow alive state not as expected! RefId: " + workflow.getCbVorgang().getCarrierRefNr() +
                            " workflow state is: " + commonWorkflowService.getWorkflowState(workflow.getCbVorgang().getBusinessKey()));
                }
            }
        });
    }

    /**
     * Checks the workflow Uebertragungsverfahren.
     */
    private void assertUebertragungsverfahrenSet(Uebertragungsverfahren expected) {
        testBuilder.action(new AbstractWitaWorkflowTestAction("assertProcessInstanceAlive") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                assertNotNull(workflow.getAuftrag().getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt()
                        .getSchaltangaben());
                List<SchaltungKupfer> schaltungKupferList = workflow.getAuftrag().getGeschaeftsfall().getAuftragsPosition()
                        .getGeschaeftsfallProdukt().getSchaltangaben().getSchaltungKupfer();
                assertNotNull(schaltungKupferList);
                SchaltungKupfer schaltungKupfer = schaltungKupferList.get(0);

                if (expected == null) {
                    assertNotNull(schaltungKupfer.getUebertragungsverfahren());
                }
                else {
                    assertEquals(schaltungKupfer.getUebertragungsverfahren(), expected);
                }
            }
        });

    }

    public void assertUebertragungsverfahrenSet() {
        assertUebertragungsverfahrenSet(null);
    }
}
