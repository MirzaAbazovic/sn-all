/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.SchaltungIsisOpalMeldungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.SchaltungKupferType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class SchaltungIsisOpalMeldungTypeBuilder implements LineOrderNotificationTypeBuilder<SchaltungIsisOpalMeldungType> {

    private List<SchaltungKupferType> schaltung;
    private String v5ID;
    private List<String> zeitschlitz;

    @Override
    public SchaltungIsisOpalMeldungType build() {
        SchaltungIsisOpalMeldungType schaltungIsisOpalMeldungType = new SchaltungIsisOpalMeldungType();
        schaltungIsisOpalMeldungType.setV5ID(v5ID);
        if (schaltung != null) {
            schaltungIsisOpalMeldungType.getSchaltung().addAll(schaltung);
        }
        if (zeitschlitz != null) {
            schaltungIsisOpalMeldungType.getZeitschlitz().addAll(zeitschlitz);
        }
        return schaltungIsisOpalMeldungType;
    }

    public SchaltungIsisOpalMeldungTypeBuilder withSchaltung(List<SchaltungKupferType> schaltung) {
        this.schaltung = schaltung;
        return this;
    }

    public SchaltungIsisOpalMeldungTypeBuilder addSchaltung(SchaltungKupferType schaltung) {
        if (this.schaltung == null) {
            this.schaltung = new ArrayList<>();
        }
        this.schaltung.add(schaltung);
        return this;
    }

    public SchaltungIsisOpalMeldungTypeBuilder withV5ID(String v5ID) {
        this.v5ID = v5ID;
        return this;
    }

    public SchaltungIsisOpalMeldungTypeBuilder withZeitschlitz(List<String> zeitschlitz) {
        this.zeitschlitz = zeitschlitz;
        return this;
    }

    public SchaltungIsisOpalMeldungTypeBuilder addZeitschlitz(String zeitschlitz) {
        if (this.zeitschlitz == null) {
            this.zeitschlitz = new ArrayList<>();
        }
        this.zeitschlitz.add(zeitschlitz);
        return this;
    }

}
