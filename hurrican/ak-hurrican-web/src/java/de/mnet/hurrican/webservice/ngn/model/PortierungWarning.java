/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2016
 */
package de.mnet.hurrican.webservice.ngn.model;

/**
 */
public class PortierungWarning {
    private final String message;

    public PortierungWarning(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

