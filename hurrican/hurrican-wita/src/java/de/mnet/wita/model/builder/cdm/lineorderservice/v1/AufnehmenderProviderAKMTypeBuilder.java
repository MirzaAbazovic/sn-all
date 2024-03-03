/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.time.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AufnehmenderProviderAKMType;

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

    public AufnehmenderProviderAKMTypeBuilder withUebernahmeDatumVerbindlich(LocalDate uebernahmeDatumVerbindlich) {
        this.uebernahmeDatumGeplant = uebernahmeDatumVerbindlich;
        return this;
    }

    public AufnehmenderProviderAKMTypeBuilder setAntwortfrist(LocalDate antwortfrist) {
        this.antwortfrist = antwortfrist;
        return this;
    }

}
