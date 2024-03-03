/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionLeistungsaenderungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsaenderungAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsaenderungTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsaenderungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class LeistungsaenderungTypeBuilder implements LineOrderTypeBuilder<LeistungsaenderungType> {

    private String bktoFaktura;
    private String vertragsnummer;
    private LeistungsaenderungAnlagenType anlagen;
    private AnsprechpartnerType ansprechpartner;
    private LeistungsaenderungTermineType termine;
    private List<AuftragspositionLeistungsaenderungType> auftragsposition;

    @Override
    public LeistungsaenderungType build() {
        LeistungsaenderungType leistungsaenderungType = new LeistungsaenderungType();
        leistungsaenderungType.setBKTOFaktura(bktoFaktura);
        leistungsaenderungType.setAnsprechpartner(ansprechpartner);
        leistungsaenderungType.setVertragsnummer(vertragsnummer);
        leistungsaenderungType.setAnlagen(anlagen);
        leistungsaenderungType.setTermine(termine);
        if (auftragsposition != null) {
            leistungsaenderungType.getAuftragsposition().addAll(auftragsposition);
        }
        return null;
    }

    public LeistungsaenderungTypeBuilder withBktoFaktura(String bktoFaktura) {
        this.bktoFaktura = bktoFaktura;
        return this;
    }

    public LeistungsaenderungTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public LeistungsaenderungTypeBuilder withAnlagen(LeistungsaenderungAnlagenType anlagen) {
        this.anlagen = anlagen;
        return this;
    }

    public LeistungsaenderungTypeBuilder withAnsprechpartner(AnsprechpartnerType ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
        return this;
    }

    public LeistungsaenderungTypeBuilder withTermine(LeistungsaenderungTermineType termine) {
        this.termine = termine;
        return this;
    }

    public LeistungsaenderungTypeBuilder withAuftragsposition(List<AuftragspositionLeistungsaenderungType> auftragsposition) {
        this.auftragsposition = auftragsposition;
        return this;
    }

    public LeistungsaenderungTypeBuilder addAuftragsposition(AuftragspositionLeistungsaenderungType auftragsposition) {
        if (this.auftragsposition == null) {
            this.auftragsposition = new ArrayList<>();
        }
        this.auftragsposition.add(auftragsposition);
        return this;
    }

}
