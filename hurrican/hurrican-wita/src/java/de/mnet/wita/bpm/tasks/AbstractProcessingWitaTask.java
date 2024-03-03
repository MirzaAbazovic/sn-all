/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2011 14:47:34
 */
package de.mnet.wita.bpm.tasks;

import static de.mnet.wita.bpm.variables.ActivitiVariableUtils.*;

import org.activiti.engine.delegate.DelegateExecution;

import de.mnet.wita.bpm.WitaTaskVariables;

/**
 * Abstrakte Klasse zum Bearbeitem aller Wita Tasks.
 */
public abstract class AbstractProcessingWitaTask extends AbstractWitaTask {

    @Override
    public final void execute(DelegateExecution execution) throws Exception {
        Boolean isError = extractVariableSilent(execution, WitaTaskVariables.WORKFLOW_ERROR, Boolean.class, Boolean.FALSE);
        String errorMessage = extractVariableSilent(execution, WitaTaskVariables.WORKFLOW_ERROR_MESSAGE, String.class, "unknown error");

        if (isError) {
            workflowTaskService.setWorkflowToError(execution, errorMessage);
        }
        else {
            processMessage(execution);
        }
    }

    protected abstract void processMessage(DelegateExecution execution) throws Exception;
}
