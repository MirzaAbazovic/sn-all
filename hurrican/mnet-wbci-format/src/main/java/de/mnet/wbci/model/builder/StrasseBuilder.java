package de.mnet.wbci.model.builder;

import org.apache.commons.lang.StringUtils;

import de.mnet.wbci.model.Strasse;

public class StrasseBuilder implements WbciBuilder<Strasse> {

    protected String strassenname;
    protected String hausnummer;
    protected String hausnummernZusatz;

    @Override
    public Strasse build() {
        Strasse strasse = new Strasse();
        strasse.setHausnummer(StringUtils.stripToNull(hausnummer));
        strasse.setHausnummernZusatz(StringUtils.stripToNull(hausnummernZusatz));
        strasse.setStrassenname(StringUtils.stripToNull(strassenname));
        return strasse;
    }

    public StrasseBuilder withHausnummer(String hausnummer) {
        this.hausnummer = hausnummer;
        return this;
    }

    public StrasseBuilder withHausnummernZusatz(String hausnummernZusatz) {
        this.hausnummernZusatz = hausnummernZusatz;
        return this;
    }

    public StrasseBuilder withStrassenname(String strassenname) {
        this.strassenname = strassenname;
        return this;
    }

}
