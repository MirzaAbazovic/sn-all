/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.augustakom.hurrican.model.cc.ffm;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.enums.FfmTyp;

/**
 * Modell-Klasse zur Abbildung eines Mappings von Hurrican-Daten (Produkt, Bauauftrags-Anlass, Standort-Typ)
 * auf notwendige FFM-Parameter (Vorgangs-Name, Duration, Skills/Qualifications).
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity(name = "de.augustakom.hurrican.model.cc.ffm.FfmProductMapping")
@Table(name = "T_FFM_PRODUCT_MAPPING")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_FFM_PRODUCT_MAPPING_0", allocationSize = 1)
public class FfmProductMapping extends AbstractCCIDModel {

    private static final long serialVersionUID = -1606038532449929105L;

    public static final String FFM_ACTIVITY_TYPE_NEU_IK = "RTL_NEU_IK";
    public static final String FFM_ACTIVITY_TYPE_INT = "RTL_Int";
    public static final String FFM_ACTIVITY_TYPE_NEU_MK_HVT = "RTL_Neu_MK_HVT";

    private Long produktId;
    private FfmTyp baFfmTyp;
    private Long standortTypRefId;
    private String ffmActivityType;
    private Integer ffmPlannedDuration;
    private FfmAggregationStrategy aggregationStrategy;

    /**
     * Die Reihenfolge der Pruefungen hier sollte nach der Gewichtung der Prioritaet der einzelnen
     * Properties erfolgen.
     */
    public boolean isEqualToOrPartOf(FfmProductMapping o) {
        if ((o.standortTypRefId != null) && !o.standortTypRefId.equals(this.standortTypRefId)) {
            return false;
        }
        if ((o.produktId != null) && !o.produktId.equals(this.produktId)) {
            return false;
        }
        return true;
    }

    // @formatter:off
    /**
     * Subset examples:
     * <pre>
     *                  ProdId    StandortTyp
     *         this     540       10000
     *         other    NULL      11000        -->  true
     *         other    NULL      NULL         -->  true
     *         other    540       11000        -->  false
     *         other    541       11000        -->  false
     *         other    540       11013        -->  false
     * </pre>
     */
    // @formatter:on
    public boolean isSubsetOf(FfmProductMapping other) {
        if ((produktId == null && other.produktId != null)
                || (standortTypRefId == null && other.standortTypRefId != null)) {
            return false;
        }
        if (((produktId != null) == (other.produktId != null))
                && ((standortTypRefId != null) == (other.standortTypRefId != null))) {
            return false;
        }
        return true;
    }


    @Column(name = "PROD_ID")
    public Long getProduktId() {
        return produktId;
    }
    public void setProduktId(Long produktId) {
        this.produktId = produktId;
    }


    @Column(name = "BA_FFM_TYP")
    @Enumerated(EnumType.STRING)
    @NotNull
    public FfmTyp getBaFfmTyp() {
        return baFfmTyp;
    }

    public void setBaFfmTyp(FfmTyp baFfmTyp) {
        this.baFfmTyp = baFfmTyp;
    }


    @Column(name = "STANDORT_TYP")
    public Long getStandortTypRefId() {
        return standortTypRefId;
    }
    public void setStandortTypRefId(Long standortTypRefId) {
        this.standortTypRefId = standortTypRefId;
    }

    @Column(name = "FFM_ACTIVITY_TYPE")
    @NotNull
    public String getFfmActivityType() {
        return ffmActivityType;
    }
    public void setFfmActivityType(String ffmActivityType) {
        this.ffmActivityType = ffmActivityType;
    }

    @Column(name = "FFM_PLANNED_DURATION")
    @NotNull
    public Integer getFfmPlannedDuration() {
        return ffmPlannedDuration;
    }
    public void setFfmPlannedDuration(Integer ffmPlannedDuration) {
        this.ffmPlannedDuration = ffmPlannedDuration;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "AGGREGATION_STRATEGY")
    @NotNull
    public FfmAggregationStrategy getAggregationStrategy() {
        return aggregationStrategy;
    }
    public void setAggregationStrategy(FfmAggregationStrategy aggregationStrategy) {
        this.aggregationStrategy = aggregationStrategy;
    }

    public String toString() {
        return "FfmProductMapping{" +
                "id=" + getId() +
                ", baFfmTyp=" + getBaFfmTyp() +
                ", prodId=" + getProduktId() +
                ", standorTypRefId=" + getStandortTypRefId() +
                ", ffmActivityType=" + getFfmActivityType() +
                ", ffmPlannedDuration=" + getFfmPlannedDuration() +
                ", ffmAggregationStrategy=" + getAggregationStrategy() +
                ", toString='" + super.toString() + '\'' +
                "}";
    }
}
