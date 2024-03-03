/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.BestandsuebersichtType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class GeschaeftsfallBestandsuebersichtBuilder implements LineOrderTypeBuilder<Geschaeftsfall> {

    private BestandsuebersichtType ausbue;

    @Override
    public Geschaeftsfall build() {
        Geschaeftsfall geschaeftsfall = new Geschaeftsfall();
        geschaeftsfall.setAUSBUE(ausbue);
        return null;
    }

    public GeschaeftsfallBestandsuebersichtBuilder withAusBue(BestandsuebersichtType ausbue) {
        this.ausbue = ausbue;
        return this;
    }

}
