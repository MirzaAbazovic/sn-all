/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 15.10.13 
 */
package de.augustakom.hurrican.model.builder.cdm.errorhandling.v1;

import de.augustakom.hurrican.model.builder.cdm.errorhandling.ErrorHandlingTypeBuilder;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.ObjectFactory;

public abstract class V1ErrorHandlingBuilder<T> implements ErrorHandlingTypeBuilder<T> {
    protected static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    protected T objectType;

    @Override
    public T build() {
        return objectType;
    }
}
