/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.2015
 */
package de.augustakom.hurrican.model.cc.ffm;

import javax.persistence.*;
import javax.validation.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 *
 */
@Entity(name = "de.augustakom.hurrican.model.cc.ffm.FfmQualificationMapping")
@Table(name = "T_FFM_QUALIFICATION_MAPPING")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_FFM_PRODUCT_MAPPING_0", allocationSize = 1)
public class FfmQualificationMapping extends AbstractCCIDModel {

    private Long productId;
    private Long techLeistungId;
    private Long standortRefId;
    private Boolean vpn;

    private FfmQualification ffmQualification;

    @Column(name = "PRODUCT_ID", nullable = true)
    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Column(name = "TECH_LEISTUNG_ID", nullable = true)
    public Long getTechLeistungId() {
        return this.techLeistungId;
    }

    public void setTechLeistungId(Long techLeistungId) {
        this.techLeistungId = techLeistungId;
    }

    @Column(name = "STANDORT_REF_ID", nullable = true)
    public Long getStandortRefId() {
        return this.standortRefId;
    }

    public void setStandortRefId(Long standortRefId) {
        this.standortRefId = standortRefId;
    }

    @Column(name = "VPN", nullable = true)
    public Boolean getVpn() {
        return vpn;
    }

    public void setVpn(Boolean vpn) {
        this.vpn = vpn;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUALIFICATION_ID")
    @Valid
    public FfmQualification getFfmQualification() {
        return ffmQualification;
    }

    public void setFfmQualification(FfmQualification ffmQualification) {
        this.ffmQualification = ffmQualification;
    }

    public String toString() {
        return "FfmQualificationMapping{" +
                "id=" + getId() +
                ", qualification=" + getFfmQualification().getId() +
                " " + getFfmQualification().getQualification() +
                ", productId=" + getProductId() +
                ", techLeistungId=" + getTechLeistungId() +
                ", standortRefId=" + getStandortRefId() +
                ", vpn=" + getVpn() +
                ", toString='" + super.toString() + '\'' +
                "}";
    }
}
