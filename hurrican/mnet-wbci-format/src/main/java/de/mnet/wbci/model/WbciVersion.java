package de.mnet.wbci.model;

import java.util.*;

public enum WbciVersion {

    V2("2", "00"), UNKNOWN("UNKNOWN", "UNKNOWN", false);

    private String wbciMajorVersion;
    private String wbciMinorVersion;
    private boolean supported;

    WbciVersion(String wbciMajorVersion, String wbciMinorVersion) {
        this(wbciMajorVersion, wbciMinorVersion, true);
    }

    WbciVersion(String wbciMajorVersion, String wbciMinorVersion, boolean supported) {
        this.wbciMajorVersion = wbciMajorVersion;
        this.wbciMinorVersion = wbciMinorVersion;
        this.supported = supported;
    }

    public String getVersion() {
        return wbciMajorVersion;
    }

    public String getMinorVersion() {
        return wbciMinorVersion;
    }

    /**
     * Returns the wbci version, which is currently used. This is at the moment V4. After switching to V7 we have to
     * adjust this method!
     */
    public static WbciVersion getDefault() {
        return V2;
    }

    /**
     * Returns the wbci version enum for the provided string version.
     */
    public static WbciVersion getWbciVersion(String version) {
        for (WbciVersion wbciVersion : WbciVersion.values()) {
            if (wbciVersion.getVersion().equals(version)) {
                return wbciVersion;
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns a collection of all wbci versions, which are currently supported.
     */
    public static Collection<WbciVersion> getSupportedVersions() {
        Collection<WbciVersion> supportedVersions = new HashSet<>();
        for (WbciVersion wbciVersion : WbciVersion.values()) {
            if (wbciVersion.supported) {
                supportedVersions.add(wbciVersion);
            }
        }
        return supportedVersions;
    }

    public boolean isGreaterOrEqualThan(WbciVersion toCheck) {
        int toCheckVersion = Integer.parseInt(toCheck.getVersion());
        int thisVersion = Integer.parseInt(getVersion());
        return (thisVersion >= toCheckVersion);
    }

}
