/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2015
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import de.augustakom.common.tools.lang.NumberTools;

/**
 * Eine KVZSperre sperrt den KVZ Bereich (definiert über ONKZ/ASB/KVZ-Nummer) für TAL Neubestellungen und
 * Portzuordnungen. Die Sperre gilt sowohl für KVZ, als auch für HVT Standorte, die KVZ-Nummer wird über die GeoID der
 * Enstelle ermittelt.
 *
 *
 */
@Entity
@Table(name = "T_KVZ_SPERRE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_KVZ_SPERRE_0", allocationSize = 1)
public class KvzSperre extends AbstractCCIDModel {

    private String onkz;

    private Integer asb;

    private String kvzNummer;

    private String bemerkung;

    @Column(name = "ONKZ", length = 8, nullable = false)
    @Length(max = 8)
    @NotNull
    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    @Column(name = "ASB", nullable = false)
    @NotNull
    public Integer getAsb() {
        return asb;
    }

    public void setAsb(Integer asb) {
        this.asb = asb;
    }

    @Column(name = "KVZ_NUMMER", length = 5, nullable = false)
    @Length(max = 5)
    @NotNull
    public String getKvzNummer() {
        return kvzNummer;
    }

    public void setKvzNummer(String kvzNummer) {
        this.kvzNummer = kvzNummer;
    }

    @Column(name = "BEMERKUNG", length = 100)
    @Length(max = 100)
    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    /**
     * Prüft ob die Sperre für einen Standorttyp gilt (aktuell hardcoded HVT und FTTC_KVZ).
     *
     * @param standortTypRefId standortTypRefId
     * @return wenn die Sperre für den Standorttyp gilt
     */
    @Transient
    public boolean is4StandortTyp(Long standortTypRefId) {
        return NumberTools.isIn(standortTypRefId,
                new Long[] { HVTStandort.HVT_STANDORT_TYP_HVT, HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ });
    }
}