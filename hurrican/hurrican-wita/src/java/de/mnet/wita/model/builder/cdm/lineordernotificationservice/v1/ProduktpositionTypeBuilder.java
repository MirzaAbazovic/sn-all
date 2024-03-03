/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AktionscodeType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungProduktType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.ProduktpositionType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class ProduktpositionTypeBuilder implements LineOrderNotificationTypeBuilder<ProduktpositionType> {

    private AktionscodeType aktionscode;
    private MeldungProduktType produkt;

    @Override
    public ProduktpositionType build() {
        ProduktpositionType produktpositionType = new ProduktpositionType();
        produktpositionType.setAktionscode(aktionscode);
        produktpositionType.setProdukt(produkt);
        return produktpositionType;
    }

    public ProduktpositionTypeBuilder withAktionscode(AktionscodeType aktionscode) {
        this.aktionscode = aktionscode;
        return this;
    }

    public ProduktpositionTypeBuilder withProdukt(MeldungProduktType produkt) {
        this.produkt = produkt;
        return this;
    }

}
