/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2011 11:36:24
 */
package de.mnet.wita.bpm.tasks;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.activiti.engine.delegate.DelegateExecution;
import org.mockito.Mock;
import org.testng.annotations.Test;

import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.bpm.converter.usertask.AbbmPvUserTaskConverter;
import de.mnet.wita.bpm.converter.usertask.AbmPvUserTaskConverter;
import de.mnet.wita.exceptions.MessageOutOfOrderException;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;

@Test(groups = UNIT)
public class ProcessPvMessageTaskTest extends AbstractProcessingWitaTaskTest<ProcessPvMessageTask> {

    @Mock
    private AbbmPvUserTaskConverter abbmPvUserTaskConverter = new AbbmPvUserTaskConverter();
    @Mock
    private AbmPvUserTaskConverter abmPvUserTaskConverter = new AbmPvUserTaskConverter();

    public ProcessPvMessageTaskTest() {
        super(ProcessPvMessageTask.class);
    }

    public void testAbmPv() throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.ABM_PV);
        underTest.execute(execution);

        verify(mwfEntityDao).findById(anyInt(), eq(AuftragsBestaetigungsMeldungPv.class));
        verify(abmPvUserTaskConverter).write(any(AuftragsBestaetigungsMeldungPv.class));
        verifyZeroInteractions(workflowTaskService);
    }

    public void testAbbmPv() throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.ABBM_PV);
        underTest.execute(execution);

        verify(mwfEntityDao).findById(anyInt(), eq(AbbruchMeldungPv.class));
        verify(abbmPvUserTaskConverter).write(any(AbbruchMeldungPv.class));
        verifyZeroInteractions(workflowTaskService);
    }

    public void testErlmPv() throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.ERLM_PV);
        underTest.execute(execution);
        verifyZeroInteractions(workflowTaskService);
    }

    @Test(expectedExceptions = MessageOutOfOrderException.class)
    public void testEntmPv() throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.ENTM_PV);
        underTest.execute(execution);
    }

    public void testAbm() throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.ABM);
        underTest.execute(execution);
        verify(workflowTaskService).setWorkflowToError(eq(execution), anyString());
    }

    private DelegateExecution createExecution(MeldungsType messageType) throws FindException {
        return createExecution(witaCBVorgang, messageType, 1L, null, false);
    }
}
