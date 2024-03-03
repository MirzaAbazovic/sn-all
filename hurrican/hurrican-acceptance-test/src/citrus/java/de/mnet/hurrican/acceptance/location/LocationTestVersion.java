/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.03.2015
 */
package de.mnet.hurrican.acceptance.location;

import de.mnet.common.webservice.ServiceModelVersison;

/**
 * Represents the different version of the Location-Notification interface.
 */
public enum LocationTestVersion implements ServiceModelVersison<LocationTestVersion> {

    V1("1"),
    UNKNOWN("UNKNOWN");

    private String version;

    private LocationTestVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isGreaterOrEqualThan(LocationTestVersion versionToCheck) {
        int toCheckVersion = Integer.parseInt(versionToCheck.getVersion());
        int thisVersion = Integer.parseInt(getVersion());
        return (thisVersion >= toCheckVersion);
    }

    @Override
    public String getName() {
        return this.name();
    }
}
