/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.03.2016
 */
package de.mnet.hurrican.webservice.ngn.model;

public enum ValidationStatusEnum {
    MIGRATION_POSSIBLE,
    MIGRATION_NOT_POSSIBLE;

    public String value() {
        return name();
    }

    public static ValidationStatusEnum fromValue(String v) {
        return valueOf(v);
    }

}

