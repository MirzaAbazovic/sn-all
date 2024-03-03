/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.DoppeladerBelegtType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.DurchwahlanlageBestandType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungspositionsattributeTALABBMType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungspositionsattributeTALABBMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungspositionsattributeTALABBMType> {

    private DoppeladerBelegtType doppeladerBelegt;
    private DurchwahlanlageBestandType anschlussPortierungKorrekt;

    @Override
    public MeldungspositionsattributeTALABBMType build() {
        return null;
    }

    public MeldungspositionsattributeTALABBMTypeBuilder withDoppeladerBelegt(DoppeladerBelegtType doppeladerBelegt) {
        this.doppeladerBelegt = doppeladerBelegt;
        return this;
    }

    public MeldungspositionsattributeTALABBMTypeBuilder withAnschlussPortierungKorrekt(DurchwahlanlageBestandType anschlussPortierungKorrekt) {
        this.anschlussPortierungKorrekt = anschlussPortierungKorrekt;
        return this;
    }

}
