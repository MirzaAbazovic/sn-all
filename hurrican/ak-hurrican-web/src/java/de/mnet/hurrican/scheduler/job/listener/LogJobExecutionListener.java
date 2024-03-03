/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 09:03:27
 */
package de.mnet.hurrican.scheduler.job.listener;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.mnet.hurrican.scheduler.model.JobExecution;
import de.mnet.hurrican.scheduler.service.SchedulerJobService;
import de.mnet.hurrican.scheduler.utils.JobExecutionContextHelper;

/**
 * Listener, um den Beginn und das Ende eines Jobs zu protokollieren. <br> Die Protokollierung erfolgt sowohl ueber
 * einen Logger (je nach Konfiguration auf Konsole und/oder File) und in der Scheduler-Datenbank.
 *
 *
 */
public class LogJobExecutionListener extends AbstractJobListener {

    private static final Logger LOGGER = Logger.getLogger(LogJobExecutionListener.class);

    /**
     * Property-Name, um ein Modell vom Typ <code>JobExecution</code> in einer JobDataMap zu speichern.
     */
    public static final String JOBDATAMAP_JOB_EXECUTION = "job.execution.model";

    private static final String START_JOB = "Begin execution of job: ";
    private static final String JOB_FINISHED = "Finished job: ";

    private SchedulerJobService schedulerJobService;

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        LOGGER.info(START_JOB + context.getJobDetail().getName());
        try {
            JobExecution execution = schedulerJobService.createJobExecution(context);

            if (execution != null) {
                JobExecutionContextHelper.setJobDataMapObject(context, JOBDATAMAP_JOB_EXECUTION, execution);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        // not used
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        LOGGER.info(JOB_FINISHED + context.getJobDetail().getName());
        Object jobDataMap = JobExecutionContextHelper.getJobDataMapObject(context, JOBDATAMAP_JOB_EXECUTION);
        if (jobDataMap instanceof JobExecution) {
            ((JobExecution) jobDataMap).setEndTime(new Date());
            try {
                schedulerJobService.setJobExecutionFinished((JobExecution) jobDataMap);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public void setSchedulerJobService(SchedulerJobService schedulerJobService) {
        this.schedulerJobService = schedulerJobService;
    }

}
