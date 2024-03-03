/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2007 11:51:23
 */
package de.mnet.hurrican.scheduler.job.exports;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.google.common.base.Charsets;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.file.FileTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.billing.view.RInfo2KundeView;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.service.billing.RechnungsService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerException;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerFindException;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerStoreException;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzInterruptableJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.model.SignaturedFile;
import de.mnet.hurrican.scheduler.service.SignatureService;

/**
 * Job fuer die Signatur der Rechnungs-PDFs. <br>
 * <p/>
 * Rechnungen werden nicht nicht mehr signiert (rechtlich nicht mehr notwendig), sondern direkt in das Portal-Import
 * Portal-Import Verzeichnis vom Kundenportal Muenchen verschoben.
 *
 *
 */
public class SignatureBillPDFsJob extends AKAbstractQuartzInterruptableJob {

    private static final Logger LOGGER = Logger.getLogger(SignatureBillPDFsJob.class);

    // notwendige Parameter
    private boolean sendStatusMail = true; // Flag, ob Status-EMails gesendet werden sollen
    private Long billRunId; // BillRun-Id
    private String billingStream; // Billing-Stream
    private String billingYear; // Rechnungsjahr
    private String billingMonth; // Rechnungsmonat
    private String email; // eMail-Adressen fuer Benachrichtigungen
    private String srcDirBill; // Taifun-Dir mit den Rechnungs-PDFs (Basisverzeichnis)

    private String portalImportBase; // Basisverzeichnis fuer den Portalimport
    private String portalImportBill; // Importverzeichnis fuer die Rechnungen ins Portal

    private List<String> errors;

    private Map<String, RInfo2KundeView> sap2RInfoKundeView;

    private RechnungsService rechnungsService;

    private FeatureService featureService;


    private static final String BILL_ONLINE = "online";
    private static final String BILL_OFFLINE = "offline";

    /**
     * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.quartz.JobExecutionContext)
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            // Konfiguration auslesen...
            super.executeInternal(context);
            initJob();

            // Mapping-File SAP-DebNr zu Portalaccount erstellen
            createMappingFile();

            // Rechnungen kopieren
            featureService = getCCService(FeatureService.class);
            if (featureService.isFeatureOnline(Feature.FeatureName.FAST_COPY_BILL_TO_PORTAL_IMPORT)) {
                copyBill2PortalImportFast();
            }
            else {
                copyBill2PortalImport();
            }

            StringBuilder errorMessages = new StringBuilder();
            for (String err : errors) {
                errorMessages.append(err);
                errorMessages.append(SystemUtils.LINE_SEPARATOR);
            }

            super.sendMail(email, "Rechnungssignatur u. -Export beendet",
                    "Rechnungen wurden fuer das Portal bereit gestellt.\n\n" +
                            "Aufgetretene Fehler:\n" + errorMessages.toString(),
                    context
            );
        }
        catch (Exception e) {
            LOGGER.error("Fehler waehrend Rechnungsexport", e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            super.sendMail(email, "Fehler waehrend Rechnungsexport!", ExceptionUtils.getFullStackTrace(e), context);
        }
    }

    /**
     * Ermittelt/Laedt eine Map mit dem Mapping-Daten (SAP-DebNr zu Portalaccount).
     */
    private void loadMappings() throws AKSchedulerException {
        sap2RInfoKundeView = new HashMap<>();
        try {
            List<RInfo2KundeView> views =
                    rechnungsService.findRInfo2KundeViews(billRunId, billingYear, billingMonth);
            if (CollectionTools.isNotEmpty(views)) {
                for (RInfo2KundeView view : views) {
                    if (StringUtils.isBlank(view.getExtDebitorId())) {
                        throw new AKSchedulerException("Debitoren-ID bei Kunde " + view.getKundeNo() +
                                " ist nicht gesetzt! Mapping kann nicht erstellt werden!");
                    }
                    else {
                        sap2RInfoKundeView.put(view.getExtDebitorId(), view);
                    }
                }
            }

            if ((sap2RInfoKundeView == null) || (sap2RInfoKundeView.isEmpty())) {
                throw new AKSchedulerException(
                        "Es konnten keine Mappings fuer den Rechnungsexport ermittelt werden!");
            }
        }
        catch (AKSchedulerException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error("Fehler beim Ermitteln der Mapping-Liste", e);
            throw new AKSchedulerException("Fehler beim Ermitteln der Mapping-Liste: " + e.getMessage(), e);
        }
    }

