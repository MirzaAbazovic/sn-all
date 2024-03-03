/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.StandortVersandType;

/**
 *
 */
public class StandortVersandTypeBuilder extends StandortOhneGebaeudeteilTypeBuilder {

    @Override
    public StandortVersandType build() {
        return enrich(new StandortVersandType());
    }

}
