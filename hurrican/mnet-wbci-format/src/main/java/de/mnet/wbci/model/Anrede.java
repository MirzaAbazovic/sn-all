package de.mnet.wbci.model;

import com.google.common.collect.ImmutableList;

public enum Anrede {
    FRAU("Frau"),
    HERR("Herr"),
    FIRMA("Firma"),
    UNBEKANNT("");

    public static final ImmutableList<Anrede> PERSON_ANREDEN = ImmutableList.of(UNBEKANNT, FRAU, HERR);

    private final String description;

    private Anrede(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}


