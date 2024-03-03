package de.mnet.wbci.model;

public enum KundenTyp {
    PK("Privatkunde"),
    GK("Geschäftskunde");

    private String bezeichnung;

    private KundenTyp(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }
}
