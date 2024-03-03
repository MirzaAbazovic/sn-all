/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungspositionType;

/**
 *
 */
public class MeldungspositionTypeBuilder extends MeldungspositionOhneAttributeTypeBuilder {

    @Override
    public MeldungspositionType build() {
        return enrich(new MeldungspositionType());
    }

}
