/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2015
 */
package de.mnet.hurrican.scheduler.job.maintainance;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.service.cc.ProcessExpiredServicesService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerException;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

/**
 * Scheduler-Job, um alle Auftraege zu ermitteln, bei denen zu 'heute' eine als 'AUTO_EXPIRE' markierte Leistung endet.
 * Fuer die betroffenen Auftraege wird ein Bauauftrag zum naechsten Werktag generiert. (Durch die Erstellung des BAs
 * wird automatisch ein Leistungsabgleich generiert und somit die dann gueltige Bandbreite auf den Auftrag geschrieben.
 * Ausserdem erfolgt durch den Bauauftrag mit Typ 'automatischer Downgrade' eine automatische CPS-Provisionierung
 * zum BA-Realisierungstermin.)
 */
public class ProcessExpiredServicesJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(ProcessExpiredServicesJob.class);

    private static final String MAIL_SUBJECT = "Warnungen/Fehler bei automatischem Leistungsabgleich (SysMode: %s)";
    private static final String MAIL_BODY =
            "Beim automatischen Abgleich von AUTO_EXPIRE Leistungen sind Fehler/Warnungen aufgetreten.%n"
                    + "Bitte die Aufträge kontrollieren und manuell weiter prozessieren.%n%n%s";

    private String mailSubject;
    private String mailTo;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            initJob();

            ProcessExpiredServicesService service = getService(ProcessExpiredServicesService.class);
            AKWarnings warnings = service.processExpiredServices(HurricanScheduler.getSessionId());

            if (warnings != null && warnings.isNotEmpty()) {
                sendMailWithCc(mailTo, null, HurricanConstants.EMAIL_SEPARATOR, mailSubject,
                        String.format(MAIL_BODY, warnings.getWarningsAsText()), context);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }


    private void initJob() throws Exception {
        try {
            RegistryService rs = getCCService(RegistryService.class);
            mailTo = rs.getStringValue(RegistryService.REGID_AUTO_EXPIRE_MAIL);

            mailSubject = StringTools.formatString(MAIL_SUBJECT, new Object[] { HurricanScheduler.getApplicationMode() });

            if (StringUtils.isBlank(mailTo)) {
                throw new Exception("No eMail address defined!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerException("Error initializing the Job: " + e.getMessage());
        }
    }

}
