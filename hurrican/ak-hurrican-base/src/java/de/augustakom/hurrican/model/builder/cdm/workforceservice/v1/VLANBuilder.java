/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.math.*;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class VLANBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.VLAN> {

    private String type;
    private BigInteger cvlan;
    private BigInteger svlan;
    private BigInteger svlanBackbone;
    private String service;

    @Override
    public OrderTechnicalParams.VLAN build() {
        OrderTechnicalParams.VLAN vlan = new OrderTechnicalParams.VLAN();
        vlan.setType(this.type);
        vlan.setCVLAN(this.cvlan);
        vlan.setSVLAN(this.svlan);
        vlan.setSVLANBackbone(this.svlanBackbone);
        vlan.setService(this.service);
        return vlan;
    }

    public VLANBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public VLANBuilder withCVLAN(BigInteger cvlan) {
        this.cvlan = cvlan;
        return this;
    }

    public VLANBuilder withSVLAN(BigInteger svlan) {
        this.svlan = svlan;
        return this;
    }

    public VLANBuilder withSVLANBackbone(BigInteger svlanBackbone) {
        this.svlanBackbone = svlanBackbone;
        return this;
    }

    public VLANBuilder withService(String service) {
        this.service = service;
        return this;
    }
}