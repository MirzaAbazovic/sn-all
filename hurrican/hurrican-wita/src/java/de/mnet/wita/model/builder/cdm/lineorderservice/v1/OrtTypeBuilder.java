/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OrtType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class OrtTypeBuilder implements LineOrderTypeBuilder<OrtType> {

    protected String ortsname;
    protected String ortsteil;

    @Override
    public OrtType build() {
        OrtType ortType = new OrtType();
        ortType.setOrtsname(ortsname);
        ortType.setOrtsteil(ortsteil);
        return ortType;
    }

    public OrtTypeBuilder withOrtsname(String ortsname) {
        this.ortsname = ortsname;
        return this;
    }

    public OrtTypeBuilder withOrtsteil(String ortsteil) {
        this.ortsteil = ortsteil;
        return this;
    }

}
