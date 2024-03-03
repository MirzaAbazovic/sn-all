/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2006 15:07:14
 */
package de.mnet.hurrican.scheduler.job.base;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.hurrican.scheduler.service.SchedulerJobService;

/**
 * Job, um die alten Job-Protokollierungen zu loeschen.
 *
 *
 */
public class DeleteOldLoggingsJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(DeleteOldLoggingsJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            SchedulerJobService sjs = getSchedulerService(SchedulerJobService.class);
            sjs.deleteOldJobs();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

}
