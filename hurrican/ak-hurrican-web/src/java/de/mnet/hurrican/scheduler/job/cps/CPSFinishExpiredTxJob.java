/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2010 10:44:04
 */

package de.mnet.hurrican.scheduler.job.cps;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.tools.exceptions.LanguageException;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerException;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

/**
 * Schließt abgelaufene Transaktionen ab <ul> <li>PREPARING_FAILURE auf FAILURE_CLOSE <li>PREPARING auf CANCELLED </ul>
 *
 *
 */
public class CPSFinishExpiredTxJob extends AbstractCPSJob {

    private static final Logger LOGGER = Logger.getLogger(CPSFinishExpiredTxJob.class);

    private static final String RESOURCE = "de.mnet.hurrican.scheduler.job.cps.resources.CPSFinishExpiredTxJob";
    private ResourceReader resourceReader;
    private CPSJobMailConfig mailConfig;

    /**
     * Schließt abgelaufene Transaktionen ab <ul> <li>PREPARING_FAILURE auf FAILURE_CLOSE <li>PREPARING auf CANCELLED
     * </ul>
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        int countPreparing = 0;
        int countPreparingFailed = 0;
        int countPreparingSucceeded = 0;
        StringBuilder idsPreparingFailed = new StringBuilder();
        StringBuilder idsPreparingSucceeded = new StringBuilder();
        int countPreparingFailure = 0;
        int countPreparingFailureFailed = 0;
        int countPreparingFailureSucceeded = 0;
        StringBuilder idsPreparingFailureFailed = new StringBuilder();
        StringBuilder idsPreparingFailureSucceeded = new StringBuilder();

        LOGGER.info("Execute job 'CPSFinishExpiredTxJob'.");

        try {
            initJob();
            CPSService cps = getCCService(CPSService.class);

            // Transaktionen finden - Ablaufdatum aus Property 'cps.tx.offsetDays' bestimmen
            List<CPSTransactionExt> expiredList = cps.findExpiredTransactions(Integer.valueOf(resourceReader
                    .getValue("cps.tx.offsetDays")));

            // Jede Transaktion durchgehen
            for (CPSTransactionExt cpsTx : expiredList) {
                if (cpsTx.getTxState().equals(CPSTransaction.TX_STATE_IN_PREPARING)) {
                    try {
                        // Prepairing -> cancel
                        cps.cancelCPSTransaction(cpsTx.getId(), HurricanScheduler.getSessionId());
                        idsPreparingSucceeded.append(cpsTx.getId().toString());
                        idsPreparingSucceeded.append("\n");
                        countPreparingSucceeded++;
                    }
                    catch (LanguageException e) {
                        LOGGER.error(e.getMessage(), e);
                        new LogDBJobErrorHandler().handleError(context, e, null);
                        idsPreparingFailed.append(cpsTx.getId().toString());
                        idsPreparingFailed.append("\n");
                        countPreparingFailed++;
                    }
                }
                else if (cpsTx.getTxState().equals(CPSTransaction.TX_STATE_IN_PREPARING_FAILURE)) {
                    try {
                        // PrepairingFailure -> close
                        cps.closeCPSTx(cpsTx.getId(), HurricanScheduler.getSessionId());
                        idsPreparingFailureSucceeded.append(cpsTx.getId().toString());
                        idsPreparingFailureSucceeded.append("\n");
                        countPreparingFailureSucceeded++;
                    }
                    catch (LanguageException e) {
                        LOGGER.error(e.getMessage(), e);
                        new LogDBJobErrorHandler().handleError(context, e, null);
                        idsPreparingFailureFailed.append(cpsTx.getId().toString());
                        idsPreparingFailureFailed.append("\n");
                        countPreparingFailureFailed++;
                    }
                }
            }

            // Summen bilden
            countPreparing = countPreparingSucceeded + countPreparingFailed;
            countPreparingFailure = countPreparingFailureSucceeded + countPreparingFailureFailed;

            // Info-Mail verschicken
            sendMail(
                    mailConfig.getMailTo(),
                    HurricanConstants.EMAIL_SEPARATOR,
                    mailConfig.getMailSubject(),
                    StringTools.formatString(resourceReader.getValue("mail.body.info"), new Object[] {
                            countPreparing + countPreparingFailure, countPreparing,
                            countPreparingSucceeded, idsPreparingSucceeded.toString(), countPreparingFailed,
                            idsPreparingFailed.toString(), countPreparingFailure, countPreparingFailureSucceeded,
                            idsPreparingFailureSucceeded.toString(), countPreparingFailureFailed,
                            idsPreparingFailureFailed.toString() }), context
            );
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

    /**
     * Initialisiert diesen Job: <ul> <li>Properties aus Resourcedatei <li>mailTo (darf nicht null sein) aus DB
     * (T_REGISTRY) <li>mailCC aus DB (T_REGISTRY) <li>mailSubject aus Resourcedatei </ul>
     */
    private void initJob() throws Exception {
        try {
            resourceReader = new ResourceReader(RESOURCE);
            RegistryService rs = getCCService(RegistryService.class);
            mailConfig = new CPSJobMailConfig();
            mailConfig.initialize(resourceReader, rs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerException("Error initializing job 'CPSFinishExpiredTxJob': " + e.getMessage());
        }
    }
}
