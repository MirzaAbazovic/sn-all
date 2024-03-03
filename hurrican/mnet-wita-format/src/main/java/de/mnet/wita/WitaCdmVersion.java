package de.mnet.wita;

import de.mnet.common.webservice.ServiceModelVersison;

public enum WitaCdmVersion implements ServiceModelVersison<WitaCdmVersion> {
    V1("1"),
    V2("2"),
    UNKNOWN("UNKNOWN");

    private String version;

    WitaCdmVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean isGreaterOrEqualThan(WitaCdmVersion versionToCheck) {
        int toCheckVersion = Integer.parseInt(versionToCheck.getVersion());
        int thisVersion = Integer.parseInt(getVersion());
        return (thisVersion >= toCheckVersion);
    }

    @Override
    public String getName() {
        return this.name();
    }

    /**
     * Returns the default CDM version.
     */
    public static WitaCdmVersion getDefault() {
        return V2;
    }

    /**
     * Returns the CDM version enum for the provided string version.
     */
    public static WitaCdmVersion getCdmVersion(String version) {
        for (WitaCdmVersion witaCdmVersion : WitaCdmVersion.values()) {
            if (witaCdmVersion.getVersion().equals(version)) {
                return witaCdmVersion;
            }
        }
        return UNKNOWN;
    }
}
