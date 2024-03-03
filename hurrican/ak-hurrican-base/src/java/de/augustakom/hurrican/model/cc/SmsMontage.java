package de.augustakom.hurrican.model.cc;

/**
 * Legt fest ob die "Montage erforderlich" Information für den SMS Versand relevant ist.
 */
public enum SmsMontage {
    YES,
    NO,

    /**
     * nicht relevant
     */
    IGNORE;
}
