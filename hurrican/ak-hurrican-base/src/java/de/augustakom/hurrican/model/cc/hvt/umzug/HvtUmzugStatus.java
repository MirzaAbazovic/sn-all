/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2015
 */
package de.augustakom.hurrican.model.cc.hvt.umzug;

/**
 * Enum definiert den Status des jeweiligen KVZ innerhalb des HVT Umzugs
 */
public enum HvtUmzugStatus {

    OFFEN("offen"),
    PLANUNG_VOLLSTAENDIG("Planung vollständig"),
    AUSGEFUEHRT("ausgefuehrt"),
    BEENDET("beendet"),
    DEAKTIVIERT("deaktiviert");

    private final String displayName;

    HvtUmzugStatus(final String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {  //getter fuer AKReflectionListCellRenderer
        return displayName;
    }
}
