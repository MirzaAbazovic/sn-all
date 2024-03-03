/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2006 16:24:52
 */
package de.augustakom.hurrican.gui.tools.scheduler.status;

import java.io.*;
import java.util.*;

/**
 * Hilfs-Modell fuer die Anzeige der z.Z. aktiven Scheduler-Jobs.
 *
 *
 */
public class AKSchedulerRunningJobModel implements Serializable {

    private String fullJobName = null;
    private Date scheduledFireTime = null;
    private Date fireTime = null;
    private Date nextFireTime = null;

    /**
     * Default-Const.
     */
    public AKSchedulerRunningJobModel() {
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
}

