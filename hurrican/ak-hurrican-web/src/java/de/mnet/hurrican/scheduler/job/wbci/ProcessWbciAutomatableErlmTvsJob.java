/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.14
 */
package de.mnet.hurrican.scheduler.job.wbci;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.authentication.model.AKUser;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.wbci.service.WbciAutomationService;

/**
 * Scheduler job to trigger the automatic processing of the {@link WbciAutomationService#processAutomatableErlmTvs(AKUser)}
 * tasks. (M-net = receiving carrier)
 */
public class ProcessWbciAutomatableErlmTvsJob extends AbstractWbciJob {

    private static final Logger LOGGER = Logger.getLogger(ProcessWbciAutomatableErlmTvsJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            final WbciAutomationService wbciAutomationService = getWbciAutomationService();

            AKUser user = getCurrentUser(HurricanScheduler.getSessionId());
            final Collection<String> vaIdsErlmTv = wbciAutomationService.processAutomatableErlmTvs(user);
            LOGGER.info(String.format("Automatically processed the ERLM-TVs for the following VA-IDs:  %s", vaIdsErlmTv));
        }
        catch (Exception e) {
            handleError(context, e);
        }
    }

}
