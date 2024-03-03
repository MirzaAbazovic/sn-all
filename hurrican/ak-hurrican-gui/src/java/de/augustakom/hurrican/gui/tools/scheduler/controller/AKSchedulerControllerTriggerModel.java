/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2010 09:54:41
 */

package de.augustakom.hurrican.gui.tools.scheduler.controller;

import static de.augustakom.hurrican.gui.tools.scheduler.controller.AKSchedulerControllerJobModel.*;

import java.io.*;
import java.util.*;


/**
 * Das Model der Trigger Tabelle
 *
 *
 */
public class AKSchedulerControllerTriggerModel implements Serializable {
    private static final long serialVersionUID = 7268088826900029844L;
    public final static String TRIGGER_STATUS_PAUSED = "paused";
    public final static String TRIGGER_STATUS_RESUMED = "running";
    public final static String TRIGGER_STATUS_EXECUTING = "executing";
    public final static String TRIGGER_STATUS_EXECUTED = "executed";
    public final static String TRIGGER_STATUS_MISFIRED = "misfired";

    private Date startTime = null;
    private Date endTime = null;
    private Date nextFireTime = null;
    private Date previousFireTime = null;
    private Date finalFireTime = null;
    private boolean isVolatile = false;
    private String pauseStatus = null;
    private String fullTriggerName = null;
    private String execStatus = null;

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param nextFireTime the nextFireTime to set
     */
    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    /**
     * @return the nextFireTime
     */
    public Date getNextFireTime() {
        return nextFireTime;
    }

    /**
     * @param previousFireTime the previousFireTime to set
     */
    public void setPreviousFireTime(Date previousFireTime) {
        this.previousFireTime = previousFireTime;
    }

    /**
     * @return the previousFireTime
     */
    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    /**
     * @param finalFireTime the finalFireTime to set
     */
    public void setFinalFireTime(Date finalFireTime) {
        this.finalFireTime = finalFireTime;
    }

    /**
     * @return the finalFireTime
     */
    public Date getFinalFireTime() {
        return finalFireTime;
    }

    /**
     * @param isVolatile the isVolatile to set
     */
    public void setVolatile(boolean isVolatile) {
        this.isVolatile = isVolatile;
    }

    /**
     * @return the isVolatile
     */
    public boolean getVolatile() {
        return isVolatile;
    }

    /**
     * @param pauseStatus the pauseStatus to set
     */
    public void setPauseStatus(String pauseStatus) {
        this.pauseStatus = pauseStatus;
    }

    /**
     * @return the pauseStatus
     */
    public String getPauseStatus() {
        return pauseStatus;
    }

    /**
     * @param fullTriggerName the fullTriggerName to set
     */
    public void setFullTriggerName(String fullTriggerName) {
        this.fullTriggerName = fullTriggerName;
    }

    /**
     * @return the fullTriggerName
     */
    public String getFullTriggerName() {
        return fullTriggerName;
    }

    public String getTriggerName() {
        if (fullTriggerName == null) {
            return "";
        }
        return splitFullSchedulerName(fullTriggerName).get(1);
    }

    public String getGroupName() {
        if (fullTriggerName == null) {
            return "";
        }
        return splitFullSchedulerName(fullTriggerName).get(0);
    }

    /**
     * @param execStatus the execStatus to set
     */
    public void setExecStatus(String execStatus) {
        this.execStatus = execStatus;
    }

    /**
     * @return the execStatus
     */
    public String getExecStatus() {
        return execStatus;
    }

}
