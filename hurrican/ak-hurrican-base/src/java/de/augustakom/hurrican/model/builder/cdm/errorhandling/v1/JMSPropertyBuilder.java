/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 15.10.13 
 */
package de.augustakom.hurrican.model.builder.cdm.errorhandling.v1;

import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

public class JMSPropertyBuilder extends V1ErrorHandlingBuilder<HandleError.Message.JMSProperty> {

    public JMSPropertyBuilder() {
        this.objectType = OBJECT_FACTORY.createHandleErrorMessageJMSProperty();
    }

    public JMSPropertyBuilder withKeyValue(String key, String value) {
        objectType.setKey(key);
        objectType.setValue(value);
        return this;
    }


}
