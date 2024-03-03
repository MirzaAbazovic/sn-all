/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionBereitstellungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.BereitstellungAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.BereitstellungTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.BereitstellungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.BoolDecisionType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class BereitstellungTypeBuilder implements LineOrderTypeBuilder<BereitstellungType> {

    private String bktoFaktura;
    private BereitstellungAnlagenType anlagen;
    private AnsprechpartnerType ansprechpartner;
    private BereitstellungTermineType termine;
    private BoolDecisionType teilausfuehrung;
    private List<AuftragspositionBereitstellungType> auftragsposition;

    @Override
    public BereitstellungType build() {
        BereitstellungType bereitstellungType = new BereitstellungType();
        bereitstellungType.setAnlagen(anlagen);
        bereitstellungType.setAnsprechpartner(ansprechpartner);
        bereitstellungType.setBKTOFaktura(bktoFaktura);
        bereitstellungType.setTeilausfuehrung(teilausfuehrung);
        bereitstellungType.setTermine(termine);
        if (auftragsposition != null) {
             bereitstellungType.getAuftragsposition().addAll(auftragsposition);
        }
        return bereitstellungType;
    }

    public BereitstellungTypeBuilder withBktoFaktura(String bktoFaktura) {
        this.bktoFaktura = bktoFaktura;
        return this;
    }

    public BereitstellungTypeBuilder withAnlagen(BereitstellungAnlagenType anlagen) {
        this.anlagen = anlagen;
        return this;
    }

    public BereitstellungTypeBuilder withAnsprechpartner(AnsprechpartnerType ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
        return this;
    }

    public BereitstellungTypeBuilder withTermine(BereitstellungTermineType termine) {
        this.termine = termine;
        return this;
    }

    public BereitstellungTypeBuilder withTeilausfuehrung(BoolDecisionType teilausfuehrung) {
        this.teilausfuehrung = teilausfuehrung;
        return this;
    }

    public BereitstellungTypeBuilder withAuftragsposition(List<AuftragspositionBereitstellungType> auftragsposition) {
        this.auftragsposition = auftragsposition;
        return this;
    }

    public BereitstellungTypeBuilder addAuftragsposition(AuftragspositionBereitstellungType auftragsposition) {
        if (this.auftragsposition == null) {
            this.auftragsposition = new ArrayList<>();
        }
        this.auftragsposition.add(auftragsposition);
        return this;
    }

}
