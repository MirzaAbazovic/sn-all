/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.10.13
 */
package de.augustakom.hurrican.model.builder.cdm.errorhandling.v1;

import java.util.*;

import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

public class HandleErrorBuilder extends V1ErrorHandlingBuilder<HandleError> {

    public HandleErrorBuilder() {
        this.objectType = OBJECT_FACTORY.createHandleError();
    }

    public HandleErrorBuilder withComponent(HandleError.Component component) {
        objectType.setComponent(component);
        return this;
    }

    public HandleErrorBuilder withError(HandleError.Error error) {
        objectType.setError(error);
        return this;
    }

    public HandleErrorBuilder withMessage(HandleError.Message message) {
        objectType.setMessage(message);
        return this;
    }

    public HandleErrorBuilder withTrackingId(String trackingId) {
        objectType.setTrackingId(trackingId);
        return this;
    }

    public HandleErrorBuilder withBusinessKeys(List<HandleError.BusinessKey> businessKeys) {
        objectType.getBusinessKey().clear();
        objectType.getBusinessKey().addAll(businessKeys);
        return this;
    }

    public HandleErrorBuilder addBusinessKey(HandleError.BusinessKey businessKey) {
        objectType.getBusinessKey().add(businessKey);
        return this;
    }
}
