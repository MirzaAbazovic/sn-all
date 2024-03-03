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
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.VerbundleistungAnlagenType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class VerbundleistungAnlagenTypeBuilder implements LineOrderTypeBuilder<VerbundleistungAnlagenType> {

    private AnlageType kuendigungsschreiben;
    private List<AnlageMitTypType> sonstige;

    @Override
    public VerbundleistungAnlagenType build() {
        VerbundleistungAnlagenType anlagenType = new VerbundleistungAnlagenType();
        anlagenType.setKuendigungsschreiben(kuendigungsschreiben);
        if (sonstige != null) {
            anlagenType.getSonstige().addAll(sonstige);
        }
        return anlagenType;
    }

    public VerbundleistungAnlagenTypeBuilder withVerbundleistungsschreiben(AnlageType kuendigungsschreiben) {
        this.kuendigungsschreiben = kuendigungsschreiben;
        return this;
    }

    public VerbundleistungAnlagenTypeBuilder withSonstige(List<AnlageMitTypType> sonstige) {
        this.sonstige = sonstige;
        return this;
    }

    public VerbundleistungAnlagenTypeBuilder addSonstige(AnlageMitTypType sonstige) {
        if (this.sonstige == null) {
            this.sonstige = new ArrayList<>();
        }
        this.sonstige.add(sonstige);
        return this;
    }

}
