/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionBereitstellungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALBereitstellungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.XDSLbBereitstellungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class AuftragspositionBereitstellungTypeBuilder implements LineOrderTypeBuilder<AuftragspositionBereitstellungType> {

    private ProduktType produkt;
    private List<AuftragspositionType> position;
    private TALBereitstellungType tal;
    private XDSLbBereitstellungType xdslb;

    @Override
    public AuftragspositionBereitstellungType build() {
        AuftragspositionBereitstellungType auftragspositionBereitstellung = new AuftragspositionBereitstellungType();
        auftragspositionBereitstellung.setProdukt(produkt);
        if (position != null) {
            auftragspositionBereitstellung.getPosition().addAll(position);
        }
        if (tal != null || xdslb != null) {
            AuftragspositionBereitstellungType.GeschaeftsfallProdukt geschaeftsfallProdukt =
                    new AuftragspositionBereitstellungType.GeschaeftsfallProdukt();
            geschaeftsfallProdukt.setTAL(tal);
            geschaeftsfallProdukt.setXDSLB(xdslb);
            auftragspositionBereitstellung.setGeschaeftsfallProdukt(geschaeftsfallProdukt);
        }
        return auftragspositionBereitstellung;
    }

    public AuftragspositionBereitstellungTypeBuilder withProdukt(ProduktType produkt) {
        this.produkt = produkt;
        return this;
    }

    public AuftragspositionBereitstellungTypeBuilder withPosition(List<AuftragspositionType> position) {
        this.position = position;
        return this;
    }

    public AuftragspositionBereitstellungTypeBuilder withTal(TALBereitstellungType tal) {
        this.tal = tal;
        return this;
    }

    public AuftragspositionBereitstellungTypeBuilder withXdslb(XDSLbBereitstellungType xdslb) {
        this.xdslb = xdslb;
        return this;
    }

}
