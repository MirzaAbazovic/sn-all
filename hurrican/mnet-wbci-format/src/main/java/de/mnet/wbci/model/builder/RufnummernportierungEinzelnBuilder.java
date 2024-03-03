package de.mnet.wbci.model.builder;

import java.util.*;

import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.RufnummernportierungEinzeln;

public class RufnummernportierungEinzelnBuilder extends RufnummernportierungBuilder<RufnummernportierungEinzeln> {

    protected Boolean alleRufnummernPortieren;
    protected List<RufnummerOnkz> rufnummernListe;

    @Override
    public RufnummernportierungEinzeln build() {
        RufnummernportierungEinzeln rufnummernportierungEinzeln = new RufnummernportierungEinzeln();
        enrich(rufnummernportierungEinzeln);
        rufnummernportierungEinzeln.setAlleRufnummernPortieren(alleRufnummernPortieren);
        rufnummernportierungEinzeln.setRufnummernOnkz(rufnummernListe);
        return rufnummernportierungEinzeln;
    }

    public RufnummernportierungEinzelnBuilder withAlleRufnummernPortieren(Boolean alleRufnummernPortieren) {
        this.alleRufnummernPortieren = alleRufnummernPortieren;
        return this;
    }

    public RufnummernportierungEinzelnBuilder withRufnummerOnkzs(List<RufnummerOnkz> rufnummernListe) {
        this.rufnummernListe = rufnummernListe;
        return this;
    }

    public RufnummernportierungEinzelnBuilder addRufnummer(RufnummerOnkz rufnummer) {
        if (rufnummernListe == null) {
            rufnummernListe = new ArrayList<>();
        }
        this.rufnummernListe.add(rufnummer);
        return this;
    }
}
