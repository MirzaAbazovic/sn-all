
/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.GebaeudeteilType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.OrtType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.StandortKorrekturType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.StrasseType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class StandortKorrekturTypeBuilder implements LineOrderNotificationTypeBuilder<StandortKorrekturType> {

    private StrasseType strasse;
    private GebaeudeteilType gebaeudeteil;
    private String land;
    private String postleitzahl;
    private OrtType ort;

    @Override
    public StandortKorrekturType build() {
        StandortKorrekturType standortType = new StandortKorrekturType();
        standortType.setLand(land);
        standortType.setOrt(ort);
        standortType.setPostleitzahl(postleitzahl);
        standortType.setStrasse(strasse);
        standortType.setGebaeudeteil(gebaeudeteil);
        return standortType;
    }

    public StandortKorrekturTypeBuilder withStrasse(StrasseType strasse) {
        this.strasse = strasse;
        return this;
    }

    public StandortKorrekturTypeBuilder withGebaeudeteil(GebaeudeteilType gebaeudeteil) {
        this.gebaeudeteil = gebaeudeteil;
        return this;
    }

    public StandortKorrekturTypeBuilder withLand(String land) {
        this.land = land;
        return this;
    }

    public StandortKorrekturTypeBuilder withPostleitzahl(String postleitzahl) {
        this.postleitzahl = postleitzahl;
        return this;
    }

    public StandortKorrekturTypeBuilder withOrt(OrtType ort) {
        this.ort = ort;
        return this;
    }

}
