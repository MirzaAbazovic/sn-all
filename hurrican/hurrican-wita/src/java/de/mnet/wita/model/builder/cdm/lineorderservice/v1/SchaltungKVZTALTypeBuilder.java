/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.SchaltungKVZTALType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class SchaltungKVZTALTypeBuilder implements LineOrderTypeBuilder<SchaltungKVZTALType> {

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
