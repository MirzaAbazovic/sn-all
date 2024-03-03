/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OnkzRufNrType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortierungRufnummernMeldungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class PortierungRufnummernMeldungTypeBuilder implements LineOrderTypeBuilder<PortierungRufnummernMeldungType> {

    private List<OnkzRufNrType> zuPortierendeOnkzRnr;

    @Override
    public PortierungRufnummernMeldungType build() {
        PortierungRufnummernMeldungType portierungRufnummernMeldungType = new PortierungRufnummernMeldungType();
        if (zuPortierendeOnkzRnr != null) {
            portierungRufnummernMeldungType.getZuPortierendeOnkzRnr().addAll(zuPortierendeOnkzRnr);
        }
        return portierungRufnummernMeldungType;
    }

    public PortierungRufnummernMeldungTypeBuilder withZuPortierendeOnkzRnr(List<OnkzRufNrType> zuPortierendeOnkzRnr) {
        this.zuPortierendeOnkzRnr = zuPortierendeOnkzRnr;
        return this;
    }

    public PortierungRufnummernMeldungTypeBuilder addZuPortierendeOnkzRnr(OnkzRufNrType zuPortierendeOnkzRnr) {
        if (this.zuPortierendeOnkzRnr == null) {
            this.zuPortierendeOnkzRnr = new ArrayList<>();
        }
        this.zuPortierendeOnkzRnr.add(zuPortierendeOnkzRnr);
        return this;
    }

}
