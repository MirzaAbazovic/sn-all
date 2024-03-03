/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.14
 */
package de.mnet.hurrican.scheduler.job.ffm;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.base.ProcessPendingEmailsJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

// @formatter:off
/**
 * Scheduler-Job, der aus den FFM Material-Rueckmeldungen eMails generiert und diese in die Mail-Tabelle
 * fuer den Versand eintraegt. <br/>
 * Der eigentliche Versand der Mails erfolgt ueber den Job {@link ProcessPendingEmailsJob}.
 */
// @formatter:on
public class NotifyMaterialFeedbackJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(NotifyMaterialFeedbackJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            FFMService ffmService = getCCService(FFMService.class);
            ffmService.createMailsForFfmFeedbacks();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

}
