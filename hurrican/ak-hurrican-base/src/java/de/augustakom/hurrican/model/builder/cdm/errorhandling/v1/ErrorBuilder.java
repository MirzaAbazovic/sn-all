/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.10.13
 */
package de.augustakom.hurrican.model.builder.cdm.errorhandling.v1;

import java.time.*;

import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

public class ErrorBuilder extends V1ErrorHandlingBuilder<HandleError.Error> {
    public ErrorBuilder() {
        this.objectType = OBJECT_FACTORY.createHandleErrorError();
    }

    public ErrorBuilder withCode(String code) {
        objectType.setCode(code);
        return this;
    }

    public ErrorBuilder withErrorDetails(String errorDetails) {
        objectType.setErrorDetails(errorDetails);
        return this;
    }

    public ErrorBuilder withMessage(String message) {
        objectType.setMessage(message);
        return this;
    }

    public ErrorBuilder withTime(LocalDateTime time) {
        objectType.setTime(time);
        return this;
    }

}
