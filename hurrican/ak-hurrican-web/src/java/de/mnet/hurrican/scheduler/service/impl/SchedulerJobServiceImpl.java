/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 16:25:04
 */
package de.mnet.hurrican.scheduler.service.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import de.augustakom.common.tools.lang.DateTools;
import de.mnet.hurrican.scheduler.dao.JobErrorDAO;
import de.mnet.hurrican.scheduler.dao.JobExecutionDAO;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerDeleteException;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerFindException;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerStoreException;
import de.mnet.hurrican.scheduler.job.listener.LogJobExecutionListener;
import de.mnet.hurrican.scheduler.model.JobError;
import de.mnet.hurrican.scheduler.model.JobExecution;
import de.mnet.hurrican.scheduler.service.SchedulerJobService;
import de.mnet.hurrican.scheduler.utils.JobExecutionContextHelper;


/**
 * Service-Implementierung von <code>SchedulerJobService</code>.
 *
 *
 */
public class SchedulerJobServiceImpl extends BaseSchedulerService implements SchedulerJobService {

    /**
     * Anzahl der Tage, ab denen eine Job-Protokollierung als 'alt' angesehen wird. <br> Es muss sich hierbei immer um
     * einen negativen Wert handeln!
     */
    public static final int DAY_COUNT_4_OLD_JOBS = -7;

    private static final Logger LOGGER = Logger.getLogger(SchedulerJobServiceImpl.class);

    private JobExecutionDAO jobExecutionDAO;
    private JobErrorDAO jobErrorDAO;

    @Override
    public JobExecution createJobExecution(JobExecutionContext context) throws AKSchedulerStoreException {
        if (context == null) { return null; }

        try {
            JobDetail jobDetail = context.getJobDetail();
            if (jobDetail == null) {
                throw new AKSchedulerStoreException(AKSchedulerStoreException.JOB_DETAIL_NOT_AVAILABLE);
            }

            JobExecution jobExec = new JobExecution();
            jobExec.setJobName(jobDetail.getName());
            jobExec.setJobClass((jobDetail.getJobClass() != null) ? jobDetail.getJobClass().getName() : "unknown");
            jobExec.setStartTime(new Date());
            jobExec.setNextTime((context.getTrigger() != null) ? context.getTrigger().getNextFireTime() : null);

            jobExecutionDAO.store(jobExec);
            return jobExec;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerStoreException(AKSchedulerStoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void setJobExecutionFinished(JobExecution job) throws AKSchedulerStoreException {
        if (job == null) { return; }
        try {
            job.setEndTime(new Date());
            jobExecutionDAO.store(job);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerStoreException(AKSchedulerStoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void addJobError(JobExecutionContext context, Throwable error) throws AKSchedulerStoreException {
        Object tmp = JobExecutionContextHelper.getJobDataMapObject(context, LogJobExecutionListener.JOBDATAMAP_JOB_EXECUTION);
        if ((tmp == null) || !(tmp instanceof JobExecution)) {
            return;
        }
        try {
            JobExecution jobExec = (JobExecution) tmp;

            JobError jobError = new JobError();
            jobError.setJobId(jobExec.getId());
            jobError.setErrorLevel(JobError.ERROR_LEVEL_ERROR);
            jobError.setErrorMessage(StringUtils.substring(ExceptionUtils.getFullStackTrace(error), 0, 4000));

            jobErrorDAO.store(jobError);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerStoreException(AKSchedulerStoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<JobError> getJobErrors(JobExecutionContext context) throws AKSchedulerFindException {
        Object tmp = JobExecutionContextHelper.getJobDataMapObject(context, LogJobExecutionListener.JOBDATAMAP_JOB_EXECUTION);
        if ((tmp == null) || !(tmp instanceof JobExecution)) {
            return null;
        }
        try {
            return getJobErrorsByJobId(((JobExecution) tmp).getId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerFindException(AKSchedulerFindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<JobError> getJobErrorsByJobId(Long jobId) throws AKSchedulerFindException {
        if (jobId == null) {
            return null;
        }
        try {
            JobError example = new JobError();
            example.setJobId(jobId);
            return jobErrorDAO.queryByExample(example, JobError.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerFindException(AKSchedulerFindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public int deleteOldJobs() throws AKSchedulerDeleteException {
        try {
            Date maxStartDate = DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, DAY_COUNT_4_OLD_JOBS);
            return jobExecutionDAO.deleteOldJobs(maxStartDate);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerDeleteException(AKSchedulerDeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public JobExecution findLastSuccessfullJobExecution(String jobName) throws AKSchedulerFindException {
        try {
            return jobExecutionDAO.findLastSuccessfullExecutionByJobName(jobName);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerFindException(AKSchedulerFindException._UNEXPECTED_ERROR, e);
        }
    }

    public void setJobExecutionDAO(JobExecutionDAO jobExecutionDAO) {
        this.jobExecutionDAO = jobExecutionDAO;
    }

    public void setJobErrorDAO(JobErrorDAO jobErrorDAO) {
        this.jobErrorDAO = jobErrorDAO;
    }

}


