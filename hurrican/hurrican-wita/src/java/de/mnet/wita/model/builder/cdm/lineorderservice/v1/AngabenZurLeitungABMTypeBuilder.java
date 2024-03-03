/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AngabenZurLeitungABMType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeitungsabschnittType;

/**
 *
 */
public class AngabenZurLeitungABMTypeBuilder extends AngabenZurLeitungTypeBuilder {

    private List<LeitungsabschnittType> leitungsabschnitt;
    private String maxBruttoBitrate;

    @Override
    public AngabenZurLeitungABMType build() {
        AngabenZurLeitungABMType angabenZurLeitungABMType = enrich(new AngabenZurLeitungABMType());
        angabenZurLeitungABMType.setMaxBruttoBitrate(maxBruttoBitrate);
        if (leitungsabschnitt != null) {
            angabenZurLeitungABMType.getLeitungsabschnitt().addAll(leitungsabschnitt);
        }
        return angabenZurLeitungABMType;
    }

    public AngabenZurLeitungABMTypeBuilder withMaxBruttoBitrate(String value) {
        this.maxBruttoBitrate = value;
        return this;
    }

    public AngabenZurLeitungABMTypeBuilder withLeitungsabschnittType(List<LeitungsabschnittType> leitungsabschnitte) {
        this.leitungsabschnitt = leitungsabschnitte;
        return this;
    }

    public AngabenZurLeitungABMTypeBuilder addLeitungsabschnittType(LeitungsabschnittType leitungsabschnitt) {
        if (this.leitungsabschnitt == null) {
            this.leitungsabschnitt = new ArrayList<>();
        }
        this.leitungsabschnitt.add(leitungsabschnitt);
        return this;
    }

}
