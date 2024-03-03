/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnlageMitTypType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnlagentypType;

/**
 *
 */
public class AnlageMitTypTypeBuilder extends AnlageTypeBuilder {

    private AnlagentypType anlagentyp;

    @Override
    public AnlageMitTypType build() {
        AnlageMitTypType anlageType = new AnlageMitTypType();
        anlageType.setAnlagentyp(anlagentyp);
        return enrich(anlageType);
    }

    public AnlageMitTypTypeBuilder withAnlagentyp(AnlagentypType value) {
        this.anlagentyp = value;
        return this;
    }

}
