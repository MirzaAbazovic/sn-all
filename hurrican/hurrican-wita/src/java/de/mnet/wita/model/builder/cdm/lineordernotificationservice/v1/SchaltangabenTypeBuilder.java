/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.SchaltangabenType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.SchaltungAbstractType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class SchaltangabenTypeBuilder implements LineOrderNotificationTypeBuilder<SchaltangabenType> {

    private List<SchaltungAbstractType> schaltung = new ArrayList<>();

    @Override
    public SchaltangabenType build() {
        SchaltangabenType schaltangabenType = new SchaltangabenType();
        schaltangabenType.getSchaltung().addAll(schaltung);
        return null;
    }

    public SchaltangabenTypeBuilder addSchaltung(SchaltungAbstractType schaltungAbstractType) {
        this.schaltung.add(schaltungAbstractType);
        return this;
    }

}
