/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AktionscodeType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class AuftragspositionTypeBuilder implements LineOrderTypeBuilder<AuftragspositionType> {

    private AktionscodeType aktionscode;
    private ProduktType produkt;
    private List<AuftragspositionType> position;

    @Override
    public AuftragspositionType build() {
        return enrich(new AuftragspositionType());
    }

    protected <AP extends AuftragspositionType> AP enrich(AP auftragsposition) {
        auftragsposition.setProdukt(produkt);
        auftragsposition.setAktionscode(aktionscode);
        if (position != null) {
            auftragsposition.getPosition().addAll(position);
        }
        return auftragsposition;
    }

    public AuftragspositionTypeBuilder withAktionscode(AktionscodeType aktionscode) {
        this.aktionscode = aktionscode;
        return this;
    }

    public AuftragspositionTypeBuilder withProdukt(ProduktType produkt) {
        this.produkt = produkt;
        return this;
    }

    public AuftragspositionTypeBuilder withPosition(List<AuftragspositionType> position) {
        this.position = position;
        return this;
    }

    public AuftragspositionTypeBuilder addPosition(AuftragspositionType position) {
        if (this.position == null) {
            this.position = new ArrayList<>();
        }
        this.position.add(position);
        return this;
    }

}
