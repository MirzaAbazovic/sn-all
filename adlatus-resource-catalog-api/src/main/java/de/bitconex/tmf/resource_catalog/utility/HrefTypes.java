package de.bitconex.tmf.resource_catalog.utility;

public enum HrefTypes {
    ResourceSpecification("/resourceSpecification"),
    ResourceCandidate("/resourceCandidate"),
    ResourceCatalog("/resourceCatalog"),
    ResourceCategory("/resourceCategory"),
    ImportJob("/importJob"),
    ExportJob("/exportJob");

    private final String hrefType;

    HrefTypes(String s) {
        this.hrefType = s;
    }

    public String getHrefType() {
        return hrefType;
    }
}
