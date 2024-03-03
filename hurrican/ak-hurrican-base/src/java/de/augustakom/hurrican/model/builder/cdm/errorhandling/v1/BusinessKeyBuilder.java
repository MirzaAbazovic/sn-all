/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 15.10.13 
 */
package de.augustakom.hurrican.model.builder.cdm.errorhandling.v1;

import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

public class BusinessKeyBuilder extends V1ErrorHandlingBuilder<HandleError.BusinessKey> {

    public BusinessKeyBuilder() {
        this.objectType = OBJECT_FACTORY.createHandleErrorBusinessKey();
    }

    public BusinessKeyBuilder withKeyValue(String key, String value) {
        objectType.setKey(key);
        objectType.setValue(value);
        return this;
    }


}
