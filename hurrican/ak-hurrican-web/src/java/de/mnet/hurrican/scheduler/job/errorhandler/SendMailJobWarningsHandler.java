/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.10.2007 15:48:39
 */
package de.mnet.hurrican.scheduler.job.errorhandler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import de.augustakom.common.tools.messages.AKWarnings;
import de.mnet.hurrican.scheduler.HurricanScheduler;


/**
 * Error-Handler, um eine EMail mit dem aufgetretenen Fehler (AKWarnings) zu senden.
 *
 *
 */
public class SendMailJobWarningsHandler extends AbstractJobWarningsHandler {

    private static final Logger LOGGER = Logger.getLogger(SendMailJobWarningsHandler.class);

    @Override
    public void handleWarnings(JobExecutionContext context, AKWarnings warnings) {
        try {
            JavaMailSender mailSender = getAKScheduler().getBean("mailSender", JavaMailSender.class);

            // EMail-Message
            SimpleMailMessage mm = getAKScheduler().getBean("warningsMailMsg", SimpleMailMessage.class);
            if (hasMailReceiver(mm)) {
                if (!StringUtils.endsWithIgnoreCase(mm.getSubject(), HurricanScheduler.getApplicationMode())) {
                    mm.setSubject(mm.getSubject() + " - " + HurricanScheduler.getApplicationMode());
                }

                StringBuilder msg = new StringBuilder();
                msg.append("Problems while executing job: ").append(context.getJobDetail().getName());
                msg.append(SystemUtils.LINE_SEPARATOR);
                msg.append("========= Warnings =========");
                msg.append(SystemUtils.LINE_SEPARATOR);
                msg.append(warnings.getMessagesAsText());

                mm.setText(StringUtils.trimToEmpty(msg.toString()));
                mailSender.send(mm);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}


