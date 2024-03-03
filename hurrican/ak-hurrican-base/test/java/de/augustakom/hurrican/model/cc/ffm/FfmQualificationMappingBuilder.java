/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.2015
 */
package de.augustakom.hurrican.model.cc.ffm;

import de.augustakom.hurrican.model.cc.AbstractCCIDModelBuilder;

/**
 *
 */
public class FfmQualificationMappingBuilder extends AbstractCCIDModelBuilder<FfmQualificationMappingBuilder, FfmQualificationMapping> {

    private Long productId;
    private Long techLeistungId;
    private Long standortRefId;
    private Boolean vpn;

    private FfmQualification ffmQualification;

    public FfmQualificationMappingBuilder withFfmQualification(FfmQualification ffmQualification) {
        this.ffmQualification = ffmQualification;
        return this;
    }

    public FfmQualificationMappingBuilder withProductId(Long productId) {
        this.productId = productId;
        return this;
    }

    public FfmQualificationMappingBuilder withTechLeistungId(Long techLeistungId) {
        this.techLeistungId = techLeistungId;
        return this;
    }

    public FfmQualificationMappingBuilder withStandortRefId(Long standortRefId) {
        this.standortRefId = standortRefId;
        return this;
    }

    public FfmQualificationMappingBuilder withVpn(Boolean vpn) {
        this.vpn = vpn;
        return this;
    }
}
