/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.05.2011 11:51:53
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MwfEntity;

/**
 * Basis-Daten fuer einen WITA Standort.
 */
@MappedSuperclass
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
public class AbstractStandort extends MwfEntity {

    private static final long serialVersionUID = 7616113693948502966L;

    private String strassenname;
    private String hausnummer;
    private String hausnummernZusatz;
    private String postleitzahl;
    private String ortsname;
    private String ortsteil;
    private String land;

    @Override
    public String toString() {
        return "Standort [strassenname=" + strassenname + ", hausnummer=" + hausnummer
                + ", hausnummernZusatz=" + hausnummernZusatz + ", postleitzahl=" + postleitzahl
                + ", ortsname=" + ortsname + ", ortsteil=" + ortsteil + "]";
    }

    @NotNull
    @Size(min = 1, max = 40)
    public String getStrassenname() {
        return strassenname;
    }

    public void setStrassenname(String strassenname) {
        this.strassenname = strassenname;
    }

    @NotNull
    @Size(min = 1, max = 4)
    public String getHausnummer() {
        return hausnummer;
    }

    public void setHausnummer(String hausnummer) {
        this.hausnummer = hausnummer;
    }

    @Transient
    public String getHausnummerTrimmed() {
        return (hausnummer != null) ? hausnummer.trim() : null;
    }

    @Size(max = 6)
    public String getHausnummernZusatz() {
        return hausnummernZusatz;
    }

    public void setHausnummernZusatz(String hausnummernZusatz) {
        this.hausnummernZusatz = hausnummernZusatz;
    }

    @NotNull
    @Size(min = 1, max = 10)
    public String getPostleitzahl() {
        return postleitzahl;
    }

    public void setPostleitzahl(String postleitzahl) {
        this.postleitzahl = postleitzahl;
    }

    @NotNull
    @Size(min = 1, max = 40)
    public String getOrtsname() {
        return ortsname;
    }

    public void setOrtsname(String ortsname) {
        this.ortsname = ortsname;
    }

    @Size(max = 2)
    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    @Size(min = 1, max = 40)
    public String getOrtsteil() {
        return ortsteil;
    }

    public void setOrtsteil(String ortsteil) {
        this.ortsteil = ortsteil;
    }

}
