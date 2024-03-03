/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.time.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AufnehmenderProviderAKMType;

/**
 *
 */
public class AufnehmenderProviderAKMTypeBuilder extends AufnehmenderProviderTypeBuilder {

    private LocalDate uebernahmeDatumGeplant;
    private LocalDate antwortfrist;

    @Override
    public AufnehmenderProviderAKMType build() {
        AufnehmenderProviderAKMType aufnehmenderProvider = new AufnehmenderProviderAKMType();
        aufnehmenderProvider.setUebernahmeDatumGeplant(DateConverterUtils.toXmlGregorianCalendar(uebernahmeDatumGeplant));
        aufnehmenderProvider.setAntwortfrist(DateConverterUtils.toXmlGregorianCalendar(antwortfrist));
        return enrich(aufnehmenderProvider);
    }

    public AufnehmenderProviderAKMTypeBuilder withUebernahmeDatumGeplant(LocalDate uebernahmeDatumGeplant) {
        this.uebernahmeDatumGeplant = uebernahmeDatumGeplant;
        return this;
    }

    public AufnehmenderProviderAKMTypeBuilder withAntwortfrist(LocalDate antwortfrist) {
        this.antwortfrist = antwortfrist;
        return this;
    }

    public AufnehmenderProviderAKMTypeBuilder withProvidernameAufnehmend(String providernameAufnehmend) {
        return (AufnehmenderProviderAKMTypeBuilder) super.withProvidernameAufnehmend(providernameAufnehmend);
    }

}
