/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerMitEmailType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerMitRolleType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class AnsprechpartnerTypeBuilder implements LineOrderTypeBuilder<AnsprechpartnerType> {

    private AnsprechpartnerMitEmailType auftragsmanagement;
    private List<AnsprechpartnerMitRolleType> ansprechpartnerMitRolle;

    @Override
    public AnsprechpartnerType build() {
        AnsprechpartnerType ansprechpartnerType = new AnsprechpartnerType();
        ansprechpartnerType.setAuftragsmanagement(auftragsmanagement);
        if (ansprechpartnerMitRolle != null) {
            ansprechpartnerType.getAnsprechpartner().addAll(ansprechpartnerMitRolle);
        }
        return ansprechpartnerType;
    }

    public AnsprechpartnerTypeBuilder withAuftragsmanagement(AnsprechpartnerMitEmailType value) {
        this.auftragsmanagement = value;
        return this;
    }

    public AnsprechpartnerTypeBuilder withAnsprechpartnern(List<AnsprechpartnerMitRolleType> ansprechpartnern) {
        this.ansprechpartnerMitRolle = ansprechpartnern;
        return this;
    }

    public AnsprechpartnerTypeBuilder addAnsprechpartnern(AnsprechpartnerMitRolleType ansprechpartner) {
        if (this.ansprechpartnerMitRolle == null) {
            this.ansprechpartnerMitRolle = new ArrayList<>();
        }
        this.ansprechpartnerMitRolle.add(ansprechpartner);
        return this;
    }

}
