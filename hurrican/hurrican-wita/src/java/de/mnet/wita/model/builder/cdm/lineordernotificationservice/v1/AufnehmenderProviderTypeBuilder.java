/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AufnehmenderProviderType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public abstract class AufnehmenderProviderTypeBuilder implements LineOrderNotificationTypeBuilder<AufnehmenderProviderType> {

    private String providernameAufnehmend;

    protected <AP extends AufnehmenderProviderType> AP enrich(AP aufnehmenderProvider) {
        aufnehmenderProvider.setProvidernameAufnehmend(providernameAufnehmend);
        return aufnehmenderProvider;
    }

    public AufnehmenderProviderTypeBuilder withProvidernameAufnehmend(String providernameAufnehmend) {
        this.providernameAufnehmend = providernameAufnehmend;
        return this;
    }

}
