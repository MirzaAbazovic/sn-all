/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2010 10:55:48
 */

package de.augustakom.hurrican.gui.tools.scheduler.controller;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * Wrapper Klasse zum Dispatchen der Scheduler Events auf Hurrican Seite.
 *
 *
 */
public class AKJobListener extends AKAbstractSchedulerListener implements JobListener {

    private JobListener listener = null;

    /**
     * @param listener the listener to set
     */
    public void setListener(JobListener listener) {
        this.listener = listener;
    }

    /* (non-Javadoc)
     * @see org.quartz.JobListener#getName()
     */
    @Override
    public String getName() {
        if (listener != null) {
            return listener.getName();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.quartz.JobListener#jobToBeExecuted(org.quartz.JobExecutionContext)
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        callMethod(listener, "jobToBeExecuted",
                new Class<?>[] { JobExecutionContext.class },
                new Object[] { context });
    }

    /* (non-Javadoc)
     * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        callMethod(listener, "jobExecutionVetoed",
                new Class<?>[] { JobExecutionContext.class },
                new Object[] { context });
    }

    /* (non-Javadoc)
     * @see org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext, org.quartz.JobExecutionException)
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        callMethod(listener, "jobWasExecuted",
                new Class<?>[] { JobExecutionContext.class, JobExecutionException.class },
                new Object[] { context, jobException });
    }

}
