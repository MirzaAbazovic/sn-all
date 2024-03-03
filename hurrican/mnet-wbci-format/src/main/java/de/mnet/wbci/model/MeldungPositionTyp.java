package de.mnet.wbci.model;

import org.apache.commons.lang.NotImplementedException;

/**
 *
 */
public enum MeldungPositionTyp {

    AKM_TR("AKM-TR", MeldungPositionUebernahmeRessourceMeldung.class, "Meldung Position Uebernahme Ressource"),
    ABBM("ABBM", MeldungPositionAbbruchmeldung.class, "Meldung Position Abbruchmeldung"),
    ABBM_TR("ABBM-TR", MeldungPositionAbbruchmeldungTechnRessource.class, "Meldung Position Abbruchmeldung techn. Ressource"),
    RUEM_VA("RUEM-VA", MeldungPositionRueckmeldungVa.class, "Meldung Position Rueckmeldung Vorabstimmung"),
    ERLM("ERLM", MeldungPositionErledigtmeldung.class, "Meldung Position Erledigtmeldung");

    private final String value;
    private final String longName;

    private final Class<? extends MeldungPosition> meldungPositionClass;

    private MeldungPositionTyp(String value, Class<? extends MeldungPosition> meldungPositionClass, String longName) {
        this.value = value;
        this.longName = longName;

        this.meldungPositionClass = meldungPositionClass;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }

    public String getLongName() {
        return longName;
    }

    public Class<? extends MeldungPosition> getMeldungPositionClass() {
        if (meldungPositionClass == null) {
            throw new NotImplementedException("No meldung position class available");
        }

        return meldungPositionClass;
    }

    public static MeldungPositionTyp of(String typ) {
        for (MeldungPositionTyp meldungPositionTyp : MeldungPositionTyp.values()) {
            if (meldungPositionTyp.value.equals(typ)) {
                return meldungPositionTyp;
            }
        }
        throw new IllegalArgumentException("There exists no MeldungsPositionTyp for name '" + typ + "'");
    }
}
