/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionKuendigungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KuendigungAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KuendigungTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KuendigungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class KuendigungTypeBuilder implements LineOrderTypeBuilder<KuendigungType> {

    private String bktoFaktura;
    private String vertragsnummer;
    private KuendigungAnlagenType anlagen;
    private AnsprechpartnerType ansprechpartner;
    private KuendigungTermineType termine;
    private List<AuftragspositionKuendigungType> auftragsposition;

    @Override
    public KuendigungType build() {
        KuendigungType kuendigungType = new KuendigungType();
        kuendigungType.setBKTOFaktura(bktoFaktura);
        kuendigungType.setAnsprechpartner(ansprechpartner);
        kuendigungType.setVertragsnummer(vertragsnummer);
        kuendigungType.setAnlagen(anlagen);
        kuendigungType.setTermine(termine);
        if (auftragsposition != null) {
            kuendigungType.getAuftragsposition().addAll(auftragsposition);
        }
        return null;
    }

    public KuendigungTypeBuilder withBktoFaktura(String bktoFaktura) {
        this.bktoFaktura = bktoFaktura;
        return this;
    }

    public KuendigungTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public KuendigungTypeBuilder withAnlagen(KuendigungAnlagenType anlagen) {
        this.anlagen = anlagen;
        return this;
    }

    public KuendigungTypeBuilder withAnsprechpartner(AnsprechpartnerType ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
        return this;
    }

    public KuendigungTypeBuilder withTermine(KuendigungTermineType termine) {
        this.termine = termine;
        return this;
    }

    public KuendigungTypeBuilder withAuftragsposition(List<AuftragspositionKuendigungType> auftragsposition) {
        this.auftragsposition = auftragsposition;
        return this;
    }

    public KuendigungTypeBuilder addAuftragsposition(AuftragspositionKuendigungType auftragsposition) {
        if (this.auftragsposition == null) {
            this.auftragsposition = new ArrayList<>();
        }
        this.auftragsposition.add(auftragsposition);
        return this;
    }

}
