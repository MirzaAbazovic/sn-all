/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.03.2016
 */
package de.mnet.hurrican.webservice.ngn.model;

import java.util.*;

public class PortierungRequest {
    private List<Long> billingOrderNumbers;

    public PortierungRequest(List<Long> billingOrderNumbers) {
        this.billingOrderNumbers = billingOrderNumbers;
    }

    public List<Long> getBillingOrderNumbers() {
        return billingOrderNumbers;
    }
}

