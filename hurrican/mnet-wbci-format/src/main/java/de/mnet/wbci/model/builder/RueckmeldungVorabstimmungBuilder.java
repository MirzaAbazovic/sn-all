/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.08.13
 */
package de.mnet.wbci.model.builder;

import java.time.*;
import java.util.*;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungPositionRueckmeldungVa;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.Technologie;

/**
 *
 */
public class RueckmeldungVorabstimmungBuilder extends
        MeldungBuilder<RueckmeldungVorabstimmung, MeldungPositionRueckmeldungVa> {

    protected Technologie technologie;
    protected LocalDate wechseltermin;
    protected Rufnummernportierung rufnummernportierung;
    protected Set<TechnischeRessource> technischeRessourcen = new HashSet<>();

    public RueckmeldungVorabstimmung build() {
        RueckmeldungVorabstimmung meldung = new RueckmeldungVorabstimmung();
        meldung.setWechseltermin(wechseltermin != null ? wechseltermin : null);
        meldung.setRufnummernportierung(rufnummernportierung);

        boolean isRrnp = (wbciGeschaeftsfall != null && GeschaeftsfallTyp.VA_RRNP.equals(wbciGeschaeftsfall.getTyp()));
        if (!isRrnp) {
            // bei RRNP darf Technologie + TechnischeRessource nicht in RUEM-VA eingetragen werden!
            meldung.setTechnologie(technologie);
            meldung.setTechnischeRessourcen(technischeRessourcen);
        }

        super.enrich(meldung);

        return meldung;
    }

    public RueckmeldungVorabstimmungBuilder withTechnologie(Technologie technologie) {
        this.technologie = technologie;
        return this;
    }

    public RueckmeldungVorabstimmungBuilder withWechseltermin(LocalDate wechseltermin) {
        this.wechseltermin = wechseltermin;
        return this;
    }

    public RueckmeldungVorabstimmungBuilder withRufnummernportierung(Rufnummernportierung rufnummernportierung) {
        this.rufnummernportierung = rufnummernportierung;
        return this;
    }

    public RueckmeldungVorabstimmungBuilder withTechnischeRessourcen(Set<TechnischeRessource> technischeRessourcen) {
        this.technischeRessourcen = technischeRessourcen;
        return this;
    }

    public RueckmeldungVorabstimmungBuilder addTechnischeRessource(TechnischeRessource technischeRessource) {
        if (technischeRessourcen == null) {
            technischeRessourcen = new HashSet<>();
        }
        technischeRessourcen.add(technischeRessource);
        return this;
    }

    public RueckmeldungVorabstimmungBuilder addMeldungPositionen(Set<MeldungPositionRueckmeldungVa> positionen) {
        for (MeldungPositionRueckmeldungVa mpa : positionen) {
            super.addMeldungPosition(mpa);
        }
        return this;
    }

}
