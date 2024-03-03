/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.12.2014
 */
package de.mnet.hurrican.acceptance.ffm;

import de.mnet.common.webservice.ServiceModelVersison;

/**
 * Represents the different CDM version of the ATLAS FFM implementation
 *
 *
 */
public enum FfmTestVersion implements ServiceModelVersison<FfmTestVersion> {

    V1("1"),
    UNKNOWN("UNKNOWN");

    private String version;

    FfmTestVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isGreaterOrEqualThan(FfmTestVersion versionToCheck) {
        int toCheckVersion = Integer.parseInt(versionToCheck.getVersion());
        int thisVersion = Integer.parseInt(getVersion());
        return (thisVersion >= toCheckVersion);
    }

    @Override
    public String getName() {
        return this.name();
    }

}
