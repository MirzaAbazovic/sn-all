/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.12.2010 12:14:58
 */

package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;


/**
 * Modell-Klasse fuer 1 zu n Relationen zwischen techn. Standort und Technologietypen.
 *
 *
 */
@Entity
@Table(name = "T_HVT_STANDORT_TECH_TYPE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HVT_STANDORT_TECH_TYPE_0", allocationSize = 1)
public class HVTStandortTechType extends AbstractCCIDModel implements HvtIdStandortModel {

    public enum TechnologyType {
        ADSL(22250L),
        ADSL_POTS(22251L),
        ADSL_ISDN(22252L),
        ADSL2PLUS(22253L),
        ADSL2PLUS_POTS(22254L),
        ADSL2PLUS_ISDN(22255L),
        FTTX_POTS(22256L),
        ISDN(22257L),
        POTS(22258L),
        RF(22259L),
        SDSL(22260L),
        SHDSL(22261L),
        VDSL2(22262L);

        private final Long refId;

        private TechnologyType(Long refId) {
            this.refId = refId;
        }

        public Long getRefId() {
            return refId;
        }

        public static final TechnologyType findById(Long refId) {
            if (refId == null) { return null;}

            TechnologyType[] types = TechnologyType.values();
            for (TechnologyType technologyType : types) {
                if (NumberTools.equal(technologyType.getRefId(), refId)) {
                    return technologyType;
                }
            }
            return null;
        }
    }

    private Long hvtIdStandort = null;
    public static final String TECH_TYPE_REFERENCE = "technologyTypeReference";
    public static final String TECH_TYPE_NAME = "technologyTypeName";
    private Reference technologyTypeReference = null;
    public static final String AVAILABLE_FROM = "availableFrom";
    private Date availableFrom = null;
    public static final String AVAILABLE_TO = "availableTo";
    private Date availableTo = null;
    private String userW = null;
    private Date dateW = null;

    @Override
    @Column(name = "HVT_ID_STANDORT")
    @NotNull
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TECH_TYPE_REF_ID", nullable = false)
    public Reference getTechnologyTypeReference() {
        return technologyTypeReference;
    }

    public void setTechnologyTypeReference(Reference technologyTypeReference) {
        this.technologyTypeReference = technologyTypeReference;
    }

    @Transient
    public String getTechnologyTypeName() {
        if (technologyTypeReference != null) {
            return technologyTypeReference.getStrValue();
        }
        return null;
    }

    @Column(name = "AVAILABLE_FROM")
    public Date getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(Date availableFrom) {
        this.availableFrom = availableFrom;
    }

    @Column(name = "AVAILABLE_TO")
    public Date getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(Date availableTo) {
        this.availableTo = availableTo;
    }

    @Column(name = "USERW")
    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }

    @Column(name = "DATEW")
    public Date getDateW() {
        return dateW;
    }

    public void setDateW(Date dateW) {
        this.dateW = dateW;
    }

}
