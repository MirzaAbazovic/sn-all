/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.12.2005 13:33:03
 */
package de.mnet.hurrican.scheduler.job.errorhandler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


/**
 * Error-Handler, um eine SMS mit dem aufgetretenen Fehler zu senden.
 *
 *
 */
public class SendSMSJobErrorHandler extends AbstractSendMailJobErrorHandler {

    private static final Logger LOGGER = Logger.getLogger(SendSMSJobErrorHandler.class);

    @Override
    public void handleError(JobExecutionContext context, Throwable error, Object[] params) {
        try {
            JavaMailSender mailSender = getAKScheduler().getBean("mailSender", JavaMailSender.class);
            SimpleMailMessage mm = getAKScheduler().getBean("errorSMSMsg", SimpleMailMessage.class);
            if (hasMailReceiver(mm)) {
                StringBuilder msg = new StringBuilder("Error in Job: ");
                msg.append(context.getJobDetail().getName());
                msg.append(SystemUtils.LINE_SEPARATOR);
                msg.append(ExceptionUtils.getFullStackTrace(error));

                mm.setText(StringUtils.substring(msg.toString(), 0, 150));
                mailSender.send(mm);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}


