/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2010 16:13:22
 */

package de.augustakom.hurrican.gui.tools.scheduler.controller;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;


/**
 * Wrapper Klasse zum Dispatchen der Scheduler Events auf Hurrican Seite.
 *
 *
 */
public class AKSchedulerListener extends AKAbstractSchedulerListener implements SchedulerListener {

    private SchedulerListener listener = null;

    /**
     * @param listener the listener to set
     */
    public void setListener(SchedulerListener listener) {
        this.listener = listener;
    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobScheduled(org.quartz.Trigger)
     */
    @Override
    public void jobScheduled(Trigger trigger) {
        callMethod(listener, "jobScheduled",
                new Class<?>[] { Trigger.class },
                new Object[] { trigger });
    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobUnscheduled(java.lang.String, java.lang.String)
     */
    @Override
    public void jobUnscheduled(String triggerName, String triggerGroup) {
        callMethod(listener, "jobUnscheduled",
                new Class<?>[] { String.class, String.class },
                new Object[] { triggerName, triggerGroup });
    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#triggerFinalized(org.quartz.Trigger)
     */
    @Override
    public void triggerFinalized(Trigger trigger) {
        callMethod(listener, "triggerFinalized",
                new Class<?>[] { Trigger.class },
                new Object[] { trigger });
    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#triggersPaused(java.lang.String, java.lang.String)
     */
    @Override
    public void triggersPaused(String triggerName, String triggerGroup) {
        callMethod(listener, "triggersPaused",
                new Class<?>[] { String.class, String.class },
                new Object[] { triggerName, triggerGroup });
    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#triggersResumed(java.lang.String, java.lang.String)
     */
    @Override
    public void triggersResumed(String triggerName, String triggerGroup) {
        callMethod(listener, "triggersResumed",
                new Class<?>[] { String.class, String.class },
                new Object[] { triggerName, triggerGroup });
    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobAdded(org.quartz.JobDetail)
     */
    @Override
    public void jobAdded(JobDetail jobDetail) {
        callMethod(listener, "jobAdded",
                new Class<?>[] { JobDetail.class },
                new Object[] { jobDetail });
    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobDeleted(java.lang.String, java.lang.String)
     */
    @Override
    public void jobDeleted(String jobName, String groupName) {
        callMethod(listener, "jobDeleted",
                new Class<?>[] { String.class, String.class },
                new Object[] { jobName, groupName });
    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobsPaused(java.lang.String, java.lang.String)
     */
    @Override
    public void jobsPaused(String jobName, String jobGroup) {
        callMethod(listener, "jobsPaused",
                new Class<?>[] { String.class, String.class },
                new Object[] { jobName, jobGroup });
    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobsResumed(java.lang.String, java.lang.String)
     */
    @Override
    public void jobsResumed(String jobName, String jobGroup) {
        callMethod(listener, "jobsResumed",
                new Class<?>[] { String.class, String.class },
                new Object[] { jobName, jobGroup });
    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerError(java.lang.String, org.quartz.SchedulerException)
     */
    @Override
    public void schedulerError(String msg, SchedulerException cause) {
        callMethod(listener, "schedulerError",
                new Class<?>[] { String.class, SchedulerException.class },
                new Object[] { msg, cause });
    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerInStandbyMode()
     */
    @Override
    public void schedulerInStandbyMode() {
        callMethod(listener, "schedulerInStandbyMode",
                new Class<?>[] { },
                new Object[] { });
    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerStarted()
     */
    @Override
    public void schedulerStarted() {
        callMethod(listener, "schedulerStarted",
                new Class<?>[] { },
                new Object[] { });
    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerShutdown()
     */
    @Override
    public void schedulerShutdown() {
        callMethod(listener, "schedulerShutdown",
                new Class<?>[] { },
                new Object[] { });
    }

}
