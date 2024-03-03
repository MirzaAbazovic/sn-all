/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.10.13
 */
package de.mnet.hurrican.scheduler.job.wbci;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.mnet.common.exceptions.ServiceException;
import de.mnet.hurrican.scheduler.job.base.AbstractSendScheduledRequestsJob;
import de.mnet.wbci.service.WbciSchedulerService;

/**
 * Scheduler-Job, der alle noch nicht versendete {@link de.mnet.wbci.model.WbciRequest}s über den WbciSchedulerService
 * ermittelt und versendet.
 *
 *
 */
public class SendWbciRequestsJob extends AbstractSendScheduledRequestsJob {

    private static final Logger LOGGER = Logger.getLogger(SendWbciRequestsJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            LOGGER.info("SendWbciRequests Job triggered");
            final WbciSchedulerService wbciSchedulerService = getWbciSchedulerService();
            final List<Long> requestIds = wbciSchedulerService.findScheduledWbciRequestIds();
            for (Long requestId : requestIds) {
                try {
                    wbciSchedulerService.sendScheduledRequest(requestId);
                    LOGGER.info(String.format("WbciRequest with id '%s' has been successfully sent", requestId));
                }
                catch (ServiceException e) {
                    handleError(context, e);
                }
            }
            LOGGER.info("SendWbciRequests Job completed");
        }
        catch (Exception e) {
            handleError(context, e);
        }
    }

    private WbciSchedulerService getWbciSchedulerService() throws ServiceNotFoundException {
        return getService(WbciSchedulerService.class);
    }

    @Override
    protected ExceptionLogEntryContext getSchedulerExceptionLogEntryContext() {
        return ExceptionLogEntryContext.WBCI_SCHEDULER_SEARCH_REQUEST_ERROR;
    }
}
