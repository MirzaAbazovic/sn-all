/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2006 14:29:48
 */
package de.augustakom.common.tools.quartz;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;


/**
 * Client fuer einen Quartz-Scheduler. <br> Der Client stellt die Verbindung zu einen Quartz-Scheduler ueber RMI her.
 *
 *
 */
public class QuartzRMISchedulerClient {

    private Scheduler scheduler;

    /**
     * Stellt die Verbindung zu einem Quartz-Scheduler ueber RMI her.
     *
     * @param schedulerName Name des Schedulers, mit dem eine Verbindung hergestellt werden soll
     * @param rmiHost       Host, auf dem der zu verbindende Scheduler laeuft
     * @param rmiPort       Port, auf dem der zu verbindende Scheduler laeuft
     * @throws SchedulerException wenn die RMI-Verbindung zu dem Scheduler nicht hergestellt werden kann.
     */
    public void connect2RMIScheduler(String rmiHost, String rmiPort, String schedulerName) throws SchedulerException {
        Properties quartzProps = new Properties();
        quartzProps.put("org.quartz.scheduler.instanceName", schedulerName);
        quartzProps.put("org.quartz.scheduler.rmi.registryHost", rmiHost);
        quartzProps.put("org.quartz.scheduler.rmi.registryPort", rmiPort);
        quartzProps.put("org.quartz.scheduler.rmi.proxy", "true");
        quartzProps.put("org.quartz.scheduler.rmi.export", "false");
        scheduler = new StdSchedulerFactory(quartzProps).getScheduler();
    }

    /**
     * Gibt die Scheduler-Instanz zurueck, zu der ueber 'connect2RMIScheduler' zuvor eine Verbindung hergestellt wurde.
     *
     * @return Instanz von org.quartz.Scheduler
     * @throws SchedulerException wenn der Scheduler nicht vorhanden ist.
     */
    public Scheduler getScheduler() throws SchedulerException {
        checkScheduler();
        return scheduler;
    }

    /**
     * Ueberprueft, ob der angegebene Job gerade ausgefuehrt wird.
     *
     * @param jobName  Name des Jobs
     * @param jobGroup (optional) Gruppe des Jobs
     * @return true wenn der Job gerade ausgefuehrt wird.
     * @throws SchedulerException
     */
    public boolean isJobRunning(String jobName, String jobGroup) throws SchedulerException {
        jobGroup = (StringUtils.isNotBlank(jobGroup)) ? jobGroup : Scheduler.DEFAULT_GROUP;
        String fullNameToCheck = StringUtils.join(new Object[] { jobGroup, ".", jobName });
        return isJobRunning(fullNameToCheck);
    }

    /**
     * @param fullJobName
     * @return wenn der Job gerade ausgefuehrt wird.
     * @throws SchedulerException
     * @see isJobRunning(java.lang.String, java.lang.String)
     */
    public boolean isJobRunning(String fullJobName) throws SchedulerException {
        try {
            checkScheduler();
            @SuppressWarnings("unchecked")
            List<JobExecutionContext> jobs = scheduler.getCurrentlyExecutingJobs();
            if (jobs != null) {
                for (JobExecutionContext jec : jobs) {
                    JobDetail jd = jec.getJobDetail();
                    if ((jd != null) && StringUtils.equals(fullJobName, jd.getFullName())) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            throw new SchedulerException(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Ruft die Methode Scheduler.triggerJobWithVolatileTrigger(String, String, JobDataMap) auf.
     *
     * @param fullJobName
     * @param map
     * @throws SchedulerException
     */
    public void triggerJobWithVolatileTrigger(String fullJobName, JobDataMap map) throws SchedulerException {
        try {
            checkScheduler();
            String jobGroup = StringUtils.substringBefore(fullJobName, ".");
            String jobName = StringUtils.substringAfter(fullJobName, ".");
            scheduler.triggerJobWithVolatileTrigger(jobName, jobGroup, map);
        }
        catch (Exception e) {
            throw new SchedulerException(e.getMessage(), e);
        }
    }

    /**
     * Versucht, den Job mit dem Namen (Full-Name!) 'fullJobName' zu unterbrechen.
     */
    public void interruptJob(String fullJobName) throws SchedulerException {
        if (!isJobRunning(fullJobName)) {
            throw new SchedulerException(
                    "Der angegebene Job kann nicht abgebrochen werden, da er z.Z. nicht ausgefuehrt wird.");
        }

        try {
            checkScheduler();

            String jobGroup = StringUtils.substringBefore(fullJobName, ".");
            String jobName = StringUtils.substringAfter(fullJobName, ".");
            JobDetail jd = scheduler.getJobDetail(jobName, jobGroup);
            if (jd == null) {
                throw new SchedulerException("JobDetail zu " + fullJobName + " konnte nicht ermittelt werden!");
            }

            if (!InterruptableJob.class.isAssignableFrom(jd.getJobClass())) {
                throw new SchedulerException("Der Job " + fullJobName + " kann nicht abgebrochen werden!");
            }

            scheduler.interrupt(jobName, jobGroup);
        }
        catch (Exception e) {
            throw new SchedulerException(e.getMessage(), e);
        }
    }

    /**
     * Ueberprueft, ob der Scheduler initialisiert wurde.
     */
    private void checkScheduler() throws SchedulerException {
        if (scheduler == null) {
            throw new SchedulerException("No connection established to the scheduler");
        }
    }

}


