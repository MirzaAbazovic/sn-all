/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.14
 */
package de.mnet.hurrican.atlas.simulator.ffm;

import de.mnet.hurrican.simulator.exception.SimulatorException;

/**
 *
 */
public enum FFMVersion {

    V1("http://www.mnet.de/esb/cdm/Resource/WorkforceService/v1",
            "http://www.mnet.de/esb/cdm/Resource/WorkforceNotificationService/v1",
            "v1");

    private final String namespace;
    private final String notificationNamespace;
    private final String majorVersion;

    FFMVersion(String namespace, String notificationNamespace, String majorVersion) {
        this.namespace = namespace;
        this.notificationNamespace = notificationNamespace;
        this.majorVersion = majorVersion;
    }

    /**
     * Gets version from given namespace URI. Either workforce namespace or notification namespace matching
     * required.
     *
     * @param namespace
     * @return
     */
    public static FFMVersion fromNamespace(String namespace) {
        for (FFMVersion version : values()) {
            if (version.namespace.equals(namespace) || version.getNotificationNamespace().equals(namespace)) {
                return version;
            }
        }

        throw new SimulatorException("Unknown WBCI namespace '" + namespace + "'");
    }

    @Override
    public String toString() {
        return getMajorVersion();
    }

    public String getNamespace() {
        return namespace;
    }
    public String getNotificationNamespace() {
        return notificationNamespace;
    }

    public String getMajorVersion() {
        return majorVersion;
    }
}
