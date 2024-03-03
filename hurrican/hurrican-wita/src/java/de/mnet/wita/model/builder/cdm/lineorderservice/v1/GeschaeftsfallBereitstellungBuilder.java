/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.BereitstellungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class GeschaeftsfallBereitstellungBuilder implements LineOrderTypeBuilder<Geschaeftsfall> {

    private BereitstellungType neu;

    @Override
    public Geschaeftsfall build() {
        Geschaeftsfall geschaeftsfall = new Geschaeftsfall();
        geschaeftsfall.setNEU(neu);
        return null;
    }

    public GeschaeftsfallBereitstellungBuilder withNeu(BereitstellungType neu) {
        this.neu = neu;
        return this;
    }

}
