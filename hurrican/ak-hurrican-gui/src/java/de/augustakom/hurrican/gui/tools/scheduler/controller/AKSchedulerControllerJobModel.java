/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.07.2010 14:00:09
 */

package de.augustakom.hurrican.gui.tools.scheduler.controller;

import static com.google.common.collect.Lists.*;

import java.io.*;
import java.util.*;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;


/**
 * Das Model der Jobs Tabelle
 *
 *
 */
public class AKSchedulerControllerJobModel implements Serializable {

    private static final long serialVersionUID = -7286511107301958268L;
    public final static String JOB_STATUS_TO_BE_EXECUTED = "executing";
    public final static String JOB_STATUS_WAS_EXECUTED = "executed";
    public final static String JOB_STATUS_EXECUTION_VETOED = "vetoed";
    public final static String JOB_STATUS_SCHEDULED = "scheduled";
    public final static String JOB_STATUS_UNSCHEDULED = "unscheduled";
    public final static String JOB_STATUS_PAUSED = "paused";
    public final static String JOB_STATUS_RESUMED = "running";

    public static List<String> splitFullSchedulerName(String fullJobName) {
        if (fullJobName == null || !fullJobName.contains(".")) {
            return ImmutableList.of("", "");
        }
        return newArrayList(Splitter.on(".").limit(2).split(fullJobName));
    }

    private boolean isDurable = false;
    private boolean isStateful = false;
    private boolean isVolatile = false;
    private String execStatus = null;
    private String scheduleStatus = null;
    private String pauseStatus = null;
    private String fullJobName = null;
    private Date scheduledFireTime = null;
    private Date fireTime = null;
    private Date nextFireTime = null;

    /**
     * Default-Const.
     */
    public AKSchedulerControllerJobModel() {
    }

    /**
     * @return Returns the fireTime.
     */
    public Date getFireTime() {
        return fireTime;
    }

    /**
     * @param fireTime The fireTime to set.
     */
    public void setFireTime(Date fireTime) {
        this.fireTime = fireTime;
    }

    /**
     * @return Returns the fullJobName.
     */
    public String getFullJobName() {
        return fullJobName;
    }

    public String getJobName() {
        return splitFullSchedulerName(fullJobName).get(1);
    }

    public String getGroupName() {
        return splitFullSchedulerName(fullJobName).get(0);
    }

    /**
     * @param fullJobName The fullJobName to set.
     */
    public void setFullJobName(String fullJobName) {
        this.fullJobName = fullJobName;
    }

    /**
     * @return Returns the nextFireTime.
     */
    public Date getNextFireTime() {
        return nextFireTime;
    }

    /**
     * @param nextFireTime The nextFireTime to set.
     */
    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    /**
     * @return Returns the scheduledFireTime.
     */
    public Date getScheduledFireTime() {
        return scheduledFireTime;
    }

    /**
     * @param scheduledFireTime The scheduledFireTime to set.
     */
    public void setScheduledFireTime(Date scheduledFireTime) {
        this.scheduledFireTime = scheduledFireTime;
    }

    /**
     * @param isDurable the isDurable to set
     */
    public void setDurable(boolean isDurable) {
        this.isDurable = isDurable;
    }

    /**
     * @return the isDurable
     */
    public boolean getDurable() {
        return isDurable;
    }

    /**
     * @param isStateful the isStateful to set
     */
    public void setStateful(boolean isStateful) {
        this.isStateful = isStateful;
    }

    /**
     * @return the isStateful
     */
    public boolean getStateful() {
        return isStateful;
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

    /**
     * @param scheduleStatus the scheduleStatus to set
     */
    public void setScheduleStatus(String scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    /**
     * @return the scheduleStatus
     */
    public String getScheduleStatus() {
        return scheduleStatus;
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

}
