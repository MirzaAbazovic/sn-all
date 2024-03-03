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
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 *
 */
public class RueckmeldungVorabstimmungTestBuilder extends RueckmeldungVorabstimmungBuilder implements
        WbciTestBuilder<RueckmeldungVorabstimmung> {

    private boolean withoutRnp = false;

    @Override
    public RueckmeldungVorabstimmung buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        MeldungTestBuilder.enrich(this, wbciCdmVersion, gfTyp);

        // explicitly set meldung positionen according to this builders meldung.
        if (meldungsPositionen.isEmpty()) {
            meldungsPositionen.add(new MeldungPositionRueckmeldungVaTestBuilder().buildValid(wbciCdmVersion, gfTyp));
        }

        if (technologie == null && !GeschaeftsfallTyp.VA_RRNP.equals(gfTyp)) {
            technologie = Technologie.TAL_ISDN;
        }

        if (wechseltermin == null) {
            wechseltermin = wbciGeschaeftsfall.getKundenwunschtermin();
        }

        if (rufnummernportierung == null && !GeschaeftsfallTyp.VA_KUE_ORN.equals(gfTyp) && !withoutRnp) {
            rufnummernportierung = new RufnummernportierungAnlageTestBuilder().buildValid(wbciCdmVersion, gfTyp);
        }

        if (technischeRessourcen.isEmpty() && !GeschaeftsfallTyp.VA_RRNP.equals(gfTyp)) {
            technischeRessourcen.add(new TechnischeRessourceTestBuilder().buildValid(wbciCdmVersion, gfTyp));
        }

        return build();
    }

    @Override
    public RueckmeldungVorabstimmungTestBuilder addTechnischeRessource(TechnischeRessource technischeRessource) {
        super.addTechnischeRessource(technischeRessource);
        return this;
    }

    @Override
    public RueckmeldungVorabstimmungTestBuilder addMeldungPosition(MeldungPositionRueckmeldungVa position) {
        return (RueckmeldungVorabstimmungTestBuilder) super.addMeldungPosition(position);
    }

    @Override
    public RueckmeldungVorabstimmungTestBuilder withWbciGeschaeftsfall(WbciGeschaeftsfall wbciGeschaeftsfall) {
        super.withWbciGeschaeftsfall(wbciGeschaeftsfall);
        return this;
    }

    @Override
    public RueckmeldungVorabstimmungTestBuilder withWechseltermin(LocalDate wechseltermin) {
        super.withWechseltermin(wechseltermin);
        return this;
    }

    @Override
    public RueckmeldungVorabstimmungTestBuilder withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        super.withVorabstimmungsIdRef(vorabstimmungsIdRef);
        return this;
    }

    @Override
    public RueckmeldungVorabstimmungTestBuilder withRufnummernportierung(Rufnummernportierung rufnummernportierung) {
        return (RueckmeldungVorabstimmungTestBuilder) super.withRufnummernportierung(rufnummernportierung);
    }

    public RueckmeldungVorabstimmungTestBuilder withoutRufnummernportierung() {
        this.withoutRnp = true;
        return this;
    }

    @Override
    public RueckmeldungVorabstimmungTestBuilder addMeldungPositionen(Set<MeldungPositionRueckmeldungVa> positionen) {
        return (RueckmeldungVorabstimmungTestBuilder) super.addMeldungPositionen(positionen);
    }

    @Override
    public RueckmeldungVorabstimmungTestBuilder withTechnologie(Technologie technologie) {
        return (RueckmeldungVorabstimmungTestBuilder) super.withTechnologie(technologie);
    }

    @Override
    public RueckmeldungVorabstimmungTestBuilder withTechnischeRessourcen(Set<TechnischeRessource> technischeRessourcen) {
        return (RueckmeldungVorabstimmungTestBuilder) super.withTechnischeRessourcen(technischeRessourcen);
    }

    @Override
    public RueckmeldungVorabstimmungTestBuilder withMeldungsPositionen(Set<MeldungPositionRueckmeldungVa> positionen) {
        return (RueckmeldungVorabstimmungTestBuilder) super.withMeldungsPositionen(positionen);
    }

    @Override
    public RueckmeldungVorabstimmungTestBuilder withProcessedAt(LocalDateTime processedAt) {
        return (RueckmeldungVorabstimmungTestBuilder) super.withProcessedAt(processedAt);
    }
}
