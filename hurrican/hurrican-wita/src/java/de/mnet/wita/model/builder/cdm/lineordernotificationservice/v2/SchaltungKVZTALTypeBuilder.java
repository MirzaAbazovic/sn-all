/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.SchaltungKVZTALType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class SchaltungKVZTALTypeBuilder implements LineOrderNotificationTypeBuilder<SchaltungKVZTALType> {

    private String kvzNr;
    private String kvzSchaltnummer;

    @Override
    public SchaltungKVZTALType build() {
        SchaltungKVZTALType schaltungKVZTALType = new SchaltungKVZTALType();
        schaltungKVZTALType.setKVZNr(kvzNr);
        schaltungKVZTALType.setKVZSchaltnummer(kvzSchaltnummer);
        return schaltungKVZTALType;
    }

    public SchaltungKVZTALTypeBuilder withKvzNr(String kvzNr) {
        this.kvzNr = kvzNr;
        return this;
    }

    public SchaltungKVZTALTypeBuilder withKvzSchaltnummer(String kvzSchaltnummer) {
        this.kvzSchaltnummer = kvzSchaltnummer;
        return this;
    }

}
