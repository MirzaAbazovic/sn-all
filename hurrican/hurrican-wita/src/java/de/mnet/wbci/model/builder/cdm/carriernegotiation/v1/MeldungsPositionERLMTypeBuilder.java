/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionERLMType;

/**
 *
 */
public class MeldungsPositionERLMTypeBuilder extends AbstractMeldungsPositionTypeBuilder<MeldungsPositionERLMType> {

    public MeldungsPositionERLMTypeBuilder() {
        objectType = OBJECT_FACTORY.createMeldungsPositionERLMType();
    }
}
