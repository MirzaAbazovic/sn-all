/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockType;

/**
 *
 */
public class RufnummernblockTypeBuilder extends AbstractRufnummernblockTypeBuilder<RufnummernblockType> {

    public RufnummernblockTypeBuilder() {
        objectType = OBJECT_FACTORY.createRufnummernblockType();
    }
}
