/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.14
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 * Konfiguration SMS Versand fuer WITA.
 */
@Entity
@Table(name = "T_SMS_CONFIG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_SMS_CONFIG_0", allocationSize = 1)
@ObjectsAreNonnullByDefault
public class SmsConfig extends AbstractCCIDModel {
    private static final long serialVersionUID = 452250926511610870L;

    private String schnittstelle;
    private String geschaeftsfallTyp;
    private String meldungTyp;
    private SmsMontage montage;
    private String aenderungsKennzeichen;
    private String templateText;

    private String emailText;
    private String emailBetreff;
    private String meldungsCode;

    /**
     * Fuer welche Schnittstelle gilt die Konfiguration. Aktuell nur "WITA" (spaeter auch WBCI).
     *
     * @return
     */
    @NotNull
    @Column(name = "SCHNITTSTELLE", nullable = false)
    public String getSchnittstelle() {
        return schnittstelle;
    }

    public void setSchnittstelle(String schnittstelle) {
        this.schnittstelle = schnittstelle;
    }

    /**
     * siehe wita.message.GeschaeftsfallTyp.
     *
     * @return
     */
    @NotNull
    @Column(name = "GESCHAEFTSFALL_TYP", nullable = false)
    public String getGeschaeftsfallTyp() {
        return geschaeftsfallTyp;
    }

    public void setGeschaeftsfallTyp(String geschaeftsfallTyp) {
        this.geschaeftsfallTyp = geschaeftsfallTyp;
    }

    /**
     * siehe wita.message.MeldungsType.
     *
     * @return
     */
    @NotNull
    @Column(name = "MELDUNG_TYP", nullable = false)
    public String getMeldungTyp() {
        return meldungTyp;
    }

    public void setMeldungTyp(String meldungTyp) {
        this.meldungTyp = meldungTyp;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "MONTAGE", nullable = false)
    public SmsMontage getMontage() {
        return montage;
    }

    public void setMontage(SmsMontage montage) {
        this.montage = montage;
    }

    /**
     * siehe wita.message.meldung.position.AenderungsKennzeichen.
     *
     * @return
     */
    @NotNull
    @Column(name = "AENDERUNGSKENNZEICHEN", nullable = false)
    public String getAenderungsKennzeichen() {
        return aenderungsKennzeichen;
    }

    public void setAenderungsKennzeichen(String aenderungsKennzeichen) {
        this.aenderungsKennzeichen = aenderungsKennzeichen;
    }

    @NotNull
    @Column(name = "TEMPLATE_TEXT", nullable = false)
    public String getTemplateText() {
        return templateText;
    }

    public void setTemplateText(String templateText) {
        this.templateText = templateText;
    }

    @NotNull
    @Column(name = "EMAIL_TEXT", nullable = false)
    public String getEmailText() {
        return emailText;
    }

    public void setEmailText(String emailText) {
        this.emailText = emailText;
    }

    @NotNull
    @Column(name = "EMAIL_BETREFF", nullable = false)
    public String getEmailBetreff() {
        return emailBetreff;
    }

    public void setEmailBetreff(String emailBetreff) {
        this.emailBetreff = emailBetreff;
    }

    @NotNull
    @Column(name = "MELDUNGS_CODE", nullable = false)
    public String getMeldungsCode() {
        return meldungsCode;
    }

    public void setMeldungsCode(String meldungsCode) {
        this.meldungsCode = meldungsCode;
    }
}
