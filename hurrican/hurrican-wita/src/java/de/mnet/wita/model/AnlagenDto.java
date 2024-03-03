/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.12.2011 16:49:01
 */
package de.mnet.wita.model;

import java.io.*;
import java.time.*;

import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.mnet.wita.IOArchiveProperties.IOType;
import de.mnet.wita.message.common.Anlage;

/**
 * Dto für das anzeigen der Anlagen
 */
public class AnlagenDto implements Serializable {

    private static final long serialVersionUID = 1150381370383615726L;

    private String geschaeftsfallOderMeldungsTyp;
    private LocalDateTime sentTimestamp;
    private String witaExtOrderNo;

    // Archive Document set for requests
    private ArchiveDocumentDto archiveDocumentDto;

    // Anlage set for Meldung
    private Anlage anlage;

    public AnlagenDto(String geschaeftsfallOderMeldungsTyp, LocalDateTime sentTimestamp,
            String witaExtOrderNo, ArchiveDocumentDto archiveDocumentDto) {
        this(geschaeftsfallOderMeldungsTyp, sentTimestamp, witaExtOrderNo);
        this.archiveDocumentDto = archiveDocumentDto;
    }

    public AnlagenDto(String geschaeftsfallOderMeldungsTyp, LocalDateTime sentTimestamp,
            String witaExtOrderNo, Anlage anlage) {
        this(geschaeftsfallOderMeldungsTyp, sentTimestamp, witaExtOrderNo);
        this.anlage = anlage;
    }

    private AnlagenDto(String geschaeftsfallOderMeldungsTyp, LocalDateTime sentTimestamp,
            String witaExtOrderNo) {
        this.geschaeftsfallOderMeldungsTyp = geschaeftsfallOderMeldungsTyp;
        this.sentTimestamp = sentTimestamp;
        this.witaExtOrderNo = witaExtOrderNo;
    }

    public String getGeschaeftsfallOderMeldungsTyp() {
        return geschaeftsfallOderMeldungsTyp;
    }

    public void setGeschaeftsfallOderMeldungsTyp(String geschaeftsfallOderMeldungsTyp) {
        this.geschaeftsfallOderMeldungsTyp = geschaeftsfallOderMeldungsTyp;
    }

    public IOType getIoType() {
        return getArchiveDocumentDto() != null ? IOType.OUT : IOType.IN;
    }

    public LocalDateTime getSentTimestamp() {
        return sentTimestamp;
    }

    public void setSentTimestamp(LocalDateTime sentTimestamp) {
        this.sentTimestamp = sentTimestamp;
    }

    public String getWitaExtOrderNo() {
        return witaExtOrderNo;
    }

    public void setWitaExtOrderNo(String witaExtOrderNo) {
        this.witaExtOrderNo = witaExtOrderNo;
    }

    public String getArchiveDocumentType() {
        if (getArchiveDocumentDto() != null) {
            return getArchiveDocumentDto().getDocumentType().getDocumentTypeName();

        }
        if (anlage != null) {
            return anlage.getAnlagentyp().value;
        }
        return null;
    }

    public String getDateiName() {
        if (getArchiveDocumentDto() != null) {
            return getArchiveDocumentDto().getLabel();
        }
        if (getAnlage() != null) {
            return getAnlage().getDateiname();
        }
        return "";
    }

    public ArchiveDocumentDto getArchiveDocumentDto() {
        return archiveDocumentDto;
    }

    public Anlage getAnlage() {
        return anlage;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((getArchiveDocumentDto() == null) ? 0 : getArchiveDocumentDto().hashCode());
        result = (prime * result)
                + ((geschaeftsfallOderMeldungsTyp == null) ? 0 : geschaeftsfallOderMeldungsTyp.hashCode());
        result = (prime * result) + ((sentTimestamp == null) ? 0 : sentTimestamp.hashCode());
        result = (prime * result) + ((witaExtOrderNo == null) ? 0 : witaExtOrderNo.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "AnlagenDto [geschaeftsfallOderMeldungsTyp=" + geschaeftsfallOderMeldungsTyp
                + ", sentTimestamp=" + sentTimestamp + ", witaExtOrderNo=" + witaExtOrderNo + ", archiveDocument="
                + getArchiveDocumentDto() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AnlagenDto other = (AnlagenDto) obj;
        if (getArchiveDocumentDto() != other.getArchiveDocumentDto()) {
            return false;
        }
        if (geschaeftsfallOderMeldungsTyp == null) {
            if (other.geschaeftsfallOderMeldungsTyp != null) {
                return false;
            }
        }
        else if (!geschaeftsfallOderMeldungsTyp.equals(other.geschaeftsfallOderMeldungsTyp)) {
            return false;
        }
        if (sentTimestamp == null) {
            if (other.sentTimestamp != null) {
                return false;
            }
        }
        else if (!sentTimestamp.equals(other.sentTimestamp)) {
            return false;
        }
        if (witaExtOrderNo == null) {
            if (other.witaExtOrderNo != null) {
                return false;
            }
        }
        else if (!witaExtOrderNo.equals(other.witaExtOrderNo)) {
            return false;
        }
        return true;
    }


}
