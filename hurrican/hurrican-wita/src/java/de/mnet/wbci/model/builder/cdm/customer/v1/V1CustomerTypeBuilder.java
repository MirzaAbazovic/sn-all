/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.wbci.model.builder.cdm.customer.v1;

import de.mnet.esb.cdm.customer.customerservice.v1.ObjectFactory;
import de.mnet.wbci.model.builder.cdm.customer.CustomerTypeBuilder;

public abstract class V1CustomerTypeBuilder<T> implements CustomerTypeBuilder<T> {
    protected static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    protected T objectType;

    @Override
    public T build() {
        return objectType;
    }
}
