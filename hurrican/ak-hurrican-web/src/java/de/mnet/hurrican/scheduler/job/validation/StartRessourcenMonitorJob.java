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

import de.augustakom.hurrican.service.cc.MonitorService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerException;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

/**
 * Job zum Starten des Ressourcen-Monitors
 *
 *
 */
public class StartRessourcenMonitorJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(StartRessourcenMonitorJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            Long rmType = null;

            // Ermittle Monitor-Typ
            Object param = getJobDataMapObject(context, "monitor.type");
            if (param == null) {
                param = getJobDataMapObjectFromTrigger(context, "monitor.type");
            }
            if (param instanceof String) {
                rmType = Long.valueOf((String) param);
            }
            else if (param instanceof Long) {
                rmType = (Long) param;
            }
            if (rmType == null) {
                throw new AKSchedulerException("Kein Monitor-Typ übergeben!");
            }

            MonitorService ms = CCServiceFinder.instance().getCCService(MonitorService.class);
            ms.startMonitor(rmType, HurricanScheduler.getSessionId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

}
