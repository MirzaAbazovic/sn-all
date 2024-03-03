/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.time.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AufnehmenderProviderABMType;

/**
 *
 */
public class AufnehmenderProviderABMTypeBuilder extends AufnehmenderProviderTypeBuilder {

    private LocalDate uebernahmeDatumVerbindlich;

    @Override
    public AufnehmenderProviderABMType build() {
        AufnehmenderProviderABMType aufnehmenderProvider = new AufnehmenderProviderABMType();
        aufnehmenderProvider.setUebernahmeDatumVerbindlich(DateConverterUtils.toXmlGregorianCalendar(uebernahmeDatumVerbindlich));
        return enrich(aufnehmenderProvider);
    }

    public AufnehmenderProviderABMTypeBuilder withUebernahmeDatumVerbindlich(LocalDate uebernahmeDatumVerbindlich) {
        this.uebernahmeDatumVerbindlich = uebernahmeDatumVerbindlich;
        return this;
    }

}
