/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2011 09:34:54
 */
package de.mnet.wita.model;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.activiti.engine.history.HistoricActivityInstance;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.bpm.CommonWorkflowService.WorkflowReentryState;
import de.mnet.wita.bpm.WorkflowTaskName;
import de.mnet.wita.bpm.impl.Workflow;

/**
 * DTO-Objekt fuer die Uebersicht aller aufgetretenden Workflow-Fehler
 */
public class WorkflowInstanceDetailsDto implements CbTask, Serializable {

    private static final long serialVersionUID = -6911382113728822189L;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm:ss");

    public static final Function<WorkflowInstanceDetailsDto, LocalDateTime> GET_START = new Function<WorkflowInstanceDetailsDto, LocalDateTime>() {

        @Override
        public LocalDateTime apply(WorkflowInstanceDetailsDto input) {
            return input.getStart();
        }
    };

    public static final Function<WorkflowInstanceDetailsDto, LocalDateTime> GET_LAST_ERROR_TASK_START = new Function<WorkflowInstanceDetailsDto, LocalDateTime>() {

        @Override
        public LocalDateTime apply(WorkflowInstanceDetailsDto input) {
            return input.getLastErrorTaskStart();
        }
    };

    private String businessKey;
    private List<ActivityData> taskHistory = Lists.<ActivityData>newArrayList();
    private Map<String, Object> workflowVariables;
    private Workflow workflowTyp;
    private String taskBearbeiter;

    public WorkflowInstanceDetailsDto(String businessKey, List<HistoricActivityInstance> taskHistory) {
        this.setBusinessKey(businessKey);
        for (HistoricActivityInstance historicActivityInstance : taskHistory) {
            ActivityData activityData = new ActivityData();
            activityData.setId(historicActivityInstance.getActivityId());
            activityData.setName(historicActivityInstance.getActivityName());
            activityData.setStart((historicActivityInstance.getStartTime() != null)
                    ? DateConverterUtils.asLocalDateTime(historicActivityInstance.getStartTime())
                    : LocalDateTime.now());
            activityData.setEnd((historicActivityInstance.getEndTime() != null)
                    ? DateConverterUtils.asLocalDateTime(historicActivityInstance.getEndTime())
                    : LocalDateTime.now());
            getTaskHistory().add(activityData);
        }
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
        return getLastErrorTask() != null;
    }

    private ActivityData getLastErrorTask() {
        ActivityData activityData = Iterables.getLast(getTaskHistory());
        return WorkflowTaskName.WORKFLOW_ERROR.id.equals(activityData.getId()) ? activityData : null;
    }

    public String getLastErrorTaskStartString() {
        ActivityData lastErrorTask = getLastErrorTask();
        return lastErrorTask != null ? lastErrorTask.getStartString() : null;
    }

    public LocalDateTime getLastErrorTaskStart() {
        ActivityData lastErrorTask = getLastErrorTask();
        return lastErrorTask != null ? lastErrorTask.getStart() : null;
    }

    public String getStartString() {
        ActivityData activityData = Iterables.getFirst(getTaskHistory(), null);
        return activityData != null ? activityData.getStartString() : null;
    }

    public LocalDateTime getStart() {
        ActivityData activityData = Iterables.getFirst(getTaskHistory(), null);
        return activityData != null ? activityData.getStart() : null;
    }

    public List<WorkflowReentryState> getPossibleReentryStates() {
        return WorkflowReentryState.getFor(workflowTyp);
    }

    public Map<String, Object> getWorkflowVariables() {
        return workflowVariables;
    }

    public void setWorkflowVariables(Map<String, Object> workflowVariables) {
        this.workflowVariables = workflowVariables;
    }

    public List<ActivityData> getTaskHistory() {
        return taskHistory;
    }

    public static class ActivityData implements Serializable {

        private static final long serialVersionUID = 8103594505332310458L;

        private String id;
        private String name;
        private LocalDateTime start;
        private LocalDateTime end;

        @Override
        public String toString() {
            return getId();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public String getStartString() {
            return getStart() != null ? getStart().format(DATE_TIME_FORMATTER) : null;
        }

        public void setStart(LocalDateTime start) {
            this.start = start;
        }

        public LocalDateTime getEnd() {
            return end;
        }

        public String getEndString() {
            return getEnd() != null ? getEnd().format(DATE_TIME_FORMATTER) : null;
        }

        public void setEnd(LocalDateTime end) {
            this.end = end;
        }
    }


}


