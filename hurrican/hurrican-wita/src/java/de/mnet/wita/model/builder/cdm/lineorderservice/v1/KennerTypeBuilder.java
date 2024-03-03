/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragskennerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KennerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProjektIDType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class KennerTypeBuilder implements LineOrderTypeBuilder<KennerType> {

    private ProjektIDType projektID;
    private AuftragskennerType auftragskenner;

    @Override
    public KennerType build() {
        KennerType kennerType = new KennerType();
        kennerType.setAuftragskenner(auftragskenner);
        kennerType.setProjektID(projektID);
        return kennerType;
    }

    public KennerTypeBuilder withProjektID(ProjektIDType projektID) {
        this.projektID = projektID;
        return this;
    }

    public KennerTypeBuilder withAuftragskenner(AuftragskennerType auftragskenner) {
        this.auftragskenner = auftragskenner;
        return this;
    }

}
