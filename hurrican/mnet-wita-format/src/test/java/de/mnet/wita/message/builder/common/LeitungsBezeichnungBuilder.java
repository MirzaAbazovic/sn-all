package de.mnet.wita.message.builder.common;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.builder.AbstractWitaBuilder;
import de.mnet.wita.message.common.LeitungsBezeichnung;

/**
 *
 */
public class LeitungsBezeichnungBuilder extends AbstractWitaBuilder<LeitungsBezeichnung> {

    private String leitungsSchluesselZahl;
    /**
     * @deprecated - removed in WITA v5
     */
    private String leitungsSchluesselZahlErgaenzung;
    private String onkzKunde;
    private String onkzKollokation;
    private String ordnungsNummer;

    public LeitungsBezeichnungBuilder(WitaCdmVersion witaCdmVersion) {
        super(witaCdmVersion);
    }

    public LeitungsBezeichnung buildValid() {
        if (onkzKunde == null) {
            onkzKunde = "1234";
        }
        if (onkzKollokation == null) {
            onkzKollokation = "12345";
        }
        if (leitungsSchluesselZahl == null) {
            leitungsSchluesselZahl = "1234";
        }
        if (ordnungsNummer == null) {
            ordnungsNummer = "1234567890";
        }
        return build();
    }

    public LeitungsBezeichnung build() {
        LeitungsBezeichnung leitungsBezeichnung = new LeitungsBezeichnung();
        leitungsBezeichnung.setLeitungsSchluesselZahl(leitungsSchluesselZahl);
        leitungsBezeichnung.setOnkzKollokation(onkzKollokation);
        leitungsBezeichnung.setOnkzKunde(onkzKunde);
        leitungsBezeichnung.setOrdnungsNummer(ordnungsNummer);
        return leitungsBezeichnung;
    }

    public LeitungsBezeichnungBuilder withLeitungsSchluesselZahl(String leitungsSchluesselZahl) {
        this.leitungsSchluesselZahl = leitungsSchluesselZahl;
        return this;
    }

    public LeitungsBezeichnungBuilder withOnkzKunde(String onkzKunde) {
        this.onkzKunde = onkzKunde;
        return this;
    }

    public LeitungsBezeichnungBuilder withOnkzKollokation(String onkzKollokation) {
        this.onkzKollokation = onkzKollokation;
        return this;
    }

    public LeitungsBezeichnungBuilder withOrdnungsNummer(String ordnungsNummer) {
        this.ordnungsNummer = ordnungsNummer;
        return this;
    }

    /**
     * @deprecated
     */
    public LeitungsBezeichnungBuilder withLeitungsSchluesselZahlErgaenzung(String leitungsSchluesselZahlErgaenzung) {
        this.leitungsSchluesselZahlErgaenzung = leitungsSchluesselZahlErgaenzung;
        return this;
    }

}
