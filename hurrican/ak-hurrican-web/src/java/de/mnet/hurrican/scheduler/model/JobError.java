/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 15:53:59
 */
package de.mnet.hurrican.scheduler.model;


/**
 * Modell zur Protokollierung von Fehlermeldungen eines Jobs.
 *
 *
 */
public class JobError extends BaseSchedulerModel {

    private static final long serialVersionUID = -7781029513702752514L;

    public static final String ERROR_LEVEL_WARNING = "warning";
    public static final String ERROR_LEVEL_ERROR = "error";
    public static final String ERROR_LEVEL_CRITICAL = "critical";

    private Long jobId;
    private String errorLevel;
    private String errorMessage;

    public String getErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(String errorLevel) {
        this.errorLevel = errorLevel;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

}
