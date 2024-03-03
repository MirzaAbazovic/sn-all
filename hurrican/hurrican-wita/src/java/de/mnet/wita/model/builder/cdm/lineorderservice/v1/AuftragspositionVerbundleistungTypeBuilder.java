/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionVerbundleistungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALVerbundleistungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.VerbundleistungProduktType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class AuftragspositionVerbundleistungTypeBuilder implements LineOrderTypeBuilder<AuftragspositionVerbundleistungType> {

    private ProduktType produkt;
    private VerbundleistungProduktType geschaeftsfallProdukt;
    private List<AuftragspositionType> position;
    private TALVerbundleistungType tal;

    @Override
    public AuftragspositionVerbundleistungType build() {
        AuftragspositionVerbundleistungType leistungsmerkmalAenderung =
                new AuftragspositionVerbundleistungType();
        leistungsmerkmalAenderung.setProdukt(produkt);

        if (tal != null) {
            AuftragspositionVerbundleistungType.GeschaeftsfallProdukt geschaeftsfallProdukt =
                    new AuftragspositionVerbundleistungType.GeschaeftsfallProdukt();
            geschaeftsfallProdukt.setTAL(tal);
            leistungsmerkmalAenderung.setGeschaeftsfallProdukt(geschaeftsfallProdukt);
        }

        if (position != null) {
            leistungsmerkmalAenderung.getPosition().addAll(position);
        }
        return leistungsmerkmalAenderung;
    }

    public AuftragspositionVerbundleistungTypeBuilder withGeschaeftsfallProdukt(VerbundleistungProduktType geschaeftsfallProdukt) {
        this.geschaeftsfallProdukt = geschaeftsfallProdukt;
        return this;
    }

    public AuftragspositionVerbundleistungTypeBuilder withProdukt(ProduktType produkt) {
        this.produkt = produkt;
        return this;
    }

    public AuftragspositionVerbundleistungTypeBuilder withPosition(List<AuftragspositionType> position) {
        this.position = position;
        return this;
    }

    public AuftragspositionVerbundleistungTypeBuilder addPosition(AuftragspositionType position) {
        if (this.position == null) {
            this.position = new ArrayList<>();
        }
        this.position.add(position);
        return this;
    }

    public AuftragspositionVerbundleistungTypeBuilder withTal(TALVerbundleistungType tal) {
        this.tal = tal;
        return this;
    }
}
