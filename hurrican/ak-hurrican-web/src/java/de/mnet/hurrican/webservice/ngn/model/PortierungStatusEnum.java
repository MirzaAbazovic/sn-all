/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.03.2016
 */
package de.mnet.hurrican.webservice.ngn.model;

public enum PortierungStatusEnum {
    SUCCESSFUL,
    ERROR;

    public String value() {
        return name();
    }

    public static PortierungStatusEnum fromValue(String v) {
        return valueOf(v);
    }

}
