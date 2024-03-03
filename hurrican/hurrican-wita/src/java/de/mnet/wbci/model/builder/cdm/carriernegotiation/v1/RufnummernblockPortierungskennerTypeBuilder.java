/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockPortierungskennerType;

/**
 *
 */
public class RufnummernblockPortierungskennerTypeBuilder extends
        AbstractRufnummernblockTypeBuilder<RufnummernblockPortierungskennerType> {

    public RufnummernblockPortierungskennerTypeBuilder() {
        objectType = OBJECT_FACTORY.createRufnummernblockPortierungskennerType();
    }

    public RufnummernblockPortierungskennerTypeBuilder withPortierungskennungPKIabg(String portierungskennungPKIabg) {
        objectType.setPortierungskennungPKIabg(portierungskennungPKIabg);
        return this;
    }
}
