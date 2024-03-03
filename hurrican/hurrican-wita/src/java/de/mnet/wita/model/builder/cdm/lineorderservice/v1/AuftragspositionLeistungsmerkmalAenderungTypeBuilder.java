/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AktionscodeType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionLeistungsmerkmalAenderungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsmerkmalPositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALLeistungsmerkmalAenderungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.XDSLbLeistungsmerkmalAenderungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class AuftragspositionLeistungsmerkmalAenderungTypeBuilder implements LineOrderTypeBuilder<AuftragspositionLeistungsmerkmalAenderungType> {

    private AktionscodeType aktionscode;
    private ProduktType produkt;
    private List<LeistungsmerkmalPositionType> position;
    private TALLeistungsmerkmalAenderungType tal;
    private XDSLbLeistungsmerkmalAenderungType xdslb;

    @Override
    public AuftragspositionLeistungsmerkmalAenderungType build() {
        AuftragspositionLeistungsmerkmalAenderungType leistungsmerkmalAenderung =
                new AuftragspositionLeistungsmerkmalAenderungType();
        leistungsmerkmalAenderung.setProdukt(produkt);
        leistungsmerkmalAenderung.setAktionscode(aktionscode);
        if (position != null) {
            leistungsmerkmalAenderung.getPosition().addAll(position);
        }
        if (tal != null || xdslb != null) {
            AuftragspositionLeistungsmerkmalAenderungType.GeschaeftsfallProdukt geschaeftsfallProdukt =
                    new AuftragspositionLeistungsmerkmalAenderungType.GeschaeftsfallProdukt();
            geschaeftsfallProdukt.setTAL(tal);
            geschaeftsfallProdukt.setXDSLB(xdslb);
            leistungsmerkmalAenderung.setGeschaeftsfallProdukt(geschaeftsfallProdukt);
        }
        return leistungsmerkmalAenderung;
    }

    public AuftragspositionLeistungsmerkmalAenderungTypeBuilder withAktionscode(AktionscodeType aktionscode) {
        this.aktionscode = aktionscode;
        return this;
    }

    public AuftragspositionLeistungsmerkmalAenderungTypeBuilder withProdukt(ProduktType produkt) {
        this.produkt = produkt;
        return this;
    }

    public AuftragspositionLeistungsmerkmalAenderungTypeBuilder withPosition(List<LeistungsmerkmalPositionType> position) {
        this.position = position;
        return this;
    }

    public AuftragspositionLeistungsmerkmalAenderungTypeBuilder withTal(TALLeistungsmerkmalAenderungType tal) {
        this.tal = tal;
        return this;
    }

    public AuftragspositionLeistungsmerkmalAenderungTypeBuilder withXdslb(XDSLbLeistungsmerkmalAenderungType xdslb) {
        this.xdslb = xdslb;
        return this;
    }

    public AuftragspositionLeistungsmerkmalAenderungTypeBuilder addPosition(LeistungsmerkmalPositionType position) {
        if (this.position == null) {
            this.position = new ArrayList<>();
        }
        this.position.add(position);
        return this;
    }

}
