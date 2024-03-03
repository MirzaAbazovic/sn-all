/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeTALAKMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.RufnummernportierungMeldungType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeTALAKMPVTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungsattributeTALAKMPVType> {

    private String vorabstimmungsID;
    private AngabenZurLeitungType leitung;
    private AnschlussType anschluss;
    private RufnummernportierungMeldungType rnrPortierung;

    @Override
    public MeldungsattributeTALAKMPVType build() {
        MeldungsattributeTALAKMPVType meldungsattribute = new MeldungsattributeTALAKMPVType();
        meldungsattribute.setVorabstimmungsID(vorabstimmungsID);
        meldungsattribute.setAnschluss(anschluss);
        meldungsattribute.setLeitung(leitung);
        meldungsattribute.setRnrPortierung(rnrPortierung);
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

    public MeldungsattributeTALAKMPVTypeBuilder withRnrPortierung(RufnummernportierungMeldungType rnrPortierung) {
        this.rnrPortierung = rnrPortierung;
        return this;
    }

}
