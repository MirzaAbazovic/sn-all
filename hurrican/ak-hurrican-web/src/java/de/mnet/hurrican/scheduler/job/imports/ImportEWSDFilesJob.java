/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2011 15:14:04
 */
package de.mnet.hurrican.scheduler.job.imports;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.service.cc.EWSDService;
import de.mnet.hurrican.scheduler.job.errorhandler.ConfigureableSendMailJobWarningsHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.hurrican.scheduler.model.JobExecution;
import de.mnet.hurrican.scheduler.service.SchedulerJobService;
import de.mnet.hurrican.scheduler.utils.JobExecutionContextHelper;

/**
 * Import von EWSD Dateien zur automatischen Portfreigabe <br> Job-Konfiguration (fuer EWSD-Datenimport): <br> Key:
 * Value <br> ${importEWSDFilesJob.ewsd.import.dir}: Angabe des Import-Verzeichnisses fuer die EWSD-Dateien <br>
 *
 *
 */
public class ImportEWSDFilesJob extends AbstractImportJob {

    private static final Logger LOGGER = Logger.getLogger(ImportEWSDFilesJob.class);

    /**
     * Key fuer das zu konfigurierende Verzeichnis, aus dem die EWSD-Dateien eingelesen werden
     */
    private static final String EWSD_IMPORT_DIR = "import.dir";

    private JobExecutionContext context;
    private File destinationDir;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("Execute job 'ImportEWSDFilesJob'.");
        this.context = context;
        try {
            // Change Date ermitteln
            SchedulerJobService schedulerJobService = getSchedulerService(SchedulerJobService.class);
            // JobName = 'id' Attribut der Spring <bean> (Job Detail)
            JobExecution jobExecution = schedulerJobService.findLastSuccessfullJobExecution(context.getJobDetail()
                    .getName());
            Date lastImport = ((jobExecution != null) && (jobExecution.getStartTime() != null)) ? jobExecution
                    .getStartTime() : null;
            // Wenn Job bisher nicht gestartet wurde einfach in die Vergangenheit setzten, damit der Import durchgeführt
            // wird
            if (lastImport == null) {
                Calendar cal = Calendar.getInstance();
                cal.set(2000, 5, 5);
                lastImport = cal.getTime();
            }
            // import EWSD-Data
            this.doImport(lastImport);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

    /**
     * Import von EWSD Dateien
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "REC_CATCH_EXCEPTION", justification = "Catch all ist hier gewollt")
    private void doImport(Date lastImport) {
        try {
            String destinationLocation = (String) JobExecutionContextHelper.getJobDataMapObject(context,
                    EWSD_IMPORT_DIR);
            destinationDir = new File(destinationLocation);
            if ((destinationDir == null) || !destinationDir.isDirectory()) {
                throw new JobExecutionException("Import-Directory for EWSD-Files (" + destinationLocation
                        + ") not defined or does not exist!");
            }

            EWSDService ewsdService = getCCService(EWSDService.class);

            File[] filesOfDestinationDir = destinationDir.listFiles();
            List<String> importFiles = new ArrayList<>();
            AKWarnings importWarnings = new AKWarnings();

            for (File file : filesOfDestinationDir) {
                // checken, ob die aktuell vorhandenen Dateien bereits importiert wurden
                if (file.lastModified() > lastImport.getTime()) {
                    // in Import-Fileliste aufnehmen
                    importFiles.add(file.getAbsolutePath());
                    LOGGER.info("add File: " + file.getAbsolutePath() + " to EWSD-Import");
                }
                else {
                    // Warnung an OVST-Resourcen@m-net.de
                    importWarnings.addAKWarning(this,
                            "Die Datei:" + file.getAbsolutePath() + " wurde nicht importiert, "
                                    + "da diese seit dem letzten Import (am " + lastImport.toString()
                                    + ") nicht aktualisiert wurde!"
                    );
                }
            }

            // Wenn keine Dateien zu importieren sind, werden die bestehenden Daten auch nicht geloescht
            if (!importFiles.isEmpty()) {
                // vorhandene Rangierungen loeschen
                ewsdService.deletePortGesamt();
                // zu importierende Files schlussendlich importieren
                ewsdService.importEWSDFiles(importFiles);
            }

            // vorhandene Warnungen per Mail verschicken
            if (!importWarnings.isEmpty()) {
                // Importierte Dateien auflisten
                StringBuilder messageAboutImportedFiles = new StringBuilder();
                messageAboutImportedFiles.append(SystemUtils.LINE_SEPARATOR);
                messageAboutImportedFiles.append("FOLGENDE DATEIEN WURDEN IMPORTIERT: ");
                for (String file : importFiles) {
                    messageAboutImportedFiles.append(SystemUtils.LINE_SEPARATOR);
                    messageAboutImportedFiles.append(file);
                }
                importWarnings.addAKWarning(this, messageAboutImportedFiles.toString());
                new ConfigureableSendMailJobWarningsHandler()
                        .handleWarnings(context, importWarnings, "ovstInfoMailMsg");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

}
