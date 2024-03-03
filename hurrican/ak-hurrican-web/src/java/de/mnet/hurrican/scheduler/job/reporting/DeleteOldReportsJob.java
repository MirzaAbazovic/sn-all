/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2007 16:17:14
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
 * Job um Reports, die ein bestimmtes Alter überschritten haben, zu loeschen.
 *
 *
 */
public class DeleteOldReportsJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(DeleteOldReportsJob.class);
    private static final String DAYS = "days";

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            Integer days = Integer.valueOf((String) getJobDataMapObject(context, DAYS));
            ReportService rs = getReportService(ReportService.class);
            if (days != null) {
                rs.deleteOldReports(days, Boolean.TRUE);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

}
