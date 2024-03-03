/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnlageMitTypType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnlageType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.BereitstellungAnlagenType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class BereitstellungAnlagenTypeBuilder implements LineOrderTypeBuilder<BereitstellungAnlagenType> {

    private AnlageType lageplan;
    private List<AnlageMitTypType> sonstige;

    @Override
    public BereitstellungAnlagenType build() {
        BereitstellungAnlagenType bereitstellungAnlagenType = new BereitstellungAnlagenType();
        bereitstellungAnlagenType.setLageplan(lageplan);
        if (sonstige != null) {
            bereitstellungAnlagenType.getSonstige().addAll(sonstige);
        }
        return bereitstellungAnlagenType;
    }

    public BereitstellungAnlagenTypeBuilder withLageplan(AnlageType lageplan) {
        this.lageplan = lageplan;
        return this;
    }

    public BereitstellungAnlagenTypeBuilder withSonstige(List<AnlageMitTypType> sonstige) {
        this.sonstige = sonstige;
        return this;
    }

    public BereitstellungAnlagenTypeBuilder addSonstige(AnlageMitTypType sonstige) {
        if (this.sonstige == null) {
            this.sonstige = new ArrayList<>();
        }
        this.sonstige.add(sonstige);
        return this;
    }

}
