/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.14
 */
package de.mnet.hurrican.atlas.simulator.wita;

/**
 *
 */
public enum WitaLineOrderServiceVersion {
    V1("http://www.mnet.de/esb/cdm/SupplierPartner/LineOrderService/v1", "1", "/LineOrderService/createOrder", "/LineOrderNotificationService/notifyUpdateOrder");

    private String namespace;
    private String majorVersion;
    private final String anfrageSoapAction;
    private final String meldungSoapAction;

    WitaLineOrderServiceVersion(String namespace, String majorVersion, String anfrageSoapAction, String meldungSoapAction) {
        this.namespace = namespace;
        this.majorVersion = majorVersion;
        this.anfrageSoapAction = anfrageSoapAction;
        this.meldungSoapAction = meldungSoapAction;
    }

    /**
     * Gets cdm version enumeration value from namespace value.
     *
     * @param namespace
     * @return
     */
    public static WitaLineOrderServiceVersion fromNamespace(String namespace) {
        for (WitaLineOrderServiceVersion version : values()) {
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

    public String getAnfrageSoapAction() {
        return anfrageSoapAction;
    }

    public String getMeldungSoapAction() {
        return meldungSoapAction;
    }
}
