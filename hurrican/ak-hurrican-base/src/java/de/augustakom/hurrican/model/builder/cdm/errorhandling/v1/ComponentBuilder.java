/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 15.10.13 
 */
package de.augustakom.hurrican.model.builder.cdm.errorhandling.v1;

import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

public class ComponentBuilder extends V1ErrorHandlingBuilder<HandleError.Component> {

    public ComponentBuilder() {
        this.objectType = OBJECT_FACTORY.createHandleErrorComponent();
    }

    public ComponentBuilder withHost(String host) {
        objectType.setHost(host);
        return this;
    }

    public ComponentBuilder withName(String name) {
        objectType.setName(name);
        return this;
    }

    public ComponentBuilder withOperation(String operation) {
        objectType.setOperation(operation);
        return this;
    }

    public ComponentBuilder withProcessId(String processId) {
        objectType.setProcessId(processId);
        return this;
    }

    public ComponentBuilder withProcessName(String processName) {
        objectType.setProcessName(processName);
        return this;
    }

    public ComponentBuilder withService(String service) {
        objectType.setService(service);
        return this;
    }

}
