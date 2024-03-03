/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.03.2016
 */
package de.mnet.hurrican.webservice.ngn.model;

public class ValidationResponse {
    private Long orderNoOrig;
    private ValidationStatus status;

    public ValidationResponse(Long orderNoOrig, ValidationStatus status) {
        this.orderNoOrig = orderNoOrig;
        this.status = status;
    }

    public Long getOrderNoOrig() {
        return orderNoOrig;
    }

    public ValidationStatus getStatus() {
        return status;
    }
}

