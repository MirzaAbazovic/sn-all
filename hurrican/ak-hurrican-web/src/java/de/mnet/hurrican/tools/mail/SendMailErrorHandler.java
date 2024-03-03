/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.03.2007 15:38:39
 */
package de.mnet.hurrican.tools.mail;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import de.augustakom.common.service.iface.IServiceMode;
import de.augustakom.common.tools.lang.DateTools;


/**
 * Error-Handler, um eine EMail mit dem aufgetretenen Fehler zu senden.
 */
public class SendMailErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = Logger.getLogger(SendMailErrorHandler.class);
    private JavaMailSender mailSender;


    public SendMailErrorHandler(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Verschickt eine Email mit Informationen zu einem best. Fehler (Exception)
     *
     * @param ex      Exception, wird als Stacktrace in der Email angegeben
     * @param message Information zum Fehler im Klartext
     */
    @Override
    public void handleError(SimpleMailMessage errorMailMessage, Throwable ex, String message) {
        try {
            StringBuilder msg = new StringBuilder();
            String mode = System.getProperty(IServiceMode.SYSTEM_PROPERTY_MODE);
            if (StringUtils.isNotBlank(mode)) {
                errorMailMessage.setSubject(String.format("%s - %s", errorMailMessage.getSubject(), mode));
            }
            if (StringUtils.isNotBlank(message)) {
                msg.append(message);
                msg.append(SystemUtils.LINE_SEPARATOR);
            }
            msg.append("Zeitpunkt: " + DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_LONG));
            msg.append(SystemUtils.LINE_SEPARATOR);
            msg.append(SystemUtils.LINE_SEPARATOR);
            msg.append("========= ERROR =========");
            msg.append(SystemUtils.LINE_SEPARATOR);
            msg.append(ExceptionUtils.getFullStackTrace(ex));
            msg.append(SystemUtils.LINE_SEPARATOR);

            errorMailMessage.setText(msg.toString());
            mailSender.send(errorMailMessage);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
