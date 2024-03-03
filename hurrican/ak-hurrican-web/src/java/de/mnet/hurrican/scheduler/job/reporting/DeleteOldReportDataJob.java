/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2007 16:07:14
 */
package de.mnet.hurrican.scheduler.job.reporting;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.hurrican.service.reporting.ReportService;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

/**
 * Job, um alte ReportData-Datensätze zu loeschen.
 *
 *
 */
public class DeleteOldReportDataJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(DeleteOldReportDataJob.class);
    private static final String DAYS = "days";

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            Integer days = Integer.valueOf((String) getJobDataMapObject(context, DAYS));
            ReportService rs = getReportService(ReportService.class);
            if (days != null) {
                rs.deleteOldReportData(days);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

}
