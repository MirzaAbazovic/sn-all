/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoType;

/**
 *
 */
public class AbstractStornoTypeBuilder<T extends StornoType> extends AbstractAnfrageTypeBuilder<T> {

    public AbstractAnfrageTypeBuilder<T> withStornoId(String stornoId) {
        objectType.setStornoId(stornoId);
        return this;
    }

    public AbstractAnfrageTypeBuilder<T> withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        objectType.setVorabstimmungsIdRef(vorabstimmungsIdRef);
        return this;
    }
}
