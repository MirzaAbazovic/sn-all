package de.mnet.wita.message.meldung.position;


public enum GeschaeftsfallArt {

    BEREITSTELLUNG("Bereitstellung"),
    KUENDIGUNG("Kuendigung"),
    AENDERUNG("Aenderung"),
    PRODUKTGRUPPENWECHSEL("Produktgruppenwechsel"),
    ANBIETERWECHSEL("Anbieterwechsel"),
    BEREITSTELLUNG_SERVICE("BereitstellungService"),
    AUSKUNFT("Auskunft");

    private final String value;

    private GeschaeftsfallArt(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static GeschaeftsfallArt build(final String value) {
        for (GeschaeftsfallArt typ : GeschaeftsfallArt.values()) {
            if (typ.getValue().equals(value)) {
                return typ;
            }
        }
        throw new IllegalArgumentException("Unknown value " + value);
    }
}
