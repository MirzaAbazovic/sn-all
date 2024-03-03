/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2014
 */
package de.mnet.hurrican.scheduler.job.wbci;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.messages.AKWarnings;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobWarningsHandler;
import de.mnet.wbci.service.WbciAutomationDonatingService;
import de.mnet.wbci.service.WbciAutomationService;

/**
 *
 */
public abstract class AbstractWbciJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(AbstractWbciJob.class);

    private LogDBJobErrorHandler logDBJobErrorHandler = new LogDBJobErrorHandler();
    private SendMailJobErrorHandler sendMailJobErrorHandler = new SendMailJobErrorHandler();
    private SendMailJobWarningsHandler sendMailJobWarningHandler = new SendMailJobWarningsHandler();

    protected void handleError(JobExecutionContext context, Exception e) {
        // In case of a technical exception, just send one mail and abort the whole job
        LOGGER.error(e.getMessage(), e);
        logDBJobErrorHandler.handleError(context, e, null);
        sendMailJobErrorHandler.handleError(context, e, null);
    }

    protected void handleWarnings(JobExecutionContext context, AKWarnings warnings) {
        // Are there any warnings stored? Dispatch them and continue
        LOGGER.warn(warnings.getWarningsAsText());
        sendMailJobWarningHandler.handleWarnings(context, warnings);
    }

    protected WbciAutomationService getWbciAutomationService() throws ServiceNotFoundException {
        return getService(WbciAutomationService.class);
    }

    protected WbciAutomationDonatingService getWbciAutomationDonatingService() throws ServiceNotFoundException {
        return getService(WbciAutomationDonatingService.class);
    }

}
