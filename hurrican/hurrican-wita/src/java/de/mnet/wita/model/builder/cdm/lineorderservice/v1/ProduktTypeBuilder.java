/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class ProduktTypeBuilder implements LineOrderTypeBuilder<ProduktType> {

    private String kategorie;
    private String bezeichner;

    @Override
    public ProduktType build() {
        return enrich(new ProduktType());
    }

    protected <P extends ProduktType> P enrich(P produkt) {
        produkt.setKategorie(kategorie);
        produkt.setBezeichner(bezeichner);
        return produkt;
    }

    public ProduktTypeBuilder withKategorie(String kategorie) {
        this.kategorie = kategorie;
        return this;
    }

    public ProduktTypeBuilder withBezeichner(String bezeichner) {
        this.bezeichner = bezeichner;
        return this;
    }

}
