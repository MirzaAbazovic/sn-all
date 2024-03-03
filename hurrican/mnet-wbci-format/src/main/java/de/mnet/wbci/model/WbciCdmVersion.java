package de.mnet.wbci.model;

import de.mnet.common.webservice.ServiceModelVersison;

/**
 * represents the versions of the atlas cdm model, see {@link de.mnet.esb.cdm.supplierpartner.carriernegotiationservice}
 */
public enum WbciCdmVersion implements ServiceModelVersison<WbciCdmVersion> {

    V1("1"),
    UNKNOWN("UNKNOWN");

    private String version;

    WbciCdmVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    /**
     * Returns the version of atlas-carrierNegotiationService, which is currently used. This is at the moment V1. After
     * an updated of the CDM, the method must be adjusted!
     */
    public static WbciCdmVersion getDefault() {
        return V1;
    }

    /**
     * Returns the wbci version enum for the provided string version.
     */
    public static WbciCdmVersion getCdmVersion(String version) {
        for (WbciCdmVersion wbciCdmVersion : WbciCdmVersion.values()) {
            if (wbciCdmVersion.getVersion().equals(version)) {
                return wbciCdmVersion;
            }
        }
        return UNKNOWN;
    }

    @Override
    public boolean isGreaterOrEqualThan(WbciCdmVersion toCheck) {
        int toCheckVersion = Integer.parseInt(toCheck.getVersion());
        int thisVersion = Integer.parseInt(getVersion());
        return (thisVersion >= toCheckVersion);
    }

    @Override
    public String getName() {
        return this.name();
    }

}