    /**
     * Erzeugt ein CSV-File mit den Mapping-Daten von SAP-DebNr zu Portalaccount.
     */
    private void createMappingFile() throws AKSchedulerException {
        try {
            loadMappings();
            OutputStreamWriter writer = null;
            BufferedWriter bw = null;

            FileOutputStream outputStream = new FileOutputStream(portalImportBase +
                    (portalImportBase.endsWith(SystemUtils.FILE_SEPARATOR) ? "" : SystemUtils.FILE_SEPARATOR) +
                    billingStream + "_SAPDebNr2Account_" + billingYear + billingMonth + ".txt");
            writer = new OutputStreamWriter(outputStream, Charsets.UTF_8);
            bw = new BufferedWriter(writer);

            Set<String> keys = sap2RInfoKundeView.keySet();
            Iterator<String> keyIt = keys.iterator();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                RInfo2KundeView view = sap2RInfoKundeView.get(key);

                writer.write(key);
                writer.write(";");
                writer.write(view.getSAPDebNrByReseller());
                writer.write(";");
                writer.write((BooleanTools.nullToFalse(view.getInvMaxi())) ? BILL_ONLINE : BILL_OFFLINE);
                writer.write(SystemUtils.LINE_SEPARATOR);
                writer.flush();
            }
            bw.close();
            writer.close();
            sendStatusMail("Mapping-File mit SAP-Debitorennummer zu Portal-Account generiert.");
            LOGGER.debug("******************* done!");

            Thread.sleep(30000);
        }
        catch (AKSchedulerException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error("Bei der Erstellung des Mapping-Files ist ein Fehler aufgetreten", e);
            throw new AKSchedulerException(
                    "Bei der Erstellung des Mapping-Files ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    private void copyBill2PortalImportFast() throws IOException {
        final Path srcDir = Paths.get(srcDirBill);
        if (!Files.isDirectory(srcDir)) {
            throw new IOException("Source-Verzeichnis fuer Rechnungen nicht vorhanden: " + srcDirBill);
        }

        long billsCopied = 0L;
        final Path destDir = Paths.get(portalImportBill);
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(srcDir)) {
            for (Path source : stream) {
                if (isInterrupted()) {
                    break;
                }

                final String filename = source.getFileName().toString();
                final boolean isPdfBill =
                        filename.matches("\\w\\d+_\\d+\\.pdf") && !StringUtils.contains(filename, "EVN");
                if (!isPdfBill) {
                    continue;
                }

                final Path target = destDir.resolve(source.getFileName());
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                billsCopied++;
            }
        }

        if (!isInterrupted()) {
            LOGGER.info("Rechnungen kopiert: " + billsCopied);
            sendStatusMail("Es wurden " + billsCopied + " Dateien nach 'portal.import' kopiert.");
        }
    }

    private void copyBill2PortalImport() throws ServiceNotFoundException, AKSchedulerFindException,
            AKSchedulerStoreException, IOException {
        SignatureService sigService = getSchedulerService(SignatureService.class);

        File srcDir = new File(srcDirBill);
        if (!srcDir.isDirectory()) {
            throw new IOException("Source-Verzeichnis fuer Rechnungen nicht vorhanden: " + srcDirBill);
        }

        File destDir = new File(portalImportBill);
        File[] bills = srcDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                // nur Dateien ohne(!) "EVN" im Namen akzeptieren
                return (pathname.getName().matches("\\w\\d+_\\d+\\.pdf") && !StringUtils.contains(pathname.getName(),
                        "EVN"));
            }
        });
        int billsCopied = 0;

        for (File bill : bills) {
            if (!isInterrupted()) {
                String filename = bill.getName();
                LOGGER.debug(filename);
                if (StringUtils.isNotBlank(filename)) {
                    SignaturedFile sigFile = sigService.findSignaturedFile(filename, false);
                    if (sigFile != null) {
                        // bereits kopiert ...
                        continue;
                    }
                    FileTools.copyFile2Dir(bill, destDir, false, true);
                    sigFile = SignaturedFile.createSF(billingYear, billingMonth, filename, bill.getAbsolutePath(),
                            billingStream);
                    Date now = new Date();
                    sigFile.setSignatureStart(now);
                    sigFile.setSignatureEnd(now);
                    sigService.save(sigFile);
                    billsCopied++;
                }
            }
        }

        if (!isInterrupted()) {
            LOGGER.info("Anzahl Rechnungen: " + bills.length + ", kopiert: " + billsCopied);
            sendStatusMail("Es wurden " + billsCopied + " Dateien nach 'portal.import' kopiert.");
        }
    }


    @Override
    protected void handleInterrupt() {
    }

    /**
     * Initialisiert den Job.
     */
    private void initJob() throws AKSchedulerException {
        errors = new ArrayList<>();
        readJobParameters();
        checkJobParameters();

        try {
            rechnungsService = getBillingService(RechnungsService.class);

            LOGGER.info("*****************************************");
            LOGGER.info("* Bill period  : " + billingYear + "/" + billingMonth);
            LOGGER.info("* ");
            LOGGER.info("* sleep for 5 sec before job-start");
            Thread.sleep(5000);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerException("RechnungsService konnte nicht ermittelt werden: " + e.getMessage(), e);
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
                new Object[] { billingStream, billingYear, billingMonth, billRunId.toString() }, null
        );
        portalImportBase = (String) getJobDataMapObjectFromTrigger(getJobExecCtx(), "portal.import.base");
        portalImportBill = (String) getJobDataMapObjectFromTrigger(getJobExecCtx(), "portal.import.bill");
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

        if (StringUtils.isBlank(portalImportBase)) {
            throw new AKSchedulerException("Basisverzeichnis fuer den Portal-Import ist nicht definiert!");
        }

        if (StringUtils.isBlank(portalImportBill)) {
            throw new AKSchedulerException("Rechnungsimport-Verzeichniss fuer Portal ist nicht definiert!");
        }
    }

    /**
     * Schickt eine Status-EMail mit dem angegebenen Body
     */
    private void sendStatusMail(String message) {
        if (sendStatusMail) {
            super.sendMail(email, "Signatur-/Export Status", message, getJobExecCtx());
        }
    }

}
