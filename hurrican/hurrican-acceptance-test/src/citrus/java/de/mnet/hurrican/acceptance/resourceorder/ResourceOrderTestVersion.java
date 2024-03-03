/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 11.04.2016

 */

package de.mnet.hurrican.acceptance.resourceorder;


import de.mnet.common.webservice.ServiceModelVersison;

/**
 * Represents the different version of the ResourceOrderManagement-Notification interface.
 *
 * Created by petersde on 11.04.2016.
 */

public enum ResourceOrderTestVersion implements ServiceModelVersison<ResourceOrderTestVersion> {

    V1("1"),
    UNKNOWN("UNKNOWN");

    private String version;

    private ResourceOrderTestVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isGreaterOrEqualThan(ResourceOrderTestVersion versionToCheck) {
        int toCheckVersion = Integer.parseInt(versionToCheck.getVersion());
        int thisVersion = Integer.parseInt(getVersion());
        return (thisVersion >= toCheckVersion);
    }

    @Override
    public String getName() {
        return this.name();
    }
}
