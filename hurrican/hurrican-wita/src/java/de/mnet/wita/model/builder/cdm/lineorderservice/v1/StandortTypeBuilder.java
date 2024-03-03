
/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.FirmaType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.GebaeudeteilType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OrtType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PersonType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StandortType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StrasseType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class StandortTypeBuilder implements LineOrderTypeBuilder<StandortType> {

    private FirmaType firma;
    private PersonType person;
    private StrasseType strasse;
    private GebaeudeteilType gebaeudeteil;
    private String land;
    private String postleitzahl;
    private OrtType ort;

    @Override
    public StandortType build() {
        return enrich(new StandortType());
    }

    protected <ST extends StandortType> ST enrich(ST standortType) {
        standortType.setFirma(firma);
        standortType.setPerson(person);
        standortType.setLand(land);
        standortType.setOrt(ort);
        standortType.setPostleitzahl(postleitzahl);
        standortType.setStrasse(strasse);
        standortType.setGebaeudeteil(gebaeudeteil);
        return standortType;
    }

    public StandortTypeBuilder withFirma(FirmaType firma) {
        this.firma = firma;
        return this;
    }

    public StandortTypeBuilder withPerson(PersonType person) {
        this.person = person;
        return this;
    }

    public StandortTypeBuilder withStrasse(StrasseType strasse) {
        this.strasse = strasse;
        return this;
    }

    public StandortTypeBuilder withGebaeudeteil(GebaeudeteilType gebaeudeteil) {
        this.gebaeudeteil = gebaeudeteil;
        return this;
    }

    public StandortTypeBuilder withLand(String land) {
        this.land = land;
        return this;
    }

    public StandortTypeBuilder withPostleitzahl(String postleitzahl) {
        this.postleitzahl = postleitzahl;
        return this;
    }

    public StandortTypeBuilder withOrt(OrtType ort) {
        this.ort = ort;
        return this;
    }

}
