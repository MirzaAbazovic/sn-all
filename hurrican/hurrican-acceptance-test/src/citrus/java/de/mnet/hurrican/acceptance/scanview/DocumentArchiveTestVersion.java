/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.12.2014
 */
package de.mnet.hurrican.acceptance.scanview;

import de.mnet.common.webservice.ServiceModelVersison;

/**
 *
 */
public enum DocumentArchiveTestVersion implements ServiceModelVersison<DocumentArchiveTestVersion> {

    V1("1"),
    UNKNOWN("UNKNOWN");

    private String version;

    DocumentArchiveTestVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isGreaterOrEqualThan(DocumentArchiveTestVersion versionToCheck) {
        int toCheckVersion = Integer.parseInt(versionToCheck.getVersion());
        int thisVersion = Integer.parseInt(getVersion());
        return (thisVersion >= toCheckVersion);
    }

    @Override
    public String getName() {
        return this.name();
    }

}
