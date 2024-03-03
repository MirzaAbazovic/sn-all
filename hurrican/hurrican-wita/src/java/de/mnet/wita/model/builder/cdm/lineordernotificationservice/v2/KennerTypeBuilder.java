/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AuftragskennerType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.KennerType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.ProjektIDType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class KennerTypeBuilder implements LineOrderNotificationTypeBuilder<KennerType> {

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
