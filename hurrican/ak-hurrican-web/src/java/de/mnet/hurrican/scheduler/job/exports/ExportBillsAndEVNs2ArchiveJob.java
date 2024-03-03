/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.04.2007 15:11:00
 */
package de.mnet.hurrican.scheduler.job.exports;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.tools.file.FileTools;
import de.augustakom.common.tools.lang.ArrayTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.billing.view.RInfo2KundeView;
import de.augustakom.hurrican.service.billing.RechnungsService;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerException;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzInterruptableJob;
import de.mnet.hurrican.scheduler.model.ExportedBillingFile;
import de.mnet.hurrican.scheduler.service.SchedulerLogService;

/**
 * Job-Implementierung, um die Rechnungen und EVNs zum ScanView-Archiv zu exportieren. <br>
 * Fuer jede Datei wird eine sog. Control-Datei (Endung ctl) erzeugt. In dieser Datei ist die Verschlagwortung fuer die
 * jeweils zu archivierende Datei enthalten.
 */
public class ExportBillsAndEVNs2ArchiveJob extends AKAbstractQuartzInterruptableJob {

    private static final Logger LOGGER = Logger.getLogger(ExportBillsAndEVNs2ArchiveJob.class);
    private static final String ENCODING = "ISO-8859-1";
    private static final String RESOURCE = "de.mnet.hurrican.scheduler.job.exports.resources.ExportBillsAndEVNs2ArchiveJob";

    private static final int SHORT_SLEEP = 5000;
    private static final int LONG_SLEEP = 60000;

    // Job-Parameter
    private String billingStream; // Billing-Stream
    private String billingYear; // Rechnungsjahr
    private String billingMonth; // Rechnungsmonat
    private Long billRunId; // Id des BillRuns
    private String email; // eMail-Adressen fuer Benachrichtigungen
    private String srcDirBill; // Taifun-Dir mit den Rechnungs-PDFs (Basisverzeichnis)
    private String srcDirEvnPDF; // Basisverzeichnis fuer die EVNs im PDF-Format
    private String srcDirEvnASCII; // Basisverzeichnis fuer die EVNs im ASCII-Format
    private String archiveImport; // Import-Verzeichnis vom ScanView-Archiv
    private String ctlFileBaseName;
    private String archiveDb;

    private static final String CTL_DOC_ART_BILL = "Rechnung";
    private static final String CTL_DOC_ART_EVN = "EVN";
    private static final String CTL_TYPE_PHONE = "Telefonie";
    private static final String CTL_DOC_TYPE_BILL = "240"; // DocType fuer Rechnungen
    private static final String CTL_DOC_TYPE_EVN = "252"; // DocType fuer PDF EVNs
    private static final String CTL_DOC_TYPE_EVN_ASCII = "251"; // DocType fuer ASCII EVNs

    private static final String SUFFIX_PDF_EVN = "EVN.pdf";
    private static final String SUFFIX_ASCII_EVN = ".txt";

    private ResourceReader resourceReader;
    private RechnungsService rechService;
    private Date invoiceDate;
    private String buchungsdatum;
    private String ctlContent;
    private List<String> errors;

    private SchedulerLogService logService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            super.executeInternal(context);
            initJob();

            if (!isInterrupted()) {
                exportEvnPDF();
            }

            if (!isInterrupted()) {
                exportEvnASCII();
            }

            if (!isInterrupted()) {
                exportBills2Archive();
            }

