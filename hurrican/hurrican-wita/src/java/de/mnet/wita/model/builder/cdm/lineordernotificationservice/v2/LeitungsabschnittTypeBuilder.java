/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.LeitungsabschnittType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class LeitungsabschnittTypeBuilder implements LineOrderNotificationTypeBuilder<LeitungsabschnittType> {

    private int lfdNrLeitungsabschnitt;
    private String leitungslaenge;
    private String leitungsdurchmesser;

    @Override
    public LeitungsabschnittType build() {
        LeitungsabschnittType leitungsabschnittType = new LeitungsabschnittType();
        leitungsabschnittType.setLeitungsdurchmesser(leitungsdurchmesser);
        leitungsabschnittType.setLeitungslaenge(leitungslaenge);
        leitungsabschnittType.setLfdNrLeitungsabschnitt(lfdNrLeitungsabschnitt);
        return leitungsabschnittType;
    }

    public LeitungsabschnittTypeBuilder withLfdNrLeitungsabschnitt(int lfdNrLeitungsabschnitt) {
        this.lfdNrLeitungsabschnitt = lfdNrLeitungsabschnitt;
        return this;
    }

    public LeitungsabschnittTypeBuilder withLeitungslaenge(String leitungslaenge) {
        this.leitungslaenge = leitungslaenge;
        return this;
    }

    public LeitungsabschnittTypeBuilder withLeitungsdurchmesser(String leitungsdurchmesser) {
        this.leitungsdurchmesser = leitungsdurchmesser;
        return this;
    }

}
