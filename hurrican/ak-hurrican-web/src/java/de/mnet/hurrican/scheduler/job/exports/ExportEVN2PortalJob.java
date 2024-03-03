/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.04.2007 15:10:17
 */
package de.mnet.hurrican.scheduler.job.exports;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.tools.file.FileTools;
import de.augustakom.common.tools.lang.StringTools;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerException;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzInterruptableJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;

/**
 * Job fuer den Export der EVNs fuer das Kundenportal Muenchen.
 *
 *
 */
public class ExportEVN2PortalJob extends AKAbstractQuartzInterruptableJob {

    private static final Logger LOGGER = Logger.getLogger(ExportEVN2PortalJob.class);

    private static final String SUFFIX_PDF_EVN = "EVN.pdf";
    private static final String SUFFIX_ASCII_EVN = ".txt";
    private static final String PREFIX_ASCII_FILELIST = "filelist";

    // notwendige Parameter
    private Long billRunId; // BillRun-Id
    private String billingStream; // Billing-Stream
    private String billingYear; // Rechnungsjahr
    private String billingMonth; // Rechnungsmonat
    private String email; // eMail-Adressen fuer Benachrichtigungen
    private String srcDirEvnPDF; // Basisverzeichnis fuer die EVNs im PDF-Format
    private String srcDirEvnASCII; // Basisverzeichnis fuer die EVNs im ASCII-Format
    private String portalImportEvnPDF; // Importverzeichnis fuer die PDF Rechnungen ins Portal
    private String portalImportEvnASCII; // Importverzeichnis fuer die ASCII Rechnungen ins Portal

    private List<String> errors;

    /**
     * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.quartz.JobExecutionContext)
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            super.executeInternal(context);
            initJob();

            ExecutorService sigExecutor = Executors.newCachedThreadPool();

            ExportASCIICall asciiCall = new ExportASCIICall();
            Future<Boolean> asciiFuture = sigExecutor.submit(asciiCall);

            ExportPDFCall pdfCall = new ExportPDFCall();
            Future<Boolean> pdfFuture = sigExecutor.submit(pdfCall);

            if (!asciiFuture.isDone()) {
                LOGGER.info("asciiFuture is not finished - call get() to wait for it");
                Boolean result = asciiFuture.get();
                LOGGER.info("Result of asciiFuture: " + result);
            }

            if (!pdfFuture.isDone()) {
                LOGGER.info("pdfFuture is not finished - call get() to wait for it");
                Boolean result = pdfFuture.get();
                LOGGER.info("Result of pdfFuture: " + result);
            }

            StringBuilder errorMessages = new StringBuilder();
            for (String err : errors) {
                errorMessages.append(err);
                errorMessages.append(SystemUtils.LINE_SEPARATOR);
            }

            super.sendMail(email, "EVN-Export beendet",
                    "EVNs im PDF und ASCII Format wurden zum Kundenportal Muenchen exportiert.\n\n" +
                            "Aufgetretene Fehler:\n" + errorMessages.toString(),
                    context
            );
        }
        catch (Exception e) {
            LOGGER.error("Fehler beim EVN-Export", e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            super.sendMail(email, "Fehler waehrend dem EVN-Export!",
                    ExceptionUtils.getFullStackTrace(e), context);
        }
    }

    /**
     * Exportiert alle EVNs im ASCII-Format zum Kundenportal Muenchen.
     */
    private void exportEvnASCII() throws AKSchedulerException {
        try {
            File destDir = new File(portalImportEvnASCII);
            File baseDir = new File(srcDirEvnASCII);
            File[] subDirs = baseDir.listFiles();
            if ((subDirs == null) || (subDirs.length == 0)) {
                LOGGER.warn("exportEvnASCII() - Keine EVNs im konfigurierten Verzeichnis gefunden vorhanden");
                return;
            }
            for (File subDir : subDirs) {
                if (subDir.isDirectory()) {
                    File[] evns = subDir.listFiles();
                    File destSubDir = new File(destDir, subDir.getName());
                    if (!destSubDir.exists() && !destSubDir.mkdir()) {
                        LOGGER.error("Fehler beim erstellen des out-Verzeichnisses: " + destSubDir);
                        throw new IOException("Fehler beim erstellen des out-Verzeichnisses: " + destSubDir);
                    }
                    iterateOverEvnsAndSaveAscii(subDir, evns, destSubDir);
                }
            }
        }
        catch (IOException e) {
            throw new AKSchedulerException("Fehler beim ASCII EVN-Export", e);
        }
    }

    private void iterateOverEvnsAndSaveAscii(File subDir, File[] evns, File destSubDir) {
        for (File evn : evns) {
            try {
                if (evn.getName().endsWith(SUFFIX_ASCII_EVN)) {
                    FileTools.copyFile2Dir(evn, destSubDir, false, true);
                }
                else if (evn.getName().toLowerCase().startsWith(PREFIX_ASCII_FILELIST)) {
                    saveEvnPrefixAsciiFileList(subDir, destSubDir, evn);
                }
            }
            catch (Exception e) {
                LOGGER.error("Error copying file " + evn.getName(), e);
                errors.add("Error copying file " + evn.getName());
            }
        }
    }

