/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.13
 */
package de.mnet.wbci.model;

import org.apache.commons.lang.NotImplementedException;

/**
 * Base enumeration for all notification message types known to WBCI. Value goes to database for message type
 * identification.
 *
 *
 */
public enum MeldungTyp {

    AKM_TR("AKM-TR", UebernahmeRessourceMeldung.class, "Mitteilung zur Übernahme der techn. Ressource"),
    RUEM_VA("RUEM-VA", RueckmeldungVorabstimmung.class, "Rueckmeldung Vorabstimmung"),
    ABBM("ABBM", Abbruchmeldung.class, "Abbruchmeldung"),
    ABBM_TR("ABBM-TR", AbbruchmeldungTechnRessource.class, "Abbruchmeldung techn. Ressource"),
    ERLM("ERLM", Erledigtmeldung.class, "Erledigtmeldung zur TV oder Storno"),
    DEFAULT("DEFAULT", null, "Default Meldung"); /* keine Meldungsklasse! */

    private final String shortName;
    private final String longName;

    private final Class<? extends Meldung<?>> meldungsClass;

    private MeldungTyp(String shortName, Class<? extends Meldung<?>> meldungClass, String longName) {
        this.shortName = shortName;
        this.longName = longName;

        this.meldungsClass = meldungClass;
    }

    @Override
    public String toString() {
        return shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public Class<? extends Meldung<?>> getMeldungClass() {
        if (meldungsClass == null) {
            throw new NotImplementedException("No meldung class available");
        }

        return meldungsClass;
    }

    public static MeldungTyp buildFromName(String typName) {
        for (MeldungTyp meldungsTyp : MeldungTyp.values()) {
            if (meldungsTyp.name().equals(typName)) {
                return meldungsTyp;
            }
        }
        throw new IllegalArgumentException("There exists no MeldungsTyp for name '" + typName + "'");
    }

    public static MeldungTyp buildFromShortName(String typName) {
        for (MeldungTyp meldungsTyp : MeldungTyp.values()) {
            if (meldungsTyp.getShortName().equals(typName)) {
                return meldungsTyp;
            }
        }
        throw new IllegalArgumentException("There exists no MeldungsTyp for short name '" + typName + "'");
    }

}
