/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypERGMType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungstypERGMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypERGMType> {

    private MeldungstypERGMType.Meldungsattribute meldungsattribute;

    @Override
    public MeldungstypERGMType build() {
        MeldungstypERGMType meldungstyp = new MeldungstypERGMType();
        meldungstyp.setMeldungsattribute(meldungsattribute);
        return meldungstyp;
    }

    public MeldungstypERGMTypeBuilder withMeldungsattribute(MeldungstypERGMType.Meldungsattribute meldungsattribute) {
        this.meldungsattribute = meldungsattribute;
        return this;
    }

}
