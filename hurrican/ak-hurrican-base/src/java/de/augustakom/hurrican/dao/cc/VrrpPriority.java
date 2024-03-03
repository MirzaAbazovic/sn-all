/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.06.2015
 */
package de.augustakom.hurrican.dao.cc;

/**
 * Priorität fuer das VRRP-Protokoll
 *
 */
public enum VrrpPriority {

    PRIMARY("primary"),
    SECONDARY("secondary");

    private final String displayText;

    VrrpPriority(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

}
