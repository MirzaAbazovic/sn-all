/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.2014
 */
package de.mnet.wbci.model.builder;

import static de.mnet.wbci.model.AutomationTask.*;

import java.time.*;
import java.util.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.Meldung;

/**
 *
 */
public class AutomationTaskBuilder implements WbciBuilder<AutomationTask> {

    protected TaskName name;
    protected AutomationStatus status;
    protected LocalDateTime createdAt;
    protected LocalDateTime completedAt;
    protected Long userId;
    protected String userName;
    protected String executionLog;
    protected Meldung meldung;

    @Override
    public AutomationTask build() {
        AutomationTask automationTask = new AutomationTask();
        automationTask.setName(name);
        automationTask.setStatus(status);
        automationTask.setCreatedAt(DateConverterUtils.asDate(createdAt));
        automationTask.setCompletedAt(DateConverterUtils.asDate(completedAt));
        automationTask.setUserId(userId);
        automationTask.setUserName(userName);
        automationTask.setExecutionLog(executionLog);
        automationTask.setMeldung(meldung);
        return automationTask;
    }

    public AutomationTaskBuilder withName(TaskName name) {
        this.name = name;
        return this;
    }

    public AutomationTaskBuilder withStatus(AutomationStatus status) {
        this.status = status;
        return this;
    }

    public AutomationTaskBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public AutomationTaskBuilder withCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
        return this;
    }

    public AutomationTaskBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public AutomationTaskBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public AutomationTaskBuilder withExecutionLog(String executionLog) {
        this.executionLog = executionLog;
        return this;
    }
    
    public AutomationTaskBuilder withMeldung(Meldung meldung) {
        this.meldung = meldung;
        return this;
    }

}
