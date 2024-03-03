/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2015
 */
package de.mnet.hurrican.acceptance.workforcedata;

import de.mnet.common.webservice.ServiceModelVersison;

/**
 *
 */
public enum WorkforceDataTestVersion implements ServiceModelVersison<WorkforceDataTestVersion> {

    V1("1"),
    UNKNOWN("UNKNOWN");

    private String version;

    WorkforceDataTestVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isGreaterOrEqualThan(WorkforceDataTestVersion versionToCheck) {
        int toCheckVersion = Integer.parseInt(versionToCheck.getVersion());
        int thisVersion = Integer.parseInt(getVersion());
        return (thisVersion >= toCheckVersion);
    }

    @Override
    public String getName() {
        return this.name();
    }

}