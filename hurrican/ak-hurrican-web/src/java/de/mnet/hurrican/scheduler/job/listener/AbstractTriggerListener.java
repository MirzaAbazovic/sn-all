/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 17:05:39
 */
package de.mnet.hurrican.scheduler.job.listener;

import org.apache.commons.lang.StringUtils;
import org.quartz.Scheduler;
import org.quartz.TriggerListener;

/**
 * Abstrakte Implementierung eines TriggerListeners. <br> Dem Listener kann ueber die Properties 'jobName' und
 * 'jobGroup' mitgeteilt werden, fuer welchen Job der Listener 'gedacht' ist.
 *
 *
 */
public abstract class AbstractTriggerListener implements TriggerListener {

    private String jobListenerName;
    private String jobName;
    private String jobGroup;

    /**
     * Setzt den Namen fuer den TriggerListener.
     */
    public void setName(String name) {
        this.jobListenerName = name;
    }

    @Override
    public String getName() {
        return jobListenerName;
    }

    public String getJobGroup() {
        return (StringUtils.isNotBlank(jobGroup)) ? jobGroup : Scheduler.DEFAULT_GROUP;
    }

    /**
     * Angabe der Job-Gruppe, auf die der Listener reagieren soll.
     */
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobName() {
        return jobName;
    }

    /**
     * Angabe des Job-Namens, auf den der Listener reagieren soll.
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Gibt den kompletten Namen (group+.+name) des Jobs zurueck, auf den der Listener reagieren soll.
     */
    public String getFullJobName() {
        return StringUtils.join(new Object[] { getJobGroup(), ".", getJobName() });
    }

}
