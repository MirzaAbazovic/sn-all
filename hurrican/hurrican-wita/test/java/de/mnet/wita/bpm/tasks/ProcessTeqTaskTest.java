/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2011 09:28:18
 */
package de.mnet.wita.bpm.tasks;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.hurrican.model.cc.tal.CBVorgang.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.activiti.engine.delegate.DelegateExecution;
import org.testng.annotations.Test;

import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.message.MeldungsType;

@Test(groups = UNIT)
public class ProcessTeqTaskTest extends AbstractProcessingWitaTaskTest<ProcessTeqTask> {

    public ProcessTeqTaskTest() {
        super(ProcessTeqTask.class);
    }

    public void testTeq() throws Exception {
        DelegateExecution execution = createExecution(witaCBVorgang, MeldungsType.TEQ, null, null, false);
        execution.setVariable(WitaTaskVariables.TEQ_MESSAGE_TYPE.id, true);
        witaCBVorgang.setStatus(STATUS_SUBMITTED);
        underTest.execute(execution);

        verify(workflowTaskService, times(0)).setWorkflowToError(any(DelegateExecution.class), any(String.class));
        assertEquals(witaCBVorgang.getStatus(), STATUS_TRANSFERRED);
        verify(carrierElTalService).saveCBVorgang(witaCBVorgang);
    }
}
