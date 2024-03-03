/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2011 10:27:04
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
 * Mailing-Handler fuer Info Nachrichten an OVST-Ressourcen
 *
 *
 */
public class ConfigureableSendMailJobWarningsHandler extends AbstractJobHandler {

    private static final Logger LOGGER = Logger.getLogger(ConfigureableSendMailJobWarningsHandler.class);

    public void handleWarnings(JobExecutionContext context, AKWarnings importWarnings, String mailMessageBean) {
        try {
            JavaMailSender mailSender = getAKScheduler().getBean("mailSender", JavaMailSender.class);

            // EMail-Message
            SimpleMailMessage mm = getAKScheduler().getBean(mailMessageBean, SimpleMailMessage.class);
            if (hasMailReceiver(mm)) {
                // Applikationsmodus in Subject aufnehmen
                if (!StringUtils.endsWithIgnoreCase(mm.getSubject(), HurricanScheduler.getApplicationMode())) {
                    mm.setSubject(mm.getSubject() + " - " + HurricanScheduler.getApplicationMode());
                }

                StringBuilder msg = new StringBuilder();
                msg.append("Info : ").append(context.getJobDetail().getName());
                msg.append(SystemUtils.LINE_SEPARATOR);
                msg.append("========= Warnings =========");
                msg.append(SystemUtils.LINE_SEPARATOR);
                msg.append(importWarnings.getMessagesAsText());

                mm.setText(StringUtils.trimToEmpty(msg.toString()));
                mailSender.send(mm);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
