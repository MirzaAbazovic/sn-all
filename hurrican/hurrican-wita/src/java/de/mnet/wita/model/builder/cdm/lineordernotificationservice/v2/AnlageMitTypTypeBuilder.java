/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnlageMitTypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnlagentypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.DokumenttypType;

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

    public AnlageMitTypTypeBuilder withDateiname(String value) {
        return (AnlageMitTypTypeBuilder) super.withDateiname(value);
    }

    public AnlageMitTypTypeBuilder withDateityp(DokumenttypType value) {
        return (AnlageMitTypTypeBuilder) super.withDateityp(value);
    }

    public AnlageMitTypTypeBuilder withBeschreibung(String value) {
        return (AnlageMitTypTypeBuilder) super.withBeschreibung(value);
    }

    public AnlageMitTypTypeBuilder withInhalt(byte[] value) {
        return (AnlageMitTypTypeBuilder) super.withInhalt(value);
    }

}
