/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.03.2016
 */
package de.mnet.hurrican.webservice.ngn.model;

import java.util.*;

public class PortierungStatus {
    private final PortierungStatusEnum portierungStatusEnum;
    private final String message;
    private final Optional<PortierungWarnings> portierungWarnings;

    public PortierungStatus(PortierungStatusEnum portierungStatusEnum, String message, Optional<PortierungWarnings> warnings) {
        this.portierungStatusEnum = portierungStatusEnum;
        this.message = message;
        this.portierungWarnings = warnings;
    }


    public PortierungStatusEnum getPortierungStatusEnum() {
        return portierungStatusEnum;
    }

    public String getMessage() {
        return message;
    }

    public Optional<PortierungWarnings> getPortierungWarnings() {
        return portierungWarnings;
    }
}