            super.sendMail(email, "Archiv-Export beendet",
                    "Die Rechnungen und EVNs wurden zum ScanView-Archiv exportiert.\n\n" +
                            "Aufgetretene Fehler:\n" + errors.toString(),
                    context
                    );
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            super.sendMail(email, "Fehler beim ScanView-Export!",
                    ExceptionUtils.getFullStackTrace(e), context);
        }
    }

    /**
     * Kopiert die Rechnungen in den Archiv-Import Ordner
     */
    private void exportBills2Archive() throws AKSchedulerException {
        try {
            File srcDir = new File(srcDirBill);
            File[] files = srcDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return (file.getName().matches("\\w\\d+_\\d+\\.pdf") && !file.getName().endsWith(SUFFIX_PDF_EVN)) ? true
                            : false;
                }
            });
            iterateOverBillsAndSave(files);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerException(e.getMessage(), e);
        }
    }

    private void iterateOverBillsAndSave(File[] files) {
        int count = 0;

        File destDir = new File(archiveImport);
        for (File toArchive : files) {
            try {
                if (!isArchived(toArchive)) {
                    // Control-File fuer Archiv-Import erzeugen
                    File ctlFile = createBillCtlFile(toArchive);
                    copyFileAndRenameCtlAfterwardsAndDoLogEntry(destDir, toArchive, ctlFile);

                    count = getCountAndWaitForThreshold(count);
                }
            }
            catch (Exception e) {
                errors.add("Error during copy process (Bills) to ScanView Archive: "
                        + e.getMessage() + "\nFile: " + toArchive.getAbsolutePath());
            }
        }
    }

    private int getCountAndWaitForThreshold(int count) throws AKSchedulerException {
        int count1 = count;
        count1++;
        if (count1 == 2500) {
            letFxApiWork();
            count1 = 0;
        }
        return count1;
    }

    /**
     * Export die EVNs im PDF-Format zum ScanView-Archiv
     */
    private void exportEvnPDF() throws AKSchedulerException {
        try {
            if (StringUtils.isBlank(srcDirEvnPDF)) {
                return;
            }

            File srcDir = new File(srcDirEvnPDF);
            File[] files = srcDir.listFiles(file -> file.getName().endsWith(SUFFIX_PDF_EVN));
            if (ArrayTools.isEmpty(files)) {
                return;
            }
            iterateOverAndSaveEvnsPDF(files);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerException(e.getMessage(), e);
        }
    }

    private void iterateOverAndSaveEvnsPDF(File[] files) {
        int count = 0;

        File destDir = new File(archiveImport);
        for (File toArchive : files) {
            try {
                if (!isArchived(toArchive)) {
                    // Control-File fuer Archiv-Import erzeugen
                    File ctlFile = createEVNCtlFile(toArchive);
                    copyFileAndRenameCtlAfterwardsAndDoLogEntry(destDir, toArchive, ctlFile);

                    count = getCountAndWaitForThreshold(count);
                }

                count = getCountAndWaitForThreshold(count);
            }
            catch (Exception e) {
                errors.add("Error during copy process (PDF-EVNs) to ScanView Archive: "
                        + e.getMessage() + "\nFile: " + toArchive.getAbsolutePath());
            }
        }
    }

    /**
     * Exportiert die EVNs im ASCII-Format zum ScanView-Archiv.
     */
    private void exportEvnASCII() throws AKSchedulerException {
        try {
            if (StringUtils.isBlank(srcDirEvnASCII)) {
                return;
            }

            File baseDir = new File(srcDirEvnASCII);
            File[] subDirs = baseDir.listFiles();
            if (ArrayTools.isEmpty(subDirs)) {
                return;
            }

            File destDir = new File(archiveImport);
            int count = 0;
            for (File subDir : subDirs) {
                if (subDir.isDirectory()) {
                    count = iterateOverSubdirsAndSaveEvnsAscii(destDir, count, subDir);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerException(e.getMessage(), e);
        }
    }

    private int iterateOverSubdirsAndSaveEvnsAscii(File destDir, int count, File subDir) {
        int count1 = count;
        File[] evns = subDir.listFiles();
        for (File toArchive : evns) {
            try {
                if (toArchive.getName().endsWith(SUFFIX_ASCII_EVN)) {
                    if (!isArchived(toArchive)) {
                        // Control-File fuer Archiv-Import erzeugen
                        File ctlFile = createEVNCtlFile(toArchive);
                        copyFileAndRenameCtlAfterwardsAndDoLogEntry(destDir, toArchive, ctlFile);

                        count1 = getCountAndWaitForThreshold(count1);
                    }

                    count1 = getCountAndWaitForThreshold(count1);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                errors.add("Error during copy process (Ascii-EVNs) to ScanView Archive: "
                        + e.getMessage() + "\nFile: " + toArchive.getAbsolutePath());
            }
        }
        return count1;
    }

    /*
     * Kopiert die Datei {@code toArchive} nach {@code destDir} und benennt das {@code ctlFile} von .tmp in .ctl um. Der
     * eigentliche Kopiervorgang wird 3x versucht, damit in einem Fehlerfall ein retry statt findet. Bei "normalen"
     * Fehlern wird der retry mit einer Verzoegerung von 5 Sek. durchgefuehrt; bei IOExceptions wird 1 Minute vor dem 
     * naechsten Retry gewartet.
     * Falls auch beim 3ten Retry ein Fehler auftritt, so wird dieser von der Methode weiter gereicht.
     */
    void copyFileAndRenameCtlAfterwardsAndDoLogEntry(File destDir, File toArchive, File ctlFile) throws Exception {
        boolean copySuccess = false;
        int retryCount = 0;
        Exception occuredException = null;

        while (!copySuccess && retryCount < 3) {
            try {
                retryCount++;
                occuredException = null;
                
                copyFile2Dir(destDir, toArchive);

                copySuccess = true;
            }
            catch (IOException e) {
                occuredException = e;
                LOGGER.error(String.format("IOException during copy of file %s (sleep before retry): %s",
                        toArchive.getAbsolutePath(), e.getMessage()));
                copySuccess = false;
                Thread.sleep(getSleepTime(false));
            }
            catch (Exception e) {
                occuredException = e;
                LOGGER.error(String.format("%s during copy of file %s (sleep before retry): %s",
                        e.getClass().getName(), toArchive.getAbsolutePath(), e.getMessage()));
                copySuccess = false;
                Thread.sleep(getSleepTime(true));
            }
        }

        if (copySuccess) {
            renameCtlFile(ctlFile);
            logArchive(toArchive);
        }
        else if (occuredException != null) {
            throw occuredException;
        }
    }

    void copyFile2Dir(File destDir, File toArchive) throws IOException {
        FileTools.copyFile2Dir(toArchive, destDir, false);
    }

    /**
     * Control-Files (mit Endung .ctl.tmp) fuer die Rechnung 'bill' erstellen
     */
    private File createBillCtlFile(File bill) {
        try {
            String billId = StringUtils.substringBetween(bill.getName(), "_", ".");

            // SAP-Nummer ermitteln
            RInfo2KundeView view = rechService.findRInfo2KundeView4BillId(billId, billingYear, billingMonth);

            if (view != null) {
                // CTL-File fuer Rechnung erzeugen
                return writeCtlFile(bill, CTL_DOC_TYPE_BILL, CTL_DOC_ART_BILL, billId, view);
            }
            else {
                errors.add("Invoice CTL-File could not be created. Reason: RInfo-View for Bill-Id not found: " + billId);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Control-Files (mit Endung .ctl.tmp) fuer die EVNs erstellen
     */
    private File createEVNCtlFile(File evn) {
        try {
            String filenameOrig = evn.getName();
            String billId = "";
            // Unterscheidung notwendig da Nuernberger Debitoren kuerzer (momentan nur pdf)
            if (filenameOrig.toLowerCase().endsWith("pdf")) {
                billId = StringUtils.substringBetween(filenameOrig, "_", ".");
            }
            else {
                billId = StringUtils.substringAfter(filenameOrig, "_");
                billId = StringUtils.substringBefore(billId, "_");
            }

            // SAP-Nummer fuer Archiv ermitteln
            RInfo2KundeView view = rechService.findRInfo2KundeView4BillId(billId, billingYear, billingMonth);
            if (view != null) {
                // Doc-Type abhaengig vom EVN-Typ (PDF oder ASCII) setzen
                String doyType = (filenameOrig.toLowerCase().endsWith("pdf"))
                        ? CTL_DOC_TYPE_EVN : CTL_DOC_TYPE_EVN_ASCII;

                // CTL-File fuer EVN erzeugen
                return writeCtlFile(evn, doyType, CTL_DOC_ART_EVN, billId, view);
            }
            else {
                LOGGER.warn("RInfo2KundeView not found for Bill-ID: " + billId);
                errors.add("EVN CTL-File could not be created. Reason: RInfo-View for Bill-Id not found: " + billId);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Erstellt ein Control-File fuer den Archiv-Import (mit Endung .ctl.tmp, damit der Import noch nicht sofort
     * stattfindet).
     * 
     * @param toArchive zu archivierendes File
     * @param docType Kennzeichnung des DocTypes (entspricht dem Archiv, in das das File archiviert werden soll)
     * @param docArt Art des zu archivierenden Dokuments (Rechnung, EVN)
     * @param billId Rechnungsnummer der Rechnung oder des EVNs
     * @param view View mit Informationen zu dem zu archivierenden File
     * @throws AKSchedulerException
     */
    private File writeCtlFile(File toArchive, String docType, String docArt, String billId, RInfo2KundeView view) {
        try {
            String filename = toArchive.getName();
            String ctlFileName = StringTools.formatString(ctlFileBaseName, new Object[] { filename}, null);
            File ctlFile = new File(ctlFileName);

            FileOutputStream outputStream = new FileOutputStream(ctlFileName, true);
            OutputStreamWriter oswriter = new OutputStreamWriter(outputStream, ENCODING);

            String kundeNo = "" + view.getKundeNo();
            String niederlassung = getNLText4Area(view.getAreaNo());
            String invoiceDateStr = DateTools.formatDate(invoiceDate, DateTools.PATTERN_DAY_MONTH_YEAR);

            String content = StringTools.formatString(ctlContent,
                    new Object[] { archiveDb, docType, kundeNo, view.getExtDebitorId(), docArt, billId,
                            CTL_TYPE_PHONE, niederlassung, invoiceDateStr, buchungsdatum,
                            StringUtils.trimToEmpty(view.getAddressName()),
                            StringUtils.trimToEmpty(view.getAddressVorname()),
                            StringUtils.trimToEmpty(view.getAddressStrasse()),
                            StringUtils.trimToEmpty(view.getPostfachOrPLZ()),
                            StringUtils.trimToEmpty(view.getAddressOrt()),
                            StringUtils.trimToEmpty(view.getAddressNr()), filename}, null
                    );
            oswriter.write(content);
            oswriter.close();

            return ctlFile;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Benennt das CTL-File um (entfernt den Suffix ".tmp"
     */
    void renameCtlFile(File file2Rename) {
        if (file2Rename != null) {
            boolean renamed = file2Rename.renameTo(
                    new File(StringUtils.substringBeforeLast(file2Rename.getAbsolutePath(), ".")));
            if (!renamed) {
                LOGGER.warn("File has not been renamed! File: " + file2Rename);
            }
        }
    }

    /**
     * Wartet so lange, bis im ScanView-Import nur noch 100 Dateien vorhanden sind.
     */
    private void letFxApiWork() throws AKSchedulerException {
        try {
            File arcDir = new File(archiveImport);
            File[] files = arcDir.listFiles();
            if (files != null) {
                while (files.length > 1000) {
                    LOGGER.info("let FxApi work ... sleep until arch-import has less than 1000 files");
                    Thread.sleep(getSleepTime(false));
                    files = arcDir.listFiles();
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerException(e.getMessage(), e);
        }
    }

    /**
     * Protokolliert die Archivierung des Files 'toArchive'.
     */
    void logArchive(File toArchive) {
        try {
            ExportedBillingFile exFile = new ExportedBillingFile();
            exFile.setBillingYear(billingYear);
            exFile.setBillingMonth(billingMonth);
            exFile.setFilename(toArchive.getName());
            exFile.setExportedAt(new Date());
            exFile.setExportedToDir(archiveImport);
            exFile.setBillingStream(billingStream);

            logService.logRechnungsExport(exFile);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Prueft, ob das File 'toArchive' bereits an das Archiv uebergeben wurde, bzw. ob eine entsprechende
     * Protokollierung vorhanden ist.
     */
    private boolean isArchived(File toArchive) {
        try {
            return logService.isLogged(billingYear, billingMonth, toArchive.getName());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Initialisiert den Job.
     */
    private void initJob() throws AKSchedulerException {
        try {
            errors = new ArrayList<String>();
            resourceReader = new ResourceReader(RESOURCE);
            ctlContent = resourceReader.getValue("CTL_CONTENT");

            readJobParameters();
            checkJobParameters();
            rechService = getBillingService(RechnungsService.class);
            invoiceDate = rechService.findInvoiceDate(billRunId);

            logService = getSchedulerService(SchedulerLogService.class);

            // Buchungsdatum berechnen
            Date bdate = DateTools.getLastDateOfMonth(
                    Integer.parseInt(billingYear), Integer.parseInt(billingMonth) - 1);
            buchungsdatum = (bdate != null) ? DateTools.formatDate(bdate, DateTools.PATTERN_DAY_MONTH_YEAR) : "";

            LOGGER.info("********************************************");
            LOGGER.info("* Billing year/month: " + billingYear + "/" + billingMonth);
            LOGGER.info("* Invoice-Date      : " + DateTools.formatDate(invoiceDate, DateTools.PATTERN_DAY_MONTH_YEAR));
            LOGGER.info("* Buchungsdatum     : " + buchungsdatum);
            LOGGER.info("* ");
            Thread.sleep(getSleepTime(true));
        }
        catch (AKSchedulerException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerException(e.getMessage(), e);
        }
    }

    protected void readJobParameters() throws AKSchedulerException {
        billRunId = (Long) getJobDataMapObjectFromTrigger(getJobExecCtx(), "bill.run.id");
        billingStream = (String) getJobDataMapObjectFromTrigger(getJobExecCtx(), "bill.stream");
        billingYear = (String) getJobDataMapObjectFromTrigger(getJobExecCtx(), "bill.year");
        billingMonth = (String) getJobDataMapObjectFromTrigger(getJobExecCtx(), "bill.month");
        billingMonth = StringTools.fillToSize(billingMonth, 2, '0', true);

        email = (String) getJobDataMapObjectFromTrigger(getJobExecCtx(), "email");
        srcDirBill = StringTools.formatString((String) getJobDataMapObjectFromTrigger
                (getJobExecCtx(), "src.dir.bills"),
                new Object[] { billingStream, billingYear, billingMonth, billRunId.toString()}, null);
        srcDirEvnPDF = StringTools.formatString((String) getJobDataMapObjectFromTrigger
                (getJobExecCtx(), "src.dir.evn.pdf"), new Object[] { billingStream, billingYear, billingMonth,
                        billRunId.toString()}, null);
        srcDirEvnASCII = StringTools.formatString((String) getJobDataMapObjectFromTrigger
                (getJobExecCtx(), "src.dir.evn.ascii"), new Object[] { billingStream, billingYear, billingMonth,
                        billRunId.toString()}, null);
        archiveImport = (String) getJobDataMapObjectFromTrigger(getJobExecCtx(), "portal.import");
        archiveDb = (String) getJobDataMapObjectFromTrigger(getJobExecCtx(), "archive.db");

        ctlFileBaseName = archiveImport + "{0}.ctl.tmp";
    }

    protected void checkJobParameters() throws AKSchedulerException {
        if (StringUtils.isBlank(billingStream)) {
            throw new AKSchedulerException("Billing-Stream ist nicht definiert!");
        }

        if (billRunId == null) {
            throw new AKSchedulerException("BillRun-ID ist nicht definiert!");
        }

        if (StringUtils.isBlank(billingYear) || StringUtils.isBlank(billingMonth)) {
            throw new AKSchedulerException("Billing-Periode ist nicht definiert!");
        }

        if (StringUtils.isBlank(srcDirBill)) {
            throw new AKSchedulerException("Source-Verzeichnisse der Rechnungen ist nicht definiert!");
        }
    }

    /**
     * Gibt den Namen fuer eine Niederlassung zurueck. <br>
     * Der Name ist im Job-Resourcefile definiert.
     */
    private String getNLText4Area(Long areaNo) {
        if (areaNo != null) {
            switch (areaNo.intValue()) {
                case 1:
                    return "Augsburg";
                case 2:
                    return "München";
                case 3:
                    return "Nürnberg";
                case 4:
                    return "Kempten";
                case 5:
                    return "Ulm";
                case 6:
                    return "Bayreuth";
                case 7:
                    return "Regensburg";
                case 8:
                    return "Würzburg";
                case 9:
                    return "Ingolstadt";
                default:
                    return "München";
            }
        }
        else {
            return "München";
        }
    }

    @Override
    protected void handleInterrupt() {
        // not used
    }
    
    int getSleepTime(boolean shortSleepTime) {
        return (shortSleepTime) ? SHORT_SLEEP : LONG_SLEEP;
    }

}
