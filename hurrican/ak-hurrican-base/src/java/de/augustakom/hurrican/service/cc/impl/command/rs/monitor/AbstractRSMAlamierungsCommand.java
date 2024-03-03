/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.01.2009 12:29:38
 */
package de.augustakom.hurrican.service.cc.impl.command.rs.monitor;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.cc.RSMonitorRun;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.MonitorService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;


/**
 * Abstrakte Implemetierung eines Service-Commands fuer die Alarmierung der Ressourcenmonitore
 *
 *
 */
public abstract class AbstractRSMAlamierungsCommand extends AbstractServiceCommand {
    @Resource(name = "mailSender")
    private JavaMailSender mailSender;

    /**
     * Funktion prueft, ob der Monitor-Lauf abgeschlossen ist
     *
     * @param type Typ des zu pruefenden Monitors
     * @throws HurricanServiceCommandException Falls ein Fehler auftrat
     *
     */
    protected void checkMonitorRun(Long type) throws HurricanServiceCommandException {
        try {
            MonitorService ms = getCCService(MonitorService.class);
            RSMonitorRun run = ms.findByMonitorType(type);
            if ((run == null) || !NumberTools.equal(run.getState(), RSMonitorRun.RS_REF_STATE_FINISHED)) {
                throw new HurricanServiceCommandException("Monitor-Lauf nicht abgeschlossen oder fehlerhaft!");
            }
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e.getMessage(), e);
        }
    }

    /**
     * Verschickt eine Error-Email
     *
     * @param error       Fehlertext
     * @param mailTo      Empfaenger
     * @param mailSubject Subject der Email
     * @throws HurricanServiceCommandException Falls ein Fehler auftrat
     *
     */
    protected void sendMail(String error, String mailTo, String mailSubject) throws HurricanServiceCommandException {
        if (StringUtils.isBlank(error)) {
            return;
        }
        try {
            // Erzeuge Email
            SimpleMailMessage mm = new SimpleMailMessage();

            String[] emails = StringUtils.split(mailTo, HurricanConstants.EMAIL_SEPARATOR);
            if ((emails != null) && (emails.length > 0)) {
                mm.setTo(emails);
            }
            else {
                throw new HurricanServiceCommandException("Keine Email-Empfänger für die Alarmierung vorhanden");
            }
            mm.setFrom("Hurrican_Ressourcen_Monitor");
            mm.setSubject(mailSubject);
            mm.setText(error);

            // Sende Mail
            mailSender.send(mm);
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e.getMessage(), e);
        }
    }
}


