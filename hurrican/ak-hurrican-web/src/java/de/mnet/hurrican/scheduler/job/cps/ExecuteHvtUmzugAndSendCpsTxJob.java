/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.06.2015
 */
package de.mnet.hurrican.scheduler.job.cps;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.service.cc.HvtUmzugService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerException;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;

/**
 * Scheduler-Job, um einen zu "heute" definierten {@link de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug} auszufuehren
 * und auch die zugehoerigen CPS-Transaktionen zu erstellen / senden. <br/>
 * Die Ergebnisse des Jobs werden per Mail an ein definiertes Postfach gesendet.
 */
public class ExecuteHvtUmzugAndSendCpsTxJob extends AbstractCPSJob {

    private static final Logger LOGGER = Logger.getLogger(ExecuteHvtUmzugAndSendCpsTxJob.class);

    private static final String RESOURCE = "de.mnet.hurrican.scheduler.job.cps.resources.ExecuteHvtUmzugAndSendCpsTxJob";

    private ResourceReader resourceReader;
    private String mailSubjectError;
    private String mailSubject;
    private String mailTo;

    private HvtUmzugService hvtUmzugService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            initJob();

            List<HvtUmzug> hvtUmzugList = hvtUmzugService.findForCurrentDay();
            for (HvtUmzug hvtUmzug : hvtUmzugList) {
                if (hvtUmzug.isExecutePlanningAllowed()) {
                    try {
                        AKWarnings executeWarnings = hvtUmzugService.executePlanning(hvtUmzug, HurricanScheduler.getSessionId());
                        AKWarnings cpsWarnings = hvtUmzugService.sendCpsModifies(hvtUmzug, false,
                                HurricanScheduler.getSessionId());

                        AKWarnings warnings = new AKWarnings();
                        warnings.addAKWarnings(executeWarnings, cpsWarnings);

                        if (warnings.isEmpty()) {
                            sendMail(mailTo, HurricanConstants.EMAIL_SEPARATOR, mailSubject,
                                    StringTools.formatString(resourceReader.getValue("mail.body.execution.success"),
                                            new Object[] { hvtUmzug.getKvzNr() }),
                                    context);
                        }
                        else {
                            sendMail(mailTo, HurricanConstants.EMAIL_SEPARATOR, mailSubject,
                                    StringTools.formatString(resourceReader.getValue("mail.body.execution.warnings"),
                                        new Object[] { hvtUmzug.getKvzNr(), warnings.getWarningsAsText() } ),
                                    context);
                        }
                    }
                    catch (Exception e) {
                        // Fehler fangen und per Mail einzeln senden; also pro HvtUmzug eine Fehler-Mail!
                        sendMail(mailTo, HurricanConstants.EMAIL_SEPARATOR, mailSubjectError,
                                StringTools.formatString(resourceReader.getValue("mail.body.execution.error"),
                                        new Object[] { hvtUmzug.getKvzNr(), e.getMessage() }),
                                context);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

            // Error-Mail
            sendMail(mailTo, HurricanConstants.EMAIL_SEPARATOR, mailSubjectError,
                    StringTools.formatString(resourceReader.getValue("mail.body.unexpected.error"),
                            new Object[] { e.getMessage() }),
                    context);

            new LogDBJobErrorHandler().handleError(context, e, null);
        }
    }


    private void initJob() throws Exception {
        try {
            hvtUmzugService = getCCService(HvtUmzugService.class);

            resourceReader = new ResourceReader(RESOURCE);

            RegistryService rs = getCCService(RegistryService.class);
            mailTo = rs.getStringValue(RegistryService.REGID_HVT_UMZUG_INFO_MAIL);

            mailSubject = StringTools.formatString(resourceReader.getValue("mail.subject"),
                    new Object[] { HurricanScheduler.getApplicationMode() });
            mailSubjectError = StringTools.formatString(resourceReader.getValue("mail.subject.error"),
                    new Object[] { HurricanScheduler.getApplicationMode() });

            if (StringUtils.isBlank(mailTo)) {
                throw new Exception("No eMail address defined!");
            }
        }
        catch (Exception e) {
            throw new AKSchedulerException("Error initializing the Job: " + e.getMessage());
        }
    }

}
