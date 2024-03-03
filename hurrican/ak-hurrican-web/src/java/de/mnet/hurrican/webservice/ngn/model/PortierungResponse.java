/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.03.2016
 */
package de.mnet.hurrican.webservice.ngn.model;

import java.util.*;

public class PortierungResponse {
    private PortierungResult portierungResult;

    public PortierungResult getPortierungResult() {
        return portierungResult;
    }

    public PortierungResponse(PortierungResult portierungResult) {
        this.portierungResult = portierungResult;
    }

    public Map<Long, PortierungStatus> getResponses() {
        return portierungResult.getPortierungResults();
    }
}

