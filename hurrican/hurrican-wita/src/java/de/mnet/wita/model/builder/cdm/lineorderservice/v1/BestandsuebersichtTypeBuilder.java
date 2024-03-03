/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.BestandsuebersichtType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class BestandsuebersichtTypeBuilder implements LineOrderTypeBuilder<BestandsuebersichtType> {

    private String bktoFaktura;
    private String bktoBestand;
    private AnsprechpartnerType ansprechpartner;

    @Override
    public BestandsuebersichtType build() {
        BestandsuebersichtType bestandsuebersichtType = new BestandsuebersichtType();
        bestandsuebersichtType.setBKTOFaktura(bktoBestand);
        bestandsuebersichtType.setAnsprechpartner(ansprechpartner);
        bestandsuebersichtType.setBKTOFaktura(bktoFaktura);
        return bestandsuebersichtType;
    }

    public BestandsuebersichtTypeBuilder withBktoFaktura(String bktoFaktura) {
        this.bktoFaktura = bktoFaktura;
        return this;
    }

    public BestandsuebersichtTypeBuilder withBktoBestand(String bktoBestand) {
        this.bktoBestand = bktoBestand;
        return this;
    }

    public BestandsuebersichtTypeBuilder withAnsprechpartner(AnsprechpartnerType ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
        return this;
    }

}
