/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.12.2011 12:48:28
 */
package de.mnet.hurrican.scheduler.job.wita.archive;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.mnet.hurrican.archive.ArchiveDumperService;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.service.MwfEntityService;

/**
 * Scheduler-Job, der {@link Anlage}-Dokumente auf Viren pruefen laesst und danach im Scan-View archiviert
 */
public class ScanAndArchiveWitaFilesJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(ScanAndArchiveWitaFilesJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            MwfEntityService mwfEntityService = getService(MwfEntityService.class);
            ArchiveDumperService archiveDumperService = getService(ArchiveDumperService.class);
            List<Anlage> anlagen = mwfEntityService.findUnArchivedAnlagen();
            for (Anlage anlage : anlagen) {
                archiveAnlagen(context, mwfEntityService, archiveDumperService, anlage);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

    private void archiveAnlagen(JobExecutionContext context, MwfEntityService mwfEntityService,
            ArchiveDumperService archiveDumperService, Anlage anlage) {
        try {
            archiveDumperService.archiveDocument(anlage);
        }
        catch (Exception e) {
            // Anlage auf Error setzen, damit diese nicht sofort nochmal versucht wird zu archivieren
            anlage.setArchivingCancelReason(Anlage.CANCEL_ERROR_HAPPEND);
            mwfEntityService.store(anlage);

            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }
}


