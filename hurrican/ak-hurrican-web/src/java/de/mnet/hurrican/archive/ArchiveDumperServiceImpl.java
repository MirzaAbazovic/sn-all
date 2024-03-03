/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.12.2011 15:28:21
 */
package de.mnet.hurrican.archive;

import java.io.*;
import java.time.*;
import java.util.*;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveMetaData;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveMetaData.ArchiveFileExtension;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.MailService;
import de.augustakom.hurrican.service.exmodules.archive.ArchiveService;
import de.mnet.antivirus.scan.AntivirusScanService;
import de.mnet.antivirus.scan.AntivirusScanService.AntivirusScanStatus;
import de.mnet.common.service.HistoryService;
import de.mnet.hurrican.reporting.documentconvertor.service.DocumentConvertorService;
import de.mnet.hurrican.reporting.documentconvertor.service.DocumentConvertorService.MimeType;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.dao.TaskDao;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.AbgebendeLeitungenUserTask;
import de.mnet.wita.model.WitaCBVorgang;

@CcTxRequired
public class ArchiveDumperServiceImpl implements ArchiveDumperService {

    @Autowired
    private DocumentConvertorService documentConvertorService;

    @Autowired
    private ArchiveService archiveService;

    @Autowired
    private AntivirusScanService antivirusScanService;

    @Autowired
    private BillingAuftragService billingAuftragService;
    @Autowired
    private CCAuftragService auftragService;
    @Autowired
    private MwfEntityDao mwfEntityDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private CarrierElTALService carrierElTALService;
    @Autowired
    private MailService mailService;
    @Autowired
    private HistoryService historyService;

    private String mailRecipies;

    @Override
    public void archiveDocument(Anlage anlage) throws FindException {
        Preconditions.checkNotNull(anlage);

        // Hier muss die Anlage nachgeladen werden, da die uebergebene Anlage detached ist, aber eine
        // andere Anlage (wegen der Suche im createMetaData) an der Session attached ist.
        Anlage foundAnlage = mwfEntityDao.findById(anlage.getId(), Anlage.class);

        byte[] document = anlage.getInhalt();
        AntivirusScanStatus scanStatus = antivirusScanService.scanFileStream(document);

        if (AntivirusScanStatus.FILE_OK.equals(scanStatus)) {
            ByteArrayInputStream input = new ByteArrayInputStream(document);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            MimeType convertedType = documentConvertorService.convert(input, output, anlage.getDateityp().mimeTyp);
            ArchiveMetaData metaData = createMetaData(anlage, convertedType);
            if (metaData == null) {
                foundAnlage.setArchivingCancelReason(Anlage.AUFTRAG_NOT_ASSOCIATED);
            }
            else {
                String uuid = archiveService.archiveDocument(output.toByteArray(), metaData);

                foundAnlage.setArchivSchluessel(uuid);
                foundAnlage.setInhalt(uuid.getBytes(Charsets.UTF_8));
            }
        }
        else {
            switch (scanStatus) {
                case FILE_CAUSES_ERROR:
                    foundAnlage.setArchivingCancelReason(Anlage.CANCEL_ERROR_HAPPEND);
                    sendErrorMail("Virusscan der Wita-Anlage verursacht einen Fehler! T_MWF_ANLAGE -> Anlage ID " + foundAnlage.getId());
                    break;
                case FILE_INFECTED:
                    foundAnlage.setArchivingCancelReason(Anlage.CANCEL_VIRUS_FOUND);
                    sendErrorMail("Es wurde ein Virus gefunden bei einer Dateianlage gefunden! T_MWF_ANLAGE -> Anlage-ID " + foundAnlage.getId());
                    break;
                case FILE_OK:
                    break;
                default:
                    break;
            }
        }
        mwfEntityDao.store(foundAnlage);
    }

    private void sendErrorMail(String mailText) {
        Mail mail = new Mail();
        mail.setTo(mailRecipies);
        mail.setFrom(mailRecipies);
        mail.setSubject("Hurrican - Es ist ein Fehler bei der Wita-Anlagenarchivierung aufgetreten!");
        mail.setText(mailText);
        mailService.sendMailFromHurricanServer(mail);
    }

