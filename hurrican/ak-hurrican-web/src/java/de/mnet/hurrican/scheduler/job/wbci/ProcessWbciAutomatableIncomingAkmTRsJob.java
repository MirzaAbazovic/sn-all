/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.11.14
 */
package de.mnet.hurrican.scheduler.job.wbci;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.collections.CollectionTools;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.wbci.service.WbciAutomationService;

/**
 * Job zur automatischen Weiterverarbeitung von eingehenden (=empfangenen) AKM-TRs. (M-net = abgebender Carrier) <br/>
 * (Nach dem Empfang der AKM-TR muss ggf. eine WITA-Kuendigung fuer die TALs der Hurrican-Auftraege erstellt
 * werden - abhaengig von der Technologie und der Uebernahme-Strategie aus der AKM-TR)
 */
public class ProcessWbciAutomatableIncomingAkmTRsJob extends AbstractWbciJob {

    private static final Logger LOGGER = Logger.getLogger(ProcessWbciAutomatableIncomingAkmTRsJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            final WbciAutomationService wbciAutomationService = getWbciAutomationService();
            AKUser user = getCurrentUser(HurricanScheduler.getSessionId());
            final Collection<String> carrierRefIds = wbciAutomationService.processAutomatableIncomingAkmTrs(user);

            LOGGER.info(String.format("Automatically processed the incoming AKM-TRs and created the following WITA orders:  %s",
                    CollectionTools.formatCommaSeparated(carrierRefIds)));
        }
        catch (Exception e) {
            handleError(context, e);
        }
    }

}
