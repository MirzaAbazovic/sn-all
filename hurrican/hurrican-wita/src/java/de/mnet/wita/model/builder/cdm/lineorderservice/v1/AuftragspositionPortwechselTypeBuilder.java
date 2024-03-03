/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionPortwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALPortwechselType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class AuftragspositionPortwechselTypeBuilder implements LineOrderTypeBuilder<AuftragspositionPortwechselType> {

    private ProduktType produkt;
    private List<AuftragspositionType> position;
    private TALPortwechselType tal;

    @Override
    public AuftragspositionPortwechselType build() {
        AuftragspositionPortwechselType auftragspositionPortwechsel = new AuftragspositionPortwechselType();
        auftragspositionPortwechsel.setProdukt(produkt);
        if (position != null) {
            auftragspositionPortwechsel.getPosition().addAll(position);
        }
        if (tal != null) {
            AuftragspositionPortwechselType.GeschaeftsfallProdukt geschaeftsfallProdukt =
                    new AuftragspositionPortwechselType.GeschaeftsfallProdukt();
            geschaeftsfallProdukt.setTAL(tal);
            auftragspositionPortwechsel.setGeschaeftsfallProdukt(geschaeftsfallProdukt);
        }
        return auftragspositionPortwechsel;
    }

    public AuftragspositionPortwechselTypeBuilder withProdukt(ProduktType produkt) {
        this.produkt = produkt;
        return this;
    }

    public AuftragspositionPortwechselTypeBuilder withPosition(List<AuftragspositionType> position) {
        this.position = position;
        return this;
    }

    public AuftragspositionPortwechselTypeBuilder withTal(TALPortwechselType tal) {
        this.tal = tal;
        return this;
    }

}
