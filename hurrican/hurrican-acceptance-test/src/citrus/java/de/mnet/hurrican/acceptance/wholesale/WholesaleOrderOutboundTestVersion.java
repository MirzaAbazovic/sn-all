/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 10.02.2017

 */

package de.mnet.hurrican.acceptance.wholesale;


import de.mnet.common.webservice.ServiceModelVersison;

/**
 * Represents the different version
 * <p>
 * Created by wieran on 10.02.2017.
 */
public enum WholesaleOrderOutboundTestVersion implements ServiceModelVersison<WholesaleOrderOutboundTestVersion> {

    V2("2"),
    UNKNOWN("UNKNOWN");

    private String version;

    private WholesaleOrderOutboundTestVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isGreaterOrEqualThan(WholesaleOrderOutboundTestVersion versionToCheck) {
        int toCheckVersion = Integer.parseInt(versionToCheck.getVersion());
        int thisVersion = Integer.parseInt(getVersion());
        return (thisVersion >= toCheckVersion);
    }

    @Override
    public String getName() {
        return this.name();
    }
}
