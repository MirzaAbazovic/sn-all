/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AuftragskennerType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class AuftragskennerTypeBuilder implements LineOrderNotificationTypeBuilder<AuftragskennerType> {

    private String auftragsklammer;
    private int anzahlAuftraege;

    @Override
    public AuftragskennerType build() {
        AuftragskennerType auftragskenner = new AuftragskennerType();
        auftragskenner.setAnzahlAuftraege(anzahlAuftraege);
        auftragskenner.setAuftragsklammer(auftragsklammer);
        return auftragskenner;
    }

    public AuftragskennerTypeBuilder withAuftragsklammer(String auftragsklammer) {
        this.auftragsklammer = auftragsklammer;
        return this;
    }

    public AuftragskennerTypeBuilder withAnzahlAuftraege(int anzahlAuftraege) {
        this.anzahlAuftraege = anzahlAuftraege;
        return this;
    }

}
