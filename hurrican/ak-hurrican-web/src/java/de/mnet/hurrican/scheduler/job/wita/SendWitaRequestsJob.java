/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.2011 13:49:09
 */
package de.mnet.hurrican.scheduler.job.wita;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.transaction.UnexpectedRollbackException;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.common.tools.messages.AKWarnings;
import de.mnet.common.exceptions.ServiceException;
import de.mnet.hurrican.scheduler.job.base.AbstractSendScheduledRequestsJob;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.model.WitaSendLimit;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaSendMessageService;

/**
 * Scheduler-Job, der nicht uebermittelte {@link MnetWitaRequest}s ermittelt und diese erneut an den JMS versendet.
 * (Ueber den JMS Versand ist sichergestellt, dass evtl. konfigurierte {@link WitaSendLimit}s erneut ueberprueft werden
 * und der Versand somit nur dann erfolgt, wenn er auch freigegeben ist.)
 */
public class SendWitaRequestsJob extends AbstractSendScheduledRequestsJob {

    private static final Logger LOGGER = Logger.getLogger(SendWitaRequestsJob.class);

    private static final int MAX_SEND_BLOCK_SIZE = 100;
    // 12 weil wir max. 100 Request schicken wollen und jeweils 12 pro Geschaeftsfall 8*12=96 (<100)
    private static final int MAX_SEND_BLOCK_PER_GESCHAEFTSFALL = 12;
    private static final String MORE_REQUESTS_FOUND = "More than %s unsent MnetWitaRequests found; send was limitted to the first %s rows!";
    private static final String SUCCESSFUL_REQUESTS_SENT = "Successfully sent %s MnetWitaRequests over the SenWitaRequestJob";

    private MwfEntityService mwfEntityService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        AKWarnings warnings = new AKWarnings();
        try {
            List<Long> unsentRequestIds = getMwfEntityService()
                    .findUnsentRequestsForEveryGeschaeftsfall(MAX_SEND_BLOCK_PER_GESCHAEFTSFALL);
            if (CollectionTools.isEmpty(unsentRequestIds)) {
                return;
            }

            WitaSendMessageService witaSendMessageService = getCCService(WitaSendMessageService.class);
            int sendCount = 0;
            for (Long unsentRequestId : unsentRequestIds) {
                if (sendCount < MAX_SEND_BLOCK_SIZE) {
                    try {
                        boolean sent = witaSendMessageService.sendScheduledRequest(unsentRequestId);
                        if (sent) {
                            sendCount++;
                        }
                    }
                    catch (ServiceException | UnexpectedRollbackException e) {
                        handleError(context, e);
                    }
                }
                else {
                    warnings.addAKWarning(this, String.format(MORE_REQUESTS_FOUND, MAX_SEND_BLOCK_SIZE, MAX_SEND_BLOCK_SIZE));
                }
            }
            LOGGER.info(String.format(SUCCESSFUL_REQUESTS_SENT, sendCount));
        }
        catch (Exception e) {
            handleError(context, e);
        }
        if (warnings.isNotEmpty()) {
            // Are there any warnings stored? Dispatch them an continue
            handleWarnings(context, warnings);
        }
    }

    @Override
    protected ExceptionLogEntryContext getSchedulerExceptionLogEntryContext() {
        return ExceptionLogEntryContext.WITA_SCHEDULER_SEARCH_REQUEST_ERROR;
    }

    private MwfEntityService getMwfEntityService() throws ServiceNotFoundException {
        if (mwfEntityService == null) {
            mwfEntityService = getService(MwfEntityService.class);
        }
        return mwfEntityService;
    }
}
