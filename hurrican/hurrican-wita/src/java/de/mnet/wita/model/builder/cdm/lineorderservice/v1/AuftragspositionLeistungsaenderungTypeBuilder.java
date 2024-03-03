/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AktionscodeType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionLeistungsaenderungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsmerkmalPositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALLeistungsaenderungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class AuftragspositionLeistungsaenderungTypeBuilder implements LineOrderTypeBuilder<AuftragspositionLeistungsaenderungType> {

    private AktionscodeType aktionscode;
    private ProduktType produkt;
    private List<LeistungsmerkmalPositionType> position;
    private TALLeistungsaenderungType tal;

    @Override
    public AuftragspositionLeistungsaenderungType build() {
        AuftragspositionLeistungsaenderungType leistungsaenderung = new AuftragspositionLeistungsaenderungType();
        leistungsaenderung.setProdukt(produkt);
        leistungsaenderung.setAktionscode(aktionscode);

        if (tal != null) {
            AuftragspositionLeistungsaenderungType.GeschaeftsfallProdukt geschaeftsfallProdukt =
                    new AuftragspositionLeistungsaenderungType.GeschaeftsfallProdukt();
            geschaeftsfallProdukt.setTAL(tal);
            leistungsaenderung.setGeschaeftsfallProdukt(geschaeftsfallProdukt);
        }

        if (position != null) {
            leistungsaenderung.getPosition().addAll(position);
        }
        return leistungsaenderung;
    }

    public AuftragspositionLeistungsaenderungTypeBuilder withAktionscode(AktionscodeType aktionscode) {
        this.aktionscode = aktionscode;
        return this;
    }

    public AuftragspositionLeistungsaenderungTypeBuilder withProdukt(ProduktType produkt) {
        this.produkt = produkt;
        return this;
    }

    public AuftragspositionLeistungsaenderungTypeBuilder withPosition(List<LeistungsmerkmalPositionType> position) {
        this.position = position;
        return this;
    }

    public AuftragspositionLeistungsaenderungTypeBuilder addPosition(LeistungsmerkmalPositionType position) {
        if (this.position == null) {
            this.position = new ArrayList<>();
        }
        this.position.add(position);
        return this;
    }

    public AuftragspositionLeistungsaenderungTypeBuilder withTal(TALLeistungsaenderungType tal) {
        this.tal = tal;
        return this;
    }
}
