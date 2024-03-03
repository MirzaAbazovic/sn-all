/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.SchaltangabenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.SchaltungGlasfaserType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.SchaltungIsisOpalType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.SchaltungKVZTALType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.SchaltungKupferType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class SchaltangabenTypeBuilder implements LineOrderTypeBuilder<SchaltangabenType> {

    private List<SchaltangabenType.Schaltung> schaltung = new ArrayList<>();

    @Override
    public SchaltangabenType build() {
        SchaltangabenType schaltangabenType = new SchaltangabenType();
        schaltangabenType.getSchaltung().addAll(schaltung);
        return null;
    }

    public SchaltangabenTypeBuilder addGlasfaser(SchaltungGlasfaserType glasfaser) {
        this.schaltung.add(
                new SchaltungBuilder()
                        .withGlasfaser(glasfaser)
                        .build()
        );
        return this;
    }

    public SchaltangabenTypeBuilder withIsisOpal(SchaltungIsisOpalType isisOpal) {
        this.schaltung.add(
                new SchaltungBuilder()
                        .withIsisOpal(isisOpal)
                        .build()
        );
        return this;
    }

    public SchaltangabenTypeBuilder withKupfer(SchaltungKupferType kupfer) {
        this.schaltung.add(
                new SchaltungBuilder()
                        .withKupfer(kupfer)
                        .build()
        );
        return this;
    }

    public SchaltangabenTypeBuilder withKvzTal(SchaltungKVZTALType kvzTal) {
        this.schaltung.add(
                new SchaltungBuilder()
                        .withKvzTal(kvzTal)
                        .build()
        );
        return this;
    }

    private static class SchaltungBuilder implements LineOrderTypeBuilder<SchaltangabenType.Schaltung> {

        private SchaltungGlasfaserType glasfaser;
        private SchaltungIsisOpalType isisOpal;
        private SchaltungKupferType kupfer;
        private SchaltungKVZTALType kvzTal;

        @Override
        public SchaltangabenType.Schaltung build() {
            return null;
        }

        public SchaltungBuilder withGlasfaser(SchaltungGlasfaserType glasfaser) {
            this.glasfaser = glasfaser;
            return this;
        }

        public SchaltungBuilder withIsisOpal(SchaltungIsisOpalType isisOpal) {
            this.isisOpal = isisOpal;
            return this;
        }

        public SchaltungBuilder withKupfer(SchaltungKupferType kupfer) {
            this.kupfer = kupfer;
            return this;
        }

        public SchaltungBuilder withKvzTal(SchaltungKVZTALType kvzTal) {
            this.kvzTal = kvzTal;
            return this;
        }

    }

}
