/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTALAKMPVType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeTALAKMPVTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungsattributeTALAKMPVType> {

    private String vorabstimmungsID;
    private AngabenZurLeitungType leitung;
    private AnschlussType anschluss;

    @Override
    public MeldungsattributeTALAKMPVType build() {
        MeldungsattributeTALAKMPVType meldungsattribute = new MeldungsattributeTALAKMPVType();
        meldungsattribute.setVorabstimmungsID(vorabstimmungsID);
        meldungsattribute.setAnschluss(anschluss);
        meldungsattribute.setLeitung(leitung);
        return meldungsattribute;
    }

    public MeldungsattributeTALAKMPVTypeBuilder withVorabstimmungsID(String vorabstimmungsID) {
        this.vorabstimmungsID = vorabstimmungsID;
        return this;
    }

    public MeldungsattributeTALAKMPVTypeBuilder withLeitung(AngabenZurLeitungType leitung) {
        this.leitung = leitung;
        return this;
    }

    public MeldungsattributeTALAKMPVTypeBuilder withAnschluss(AnschlussType anschluss) {
        this.anschluss = anschluss;
        return this;
    }
}
