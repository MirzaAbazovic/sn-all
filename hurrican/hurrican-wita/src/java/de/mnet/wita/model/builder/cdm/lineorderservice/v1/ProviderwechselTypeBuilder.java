/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionProviderwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProviderwechselAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProviderwechselTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProviderwechselType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class ProviderwechselTypeBuilder implements LineOrderTypeBuilder<ProviderwechselType> {

    private String bktoFaktura;
    private String vertragsnummer;
    private ProviderwechselAnlagenType anlagen;
    private AnsprechpartnerType ansprechpartner;
    private ProviderwechselTermineType termine;
    private List<AuftragspositionProviderwechselType> auftragsposition;

    @Override
    public ProviderwechselType build() {
        ProviderwechselType providerwechselType = new ProviderwechselType();
        providerwechselType.setBKTOFaktura(bktoFaktura);
        providerwechselType.setAnsprechpartner(ansprechpartner);
        providerwechselType.setVertragsnummer(vertragsnummer);
        providerwechselType.setAnlagen(anlagen);
        providerwechselType.setTermine(termine);
        if (auftragsposition != null) {
            providerwechselType.getAuftragsposition().addAll(auftragsposition);
        }
        return null;
    }

    public ProviderwechselTypeBuilder withBktoFaktura(String bktoFaktura) {
        this.bktoFaktura = bktoFaktura;
        return this;
    }

    public ProviderwechselTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public ProviderwechselTypeBuilder withAnlagen(ProviderwechselAnlagenType anlagen) {
        this.anlagen = anlagen;
        return this;
    }

    public ProviderwechselTypeBuilder withAnsprechpartner(AnsprechpartnerType ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
        return this;
    }

    public ProviderwechselTypeBuilder withTermine(ProviderwechselTermineType termine) {
        this.termine = termine;
        return this;
    }

    public ProviderwechselTypeBuilder withAuftragsposition(List<AuftragspositionProviderwechselType> auftragsposition) {
        this.auftragsposition = auftragsposition;
        return this;
    }

    public ProviderwechselTypeBuilder addAuftragsposition(AuftragspositionProviderwechselType auftragsposition) {
        if (this.auftragsposition == null) {
            this.auftragsposition = new ArrayList<>();
        }
        this.auftragsposition.add(auftragsposition);
        return this;
    }

}
