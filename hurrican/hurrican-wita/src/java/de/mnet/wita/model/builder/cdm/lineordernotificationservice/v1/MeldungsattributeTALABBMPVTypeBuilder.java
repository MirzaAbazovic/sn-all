/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeTALABBMPVType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeTALABBMPVTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungsattributeTALABBMPVType> {

    private AngabenZurLeitungType leitung;

    @Override
    public MeldungsattributeTALABBMPVType build() {
        MeldungsattributeTALABBMPVType meldungsattribute = new MeldungsattributeTALABBMPVType();
        meldungsattribute.setLeitung(leitung);
        return meldungsattribute;
    }

    public MeldungsattributeTALABBMPVTypeBuilder withLeitung(AngabenZurLeitungType leitung) {
        this.leitung = leitung;
        return this;
    }

}
