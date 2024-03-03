/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StornierungType;

/**
 *
 */
public class StornierungTypeBuilder extends AuftragTypeBuilder {

    @Override
    public StornierungType build() {
        return enrich(new StornierungType());
    }

}
