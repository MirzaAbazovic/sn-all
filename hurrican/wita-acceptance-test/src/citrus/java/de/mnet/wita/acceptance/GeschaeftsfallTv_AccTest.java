/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2015
 */
package de.mnet.wita.acceptance;

import static de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus.TV_60_TAGE;
import static de.mnet.wita.service.WitaCheckConditionService.*;

import java.time.*;
import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.bpm.WorkflowTaskName;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.citrus.actions.AbstractWitaWorkflowTestAction;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.service.WitaTalOrderService;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class GeschaeftsfallTv_AccTest extends AbstractWitaAcceptanceTest {

    @Autowired
    private WitaTalOrderService talOrderService;

    /**
     * Stellt sicher, dass die Status beim Senden einer Terminverschiebung passen und dass eine TV nach einer ERLM nicht
     * mehr gesendet werden kann.
     */
    @CitrusTest(name = "GeschaeftsfallTv_TvAfterErlmNotPossible_AccTest")
    public void testTvStatusAndTvAfterErlmNotPossible() {
        useCase(WitaAcceptanceUseCase.NEU_QEB_TV_ABM_ERLM_TV_ABBM, getWitaVersionForAcceptanceTest());

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_QEB_TV_ABM_ERLM_TV_ABBM);

        atlas().receiveOrder("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        workflow().assertWorkflowState(WorkflowTaskName.WAIT_FOR_MESSAGE.id);

        action(new AbstractWitaWorkflowTestAction("saveVorgabeDate") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                testContext.setVariable(VariableNames.REQUESTED_CUSTOMER_DATE, workflow.getCbVorgang().getVorgabeMnet());
            }
        });

        workflow().sendTv();
        atlas().receiveNotification("TV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE_TV);

        action(new AbstractWitaWorkflowTestAction("checkVorgabeDate") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                Date vorgabeBeforeTv = (Date) testContext.getVariables().get(VariableNames.REQUESTED_CUSTOMER_DATE);

                if (!workflow.getCbVorgang().getVorgabeMnet().after(vorgabeBeforeTv)) {
                    throw new CitrusRuntimeException("Vorgabe M-net date was not adjusted with TV");
                }
            }
        });

        workflow().assertWorkflowState(WorkflowTaskName.WAIT_FOR_MESSAGE.id);

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        workflow().assertWorkflowState(WorkflowTaskName.WAIT_FOR_ENTM.id);

        action(new AbstractWitaWorkflowTestAction("doTerminverschiebung") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                try {
                    talOrderService.doTerminverschiebung(workflow.getCbVorgangId(), LocalDate.now(),
                            workflow.getCreatedData().user, false, null, TV_60_TAGE);
                    throw new CitrusRuntimeException("Missing exception due to invalid TV after ERLM");
                }
                catch (Exception e) {
                    Assert.assertEquals(e.getClass(), WitaUserException.class);
                    Assert.assertEquals(e.getMessage(), String.format(TV_ONLY_BEFORE_ERLM, workflow.getCbVorgang().getCarrierRefNr()));
                }
            }
        });

        workflow().assertProcessInstanceAlive(true);
        workflow().assertWorkflowState(WorkflowTaskName.WAIT_FOR_ENTM.id);
    }
}
