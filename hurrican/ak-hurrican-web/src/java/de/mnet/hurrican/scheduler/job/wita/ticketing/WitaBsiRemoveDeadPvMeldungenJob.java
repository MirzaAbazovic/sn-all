/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.02.2012 13:21:29
 */
package de.mnet.hurrican.scheduler.job.wita.ticketing;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.wita.ticketing.WitaBsiProtokollService;

/**
 * Scheduler Job, der Meldung als "nicht nach BSI zu senden" markiert, die keinem oder mehreren Auftraegen zuzuordnen
 * ist.
 */
public class WitaBsiRemoveDeadPvMeldungenJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(WitaBsiRemoveDeadPvMeldungenJob.class);
    private WitaBsiProtokollService witaBsiProtokollService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        initServices(context);
        try {
            witaBsiProtokollService.dontSentPvMeldungenIfAuftragIsNotSet();
        }
        catch (Exception e) {
            logException(context, e);
        }
    }

    private void initServices(JobExecutionContext context) throws JobExecutionException {
        try {
            witaBsiProtokollService = getService(WitaBsiProtokollService.class);
        }
        catch (Exception e) {
            logException(context, e);
            throw new JobExecutionException(e.getMessage());
        }
    }

    private void logException(JobExecutionContext context, Exception e) {
        LOGGER.error(e.getMessage(), e);
        new LogDBJobErrorHandler().handleError(context, e, null);
        new SendMailJobErrorHandler().handleError(context, e, null);
    }

}
