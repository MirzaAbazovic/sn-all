/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnlageMitTypType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsaenderungAnlagenType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class LeistungsaenderungAnlagenTypeBuilder implements LineOrderTypeBuilder<LeistungsaenderungAnlagenType> {

    private List<AnlageMitTypType> sonstige;

    @Override
    public LeistungsaenderungAnlagenType build() {
        LeistungsaenderungAnlagenType anlagenType = new LeistungsaenderungAnlagenType();
        if (sonstige != null) {
            anlagenType.getSonstige().addAll(sonstige);
        }
        return anlagenType;
    }

    public LeistungsaenderungAnlagenTypeBuilder withSonstige(List<AnlageMitTypType> sonstige) {
        this.sonstige = sonstige;
        return this;
    }

    public LeistungsaenderungAnlagenTypeBuilder addSonstige(AnlageMitTypType sonstige) {
        if (this.sonstige == null) {
            this.sonstige = new ArrayList<>();
        }
        this.sonstige.add(sonstige);
        return this;
    }

}
