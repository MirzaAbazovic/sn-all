/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 30.01.14 
 */
package de.mnet.hurrican.scheduler.job.wbci;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.service.WbciEscalationService;

/**
 * Quartz job for triggering the house-keeping job in WBCI
 */
public class WbciEscalationReportJob extends AbstractWbciJob {

    private static final Logger LOGGER = Logger.getLogger(WbciEscalationReportJob.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            LOGGER.info("Job 'WBCI internal overview report' triggered");
            getEscalationService().sendInternalOverviewReport();
            LOGGER.info("Job completed - the internal overview report has been generated");

            LOGGER.info("Job 'WBCI carrier escalation overview report' triggered");
            getEscalationService().sendCarrierEscalationOverviewReport();
            LOGGER.info("Job completed - the carrier escalation overview report has been generated");

            LOGGER.info("Job 'WBCI carrier specific escalation reports' triggered");
            final List<CarrierCode> carrierCodes = getEscalationService().sendCarrierSpecificEscalationReports();
            LOGGER.info(String.format("Job completed - %s carrier specific reports have been generated", carrierCodes.size()));
        }
        catch (Exception e) {
            handleError(jobExecutionContext, e);
        }
    }

    private WbciEscalationService getEscalationService() throws ServiceNotFoundException {
        return getService(WbciEscalationService.class);
    }

}
