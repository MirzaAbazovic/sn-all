/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungsattributeTALRUEMPVType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class MeldungsattributeTALRUEMPVTypeBuilder implements LineOrderTypeBuilder<MeldungsattributeTALRUEMPVType> {

    private AngabenZurLeitungType leitung;
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

    public MeldungsattributeTALRUEMPVTypeBuilder withLeitung(AngabenZurLeitungType leitung) {
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
