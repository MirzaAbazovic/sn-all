/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionProduktgruppenwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktgruppenwechselAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktgruppenwechselTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktgruppenwechselType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class ProduktgruppenwechselTypeBuilder implements LineOrderTypeBuilder<ProduktgruppenwechselType> {

    private String bktoFaktura;
    private String vertragsnummer;
    private ProduktgruppenwechselAnlagenType anlagen;
    private AnsprechpartnerType ansprechpartner;
    private ProduktgruppenwechselTermineType termine;
    private List<AuftragspositionProduktgruppenwechselType> auftragsposition;

    @Override
    public ProduktgruppenwechselType build() {
        ProduktgruppenwechselType produktgruppenwechselType = new ProduktgruppenwechselType();
        produktgruppenwechselType.setBKTOFaktura(bktoFaktura);
        produktgruppenwechselType.setAnsprechpartner(ansprechpartner);
        produktgruppenwechselType.setVertragsnummer(vertragsnummer);
        produktgruppenwechselType.setAnlagen(anlagen);
        produktgruppenwechselType.setTermine(termine);
        if (auftragsposition != null) {
            produktgruppenwechselType.getAuftragsposition().addAll(auftragsposition);
        }
        return null;
    }

    public ProduktgruppenwechselTypeBuilder withBktoFaktura(String bktoFaktura) {
        this.bktoFaktura = bktoFaktura;
        return this;
    }

    public ProduktgruppenwechselTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public ProduktgruppenwechselTypeBuilder withAnlagen(ProduktgruppenwechselAnlagenType anlagen) {
        this.anlagen = anlagen;
        return this;
    }

    public ProduktgruppenwechselTypeBuilder withAnsprechpartner(AnsprechpartnerType ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
        return this;
    }

    public ProduktgruppenwechselTypeBuilder withTermine(ProduktgruppenwechselTermineType termine) {
        this.termine = termine;
        return this;
    }

    public ProduktgruppenwechselTypeBuilder withAuftragsposition(List<AuftragspositionProduktgruppenwechselType> auftragsposition) {
        this.auftragsposition = auftragsposition;
        return this;
    }

    public ProduktgruppenwechselTypeBuilder addAuftragsposition(AuftragspositionProduktgruppenwechselType auftragsposition) {
        if (this.auftragsposition == null) {
            this.auftragsposition = new ArrayList<>();
        }
        this.auftragsposition.add(auftragsposition);
        return this;
    }

}
