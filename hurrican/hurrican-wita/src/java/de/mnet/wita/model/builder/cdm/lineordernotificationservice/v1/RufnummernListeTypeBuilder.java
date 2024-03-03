/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.OnkzRufNrType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.RufnummernListeType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class RufnummernListeTypeBuilder implements LineOrderNotificationTypeBuilder<RufnummernListeType> {

    private List<OnkzRufNrType> zuPortierendeOnkzRnr = new ArrayList<>();

    @Override
    public RufnummernListeType build() {
        return null;
    }

    public RufnummernListeTypeBuilder withZuPortierendeOnkzRnr(List<OnkzRufNrType> zuPortierendeOnkzRnr) {
        this.zuPortierendeOnkzRnr = zuPortierendeOnkzRnr;
        return this;
    }

    public RufnummernListeTypeBuilder addZuPortierendeOnkzRnr(OnkzRufNrType zuPortierendeOnkzRnr) {
        this.zuPortierendeOnkzRnr.add(zuPortierendeOnkzRnr);
        return this;
    }

}
