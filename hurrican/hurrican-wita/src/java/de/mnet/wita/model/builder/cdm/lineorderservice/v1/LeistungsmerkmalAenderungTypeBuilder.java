/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionLeistungsmerkmalAenderungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsmerkmalAenderungAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsmerkmalAenderungTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsmerkmalAenderungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class LeistungsmerkmalAenderungTypeBuilder implements LineOrderTypeBuilder<LeistungsmerkmalAenderungType> {

    private String bktoFaktura;
    private String vertragsnummer;
    private LeistungsmerkmalAenderungAnlagenType anlagen;
    private AnsprechpartnerType ansprechpartner;
    private LeistungsmerkmalAenderungTermineType termine;
    private List<AuftragspositionLeistungsmerkmalAenderungType> auftragsposition;

    @Override
    public LeistungsmerkmalAenderungType build() {
        LeistungsmerkmalAenderungType leistungsmerkmalAenderungType = new LeistungsmerkmalAenderungType();
        leistungsmerkmalAenderungType.setBKTOFaktura(bktoFaktura);
        leistungsmerkmalAenderungType.setAnsprechpartner(ansprechpartner);
        leistungsmerkmalAenderungType.setVertragsnummer(vertragsnummer);
        leistungsmerkmalAenderungType.setAnlagen(anlagen);
        leistungsmerkmalAenderungType.setTermine(termine);
        if (auftragsposition != null) {
            leistungsmerkmalAenderungType.getAuftragsposition().addAll(auftragsposition);
        }
        return null;
    }

    public LeistungsmerkmalAenderungTypeBuilder withBktoFaktura(String bktoFaktura) {
        this.bktoFaktura = bktoFaktura;
        return this;
    }

    public LeistungsmerkmalAenderungTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public LeistungsmerkmalAenderungTypeBuilder withAnlagen(LeistungsmerkmalAenderungAnlagenType anlagen) {
        this.anlagen = anlagen;
        return this;
    }

    public LeistungsmerkmalAenderungTypeBuilder withAnsprechpartner(AnsprechpartnerType ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
        return this;
    }

    public LeistungsmerkmalAenderungTypeBuilder withTermine(LeistungsmerkmalAenderungTermineType termine) {
        this.termine = termine;
        return this;
    }

    public LeistungsmerkmalAenderungTypeBuilder withAuftragsposition(List<AuftragspositionLeistungsmerkmalAenderungType> auftragsposition) {
        this.auftragsposition = auftragsposition;
        return this;
    }

    public LeistungsmerkmalAenderungTypeBuilder addAuftragsposition(AuftragspositionLeistungsmerkmalAenderungType auftragsposition) {
        if (this.auftragsposition == null) {
            this.auftragsposition = new ArrayList<>();
        }
        this.auftragsposition.add(auftragsposition);
        return this;
    }

}
