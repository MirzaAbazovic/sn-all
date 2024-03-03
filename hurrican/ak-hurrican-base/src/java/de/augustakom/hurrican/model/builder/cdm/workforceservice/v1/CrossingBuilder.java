/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.02.2015 08:49
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class CrossingBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.Site.WiringData.Crossing> {

    private String physicType;
    private String hweqn;
    private String distributor;
    private String panelPin1;
    private String panelPin2;

    @Override
    public OrderTechnicalParams.Site.WiringData.Crossing build() {
        final OrderTechnicalParams.Site.WiringData.Crossing kreuzung = new OrderTechnicalParams.Site.WiringData.Crossing();
        kreuzung.setDistributor(this.distributor);
        kreuzung.setHWEQN(this.hweqn);
        kreuzung.setPanelPin1(this.panelPin1);
        kreuzung.setPanelPin2(this.panelPin2);
        kreuzung.setPhysicType(this.physicType);
        return kreuzung;
    }

    public CrossingBuilder withPhysikType(final String physicType) {
        this.physicType = physicType;
        return this;
    }

    public CrossingBuilder withHwEqn(final String hweqn) {
        this.hweqn = hweqn;
        return this;
    }

    public CrossingBuilder withDistributor(final String distributor) {
        this.distributor = distributor;
        return this;
    }

    public CrossingBuilder withPanelPin1(final String panelPin1) {
        if (StringUtils.isNotBlank(panelPin1)) {
            this.panelPin1 = panelPin1;
        }
        return this;
    }

    public CrossingBuilder withPanelPin2(final String panelPin2) {
        if (StringUtils.isNotBlank(panelPin2)) {
            this.panelPin2 = panelPin2;
        }
        return this;
    }

}
