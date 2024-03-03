/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungProduktType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.UebertragungsverfahrenType;

/**
 *
 */
public class MeldungProduktTypeBuilder extends ProduktTypeBuilder {

    private UebertragungsverfahrenType uebertragungsVerfahren;

    @Override
    public MeldungProduktType build() {
        MeldungProduktType produktType = new MeldungProduktType();
        produktType.setUebertragungsVerfahren(uebertragungsVerfahren);
        return enrich(produktType);
    }

    public MeldungProduktTypeBuilder withUebertragungsVerfahren(UebertragungsverfahrenType uebertragungsVerfahren) {
        this.uebertragungsVerfahren = uebertragungsVerfahren;
        return this;
    }

    public MeldungProduktTypeBuilder withKategorie(String kategorie) {
        return (MeldungProduktTypeBuilder) super.withKategorie(kategorie);
    }

    public MeldungProduktTypeBuilder withBezeichner(String bezeichner) {
        return (MeldungProduktTypeBuilder) super.withBezeichner(bezeichner);
    }

}
