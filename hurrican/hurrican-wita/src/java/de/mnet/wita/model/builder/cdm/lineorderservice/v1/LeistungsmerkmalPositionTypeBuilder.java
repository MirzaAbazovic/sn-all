/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsmerkmalPositionType;

/**
 *
 */
public class LeistungsmerkmalPositionTypeBuilder extends AuftragspositionTypeBuilder {

    @Override
    public AuftragspositionType build() {
        return enrich(new LeistungsmerkmalPositionType());
    }

}
