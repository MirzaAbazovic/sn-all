/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.12.2014
 */
package de.mnet.hurrican.acceptance.resource;

import de.mnet.common.webservice.ServiceModelVersison;

/**
 * Represents the different version of the Command implementation.
 *
 *
 */
public enum ResourceInventoryTestVersion implements ServiceModelVersison<ResourceInventoryTestVersion> {

    V1("1"),
    UNKNOWN("UNKNOWN");

    private String version;

    ResourceInventoryTestVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isGreaterOrEqualThan(ResourceInventoryTestVersion versionToCheck) {
        int toCheckVersion = Integer.parseInt(versionToCheck.getVersion());
        int thisVersion = Integer.parseInt(getVersion());
        return (thisVersion >= toCheckVersion);
    }

    @Override
    public String getName() {
        return this.name();
    }

}
