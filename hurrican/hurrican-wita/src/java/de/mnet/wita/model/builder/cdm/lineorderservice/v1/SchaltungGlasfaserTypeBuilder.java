/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.SchaltungGlasfaserType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class SchaltungGlasfaserTypeBuilder implements LineOrderTypeBuilder<SchaltungGlasfaserType> {

    private String uevt;
    private String evs;
    private String kupplung;

    @Override
    public SchaltungGlasfaserType build() {
        SchaltungGlasfaserType schaltungGlasfaserType = new SchaltungGlasfaserType();
        schaltungGlasfaserType.setEVS(evs);
        schaltungGlasfaserType.setKupplung(kupplung);
        schaltungGlasfaserType.setUEVT(uevt);
        return schaltungGlasfaserType;
    }

    public SchaltungGlasfaserTypeBuilder withUevt(String uevt) {
        this.uevt = uevt;
        return this;
    }

    public SchaltungGlasfaserTypeBuilder withEvs(String evs) {
        this.evs = evs;
        return this;
    }

    public SchaltungGlasfaserTypeBuilder withKupplung(String kupplung) {
        this.kupplung = kupplung;
        return this;
    }

}
