/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.FirmaType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class FirmaTypeBuilder implements LineOrderTypeBuilder<FirmaType> {

    private String anrede;
    private String firmenname;
    private String firmennameZweiterTeil;

    @Override
    public FirmaType build() {
        FirmaType firmaType = new FirmaType();
        firmaType.setAnrede(anrede);
        firmaType.setFirmenname(firmenname);
        firmaType.setFirmennameZweiterTeil(firmennameZweiterTeil);
        return firmaType;
    }

    public FirmaTypeBuilder withAnrede(String anrede) {
        this.anrede = anrede;
        return this;
    }

    public FirmaTypeBuilder withFirmenname(String firmenname) {
        this.firmenname = firmenname;
        return this;
    }

    public FirmaTypeBuilder withFirmennameZweiterTeil(String firmennameZweiterTeil) {
        this.firmennameZweiterTeil = firmennameZweiterTeil;
        return this;
    }

}
