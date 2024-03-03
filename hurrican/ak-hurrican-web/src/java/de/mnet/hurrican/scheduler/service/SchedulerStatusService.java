/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2011 10:56:43
 */
package de.mnet.hurrican.scheduler.service;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;

import de.mnet.common.service.status.ApplicationStatusResult;
import de.mnet.common.service.status.ApplicationStatusService;
import de.mnet.hurrican.scheduler.HurricanScheduler;

public class SchedulerStatusService implements ApplicationStatusService {

    private static final Logger LOGGER = Logger.getLogger(SchedulerStatusService.class);

    @Override
    public ApplicationStatusResult getStatus() {
        ApplicationStatusResult result = new ApplicationStatusResult();
        try {
            Scheduler scheduler = HurricanScheduler.getInstance().getBean("ak.scheduler", Scheduler.class);
            if (!scheduler.isStarted()) {
                result.addError("Scheduler has not been started!");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error querying status of scheduler", e);
            result.addError("Error querying status of scheduler: " + e.getMessage());
        }

        return result;
    }

    @Override
    public String getStatusName() {
        return "Scheduler Status";
    }

}
