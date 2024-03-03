/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktgruppenwechselType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class GeschaeftsfallProduktgruppenwechselBuilder implements LineOrderTypeBuilder<Geschaeftsfall> {

    private ProduktgruppenwechselType pgw;

    @Override
    public Geschaeftsfall build() {
        Geschaeftsfall geschaeftsfall = new Geschaeftsfall();
        geschaeftsfall.setPGW(pgw);
        return null;
    }

    public GeschaeftsfallProduktgruppenwechselBuilder withPgw(ProduktgruppenwechselType pgw) {
        this.pgw = pgw;
        return this;
    }

}
