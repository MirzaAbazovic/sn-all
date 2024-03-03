/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2009 13:06:30
 */
package de.mnet.hurrican.scheduler.job.cps;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerException;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

/**
 * Job-Implementierung, um CPS-Transactions fuer die Sperren zu erstellen. <br> Die gesamte Logik ist in den
 * Service-Klassen enthalten. Die Job-Klasse uebernimmt noch gewisse Logging- und Messaging-Funktionen.
 *
 *
 */
public class CPSCreateCPSTx4LocksJob extends AbstractCPSJob {

    private static final Logger LOGGER = Logger.getLogger(CPSCreateCPSTx4LocksJob.class);

    private static final String RESOURCE = "de.mnet.hurrican.scheduler.job.cps.resources.CPSCreateCPSTx4LocksJob";

    private ResourceReader resourceReader;
    private CPSJobMailConfig mailConfig;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Date actDate = new Date();
        LOGGER.info("Date for DN services: " + actDate);

        try {
            initJob();
            CPSService cps = getCCService(CPSService.class);

            // CPS-Tx erstellen
            CPSTransactionResult result = cps.createCPSTransactions4Lock(HurricanScheduler.getSessionId());

            int cpsTxCount = 0;
            int[] sendCounts = new int[] { 0, 0 };

            AKWarnings warnings = ((result != null) && (result.getWarnings() != null)) ? result.getWarnings()
                    : new AKWarnings();

            if (result != null) {
                cpsTxCount = (result.getCpsTransactions() != null) ? result.getCpsTransactions().size() : 0;

                // CPS-Tx senden
                sendCounts = sendCPSTransactions(result.getCpsTransactions(), warnings, cps);
            }

            if ((cpsTxCount > 0) || warnings.isNotEmpty()) {
                // Info-Mail
                sendMail(
                        mailConfig.getMailTo(),
                        HurricanConstants.EMAIL_SEPARATOR,
                        mailConfig.getMailSubject(),
                        StringTools.formatString(
                                resourceReader.getValue("mail.body.info"),
                                new Object[] { cpsTxCount, sendCounts[0], sendCounts[1],
                                        DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_LONG),
                                        warnings.getWarningsAsText() }
                        ), context
                );
            }
        }
        catch (StoreException e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);

            // Error-Mail
            sendMail(
                    mailConfig.getMailTo(),
                    HurricanConstants.EMAIL_SEPARATOR,
                    mailConfig.getMailSubject(),
                    StringTools.formatString(
                            resourceReader.getValue("mail.body.error"),
                            new Object[] { DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_LONG),
                                    e.getMessage() }
                    ), context
            );
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

    private void initJob() throws Exception {
        try {
            resourceReader = new ResourceReader(RESOURCE);
            RegistryService rs = getCCService(RegistryService.class);
            mailConfig = new CPSJobMailConfig();
            mailConfig.initialize(resourceReader, rs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerException("Error initializing job 'CPSCreateCPSTx4LocksJob': " + e.getMessage());
        }
    }

}
