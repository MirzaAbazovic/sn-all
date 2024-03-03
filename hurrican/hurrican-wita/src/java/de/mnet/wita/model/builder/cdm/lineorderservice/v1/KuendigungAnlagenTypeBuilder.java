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
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KuendigungAnlagenType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class KuendigungAnlagenTypeBuilder implements LineOrderTypeBuilder<KuendigungAnlagenType> {

    private AnlageType kuendigungsschreiben;
    private List<AnlageMitTypType> sonstige;

    @Override
    public KuendigungAnlagenType build() {
        KuendigungAnlagenType bereitstellungAnlagenType = new KuendigungAnlagenType();
        bereitstellungAnlagenType.setKuendigungsschreiben(kuendigungsschreiben);
        if (sonstige != null) {
            bereitstellungAnlagenType.getSonstige().addAll(sonstige);
        }
        return bereitstellungAnlagenType;
    }

    public KuendigungAnlagenTypeBuilder withKuendigungsschreiben(AnlageType kuendigungsschreiben) {
        this.kuendigungsschreiben = kuendigungsschreiben;
        return this;
    }

    public KuendigungAnlagenTypeBuilder withSonstige(List<AnlageMitTypType> sonstige) {
        this.sonstige = sonstige;
        return this;
    }

    public KuendigungAnlagenTypeBuilder addSonstige(AnlageMitTypType sonstige) {
        if (this.sonstige == null) {
            this.sonstige = new ArrayList<>();
        }
        this.sonstige.add(sonstige);
        return this;
    }

}
