/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeTALERLMType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeTALERLMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungsattributeTALERLMType> {

    private AnschlussType anschluss;

    @Override
    public MeldungsattributeTALERLMType build() {
        MeldungsattributeTALERLMType meldungsattribute = new MeldungsattributeTALERLMType();
        meldungsattribute.setAnschluss(anschluss);
        return meldungsattribute;
    }

    public MeldungsattributeTALERLMTypeBuilder withAnschluss(AnschlussType anschluss) {
        this.anschluss = anschluss;
        return this;
    }

}
