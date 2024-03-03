/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2014
 */
package de.mnet.hurrican.scheduler.job.base;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobWarningsHandler;

public abstract class AbstractSendScheduledRequestsJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(AbstractSendScheduledRequestsJob.class);

    private ExceptionLogService exceptionLogService;

    public void handleError(JobExecutionContext context, Exception e) {
        LOGGER.error(e.getMessage(), e);
        try {
            getExceptionLogService().saveExceptionLogEntry(createExcpetionLogEntry(e));
        }
        catch (Exception subEx) {
            handleErrorFallback(context, subEx);
            handleErrorFallback(context, e);
        }
    }

    public void handleWarnings(JobExecutionContext context, AKWarnings warnings) {
        // Are there any warnings stored? Dispatch them and continue
        LOGGER.warn(warnings.getWarningsAsText());
        getSendMailJobWarningHandler().handleWarnings(context, warnings);
    }

    protected abstract ExceptionLogEntryContext getSchedulerExceptionLogEntryContext();

    private ExceptionLogEntry createExcpetionLogEntry(Exception e) {
        return new ExceptionLogEntry(getSchedulerExceptionLogEntryContext(), e.getMessage(), e);
    }

    protected void handleErrorFallback(JobExecutionContext context, Exception e) {
        // In case of a technical exception, just send one mail and abort the whole job
        getLogDBJobErrorHandler().handleError(context, e, null);
        getSendMailJobErrorHandler().handleError(context, e, null);
    }

    protected ExceptionLogService getExceptionLogService() throws ServiceNotFoundException {
        if (exceptionLogService == null) {
            exceptionLogService = getCCService(ExceptionLogService.class);
        }
        return exceptionLogService;
    }

    protected SendMailJobWarningsHandler getSendMailJobWarningHandler() {
        return new SendMailJobWarningsHandler();
    }

    protected SendMailJobErrorHandler getSendMailJobErrorHandler() {
        return new SendMailJobErrorHandler();
    }

    protected LogDBJobErrorHandler getLogDBJobErrorHandler() {
        return new LogDBJobErrorHandler();
    }

}
