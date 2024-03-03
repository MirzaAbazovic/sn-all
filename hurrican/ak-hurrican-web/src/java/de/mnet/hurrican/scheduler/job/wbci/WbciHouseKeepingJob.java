/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 30.01.14 
 */
package de.mnet.hurrican.scheduler.job.wbci;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * Quartz job for triggering the 'house-keeping' in WBCI.
 */
public class WbciHouseKeepingJob extends AbstractWbciJob {

    private static final Logger LOGGER = Logger.getLogger(WbciHouseKeepingJob.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            LOGGER.info("Housekeeping Job triggered");

            LOGGER.info("Auto-Complete eligible preagreements started");
            int autoCompleted = getWbciGeschaeftsfallService().autoCompleteEligiblePreagreements();
            LOGGER.info(String.format("Auto-Complete eligible preagreements - %s preagreements were completed", autoCompleted));

            LOGGER.info("Update expired preagreements");
            int numExpiredPreagreements = getWbciGeschaeftsfallService().updateExpiredPreagreements();
            LOGGER.info(String.format("Update expired preagreements - %s preagreements were updated", numExpiredPreagreements));

            LOGGER.info("Housekeeping Job completed");
        }
        catch (Exception e) {
            handleError(jobExecutionContext, e);
        }
    }

    private WbciGeschaeftsfallService getWbciGeschaeftsfallService() throws ServiceNotFoundException {
        return getService(WbciGeschaeftsfallService.class);
    }

}
