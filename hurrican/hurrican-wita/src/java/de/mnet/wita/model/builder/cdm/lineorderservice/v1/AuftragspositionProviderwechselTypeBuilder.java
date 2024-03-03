/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionProviderwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsmerkmalPositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALProviderwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.XDSLbProviderwechselType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class AuftragspositionProviderwechselTypeBuilder implements LineOrderTypeBuilder<AuftragspositionProviderwechselType> {

    private ProduktType produkt;
    private List<AuftragspositionType> position;
    private TALProviderwechselType tal;
    private XDSLbProviderwechselType xdslb;

    @Override
    public AuftragspositionProviderwechselType build() {
        AuftragspositionProviderwechselType produktgruppenwechsel =
                new AuftragspositionProviderwechselType();
        produktgruppenwechsel.setProdukt(produkt);
        if (position != null) {
            produktgruppenwechsel.getPosition().addAll(position);
        }
        if (tal != null || xdslb != null) {
            AuftragspositionProviderwechselType.GeschaeftsfallProdukt geschaeftsfallProdukt =
                    new AuftragspositionProviderwechselType.GeschaeftsfallProdukt();
            geschaeftsfallProdukt.setTAL(tal);
            geschaeftsfallProdukt.setXDSLB(xdslb);
            produktgruppenwechsel.setGeschaeftsfallProdukt(geschaeftsfallProdukt);
        }
        return produktgruppenwechsel;
    }

    public AuftragspositionProviderwechselTypeBuilder withProdukt(ProduktType produkt) {
        this.produkt = produkt;
        return this;
    }

    public AuftragspositionProviderwechselTypeBuilder withPosition(List<AuftragspositionType> position) {
        this.position = position;
        return this;
    }

    public AuftragspositionProviderwechselTypeBuilder withTal(TALProviderwechselType tal) {
        this.tal = tal;
        return this;
    }

    public AuftragspositionProviderwechselTypeBuilder withXdslb(XDSLbProviderwechselType xdslb) {
        this.xdslb = xdslb;
        return this;
    }

    public AuftragspositionProviderwechselTypeBuilder addPosition(LeistungsmerkmalPositionType position) {
        if (this.position == null) {
            this.position = new ArrayList<>();
        }
        this.position.add(position);
        return this;
    }

}
