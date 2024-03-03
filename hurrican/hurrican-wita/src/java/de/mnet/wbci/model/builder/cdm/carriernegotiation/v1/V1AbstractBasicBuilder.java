/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ObjectFactory;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationTypeBuilder;

/**
 * Basic abstract class to implement all Builders for the CarrierNegotiationService - Objects. In the extended class you
 * have to implement a constructor, which creates a instance for the field {@link #objectType}.
 *
 *
 */
public abstract class V1AbstractBasicBuilder<T> implements CarrierNegotiationTypeBuilder<T> {
    protected ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    protected T objectType;

    @Override
    public T build() {
        return objectType;
    }
}
