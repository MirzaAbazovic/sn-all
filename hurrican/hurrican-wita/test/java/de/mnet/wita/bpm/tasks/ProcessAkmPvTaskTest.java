/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2012 11:27:03
 */
package de.mnet.wita.bpm.tasks;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;

@Test(groups = BaseTest.UNIT)
public class ProcessAkmPvTaskTest extends AbstractProcessingWitaTaskTest<ProcessAkmPvTask> {

    private static final long AKM_PV_ID = 1871584585425L;

    private AnkuendigungsMeldungPv akmPv;

    public ProcessAkmPvTaskTest() {
        super(ProcessAkmPvTask.class);
    }

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        akmPv = (new AnkuendigungsMeldungPvBuilder()).withId(AKM_PV_ID).build();
        when(mwfEntityDao.findById(AKM_PV_ID, AnkuendigungsMeldungPv.class)).thenReturn(akmPv);
    }

    public void testProcessMessageOnNormalAkmPvCreatesUserTask() throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.AKM_PV);

        when(workflowTaskService.validateMwfInput(akmPv, execution)).thenReturn(true);
        when(witaUsertaskService.getAutomaticAnswerForAkmPv(akmPv)).thenReturn(null);

        underTest.processMessage(execution);

        verify(witaUsertaskService).createAkmPvUserTask(akmPv);
    }

    public void testProcessMessageOnNormalAkmPvAnswersAutomaticallyWithRuemPv() throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.AKM_PV);

        when(workflowTaskService.validateMwfInput(akmPv, execution)).thenReturn(true);
        Map<WitaTaskVariables, Object> variables = new HashMap<>();
        variables.put(WitaTaskVariables.RUEM_PV_ANTWORTCODE, RuemPvAntwortCode.OK);
        when(witaUsertaskService.getAutomaticAnswerForAkmPv(akmPv)).thenReturn(variables);

        underTest.processMessage(execution);

        assertEquals(execution.getVariable(WitaTaskVariables.WITA_MESSAGE_TYPE.id), MeldungsType.RUEM_PV.name());
        assertEquals(execution.getVariable(WitaTaskVariables.RUEM_PV_ANTWORTCODE.id), RuemPvAntwortCode.OK);
        verify(witaUsertaskService, never()).createAkmPvUserTask(akmPv);
    }

    public void testProcessMessageOnNotAkmShouldSentToWorkflowError() throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.ABM_PV);

        underTest.processMessage(execution);

        verify(workflowTaskService).setWorkflowToError(eq(execution),
                matches("Wrong MeldungsTyp.+" + MeldungsType.ABM_PV + ".+Expected MeldungsType.+AKM-PV.+"));
        verify(witaUsertaskService, never()).createAkmPvUserTask(any(AnkuendigungsMeldungPv.class));
    }

    public void testProcessMessageOnNotValidAkmShouldDoNothing() throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.AKM_PV);

        when(workflowTaskService.validateMwfInput(akmPv, execution)).thenReturn(false);

        underTest.processMessage(execution);

        verify(workflowTaskService).validateMwfInput(akmPv, execution);
        verifyNoMoreInteractions(workflowTaskService, witaUsertaskService);
    }

    private DelegateExecution createExecution(MeldungsType messageType) throws Exception {
        return createExecution(null, messageType, AKM_PV_ID, null, false);
    }
}
