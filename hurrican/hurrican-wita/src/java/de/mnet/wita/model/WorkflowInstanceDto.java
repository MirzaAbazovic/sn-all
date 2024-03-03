/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2011 09:34:54
 */
package de.mnet.wita.model;

import java.io.*;
import java.time.*;
import java.util.*;
import com.google.common.base.Function;

import de.mnet.wita.bpm.CommonWorkflowService.WorkflowReentryState;
import de.mnet.wita.bpm.impl.Workflow;

/**
 * DTO-Objekt fuer die Uebersicht aller aufgetretenden Workflow-Fehler
 */
public class WorkflowInstanceDto implements CbTask, Serializable {

    private static final long serialVersionUID = -6911382113728822189L;

    public static final Function<WorkflowInstanceDto, LocalDateTime> GET_START = new Function<WorkflowInstanceDto, LocalDateTime>() {

        @Override
        public LocalDateTime apply(WorkflowInstanceDto input) {
            return input.getStart();
        }
    };

    public static final Function<WorkflowInstanceDto, LocalDateTime> GET_LAST_ERROR_TASK_START = new Function<WorkflowInstanceDto, LocalDateTime>() {

        @Override
        public LocalDateTime apply(WorkflowInstanceDto input) {
            return input.getLastErrorTaskStart();
        }
    };

    private String businessKey;
    private Workflow workflowTyp;
    private String taskBearbeiter;
    private LocalDateTime start;
    private LocalDateTime lastErrorTaskStart;

    public WorkflowInstanceDto(String businessKey) {
        this.setBusinessKey(businessKey);
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public Workflow getWorkflow() {
        return workflowTyp;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflowTyp = workflow;
    }

    @Override
    public String getTaskBearbeiter() {
        return taskBearbeiter;
    }

    public void setTaskBearbeiter(String taskBearbeiter) {
        this.taskBearbeiter = taskBearbeiter;
    }

    public boolean isError() {
        return getLastErrorTaskStart() != null;
    }

    public LocalDateTime getLastErrorTaskStart() {
        return lastErrorTaskStart;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public List<WorkflowReentryState> getPossibleReentryStates() {
        return WorkflowReentryState.getFor(workflowTyp);
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setLastErrorTaskStart(LocalDateTime lastErrorTaskStart) {
        this.lastErrorTaskStart = lastErrorTaskStart;
    }
}
