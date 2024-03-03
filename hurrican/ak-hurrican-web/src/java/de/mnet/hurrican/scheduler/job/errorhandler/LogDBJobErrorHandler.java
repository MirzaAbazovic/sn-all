/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.2005 10:47:20
 */
package de.mnet.hurrican.scheduler.job.errorhandler;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;

import de.mnet.hurrican.scheduler.service.SchedulerJobService;

/**
 * Error-Handler, um die Details zu einem Job-Fehler in der Scheduler-Datenbank zu speichern.
 *
 *
 */
public class LogDBJobErrorHandler extends AbstractJobErrorHandler {

    private static final Logger LOGGER = Logger.getLogger(LogDBJobErrorHandler.class);

    @Override
    public void handleError(JobExecutionContext context, Throwable error, Object[] params) {
        try {
            SchedulerJobService sjs = getSchedulerService(SchedulerJobService.class);
            sjs.addJobError(context, error);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
