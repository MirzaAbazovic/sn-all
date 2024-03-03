/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 14:38:39
 */
package de.mnet.hurrican.scheduler.job.errorhandler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import de.mnet.hurrican.scheduler.HurricanScheduler;

/**
 * Error-Handler, um eine EMail mit dem aufgetretenen Fehler zu senden.
 */
public class SendMailJobErrorHandler extends AbstractSendMailJobErrorHandler {

    private static final Logger LOGGER = Logger.getLogger(SendMailJobErrorHandler.class);

    @Override
    public void handleError(JobExecutionContext context, Throwable error, Object[] params) {
        try {
            JavaMailSender mailSender = getAKScheduler().getBean("mailSender", JavaMailSender.class);

            // EMail-Message
            SimpleMailMessage mm = getAKScheduler().getBean("errorMailMsg", SimpleMailMessage.class);
            if (hasMailReceiver(mm)) {
                if (!StringUtils.endsWithIgnoreCase(mm.getSubject(), HurricanScheduler.getApplicationMode())) {
                    mm.setSubject(mm.getSubject() + " - " + HurricanScheduler.getApplicationMode());
                }

                final StringBuilder msg = new StringBuilder();
                msg.append("Error while executing job: ");
                msg.append(context.getJobDetail().getName());
                msg.append(SystemUtils.LINE_SEPARATOR);
                msg.append("========= ERROR =========");
                msg.append(SystemUtils.LINE_SEPARATOR);
                msg.append(ExceptionUtils.getFullStackTrace(error));

                mm.setText(StringUtils.trimToEmpty(msg.toString()));
                mailSender.send(mm);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
