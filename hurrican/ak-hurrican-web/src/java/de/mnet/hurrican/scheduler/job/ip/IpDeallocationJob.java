/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.12.2011 11:07:54
 */
package de.mnet.hurrican.scheduler.job.ip;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.ConfigureableSendMailJobWarningsHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

/**
 * Gibt alle zur Freigabe vorgemerkten IP-Adressen in monline frei
 */
public class IpDeallocationJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(IpDeallocationJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("Execute job 'IpDeallocationJob'.");
        IPAddressService ipAddrService = null;
        try {
            ipAddrService = getCCService(IPAddressService.class);
            AKWarnings warnings = ipAddrService.releaseIPAddresses(HurricanScheduler.getSessionId());
            if ((warnings != null) && warnings.isNotEmpty()) {
                LOGGER.warn(warnings.getWarningsAsText());
                new ConfigureableSendMailJobWarningsHandler()
                        .handleWarnings(context, warnings, "ipDeallocationMailMsg");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }
}
