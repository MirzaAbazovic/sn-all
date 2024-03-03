package de.bitconex.tmf.party.utility;

public enum HrefTypes {
    Individual("/individual"),
    Organization("/organization");

    private final String hrefType;

    HrefTypes(String s) {
        this.hrefType = s;
    }

    public String getHrefType() {
        return hrefType;
    }
}