    private ArchiveMetaData createMetaData(Anlage anlage, MimeType convertedType) throws FindException {
        Meldung<?> meldung = mwfEntityDao.findMeldung4Anlage(anlage);

        ArchiveMetaData archiveMetaData = new ArchiveMetaData();
        archiveMetaData.setDateiname(anlage.getDateiname());
        archiveMetaData.setUseCaseId("Wita");
        archiveMetaData.setDocumentType(historyService.getArchiveDocumentType(anlage.getAnlagentyp()));
        archiveMetaData.setFileExtension(getFileExtensionForMimeType(convertedType));

        if (meldung == null) {
            throw new RuntimeException("Keine Meldung zur Anlage mit id " + anlage.getId() + " gefunden!");
        }

        archiveMetaData.setDokumentenDatum(Instant.ofEpochMilli(meldung.getVersandZeitstempel().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
        String externeAuftragsnummer = meldung.getExterneAuftragsnummer();
        if (externeAuftragsnummer == null) {
            throw new RuntimeException("Meldung ohne externe Auftragsnummer zur Anlage mit id " + anlage.getId()
                    + " gefunden!");
        }
        Set<Long> auftragIds = findAuftragIdsExterneAuftragsnummer(externeAuftragsnummer);
        if (auftragIds.size() != 1) {
            // Keine oder mehr als eine Auftrag-Id zur Meldung -> Muss vom Sachbearbeiter bereinigt werden.
            // Erstmal nicht archivieren, erst dann wenn der Sachbearbeiter das bereinigt hat.
            return null;
        }

        Long auftragId = Iterables.getOnlyElement(auftragIds);
        addMetaDataForAuftragId(archiveMetaData, auftragId);

        return archiveMetaData;
    }

    private void addMetaDataForAuftragId(ArchiveMetaData archiveMetaData, Long auftragId) throws FindException {
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(auftragId);
        Long auftragNoOrig = auftragDaten.getAuftragNoOrig();
        BAuftrag bAuftrag = billingAuftragService.findAuftrag(auftragNoOrig);

        if (bAuftrag == null) {
            throw new FindException(
                    String.format("Kein Billing-Auftrag mit auftragNoOrig=%d vorhanden.", auftragNoOrig));
        }
        archiveMetaData.setKundennr(bAuftrag.getKundeNo());
        archiveMetaData.setSapAuftragsnummer(bAuftrag.getSapId());
        archiveMetaData.setTaifunAuftragsnr(bAuftrag.getAuftragNoOrig());
        archiveMetaData.setDebitornr(billingAuftragService
                .findDebitorNrForAuftragNo(bAuftrag.getAuftragNo()));
    }

    private Set<Long> findAuftragIdsExterneAuftragsnummer(String externeAuftragsnummer) throws FindException {
        Set<Long> auftragIds = Sets.newHashSet();
        auftragIds.addAll(findAuftragIdsFromCbVorgang(externeAuftragsnummer));

        auftragIds.addAll(findAuftragIdsFromUserTasks(externeAuftragsnummer));

        return auftragIds;
    }

    private Collection<Long> findAuftragIdsFromUserTasks(String externeAuftragsnummer) {
        Set<Long> auftragIds = Sets.newHashSet();
        List<AbgebendeLeitungenUserTask> abgebendeLeitungen = taskDao
                .findAbgebendeLeitungenUserTask(externeAuftragsnummer);
        for (AbgebendeLeitungenUserTask abgebendeLeitungenUserTask : abgebendeLeitungen) {
            auftragIds.addAll(abgebendeLeitungenUserTask.getAuftragIds());
        }
        return auftragIds;
    }

    private Collection<Long> findAuftragIdsFromCbVorgang(String externeAuftragsnummer)
            throws FindException {
        Set<Long> auftragIds = Sets.newHashSet();
        WitaCBVorgang witaCbVorgang = WitaCBVorgang.createCompletelyEmptyInstance();
        witaCbVorgang.setCarrierRefNr(externeAuftragsnummer);
        List<WitaCBVorgang> cbVorgaenge = carrierElTALService.findCBVorgaengeByExample(witaCbVorgang);
        for (WitaCBVorgang cbv : cbVorgaenge) {
            auftragIds.add(cbv.getAuftragId());
        }
        return auftragIds;
    }

    private ArchiveFileExtension getFileExtensionForMimeType(MimeType convertedType) {
        if (convertedType == MimeType.PDF) {
            return ArchiveFileExtension.PDF;
        }
        else if (convertedType == MimeType.TIFF) {
            return ArchiveFileExtension.TIF;
        }
        throw new RuntimeException("Nicht erlaubter Typ fuer Archiv: " + convertedType);
    }

    @Value("${mail.warnings.recipients}")
    public void setMailRecipies(String mailRecipies) {
        this.mailRecipies = mailRecipies;
    }
}


