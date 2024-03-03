/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.05.2011 16:05:10
 */
package de.mnet.hurrican.scheduler.job.clearance;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.view.PhysikFreigebenView;
import de.augustakom.hurrican.service.cc.RangierungFreigabeService;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.ConfigureableSendMailJobWarningsHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobWarningsHandler;
import de.mnet.hurrican.scheduler.job.imports.ImportEWSDFilesJob;

/**
 * Führt die automatische Portfreigabe jede Nacht durch
 *
 *
 */
public class AutomaticClearanceOfPhysicsJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(ImportEWSDFilesJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("Execute job 'AutomaticClearanceOfPhysikJob'.");
        AKWarnings warnings = null;
        try {
            Map<Long, List<PhysikFreigebenView>> freigabebereit;
            RangierungFreigabeService rangierungFreigabeService = getCCService(RangierungFreigabeService.class);
            freigabebereit = rangierungFreigabeService.createPhysikFreigabeView(new Date(), null, false);
            warnings = rangierungFreigabeService.prepareAutomaticClearance(freigabebereit);
            warnings.addAKWarnings(rangierungFreigabeService.saveRangierungFreigabeInfos(freigabebereit));
            rangierungFreigabeService.rangierungenFreigeben(freigabebereit);
            sendWarningsToDepartments(context, warnings);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            sendWarningsToDepartments(context, warnings);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

    /**
     * Verschickt via Email Warnungen an den Fachbereich und die Entwickler
     */
    private void sendWarningsToDepartments(JobExecutionContext context, AKWarnings warnings) {
        if ((warnings != null) && !warnings.isEmpty()) {
            new SendMailJobWarningsHandler().handleWarnings(context, warnings);
            new ConfigureableSendMailJobWarningsHandler().handleWarnings(context, warnings, "ovstInfoMailMsg");
        }
    }
}
