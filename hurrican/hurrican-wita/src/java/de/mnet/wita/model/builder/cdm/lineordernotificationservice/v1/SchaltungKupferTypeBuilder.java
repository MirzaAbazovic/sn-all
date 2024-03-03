/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.SchaltungKupferType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class SchaltungKupferTypeBuilder implements LineOrderNotificationTypeBuilder<SchaltungKupferType> {

    private String uevt;
    private String evs;
    private String doppelader;

    @Override
    public SchaltungKupferType build() {
        SchaltungKupferType schaltungKupferType = new SchaltungKupferType();
        schaltungKupferType.setEVS(evs);
        schaltungKupferType.setDoppelader(doppelader);
        schaltungKupferType.setUEVT(uevt);
        return schaltungKupferType;
    }

    public SchaltungKupferTypeBuilder withUevt(String uevt) {
        this.uevt = uevt;
        return this;
    }

    public SchaltungKupferTypeBuilder withEvs(String evs) {
        this.evs = evs;
        return this;
    }

    public SchaltungKupferTypeBuilder withDoppelader(String doppelader) {
        this.doppelader = doppelader;
        return this;
    }

}
