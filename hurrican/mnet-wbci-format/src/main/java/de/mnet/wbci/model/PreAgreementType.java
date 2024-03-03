package de.mnet.wbci.model;

/**
 * This entity is an extension of KundenTyp object. New is 'WS' for display purposes on the GUI only
 */
public enum PreAgreementType {
    PK("Privatkunde"),
    GK("Geschäftskunde"),
    WS("Wholesale");

    String description;

    PreAgreementType(String bezeichnung) {
        this.description = bezeichnung;
    }

    public static PreAgreementType fromKundenTyp(KundenTyp kundenTyp) {
        return (kundenTyp != null) ? valueOf(kundenTyp.name()) : null;
    }
}
