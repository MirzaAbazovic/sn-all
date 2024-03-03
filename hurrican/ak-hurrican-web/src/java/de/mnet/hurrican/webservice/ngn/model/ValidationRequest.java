/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.03.2016
 */
package de.mnet.hurrican.webservice.ngn.model;

public class ValidationRequest {
    private long billingOrderNumber;

    public ValidationRequest(long billingOrderNumber) {
        this.billingOrderNumber = billingOrderNumber;
    }

    public long getBillingOrderNumber() {
        return billingOrderNumber;
    }
}

