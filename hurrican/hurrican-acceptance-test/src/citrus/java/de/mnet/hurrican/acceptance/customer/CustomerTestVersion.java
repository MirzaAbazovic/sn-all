/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.03.2015
 */
package de.mnet.hurrican.acceptance.customer;

import de.mnet.common.webservice.ServiceModelVersison;

/**
 * Represents the different version of the CustomerOrder interface.
 */
public enum CustomerTestVersion implements ServiceModelVersison<CustomerTestVersion> {

    V1("1"),
    UNKNOWN("UNKNOWN");

    private String version;

    private CustomerTestVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isGreaterOrEqualThan(CustomerTestVersion versionToCheck) {
        int toCheckVersion = Integer.parseInt(versionToCheck.getVersion());
        int thisVersion = Integer.parseInt(getVersion());
        return (thisVersion >= toCheckVersion);
    }

    @Override
    public String getName() {
        return this.name();
    }
}
