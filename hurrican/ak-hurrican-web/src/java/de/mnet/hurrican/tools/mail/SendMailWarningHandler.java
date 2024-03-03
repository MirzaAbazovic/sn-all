/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2013 16:52:06
 */
package de.mnet.hurrican.tools.mail;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import de.augustakom.common.service.iface.IServiceMode;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.messages.AKWarnings;

/**
 *
 */
public class SendMailWarningHandler implements WarningHandler {

    private static final Logger LOGGER = Logger.getLogger(SendMailErrorHandler.class);
    private JavaMailSender mailSender;

    public SendMailWarningHandler(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void handleWarning(SimpleMailMessage warningMailMessage, AKWarnings warnings, String message) {
        try {
            StringBuilder msg = new StringBuilder();
            String mode = System.getProperty(IServiceMode.SYSTEM_PROPERTY_MODE);
            if (StringUtils.isNotBlank(mode)) {
                warningMailMessage.setSubject(String.format("%s - %s", warningMailMessage.getSubject(), mode));
            }
            if (StringUtils.isNotBlank(message)) {
                msg.append(message);
                msg.append(SystemUtils.LINE_SEPARATOR);
            }
            msg.append("Zeitpunkt: " + DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_LONG));
            msg.append(SystemUtils.LINE_SEPARATOR);
            msg.append(SystemUtils.LINE_SEPARATOR);
            msg.append("========= WARNING =========");
            msg.append(SystemUtils.LINE_SEPARATOR);
            msg.append(warnings.getMessagesAsText());
            msg.append(SystemUtils.LINE_SEPARATOR);

            warningMailMessage.setText(msg.toString());
            mailSender.send(warningMailMessage);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
