/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.BasisAngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTALRUEMPVType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeTALRUEMPVTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungsattributeTALRUEMPVType> {

    private BasisAngabenZurLeitungType leitung;
    private AnschlussType anschluss;
    private String vorabstimmungsID;

    @Override
    public MeldungsattributeTALRUEMPVType build() {
        MeldungsattributeTALRUEMPVType meldungsattributeTALRUEMPVType = new MeldungsattributeTALRUEMPVType();
        meldungsattributeTALRUEMPVType.setAnschluss(anschluss);
        meldungsattributeTALRUEMPVType.setLeitung(leitung);
        meldungsattributeTALRUEMPVType.setVorabstimmungsID(vorabstimmungsID);
        return meldungsattributeTALRUEMPVType;
    }

    public MeldungsattributeTALRUEMPVTypeBuilder withLeitung(BasisAngabenZurLeitungType leitung) {
        this.leitung = leitung;
        return this;
    }

    public MeldungsattributeTALRUEMPVTypeBuilder withAnschluss(AnschlussType anschluss) {
        this.anschluss = anschluss;
        return this;
    }

    public MeldungsattributeTALRUEMPVTypeBuilder withVorabstimmungsID(String vorabstimmungsID) {
        this.vorabstimmungsID = vorabstimmungsID;
        return this;
    }

}
