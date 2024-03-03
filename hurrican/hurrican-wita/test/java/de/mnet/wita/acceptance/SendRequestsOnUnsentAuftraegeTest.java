/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2011 10:28:00
 */
package de.mnet.wita.acceptance;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wita.WitaSimulatorTestUser.*;
import static de.mnet.wita.bpm.WorkflowTaskName.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.acceptance.common.AbstractWitaAcceptanceBaseTest;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.WorkflowTaskName;

/**
 * Akzpeptanztest fuer Storno und Terminverschiebung von vogehaltenen (noch nicht gesendendeten) WITA-Auftraegen.
 * <p/>
 * TV -> Aendert das Vorgabedatum des Ursprungsauftrags ab.
 * <p/>
 * Storno -> Beendet den Workflow.
 */
@Test(groups = ACCEPTANCE)
public class SendRequestsOnUnsentAuftraegeTest extends AbstractWitaAcceptanceBaseTest {

    @Autowired
    private CommonWorkflowService commonWorkflowService;

    public void sendStornoInPassiveMode() throws Exception {
        AcceptanceTestWorkflow workflow = sendNeubestellungIn2Jahren();
        assertProcessInstanceInState(workflow, WAIT_FOR_MESSAGE);

        workflow.sendStorno();
        assertTrue(commonWorkflowService.isProcessInstanceFinished(workflow.getCbVorgang().getBusinessKey()));
    }

    public void sendTvForUnsentNeubestellung() throws Exception {
        AcceptanceTestWorkflow workflow = sendNeubestellungIn2Jahren();
        assertProcessInstanceInState(workflow, WAIT_FOR_MESSAGE);
        Date vorgabe1 = workflow.getCbVorgang().getVorgabeMnet();

        workflow.sendTv();
        assertProcessInstanceInState(workflow, WAIT_FOR_MESSAGE);
        Date vorgabe2 = workflow.getCbVorgang().getVorgabeMnet();
        assertTrue(vorgabe2.after(vorgabe1));
    }

    private AcceptanceTestWorkflow sendNeubestellungIn2Jahren() throws Exception {
        AcceptanceTestWorkflow workflow = accTestWorkflowProvider.get();
        AcceptanceTestDataBuilder builder = testDataBuilderProvider.get().withHurricanProduktId(Produkt.AK_CONNECT)
                .withUserName(TAL_BEREIT_NEU_02AA).withVorgabeMnet(getNextWorkingDayIn2Years().atStartOfDay());
        workflow.sendBereitstellung(builder);
        return workflow;
    }

    private void assertProcessInstanceInState(AcceptanceTestWorkflow workflow, WorkflowTaskName state)
            throws FindException {
        ProcessInstance pi = commonWorkflowService.retrieveProcessInstance(workflow.getCbVorgang().getBusinessKey());
        assertNotNull(pi);
        assertTrue(pi instanceof ExecutionEntity);
        assertEquals(((ExecutionEntity) pi).getActivityId(), state.id);
    }

    private LocalDate getNextWorkingDayIn2Years() {
        LocalDate in2years = LocalDate.now().plusYears(2);
        while (!DateCalculationHelper.isWorkingDay(in2years)) {
            in2years = in2years.plusDays(1);
        }
        return in2years;
    }

}
