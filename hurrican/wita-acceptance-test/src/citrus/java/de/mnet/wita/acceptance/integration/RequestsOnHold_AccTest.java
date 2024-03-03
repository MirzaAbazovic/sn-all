/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.01.2012 10:17:40
 */
package de.mnet.wita.acceptance.integration;

import static de.mnet.wita.WitaSimulatorTestUser.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.apache.log4j.Logger;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.WitaMessageAssertionTestService;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.bpm.WorkflowTaskName;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.UserTask;
import de.mnet.wita.service.WitaConfigService;

/**
 * Acceptance-Test, um das verzoegerte Senden von Nachrichten zu testen.
 * <p/>
 * Transformation von de.mnet.wita.acceptance.integration.RequestsOnHoldAccTest nach Citrus
 */
@Test(groups = BaseTest.ACCEPTANCE_INTEGRATION)
public class RequestsOnHold_AccTest extends AbstractWitaAcceptanceTest {

    private static final Logger LOGGER = Logger.getLogger(RequestsOnHold_AccTest.class);

    @Autowired
    private WitaConfigService witaConfigService;

    @Autowired
    private WitaMessageAssertionTestService messageAssertions;

    @AfterMethod(alwaysRun = true)
    public void resetMinutesWhileRequestIsOnHold() {
        if (witaConfigService != null) {
            witaConfigService.saveMinutesWhileRequestIsOnHold("0");
        }
    }


    @CitrusTest(name = "SendWithConfiguredNoWait")
    public void testSendWithConfiguredNowait() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM, WitaCdmVersion.V1);
        workflow().select(workflow().get()).sendBereitstellung(NEU_QEB_ABM);
        workflow().doWithWorkflow(wf -> messageAssertions.assertIoArchiveEntryReceived(wf.getCbVorgang()));
    }

    @CitrusTest(name = "SendWithConfiguredWaitOneMinute")
    public void testSendWithConfiguredWaitOneMinute() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM, WitaCdmVersion.V1);
        workflow().doWithWorkflow(wf -> witaConfigService.saveMinutesWhileRequestIsOnHold("1"));
        workflow().select(workflow().get()).sendBereitstellung(NEU_QEB_ABM);
        workflow().doWithWorkflow(wf -> messageAssertions.assertNoIoArchiveEntryReceived(wf.getCbVorgang()));
    }

    /**
     * Test der eine vorgehaltene TV (Terminverschiebung) schickt, sie dann storniert bevor sie rausgeht und dann noch
     * einmal eine TV schickt.
     */
    @CitrusTest(name = "tvOnHoldAndStornoOnIt")
    public void testTvOnHoldAndStornoOnIt() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_23, WitaCdmVersion.V1);

        workflow().select(workflow().get()).sendBereitstellung(WitaSimulatorTestUser.TAL_BEREIT_NEU_23);
        atlas().receiveOrder("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        workflow().doWithWorkflow(wf -> wf.closeCbVorgang());

        atlas().sendNotification("TAM");
        workflow().waitForTAM();

        workflow().doWithWorkflow(wf -> wf.assertStateAndCbVorgang(WorkflowTaskName.PROCESS_TAM, CBVorgang.STATUS_CLOSED,
                AenderungsKennzeichen.STANDARD, UserTask.UserTaskStatus.OFFEN));

        //Create vorgehaltene TV, so do not send next request immediately
        workflow().doWithWorkflow(wf -> witaConfigService.saveMinutesWhileRequestIsOnHold("1"));

        workflow().sendTv();
        workflow().doWithWorkflow(wf -> wf.assertStateAndCbVorgang(WorkflowTaskName.PROCESS_TAM, CBVorgang.STATUS_SUBMITTED,
                AenderungsKennzeichen.TERMINVERSCHIEBUNG, UserTask.UserTaskStatus.GESCHLOSSEN));

        workflow().sendStorno();
        workflow().doWithWorkflow(wf -> wf.assertStateAndCbVorgang(WorkflowTaskName.PROCESS_TAM, CBVorgang.STATUS_CLOSED,
                AenderungsKennzeichen.STANDARD, UserTask.UserTaskStatus.OFFEN));

        // send next request immediately
        workflow().doWithWorkflow(wf -> witaConfigService.saveMinutesWhileRequestIsOnHold("0"));

        workflow().sendTv();
        atlas().receiveNotification("TV").extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(),
                VariableNames.REQUESTED_CUSTOMER_DATE);
        workflow().doWithWorkflow(wf -> wf.assertStateAndCbVorgangWithRetry(WorkflowTaskName.WAIT_FOR_MESSAGE, CBVorgang.STATUS_TRANSFERRED,
                AenderungsKennzeichen.TERMINVERSCHIEBUNG, UserTask.UserTaskStatus.GESCHLOSSEN, 10));

        atlas().sendNotification("ABM2");
        workflow().waitForABM();

        workflow().doWithWorkflow(wf -> wf.closeCbVorgang());

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> wf.assertWorkflowState("end"));
    }

}
