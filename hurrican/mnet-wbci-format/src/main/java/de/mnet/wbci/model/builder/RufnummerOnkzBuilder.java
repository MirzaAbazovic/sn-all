package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.RufnummerOnkz;

public class RufnummerOnkzBuilder implements WbciBuilder<RufnummerOnkz> {

    protected String onkz;
    protected String rufnummer;
    protected String portierungKennungPKIabg;

    @Override
    public RufnummerOnkz build() {
        RufnummerOnkz rufnummerOnkz = new RufnummerOnkz();
        rufnummerOnkz.setOnkz(onkz);
        rufnummerOnkz.setRufnummer(rufnummer);
        rufnummerOnkz.setPortierungskennungPKIabg(portierungKennungPKIabg);
        return rufnummerOnkz;
    }

    public RufnummerOnkzBuilder withOnkz(String onkz) {
        this.onkz = onkz;
        return this;
    }

    public RufnummerOnkzBuilder withRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
        return this;
    }

    public RufnummerOnkzBuilder withPortierungKennungPKIabg(String portierungKennungPKIabg) {
        this.portierungKennungPKIabg = portierungKennungPKIabg;
        return this;
    }

}
