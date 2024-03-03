/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.14
 */
package de.mnet.hurrican.scheduler.job.wbci;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.authentication.model.AKUser;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.wbci.service.WbciAutomationDonatingService;
import de.mnet.wbci.service.WbciAutomationService;

/**
 * Scheduler job to trigger the automatic processing of the {@link WbciAutomationService#processAutomatableStrAufhErlms(AKUser)}
 * tasks. (M-net = donating carrier)
 */
public class ProcessWbciAutomatableStrAufErlmsDonatingJob extends AbstractWbciJob {

    private static final Logger LOGGER = Logger.getLogger(ProcessWbciAutomatableStrAufErlmsDonatingJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {

            final WbciAutomationDonatingService wbciAutomationDonatingService = getWbciAutomationDonatingService();

            AKUser user = getCurrentUser(HurricanScheduler.getSessionId());
            final Collection<String> vaIdsStrErlm = wbciAutomationDonatingService
                    .processAutomatableStrAufhErlmsDonating(user, HurricanScheduler.getSessionId());
            LOGGER.info(String.format(
                    "Automatically processed the STR-ERLMs (donating) for the following VA-IDs:  %s", vaIdsStrErlm));
        }
        catch (Exception e) {
            handleError(context, e);
        }
    }

}
