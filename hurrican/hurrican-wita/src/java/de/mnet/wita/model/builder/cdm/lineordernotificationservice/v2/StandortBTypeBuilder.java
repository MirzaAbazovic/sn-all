
/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.GebaeudeteilType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.OnkzAnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.OrtType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.StandortBType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.StrasseType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class StandortBTypeBuilder implements LineOrderNotificationTypeBuilder<StandortBType> {

    private StrasseType strasse;
    private GebaeudeteilType gebaeudeteil;
    private String land;
    private String postleitzahl;
    private OrtType ort;
    private OnkzAnschlussType onkzAnschlussType;

    @Override
    public StandortBType build() {
        StandortBType standortType = new StandortBType();
        standortType.setLand(land);
        standortType.setOrt(ort);
        standortType.setPostleitzahl(postleitzahl);
        standortType.setStrasse(strasse);
        standortType.setLageKollokationsraum(onkzAnschlussType);
        return standortType;
    }

    public StandortBTypeBuilder withStrasse(StrasseType strasse) {
        this.strasse = strasse;
        return this;
    }

    public StandortBTypeBuilder withGebaeudeteil(GebaeudeteilType gebaeudeteil) {
        this.gebaeudeteil = gebaeudeteil;
        return this;
    }

    public StandortBTypeBuilder withLand(String land) {
        this.land = land;
        return this;
    }

    public StandortBTypeBuilder withPostleitzahl(String postleitzahl) {
        this.postleitzahl = postleitzahl;
        return this;
    }

    public StandortBTypeBuilder withOrt(OrtType ort) {
        this.ort = ort;
        return this;
    }

}
