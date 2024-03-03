/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTALERLMPVType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeTALERLMPVTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungsattributeTALERLMPVType> {

    private AngabenZurLeitungType leitung;
    private AnschlussType anschluss;

    @Override
    public MeldungsattributeTALERLMPVType build() {
        MeldungsattributeTALERLMPVType meldungsattribute = new MeldungsattributeTALERLMPVType();
        meldungsattribute.setAnschluss(anschluss);
        meldungsattribute.setLeitung(leitung);
        return meldungsattribute;
    }

    public MeldungsattributeTALERLMPVTypeBuilder withLeitung(AngabenZurLeitungType leitung) {
        this.leitung = leitung;
        return this;
    }

    public MeldungsattributeTALERLMPVTypeBuilder withAnschluss(AnschlussType anschluss) {
        this.anschluss = anschluss;
        return this;
    }

}
