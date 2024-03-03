/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTALABMPVType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeTALABMPVTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungsattributeTALABMPVType> {

    private AngabenZurLeitungType leitung;
    private AnschlussType anschluss;

    @Override
    public MeldungsattributeTALABMPVType build() {
        MeldungsattributeTALABMPVType meldungsattribute = new MeldungsattributeTALABMPVType();
        meldungsattribute.setAnschluss(anschluss);
        meldungsattribute.setLeitung(leitung);
        return meldungsattribute;
    }

    public MeldungsattributeTALABMPVTypeBuilder withLeitung(AngabenZurLeitungType leitung) {
        this.leitung = leitung;
        return this;
    }

    public MeldungsattributeTALABMPVTypeBuilder withAnschluss(AnschlussType anschluss) {
        this.anschluss = anschluss;
        return this;
    }

}
