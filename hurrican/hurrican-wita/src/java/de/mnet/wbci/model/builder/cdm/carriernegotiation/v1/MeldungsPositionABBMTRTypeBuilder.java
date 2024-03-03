/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMTRType;

/**
 *
 */
public class MeldungsPositionABBMTRTypeBuilder extends AbstractMeldungsPositionTypeBuilder<MeldungsPositionABBMTRType> {

    public MeldungsPositionABBMTRTypeBuilder() {
        objectType = OBJECT_FACTORY.createMeldungsPositionABBMTRType();
    }
}
