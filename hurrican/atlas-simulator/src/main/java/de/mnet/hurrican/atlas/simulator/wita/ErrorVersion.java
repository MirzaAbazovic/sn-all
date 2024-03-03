/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.2014
 */
package de.mnet.hurrican.atlas.simulator.wita;

public enum ErrorVersion {
    V1("http://www.mnet.de/esb/cdm/Shared/ErrorHandlingService/v1", "1", "/ErrorHandlingService/handleError");

    private String namespace;
    private String majorVersion;
    private String soapAction;

    ErrorVersion(String namespace, String majorVersion, String soapAction) {
        this.namespace = namespace;
        this.majorVersion = majorVersion;
        this.soapAction= soapAction;
    }

    /**
     * Gets cdm version enumeration value from namespace value.
     *
     * @param namespace
     * @return
     */
    public static ErrorVersion fromNamespace(String namespace) {
        for (ErrorVersion version : values()) {
            if (version.namespace.equals(namespace)) {
                return version;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    /**
     * Gets the namespace URI associated to this enum value.
     *
     * @return
     */
    public String getNamespace() {
        return namespace;
    }

    public String getMajorVersion() {
        return majorVersion;
    }
    public String getSoapAction() {
        return soapAction;
    }

}
