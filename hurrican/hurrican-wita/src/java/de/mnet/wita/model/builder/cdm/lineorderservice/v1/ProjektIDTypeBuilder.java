/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProjektIDType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class ProjektIDTypeBuilder implements LineOrderTypeBuilder<ProjektIDType> {

    private String projektkenner;
    private String kopplungskenner;

    @Override
    public ProjektIDType build() {
        ProjektIDType projektIDType = new ProjektIDType();
        projektIDType.setKopplungskenner(kopplungskenner);
        projektIDType.setProjektkenner(projektkenner);
        return projektIDType;
    }

    public ProjektIDTypeBuilder withProjektkenner(String projektkenner) {
        this.projektkenner = projektkenner;
        return this;
    }

    public ProjektIDTypeBuilder withKopplungskenner(String kopplungskenner) {
        this.kopplungskenner = kopplungskenner;
        return this;
    }

}
