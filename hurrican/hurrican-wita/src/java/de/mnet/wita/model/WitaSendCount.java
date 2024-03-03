/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2011 09:41:56
 */
package de.mnet.wita.model;

import java.time.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

/**
 * Modell dient als eine Art Counter, der jeden gesendeten WITA Vorgang zu einem Geschaeftsfall protokolliert. <br> <br>
 * Das Modell besitzt keine einzelne Counter-Spalte, um moegliche DB-Locks durch Multi-Threading zu verhindern. Die
 * Gesamtanzahl an Vorgaengen pro GeschaeftsfallTyp muss ueber entsprechende SQL Statements ermittelt werden.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_WITA_SEND_COUNT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WITA_SEND_COUNT_0", allocationSize = 1)
public class WitaSendCount extends AbstractWitaModel {

    private static final long serialVersionUID = -352769412527118243L;

    public static final String GESCHAEFTSFALLTYP = "geschaeftsfallTyp";
    private String geschaeftsfallTyp;
    private LocalDate sentAt;
    public static final String KOLLOKATIONSTYP = "kollokationsTyp";
    private KollokationsTyp kollokationsTyp;
    public static final String ITU_CARRIER_CODE = "ituCarrierCode";
    private String ituCarrierCode;

    public WitaSendCount() {
        // required by Hibernate
    }

    public WitaSendCount(String geschaeftsfallTyp, KollokationsTyp kollokationsTyp, LocalDate sentAt) {
        this.geschaeftsfallTyp = geschaeftsfallTyp;
        this.kollokationsTyp = kollokationsTyp;
        this.sentAt = sentAt;
    }

    public WitaSendCount(String geschaeftsfallTyp, String ituCarrierCode, LocalDate sentAt) {
        this.geschaeftsfallTyp = geschaeftsfallTyp;
        this.ituCarrierCode = ituCarrierCode;
        this.sentAt = sentAt;
    }

    @NotNull
    @Column(name = "SENT_AT")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    public LocalDate getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDate sentAt) {
        this.sentAt = sentAt;
    }

    @NotNull
    @Column(name = "GESCHAEFTSFALL_TYP")
    public String getGeschaeftsfallTyp() {
        return geschaeftsfallTyp;
    }

    public void setGeschaeftsfallTyp(String geschaeftsfallTyp) {
        this.geschaeftsfallTyp = geschaeftsfallTyp;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "KOLLOKATIONS_TYP")
    public KollokationsTyp getKollokationsTyp() {
        return kollokationsTyp;
    }

    public void setKollokationsTyp(KollokationsTyp kollokationsTyp) {
        this.kollokationsTyp = kollokationsTyp;
    }

    @Column(name = "ITU_CARRIER_CODE")
    public String getItuCarrierCode() {
        return ituCarrierCode;
    }

    public void setItuCarrierCode(String ituCarrierCode) {
        this.ituCarrierCode = ituCarrierCode;
    }

}
