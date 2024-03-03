/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionPortwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortwechselAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortwechselTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortwechselType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class PortwechselTypeBuilder implements LineOrderTypeBuilder<PortwechselType> {

    private String bktoFaktura;
    private String vertragsnummer;
    private PortwechselAnlagenType anlagen;
    private AnsprechpartnerType ansprechpartner;
    private PortwechselTermineType termine;
    private List<AuftragspositionPortwechselType> auftragsposition;

    @Override
    public PortwechselType build() {
        PortwechselType portwechselType = new PortwechselType();
        portwechselType.setBKTOFaktura(bktoFaktura);
        portwechselType.setAnsprechpartner(ansprechpartner);
        portwechselType.setVertragsnummer(vertragsnummer);
        portwechselType.setAnlagen(anlagen);
        portwechselType.setTermine(termine);
        if (auftragsposition != null) {
            portwechselType.getAuftragsposition().addAll(auftragsposition);
        }
        return null;
    }

    public PortwechselTypeBuilder withBktoFaktura(String bktoFaktura) {
        this.bktoFaktura = bktoFaktura;
        return this;
    }

    public PortwechselTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public PortwechselTypeBuilder withAnlagen(PortwechselAnlagenType anlagen) {
        this.anlagen = anlagen;
        return this;
    }

    public PortwechselTypeBuilder withAnsprechpartner(AnsprechpartnerType ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
        return this;
    }

    public PortwechselTypeBuilder withTermine(PortwechselTermineType termine) {
        this.termine = termine;
        return this;
    }

    public PortwechselTypeBuilder withAuftragsposition(List<AuftragspositionPortwechselType> auftragsposition) {
        this.auftragsposition = auftragsposition;
        return this;
    }

    public PortwechselTypeBuilder addAuftragsposition(AuftragspositionPortwechselType auftragsposition) {
        if (this.auftragsposition == null) {
            this.auftragsposition = new ArrayList<>();
        }
        this.auftragsposition.add(auftragsposition);
        return this;
    }

}
