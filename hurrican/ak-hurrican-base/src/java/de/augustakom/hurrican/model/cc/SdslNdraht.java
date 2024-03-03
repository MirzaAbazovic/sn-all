package de.augustakom.hurrican.model.cc;

public enum SdslNdraht {
    /** Realisierung ueber 2-Draht (= 1 Auftrag) */
    ZWEI("2-Draht", 1, true),
    /** Realisierung ueber 4-Draht (= 2 Auftraege) */
    VIER("4-Draht", 2, true),
    /** Realisierung ueber 8-Draht (= 4 Auftraege) */
    ACHT("8-Draht", 4, true),
    /**
     * Realisierung ueber bonding-faehige Baugruppe; Ports muessen nicht in Reihe vergeben werden!
     */
    OPTIONAL_BONDING("optionales bonding", 2, false);

    public final String displayName;

    /**
     * insgesamte Anzahl an erwarteten Auftraegen im Buendel (SDSL-Produkt + N-Draht-Optionen - Produkt)
     */
    public final int anzahlAuftraege;

    /**
     * Flag gibt an, ob die Anzahl der angegebenen Auftraege Pflicht oder optional ist.
     */
    public final boolean mandatory;

    SdslNdraht(final String displayName, final int anzahlAuftraege, boolean mandatory) {
        this.displayName = displayName;
        this.anzahlAuftraege = anzahlAuftraege;
        this.mandatory = mandatory;
    }
}
