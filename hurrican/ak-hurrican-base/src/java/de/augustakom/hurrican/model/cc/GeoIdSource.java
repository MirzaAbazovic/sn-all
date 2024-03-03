package de.augustakom.hurrican.model.cc;

public enum GeoIdSource {
    HVT("Endstelle"),
    GEO_ID("GeoId"),
    OHNE("Keine");

    GeoIdSource(final String displayName) {
        this.displayName = displayName;
    }

    public final String displayName;

    public String getDisplayName() {  //getter fuer AKReflectionListCellRenderer
        return displayName;
    }
}
