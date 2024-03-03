package de.mnet.wbci.model.builder;

import java.util.*;

import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.RufnummernportierungAnlage;

public class RufnummernportierungAnlageBuilder extends RufnummernportierungBuilder<RufnummernportierungAnlage> {

    protected String onkz;
    protected String durchwahlnummer;
    protected String abfragestelle;
    protected List<Rufnummernblock> rufnummernbloecke;

    @Override
    public RufnummernportierungAnlage build() {
        RufnummernportierungAnlage rufnummernportierungAnlage = new RufnummernportierungAnlage();
        enrich(rufnummernportierungAnlage);
        rufnummernportierungAnlage.setOnkz(onkz);
        rufnummernportierungAnlage.setDurchwahlnummer(durchwahlnummer);
        rufnummernportierungAnlage.setAbfragestelle(abfragestelle);
        rufnummernportierungAnlage.setRufnummernbloecke(rufnummernbloecke);
        return rufnummernportierungAnlage;
    }

    public RufnummernportierungAnlageBuilder withOnkz(String onkz) {
        this.onkz = onkz;
        return this;
    }

    public RufnummernportierungAnlageBuilder withDurchwahlnummer(String durchwahlnummer) {
        this.durchwahlnummer = durchwahlnummer;
        return this;
    }

    public RufnummernportierungAnlageBuilder withAbfragestelle(String abfragestelle) {
        this.abfragestelle = abfragestelle;
        return this;
    }

    public RufnummernportierungAnlageBuilder withRufnummernbloecke(List<Rufnummernblock> rufnummernbloecke) {
        this.rufnummernbloecke = rufnummernbloecke;
        return this;
    }

    public RufnummernportierungAnlageBuilder addRufnummernblock(Rufnummernblock rufnummernblock) {
        if (rufnummernbloecke == null) {
            rufnummernbloecke = new ArrayList<>();
        }
        this.rufnummernbloecke.add(rufnummernblock);
        return this;
    }
}
