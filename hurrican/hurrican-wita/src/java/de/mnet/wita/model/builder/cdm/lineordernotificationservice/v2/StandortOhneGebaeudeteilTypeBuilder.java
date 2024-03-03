
/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.FirmaType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.OrtType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.PersonType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.StandortOhneGebaeudeteilType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.StrasseType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class StandortOhneGebaeudeteilTypeBuilder implements LineOrderNotificationTypeBuilder<StandortOhneGebaeudeteilType> {

    private FirmaType firma;
    private PersonType person;
    private StrasseType strasse;
    private String land;
    private String postleitzahl;
    private OrtType ort;

    @Override
    public StandortOhneGebaeudeteilType build() {
        return enrich(new StandortOhneGebaeudeteilType());
    }

    protected <ST extends StandortOhneGebaeudeteilType> ST enrich(ST standortType) {
        standortType.setFirma(firma);
        standortType.setPerson(person);
        standortType.setLand(land);
        standortType.setOrt(ort);
        standortType.setPostleitzahl(postleitzahl);
        standortType.setStrasse(strasse);
        return standortType;
    }

    public StandortOhneGebaeudeteilTypeBuilder withFirma(FirmaType firma) {
        this.firma = firma;
        return this;
    }

    public StandortOhneGebaeudeteilTypeBuilder withPerson(PersonType person) {
        this.person = person;
        return this;
    }

    public StandortOhneGebaeudeteilTypeBuilder withStrasse(StrasseType strasse) {
        this.strasse = strasse;
        return this;
    }

    public StandortOhneGebaeudeteilTypeBuilder withLand(String land) {
        this.land = land;
        return this;
    }

    public StandortOhneGebaeudeteilTypeBuilder withPostleitzahl(String postleitzahl) {
        this.postleitzahl = postleitzahl;
        return this;
    }

    public StandortOhneGebaeudeteilTypeBuilder withOrt(OrtType ort) {
        this.ort = ort;
        return this;
    }

}