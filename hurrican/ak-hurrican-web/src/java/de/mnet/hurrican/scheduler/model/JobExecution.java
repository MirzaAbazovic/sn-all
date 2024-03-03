/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 15:50:54
 */
package de.mnet.hurrican.scheduler.model;

import java.util.*;


/**
 * Modell zur Protokollierung von Job-Executions.
 *
 *
 */
public class JobExecution extends BaseSchedulerModel {

    private static final long serialVersionUID = -7581005749045359263L;

    private String jobName;
    private String jobClass;
    private Date startTime;
    private Date endTime;
    private Date nextTime;

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Dateobjekt wird nicht veraendert!")
    public Date getEndTime() {
        return endTime;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Dateobjekt wird nicht veraendert!")
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Dateobjekt wird nicht veraendert!")
    public Date getStartTime() {
        return startTime;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Dateobjekt wird nicht veraendert!")
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Dateobjekt wird nicht veraendert!")
    public Date getNextTime() {
        return nextTime;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Dateobjekt wird nicht veraendert!")
    public void setNextTime(Date nextTime) {
        this.nextTime = nextTime;
    }

}
