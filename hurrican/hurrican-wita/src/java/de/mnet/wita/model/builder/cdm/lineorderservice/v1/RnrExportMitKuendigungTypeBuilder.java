/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionRnrExportMitKuendigungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RnrExportMitKuendigungAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RnrExportMitKuendigungTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RnrExportMitKuendigungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class RnrExportMitKuendigungTypeBuilder implements LineOrderTypeBuilder<RnrExportMitKuendigungType> {

    private String bktoFaktura;
    private RnrExportMitKuendigungAnlagenType anlagen;
    private AnsprechpartnerType ansprechpartner;
    private RnrExportMitKuendigungTermineType termine;
    private List<AuftragspositionRnrExportMitKuendigungType> auftragsposition;

    @Override
    public RnrExportMitKuendigungType build() {
        RnrExportMitKuendigungType produktgruppenwechselType = new RnrExportMitKuendigungType();
        produktgruppenwechselType.setBKTOFaktura(bktoFaktura);
        produktgruppenwechselType.setAnsprechpartner(ansprechpartner);
        produktgruppenwechselType.setAnlagen(anlagen);
        produktgruppenwechselType.setTermine(termine);
        if (auftragsposition != null) {
            produktgruppenwechselType.getAuftragsposition().addAll(auftragsposition);
        }
        return null;
    }

    public RnrExportMitKuendigungTypeBuilder withBktoFaktura(String bktoFaktura) {
        this.bktoFaktura = bktoFaktura;
        return this;
    }

    public RnrExportMitKuendigungTypeBuilder withAnlagen(RnrExportMitKuendigungAnlagenType anlagen) {
        this.anlagen = anlagen;
        return this;
    }

    public RnrExportMitKuendigungTypeBuilder withAnsprechpartner(AnsprechpartnerType ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
        return this;
    }

    public RnrExportMitKuendigungTypeBuilder withTermine(RnrExportMitKuendigungTermineType termine) {
        this.termine = termine;
        return this;
    }

    public RnrExportMitKuendigungTypeBuilder withAuftragsposition(List<AuftragspositionRnrExportMitKuendigungType> auftragsposition) {
        this.auftragsposition = auftragsposition;
        return this;
    }

    public RnrExportMitKuendigungTypeBuilder addAuftragsposition(AuftragspositionRnrExportMitKuendigungType auftragsposition) {
        if (this.auftragsposition == null) {
            this.auftragsposition = new ArrayList<>();
        }
        this.auftragsposition.add(auftragsposition);
        return this;
    }

}
