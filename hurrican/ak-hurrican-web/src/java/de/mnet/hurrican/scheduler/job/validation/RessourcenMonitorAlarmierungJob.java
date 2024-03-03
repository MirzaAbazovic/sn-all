/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2009 10:47:14
 */
package de.mnet.hurrican.scheduler.job.validation;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.hurrican.model.cc.RSMonitorRun;
import de.augustakom.hurrican.service.cc.MonitorService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

/**
 * Job zum Starten des Ressourcen-Monitors
 *
 *
 */
public class RessourcenMonitorAlarmierungJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(RessourcenMonitorAlarmierungJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            MonitorService ms = CCServiceFinder.instance().getCCService(MonitorService.class);
            // Rangierungsmonitor
            ms.monitorAlarmierung(RSMonitorRun.RS_REF_TYPE_RANG_MONITOR);
            // Port-Monitor
            ms.monitorAlarmierung(RSMonitorRun.RS_REF_TYPE_EQ_MONITOR);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

}
