/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionVerbundleistungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.VerbundleistungAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.VerbundleistungTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.VerbundleistungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class VerbundleistungTypeBuilder implements LineOrderTypeBuilder<VerbundleistungType> {

    private String bktoFaktura;
    private String vertragsnummer;
    private VerbundleistungAnlagenType anlagen;
    private AnsprechpartnerType ansprechpartner;
    private VerbundleistungTermineType termine;
    private List<AuftragspositionVerbundleistungType> auftragsposition;

    @Override
    public VerbundleistungType build() {
        VerbundleistungType providerwechselType = new VerbundleistungType();
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

    public VerbundleistungTypeBuilder withBktoFaktura(String bktoFaktura) {
        this.bktoFaktura = bktoFaktura;
        return this;
    }

    public VerbundleistungTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public VerbundleistungTypeBuilder withAnlagen(VerbundleistungAnlagenType anlagen) {
        this.anlagen = anlagen;
        return this;
    }

    public VerbundleistungTypeBuilder withAnsprechpartner(AnsprechpartnerType ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
        return this;
    }

    public VerbundleistungTypeBuilder withTermine(VerbundleistungTermineType termine) {
        this.termine = termine;
        return this;
    }

    public VerbundleistungTypeBuilder withAuftragsposition(List<AuftragspositionVerbundleistungType> auftragsposition) {
        this.auftragsposition = auftragsposition;
        return this;
    }

    public VerbundleistungTypeBuilder addAuftragsposition(AuftragspositionVerbundleistungType auftragsposition) {
        if (this.auftragsposition == null) {
            this.auftragsposition = new ArrayList<>();
        }
        this.auftragsposition.add(auftragsposition);
        return this;
    }

}
