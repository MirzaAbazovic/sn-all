/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2007 09:56:51
 */
package de.mnet.hurrican.scheduler.job.maintainance;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.service.cc.CleanService;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

/**
 * Ueberprueft die Tabelle t_dn_leistung auf Leistungen zu nicht mehr gueltigen Rufnummern , archiviert diese und
 * loescht aus der Tabelle t_leistung_dn
 */
public class ClearLeistungDn extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(ClearLeistungDn.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            CleanService clService = getCCService(CleanService.class);
            AKWarnings warnings = clService.cleanLeistungDn();

            if ((warnings != null) && warnings.isNotEmpty()) {
                throw new SchedulerException("Warnings beim loeschen der Rufnummernleistungen :\n"
                        + warnings.getWarningsAsText());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }
}
