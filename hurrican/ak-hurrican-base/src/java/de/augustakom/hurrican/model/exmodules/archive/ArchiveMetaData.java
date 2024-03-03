/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.12.2011 12:59:19
 */
package de.augustakom.hurrican.model.exmodules.archive;

import java.io.*;
import java.time.*;


/**
 * Metadaten fuer zu archivierendes Dokument
 */
public class ArchiveMetaData implements Serializable {
    public enum ArchiveFileExtension {
        TIF,
        PDF;
    }

    private String useCaseId;
    private String dateiname;
    private ArchiveFileExtension fileExtension;
    private ArchiveDocumentType documentType;
    private Long taifunAuftragsnr;
    private String sapAuftragsnummer;
    private Long kundennr;
    private String debitornr;
    private LocalDateTime dokumentenDatum;

    public String getUseCaseId() {
        return useCaseId;
    }

    public void setUseCaseId(String useCaseId) {
        this.useCaseId = useCaseId;
    }

    public String getDateiname() {
        return dateiname;
    }

    public void setDateiname(String dateiname) {
        this.dateiname = dateiname;
    }

    public ArchiveFileExtension getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(ArchiveFileExtension fileExtension) {
        this.fileExtension = fileExtension;
    }

    public ArchiveDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(ArchiveDocumentType documentType) {
        this.documentType = documentType;
    }

    public Long getTaifunAuftragsnr() {
        return taifunAuftragsnr;
    }

    public void setTaifunAuftragsnr(Long taifunAuftragsnr) {
        this.taifunAuftragsnr = taifunAuftragsnr;
    }

    public String getSapAuftragsnummer() {
        return sapAuftragsnummer;
    }

    public void setSapAuftragsnummer(String sapAuftragsnummer) {
        this.sapAuftragsnummer = sapAuftragsnummer;
    }

    public Long getKundennr() {
        return kundennr;
    }

    public void setKundennr(Long kundennr) {
        this.kundennr = kundennr;
    }

    public String getDebitornr() {
        return debitornr;
    }

    public void setDebitornr(String debitornr) {
        this.debitornr = debitornr;
    }

    public LocalDateTime getDokumentenDatum() {
        return dokumentenDatum;
    }

    public void setDokumentenDatum(LocalDateTime dokumentenDatum) {
        this.dokumentenDatum = dokumentenDatum;
    }

}