    private void saveEvnPrefixAsciiFileList(File subDir, File destSubDir, File evn) throws ParseException, IOException {
        long resellerNr = 1;
        try {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setParseIntegerOnly(true);
            resellerNr = nf.parse(subDir.getName()).longValue();
        }
        catch (NumberFormatException e) {
            Log.warn("Konnte Reseller-Nr. nicht aus Verzeichnisname parsen: "
                    + subDir.getName());
        }
        String destFileName = evn.getName() + "_" + resellerNr;
        FileTools.copyFile(evn, new File(destSubDir, destFileName), false);
    }

    /**
     * Exportiert alle EVNs im PDF-Format zum Kundenportal Muenchen.
     */
    private void exportEvnPDF() throws AKSchedulerException {
        try {
            File destDir = new File(portalImportEvnPDF);
            File pdfDir = new File(srcDirEvnPDF);
            File[] evns = pdfDir.listFiles();
            if ((evns == null) || (evns.length == 0)) {
                LOGGER.warn("exportEvnPDF() - Keine EVNs im konfigurierten Verzeichnis gefunden vorhanden");
                return;
            }
            iterateOverWvnsAndSavePDF(destDir, evns);
        }
        catch (Exception e) {
            throw new AKSchedulerException("Fehler beim PDF EVN-Export", e);
        }
    }

    private void iterateOverWvnsAndSavePDF(File destDir, File[] evns) {
        for (File evn : evns) {
            try {
                if (evn.getName().endsWith(SUFFIX_PDF_EVN)) {
                    FileTools.copyFile2Dir(evn, destDir, false, true);
                }
            }
            catch (Exception e) {
                LOGGER.error("Error copying file " + evn.getName(), e);
                errors.add("Error copying file " + evn.getName());
            }
        }
    }

    @Override
    protected void handleInterrupt() {
        // intentionally left blank
    }

    /**
     * Initialisiert den Job.
     */
    private void initJob() throws AKSchedulerException {
        errors = new ArrayList<String>();
        readJobParameters();
        checkJobParameters();
    }

    protected void readJobParameters() throws AKSchedulerException {
        billRunId = (Long) getJobDataMapObjectFromTrigger(getJobExecCtx(), "bill.run.id");
        billingStream = (String) getJobDataMapObjectFromTrigger(getJobExecCtx(), "bill.stream");
        billingYear = (String) getJobDataMapObjectFromTrigger(getJobExecCtx(), "bill.year");
        billingMonth = (String) getJobDataMapObjectFromTrigger(getJobExecCtx(), "bill.month");
        billingMonth = StringTools.fillToSize(billingMonth, 2, '0', true);

        email = (String) getJobDataMapObjectFromTrigger(getJobExecCtx(), "email");
        srcDirEvnPDF = StringTools.formatString((String) getJobDataMapObjectFromTrigger
                (getJobExecCtx(), "src.dir.evn.pdf"), new Object[] { billingStream, billingYear, billingMonth,
                billRunId.toString() }, null);
        srcDirEvnASCII = StringTools.formatString((String) getJobDataMapObjectFromTrigger
                (getJobExecCtx(), "src.dir.evn.ascii"), new Object[] { billingStream, billingYear, billingMonth,
                billRunId.toString() }, null);
        portalImportEvnPDF = (String) getJobDataMapObjectFromTrigger(getJobExecCtx(), "portal.import.evn.pdf");
        portalImportEvnASCII = (String) getJobDataMapObjectFromTrigger(getJobExecCtx(), "portal.import.evn.ascii");
    }

    protected void checkJobParameters() throws AKSchedulerException {
        if ((billRunId == null) || StringUtils.isBlank(billingYear) || StringUtils.isBlank(billingMonth)) {
            throw new AKSchedulerException("Billing-Periode ist nicht definiert!");
        }

        if (StringUtils.isBlank(srcDirEvnPDF) || StringUtils.isBlank(srcDirEvnASCII)) {
            throw new AKSchedulerException("Source-Verzeichnisse fuer EVNs sind nicht definiert!");
        }

        if (StringUtils.isBlank(portalImportEvnPDF)) {
            throw new AKSchedulerException("Import-Verzeichniss PDF fuer Portal ist nicht definiert!");
        }
        if (StringUtils.isBlank(portalImportEvnASCII)) {
            throw new AKSchedulerException("Import-Verzeichniss ASCII fuer Portal ist nicht definiert!");
        }
    }

    /**
     * Callable-Implementierung fuer das Kopieren der ASCII-Files
     */
    class ExportASCIICall implements Callable<Boolean> {
        /**
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Boolean call() throws Exception {
            exportEvnASCII();
            return Boolean.TRUE;
        }
    }

    /**
     * Callable-Implementierung fuer das Kopieren der PDF-Files
     */
    class ExportPDFCall implements Callable<Boolean> {
        /**
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Boolean call() throws Exception {
            exportEvnPDF();
            return Boolean.TRUE;
        }
    }

}
