/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2011 12:45:14
 */
package de.mnet.wita.message.common;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.ArrayUtils;

import de.mnet.wita.message.MwfEntity;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_ANLAGE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_ANLAGE_0", allocationSize = 1)
public class Anlage extends MwfEntity {

    private static final long serialVersionUID = -8942143293613548105L;

    public static final String AUFTRAG_NOT_ASSOCIATED = " Archivierung nicht moeglich, weil nicht genau ein Eintrag gefunden wurde; bitte korrigieren!";
    public static final String CANCEL_VIRUS_FOUND = "Virus found";
    public static final String CANCEL_ERROR_HAPPEND = "Error happend";

    private String dateiname;
    private Dateityp dateityp;
    private String beschreibung;
    private byte[] inhalt;
    private Anlagentyp anlagentyp;
    public static final String ARCHIVE_SCHLUESSEL = "archivSchluessel";
    private String archivSchluessel;
    public static final String ARCHIVING_CANCEL_REASON = "archivingCancelReason";
    private String archivingCancelReason;

    public Anlage() {
        // required by Hibernate
    }

    public Anlage(String dateiname, Dateityp dateityp, String beschreibung, byte[] inhalt, Anlagentyp anlagentyp) {
        this.dateiname = dateiname;
        this.dateityp = dateityp;
        this.beschreibung = beschreibung;
        this.inhalt = ArrayUtils.clone(inhalt);
        this.anlagentyp = anlagentyp;
    }

    @Size(min = 1, max = 255, message = "Dateiname muss zwischen 1 und 255 Zeichen lang sein")
    @NotNull(message = "Dateiname muss gesetzt sein")
    @Column(name = "DATEINAME")
    public String getDateiname() {
        return dateiname;
    }

    public void setDateiname(String dateiname) {
        this.dateiname = dateiname;
    }

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Dateityp muss gesetzt sein")
    public Dateityp getDateityp() {
        return dateityp;
    }

    public void setDateityp(Dateityp dateityp) {
        this.dateityp = dateityp;
    }

    @Size(min = 1, max = 160, message = "Beschreibung muss zwischen 1 und 160 Zeichen lang sein")
    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    @NotNull(message = "Datei-Inhalt muss gesetzt sein")
    @Column(name = "INHALT")
    @Lob
    public byte[] getInhalt() {
        return ArrayUtils.clone(inhalt);
    }

    public void setInhalt(byte[] inhalt) {
        this.inhalt = ArrayUtils.clone(inhalt);
    }

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Anlagentyp muss gesetzt sein")
    public Anlagentyp getAnlagentyp() {
        return anlagentyp;
    }

    public void setAnlagentyp(Anlagentyp anlagentyp) {
        this.anlagentyp = anlagentyp;
    }

    @Column(name = "ARCHIVE_UUID")
    public String getArchivSchluessel() {
        return archivSchluessel;
    }

    public void setArchivSchluessel(String archivSchluessel) {
        this.archivSchluessel = archivSchluessel;
    }

    @Column(name = "ARCHIVING_CANCEL_REASON")
    public String getArchivingCancelReason() {
        return archivingCancelReason;
    }

    public void setArchivingCancelReason(String archivingCancelReason) {
        this.archivingCancelReason = archivingCancelReason;
    }

    @Override
    public String toString() {
        // Es soll nur ersichtlich sein, ob inhalt null oder nicht-null ist. Der exakte Inhalt interessiert
        // uns in toString nicht. Womöglich wäre das ganze Array fürs Debuggen auch zu lang.
        //noinspection ImplicitArrayToString
        final String inhaltAsString = "" + inhalt;

        return "Anlage [dateiname=" + dateiname
                + ", dateityp=" + dateityp
                + ", beschreibung=" + beschreibung
                + ", anlagentyp=" + anlagentyp
                + ", inhalt=" +  inhaltAsString
                + ", id=" + getId()
                + "]";
    }
}
