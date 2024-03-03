/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2012 08:31:50
 */
package de.mnet.hurrican.scheduler.job.wita.ticketing;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.wita.message.MessageWithSentToBsiFlag;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.ticketing.WitaBsiProtokollService;

/**
 * Scheduler Job, der Protokolleinträge über Wita-Meldungen via SOAP nach BSI schreibt
 */
public class WitaBsiProtokollEintragJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(WitaBsiProtokollEintragJob.class);

    private static final int MAX_COUNT_DELAYED_REQUESTS = 1000;

    private MwfEntityService mwfEntityService;
    private WitaBsiProtokollService witaBsiProtokollService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        initServices(context);

        List<Meldung<?>> meldungenToBeSentToBsi = mwfEntityService.findMeldungenToBeSentToBsi();
        List<MnetWitaRequest> requestsToBeSentToBsi = mwfEntityService.findRequestsToBeSentToBsi();
        List<MnetWitaRequest> delayedRequestsToBeSentToBsi = mwfEntityService.findDelayedRequestsToBeSentToBsi(MAX_COUNT_DELAYED_REQUESTS);

        protokolliereNachrichten(context, meldungenToBeSentToBsi);
        protokolliereNachrichten(context, requestsToBeSentToBsi);
        protokolliereDelay(context, delayedRequestsToBeSentToBsi);
    }

    private <T extends MwfEntity & MessageWithSentToBsiFlag> void protokolliereNachrichten(JobExecutionContext context,
            List<T> notSentMeldungen) {
        try {
            for (T meldung : notSentMeldungen) {
                witaBsiProtokollService.protokolliereNachricht(meldung);
            }
        }
        catch (Exception e) {
            logException(context, e);
        }
    }

    private <T extends MwfEntity & MessageWithSentToBsiFlag> void protokolliereDelay(JobExecutionContext context,
            List<MnetWitaRequest> delayedRequests) {
        try {
            for (MnetWitaRequest delayed : delayedRequests) {
                witaBsiProtokollService.protokolliereDelay(delayed);
            }
        }
        catch (Exception e) {
            logException(context, e);
        }
    }

    private void initServices(JobExecutionContext context) throws JobExecutionException {
        try {
            witaBsiProtokollService = getService(WitaBsiProtokollService.class);
            mwfEntityService = getService(MwfEntityService.class);
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


