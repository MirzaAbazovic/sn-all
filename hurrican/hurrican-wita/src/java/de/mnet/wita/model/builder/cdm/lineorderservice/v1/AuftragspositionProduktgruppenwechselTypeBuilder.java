/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionProduktgruppenwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsmerkmalPositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALProduktgruppenWechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.XDSLbProduktgruppenWechselType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class AuftragspositionProduktgruppenwechselTypeBuilder implements LineOrderTypeBuilder<AuftragspositionProduktgruppenwechselType> {

    private ProduktType produkt;
    private List<AuftragspositionType> position;
    private TALProduktgruppenWechselType tal;
    private XDSLbProduktgruppenWechselType xdslb;

    @Override
    public AuftragspositionProduktgruppenwechselType build() {
        AuftragspositionProduktgruppenwechselType produktgruppenwechsel =
                new AuftragspositionProduktgruppenwechselType();
        produktgruppenwechsel.setProdukt(produkt);
        if (position != null) {
            produktgruppenwechsel.getPosition().addAll(position);
        }
        if (tal != null || xdslb != null) {
            AuftragspositionProduktgruppenwechselType.GeschaeftsfallProdukt geschaeftsfallProdukt =
                    new AuftragspositionProduktgruppenwechselType.GeschaeftsfallProdukt();
            geschaeftsfallProdukt.setTAL(tal);
            geschaeftsfallProdukt.setXDSLB(xdslb);
            produktgruppenwechsel.setGeschaeftsfallProdukt(geschaeftsfallProdukt);
        }
        return produktgruppenwechsel;
    }

    public AuftragspositionProduktgruppenwechselTypeBuilder withProdukt(ProduktType produkt) {
        this.produkt = produkt;
        return this;
    }

    public AuftragspositionProduktgruppenwechselTypeBuilder withPosition(List<AuftragspositionType> position) {
        this.position = position;
        return this;
    }

    public AuftragspositionProduktgruppenwechselTypeBuilder withTal(TALProduktgruppenWechselType tal) {
        this.tal = tal;
        return this;
    }

    public AuftragspositionProduktgruppenwechselTypeBuilder withXdslb(XDSLbProduktgruppenWechselType xdslb) {
        this.xdslb = xdslb;
        return this;
    }

    public AuftragspositionProduktgruppenwechselTypeBuilder addPosition(LeistungsmerkmalPositionType position) {
        if (this.position == null) {
            this.position = new ArrayList<>();
        }
        this.position.add(position);
        return this;
    }

}
