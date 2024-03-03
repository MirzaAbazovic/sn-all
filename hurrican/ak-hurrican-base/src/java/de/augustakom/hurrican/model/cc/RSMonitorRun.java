/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.10.2008 15:56:06
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

/**
 * Modell fuer ein Objekt vom Typ RS_Monitor_Run
 *
 *
 */
public class RSMonitorRun extends AbstractCCIDModel {

    /**
     * Werte fuer Monitor-Status.
     */
    public static final Long RS_REF_STATE_RUNNING = Long.valueOf(300);
    public static final Long RS_REF_STATE_FINISHED = Long.valueOf(301);
    public static final Long RS_REF_STATE_ERROR = Long.valueOf(302);

    /**
     * Werte fuer Monitor-Typen
     */
    public static final Long RS_REF_TYPE_EQ_MONITOR = Long.valueOf(351);
    public static final Long RS_REF_TYPE_RANG_MONITOR = Long.valueOf(350);

    private Long monitorType = null;
    private Long state = null;
    private Date startedAt = null;
    private Date finishedAt = null;
    private String runExecutedBy = null;

    /**
     * @return finishedAt
     */
    public Date getFinishedAt() {
        return finishedAt;
    }

    /**
     * @param finishedAt Festzulegender finishedAt
     */
    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    /**
     * @return monitorType
     */
    public Long getMonitorType() {
        return monitorType;
    }

    /**
     * @param monitorType Festzulegender monitorType
     */
    public void setMonitorType(Long monitorType) {
        this.monitorType = monitorType;
    }

    /**
     * @return runExecutedBy
     */
    public String getRunExecutedBy() {
        return runExecutedBy;
    }

    /**
     * @param runExecutedBy Festzulegender runExecutedBy
     */
    public void setRunExecutedBy(String runExecutedBy) {
        this.runExecutedBy = runExecutedBy;
    }

    /**
     * @return startedAt
     */
    public Date getStartedAt() {
        return startedAt;
    }

    /**
     * @param startedAt Festzulegender startedAt
     */
    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    /**
     * @return state
     */
    public Long getState() {
        return state;
    }

    /**
     * @param state Festzulegender state
     */
    public void setState(Long state) {
        this.state = state;
    }

}
