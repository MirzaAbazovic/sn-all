/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2011 09:58:35
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType;
import de.augustakom.hurrican.service.exmodules.archive.ArchiveService;

/**
 * Modell um die Anlagen fuer eine WITA Bestellung zu halten. <br> In dem Modell werden nur Informationen ueber die
 * Anlage gehalten, nicht die Anlage selbst. Diese muss aus dem {@link ArchiveService} geladen werden.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_CB_VORGANG_ANLAGE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_CB_VORGANG_ANLAGE_0", allocationSize = 1)
public class WitaCBVorgangAnlage extends AbstractCCIDModel {

    private static final long serialVersionUID = -8942143293613548105L;

    private String archiveKey;
    private String mimeType;
    private String anlagentyp;
    private ArchiveDocumentType archiveDocumentType;
    private String archiveVertragsNr;

    @Size(min = 1, max = 255, message = "ArchiveKey muss zwischen 1 und 255 Zeichen lang sein")
    @NotNull(message = "Der ArchiveKey muss gesetzt sein")
    @Column(name = "ARCHIVEKEY")
    public String getArchiveKey() {
        return archiveKey;
    }

    public void setArchiveKey(String archiveKey) {
        this.archiveKey = archiveKey;
    }

    @Size(min = 1, max = 50, message = "MimeType muss zwischen 1 und 50 Zeichen lang sein")
    @NotNull(message = "Der MimeType muss gesetzt sein")
    @Column(name = "MIMETYPE")
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Size(min = 1, max = 50, message = "Anlagentyp muss zwischen 1 und 50 Zeichen lang sein")
    @NotNull(message = "Der Anlagentyp muss gesetzt sein")
    @Column(name = "ANLAGENTYP")
    public String getAnlagentyp() {
        return anlagentyp;
    }

    public void setAnlagentyp(String anlagentyp) {
        this.anlagentyp = anlagentyp;
    }

    @NotNull(message = "Der Archiv DocumentType muss gesetzt sein")
    @Column(name = "ARCHIVE_DOCUMENT_TYPE")
    @Enumerated(EnumType.STRING)
    public ArchiveDocumentType getArchiveDocumentType() {
        return archiveDocumentType;
    }

    public void setArchiveDocumentType(ArchiveDocumentType archiveDocumentType) {
        this.archiveDocumentType = archiveDocumentType;
    }

    @NotNull(message = "Die VertragsNr fuer die Archiv-Suche muss gesetzt sein")
    @Column(name = "ARCHIVE_VERTRAGS_NR")
    public String getArchiveVertragsNr() {
        return archiveVertragsNr;
    }

    public void setArchiveVertragsNr(String archiveVertragsNr) {
        this.archiveVertragsNr = archiveVertragsNr;
    }

    @Override
    public String toString() {
        return "WitaCBVorgangAnlage [archiveKey=" + archiveKey + ", mimeType=" + mimeType + ", anlagentyp="
                + anlagentyp + ", archiveDocumentType=" + archiveDocumentType + ", archiveVertragsNr="
                + archiveVertragsNr + "]";
    }

}
