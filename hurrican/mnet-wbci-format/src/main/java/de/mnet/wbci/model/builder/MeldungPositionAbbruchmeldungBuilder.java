/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.model.builder;

import java.util.*;

import de.mnet.wbci.model.MeldungPositionAbbmRufnummer;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldung;

/**
 *
 */
public class MeldungPositionAbbruchmeldungBuilder extends MeldungPositionBuilder<MeldungPositionAbbruchmeldung> {

    protected Set<MeldungPositionAbbmRufnummer> rufnummern;

    @Override
    public MeldungPositionAbbruchmeldung build() {
        MeldungPositionAbbruchmeldung meldungPosition = new MeldungPositionAbbruchmeldung();

        meldungPosition.setRufnummern(rufnummern);

        super.enrich(meldungPosition);
        return meldungPosition;
    }

    public MeldungPositionAbbruchmeldungBuilder withRufnummern(Set<MeldungPositionAbbmRufnummer> rufnummern) {
        this.rufnummern = rufnummern;
        return this;
    }

    public MeldungPositionAbbruchmeldungBuilder addRufnummer(MeldungPositionAbbmRufnummer toAdd) {
        if (this.rufnummern == null) {
            this.rufnummern = new HashSet<>();
        }
        this.rufnummern.add(toAdd);
        return this;
    }

    public MeldungPositionAbbruchmeldungBuilder withRufnummer(String rufnummer) {
        return addRufnummer(new MeldungPositionAbbmRufnummerBuilder().withRufnummer(rufnummer).build());
    }

}
