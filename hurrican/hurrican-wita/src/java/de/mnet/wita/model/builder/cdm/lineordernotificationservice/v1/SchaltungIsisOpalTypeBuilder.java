/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AnschlussartType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.SchaltungIsisOpalType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class SchaltungIsisOpalTypeBuilder implements LineOrderNotificationTypeBuilder<SchaltungIsisOpalType> {

    private String uevt;
    private AnschlussartType anschlussart;

    @Override
    public SchaltungIsisOpalType build() {
        SchaltungIsisOpalType schaltungIsisOpalType = new SchaltungIsisOpalType();
        schaltungIsisOpalType.setUEVT(uevt);
        schaltungIsisOpalType.setAnschlussart(anschlussart);
        return schaltungIsisOpalType;
    }

    public SchaltungIsisOpalTypeBuilder withUevt(String uevt) {
        this.uevt = uevt;
        return this;
    }

    public SchaltungIsisOpalTypeBuilder withAnschlussart(AnschlussartType anschlussart) {
        this.anschlussart = anschlussart;
        return this;
    }

}
