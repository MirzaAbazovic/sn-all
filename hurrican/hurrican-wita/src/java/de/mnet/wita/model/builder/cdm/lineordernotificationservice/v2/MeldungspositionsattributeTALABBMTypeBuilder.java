/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.DoppeladerBelegtType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungspositionsattributeTALABBMType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungspositionsattributeTALABBMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungspositionsattributeTALABBMType> {

    private DoppeladerBelegtType doppeladerBelegt;

    @Override
    public MeldungspositionsattributeTALABBMType build() {
        return null;
    }

    public MeldungspositionsattributeTALABBMTypeBuilder withDoppeladerBelegt(DoppeladerBelegtType doppeladerBelegt) {
        this.doppeladerBelegt = doppeladerBelegt;
        return this;
    }
}
