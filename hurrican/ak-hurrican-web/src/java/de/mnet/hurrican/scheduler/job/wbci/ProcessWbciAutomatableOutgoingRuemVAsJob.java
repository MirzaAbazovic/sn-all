/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.14
 */
package de.mnet.hurrican.scheduler.job.wbci;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.authentication.model.AKUser;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.wbci.service.WbciAutomationDonatingService;

/**
 * Job zur automatischen Weiterverarbeitung von ausgehenden (=gesendeten) RUEM-VAs. (M-net = abgebender Carrier)<br/>
 * (Nach dem Versand der RUEM-VA muessen z.B. die Taifun- und Hurrican-Auftraege gekuendigt und entsprechende
 * Kuendigungsanschreiben erstellt / versendet werden.
 */
public class ProcessWbciAutomatableOutgoingRuemVAsJob extends AbstractWbciJob {

    private static final Logger LOGGER = Logger.getLogger(ProcessWbciAutomatableOutgoingRuemVAsJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            final WbciAutomationDonatingService wbciAutomationDonatingService = getWbciAutomationDonatingService();
            AKUser user = getCurrentUser(HurricanScheduler.getSessionId());
            final Collection<String> vaIdsRuemVa = wbciAutomationDonatingService
                    .processAutomatableOutgoingRuemVas(user, HurricanScheduler.getSessionId());
            LOGGER.info(String.format("Automatically processed the donating RUEM-VAs with the following VA-IDs:  %s",
                    vaIdsRuemVa));
        }
        catch (Exception e) {
            handleError(context, e);
        }
    }

}
