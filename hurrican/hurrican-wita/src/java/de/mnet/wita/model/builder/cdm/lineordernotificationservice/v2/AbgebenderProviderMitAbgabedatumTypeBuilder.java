/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.time.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AbgebenderProviderMitAbgabedatumType;

/**
 *
 */
public class AbgebenderProviderMitAbgabedatumTypeBuilder extends AbgebenderProviderTypeBuilder {

    private LocalDate abgabedatum;

    @Override
    public AbgebenderProviderMitAbgabedatumType build() {
        AbgebenderProviderMitAbgabedatumType abgebenderProviderType = new AbgebenderProviderMitAbgabedatumType();
        abgebenderProviderType.setAbgabedatum(DateConverterUtils.toXmlGregorianCalendar(abgabedatum));
        return enrich(abgebenderProviderType);
    }

    public AbgebenderProviderMitAbgabedatumTypeBuilder withAbgabedatum(LocalDate value) {
        this.abgabedatum = value;
        return this;
    }

}
