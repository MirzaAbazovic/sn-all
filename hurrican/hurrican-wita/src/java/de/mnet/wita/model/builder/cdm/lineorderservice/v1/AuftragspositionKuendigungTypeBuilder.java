/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionKuendigungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALKuendigungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.XDSLbKuendigungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class AuftragspositionKuendigungTypeBuilder implements LineOrderTypeBuilder<AuftragspositionKuendigungType> {

    protected ProduktType produkt;
    protected AuftragspositionKuendigungType.GeschaeftsfallProdukt geschaeftsfallProdukt;
    protected List<AuftragspositionType> position;
    protected TALKuendigungType tal;
    protected XDSLbKuendigungType xdslb;

    @Override
    public AuftragspositionKuendigungType build() {
        AuftragspositionKuendigungType auftragspositionKuendigung = new AuftragspositionKuendigungType();
        auftragspositionKuendigung.setProdukt(produkt);
        if (position != null) {
            auftragspositionKuendigung.getPosition().addAll(position);
        }
        if (tal != null || xdslb != null) {
            AuftragspositionKuendigungType.GeschaeftsfallProdukt geschaeftsfallProdukt =
                    new AuftragspositionKuendigungType.GeschaeftsfallProdukt();
            geschaeftsfallProdukt.setTAL(tal);
            geschaeftsfallProdukt.setXDSLB(xdslb);
            auftragspositionKuendigung.setGeschaeftsfallProdukt(geschaeftsfallProdukt);
        }
        return auftragspositionKuendigung;
    }

    public AuftragspositionKuendigungTypeBuilder withProdukt(ProduktType produkt) {
        this.produkt = produkt;
        return this;
    }

    public AuftragspositionKuendigungTypeBuilder withPosition(List<AuftragspositionType> position) {
        this.position = position;
        return this;
    }

    public AuftragspositionKuendigungTypeBuilder withTal(TALKuendigungType tal) {
        this.tal = tal;
        return this;
    }

    public AuftragspositionKuendigungTypeBuilder withXdslb(XDSLbKuendigungType xdslb) {
        this.xdslb = xdslb;
        return this;
    }

}
