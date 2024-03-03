/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.2011 10:35:59
 */
package de.mnet.hurrican.scheduler.job.exceptions;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.ConfigureableSendMailJobWarningsHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

/**
 * Sendet eine Email an das Hurrican-Team, wenn es Exception-Log-Eintraege gibt, die sich noch nicht in Bearbeitung
 * befinden.
 */
public class SendExceptionLogsMailJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(SendExceptionLogsMailJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            ExceptionLogService exceptionLogService = getService(ExceptionLogService.class);
            String message = exceptionLogService.getMailtextForThrownExceptions();
            if (StringUtils.isNotEmpty(message)) {
                AKWarnings warnings = new AKWarnings();
                warnings.addAKWarning(this, message);
                new ConfigureableSendMailJobWarningsHandler().handleWarnings(context, warnings, "exceptionMailMsg");
            }
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }
}