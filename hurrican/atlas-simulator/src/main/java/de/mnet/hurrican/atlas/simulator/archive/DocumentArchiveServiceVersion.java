/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.14
 */
package de.mnet.hurrican.atlas.simulator.archive;

import de.mnet.hurrican.simulator.exception.SimulatorException;

/**
 *
 */
public enum DocumentArchiveServiceVersion {
    V1("http://www.mnet.de/esb/cdm/Customer/DocumentArchiveService/v1", "v1");

    private final String namespace;
    private final String majorVersion;

    DocumentArchiveServiceVersion(String namespace, String majorVersion) {
        this.namespace = namespace;
        this.majorVersion = majorVersion;
    }

    /**
     * Gets version from given namespace URI. Either workforce namespace or notification namespace matching
     * required.
     *
     * @param namespace
     * @return
     */
    public static DocumentArchiveServiceVersion fromNamespace(String namespace) {
        for (DocumentArchiveServiceVersion version : values()) {
            if (version.namespace.equals(namespace)) {
                return version;
            }
        }

        throw new SimulatorException("Unknown DocumentArchive namespace '" + namespace + "'");
    }

    @Override
    public String toString() {
        return getMajorVersion();
    }

    public String getNamespace() {
        return namespace;
    }

    public String getMajorVersion() {
        return majorVersion;
    }

}
