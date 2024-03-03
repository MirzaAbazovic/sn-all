/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 17:05:39
 */
package de.mnet.hurrican.scheduler.job.listener;

import org.quartz.JobListener;

/**
 * Abstrakte Implementierung eines JobListeners.
 *
 *
 */
public abstract class AbstractJobListener implements JobListener {

    private String jobListenerName;

    @Override
    public String getName() {
        return jobListenerName;
    }

    public void setName(String jobListenerName) {
        this.jobListenerName = jobListenerName;
    }

}
