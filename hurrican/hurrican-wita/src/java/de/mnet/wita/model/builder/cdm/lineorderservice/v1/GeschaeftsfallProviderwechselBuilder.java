/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProviderwechselType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class GeschaeftsfallProviderwechselBuilder implements LineOrderTypeBuilder<Geschaeftsfall> {

    private ProviderwechselType pv;

    @Override
    public Geschaeftsfall build() {
        Geschaeftsfall geschaeftsfall = new Geschaeftsfall();
        geschaeftsfall.setPV(pv);
        return null;
    }

    public GeschaeftsfallProviderwechselBuilder withPv(ProviderwechselType pv) {
        this.pv = pv;
        return this;
    }

}
