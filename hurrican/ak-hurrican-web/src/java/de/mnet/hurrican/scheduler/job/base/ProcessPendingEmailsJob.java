/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2010 22:22:15
 */

package de.mnet.hurrican.scheduler.job.base;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.mail.MailSendException;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.exceptions.ProcessPendingEmailsException;
import de.augustakom.hurrican.service.cc.MailService;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

public class ProcessPendingEmailsJob extends AKAbstractQuartzJob {

    private MailService mailService;
    private LogDBJobErrorHandler logDbHandler;
    private SendMailJobErrorHandler sendMailJobErrorHandler;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            init();
        }
        catch (ServiceNotFoundException e) {
            throw new JobExecutionException(e);
        }
        processPendingEmails(context);
    }

    void init() throws ServiceNotFoundException {
        mailService = getCCService(MailService.class);
        logDbHandler = new LogDBJobErrorHandler();
        sendMailJobErrorHandler = new SendMailJobErrorHandler();
    }

    void processPendingEmails(JobExecutionContext context) throws JobExecutionException {
        try {
            mailService.processPendingEmails();
        }
        catch (ProcessPendingEmailsException e) {
            for (Exception nestedExcp : e.getNestedExceptions()) {
                if (nestedExcp instanceof MailSendException) {
                    JobExecutionException exception = new JobExecutionException(nestedExcp);
                    logDbHandler.handleError(context, exception, null);
                    sendMailJobErrorHandler.handleError(context, exception, null);
                }
            }
            throw new JobExecutionException(e);
        }
    }

}
