/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2011 09:41:41
 */
package de.mnet.wita.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.GeschaeftsfallTyp;

/**
 * Modell-Klasse zur Konfiguration der maximal erlaubten Wita-Vorgaenge fuer einen Geschaeftsfall. <br> Ist fuer einen
 * GeschaeftsfallTyp kein Limit konfiguriert bedeutet dies, dass der Geschaeftsfall ohne Einschraenkungen ausgefuehrt
 * werden darf!
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_WITA_SEND_LIMIT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WITA_SEND_LIMIT_0", allocationSize = 1)
public class WitaSendLimit extends AbstractWitaModel {

    private static final long serialVersionUID = -252769412527118243L;

    public static final Long INFINITE_LIMIT = -1L;

    public static final String GESCHAEFTSFALLTYP = "geschaeftsfallTyp";
    private String geschaeftsfallTyp;
    private Long sendLimit;
    public static final String KOLLOKATIONSTYP = "kollokationsTyp";
    private KollokationsTyp kollokationsTyp;
    private Boolean allowed;
    public static final String ITU_CARRIER_CODE = "ituCarrierCode";
    private String ituCarrierCode;
    public static final String MONTAGE_HINWEIS = "montageHinweis";
    private String montageHinweise;

    @Transient
    public boolean isLimitInfinite() {
        return INFINITE_LIMIT.equals(getWitaSendLimit());
    }

    @NotNull
    @Column(name = "GESCHAEFTSFALL_TYP")
    public String getGeschaeftsfallTyp() {
        return geschaeftsfallTyp;
    }

    public void setGeschaeftsfallTyp(String geschaeftsfallTyp) {
        this.geschaeftsfallTyp = geschaeftsfallTyp;
    }

    /**
     * Definiert eine maximale Anzahl von Requests, die fuer die Kombination {@link GeschaeftsfallTyp} und {@link
     * KollokationsTyp} gesendet werden duerfen. <br> Werte < 0 bedeuten, dass keine Limitierung vorhanden ist; >= 0
     * wird auf die entsprechende Anzahl (mit Hilfe von {@link WitaSendCount}) limitiert.
     */
    @Column(name = "SEND_LIMIT")
    public Long getWitaSendLimit() {
        return sendLimit;
    }

    public void setWitaSendLimit(Long sendLimit) {
        this.sendLimit = sendLimit;
    }

    /**
     * Gibt an, ob die Kombination aus {@link GeschaeftsfallTyp} und {@link KollokationsTyp} ueberhaupt ausgefuehrt
     * werden darf.
     */
    @NotNull
    @Column(name = "ALLOWED")
    public Boolean getAllowed() {
        return allowed;
    }

    public void setAllowed(Boolean allowed) {
        this.allowed = allowed;
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

    @Column(name = "MONTAGE_HINWEIS")
    public String getMontageHinweis() {
        return montageHinweise;
    }

    public void setMontageHinweis(String montageHinweis) {
        this.montageHinweise = montageHinweis;
    }
}
