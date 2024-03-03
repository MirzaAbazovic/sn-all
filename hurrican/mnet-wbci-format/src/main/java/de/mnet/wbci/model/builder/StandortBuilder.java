package de.mnet.wbci.model.builder;

import org.apache.commons.lang.StringUtils;

import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.Strasse;

public class StandortBuilder implements WbciBuilder<Standort> {

    protected Strasse strasse;
    protected String postleitzahl;
    protected String ort;

    @Override
    public Standort build() {
        Standort standort = new Standort();
        standort.setOrt(StringUtils.stripToNull(ort));
        standort.setPostleitzahl(StringUtils.stripToNull(postleitzahl));
        standort.setStrasse(strasse);
        return standort;
    }

    public StandortBuilder withOrt(String ort) {
        this.ort = ort;
        return this;
    }

    public StandortBuilder withPostleitzahl(String postleitzahl) {
        this.postleitzahl = postleitzahl;
        return this;
    }

    public StandortBuilder withStrasse(Strasse strasse) {
        this.strasse = strasse;
        return this;
    }

}
