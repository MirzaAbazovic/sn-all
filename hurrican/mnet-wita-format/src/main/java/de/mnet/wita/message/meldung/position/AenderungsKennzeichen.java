package de.mnet.wita.message.meldung.position;

public enum AenderungsKennzeichen {

    STANDARD("Standard"),
    STORNO("Storno"),
    TERMINVERSCHIEBUNG("Terminverschiebung");

    private final String value;

    private AenderungsKennzeichen(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AenderungsKennzeichen build(final String value) {
        for (AenderungsKennzeichen typ : AenderungsKennzeichen.values()) {
            if (typ.getValue().equals(value)) {
                return typ;
            }
        }
        return STANDARD;
    }

    @Override
    public String toString() {
        return value;
    }

}
