/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortwechselType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class GeschaeftsfallPortwechselBuilder implements LineOrderTypeBuilder<Geschaeftsfall> {

    private PortwechselType serpow;

    @Override
    public Geschaeftsfall build() {
        Geschaeftsfall geschaeftsfall = new Geschaeftsfall();
        geschaeftsfall.setSERPOW(serpow);
        return null;
    }

    public GeschaeftsfallPortwechselBuilder withSerPow(PortwechselType serpow) {
        this.serpow = serpow;
        return this;
    }

}
