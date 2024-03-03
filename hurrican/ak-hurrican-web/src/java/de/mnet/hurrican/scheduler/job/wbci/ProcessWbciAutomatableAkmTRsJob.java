/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 25.07.2014 
 */
package de.mnet.hurrican.scheduler.job.wbci;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.collections.CollectionTools;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.wbci.service.WbciAutomationService;

/**
 * Scheduler job to trigger the automatic processing of the outgoing
 * {@link WbciAutomationService#processAutomatableAkmTrs(AKUser)} tasks (M-net = receiving carrier).
 */
public class ProcessWbciAutomatableAkmTRsJob extends AbstractWbciJob {

    private static final Logger LOGGER = Logger.getLogger(ProcessWbciAutomatableAkmTRsJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            final WbciAutomationService wbciAutomationService = getWbciAutomationService();
            AKUser user = getCurrentUser(HurricanScheduler.getSessionId());
            final Collection<String> carrierRefIds = wbciAutomationService.processAutomatableAkmTrs(user);
            LOGGER.info(String.format("Automatically processed the AKM-TRs and created the following WITA orders:  %s",
                    CollectionTools.formatCommaSeparated(carrierRefIds)));
        }
        catch (Exception e) {
            handleError(context, e);
        }
    }

}
