/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionAKMTRType;

/**
 *
 */
public class MeldungsPositionAKMTRTypeBuilder extends AbstractMeldungsPositionTypeBuilder<MeldungsPositionAKMTRType> {

    public MeldungsPositionAKMTRTypeBuilder() {
        objectType = OBJECT_FACTORY.createMeldungsPositionAKMTRType();
    }
}
