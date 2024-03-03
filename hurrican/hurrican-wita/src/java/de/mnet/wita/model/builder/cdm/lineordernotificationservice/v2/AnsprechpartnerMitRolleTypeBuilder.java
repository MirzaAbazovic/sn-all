/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnsprechpartnerMitRolleType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnsprechpartnerRolleType;

/**
 *
 */
public class AnsprechpartnerMitRolleTypeBuilder extends AnsprechpartnerBaseTypeBuilder {

    private AnsprechpartnerRolleType rolle;

    @Override
    public AnsprechpartnerMitRolleType build() {
        AnsprechpartnerMitRolleType ansprechpartner = new AnsprechpartnerMitRolleType();
        ansprechpartner.setRolle(rolle);
        return super.enrich(ansprechpartner);
    }

    public AnsprechpartnerMitRolleTypeBuilder withRolle(AnsprechpartnerRolleType rolle) {
        this.rolle = rolle;
        return this;
    }

}
