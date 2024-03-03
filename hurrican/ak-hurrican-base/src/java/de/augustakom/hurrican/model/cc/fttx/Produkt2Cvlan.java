/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2012 09:28:45
 */
package de.augustakom.hurrican.model.cc.fttx;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.ObjectUtils;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.Produkt;

/**
 * Modellklasse zur Konfiguration eines {@link CVlan} zu einem Hurrican {@link Produkt}. Ueber den Parameter {@code
 * isMandatory} kann definiert werden, ob ein {@link CvlanServiceTyp} zu dem Produkt als Default oder optional angelegt
 * werden soll.
 */
@Entity
@Table(name = "T_PROD_2_CVLAN", uniqueConstraints = { @UniqueConstraint(columnNames = { "PROD_ID", "CVLAN_TYP",
        "TECH_LOCATION_TYPE_REF_ID", "HVT_TECHNIK_ID" }) })
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_PROD_2_CVLAN_0", allocationSize = 1)
public class Produkt2Cvlan extends AbstractCCIDModel {

    private Long produktId;
    private CvlanServiceTyp cvlanTyp;
    private Long techLocationTypeRefId;
    private Long hvtTechnikId;
    private Boolean isMandatory;

    @NotNull
    @Column(name = "PROD_ID", nullable = false)
    public Long getProduktId() {
        return produktId;
    }

    public void setProduktId(Long produktId) {
        this.produktId = produktId;
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "CVLAN_TYP", nullable = false)
    public CvlanServiceTyp getCvlanTyp() {
        return cvlanTyp;
    }

    public void setCvlanTyp(CvlanServiceTyp cvlanTyp) {
        this.cvlanTyp = cvlanTyp;
    }

    @Column(name = "TECH_LOCATION_TYPE_REF_ID")
    public Long getTechLocationTypeRefId() {
        return techLocationTypeRefId;
    }

    public void setTechLocationTypeRefId(Long techLocationTypeRefId) {
        this.techLocationTypeRefId = techLocationTypeRefId;
    }

    @Column(name = "HVT_TECHNIK_ID")
    public Long getHvtTechnikId() {
        return hvtTechnikId;
    }

    public void setHvtTechnikId(Long hvtTechnikId) {
        this.hvtTechnikId = hvtTechnikId;
    }

    @NotNull
    @Column(name = "IS_MANDATORY", nullable = false)
    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    /**
     * Prüft ob die aktuelle Konfiguration ein subset von <code>other</code> ist. Dies ist dann der Fall wenn diese
     * "spezieller" ist, sprich weiter einschränkt.<br> <b>ACHTUNG:</b> Die Funktion ist nur definiert wenn
     * <code>produktId</code> und <code>cvlanTyp</code> gleich sind (ansonsten wird eine Exception geworfen).
     *
     * @param other
     * @return true wenn die Konfiguration spezieller ist als "other"
     * @throws IllegalStateException wenn <code>produktId</code> oder <code>cvlanTyp</code> nicht gleich sind, oder die
     *                               beiden Konfigurationen "disjunkt" sind (Konfigurationsfehler)
     */
    @Transient
    public boolean isSubsetOf(Produkt2Cvlan other) {
        if (!cvlanTyp.equals(other.cvlanTyp) || !produktId.equals(other.produktId)) {
            throw new IllegalStateException("subset nur definiert für gleiche produktId und cvlanTyp");
        }
        if ((other.techLocationTypeRefId == null) && (other.hvtTechnikId == null)) {
            return true;
        }
        if (!ObjectUtils.equals(techLocationTypeRefId, other.techLocationTypeRefId)
                && !ObjectUtils.equals(hvtTechnikId, other.hvtTechnikId)) {
            throw new IllegalStateException(
                    "Konfigurationsfehler, 2 Kandiaten für gleichen CvlanServiceTyp, Kandidat1=" + this + " Kandidat2="
                            + other
            );
        }

        if (((techLocationTypeRefId == null) && (other.techLocationTypeRefId != null))
                || ((hvtTechnikId == null) && (other.hvtTechnikId != null))) {
            return false;
        }
        if (((techLocationTypeRefId != null) && (other.techLocationTypeRefId != null) && !techLocationTypeRefId
                .equals(other.techLocationTypeRefId))
                || ((hvtTechnikId != null) && (other.hvtTechnikId != null) && !hvtTechnikId.equals(other.hvtTechnikId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Produkt2Cvlan [produktId=" + produktId + ", cvlanTyp=" + cvlanTyp + ", techLocationTypeRefId="
                + techLocationTypeRefId + ", hvtTechnikId=" + hvtTechnikId + ", isMandatory=" + isMandatory + "]";
    }

}
